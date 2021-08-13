package com.bsi.md.agent.engine.script;

/**
 * 脚本引擎接口
 * @author fish
 */
public interface AgScriptEngine {
    /**
     * 执行方法
     * @param script
     * @return
     */
    Object execute(String script,String method) throws Exception;
}
