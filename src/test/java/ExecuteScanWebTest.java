import com.yk.host.HostHolder;
import com.yk.httprequest.HttpClientUtil;
import com.yk.crypto.RSA2048Util;
import com.yk.util.Constants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.util.Map;

public class ExecuteScanWebTest
{
    public static void main(String[] args)
    {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties");
        InputStream in2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("/conf.properties");
        InputStream in3 = Thread.class.getResourceAsStream("conf.properties");
        InputStream in4 = Thread.class.getResourceAsStream("/conf.properties");
        Map<Integer, String> hosts = HostHolder.getInstance().getHostParameters();
        ExecuteScanWebTest test = new ExecuteScanWebTest();
        test.executeScanWeb(hosts, "top-rated/", false, 0);
    }

    public void executeScanWeb(Map<Integer, String> hosts, String url, boolean repeat, int index)
    {
        System.out.println("executeScanWeb index = " + index + " url = " + url);
        if (!hosts.containsKey(index))
        {
            executeScanWeb(hosts, url, false, 0);
            return;
        }
        String results = null;
        try
        {
            results = new HttpClientUtil().getString(/*hosts.get(index) + url*/"", null, null);
            if (null == results)
            {
                System.out.println("executeScanWeb results is null url = " + url);
                executeScanWeb(hosts, url, true, ++index);
                return;
            }
        }
        catch (RuntimeException e)
        {
            System.out.println("RuntimeException executeScanWeb results is null url = " + url);
            executeScanWeb(hosts, url, true, ++index);
            return;
        }
        catch (Exception e)
        {
            System.out.println("Exception executeScanWeb results is null url = " + url);
            executeScanWeb(hosts, url, true, ++index);
            return;
        }
        Document doc = null;
        try
        {
            doc = Jsoup.parse(results);
        }
        catch (Exception e)
        {
            System.out.println("Jsoup.parse(results) error");
        }
        if (null == doc)
        {
            executeScanWeb(hosts, url, true, ++index);
            System.out.println("something is wrong!!1");
            return;
        }

        Elements e = doc.select("div.main-container");
        if (null == e)
        {
            executeScanWeb(hosts, url, true, ++index);
            System.out.println("something is wrong!!2");
            return;
        }
        Element exx = e.first();
        if (null == exx)
        {
            executeScanWeb(hosts, url, true, ++index);
            System.out.println("something is wrong!!3");
            return;
        }
        Elements e2 = exx.select("div.item");
        if (null == e2 || e2.listIterator() == null)
        {
            executeScanWeb(hosts, url, true, ++index);
            System.out.println("something is wrong!!4");
            return;
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
                String enter_href = h.attr("href");
                String name = h.attr("title");
                enter_href = enter_href.replace(hosts.get(index), "");
                String r = subOpt(hosts, enter_href, 0);
                if (null == r)
                {
                    continue;
                }
                r = r.replace(hosts.get(index), "");
                System.out.println("r = " + r);
            }
        }
        String nextUrl = nextUrl(doc);
        boolean is = false;
        if (null == nextUrl || nextUrl.equals(url))
        {
            nextUrl = url;
            is = true;
            System.out.println("nexxt url is null or equals former url" + nextUrl);
        }
        System.out.println("next url : " + nextUrl);
        executeScanWeb(hosts, nextUrl, is, index);
    }

    private String subOpt(Map<Integer, String> hosts, String enter_url, int index)
    {
        System.out.println("subOpt enter_url = " + enter_url + " index = " + index);
        if (!hosts.containsKey(index))
        {
            return null;
        }
        String results = null;
        try
        {
            results = new HttpClientUtil().getString(hosts.get(index) + enter_url, null, null);
            if (null == results)
            {
                System.out.println("subOpt results is null enter_url = " + enter_url);
                return subOpt(hosts, enter_url, ++index);
            }
        }
        catch (Exception e)
        {
            System.out.println("subOpt RuntimeException results is null enter_url = " + enter_url + e);
            return subOpt(hosts, enter_url, ++index);
        }

        Document document = null;
        try
        {
            document = Jsoup.parse(results);
        }
        catch (Exception e)
        {
            System.out.println("subOpt Jsoup.parse(results); error");
        }
        if (document == null)
        {
            return null;
        }

        Elements tabs1 = document.select("div#tab_video_info");
        if (null == tabs1)
        {
            return null;
        }
        Element tab = tabs1.first();
        if (null == tab)
        {
            return null;
        }
        Elements infos = tab.select("div.info");
        if (null == infos)
        {
            return null;
        }
        Element info = infos.first();
        if (null == info)
        {
            return null;
        }
        if (info.children() == null || info.children().size() != 5)
        {
            return null;
        }
        Element item = info.child(4);
        if (null == item)
        {
            return null;
        }
        Elements links = item.select("a[href]");
        if (null == links)
        {
            return null;
        }
        Element link = links.first();
        if (null == item)
        {
            return null;
        }
        String href = link.attr("href");
        return href;
    }

    private String nextUrl(Document doc)
    {
        Elements paginations = doc.select("div.pagination");
        if (null == paginations)
        {
            return null;
        }
        Element pagination = paginations.first();
        if (pagination == null)
        {
            return null;
        }
        Elements curs = pagination.select("li.page-current");
        if (curs == null)
        {
            return null;
        }
        Element cur = curs.first();
        if (cur == null)
        {
            return null;
        }
        Element e = cur.nextElementSibling();
        if (e == null)
        {
            return null;
        }
        Elements es = e.select("a[href]");
        e = es.first();
        if (e == null)
        {
            return null;
        }
        String str = e.html();
        try
        {
//            String u = RSA2048Util.decrypt(Constants.TOP);
//            return u + Integer.parseInt(str) + "/";
        }
        catch (Exception ex)
        {
            System.out.println("Integer.parseInt(str) error " + str + e);
        }
        return null;
    }
}
