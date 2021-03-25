package com.yk.task.categories;

import com.yk.config.CommonConfig;
import com.yk.host.HostHolder;
import com.yk.httprequest.HttpClientUtil;
import com.yk.mysql.DruidConnection;
import com.yk.crypto.RSA2048Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class CategoriesSearch
{
    
    private RSA2048Util rsa;
    private CommonConfig config;
    
    public CategoriesSearch()
    {
        this.config = CommonConfig.getInstance();
        this.rsa = RSA2048Util.getInstance(config.getStorepasswd(), config.getKeypasswd());
    }
    
    private CountDownLatch tasksStart = new CountDownLatch(1);
    
    public CountDownLatch getTasksStart()
    {
        return tasksStart;
    }
    
    public List<CategoriesType> getListCategoriesType()
    {
        try
        {
            tasksStart.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
//        listCategoriesType = queryAll().values().stream().collect(Collectors.toList());
//        listCategoriesType = queryAll().entrySet().stream().flatMap(t->new ArrayList<CategoriesType>().stream()).collect(Collectors.toList());
        List<CategoriesType> categoriesTypeList = queryAll().entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
        List<CategoriesType> returnlist = categoriesTypeList.stream().map(t -> (CategoriesType) t.clone()).collect(Collectors.toList());
        return returnlist;
    }
    
    private Logger logger = LoggerFactory.getLogger("categories");
    
    private static final String ARG = "categories";
    
    public void search()
    {
        Map<Integer, String> hosts = HostHolder.getInstance().getHostParameters();
        
        String results = null;
        for (Map.Entry<Integer, String> h : hosts.entrySet())
        {
            results = getCategories(h.getValue());
            if (null == results)
            {
                continue;
            }
            else
            {
                break;
            }
        }
        notifyAllTask();
    }
    
    public void notifyAllTask()
    {
        tasksStart.countDown();
    }
    
    public String getCategories(String host)
    {
        String results = "";
        try
        {
            results = HttpClientUtil.getString(host + ARG + "/", null, null);
            if (null == results)
            {
                logger.info("CategoriesSearch results is null");
                return null;
            }
        }
        catch (RuntimeException e)
        {
            logger.info("RuntimeException CategoriesSearch results is null url = ");
            return null;
        }
        catch (Exception e)
        {
            logger.info("Exception executeScanWeb results is null url = ");
            return null;
        }
        
        Document doc = null;
        try
        {
            doc = Jsoup.parse(results);
        }
        catch (Exception e)
        {
            logger.error("Jsoup.parse(results) error");
        }
        if (null == doc)
        {
            logger.info("something is wrong!!1");
            return null;
        }
        
        Elements container = doc.select("div.main-container");
        if (null == container)
        {
            logger.info("something is wrong!!2");
            return null;
        }
        Elements categories = container.select("div.list-categories");
        if (null == categories)
        {
            logger.info("something is wrong!!3");
            return null;
        }
        Elements e2 = categories.select("a.item");
        if (null == e2 || e2.listIterator() == null)
        {
            logger.info("something is wrong!!4");
            return null;
        }
        Map<String, String> map = new HashMap<>();
        for (Element ele : e2)
        {
            Element _hrefA = ele.selectFirst("a");
            String _href = _hrefA.attr("href");
            String _title = _hrefA.attr("title");
            if (null == _href || null == _title)
            {
                logger.info("_hrefA is null ele " + ele);
                continue;
            }
            map.put(_href.replace(host, ""), _title);
        }
        update(map);
        return "success";
    }
    
    public void update(Map<String, String> list)
    {
        if (list == null || list.size() == 0)
            return;
        DataSource dataSource = null;
        Connection conn = null;
        PreparedStatement ps = null;
        StringBuffer sql = new StringBuffer("INSERT INTO download_c_type (`categories_uuid`,`categories_name`, `categories_url`) values");
        Map<String, CategoriesType> all = queryAll();
        try
        {
            int count = 0;
            for (Map.Entry<String, String> each : list.entrySet())
            {
                if (all.containsKey(each.getKey()))
                {
                    continue;
                }
                sql.append(" (?, ?, ?),");
                count++;
            }
            if (count == 0)
            {
                return;
            }
            dataSource = DruidConnection.getInstance().getDuridDatasource();
            conn = dataSource.getConnection();
            String temp = sql.toString().endsWith(",") ? sql.toString().substring(0, sql.toString().length() - 1) : sql.toString();
            sql = new StringBuffer(temp);
            ps = conn.prepareStatement(sql.toString());
            int index = 1;
            for (Map.Entry<String, String> each : list.entrySet())
            {
                ps.setString(index++, each.getKey());
                ps.setString(index++, rsa.encrypt(each.getValue()));
                ps.setString(index++, each.getKey());
            }
            ps.execute();
        }
        catch (Exception e)
        {
            logger.error("insert sql error " + sql, e);
        }
        finally
        {
            DruidConnection.close(conn, ps, null);
        }
    }
    
    public Map<String, CategoriesType> queryAll()
    {
        DataSource dataSource = null;
        Connection conn = null;
        PreparedStatement ps = null;
        StringBuffer sql = new StringBuffer("SELECT * FROM download_c_type");
        ResultSet rs = null;
        Map<String, CategoriesType> results = new HashMap<>();
        try
        {
            dataSource = DruidConnection.getInstance().getDuridDatasource();
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            CategoriesType categories = null;
            while (rs.next())
            {
                int pkid = rs.getInt("categories_pkid");
                String uuid = rs.getString("categories_uuid");
                String name = rs.getString("categories_name");
                String url = rs.getString("categories_url");
                int page = rs.getInt("categories_page");
                if (null == uuid || null == name || null == url)
                {
                    continue;
                }
                categories = new CategoriesType();
                categories.setUuid(uuid);
                categories.setUrl(url);
                categories.setName(name);
                categories.setPkid(pkid);
                categories.setPage(page);
                results.put(url, categories);
            }
        }
        catch (Exception e)
        {
            logger.error("insert sql error " + sql, e);
        }
        finally
        {
            DruidConnection.close(conn, ps, rs);
        }
        return results;
    }
    
    public static void main(String[] args)
    {
        System.out.println("@/fdf/@ffdffd//@afdsfdfdsf\\@fdf".replaceAll("[\\\\/]", "-"));
        new CategoriesSearch().search();
    }
}
