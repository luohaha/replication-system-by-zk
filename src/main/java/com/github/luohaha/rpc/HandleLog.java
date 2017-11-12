package com.github.luohaha.rpc;

import com.github.luohaha.define.Log;
import com.github.luohaha.core.StateMachine;

import java.util.List;

public class HandleLog implements IRpcFunction {

    private StateMachine stateMachine;

    public HandleLog(StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }
    @Override
    public Object rpcCall(String function, List<Object> params) {
        return this.stateMachine.handleLog((Log) params.get(0));
    }
}
