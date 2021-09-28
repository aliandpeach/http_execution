package com.yk.task;

import com.yk.host.HostHolder;
import com.yk.latest.DownloadType;
import com.yk.latest.DownloadTypeHolder;
import com.yk.latest.DownloadTypeOption;
import com.yk.task.categories.CategoriesSearch;
import com.yk.task.categories.CategoriesTask;
import com.yk.task.categories.CategoriesType;
import com.yk.task.check.ProblemChecker;
import com.yk.task.consumer.DataConsumer;
import com.yk.task.datacenter.CategoriesCenter;
import com.yk.task.datacenter.DataCenter;
import com.yk.task.producer.DataProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MutilRequestWeb {
    private final static Logger logger = LoggerFactory.getLogger("MutilRequest");

    public static void main(String[] args) {
        Map<Integer, String> hosts = HostHolder.getInstance().getHostParameters();
        Map<String, DownloadType> types = DownloadTypeHolder.getInstance().getTypeParameters();

        ExecutorService scanService = Executors.newFixedThreadPool(3);
        types.entrySet().stream().forEach((t) -> {
            if (null == t.getValue() || (null == t.getValue().getLatestUrl() && null == t.getValue().getOriginalUrl())) {
                return;
            }
            scanService.submit(() -> {
                Thread.currentThread().setName("Scan-web-" + t.getValue().getType());
                String original = t.getValue().getOriginalUrl();
                String latest = t.getValue().getLatestUrl();
                if (null == original && null == latest) {
                    return;
                }
                String url = null == latest || latest.equals(original) ? original : latest;
                logger.info("select url = " + url);
                while (true) {
                    for (Map.Entry<Integer, String> entryHost : hosts.entrySet()) {
                        ScanTask scanTask = new ScanTask(t.getValue().getType());
                        String nextUrl = scanTask.executeScanWeb(hosts, entryHost.getValue(), url, false);
                        if (null == nextUrl) {
                            continue;
                        } else {
                            url = nextUrl;
                            new DownloadTypeOption().updateLatestUrlByType(t.getValue().getType(), url);
                            break;
                        }
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        });

        ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactory() {
            private AtomicInteger integer = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Mutil-request-web-" + integer.getAndIncrement());
            }
        });

        executorService.submit(new DataConsumer(hosts));
        executorService.submit(new DataConsumer(hosts));
        executorService.submit(new DataProducer());
        executorService.submit(new ProblemChecker());
        /*new Thread(new DataConsumer(hosts)).start();
        new Thread(new DataConsumer(hosts)).start();*/

        executorService.submit(() -> {
            while (true) {
                try {
                    CategoriesCenter.consumer(hosts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        executorService.submit(() -> {
            while (true) {
                try {
                    CategoriesCenter.consumer(hosts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        executorService.submit(() -> {
            while (true) {
                try {
                    CategoriesCenter.producer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        CategoriesSearch search = new CategoriesSearch();
        ExecutorService executorCateService = Executors.newFixedThreadPool(20);
        /*executorCateService.submit(() -> {
            search.search();
            List<CategoriesType> all = search.getListCategoriesType();
            all.stream().forEach(t -> {
                executorCateService.submit(() -> {
                    Thread.currentThread().setName("Mutil-categories-web-" + t.getUrl().replaceAll("[\\\\/]", "-"));
                    int latestPage = t.getPage();
                    while (true) {
                        for (Map.Entry<Integer, String> entryHost : hosts.entrySet()) {
                            CategoriesTask categoriesTask = new CategoriesTask(t.getUrl());
                            String url = t.getUrl() + "?mode=async&function=get_block&block_id=list_videos_common_videos_list&sort_by=post_date&from=%d&_=" + Instant.now().toEpochMilli();
                            url = String.format(url, latestPage);
                            System.out.println("async url : " + url);
                            String nextPage = categoriesTask.executeCategoriesWeb(hosts, entryHost.getValue(), url, false, search.getTasksStart());
                            if (null == nextPage) {
                                continue;
                            } else {
                                latestPage = Integer.valueOf(nextPage);
                                DownloadTypeOption.updateLatestPageByType(latestPage, t.getUrl());
                                break;
                            }
                        }
                        try {
                            TimeUnit.MILLISECONDS.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            });
        });*/
    }
}
