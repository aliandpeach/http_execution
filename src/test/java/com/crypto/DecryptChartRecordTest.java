package com.crypto;

import cn.hutool.core.io.FileUtil;
import com.yk.rsa.RSA2048Util;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class DecryptChartRecordTest
{
    @Test
    public void decrypt() throws IOException
    {
        File dir = new File("D:\\workspace\\flinkwork\\doc\\部署文档");
        List<File> listFile = FileUtil.loopFiles(dir, pathname -> pathname.getPath().contains("encrypt_"));
        for (File file : listFile)
        {
            String path = file.getParentFile().getCanonicalPath();
            String dest = path + File.separator + file.getName().replace("encrypt_", "decrypt_");
            boolean is = !new File(dest).exists() || new File(dest).delete();
            System.out.println(is);
            try (FileChannel out = new FileOutputStream(dest).getChannel();
                 FileChannel in = new FileInputStream(file).getChannel();)
            {
                ByteBuffer byteBuffer = ByteBuffer.allocate(256);
                long size = in.size();
                System.out.println(size % 256);
                int len = -1;
                while ((len = in.read(byteBuffer)) != -1)
                {
                    byteBuffer.flip();
                    
                    ByteBuffer bytes = RSA2048Util.decrypt(byteBuffer.array());
                    if(bytes.limit() < 245){
                        System.out.println();
                    }
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
