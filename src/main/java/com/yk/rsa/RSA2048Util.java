package com.yk.rsa;

import com.yk.config.CommonConfig;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.yk.util.Constants.*;

/**
 * 通过java keytool生成私钥(root.jks)和公钥(root.crt), 私钥放入keystore文件中，使用密码保护起来
 */
public class RSA2048Util
{
    private static Logger logger = LoggerFactory.getLogger("RSA");
    
    private static Map<String, byte[]> keys = new ConcurrentHashMap<>();
    
    public static synchronized byte[] getPrivateKey()
    {
        if (keys.containsKey("private") && null != keys.get("private"))
        {
            return keys.get("private");
        }
        
        InputStream inputStream = RSA2048Util.class.getClassLoader().getResourceAsStream("root.jks");
        try
        {
            KeyStore privateKeyStore = KeyStore.getInstance("JKS");
            privateKeyStore.load(inputStream, CommonConfig.getInstance().getRootJKSPwd().toCharArray());
            Certificate certificate = privateKeyStore.getCertificate(ALIAS);
            Key key = privateKeyStore.getKey(ALIAS, CommonConfig.getInstance().getRootJKSPwd().toCharArray());
            keys.put("private", key.getEncoded());
            return key.getEncoded();
        }
        catch (KeyStoreException e)
        {
            logger.error("KeyStoreException getPrivateKey error", e);
        }
        catch (CertificateException e)
        {
            logger.error("CertificateException getPrivateKey error", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("NoSuchAlgorithmException getPrivateKey error", e);
        }
        catch (IOException e)
        {
            logger.error("IOException getPrivateKey error", e);
        }
        catch (UnrecoverableKeyException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public static synchronized byte[] getPublicKey()
    {
        if (keys.containsKey("public") && null != keys.get("public"))
        {
            return keys.get("public");
        }
        InputStream inputStream = RSA2048Util.class.getClassLoader().getResourceAsStream("root.crt");
        try
        {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
            Certificate crt = certificateFactory.generateCertificate(inputStream);

            /*InputStream inputStream2 = RSA2048Util.class.getClassLoader().getResourceAsStream("root.crt");
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            int len = 0;
            byte buffer[] = new byte[1024 * 10];
            while ((len = inputStream2.read(buffer)) != -1)
                bout.write(buffer, 0, len);
            System.out.println(java.util.Base64.getEncoder().encodeToString(bout.toByteArray()));*/
            keys.put("public", crt.getPublicKey().getEncoded());
            return crt.getPublicKey().getEncoded();
        }
        catch (CertificateException e)
        {
            logger.error("CertificateException getPublicKey error", e);
        }
        return null;
    }
    
    public static ByteBuffer decrypt(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException
    {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(getPrivateKey());
        PrivateKey privateKey = KeyFactory.getInstance(RSA_ALGORITHM).generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return ByteBuffer.wrap(cipher.doFinal(bytes));
    }
    
    public static String decrypt(String str)
    {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(getPrivateKey());
        try
        {
            PrivateKey privateKey = KeyFactory.getInstance(RSA_ALGORITHM).generatePrivate(pkcs8EncodedKeySpec);
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            String outStr = new String(cipher.doFinal(Base64.decodeBase64(str.getBytes("UTF-8"))));
            return outStr;
        }
        catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException | InvalidKeyException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public static ByteBuffer encrypt(byte[] bytes) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException
    {
        
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(getPublicKey());
        PublicKey publicKey = KeyFactory.getInstance(RSA_ALGORITHM).generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return ByteBuffer.wrap(cipher.doFinal(bytes));
    }
    
    public static String encrypt(String str) throws NoSuchPaddingException, InvalidKeyException
    {
        
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(getPublicKey());
        try
        {
            PublicKey publicKey = KeyFactory.getInstance(RSA_ALGORITHM).generatePublic(x509EncodedKeySpec);
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            int blockSize = cipher.getBlockSize();
            String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
            return outStr;
        }
        catch (InvalidKeySpecException | UnsupportedEncodingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void main(String[] args)
    {
    }
}
