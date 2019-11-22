package com.yk.task;

import com.yk.httprequest.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoriesTask extends AbstractTask
{
    private Logger logger = LoggerFactory.getLogger("Categories");

    private String vType;

    public CategoriesTask(String vType)
    {
        this.vType = vType;
    }

    public String executeCategoriesWeb(String host, String url)
    {
        try
        {
            String result = HttpClientUtil.getString(host + url, null, null);
            return result;
        }
        catch (RuntimeException e)
        {
            return null;
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
