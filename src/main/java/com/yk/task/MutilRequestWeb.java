package com.yk.task;

import com.yk.host.HostHolder;
import com.yk.latest.DownloadType;
import com.yk.latest.DownloadTypeHolder;
import com.yk.latest.DownloadTypeOption;
import com.yk.mysql.DruidConnection;
import com.yk.task.check.ProblemChecker;
import com.yk.task.consumer.DataConsumer;
import com.yk.task.producer.DataProducer;
import com.yk.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.yk.util.Constants.DOWNLOAD_FILENAME;

public class MutilRequestWeb
{
    private final static Logger logger = LoggerFactory.getLogger("MutilRequest");

    public static void main(String[] args)
    {
        Map<Integer, String> hosts = HostHolder.getInstance().getHostParameters();
        Map<String, DownloadType> types = DownloadTypeHolder.getInstance().getTypeParameters();

        ExecutorService scanService = Executors.newFixedThreadPool(3);
        types.entrySet().stream().forEach((t) -> {
            if (null == t.getValue() || (null == t.getValue().getLatestUrl() && null == t.getValue().getOriginalUrl()))
            {
                return;
            }
            scanService.submit(() -> {
                Thread.currentThread().setName("Scan-web-" + t.getValue().getType());
                String original = t.getValue().getOriginalUrl();
                String latest = t.getValue().getLatestUrl();
                if (null == original && null == latest)
                {
                    return;
                }
                String url = null == latest || latest.equals(original) ? original : latest;
                logger.info("select url = " + url);
                while (true)
                {
                    for (Map.Entry<Integer, String> entryHost : hosts.entrySet())
                    {
                        ScanTask scanTask = new ScanTask(t.getValue().getType());
                        String nextUrl = scanTask.executeScanWeb(hosts, entryHost.getValue(), url, false);
                        if (null == nextUrl)
                        {
                            continue;
                        }
                        else
                        {
                            url = nextUrl;
                            DownloadTypeOption.updateLatestUrlByType(t.getValue().getType(), url);
                            break;
                        }
                    }
                    try
                    {
                        TimeUnit.MILLISECONDS.sleep(5000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        });

        ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactory()
        {
            private AtomicInteger integer = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "Mutil-request-web-" + integer.getAndIncrement());
            }
        });

        /*executorService.submit(new DataConsumer(hosts));
        executorService.submit(new DataConsumer(hosts));*/
        executorService.submit(new DataProducer());
        executorService.submit(new ProblemChecker());
        new Thread(new DataConsumer(hosts)).start();
        new Thread(new DataConsumer(hosts)).start();
    }
}
