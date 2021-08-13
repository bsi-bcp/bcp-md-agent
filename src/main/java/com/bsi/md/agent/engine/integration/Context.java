package com.bsi.md.agent.engine.integration;

import java.util.Map;

/**
 * 上下文
 * @author fish
 */
public class Context {

    public Context(){

    }
    private Map map = null;

    public void setMap(Map map){
        this.map = map;
    }

    public Map getMap(){
        return this.map;
    }

    public void put(String key,Object value){
        map.put(key,value);
    }

    public Object get(String key){
        return map.get(key);
    }
}
