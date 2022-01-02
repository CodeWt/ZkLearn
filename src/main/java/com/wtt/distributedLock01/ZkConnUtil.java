package com.wtt.distributedLock01;

import com.wtt.distributedConf01.DefaultWatcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZkConnUtil {
    public ZooKeeper getZkCli(){
        ZooKeeper zk = null;
        CountDownLatch latch = new CountDownLatch(1);
        try {
            zk = new ZooKeeper("192.168.133.128:2181,192.168.133.129:2181,192.168.133.130:2181/test",
                    3 * 1000,
                    new DefaultWatcher(latch));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zk;
    }
}
