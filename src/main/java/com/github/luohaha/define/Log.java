package com.github.luohaha.define;

import java.io.Serializable;

public class Log implements Serializable {
    private int logId;
    private String op;
    private String key;
    private String value;

    public Log(int logId, String op, String key, String value) {
        this.logId = logId;
        this.op = op;
        this.key = key;
        this.value = value;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
