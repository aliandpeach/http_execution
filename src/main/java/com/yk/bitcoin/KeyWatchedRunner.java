package com.yk.bitcoin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yk.config.CommonConfig;
import com.yk.httprequest.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class KeyWatchedRunner implements Runnable
{
    private Logger logger = LoggerFactory.getLogger("watched");
    private Logger record = LoggerFactory.getLogger("record");

    @Override
    public void run()
    {
        if (CommonConfig.getInstance().readStatus() && KeyCache.keyQueue.size() == 0)
        {
            logger.info("runner stopped!");
            return;
        }
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        try
        {
            synchronized (KeyCache.lock)
            {
                while (KeyCache.keyQueue.size() <= 0)
                {
                    try
                    {
                        KeyCache.lock.wait();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            Map<String, String> params = new HashMap<>();
            Map<String, String> temp = new HashMap<>();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 50; i++)
            {
                Map<String, String> keyMap = KeyCache.keyQueue.poll();
                if (keyMap == null)
                {
                    break;
                }
                String prikey = keyMap.get("privatekey");
                String pubkey = keyMap.get("publickey");
                sb.append(pubkey).append(",");

                temp.put(pubkey, prikey);
            }
            params.put("active", sb.toString().endsWith(",") ? sb.toString().substring(0, sb.toString().length() - 1) : sb.toString());

            Map<String, String> headers = new HashMap<>();
            headers.put("Connection", "keep-alive");
            Map<String, Map<String, Long>> result = HttpClientUtil.get("https://blockchain.info/balance"
                    , headers, params, new TypeReference<Map<String, Map<String, Long>>>()
                    {
                    }, 3);

            for (Map.Entry<String, Map<String, Long>> entry : result.entrySet())
            {
                if (null == entry)
                {
                    continue;
                }
                String pub = entry.getKey();
                Map<String, Long> values = entry.getValue();
                long balance = values.get("final_balance");
                record.info(temp.get(pub) + ", " + pub);
                if (balance > 0)
                {
                    logger.info("Wallet private key = " + temp.get(pub) + ", balance: " + balance);
                }
            }

            synchronized (KeyCache.lock)
            {
                KeyCache.lock.notifyAll();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
