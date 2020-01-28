package com.yk.task.datacenter;

import com.yk.mysql.DruidConnection;
import com.yk.task.categories.CategoriesTask;
import com.yk.task.categories.DownloadCategories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class CategoriesCenter {
    private static Logger logger = LoggerFactory.getLogger("categoriesCenter");

    public static LinkedBlockingQueue<DownloadCategories> queue = new LinkedBlockingQueue<>();

    public static Object lock = new Object();

    public static void consumer(Map<Integer, String> hosts) {
        DownloadCategories downloadCategories = null;
        synchronized (CategoriesCenter.lock) {
            logger.info("consumer : " + Thread.currentThread().getName() + " queue size " + queue.size());
            while (CategoriesCenter.queue.size() <= 0) {
                try {
                    logger.info("consumer : " + Thread.currentThread().getName() + " wait");
                    CategoriesCenter.lock.wait();
                    logger.info("consumer : " + Thread.currentThread().getName() + " wakeup");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        CategoriesTask categoriesTask = new CategoriesTask("");
        DataSource dataSource = DruidConnection.getInstance().getDuridDatasource();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        downloadCategories = CategoriesCenter.queue.poll();
        logger.info("consumer : " + Thread.currentThread().getName() + " poll url = " + downloadCategories.getUrl());
        String url = downloadCategories.getUrl();
        long pkid = downloadCategories.getPkid();

        if (url != null && pkid > 0) {
            boolean success = categoriesTask.executeDownload(hosts, url, 0, downloadCategories.getCtype());
            if (success) {
                try {
                    conn = dataSource.getConnection();
                    ps = conn.prepareStatement("update download_categories set is_success = ? where is_success = ? and pkid = ?");
                    ps.setInt(1, 1);
                    ps.setInt(2, 0);
                    ps.setLong(3, pkid);
                    ps.execute();
                } catch (SQLException e) {
                    logger.error("consumer SQLException", e);
                } finally {
                    DruidConnection.close(conn, ps, rs);
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info("consumer poll : " + Thread.currentThread().getName() + " queue size " + queue.size());
        synchronized (CategoriesCenter.lock) {
            if (CategoriesCenter.queue.size() <= 100) {
                logger.info("consumer : " + Thread.currentThread().getName() + " notifyAll");
                CategoriesCenter.lock.notifyAll();
            }
        }
    }

    public static void producer() {
        synchronized (CategoriesCenter.lock) {
            logger.info("producer : " + Thread.currentThread().getName() + " queue size " + queue.size());
            while (CategoriesCenter.queue.size() >= 200) {
                try {
                    logger.info("producer : " + Thread.currentThread().getName() + " wait");
                    CategoriesCenter.lock.wait();
                    logger.info("producer : " + Thread.currentThread().getName() + " wakeup");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        DataSource dataSource = DruidConnection.getInstance().getDuridDatasource();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement("select * from download_categories where is_success = 0 AND c_type LIKE '%e39f8fd71a0c8266eddb8a92ef15de39%' limit 100");
            rs = ps.executeQuery();
            while (rs.next()) {
                long pkid = rs.getLong("pkid");
                String url = rs.getString("url");
                String ctype = rs.getString("c_type");
                DownloadCategories categories = new DownloadCategories();
                categories.setPkid(pkid);
                categories.setUrl(url);
                categories.setCtype(ctype);
                CategoriesCenter.queue.offer(categories);
            }
        } catch (SQLException e) {
            logger.error("producer SQLException", e);
        } finally {
            DruidConnection.close(conn, ps, rs);
        }
        logger.info("producer offer : " + Thread.currentThread().getName() + " queue size " + queue.size());
        synchronized (CategoriesCenter.lock) {
            logger.info("producer : " + Thread.currentThread().getName() + " notifyAll");
            CategoriesCenter.lock.notifyAll();
        }
    }
}
