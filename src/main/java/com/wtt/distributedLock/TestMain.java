package com.wtt.distributedLock;

import com.wtt.distributedConf.ZkConnUtil;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestMain {

    ZooKeeper zk;

    @Before
    public void initConn(){
        try {
            zk = ZkConnUtil.getZkConn();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test01(){
        //起10个线程代表10个不同机器上Client
        for (int i = 0; i < 10; i++) {
            new Thread(){
                @Override
                public void run() {
                    ZkCallbackImp callbackImp = new ZkCallbackImp();
                    callbackImp.setZk(zk);
                    callbackImp.setThreadName(Thread.currentThread().getName());
                    //获取锁
                    callbackImp.tryLock();
                    //干活
                    System.out.println(Thread.currentThread().getName() + " 干活。。。");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //释放锁
                    callbackImp.unLock();

                }
            }.start();
        }
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @After
    public void destory(){
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
