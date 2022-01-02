package com.wtt.distributedConf01;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public class MyZkCallback implements AsyncCallback.StatCallback,Watcher, AsyncCallback.DataCallback , AsyncCallback.StringCallback {
    CountDownLatch latch ;
    ZooKeeper zk;
    Conf conf;
    public MyZkCallback(ZooKeeper zk,Conf conf){
        this.zk=zk;
        this.conf = conf;
    }
    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        //如果节点存在则获取，否则创建一个
        if (stat!=null){
            zk.getData(path, this,this,"getData");
        }else {
            zk.create(path,"buy00002".getBytes(StandardCharsets.UTF_8),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,this,"createZnode");
        }
    }

    @Override
    public void process(WatchedEvent event) {
        //处理get后的监控事件
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                System.out.println("节点丢了。。。");
                zk.create(event.getPath(),"after node loss, recreate node data".getBytes(StandardCharsets.UTF_8),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL,this,"reCreateNode");
                break;
            case NodeDataChanged:
                zk.getData(event.getPath(),this,this,"watchNodeCreated");
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
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        //获取数据并更新配置
        conf.setUrl(new String(data));
    }

    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        if (path.equals(name)){
            System.out.println("创建成功");
            zk.getData(path,this,this,"AftercreateZnodeGetdata");
        }
    }
    public void start()  {
        //判断节点是否存在
        zk.exists("/buy01", false, this,"exists");
    }

}
