package com.crypto.cert;

import cn.hutool.core.io.resource.ClassPathResource;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.junit.Test;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;

/**
 * ConvertTest
 */

public class ConvertTest
{
    /**
     * 转换keystore -> pkcs12
     *
     * @throws Exception
     */
    @Test
    public void convertKeystoreToPCKS12() throws Exception
    {
        KeyStore rsakeystore = KeyStore.getInstance("JKS");
        rsakeystore.load(new FileInputStream(new ClassPathResource("rsa.jks").getFile()), "QtyyLjs.WjtbYwz$.".toCharArray());
        PrivateKey key = (PrivateKey) rsakeystore.getKey("crazy", "RpmyBkd.YxqyRxs$.".toCharArray());
        byte[] encodedKey = key.getEncoded();
        /**
         * PKCS8 不加密
         */
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(encodedKey);
        byte[] bb = pkcs8EncodedKeySpec.getEncoded();
        PrivateKey key1 = KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec);
        boolean boo = key1.equals(key); //true
        boo = Arrays.equals(encodedKey, bb);//true
    
        //base64KeyString 是PKCS8不加密的格式 相当于转换成pkcs12后使用命令：
        // openssl pkcs12 -nocerts -nodes -in test.p12 -out test-key.pem  //该命令生成-----BEGIN PRIVATE KEY-----
        // openssl pkcs12 -nocerts -in test.p12 -out test-key.pem         //该命令生成-----BEGIN ENCRYPTED PRIVATE KEY-----
        String base64KeyString = Base64.getEncoder().encodeToString(encodedKey);
        
        System.out.println();
        System.out.println(base64KeyString);
        System.out.println();
        Certificate certificate = rsakeystore.getCertificate("crazy");
        byte[] encodedCer = certificate.getEncoded();
        String base64CerString = Base64.getEncoder().encodeToString(encodedCer);
        System.out.println(base64CerString);
        System.out.println();
        
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(rsakeystore, "RpmyBkd.YxqyRxs$.".toCharArray());
        
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        KeyStore truststore = KeyStore.getInstance("JKS");
        truststore.load(null, null);
        truststore.setCertificateEntry("crazy", certificate);
        trustManagerFactory.init(truststore);
        
        
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        
        
        /**
         * 导出为PKCS12
         */
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(null, null);
        Enumeration<String> aliases = rsakeystore.aliases();
        while (aliases.hasMoreElements())
        {
            String alias = aliases.nextElement();
            PrivateKey k = (PrivateKey) rsakeystore.getKey(alias, "RpmyBkd.YxqyRxs$.".toCharArray());
            Certificate[] c = rsakeystore.getCertificateChain(alias);
            pkcs12.setKeyEntry(alias, k, "RpmyBkd.YxqyRxs$.".toCharArray(), c);
        }
        pkcs12.store(new FileOutputStream("D:\\test.p12"), "QtyyLjs.WjtbYwz$.".toCharArray());
    }
    
    
    /**
     * -----BEGIN ENCRYPTED PRIVATE KEY-----
     * 使用PKCS8EncodedKeySpec去加载
     */
    @Test
    public void testLoadPKCS8Encrypt(){
        String PKCS8Encrypt = "MIIFHDBOBgkqhkiG9w0BBQ0wQTApBgkqhkiG9w0BBQwwHAQIW5pjs6k/TfcCAggA\n" +
                "MAwGCCqGSIb3DQIJBQAwFAYIKoZIhvcNAwcECOnlK7pFBjFXBIIEyHCc3KIfUwBn\n" +
                "nkfBaTeU2U5Y0Jmq7XdlIArLUIEgjwzkbUKIjj67N89PXFUjcIxL2rzfcbJ7rJYy\n" +
                "pth2U1y9qPQu6gw0JJPVs0WDLYiWocQ5PjoxFeTDm2JIMT029deNsHHA+LVjAeaf\n" +
                "luTmZGjf9rCZkqjV3xd3r0JOzVs7FOc8adyG21sugRonUQ5elxz+IZ1YB9a30AbD\n" +
                "sNPZQu6/9UQzZPu3tSjkauEjGIDpyVqlcyVSwCSLjecJZGtGrbW4vGYcuJQCK3vd\n" +
                "KRtWUOE+AoVtZisQ9YYdTMXex3zbZwhwv/mo29zpcqf4e2GI8ip4uzyGva4EQS5+\n" +
                "GRqPcsSxCX7RY2XbDX2tj7s5MNNK7yC2vGkx/widQWVWbwn0kO73qw9JyLX+zgUf\n" +
                "bMxfsd56b/MFtCZazBoJWHT9AU2C0ydrKtPRJvE4uplO5D42K3OgJDLpBBC4AP6S\n" +
                "6CI8a9HMklePSuGgKGxieUoD0gt4/PAlk0dLKnjYErriy41vOiirUh2corIA0ZKL\n" +
                "ZOqx9Lv7ZnFQ38KC1mQdO6G/JOTOZ5Hb4TWdz/Z0j82S0szOZgragLhHTRfnFgBU\n" +
                "91brfObmr+o1Vru7dsN4wsKSW8298JKKNH6Vy/vIZMPU4WEs5RChMuqowyiGsISG\n" +
                "dSQtFJIliNDuZtFF0Dkj/HvXW0Tx+4IFppkglPbl0VKS8Hbh7QW+AJ07Dzk8/+pi\n" +
                "7fLGOnVyZd2+KwRF47YWBPB2TbFyTUFp8SzlY0ejNp8zUlBsi2wmscllwpSdGy/0\n" +
                "dLiMmbFc/1pYr1n+nmfjcSFcrdX1ozNie/6MNBHSbuPYIJF10T66HA3ZvDFYcAIz\n" +
                "uTFykguZn2xglKLhQdKtQlMqgLUrKjrsxENGsoWayjpsByR7X1oHN641MI3W8y9Z\n" +
                "Zs8Obl9IhJPSaeS/ukNQWJi9xO/fkblThbkjhl5MAQ9dkIndaQCG8MPEzj45RD6I\n" +
                "DpuBAdTUj6Ejvy9ZAocqz6L4PPwwch4XntQm37MG9eXyELIwYGlfNaxLgbOmOnBT\n" +
                "RCvgC4Z7S/Nx6F0fj7voZ7zF5qBEPzKbW2HorTzJwzMLJ1FCuI9LYNdAsURPAUIP\n" +
                "A04pM8S54SDJqIExhs7Fwvg8ONRhCwkJxYyHRwTjeegpQERXneLP3w6dCEA69jAv\n" +
                "DRQUG+0exE37PS8lkS6dutF4VDAHDYqhd/kSX30oaKeM+qC3GGPDpOTyHcOetO9v\n" +
                "Xh/KphdvOoDg2Et8rOrOx1KsYth9L4xVf9aDXk1T/yd3zkGc1XKw70UEn35xZEeF\n" +
                "VCmhbuLUgw4pInsAptNLezXZEtvsBz4YdzPA5i3oHdJqc8l2t7zDeTeT2uX//0LJ\n" +
                "kCQQ15xNA9ys986JMncLeG5yAskOQGq8Wv63qbIJmQRoT7XVYc4imJOxYunWm7hJ\n" +
                "KXwtGnL7DSaCxLJ5OL1eNMF4Ll9JQb/yqq0dLOCK5VeFmVZ/pjsBLW3Kbml1N8AE\n" +
                "hkUWuca/QSX4GePtt/pIvgcz6tDU5Rv39Y3B3eoqTYby4p0LHTElzDUnYT1qz1ZX\n" +
                "CTrHSLK4wlp0P+g2FNcoj9qa6UZH+iKuoSmplCK+w6r3vCvhh9W2X0fvnXfDUxrd\n" +
                "4dMkh5tbHb+VKg1OgBoclA==";
        PKCS8Encrypt = PKCS8Encrypt.replace("\n", "");
    }
}
