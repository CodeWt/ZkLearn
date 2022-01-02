package com.wtt.distributedConf;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class ZkCallbackImp implements Watcher, AsyncCallback.DataCallback, AsyncCallback.StatCallback {

    ZooKeeper zk;
    MyConf myConf;
    CountDownLatch countDownLatch =new CountDownLatch(1);

    public MyConf getMyConf() {
        return myConf;
    }

    public void setMyConf(MyConf myConf) {
        this.myConf = myConf;
    }

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        if (stat!=null){
            myConf.setConfigContent(new String(data));
            countDownLatch.countDown();
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        if (stat!=null){
            zk.getData("/ooxx",this,this,stat);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                //创建节点后重新获取
                zk.getData("/ooxx",this,this,event.getState());
                break;
            case NodeDeleted:
                //容忍度
                myConf.setConfigContent("");
                countDownLatch = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                zk.getData("/ooxx",this,this,event.getState());
                break;
            case NodeChildrenChanged:
                break;
            case DataWatchRemoved:
                break;
            case ChildWatchRemoved:
                break;
            case PersistentWatchRemoved:
                break;
        }
    }

    public void await() {
        zk.exists("/ooxx", this, this,"exists");
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
