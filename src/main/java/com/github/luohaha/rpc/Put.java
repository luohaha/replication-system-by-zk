package com.github.luohaha.rpc;

import com.github.luohaha.define.Log;
import com.github.luohaha.core.Server;
import com.github.luohaha.core.StateMachine;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.logging.Logger;

public class Put implements IRpcFunction {

    private Server server;
    private StateMachine stateMachine;
    private RpcClient rpcClient;
    private List<String> members;
    private ReadWriteLock lock;
    private String myAddr;
    private Logger logger = Logger.getLogger("logger");

    public Put(Server server) {
        this.server = server;
        this.stateMachine = server.getStateMachine();
        this.rpcClient = server.getRpcClient();
        this.members = server.getMembers();
        this.myAddr = server.getMyAddr();
        this.lock = server.getRwLockForMembers();
    }
    @Override
    public Object rpcCall(String function, List<Object> params) {
        Log log = new Log(server.getLogId() + 1, "put", (String) params.get(0), (String) params.get(1));
        // 本地执行
        this.stateMachine.handleLog(log);
        // 发往备用
        lock.readLock().lock();
        for (String each : members) {
            if (!each.equals(myAddr)) {
                    String[] strs = each.split(":");
                    this.rpcClient.remote(strs[0], Integer.valueOf(strs[1]))
                            .call("handleLog", log);

            }
        }
        lock.readLock().unlock();
        server.writeLogId(log.getLogId());
        return "";
    }
}
