package com.yk.task.check;

import com.yk.config.CommonConfig;
import com.yk.mysql.DruidConnection;
import com.yk.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.yk.util.Constants.DOWNLOAD_FILENAME;

public class ProblemChecker implements Runnable
{

    private static Logger logger = LoggerFactory.getLogger("MutilRequest");

    @Override
    public void run()
    {
        DataSource dataSource = DruidConnection.getInstance().getDuridDatasource();
        while (true)
        {
            long _start = System.currentTimeMillis();
            File files = new File(CommonConfig.getInstance().getFileSaveDir());
            File list[] = files.listFiles(file -> file.exists() && file.length() < 1024 * 1024);
            Arrays.asList(list).stream().forEach((f) -> {
                if (f.getName().indexOf(DOWNLOAD_FILENAME) == -1 || f.getName().lastIndexOf(".") == -1 || f.getName().indexOf(DOWNLOAD_FILENAME) >= f.getName().lastIndexOf("."))
                    return;
                String name = f.getName().substring(f.getName().indexOf(Constants.DOWNLOAD_FILENAME) + Constants.DOWNLOAD_FILENAME.length(), f.getName().lastIndexOf("."));
                String sqlSearch = "select * from download_scan where url like ? and is_success = 1";
                Connection _conn = null;
                PreparedStatement _ps = null;
                ResultSet _rs = null;
                long _pkid = 0;
                try
                {
                    _conn = dataSource.getConnection();
                    _ps = _conn.prepareStatement(sqlSearch);
                    _ps.setString(1, "%" + name + ".mp4" + "%");
                    _rs = _ps.executeQuery();
                    while (_rs.next())
                        _pkid = _rs.getLong("pkid");
                    DruidConnection.close(_conn, _ps, _rs);
                }
                catch (SQLException e)
                {
                    logger.error("sql error3 ", e);
                    DruidConnection.close(_conn, _ps, _rs);
                }
                if (_pkid <= 0)
                {
                    return;
                }
                try
                {
                    _conn = dataSource.getConnection();
                    _ps = _conn.prepareStatement("update download_scan set is_success = ? where pkid = ?");
                    _ps.setInt(1, 0);
                    _ps.setLong(2, _pkid);
                    _ps.execute();
                }
                catch (SQLException e)
                {
                    logger.error("sql error4 ", e);
                }
                finally
                {
                    DruidConnection.close(_conn, _ps, _rs);
                }
            });

            long _end = System.currentTimeMillis();
            while (_end - _start < 30 * 60 * 1000)
            {
                try
                {
                    TimeUnit.MILLISECONDS.sleep(10000);
                    _end = System.currentTimeMillis();
                }
                catch (InterruptedException e)
                {
                    logger.error("interrupt error2 ", e);
                }
            }
        }
    }
}
