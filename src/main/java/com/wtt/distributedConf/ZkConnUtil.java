package com.wtt.distributedConf;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZkConnUtil {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private static ZooKeeper zk;
    private static Watcher watcher =new DefaultWatch(countDownLatch);
    public static ZooKeeper getZkConn() throws IOException, InterruptedException {
        zk = new ZooKeeper("192.168.133.128:2181,192.168.133.129:2181,192.168.133.130:2181/wtt",1000*60*3,watcher);
        countDownLatch.await();
        return zk;
    }

}
