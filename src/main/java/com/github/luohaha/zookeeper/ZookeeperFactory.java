package com.github.luohaha.zookeeper;

import org.apache.zookeeper.Watcher.Event.*;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class ZookeeperFactory {

    private static ZooKeeper zooKeeper;
    private static CountDownLatch sem = new CountDownLatch(1);
    private Logger logger = Logger.getLogger("logger");
    private String addr;
    private int sessionTimeout;

    public static ZookeeperFactory create() {
        return new ZookeeperFactory();
    }

    public ZookeeperFactory build(String addr, int sessionTimeout) {
        this.addr = addr;
        this.sessionTimeout = sessionTimeout;
        if (zooKeeper == null) {
            synchronized (this) {
                try {
                    zooKeeper = new ZooKeeper(this.addr, this.sessionTimeout, (event) -> {
                        if (event.getState() == KeeperState.SyncConnected) {
                            logger.info("zookeeper会话创建成功");
                            sem.countDown();
                        }
                    });
                    try {
                        sem.await();
                    } catch (InterruptedException e) {
                        logger.warning(e.toString());
                    }
                } catch (IOException e) {
                    logger.warning(e.toString());
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e1) {
                        logger.warning(e1.toString());
                    }
                }
            }
            if (zooKeeper == null) {
                // 说明网络异常，zookeeper会话创建失败，需要重试
                return build(this.addr, this.sessionTimeout);
            }
        }
        return this;
    }

    /**
     * 获取会话
     * @param rebuildSession
     * @param addr
     * @param sessionTimeout
     * @return
     */
    public synchronized ZooKeeper getSession(boolean rebuildSession, String addr, int sessionTimeout) {
        if (rebuildSession) {
            // 需要重建zookeeper连接
            zooKeeper = null;
            build(addr, sessionTimeout);
        }
        return zooKeeper;
    }

    /**
     * 获取会话
     * @param rebuildSession
     * @return
     */
    public ZooKeeper getSession(boolean rebuildSession) {
        return getSession(rebuildSession, this.addr, this.sessionTimeout);
    }
}
