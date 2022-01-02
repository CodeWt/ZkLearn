package com.wtt.distributedConf01;

import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestMain {

    ZooKeeper zk;
    MyZkCallback cb;
    Conf conf;

    @Before
    public void b() throws InterruptedException {
        zk = ZkConnUtils.getZkConn();
        conf = new Conf();
        cb = new MyZkCallback(zk,conf);
    }

    @Test
    public void testZk(){
        cb.start();
        while (true){
            if (!(conf.getUrl()==null||"".equals(conf.getUrl())))
                System.out.println(conf.getUrl());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @After
    public void a() throws InterruptedException {
        if (zk!=null){
            zk.close();
        }
    }
}
