package com.github.luohaha.zookeeper;

import java.util.List;

public interface IKeepAliveCb {
    public void memberChange(List<String> addrs);
}
