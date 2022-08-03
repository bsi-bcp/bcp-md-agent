package com.bsi.utils;

import com.bsi.md.agent.datasource.AgDatasourceContainer;
import com.bsi.md.agent.datasource.AgSapRFCTemplate;

import java.util.Map;

/**
 * sapRFC工具类
 * @author fish
 */
public class SapRFCUtils {
    /**
     * 执行sapRFC查询函数
     * @param functionName 函数名称
     * @param params 参数
     * @param dataSourceId 数据源id
     * @return Object 执行结果
     */
    public static Object executeQuery(String functionName, Map<String,Object> params, String dataSourceId){
        AgSapRFCTemplate template = AgDatasourceContainer.getSapRfcDataSource(dataSourceId);
        return template.executeQuery(functionName,params);
    }

}
