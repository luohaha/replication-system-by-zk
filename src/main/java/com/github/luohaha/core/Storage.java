package com.github.luohaha.core;

import com.github.luohaha.define.Log;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Storage {
    private String fileAddr;
    private File file;
    private FileWriter fileWriter;
    private Gson gson;

    public Storage(String fileAddr) throws IOException {
        this.fileAddr = fileAddr;
        this.file = new File(fileAddr);
        if (!this.file.exists()) {
            // 创建文件
            this.file.createNewFile();
        }
        this.fileWriter = new FileWriter(this.file);
        this.gson = new Gson();
    }

    public void write(Log log) throws IOException {
        String logStr = gson.toJson(log);
        this.fileWriter.write(logStr.length() + logStr);
        this.fileWriter.flush();
    }

    public void write(String logStr) throws IOException {
        this.fileWriter.write(logStr.length() + logStr);
        this.fileWriter.flush();
    }

    public void close() throws IOException {
        this.fileWriter.close();
    }
}
