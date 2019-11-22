package com.yk.task;

import java.io.Serializable;

public class DownloadScan implements Serializable
{
    private static final long serialVersionUID = -8323664616301391119L;
    private long pkid;

    private String url;

    private String fileName;

    private String fileHash;

    private int success;

    public long getPkid()
    {
        return pkid;
    }

    public void setPkid(long pkid)
    {
        this.pkid = pkid;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFileHash()
    {
        return fileHash;
    }

    public void setFileHash(String fileHash)
    {
        this.fileHash = fileHash;
    }

    public int getSuccess()
    {
        return success;
    }

    public void setSuccess(int success)
    {
        this.success = success;
    }
}
