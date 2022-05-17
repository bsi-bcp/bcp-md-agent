package com.bsi.md.agent.engine.integration;

import com.bsi.md.agent.constant.AgConstant;
import com.bsi.md.agent.engine.plugins.AgAfterOutputPluginManager;
import com.bsi.md.agent.engine.plugins.AgTaskErrorDataWarnPlugin;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 集成引擎执行引导类
 * @author fish
 */
public class AgTaskBootStrap {

    private static Logger info_log = LoggerFactory.getLogger("TASK_INFO_LOG");

    private AgIntegrationEngine engine;

    private Context context;

    /**
     * 设置上下文
     * @param context
     * @return
     */
    public AgTaskBootStrap context(Context context){
        this.context = context;
        return this;
    }

    /**
     * 设置集成引擎
     * @param engine
     * @return
     */
    public AgTaskBootStrap engine(AgIntegrationEngine engine){
        this.engine = engine;
        return this;
    }
    /**
     * 初始化 AgTaskBootStrap
     * @return
     */
    public static AgTaskBootStrap custom(){
        return new AgTaskBootStrap();
    }

    /**
     * 执行
     */
    public Object exec() throws Exception{
        info_log.debug("1.执行输入节点");
        String apiFlag = context.get("api-flag")==null?"N":"Y";
        Object obj = engine.input(context);
        context.put(AgConstant.AG_DATA,obj);
        if(obj==null && "N".equals(apiFlag)){
            info_log.info("输入节点未查询到数据，结束任务");
            return null;
        }
        info_log.debug("2.执行转换节点");
        obj = engine.transform(context);
        context.put(AgConstant.AG_DATA,obj);
        info_log.debug("3.执行输出节点");
        Object result = engine.output(context);
        info_log.debug("4.执行输出完成之后的插件");
        AgAfterOutputPluginManager.runPlugins(context);
        info_log.debug("执行完毕");
        return result;
    }
}
