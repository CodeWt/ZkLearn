package com.wtt.distributedLock01;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZkCallback implements AsyncCallback.StringCallback, AsyncCallback.ChildrenCallback,Watcher, AsyncCallback.VoidCallback , AsyncCallback.StatCallback {

    ZooKeeper zk;
    CountDownLatch latch = new CountDownLatch(1);
    String threadName;
    String tmpNode;
    public ZkCallback(ZooKeeper zk,String threadName){
        this.zk = zk;
        this.threadName = threadName;
    }

    public void tryLock(){
        Stat stat = new Stat();
        try {
            zk.create("/lock",threadName.getBytes(StandardCharsets.UTF_8),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,this,"ctx");
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unLock(ZooKeeper zk){
        zk.delete("/test/" + tmpNode,-1,this,"ctx");
        if (zk!=null){
            try {
                zk.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        //创建节点回调
        System.out.println(this.threadName + " create path" + path + "\t name : " + name);
        this.tmpNode = name.substring(1);
//        t5 create path/lock	 name : /lock0000000003
        zk.getChildren("/",false,this,"ctx");
    }

    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children) {
//        for (String child : children){
////               path: /	 child: lock0000000000
////               System.out.println("path: " + path + "\t child: " +  child);
//        }
        Collections.sort(children);
        int i = children.indexOf(tmpNode);
        if (i==0){
            //如果是最小的，则获得锁
            latch.countDown();
        }else {
            zk.exists("/test/" + children.get(i-1),this,this,"ctx");
        }

    }


    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                zk.getChildren("/",false,this,"ctx");
                break;
            case NodeDataChanged:
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

    @Override
    public void processResult(int rc, String path, Object ctx) {
        //删除节点回调
        System.out.println(path + " 被删除成功 ！");
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        //节点是否存在回调,前面节点挂了不存在，重新排序获取锁并监控
        if (stat==null){
            zk.getChildren("/",false,this,"ctx");
        }
    }
}
