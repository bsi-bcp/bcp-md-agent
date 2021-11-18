package com.bsi.md.agent.utils;

import com.alibaba.fastjson.JSONObject;
import com.bsi.md.agent.datasource.AgApiTemplate;
import com.bsi.md.agent.datasource.AgDatasourceContainer;
import com.bsi.md.agent.entity.vo.AgIntegrationConfigVo;

/**
 * 配置工具类
 */
public class AgConfigUtils {

    public static void rebuildNode(AgIntegrationConfigVo config){
        JSONObject in = config.getInputNode();
        JSONObject out = config.getOutputNode();
        JSONObject transform = config.getTransformNode();
        //删除无用配置
        in.remove("scriptContent");
        out.remove("scriptContent");
        transform.remove("scriptContent");
        //处理路径问题
        setRealpath(in);
        setRealpath(out);
    }

    public static void setRealpath(JSONObject obj){
        AgApiTemplate a = AgDatasourceContainer.getApiDataSource(obj.getString("dataSource"));
        if(a!=null){
            obj.put("path",a.getApiUrl()+obj.getString("path"));
            obj.put("host",a.getApiUrl());
        }
    }
}
