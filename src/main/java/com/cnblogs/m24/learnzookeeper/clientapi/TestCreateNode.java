package com.cnblogs.m24.learnzookeeper.clientapi;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

public class TestCreateNode implements Watcher {
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String args[]) throws Exception {

        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 5000, new TestCreateNode());
        countDownLatch.await();

        // 同步创建临时节点
        String ephemeralPath = zooKeeper.create("/zk-test-create-ephemeral-", "123".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("同步创临时建节点成功：" + ephemeralPath);

        // 同步创建临时顺序节点
        String sequentialPath = zooKeeper.create("/zk-test-create-sequential-", "456".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("同步创建临时顺序节点成功：" + sequentialPath);

        // 异步创建临时节点
        zooKeeper.create("/zk-test-create-async-ephemeral-", "abc".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, new MyStringCallBack(), "msg1");

        // 异步创建临时顺序节点
        zooKeeper.create("/zk-test-create-async-sequential-","def".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,new MyStringCallBack(),"msg2");

        Thread.sleep(10000); // 验证等待回调结果使用，可根据实际情况自行调整
    }

    public void process(WatchedEvent event) {
        if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
            countDownLatch.countDown();
        }
    }

}

class MyStringCallBack implements AsyncCallback.StringCallback{
    public void processResult(int rc, String path, Object ctx, String name) {
        System.out.println("异步创建回调结果：状态：" + rc +"；创建路径：" +
                path + "；传递信息：" + ctx + "；实际节点名称：" + name);
    }
}