package com.example.simple_rpc.test;

import com.example.simple_rpc.protocol.Protocol;
import com.example.simple_rpc.protocol.RpcProtocol;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Client {
    private static AtomicLong callAmount = new AtomicLong(0L);
    public static final String input = new String(new byte[1000]);


    public static void main(String[] args) throws Exception {
        System.setProperty("serveAddr", "113.31.114.202:8848");
        Protocol<HelloService> protocol = new RpcProtocol<>();
        HelloService helloService = protocol.refer(HelloService.class);

        int coreCount = Runtime.getRuntime().availableProcessors() * 2;
        CountDownLatch countDownLatch = new CountDownLatch(coreCount);
        long startTime = System.currentTimeMillis();

        final ExecutorService executor = Executors.newFixedThreadPool(coreCount);
        for (int i = 0; i < coreCount; i++) {
            executor.execute(() -> {
                while (callAmount.get() < 100000) {
                    try {
                        if (StringUtils.isNotBlank(helloService.say(input)))
                            callAmount.incrementAndGet();
                        else
                            continue;
                    } catch (Exception e) {
                        continue;
                    }
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await(300, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            //DO Nothing
        }
        if (callAmount.intValue() < 100000)
            System.out.println("Doesn't finish all invoking.".getBytes());
        else {
            long endTime = System.currentTimeMillis();
            Float tps = (float) callAmount.get() / (float) (endTime - startTime) * 1000F;
            StringBuilder sb = new StringBuilder();
            sb.append("tps:").append(tps).append(",call:").append(callAmount.get());
            System.out.println(sb);
        }
        System.exit(1);

    }

}
