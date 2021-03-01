package com.crypto.util;

import com.yk.rsa.EnDecryptUtil;
import com.yk.rsa.FileUtil;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

public class EnDecryptUtilTest
{
    
    private static Map<Integer, byte[]> keyMap = new HashMap<Integer, byte[]>();  //用于封装随机产生的公钥与私钥
    
    @Test
    public void fileEncryptToHex() throws Exception
    {
        String srcf = "C:\\Users\\Spinfo\\Desktop\\test\\lombok.zip";
        String tof = "C:\\Users\\Spinfo\\Desktop\\test\\lombok_encrypt.zip";
        String toHexf = "C:\\Users\\Spinfo\\Desktop\\test\\lombok_encrypt_hex.zip";
        
        new EnDecryptUtil().fileEncryptToHex(srcf, tof, toHexf, 245);
    }
    
    @Test
    public void hexDecryptToFile() throws IOException, UnrecoverableKeyException, CertificateException,
            NoSuchAlgorithmException, KeyStoreException
    {
        String srcHexf = "C:\\Users\\Spinfo\\Desktop\\test\\record__hex.txt";
        String tof = "C:\\Users\\Spinfo\\Desktop\\test\\record__hex_src.txt";
        new EnDecryptUtil().hexDecryptToFile(srcHexf, tof, 256);
    }
    
    
    @Test
    public void fileEncryptToHex1() throws Exception
    {
        String srcf = "C:\\Users\\Spinfo\\Desktop\\test\\lombok.zip";
        String toHexf = "C:\\Users\\Spinfo\\Desktop\\test\\lombok_encrypt_hex.zip";
        
        new EnDecryptUtil().fileEncryptToHex(srcf, toHexf, 245);
    }
    
    @Test
    public void hexDecryptToFile1() throws Exception
    {
        String srcHexf = "C:\\Users\\Spinfo\\Desktop\\test\\lombok_encrypt_hex.zip";
        String tof = "C:\\Users\\Spinfo\\Desktop\\test\\lombok_decrypt.zip";
        new EnDecryptUtil().hexDecryptToFile(srcHexf, tof);
    }
    
    @Test
    public void decrypt() throws IOException, UnrecoverableKeyException, CertificateException,
            NoSuchAlgorithmException, KeyStoreException, InvalidKeySpecException, BadPaddingException,
            NoSuchPaddingException, IllegalBlockSizeException, InvalidKeyException
    {
        new EnDecryptUtil().decrypt("C:\\Users\\Spinfo\\Desktop\\test");
    }
    
    @Test
    public void encrypt() throws IOException, UnrecoverableKeyException, CertificateException,
            NoSuchAlgorithmException, KeyStoreException, InvalidKeySpecException, BadPaddingException,
            NoSuchPaddingException, IllegalBlockSizeException, InvalidKeyException
    {
        new EnDecryptUtil().encrypt("C:\\Users\\Spinfo\\Desktop\\test", false);
    }
    
    @Test
    public void copy() throws IOException
    {
        new FileUtil().copy("C:\\Users\\Spinfo\\Desktop\\test", "C:\\Users\\Spinfo\\Desktop\\test");
    }
}
