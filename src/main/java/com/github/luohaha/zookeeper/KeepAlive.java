package com.github.luohaha.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.*;

import java.util.List;
import java.util.logging.Logger;

public class KeepAlive implements IKeepAlive {

    private ZookeeperFactory zkFactory;
    private Logger logger = Logger.getLogger("logger");

    public KeepAlive(ZookeeperFactory zkFactory) {
        this.zkFactory = zkFactory;
    }

    @Override
    public boolean keepAlive(String addr, String dir, byte[] msg, IKeepAliveCb cb) {
        do {
            // 使用while循环确保创建成功
            try {
                String path = zkFactory.getSession(false)
                        .create(addr, msg, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                // 成功，退出循环
                break;
            } catch (KeeperException.NodeExistsException e) {
                // 已经存在, 退出循环
                break;
            } catch (KeeperException.ConnectionLossException e) {
                // 网络出现抖动，需要重试
                logger.warning(e.toString());
            } catch (KeeperException.SessionExpiredException e) {
                // 会话异常，重新建立连接
                logger.warning(e.toString());
                zkFactory.getSession(true);
            } catch (KeeperException e) {
                // 其他异常，返回错误
                logger.warning(e.toString());
                return false;
            } catch (InterruptedException e) {
                logger.warning(e.toString());
            }
        } while (true);
        // 成功创建，注册监听
        do {
            try {
                List<String> childrenList = zkFactory.getSession(false)
                        .getChildren(dir, new KeepAliveWacher(cb, dir));
                cb.memberChange(childrenList);
                break;
            } catch (KeeperException.NoNodeException e) {
                // 节点不存在
                logger.warning(e.toString());
                return false;
            } catch (KeeperException.ConnectionLossException e) {
                // 网络出现抖动，需要重试
                logger.warning(e.toString());
            } catch (KeeperException.SessionExpiredException e) {
                // 会话异常，重新建立连接
                logger.warning(e.toString());
                zkFactory.getSession(true);
            } catch (KeeperException e) {
                logger.warning(e.toString());
                return false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (true);
        // 成功
        return true;
    }

    class KeepAliveWacher implements Watcher {

        private IKeepAliveCb cb;
        private String dir;

        public KeepAliveWacher(IKeepAliveCb cb, String dir) {
            this.cb = cb;
            this.dir = dir;
        }

        @Override
        public void process(WatchedEvent watchedEvent) {
            do {
                try {
                    cb.memberChange(zkFactory.getSession(false)
                            .getChildren(dir, this));
                    break;
                } catch (KeeperException.NoNodeException e) {
                    // 节点不存在
                    logger.warning(e.toString());
                    break;
                } catch (KeeperException.ConnectionLossException e) {
                    // 网络出现抖动，需要重试
                    logger.warning(e.toString());
                } catch (KeeperException.SessionExpiredException e) {
                    // 会话异常，重新建立连接
                    logger.warning(e.toString());
                    zkFactory.getSession(true);
                } catch (KeeperException e) {
                    logger.warning(e.toString());
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }
}
