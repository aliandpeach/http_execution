package com.yk.latest;

import com.yk.host.HostHolder;
import com.yk.mysql.DruidConnection;
import com.yk.rsa.RSA2048Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DownloadTypeHolder
{
    private Map<String, DownloadType> typeParameters = new ConcurrentHashMap<>();

    public static DownloadTypeHolder getInstance()
    {
        return DownloadTypeInstance.INSTANCE;
    }

    private Logger logger = LoggerFactory.getLogger("request");

    private DownloadTypeHolder()
    {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            DataSource dataSource = DruidConnection.getInstance().getDuridDatasource();
            conn = dataSource.getConnection();
            ps = conn.prepareStatement("select * from download_type");
            rs = ps.executeQuery();
            int index = 0;
            while (rs.next())
            {
                DownloadType downloadType = new DownloadType();
                String type = rs.getString("download_type");
                if (null == type)
                    continue;
                downloadType.setType(type);
                String originalUrl = rs.getString("download_original_url");
                if (null != originalUrl)
                {
                    originalUrl = RSA2048Util.decrypt(originalUrl);
                    downloadType.setOriginalUrl(originalUrl);
                }
                String latestUrl = rs.getString("download_latest_url");
                if (null != latestUrl)
                {
                    latestUrl = RSA2048Util.decrypt(latestUrl);
                    downloadType.setLatestUrl(latestUrl);
                }
                logger.info("downloadType : " + type + " : " + downloadType);
                typeParameters.put(type, downloadType);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            DruidConnection.close(conn, ps, rs);
        }
    }

    public Map<String, DownloadType> getTypeParameters()
    {
        return typeParameters;
    }

    private static class DownloadTypeInstance
    {
        public static DownloadTypeHolder INSTANCE = new DownloadTypeHolder();
    }
}
