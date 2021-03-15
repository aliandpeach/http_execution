package com.yk.bitcoin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class KeyGeneratorWatchedService
{
    public static void main(String[] args)
    {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(10, new ThreadFactory()
        {
            private AtomicInteger integer = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "key-generator-" + integer.getAndIncrement());
            }
        });

        service.scheduleAtFixedRate(new KeyGeneratorRunner(), 0, 5, TimeUnit.SECONDS);

        ScheduledExecutorService watched = Executors.newScheduledThreadPool(10, new ThreadFactory()
        {
            private AtomicInteger integer = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "key-watched-" + integer.getAndIncrement());
            }
        });

        watched.scheduleAtFixedRate(new KeyWatchedRunner(), 0, 5, TimeUnit.SECONDS);
    }
}
