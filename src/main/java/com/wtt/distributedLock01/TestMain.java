package com.wtt.distributedLock01;

import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

public class TestMain {

    @Test
    public void test(){
        for (int i = 0; i < 10; i++){
            new Thread(() ->{
                ZooKeeper zk = new ZkConnUtil().getZkCli();
                ZkCallback lock = new ZkCallback(zk,Thread.currentThread().getName());
                //获得锁
                lock.tryLock();
                //干活，业务代码
                System.out.println(Thread.currentThread().getName() + "=============> 开始干活了。。。");
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println(Thread.currentThread().getName() + "=============> over !!! ");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                //释放锁
                lock.unLock(zk);
            },"t" + i).start();
        }
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
