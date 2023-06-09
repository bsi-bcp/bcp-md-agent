package com.bsi.md.agent.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AgExecutorService {

    private static ExecutorService executor = Executors.newFixedThreadPool(3);

    public static ExecutorService getExecutor(){
        return executor;
    }

}
