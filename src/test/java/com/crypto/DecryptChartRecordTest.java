package com.crypto;

import cn.hutool.core.io.FileUtil;
import com.yk.rsa.RSA2048Util;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public class DecryptChartRecordTest
{
    @Test
    public void decrypt() throws IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, KeyStoreException, InvalidKeySpecException, IllegalBlockSizeException
    {
        File dir = new File("C:\\Users\\yangkai\\Desktop\\test");
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
                // 通过RSA2048加密的文件，大小一定是size%256==0否则就是有问题的（1-245长度的byte[]都会被加密为byte[256]）
                System.out.println(size % 256);
                int len = -1;
                while ((len = in.read(byteBuffer)) != -1)
                {
                    byteBuffer.flip();

                    ByteBuffer bytes = RSA2048Util.decrypt(byteBuffer.array());
                    out.write(bytes);

                    byteBuffer.clear();
                }
            }
        }
    }
}
