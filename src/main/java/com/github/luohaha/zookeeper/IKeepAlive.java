package com.github.luohaha.zookeeper;

public interface IKeepAlive {
    public boolean keepAlive(String addr, String dir, byte[] msg, IKeepAliveCb cb);
}
