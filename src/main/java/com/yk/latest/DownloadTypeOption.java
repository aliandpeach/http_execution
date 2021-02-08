package com.yk.latest;

import com.yk.mysql.DruidConnection;
import com.yk.rsa.RSA2048Util;

import javax.crypto.NoSuchPaddingException;
import javax.sql.DataSource;
import java.security.InvalidKeyException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DownloadTypeOption {
    public static void updateLatestUrlByType(String type, String latestUrl) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            DataSource dataSource = DruidConnection.getInstance().getDuridDatasource();
            conn = dataSource.getConnection();
            ps = conn.prepareStatement("update download_type set download_latest_url = ? where download_type = ?");
            ps.setString(1, RSA2048Util.encrypt(latestUrl));
            ps.setString(2, type);
            ps.execute();
        } catch (Exception e) {
            throw new RuntimeException();
        }
        finally {
            DruidConnection.close(conn, ps, rs);
        }
    }

    public static void updateLatestPageByType(int page, String latestUrl) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            DataSource dataSource = DruidConnection.getInstance().getDuridDatasource();
            conn = dataSource.getConnection();
            ps = conn.prepareStatement("UPDATE download_c_type SET categories_page = ? where categories_url = ?");
            ps.setInt(1, page);
            ps.setString(2, latestUrl);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DruidConnection.close(conn, ps, rs);
        }
    }
}
