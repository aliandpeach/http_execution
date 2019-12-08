package com.yk.mysql;

import com.mysql.cj.jdbc.Driver;

import java.sql.SQLException;

public class JDBCConnection
{
    public static void main(String args[])
    {
        try
        {
            new Driver();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}