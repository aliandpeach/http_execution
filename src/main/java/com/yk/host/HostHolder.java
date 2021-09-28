package com.yk.host;

import com.yk.config.CommonConfig;
import com.yk.mysql.DruidConnection;
import com.yk.crypto.RSA2048Util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HostHolder
{
    private Map<Integer, String> hostParameters = new ConcurrentHashMap<>();
    
    public static HostHolder getInstance()
    {
        return HostHolderInstance.INSTANCE;
    }
    
    private RSA2048Util rsa;
    
    private CommonConfig config;
    
    private HostHolder()
    {
        this.config = CommonConfig.getInstance();
        this.rsa = RSA2048Util.getInstance(config.getStorepasswd(), config.getKeypasswd(), null, null);
    }
    
    public Map<Integer, String> getHostParameters()
    {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            DataSource dataSource = DruidConnection.getInstance().getDuridDatasource();
            conn = dataSource.getConnection();
            ps = conn.prepareStatement("select * from download_host");
            rs = ps.executeQuery();
            int index = 0;
            while (rs.next())
            {
                String name = rs.getString("host_name");
                if (null == name)
                    continue;
                name = rsa.decrypt(name);
                if (null == name)
                    continue;
                hostParameters.put(index++, name);
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
        return hostParameters;
    }
    
    private static class HostHolderInstance
    {
        public static HostHolder INSTANCE = new HostHolder();
    }
}
