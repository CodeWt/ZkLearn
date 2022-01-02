package com.wtt;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, KeeperException, InterruptedException {
        //zk 是有session概念的， 没有连接池的概念
        //watcher 观察 回调   watch只注册在read exist 事件
        //第一类 : new zk时候 传入的watcher是session级别的，和path及node无关
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final ZooKeeper zk = new ZooKeeper("192.168.133.128:2181,192.168.133.129:2181,192.168.133.130:2181",
                1000*60*3,//sessiontimeout决定零时节点的存活周期
                new Watcher() {
            //watcher回调方法
            @Override
            public void process(WatchedEvent event) {
                Event.EventType type = event.getType();
                Event.KeeperState state = event.getState();
                System.out.println("new zk watch : " + event.toString());
                switch (state) {
                    case Unknown:
                        break;
                    case Disconnected:
                        System.out.println("断开连接。。。");
                        break;
                    case NoSyncConnected:
                        break;
                    case SyncConnected:
                        System.out.println("connected 。。。");
                        countDownLatch.countDown();
                        break;
                    case AuthFailed:
                        break;
                    case ConnectedReadOnly:
                        break;
                    case SaslAuthenticated:
                        break;
                    case Expired:
                        break;
                    case Closed:
                        break;
                }
                switch (type) {
                    case None:
                        break;
                    case NodeCreated:
                        break;
                    case NodeDeleted:
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
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final ZooKeeper.States state = zk.getState();
        switch (state) {
            case CONNECTING:
                System.out.println("conn ing ...");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("conn ed ...");
                countDownLatch.countDown();
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }
        final String node = zk.create("/ooxx", "olddata".getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        final Stat stat = new Stat();
        /*byte[] data = zk.getData("/ooxx", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                Event.EventType type = event.getType();
                Event.KeeperState state1 = event.getState();
                System.out.println("zk.getData watch callback : " + type + "\t" + state1);
                try {
                    //true为默认new zk回调
//                    zk.getData("/ooxx", true, stat);
                    //重新调用自己的回调
                    zk.getData("/ooxx",this,stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, stat);
        System.out.println("data : " + new String(data));
        //触发上边回调
        Stat stat1 = zk.setData("/ooxx", "newdata".getBytes(StandardCharsets.UTF_8), 0);
        //不会继续触发的。。。
        zk.setData("/ooxx","newdata01".getBytes(StandardCharsets.UTF_8),stat1.getVersion());*/

        /**
         * react 异步版本
         */
        zk.create("/ooxx", "olddata".getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, new AsyncCallback.StringCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, String name) {
                System.out.println("rc: " + rc + "  path :" + path + "  ctx: " + ctx.toString() + " name: " + name);
                zk.getData("/ooxx", new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        System.out.println("asyn call get watcher : " + event.toString());
                        //继续注册回调本身
                        try {
                            zk.getData("/ooxx",this,stat);
                        } catch (KeeperException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, new DataCallback() {
                    @Override
                    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                        System.out.println("getData is :" + new String(data));
                        zk.setData("/ooxx", "newdata".getBytes(StandardCharsets.UTF_8), stat.getVersion(), new StatCallback() {
                            @Override
                            public void processResult(int rc, String path, Object ctx, Stat stat) {
                                System.out.println("first setstat : " + stat);
                                try {
                                    zk.setData(path,"sec set new data".getBytes(StandardCharsets.UTF_8),stat.getVersion());
                                } catch (KeeperException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        },stat);
                    }
                },stat);
            }
        },stat);

        Thread.sleep(1000 * 60 * 30);
    }
}
