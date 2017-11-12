package com.github.luohaha.zookeeper;

public interface ILeaderElection {
    public boolean tryLock(String lockAddress, byte[] msg);
}
