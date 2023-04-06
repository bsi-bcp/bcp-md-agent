package com.bsi.utils;

import com.bsi.framework.core.utils.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
public class DbfUtils {
    private static Logger info_log = LoggerFactory.getLogger("TASK_INFO_LOG");
    /**
     * 读取指定路径的dbf文件数据
     * @param path 文件路径
     * @return  List<Map<String, Object>>
     * @throws SQLException
     */
    public static List<Map<String, Object>> readDbf(String path,String sql)  {
        List<Map<String, Object>> data = new ArrayList<>();
        String url = "jdbc:dbf:" + path;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName("com.hxtt.sql.dbf.DBFDriver");
            conn = DriverManager.getConnection(url);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getString(i);
                    row.put(columnName, value);
                }
                data.add(row);
            }
        }catch (Exception e){
            info_log.info("读取dbf文件报错,错误信息:{}", ExceptionUtils.getFullStackTrace(e));
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
        return data;
    }
}
