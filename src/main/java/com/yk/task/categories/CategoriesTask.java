package com.yk.task.categories;

import com.yk.config.CommonConfig;
import com.yk.host.HostHolder;
import com.yk.httprequest.HttpClientUtil;
import com.yk.latest.DownloadTypeOption;
import com.yk.mysql.DruidConnection;
import com.yk.task.AbstractTask;
import com.yk.task.MutilRequestWeb;
import com.yk.util.Constants;
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
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CategoriesTask extends AbstractTask
{
    private Logger logger = LoggerFactory.getLogger("categories");
    
    private String ctype;
    
    public CategoriesTask(String ctype)
    {
        this.ctype = ctype;
    }
    
    private void _sleep(int i)
    {
        try
        {
            TimeUnit.MILLISECONDS.sleep(i);
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }
    
    public String executeCategoriesWeb(Map<Integer, String> hosts, String host, String url, boolean repeat, CountDownLatch start)
    {
        try
        {
            logger.info("executeCategoriesWeb await : " + Thread.currentThread().getName());
            start.await();
            logger.info("executeCategoriesWeb un await : " + Thread.currentThread().getName());
            
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        logger.info("executeCategoriesWeb url = " + url);
        String results = null;
        try
        {
            results = MutilRequestWeb.httpClientUtil.getString(host + url, null, null);
            if (null == results)
            {
                logger.error("executeCategoriesWeb results is null url = " + url);
                return null;
            }
        }
        catch (RuntimeException e)
        {
            logger.error("RuntimeException executeCategoriesWeb results is null url = " + url);
            return null;
        }
        catch (Exception e)
        {
            logger.error("Exception executeCategoriesWeb results is null url = " + url);
            return null;
        }
        _sleep(12000);
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
        
        Elements e = doc.select("div.list-videos");
        if (null == e)
        {
            logger.info("something is wrong!!2");
            return null;
        }
        Element exx = e.first();
        if (null == exx)
        {
            logger.info("something is wrong!!3");
            return null;
        }
        Elements e2 = exx.select("div.item");
        if (null == e2 || e2.listIterator() == null)
        {
            logger.info("something is wrong!!4");
            return null;
        }
        
        if (!repeat)
        {
            for (Element ele : e2)
            {
                if (null == ele)
                {
                    continue;
                }
                Element h = ele.selectFirst("a");
                String enterhref = h.attr("href");
                String name = h.attr("title");
                String _enter_href = enterhref.replace(host, "");
                for (Map.Entry<Integer, String> hostEntry : hosts.entrySet())
                {
                    String r = subOpt(hostEntry.getValue(), _enter_href);
                    if (null == r)
                    {
                        continue;
                    }
                    r = r.replace(hostEntry.getValue(), "");
                    String sql = "INSERT INTO download_categories (`c_type`, `url`, `file_name`, `enter_url`, `is_success`) values (?, ?, ?, ?, ?)";
                    String search = "SELECT * FROM download_categories WHERE url = ?";
                    boolean isExist = executeSearch(search, r);
                    if (isExist)
                    {
                        logger.info("url has already exist url = " + url);
                    }
                    else
                    {
                        execute(sql, r, name, _enter_href);
                    }
                    break;
                }
            }
        }
        String nextUrl = nextUrl(doc);
        logger.info("next url : " + nextUrl);
        return nextUrl;
    }
    
    private void execute(String sql, String r, String name, String enter_href)
    {
        DataSource dataSource = null;
        Connection conn = null;
        PreparedStatement ps = null;
        try
        {
            dataSource = DruidConnection.getInstance().getDuridDatasource();
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, ctype);
            ps.setString(2, r);
            ps.setString(3, name);
            ps.setString(4, enter_href);
            ps.setInt(5, 0);
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
    
    private boolean executeSearch(String sql, String r)
    {
        DataSource dataSource = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean result = false;
        try
        {
            dataSource = DruidConnection.getInstance().getDuridDatasource();
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, r);
            rs = ps.executeQuery();
            String _url = "";
            while (rs.next())
            {
                _url = rs.getString("url");
            }
            if (null != _url && _url.length() > 10)
            {
                result = true;
            }
            return result;
        }
        catch (Exception e)
        {
            logger.error("search sql error " + sql, e);
        }
        finally
        {
            DruidConnection.close(conn, ps, rs);
        }
        return result;
    }
    
    private String subOpt(String host, String enter_url)
    {
        logger.info("subOpt enter_url = " + enter_url);
        String results = null;
        try
        {
            results = MutilRequestWeb.httpClientUtil.getString(host + enter_url, null, null);
            if (null == results)
            {
                logger.error("subOpt results is null enter_url = " + enter_url);
                return null;
            }
        }
        catch (RuntimeException e)
        {
            logger.error("subOpt RuntimeException results is null enter_url = " + enter_url, e);
            return null;
        }
        
        Document document = null;
        try
        {
            document = Jsoup.parse(results);
        }
        catch (Exception e)
        {
            logger.error("subOpt Jsoup.parse(results); error");
        }
        if (document == null)
        {
            return null;
        }
        
        Elements tabs1 = document.select("div#tab_video_info");
        if (null == tabs1)
        {
            logger.error("subOpt tabs1 is null document = " + document);
            return null;
        }
        Element tab = tabs1.first();
        if (null == tab)
        {
            logger.error("subOpt tab is null tabs1 = " + tabs1);
            return null;
        }
        Elements infos = tab.select("div.info");
        if (null == infos)
        {
            logger.error("subOpt infos is null tab = " + tab);
            return null;
        }
        Element info = infos.first();
        if (null == info)
        {
            logger.error("subOpt info is null infos = " + infos);
            return null;
        }
        if (info.children() == null || info.children().size() == 0)
        {
            logger.error("subOpt info.children() is null info = " + info);
            return null;
        }
        Element item = info.child(info.children().size() - 1);
        if (null == item)
        {
            logger.error("subOpt item is null info.children = " + info.children());
            return null;
        }
        Elements links = item.select("a[href]");
        if (null == links)
        {
            logger.error("subOpt links is null item = " + item);
            return null;
        }
        Element link = links.first();
        if (null == item)
        {
            logger.error("subOpt item is null links = " + links);
            return null;
        }
        String href = link.attr("href");
        _sleep(3000);
        return href;
    }
    
    private String nextUrl(Document doc)
    {
        Elements paginations = doc.select("div.pagination");
        if (null == paginations)
        {
            logger.error("nextUrl paginations is null doc = " + doc);
            return null;
        }
        Element pagination = paginations.first();
        if (pagination == null)
        {
            logger.error("nextUrl pagination is null paginations = " + paginations);
            return null;
        }
        Elements curs = pagination.select("li.page-current");
        if (curs == null)
        {
            logger.error("nextUrl curs is null pagination = " + pagination);
            return null;
        }
        Element cur = curs.first();
        if (cur == null)
        {
            logger.error("nextUrl cur is null curs = " + curs);
            return null;
        }
        Element e = cur.nextElementSibling();
        if (e == null)
        {
            logger.error("nextUrl e is null cur = " + cur);
            return null;
        }
        Elements es = e.select("a[href]");
        e = es.first();
        if (e == null)
        {
            logger.error("nextUrl es is null e = " + e);
            return null;
        }
        String str = e.html();
        try
        {
            return Integer.parseInt(str) + "";
        }
        catch (Exception ex)
        {
            logger.error("Integer.parseInt(str) error " + str, e);
        }
        return null;
    }
    
    public static void main(String[] args)
    {
        Map<Integer, String> hosts = HostHolder.getInstance().getHostParameters();
        CategoriesSearch search = new CategoriesSearch();
        ExecutorService executorCateService = Executors.newFixedThreadPool(20);
        DownloadTypeOption downloadTypeOption = new DownloadTypeOption();
        executorCateService.submit(() ->
        {
            search.search();
            List<CategoriesType> all = search.getListCategoriesType();
            all.stream().forEach(t ->
            {
                executorCateService.submit(() ->
                {
                    Thread.currentThread().setName("Mutil-categories-web-" + t.getUrl().replaceAll("[\\\\/]", "-"));
                    int latestPage = t.getPage();
                    while (true)
                    {
                        for (Map.Entry<Integer, String> entryHost : hosts.entrySet())
                        {
                            CategoriesTask categoriesTask = new CategoriesTask(t.getUrl());
                            String url = t.getUrl() + "?mode=async&function=get_block&block_id=list_videos_common_videos_list&sort_by=post_date&from=%d&_=" + Instant.now().toEpochMilli();
                            url = String.format(url, latestPage);
                            System.out.println("async url : " + url);
                            String nextPage = categoriesTask.executeCategoriesWeb(hosts, entryHost.getValue(), url, false, search.getTasksStart());
                            if (null == nextPage)
                            {
                                continue;
                            }
                            else
                            {
                                latestPage = Integer.valueOf(nextPage);
                                downloadTypeOption.updateLatestPageByType(latestPage, t.getUrl());
                                break;
                            }
                        }
                        try
                        {
                            TimeUnit.MILLISECONDS.sleep(5000);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            });
        });
    }
    
    
    public boolean executeDownload(Map<Integer, String> hosts, String url, int index, String ctype)
    {
        logger.info("executeDownload index : " + index + "  url = " + url);
        if (!hosts.containsKey(index))
        {
            return false;
        }
        String finalName = url.replaceAll(Constants.REGEX_FILE_NAME, "");
        boolean results = false;
        try
        {
            String rootDir = CommonConfig.getInstance().getFileSaveDir();
            results = MutilRequestWeb.httpClientUtil.getBytes(hosts.get(index) + url, null, null, finalName, ctype, rootDir);
            if (!results)
            {
                logger.error("executeDownload results is null url = " + url);
                _sleep(10000);
                return executeDownload(hosts, url, ++index, ctype);
            }
        }
        catch (RuntimeException e)
        {
            logger.error("executeDownload RuntimeException results is null url = " + url, e);
            _sleep(10000);
            return executeDownload(hosts, url, ++index, ctype);
        }
        catch (Exception e)
        {
            logger.error("executeDownload Exception results is null url = " + url, e);
            _sleep(10000);
            return executeDownload(hosts, url, ++index, ctype);
        }
        
        logger.info("executeDownload index : " + index + "  url = " + url + " download success!");
        return results;
    }
}
