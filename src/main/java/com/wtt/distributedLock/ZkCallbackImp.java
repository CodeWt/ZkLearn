package com.wtt.distributedLock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZkCallbackImp implements AsyncCallback.StringCallback, Watcher, AsyncCallback.Children2Callback, AsyncCallback.StatCallback {


    ZooKeeper zk;
    String threadName;

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    String pathName;

    CountDownLatch countDownLatch =new CountDownLatch(1);

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public void tryLock() {
        zk.create("/testLock",threadName.getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL,this,"getLock");
        //如果根目录值等于线程名直接跳过执行干活（重入锁）
//        if (zk.getData("/").equals(threadName)){
//            return;
//        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        if (name!=null){
            System.out.println(threadName + " create tmpSequenceNode is : " + name);
            this.setPathName(name);
            zk.getChildren("/",false,this,"getChild");
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        //如果没来得及监控前面的节点就挂掉了
        if (stat==null){
            zk.getChildren("/",false,this,"fsjda");
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
                zk.getChildren("/",false,this,"xxxx");
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
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
//        System.out.println(threadName +   " : " +  path);
//        for (String child : children) {
//            System.out.println(child);
//        }
        Collections.sort(children);
        int i = children.indexOf(pathName.substring(1));
        //获取锁
        if (i==0){
            //最小临时序列node拿到锁
            System.out.println(threadName + " is first getLock ...");
            //获得锁的同事把所得信息写到锁里
            try {
                //重入锁
                zk.setData("/",threadName.getBytes(StandardCharsets.UTF_8),-1);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            countDownLatch.countDown();
        }else {
            //非最小则watch前一个node
            zk.exists(path+children.get(i-1),this,this,"djfsj");
        }
    }

    public void unLock() {
        try {
            System.out.println(threadName + " do over ...");
            zk.delete(pathName,-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
