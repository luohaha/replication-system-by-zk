package com.github.luohaha.core;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server("./properties/config.properties");
        server.keepAlive();
        server.leaderElection();
        server.startRpcServer();
    }
}
