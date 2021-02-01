package com.yk.rsa;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class MainBinaryHexSHA256 {

    /**
     * github连接过程中的回显的指纹信息，就是本地的publicKey进行Base64解密后，再执行MessageDigest("SHA-256") 后转16进制字符串
     *
     * @param args
     */
    public static void main(String[] args) {
        String path = "D:\\Program Files (x86)\\几鸡客户端.7z";
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
            System.out.println("byte2Hex 转换byte[]为16进制 : " + byte2Hex(rtn));
            System.out.println("把文件一次性读入后 计算的hash 再转换为16进制 : " + byte2Hex(MessageDigest.getInstance("SHA-256").digest(clone)));
            System.out.println("把文件一次性读入后 计算的hash 再转换为16进制 : " + DigestUtils.sha256Hex(clone));

            //1代表绝对值, 该方法可以把byte[] 转换为16进制字符串
            BigInteger bigInt = new BigInteger(1, rtn);
            String ahex = bigInt.toString(16);
            System.out.println("BigInteger 转换byte[]为16进制 : " + ahex);//转换为16进制
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

        int _num = 13;
        System.out.println("十进制转二进制：" + Integer.toBinaryString(_num));
        System.out.println("十进制转八进制：" + Integer.toOctalString(_num));
        System.out.println("十进制转十六进制：" + Integer.toHexString(_num));
        System.out.println("十进制转二进制：" + Integer.toString(_num, 2));
        System.out.println("十进制转八进制：" + Integer.toString(_num, 8));
        System.out.println("十进制转十六进制：" + Integer.toString(_num, 16));


        System.out.println("十六进制转换十进制：" + new BigInteger("fffff", 16).intValue());
        System.out.println("十六进制转换十进制：" + change("fffff", 16, 10));

        String _onum = "1410";
        System.out.println("八进制转换十进制：" + Integer.parseInt(_onum, 8));
        System.out.println("八进制转换十进制：" + Integer.valueOf(_onum, 8));

        String _hnum = "fffff";
        System.out.println("十六进制转换十进制：" + Integer.parseInt(_hnum, 16));
        System.out.println("十六进制转换十进制：" + Integer.valueOf(_hnum, 16));

        String _bnum = "100110";
        System.out.println("二进制转换十进制：" + Integer.parseInt(_bnum, 2));
        System.out.println("二进制转换十进制：" + Integer.valueOf(_bnum, 2));

        System.out.println(binary2Integer(13, 2));
        System.out.println(binary2Integer(13, 8));
        System.out.println(integer2Binary("1101", 2));
    }

    /**
     * number   要转换的数
     * from     原数的进制
     * to       要转换成的进制
     */
    private static String change(String number, int from, int to) {
        return new BigInteger(number, from).toString(to);
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

    private static String binary2Integer(int i, int radix) {
        Stack<Integer> stack = new Stack<>();
        while (i > 0) {
            stack.push(i % radix);
            i = i / radix;
        }

        StringBuffer str = new StringBuffer();
        while (!stack.empty()) {
            str.append(stack.pop());
        }

        return str.toString();
    }

    private static int integer2Binary(String s, int radix) {
        int r = 0;
        char ary[] = s.toCharArray();
        Stack<Integer> stack = new Stack<>();
        for (int k = ary.length - 1; k >= 0; k--) {
            System.out.println(k);
            stack.push(Integer.parseInt(ary[k] + ""));
        }

        int t = stack.size() - 1;
        while (!stack.empty()) {
            int p = stack.pop();
            r += p * (int) Math.pow(radix, t);
            t--;
        }
        return r;
    }
}
