package com.yk.config;

import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CommonConfig
{
    private Logger logger = LoggerFactory.getLogger("common_config");

    private volatile String rootDir;

    private volatile String rootJKSPwd;

    private volatile Properties druidProperties;

    private CommonConfig()
    {
        loadDruidConf();
        loadConf();
        loadLog4j2xml();
    }

    private void loadDruidConf()
    {
        Object _confPath = System.getProperty("common.conf");
        if (null == _confPath)
        {
            return;
        }
        String _userDir = System.getProperty("user.dir");
        File confFile = new File(_userDir + File.separator + _confPath + File.separator + "druid.properties");
        if (!confFile.exists() || !confFile.isFile())
        {
            return;
        }
        try
        {
            druidProperties = new Properties();
            druidProperties.load(new FileInputStream(confFile));
        }
        catch (IOException e)
        {
            logger.error("CommonConfig load druid.properties file error ", e);
        }
    }

    private void loadConf()
    {
        Object _confPath = System.getProperty("common.conf");
        if (null == _confPath)
        {
            _confPath = "";
        }
        String _userDir = System.getProperty("user.dir");
        File confFile = new File(_userDir + File.separator + _confPath + File.separator + "conf.properties");
        Properties conf = new Properties();
        try
        {
            if (!confFile.exists() || !confFile.isFile())
            {
                InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties");
                conf.load(CommonConfig.class.getClassLoader().getResourceAsStream("conf.properties"));
            }
            else
            {
                conf.load(new FileInputStream(confFile));
            }
            rootDir = conf.getProperty("f.save.dir");
            File dir = new File(rootDir);
            if (!dir.exists())
            {
                boolean flag = dir.mkdirs();
                logger.info("dir.mkdirs result : " + flag);
            }
            rootJKSPwd = conf.getProperty("rsa.keystore.pwd");
        }
        catch (IOException e)
        {
            logger.error("CommonConfig load conf.properties file error ", e);
        }
    }

    private void loadLog4j2xml()
    {
        Object _log4jConfPath = System.getProperty("common.conf");
        if (null == _log4jConfPath)
        {
            return;
        }
        String _userDir = System.getProperty("user.dir");
        File log4jxml = new File(_userDir + File.separator + _log4jConfPath + File.separator + "log4j2.xml");
        if (!log4jxml.exists() || !log4jxml.isFile())
        {
            return;
        }
        try
        {
            ConfigurationSource source = new ConfigurationSource(new FileInputStream(log4jxml));
            Configurator.initialize(null, source);
        }
        catch (IOException e)
        {
            logger.error("CommonConfig load log4jxml file error ", e);
        }
    }

    public static CommonConfig getInstance()
    {
        return CommonConfigHolder.INSTANCE;
    }

    private static class CommonConfigHolder
    {
        public static CommonConfig INSTANCE = new CommonConfig();
    }

    public String getRootDir()
    {
        return rootDir;
    }

    public String getRootJKSPwd()
    {
        return rootJKSPwd;
    }

    public Properties getDruidProperties()
    {
        return druidProperties;
    }
}
