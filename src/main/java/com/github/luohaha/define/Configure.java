package com.github.luohaha.define;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Configure {
    private String zkAddr;
    private int zkTimeout;
    private String hostAddr;
    private int port;
    private String logFileAddr;

    public Configure(String file) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(file));
        this.zkAddr = properties.getProperty("zookeeperAddress");
        this.zkTimeout = Integer.valueOf(properties.getProperty("zookeeperSessionTimeout"));
        this.hostAddr = properties.getProperty("host");
        this.port = Integer.valueOf(properties.getProperty("port"));
        this.logFileAddr = properties.getProperty("logFile");
    }

    public String getZkAddr() {
        return zkAddr;
    }

    public void setZkAddr(String zkAddr) {
        this.zkAddr = zkAddr;
    }

    public int getZkTimeout() {
        return zkTimeout;
    }

    public void setZkTimeout(int zkTimeout) {
        this.zkTimeout = zkTimeout;
    }

    public String getHostAddr() {
        return hostAddr;
    }

    public void setHostAddr(String hostAddr) {
        this.hostAddr = hostAddr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLogFileAddr() {
        return logFileAddr;
    }

    public void setLogFileAddr(String logFileAddr) {
        this.logFileAddr = logFileAddr;
    }
}
