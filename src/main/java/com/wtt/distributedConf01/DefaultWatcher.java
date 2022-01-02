package com.wtt.distributedConf01;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

public class DefaultWatcher implements Watcher {

    CountDownLatch countDownLatch;
    public DefaultWatcher(CountDownLatch countDownLatch){
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                System.out.println(this.getClass().getSimpleName() + " NodeCreated");
                break;
            case NodeDeleted:
                System.out.println(this.getClass().getSimpleName() + " NodeDeleted");
                break;
            case NodeDataChanged:
                System.out.println(this.getClass().getSimpleName() + " NodeDataChanged");
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
        switch (event.getState()) {
            case Unknown:
                break;
            case Disconnected:
                System.out.println(this.getClass().getSimpleName() + " disconnected..");
                break;
            case NoSyncConnected:
                break;
            case SyncConnected:
                System.out.println(this.getClass().getSimpleName() + " connected.");
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
    }
}
