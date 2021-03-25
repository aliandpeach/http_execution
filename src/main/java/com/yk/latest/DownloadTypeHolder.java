package com.yk.latest;

import com.yk.config.CommonConfig;
import com.yk.mysql.DruidConnection;
import com.yk.crypto.RSA2048Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    
    private RSA2048Util rsa;
    
    private CommonConfig config;
    
    private DownloadTypeHolder()
    {
        this.config = CommonConfig.getInstance();
        this.rsa = RSA2048Util.getInstance(config.getStorepasswd(), config.getKeypasswd());
    }
    
    public Map<String, DownloadType> getTypeParameters()
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
                    originalUrl = rsa.decrypt(originalUrl);
                    downloadType.setOriginalUrl(originalUrl);
                }
                String latestUrl = rs.getString("download_latest_url");
                if (null != latestUrl)
                {
                    latestUrl = rsa.decrypt(latestUrl);
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
        return typeParameters;
    }
    
    private static class DownloadTypeInstance
    {
        public static DownloadTypeHolder INSTANCE = new DownloadTypeHolder();
    }
}
