package com.yk.host;

import com.yk.mysql.DruidConnection;
import com.yk.rsa.RSA2048Util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HostHolder
{
    private Map<Integer, String> hostParameters = new ConcurrentHashMap<>();

    public static HostHolder getInstance()
    {
        return HostHolderInstance.INSTANCE;
    }

    private HostHolder()
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
                name = RSA2048Util.decrypt(name);
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
    }

    public Map<Integer, String> getHostParameters()
    {
        return hostParameters;
    }

    private static class HostHolderInstance
    {
        public static HostHolder INSTANCE = new HostHolder();
    }
}
