package com.crypto;

import cn.hutool.core.io.FileUtil;
import com.yk.rsa.RSA2048Util;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public class EncryptChartRecordTest
{

    @Test
    public void encrypt() throws IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, KeyStoreException, InvalidKeySpecException, IllegalBlockSizeException
    {
        File dir = new File("C:\\Users\\yangkai\\Desktop\\test");
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
                    // 读取文件最后部分的字节数可能小于245, ByteBuffer实际长度为limit, 所以每次根据limit的长度获取真实的byte[]
                    byte temp[] = new byte[byteBuffer.limit()];
                    byteBuffer.get(temp);
                    ByteBuffer bytes = RSA2048Util.encrypt(temp);
                    out.write(bytes);

                    byteBuffer.clear();
                }
            }
        }
    }

}
