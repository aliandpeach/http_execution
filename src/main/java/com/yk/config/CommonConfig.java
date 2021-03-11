package com.yk.config;

import cn.hutool.core.util.HexUtil;
import com.yk.rsa.RSA2048Util;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CommonConfig
{
    private static Logger logger = LoggerFactory.getLogger("common_config");
    
    private volatile String fileSaveDir;
    
    private transient volatile char[] symmetrickey;
    
    private transient volatile char[] symmetricsalt;
    
    private transient volatile char[] storepasswd;
    
    private transient volatile char[] keypasswd;
    
    private volatile Properties druidProperties;
    
    static
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
            String fileSaveDir = conf.getProperty("f.save.dir");
            File dir = new File(fileSaveDir);
            if (!dir.exists())
            {
                boolean flag = dir.mkdirs();
                logger.info("dir.mkdirs result : " + flag);
            }
            CommonConfig.getInstance().setFileSaveDir(fileSaveDir);
            
            char[] storepasswd = null;
            
            String storepasswdString = conf.getProperty("rsa.storepasswd.pwd");
            if (null == storepasswdString || storepasswdString.trim().length() == 0)
            {
                JPasswordField pwdField = new JPasswordField();
                Object[] message = {"请输入秘钥库口令：", pwdField};
                int res = JOptionPane.showConfirmDialog(null, message, " ", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                storepasswd = pwdField.getPassword();
            }
            else
            {
                storepasswd = storepasswdString.toCharArray();
            }
            
            char[] keypasswd = null;
            String keypasswdString = conf.getProperty("rsa.keypasswd.pwd");
            if (null == keypasswdString || keypasswdString.trim().length() == 0)
            {
                JPasswordField pwdField = new JPasswordField();
                Object[] message = {"请输入私钥口令: ", pwdField};
                int res = JOptionPane.showConfirmDialog(null, message, " ", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                keypasswd = pwdField.getPassword();
            }
            else
            {
                keypasswd = keypasswdString.toCharArray();
            }
            
            if (null == storepasswd || null == keypasswd)
            {
                throw new RuntimeException("rsa storepasswd and keypasswd not loaded!");
            }
            
            CommonConfig.getInstance().setStorepasswd(storepasswd);
            CommonConfig.getInstance().setKeypasswd(keypasswd);
            
            String symmetrickeyString = conf.getProperty("symmetric.key");
            String symmetricsaltString = conf.getProperty("symmetric.salt");
            if (null == symmetrickeyString || symmetrickeyString.trim().length() == 0
                    || null == symmetricsaltString || symmetricsaltString.trim().length() == 0)
            {
                throw new RuntimeException("symmetric encryption key not loaded!");
            }
            char[] symmetrickey = new String(RSA2048Util.decrypt(HexUtil.decodeHex(symmetrickeyString)).array()).toCharArray();
            char[] symmetricsalt = new String(RSA2048Util.decrypt(HexUtil.decodeHex(symmetricsaltString)).array()).toCharArray();
            CommonConfig.getInstance().setSymmetrickey(symmetrickey);
            CommonConfig.getInstance().setSymmetricsalt(symmetricsalt);
        }
        catch (Exception e)
        {
            logger.error("SecretInitializing load conf.properties file error ", e);
            throw new RuntimeException(e);
        }
    }
    
    private CommonConfig()
    {
        loadDruidConf();
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
    
    public Properties getDruidProperties()
    {
        return druidProperties;
    }
    
    public String getFileSaveDir()
    {
        return fileSaveDir;
    }
    
    public void setFileSaveDir(String fileSaveDir)
    {
        this.fileSaveDir = fileSaveDir;
    }
    
    public char[] getSymmetrickey()
    {
        return symmetrickey;
    }
    
    private void setSymmetrickey(char[] symmetrickey)
    {
        this.symmetrickey = symmetrickey;
    }
    
    public char[] getSymmetricsalt()
    {
        return symmetricsalt;
    }
    
    private void setSymmetricsalt(char[] symmetricsalt)
    {
        this.symmetricsalt = symmetricsalt;
    }
    
    public char[] getStorepasswd()
    {
        return storepasswd;
    }
    
    public void setStorepasswd(char[] storepasswd)
    {
        this.storepasswd = storepasswd;
    }
    
    public char[] getKeypasswd()
    {
        return keypasswd;
    }
    
    public void setKeypasswd(char[] keypasswd)
    {
        this.keypasswd = keypasswd;
    }
}
