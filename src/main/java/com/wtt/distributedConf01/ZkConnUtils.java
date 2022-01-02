package com.wtt.distributedConf01;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZkConnUtils {
    static CountDownLatch countDownLatch = new CountDownLatch(1);
    public static final String connStr = "192.168.133.128:2181,192.168.133.129:2181,192.168.133.130:2181/testConf";
    public static ZooKeeper getZkConn() throws InterruptedException {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(connStr, 3 * 1000, new DefaultWatcher(countDownLatch));
        } catch (IOException e) {
            e.printStackTrace();
        }
        countDownLatch.await();
        return zk;
    }
}
