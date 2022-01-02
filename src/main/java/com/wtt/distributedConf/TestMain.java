package com.wtt.distributedConf;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.common.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestMain {

    ZooKeeper zk;


    @Before
    public void testbefore() {
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
        final MyConf myConf = new MyConf();
        ZkCallbackImp zkCallbackImp = new ZkCallbackImp();
        zkCallbackImp.setZk(zk);
        zkCallbackImp.setMyConf(myConf);
        zkCallbackImp.await();
        //1.节点存在 2.节点不存在
        while (true){
            if ("".equals(myConf.getConfigContent())){
                System.out.println("节点消失了。。配置清空。。。");
                zkCallbackImp.await();
            }else {
                System.out.println(myConf.getConfigContent());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    @After
    public void testAfter(){
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
