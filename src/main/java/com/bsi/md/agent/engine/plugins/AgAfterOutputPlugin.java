package com.bsi.md.agent.engine.plugins;

import com.bsi.md.agent.engine.integration.Context;

/**
 * 输出之后调用的插件
 */
public interface AgAfterOutputPlugin {
    public void handlerMsg(Context context);
}
