package com.bsi.md.agent.datasource;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据源存储容器
 * @author fish
 */
public class AgDatasourceContainer {
    private static Map<Long,AgJdbcTemplate> jdbcMap = new HashMap<>();
    private static Map<Long, AgApiTemplate> apiMap = new HashMap<>();

    /**
     * 添加一个jdbc数据源
     * @param key
     * @param template
     */
    public static void addJdbcDataSource(Long key,AgJdbcTemplate template){
        jdbcMap.put(key,template);
    }

    /**
     * 获取jdbc数据源
     * @param key
     * @return
     */
    public static AgJdbcTemplate getJdbcDataSource(Long key){
        return jdbcMap.get(key);
    }

    /**
     * 添加一个api数据源
     * @param key
     * @param template
     */
    public static void addApiDataSource(Long key,AgApiTemplate template){
        apiMap.put(key,template);
    }

    /**
     * 获取api数据源
     * @param key
     * @return
     */
    public static AgApiTemplate getApiDataSource(Long key){
        return apiMap.get(key);
    }
}
