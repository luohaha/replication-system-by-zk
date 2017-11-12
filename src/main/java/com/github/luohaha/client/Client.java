package com.github.luohaha.client;

import com.github.luohaha.define.Configure;
import com.github.luohaha.rpc.RpcClient;
import com.github.luohaha.zookeeper.HandleData;
import com.github.luohaha.zookeeper.ZookeeperFactory;

import java.io.IOException;

public class Client {

    private RpcClient rpcClient;
    private ZookeeperFactory zookeeperFactory;
    private Configure configure;
    private HandleData handleData;

    public Client(String conf) throws IOException {
        this.configure = new Configure(conf);
        this.rpcClient = new RpcClient();
        this.rpcClient.start();
        this.zookeeperFactory = new ZookeeperFactory();
        this.zookeeperFactory.build(this.configure.getZkAddr(), this.configure.getZkTimeout());
        this.handleData = new HandleData(this.zookeeperFactory);
    }

    public void put(String key, String value) {
        byte[] res = handleData.getData("/lock");
        if (res == null) {
            System.out.println("No Master");
        } else {
            String[] strs = new String(res).split(":");
            this.rpcClient.remote(strs[0], Integer.valueOf(strs[1]))
                    .call("put", key, value);
        }
    }

    public String get(String key) {
        byte[] res = handleData.getData("/lock");
        if (res == null) {
            System.out.println("No Master");
            return null;
        } else {
            String[] strs = new String(res).split(":");
            return (String) this.rpcClient.remote(strs[0], Integer.valueOf(strs[1]))
                                    .call("get", key);
        }
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client("./properties/config.properties");
        client.put("name", "haha");
        client.put("age", "43");
        System.out.println("name : " + client.get("name"));
        System.out.println("age : " + client.get("age"));
    }
 }
