package com.crypto;

import cn.hutool.core.io.FileUtil;
import com.yk.rsa.RSA2048Util;
import org.junit.Test;
import sun.security.rsa.RSAUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class EncryptChartRecordTest
{
    
    @Test
    public void encrypt() throws IOException
    {
        File dir = new File("D:\\workspace\\flinkwork\\doc\\部署文档");
        List<File> listFile = FileUtil.loopFiles(dir, pathname -> !pathname.getPath().contains("encrypt_"));
        for (File file : listFile)
        {
            String path = file.getParentFile().getCanonicalPath();
            String dest = path + File.separator + "encrypt_" + file.getName();
            boolean is = !new File(dest).exists() || new File(dest).delete();
            System.out.println(is);
            try (FileChannel out = new FileOutputStream(path + File.separator + "encrypt_" + file.getName()).getChannel();
                 FileChannel in = new FileInputStream(file).getChannel();)
            {
                ByteBuffer byteBuffer = ByteBuffer.allocate(245);
                int len = -1;
                while ((len = in.read(byteBuffer)) != -1)
                {
                    byteBuffer.flip();
                    
                    if (byteBuffer.limit() < 245)
                    {
                        System.out.println();
                    }
                    byte temp[] = new byte[byteBuffer.limit()];
                    byteBuffer.get(temp);
                    ByteBuffer bytes = RSA2048Util.encrypt(temp);
                    out.write(bytes);
                    
                    byteBuffer.clear();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
}
