package com.github.luohaha.zookeeper;

public interface IHandleData {
    public byte[] getData(String addr);
    public boolean putData(String addr, byte[] msg);
}
