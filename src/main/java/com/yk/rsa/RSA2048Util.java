package com.yk.rsa;

import com.yk.config.CommonConfig;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.yk.util.Constants.RSA_ALGORITHM;

/**
 * 通过java keytool生成私钥(root.jks)和公钥(root.crt), 私钥放入keystore文件中，使用密码保护起来
 */
public class RSA2048Util
{
    private static Logger logger = LoggerFactory.getLogger(RSA_ALGORITHM);

    private transient static Map<String, byte[]> keys = new ConcurrentHashMap<>();

    private static final String ALISA = "crazy";

    private static final String TYPE = "JKS";

    private static final String PRI = "rsa.jks";

    private static final String PUB = "rsa.crt";

    public static synchronized byte[] getPrivateKey() throws IOException,
            KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException
    {
        if (keys.containsKey("private") && null != keys.get("private"))
        {
            return keys.get("private");
        }
        try (InputStream inputStream = RSA2048Util.class.getClassLoader().getResourceAsStream(PRI))
        {
            KeyStore privateKeyStore = KeyStore.getInstance(TYPE);
            privateKeyStore.load(inputStream, CommonConfig.getInstance().getStorepasswd());
            CommonConfig.getInstance().setStorepasswd(null);
            PrivateKey key = (PrivateKey) privateKeyStore.getKey(ALISA, CommonConfig.getInstance().getKeypasswd());
            CommonConfig.getInstance().setKeypasswd(null);
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
        try (InputStream pri = RSA2048Util.class.getClassLoader().getResourceAsStream(PRI);
             InputStream pub = RSA2048Util.class.getClassLoader().getResourceAsStream(PUB))
        {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
            Certificate crt = certificateFactory.generateCertificate(pub);
            byte pubEncoded[] = crt.getPublicKey().getEncoded();
            keys.put("public", pubEncoded);
            return pubEncoded;
        }
    }

    public static String decrypt(String str) throws UnrecoverableKeyException,
            CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException,
            InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException
    {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(getPrivateKey());
        PrivateKey privateKey = KeyFactory.getInstance(RSA_ALGORITHM).generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
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
        PublicKey publicKey = KeyFactory.getInstance(RSA_ALGORITHM).generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    public static ByteBuffer decrypt(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
            UnrecoverableKeyException, CertificateException, KeyStoreException, IOException
    {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(getPrivateKey());
        PrivateKey privateKey = KeyFactory.getInstance(RSA_ALGORITHM).generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return ByteBuffer.wrap(cipher.doFinal(bytes));
    }

    public static ByteBuffer encrypt(byte[] bytes) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException,
            UnrecoverableKeyException, CertificateException, KeyStoreException, IOException
    {

        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(getPublicKey());
        PublicKey publicKey = KeyFactory.getInstance(RSA_ALGORITHM).generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return ByteBuffer.wrap(cipher.doFinal(bytes));
    }
}
