package com.yk.task.datacenter;

import com.yk.mysql.DruidConnection;
import com.yk.task.DownloadScan;
import com.yk.task.MutilRequestWeb;
import com.yk.task.ScanTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DataCenter
{
    private static Logger logger = LoggerFactory.getLogger("data");

    public static LinkedBlockingQueue<DownloadScan> queue = new LinkedBlockingQueue<>();

    public static Object lock = new Object();

    public static void consumer(Map<Integer, String> hosts)
    {
        DownloadScan downloadScan = null;
        synchronized (DataCenter.lock)
        {
            logger.info("consumer : " + Thread.currentThread().getName() + " queue size " + queue.size());
            while (DataCenter.queue.size() <= 0)
            {
                try
                {
                    logger.info("consumer : " + Thread.currentThread().getName() + " wait");
                    DataCenter.lock.wait();
                    logger.info("consumer : " + Thread.currentThread().getName() + " wakeup");
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }

        ScanTask scanTask = new ScanTask("");
        DataSource dataSource = DruidConnection.getInstance().getDuridDatasource();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        downloadScan = DataCenter.queue.poll();
        logger.info("consumer : " + Thread.currentThread().getName() + " poll url = " + downloadScan.getUrl());
        String url = downloadScan.getUrl();
        long pkid = downloadScan.getPkid();

        if (url != null && pkid > 0)
        {
            boolean success = scanTask.executeDownload(hosts, url, 0);
            if (success)
            {
                try
                {
                    conn = dataSource.getConnection();
                    ps = conn.prepareStatement("update download_scan set is_success = ? where is_success = ? and pkid = ?");
                    ps.setInt(1, 1);
                    ps.setInt(2, 0);
                    ps.setLong(3, pkid);
                    ps.execute();
                }
                catch (SQLException e)
                {
                    logger.error("consumer SQLException", e);
                }
                finally
                {
                    DruidConnection.close(conn, ps, rs);
                }
            }
            try
            {
                TimeUnit.MILLISECONDS.sleep(30000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        logger.info("consumer poll : " + Thread.currentThread().getName() + " queue size " + queue.size());
        synchronized (DataCenter.lock)
        {
            if (DataCenter.queue.size() <= 100)
            {
                logger.info("consumer : " + Thread.currentThread().getName() + " notifyAll");
                DataCenter.lock.notifyAll();
            }
        }
    }

    public static void producer()
    {
        synchronized (DataCenter.lock)
        {
            logger.info("producer : " + Thread.currentThread().getName() + " queue size " + queue.size());
            while (DataCenter.queue.size() >= 200)
            {
                try
                {
                    logger.info("producer : " + Thread.currentThread().getName() + " wait");
                    DataCenter.lock.wait();
                    logger.info("producer : " + Thread.currentThread().getName() + " wakeup");
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }

        DataSource dataSource = DruidConnection.getInstance().getDuridDatasource();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try
        {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement("select * from download_scan where is_success = 0 limit 100");
            rs = ps.executeQuery();
            while (rs.next())
            {
                long pkid = rs.getLong("pkid");
                String url = rs.getString("url");
                DownloadScan scan = new DownloadScan();
                scan.setPkid(pkid);
                scan.setUrl(url);
                DataCenter.queue.offer(scan);
            }
        }
        catch (SQLException e)
        {
            logger.error("producer SQLException", e);
        }
        finally
        {
            DruidConnection.close(conn, ps, rs);
        }
        logger.info("producer offer : " + Thread.currentThread().getName() + " queue size " + queue.size());
        synchronized (DataCenter.lock)
        {
            logger.info("producer : " + Thread.currentThread().getName() + " notifyAll");
            DataCenter.lock.notifyAll();
        }
    }
}
