package com.crypto.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通过java keytool生成私钥(root.jks)和公钥(root.crt), 私钥放入keystore文件中，使用密码保护起来
 */
public class RSA2048Util
{
    private static Logger logger = LoggerFactory.getLogger("RSA");
    
    private static Map<String, byte[]> keys = new ConcurrentHashMap<>();
    
    private static synchronized byte[] getPrivateKey() throws IOException,
            KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException
    {
        if (keys.containsKey("private") && null != keys.get("private"))
        {
            return keys.get("private");
        }
        try (InputStream inputStream = RSA2048Util.class.getClassLoader().getResourceAsStream("root.jks"))
        {
            KeyStore privateKeyStore = KeyStore.getInstance("JKS");
            privateKeyStore.load(inputStream, "Lambda@@65535$$".toCharArray());
            PrivateKey key = (PrivateKey) privateKeyStore.getKey("crazy", "Lambda@@65535$$".toCharArray());
            keys.put("private", key.getEncoded());
            return key.getEncoded();
        }
    }
    
    public static synchronized byte[] getPublicKey() throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
    {
        if (keys.containsKey("public") && null != keys.get("public"))
        {
            return keys.get("public");
        }
        try (InputStream inputStream = RSA2048Util.class.getClassLoader().getResourceAsStream("root.jks"))
        {
            KeyStore privateKeyStore = KeyStore.getInstance("JKS");
            privateKeyStore.load(inputStream, "Lambda@@65535$$".toCharArray());
            Certificate certificate = privateKeyStore.getCertificate("crazy");
            keys.put("public", certificate.getPublicKey().getEncoded());
            //return certificate.getPublicKey().getEncoded();
        }
        try (InputStream inputStream = RSA2048Util.class.getClassLoader().getResourceAsStream("root.crt"))
        {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
            Certificate crt = certificateFactory.generateCertificate(inputStream);
            return crt.getPublicKey().getEncoded();
        }
    }
    
    public static String decrypt(String str) throws UnrecoverableKeyException,
            CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException,
            InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException
    {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(getPrivateKey());
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        String outStr = new String(cipher.doFinal(Base64.decodeBase64(str.getBytes("UTF-8"))));
        return outStr;
    }
    
    public static String encrypt(String str) throws UnrecoverableKeyException,
            CertificateException, NoSuchAlgorithmException, KeyStoreException,
            IOException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException
    {
        
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(getPublicKey());
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }
    
    public static void main(String[] args)
    {
        try
        {
            System.out.println(getPublicKey());
        } catch (IOException | CertificateException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e)
        {
            e.printStackTrace();
        }
        try
        {
            System.out.println(getPrivateKey());
        } catch (IOException | KeyStoreException | CertificateException | UnrecoverableKeyException | NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }
}
