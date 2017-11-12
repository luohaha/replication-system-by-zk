package com.github.luohaha.core;

import com.github.luohaha.define.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StateMachine {
    private Map<String, String> kv;
    private Storage storage;

    public StateMachine(Storage storage) {
        this.kv = new HashMap<>();
        this.storage = storage;
    }

    public String handleLog(Log log) {
        String res = "";
        switch (log.getOp()) {
            case "put":
                put(log.getKey(), log.getValue());
                break;
            case "remove":
                remove(log.getKey());
                break;
            case "get":
                res = get(log.getKey());
                break;
            default:
                System.out.println("error log");
        }
        // 写入磁盘
        writeLogToDisk(log);
        return res;
    }

    public void put(String key, String value) {
        this.kv.put(key, value);
    }

    public void remove(String key) {
        this.kv.remove(key);
    }

    public String get(String key) {
        return this.kv.get(key);
    }

    /**
     * 日志写入磁盘
     * @param log
     */
    private void writeLogToDisk(Log log) {
        try {
            this.storage.write(log);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
