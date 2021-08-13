package com.bsi.md.agent.engine.integration.output;

import com.bsi.md.agent.engine.integration.Context;

/**
 * 输出接口
 * @author fish
 */
public interface AgOutput {
    /**
     * 写入数据
     * @param context
     * @return
     */
    Object write(Context context);
}
