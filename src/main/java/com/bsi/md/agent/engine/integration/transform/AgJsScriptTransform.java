package com.bsi.md.agent.engine.integration.transform;

import com.bsi.framework.core.utils.ExceptionUtils;
import com.bsi.md.agent.engine.integration.Context;
import com.bsi.md.agent.engine.script.AgJavaScriptEngine;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * js脚本转换
 * @author fish
 */
public class AgJsScriptTransform implements AgTransform{
    private static Logger info_log = LoggerFactory.getLogger("TASK_INFO_LOG");
    //脚本
    protected String script;
    /**
     * 数据转换
     * @param context
     * @return
     */
    public Object transform(Context context) throws Exception{
        Object result = null;
        try {
            result = new AgJavaScriptEngine().execute(script,"transform",new Object[]{context,context.getData()});
        }catch (Exception e){
            info_log.error("转换数据报错:{}", ExceptionUtils.getFullStackTrace(e));
            throw e;
        }
        return result;
    }

    @Override
    public String setScript(String script) {
        return this.script = script;
    }
}
