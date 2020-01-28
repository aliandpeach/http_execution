package com.yk.task.consumer;

import com.yk.task.datacenter.DataCenter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DataConsumer implements Runnable
{
    private Map<Integer, String> hosts;

    public DataConsumer(Map<Integer, String> hosts)
    {
        this.hosts = hosts;
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                DataCenter.consumer(hosts);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            try
            {
                TimeUnit.MILLISECONDS.sleep(10000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
