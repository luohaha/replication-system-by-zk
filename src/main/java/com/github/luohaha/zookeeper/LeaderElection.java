package com.github.luohaha.zookeeper;

import org.apache.zookeeper.*;
import sun.rmi.runtime.Log;

import java.util.logging.Logger;

public class LeaderElection implements ILeaderElection {

    private ZookeeperFactory zkFactory;
    private Logger logger = Logger.getLogger("logger");

    public LeaderElection(ZookeeperFactory zkFactory) {
        this.zkFactory = zkFactory;
    }

    public boolean tryLock(String lockAddress, byte[] msg) {
        do {
            // 使用while循环确保获取锁成功
            try {
                String path = zkFactory.getSession(false)
                        .create(lockAddress, msg, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                // 成功获取锁，退出循环
                break;
            } catch (KeeperException.NodeExistsException e) {
                // 已经存在, 注册监听
                boolean isFinish = false;
                do {
                    try {
                        zkFactory.getSession(false)
                                .exists(lockAddress, (event) -> {
                                    if (event.getType() == Watcher.Event.EventType.NodeDeleted) {
                                        if (!tryLock(lockAddress, msg)) {
                                            logger.severe("try lock : " + msg);
                                        }
                                    }
                                });
                        isFinish = true;
                        break;
                    } catch (KeeperException.NoNodeException e1) {
                        // 节点不存在，需要再次加锁
                        logger.warning(e1.toString());
                        break;
                    } catch (KeeperException.ConnectionLossException e1) {
                        // 网络出现抖动，需要重试
                        logger.warning(e1.toString());
                    } catch (KeeperException.SessionExpiredException e1) {
                        // 会话异常，需要重建会话
                        logger.warning(e1.toString());
                        zkFactory.getSession(true);
                    } catch (KeeperException e1) {
                        // 其他异常，返回错误
                        logger.warning(e1.toString());
                        return false;
                    } catch (InterruptedException e1) {
                        logger.warning(e1.toString());
                    }
                } while(true);
                // 确认监听完成，退出
                if (isFinish) {
                    // 获取锁失败，同样退出循环
                    break;
                }
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
        // 成功
        return true;
    }
}
