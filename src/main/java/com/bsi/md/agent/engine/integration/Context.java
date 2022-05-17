package com.bsi.md.agent.engine.integration;

import com.bsi.md.agent.constant.AgConstant;
import com.bsi.md.agent.log.AgTaskLog;

import java.util.Map;

/**
 * 上下文
 * @author fish
 */
public class Context {

    public Context(){

    }
    private Map env = null;

    public void setEnv(Map map){
        this.env = map;
    }

    public Map getEnv(){
        return this.env;
    }

    public void put(String key,Object value){
        env.put(key,value);
    }

    /**
     * 全局参数
     * @return
     */
    public Object getParams(){
        return env.get("config");
    }

    /**
     * input参数
     * @return
     */
    public Object inputConf(){
        return env.get("inputConfig");
    }

    /**
     * transform参数
     * @return
     */
    public Object transformConf(){
        return env.get("transformConfig");
    }

    /**
     * output参数
     * @return
     */
    public Object outputConf(){
        return env.get("outputConfig");
    }

    public Object getData(){
        return env.get(AgConstant.AG_DATA);
    }

    public AgTaskLog getResultLog(){
        return (AgTaskLog) env.get("taskInfoLog");
    }
    public Object get(String key){
        return env.get(key);
    }
    public String getTaskId(){
        return (String) env.get("ctx_task_id");
    }

    /**
     * 读取任务错误数据
     * @return
     */
    public String getTaskErrorData(){
        return (String) env.get("ctx_task_error_data");
    }
    /**
     * 读取告警配置
     * @return
     */
    public String getWarnMethodId(){
        return (String) env.get("ctx_warn_method_id");
    }

    /**
     * 设置任务错误数据
     * @param data
     */
    public void setErrorData(String data){
        env.put("ctx_task_error_data",data);
    }
}
