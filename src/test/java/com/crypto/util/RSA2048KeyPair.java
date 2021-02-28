package com.crypto.util;

import com.yk.rsa.RSA2048Util;
import org.junit.Test;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSA2048KeyPair
{

    private static Map<Integer, byte[]> keyMap = new HashMap<Integer, byte[]>();  //用于封装随机产生的公钥与私钥

    @Test
    public void fileEncryptToHex() throws Exception
    {
        String srcf = "C:\\Users\\yangkai\\Desktop\\test\\record__.txt";
        String tof = "C:\\Users\\yangkai\\Desktop\\test\\record__to.txt";
        String toHexf = "C:\\Users\\yangkai\\Desktop\\test\\record__hex.txt";

        //生成公钥私钥
        // genKeyPair();
        loadKeyPair();
        // 加密文件（RSA2048每次只能加密245字节，加密后（无论被加密的字节数是多少）的结果是256字节）
        try (InputStream inputStream = new FileInputStream(new File(srcf));
             OutputStream outputStream = new FileOutputStream(new File(tof));
             FileWriter writer = new FileWriter(toHexf))
        {
            int len = -1;
            byte[] buf = new byte[245];
            while ((len = inputStream.read(buf)) != -1)
            {
                if (len < 245)
                {
                    // 源文件最后读取的部分可能小于245字节
                    byte[] temp = new byte[len];
                    System.arraycopy(buf, 0, temp, 0, len);
                    byte[] encrypt = encrypt(temp, keyMap.get(0));
                    String hex = new BigInteger(1, encrypt).toString(16);
                    outputStream.write(encrypt);
                    writer.write(hex);
                    break;
                }
                byte[] encrypt = encrypt(buf, keyMap.get(0));
                String hex = new BigInteger(1, encrypt).toString(16);
                writer.write(hex + "\n");
                outputStream.write(encrypt);
            }
        }
    }

    @Test
    public void hexDecryptToFile() throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException
    {
        String srcHexf = "C:\\Users\\yangkai\\Desktop\\test\\record__hex.txt";
        String tof = "C:\\Users\\yangkai\\Desktop\\test\\record__hex_src.txt";
        loadKeyPair();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(new File(srcHexf)), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader);
             OutputStream outputStream = new FileOutputStream(new File(tof));)
        {
            String line = null;
            while ((line = bufferedReader.readLine()) != null)
            {
                line.replace("\n", "");
                byte array[] = new BigInteger(line, 16).toByteArray();
//                byte array[] = HexUtil.decodeHex(line);
                byte[] temp = new byte[256];
                // 经过BigInteger计算的byte 可能会有符号的题
                if (array.length == 257 && array[0] == (byte) 0)
                {
                    System.arraycopy(array, 1, temp, 0, 256);
                } else if (array.length == 255)
                {
                    System.arraycopy(array, 0, temp, 1, 255);
                } else if (array.length == 256)
                {
                    System.arraycopy(array, 0, temp, 0, 256);
                } else
                {
                    throw new IOException();
                }
                //只能按照每次取出256个字节来解密（密文文件的大小 % 256 必须等于0）
                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(temp))
                {
                    int len = -1;
                    byte[] buf = new byte[256];
                    while ((len = inputStream.read(buf)) != -1)
                    {
                        try
                        {
                            byte[] decrypt = decrypt(buf, keyMap.get(1));
                            outputStream.write(decrypt);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static byte[] copy(byte[] src, byte[] target)
    {
        byte[] bytes = new byte[src.length + target.length];
        System.arraycopy(src, 0, bytes, 0, src.length);
        System.arraycopy(target, 0, bytes, src.length, target.length);
        return bytes;
    }

    /**
     * 随机生成密钥对
     *
     * @throws NoSuchAlgorithmException
     */
    public static void genKeyPair() throws NoSuchAlgorithmException
    {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGenerator.initialize(2048, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        // 得到私钥字符串
        // 将公钥和私钥保存到Map
        keyMap.put(0, publicKey.getEncoded());  //0表示公钥
        keyMap.put(1, privateKey.getEncoded());  //1表示私钥
    }

    /**
     * 随机生成密钥对
     *
     * @throws NoSuchAlgorithmException
     */
    public static void loadKeyPair() throws NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException
    {
        keyMap.put(0, RSA2048Util.getPublicKey());  //0表示公钥
        keyMap.put(1, RSA2048Util.getPrivateKey());  //1表示私钥
    }

    /**
     * RSA公钥加密
     *
     * @param message   加密字符串
     * @param publicKey 公钥
     * @return byte[]
     * @throws Exception 加密过程中的异常信息
     */
    public static byte[] encrypt(byte[] message, byte[] publicKey) throws Exception
    {
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKey));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(message);
    }

    /**
     * RSA私钥解密
     *
     * @param message    加密字符串
     * @param privateKey 私钥
     * @return byte[]
     * @throws Exception 解密过程中的异常信息
     */
    public static byte[] decrypt(byte[] message, byte[] privateKey) throws Exception
    {
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKey));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return cipher.doFinal(message);
    }
}
