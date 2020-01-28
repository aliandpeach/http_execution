package com.yk.rsa;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainBinaryHexSHA256 {
    public static void main(String[] args) {
        String path = "C:\\Users\\Acer\\Downloads\\openssl-1.0.2u.tar.gz";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            FileInputStream input = new FileInputStream(new File(path));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len = 0;
            byte buff[] = new byte[1024];
            while ((len = input.read(buff)) != -1) {
                messageDigest.update(buff, 0, len);
                bos.write(buff, 0, len);
            }
            byte[] clone = new byte[bos.toByteArray().length];
            System.arraycopy(bos.toByteArray(), 0, clone, 0, clone.length);
            bos.flush();
            bos.close();
            input.close();

            /*BigInteger bigIntFile = new BigInteger(1, clone);
            String fileBinaryText = bigIntFile.toString(2);
            System.out.println(fileBinaryText);//文件的byte[]对象 转换为2进制字符串*/


            byte[] rtn = messageDigest.digest();
            System.out.println("通过每次updat byte[1024] 计算的hash : " + byte2Hex(rtn));
            System.out.println("把文件一次性读入后 计算的hash : " + byte2Hex(MessageDigest.getInstance("SHA-256").digest(clone)));
            System.out.println("把文件一次性读入后 计算的hash : " + DigestUtils.sha256Hex(clone));

            //1代表绝对值, 该方法可以把byte[] 转换为16进制字符串
            BigInteger bigInt = new BigInteger(1, rtn);
            String ahex = bigInt.toString(16);
            System.out.println("通过每次updat byte[1024] 计算的hash : " + ahex);//转换为16进制
            /*MessageDigest messageDigest1 = MessageDigest.getInstance("SHA1");
            messageDigest1.update(clone);
            byte[] rtn1 = messageDigest.digest();
            BigInteger bigInt1 = new BigInteger(1, rtn1);//1代表绝对值
            System.out.println(bigInt1.toString(16));//转换为16进制*/

            //把16进制字符串转为byte[]
            BigInteger bigInt2 = new BigInteger(ahex, 16);
            byte[] ary2 = bigInt2.toByteArray();

            BigInteger bigInt22 = new BigInteger(1, ary2);//1代表绝对值
            String ahexx = bigInt22.toString(16);
            System.out.println("通过每次updat byte[1024] 计算的hex hash 字符串，再通过bigInteger 转成byte[] 再转成hex hash : " + ahexx);

            // 为什么java 计算的 OpenSSL的文件包的sha356 hash值和win certutil计算的不一样呢
            //certutil -hashfile C:\Users\Acer\Downloads\openssl-1.0.2u.tar.gz SHA256
            // 因为cerutil是每次读取byte[1024]
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将byte转为16进制
     *
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
