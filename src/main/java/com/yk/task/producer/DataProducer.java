package com.yk.task.producer;

import com.yk.task.datacenter.DataCenter;

import java.util.concurrent.TimeUnit;

public class DataProducer implements Runnable
{
    @Override
    public void run()
    {
        while (true)
        {
            DataCenter.producer();
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
