package com.github.luohaha.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

import java.util.logging.Logger;

public class HandleData implements IHandleData {

    private ZookeeperFactory zkFactory;
    private Logger logger = Logger.getLogger("logger");

    public HandleData(ZookeeperFactory zkFactory) {
        this.zkFactory = zkFactory;
    }

    @Override
    public byte[] getData(String addr) {
        try {
            byte[] data = zkFactory.getSession(false)
                    .getData(addr, false, new Stat());
            return data;
        } catch (KeeperException e) {
            logger.warning(e.toString());
        } catch (InterruptedException e) {
            logger.warning(e.toString());
        }
        return null;
    }

    @Override
    public boolean putData(String addr, byte[] msg) {
        try {
            zkFactory.getSession(false)
                    .setData(addr, msg, -1);
            return true;
        } catch (KeeperException e) {
            logger.warning(e.toString());
        } catch (InterruptedException e) {
            logger.warning(e.toString());
        }
        return false;
    }


}
