package com.yk.latest;

import com.yk.httprequest.JsonUtil;

import java.io.Serializable;

public class DownloadType implements Serializable
{
    private static final long serialVersionUID = -1432904091729641497L;

    private String type;

    private String originalUrl;

    private String latestUrl;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getOriginalUrl()
    {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl)
    {
        this.originalUrl = originalUrl;
    }

    public String getLatestUrl()
    {
        return latestUrl;
    }

    public void setLatestUrl(String latestUrl)
    {
        this.latestUrl = latestUrl;
    }

    @Override
    public String toString()
    {
        if (null == this)
        {
            return "{}";
        }
        return JsonUtil.toJson(this);
    }
}
