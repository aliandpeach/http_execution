package com.yk.rsa;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Stack;

public class BinaryHexSHA256
{
    /**
     * github连接过程中的回显的指纹信息，就是本地的publicKey进行Base64解密后，再执行MessageDigest("SHA-256") 后转16进制字符串
     *
     * @param args
     */
    public static void main(String[] args)
    {
        String path = "F:\\Download\\vcredist_x64.exe";
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            FileInputStream input = new FileInputStream(new File(path));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len = 0;
            byte buff[] = new byte[1024];
            while ((len = input.read(buff)) != -1)
            {
                // 严格注意这里的len, 如果写成buff.length那么当最后一次读取小于1024的时候，就会出错
                // 原因就是byte[1024] 没有填充的位置默认是0，sha-256的计算会包括哪些0
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
            
            // certutil -hashfile [fille] SHA256
        }
        catch (NoSuchAlgorithmException | IOException e)
        {
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
    private static String change(String number, int from, int to)
    {
        return new BigInteger(number, from).toString(to);
    }
    
    /**
     * 将byte转为16进制 (1)
     *
     * @param bytes
     * @return
     */
    public static String byte2Hex(byte[] bytes)
    {
        StringBuilder stringBuffer = new StringBuilder();
        String temp = null;
        for (int i = 0; i < bytes.length; i++)
        {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1)
            {
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
    
    /**
     * 将byte转为16进制 (2)
     *
     * @param bytes
     * @return
     */
    public static String byteArrayToHex(byte[] bytes)
    {
        StringBuilder result = new StringBuilder();
        for (int index = 0, len = bytes.length; index <= len - 1; index += 1)
        {
            int char1 = ((bytes[index] >> 4) & 0xF);
            char chara1 = Character.forDigit(char1, 16);
            int char2 = ((bytes[index]) & 0xF);
            char chara2 = Character.forDigit(char2, 16);
            result.append(chara1);
            result.append(chara2);
        }
        return result.toString();
    }
    
    /**
     * 将byte转为16进制 (3)
     *
     * @param bytes
     * @return
     */
    public static String byteArrayToHex2(byte[] bytes)
    {
        StringBuilder result = new StringBuilder();
        for (int index = 0, len = bytes.length; index <= len - 1; index += 1)
        {
            
            String invalue1 = Integer.toHexString((bytes[index] >> 4) & 0xF);
            String intValue2 = Integer.toHexString(bytes[index] & 0xF);
            result.append(invalue1);
            result.append(intValue2);
        }
        return result.toString();
    }
    
    /**
     * 将16进制转为byte[]  (1)
     *
     * @param hex
     * @return
     */
    public static byte[] hexToByteArray(String hex)
    {
        int l = hex.length();
        byte[] data = new byte[l / 2];
        for (int i = 0; i < l; i += 2)
        {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
    
    /**
     * 将16进制转为byte[]  (2)
     *
     * @param hexString
     * @return
     */
    public static byte[] hexToByteArray2(String hexString)
    {
        byte[] result = new byte[hexString.length() / 2];
        for (int len = hexString.length(), index = 0; index <= len - 1; index += 2)
        {
            String subString = hexString.substring(index, index + 2);
            int intValue = Integer.parseInt(subString, 16);
            result[index / 2] = (byte) intValue;
        }
        return result;
    }
    
    
    /**
     * 二制度字符串转字节数组，如 101000000100100101110000 -> A0 09 70
     *
     * @param input 输入字符串。
     * @return 转换好的字节数组。
     */
    public static byte[] string2bytes(String input)
    {
        StringBuilder in = new StringBuilder(input);
        // 注：这里in.length() 不可在for循环内调用，因为长度在变化
        int remainder = in.length() % 8;
        if (remainder > 0)
            for (int i = 0; i < 8 - remainder; i++)
                in.append("0");
        byte[] bts = new byte[in.length() / 8];
        
        // Step 8 Apply compression
        for (int i = 0; i < bts.length; i++)
            bts[i] = (byte) Integer.parseInt(in.substring(i * 8, i * 8 + 8), 2);
        
        return bts;
    }
    
    /**
     * 字节数组转字符串，如 A0 09 70 -> 101000000000100101110000。
     *
     * @param bts 转入字节数组。
     * @return 转换好的只有“1”和“0”的字符串。
     */
    public static String bytes2String(byte[] bts)
    {
        String[] dic = {"0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111",
                "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"};
        StringBuilder out = new StringBuilder();
        for (byte b : bts)
        {
            String s = String.format("%x", b);
            s = s.length() == 1 ? "0" + s : s;
            out.append(dic[Integer.parseInt(s.substring(0, 1), 16)]);
            out.append(dic[Integer.parseInt(s.substring(1, 2), 16)]);
        }
        return out.toString();
    }
    
    private static String binary2Integer(int i, int radix)
    {
        Stack<Integer> stack = new Stack<>();
        while (i > 0)
        {
            stack.push(i % radix);
            i = i / radix;
        }
        
        StringBuffer str = new StringBuffer();
        while (!stack.empty())
        {
            str.append(stack.pop());
        }
        
        return str.toString();
    }
    
    private static int integer2Binary(String s, int radix)
    {
        int r = 0;
        char ary[] = s.toCharArray();
        Stack<Integer> stack = new Stack<>();
        for (int k = ary.length - 1; k >= 0; k--)
        {
            System.out.println(k);
            stack.push(Integer.parseInt(ary[k] + ""));
        }
        
        int t = stack.size() - 1;
        while (!stack.empty())
        {
            int p = stack.pop();
            r += p * (int) Math.pow(radix, t);
            t--;
        }
        return r;
    }
}
