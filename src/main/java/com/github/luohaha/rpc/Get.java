package com.github.luohaha.rpc;

import com.github.luohaha.core.StateMachine;

import java.util.List;

public class Get implements IRpcFunction {

    private StateMachine stateMachine;

    public Get(StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override
    public Object rpcCall(String function, List<Object> params) {
        return this.stateMachine.get((String) params.get(0));
    }
}
