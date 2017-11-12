package com.github.luohaha.core;

import com.github.luohaha.define.Configure;
import com.github.luohaha.rpc.Get;
import com.github.luohaha.rpc.HandleLog;
import com.github.luohaha.rpc.Put;
import com.github.luohaha.rpc.RpcClient;
import com.github.luohaha.rpc.RpcServer;
import com.github.luohaha.zookeeper.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server {

    private ReadWriteLock rwLockForMembers = new ReentrantReadWriteLock();
    private List<String> members = new ArrayList<>();
    private ZookeeperFactory zookeeperFactory;
    private Storage storage;
    private StateMachine stateMachine;
    private RpcServer rpcServer;
    private RpcClient rpcClient;
    private IHandleData handleData;
    private String myAddr;
    // zookeeper 目录和文件
    private String lockAddr = "/lock";
    private String memberDir = "/nodes";
    private String logIdAddr = "/logid";

    public Server(String zkAddr, int timeout, String addr, int port, String logFileAddr) throws IOException {
        init(zkAddr, timeout, addr, port, logFileAddr);
    }

    public Server(String propertiesFile) throws IOException {
        Configure configure = new Configure(propertiesFile);
        init(configure.getZkAddr(), configure.getZkTimeout(),
                configure.getHostAddr(), configure.getPort(), configure.getLogFileAddr());
    }

    /**
     * 初始化服务器
     * @param zkAddr
     * @param timeout
     * @param addr
     * @param port
     * @param logFileAddr
     * @throws IOException
     */
    private void init(String zkAddr, int timeout, String addr, int port, String logFileAddr) throws IOException {
        this.zookeeperFactory = new ZookeeperFactory();
        this.zookeeperFactory.build(zkAddr, timeout);
        this.storage = new Storage(logFileAddr);
        this.stateMachine = new StateMachine(this.storage);
        this.rpcServer = new RpcServer(addr, port);
        this.rpcClient = new RpcClient();
        this.rpcClient.start();
        this.handleData = new HandleData(zookeeperFactory);
        this.myAddr = addr + ":" + port;
    }

    public void startRpcServer() {
        rpcServer.add("handleLog", new HandleLog(stateMachine));
        rpcServer.add("put", new Put(this));
        rpcServer.add("get", new Get(stateMachine));
        rpcServer.start();
    }

    public void keepAlive() {
        IKeepAlive keepAlive = new KeepAlive(this.zookeeperFactory);
        boolean res = false;
        do {
            res = keepAlive.keepAlive(this.memberDir + "/" + this.myAddr, this.memberDir, myAddr.getBytes(), (addrs) -> {
                rwLockForMembers.writeLock().lock();
                members.clear();
                members.addAll(addrs);
                rwLockForMembers.writeLock().unlock();
            });
            if (!res) {
                System.out.println("keep alive fail");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while (!res);
    }

    public void leaderElection() {
        ILeaderElection leaderElection = new LeaderElection(zookeeperFactory);
        boolean res = false;
        do {
            res = leaderElection.tryLock(this.lockAddr, this.myAddr.getBytes());
            if (!res) {
                System.out.println("leader election fail");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while (!res);
    }

    /**
     * 写log的id
     * @param logId
     * @return
     */
    public boolean writeLogId(int logId) {
        return this.handleData.putData(this.logIdAddr, String.valueOf(logId).getBytes());
    }

    /**
     * 获取log的id
     * @return
     */
    public int getLogId() {
        return Integer.valueOf(new String(this.handleData.getData(this.logIdAddr)));
    }

    public List<String> getMembers() {
        return members;
    }

    public StateMachine getStateMachine() {
        return stateMachine;
    }

    public RpcClient getRpcClient() {
        return rpcClient;
    }

    public String getMyAddr() {
        return myAddr;
    }

    public ReadWriteLock getRwLockForMembers() {
        return rwLockForMembers;
    }
}
