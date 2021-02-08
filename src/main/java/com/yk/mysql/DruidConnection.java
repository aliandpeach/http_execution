package com.yk.mysql;

import com.alibaba.druid.pool.DruidConnectionHolder;
import com.alibaba.druid.pool.DruidDataSource;
import com.yk.config.CommonConfig;
import com.yk.rsa.RSA2048Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DruidConnection
{
    private static Logger connLogger = LoggerFactory.getLogger("druid_conn");
    private static Properties properties;

    private static DruidDataSource druidDataSource;

    static
    {
        properties = new Properties();
        try
        {
            Properties druid = CommonConfig.getInstance().getDruidProperties();
            if (null != druid)
            {
                properties = druid;
            }
            else
            {
                properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("druid.properties"));
            }

        }
        catch (IOException e)
        {
            connLogger.error("load Properties IOException", e);
        }
    }

    /**
     * 创建单列模式
     *
     * @return JDBCDruid实例
     */
    public static DruidConnection getInstance()
    {
        return DruidConnectionHolder.INSTANCE;
    }

    public DruidDataSource getDuridDatasource()
    {
        return druidDataSource;
    }

    private DruidConnection()
    {
        druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(properties.getProperty("jdbc.driverClassName"));
        druidDataSource.setUrl(properties.getProperty("jdbc.url"));
        druidDataSource.setUsername(properties.getProperty("jdbc.username"));
        String pwd = properties.getProperty("jdbc.password");
        try
        {
            druidDataSource.setPassword(RSA2048Util.decrypt(pwd));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        druidDataSource.setMaxActive(Integer.parseInt(properties.getProperty("jdbc.maxActive")));
        druidDataSource.setInitialSize(1);
        druidDataSource.setMinIdle(5);
        druidDataSource.setMaxWait(60000);
        druidDataSource.setValidationQuery("SELECT 1");
    }

    private static class DruidConnectionHolder
    {
        public static DruidConnection INSTANCE = new DruidConnection();
    }

    public static void main(String args[])
    {
        DataSource dataSource = DruidConnection.getInstance().getDuridDatasource();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement("select * from download_scan");
            rs = ps.executeQuery();
            while (rs.next())
            {
                long pkid = rs.getLong("pkid");
                String url = rs.getString("url");
            }
        }
        catch (SQLException e)
        {
            connLogger.error("SQLException", e);
        }
        finally
        {
            close(conn, ps, rs);
        }
    }

    public static void close(Connection conn, PreparedStatement ps, ResultSet rs)
    {
        try
        {
            if (null != rs)
            {
                rs.close();
            }
        }
        catch (SQLException e)
        {
            connLogger.error("close rs error", e);
        }
        try
        {
            if (null != ps)
            {
                ps.close();
            }
        }
        catch (SQLException e)
        {
            connLogger.error("close ps error", e);
        }
        try
        {
            if (null != conn)
            {
                conn.close();
            }
        }
        catch (SQLException e)
        {
            connLogger.error("close conn error", e);
        }
    }
}
