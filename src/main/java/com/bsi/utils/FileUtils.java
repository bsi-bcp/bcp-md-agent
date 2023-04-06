package com.bsi.utils;

import com.bsi.framework.core.utils.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件工具类
 */
@Slf4j
public class FileUtils {

    /**
     * 读取文件内容
     * @param path
     * @return
     */
    public static List<String> readFile(String path){
        List<String> resultLines = null;
        try{
            resultLines = org.apache.commons.io.FileUtils.readLines(new File(path),StandardCharsets.UTF_8);
        }catch (Exception e){
            log.error("读取文件{}报错,错误信息:", ExceptionUtils.getFullStackTrace(e));
        }
        return resultLines;
    }

    /**
     * 读取文件内容
     * @param path
     * @return
     */
    public static boolean writeFile(String path,String msg,boolean append){
        boolean flag = true;
        List<String> resultLines = null;
        try{
            org.apache.commons.io.FileUtils.write(new File(path),msg, StandardCharsets.UTF_8,append);
        }catch (Exception e){
            flag = false;
            log.error("写入文件{}报错,错误信息:", ExceptionUtils.getFullStackTrace(e));
        }
        return flag;
    }

    /**
     * 读取新增或者修改的文件
     * @param path 文件路径
     * @param exts 后缀名
     * @param lastTs 最后更新时间毫秒数
     * @param maxSize 最大大小
     * @return
     */
    public static List<String> getNewOrModifiedFiles(String path, Long lastTs,Long maxSize,String[] exts) {
        File directory  = new File(path);
        return Arrays.stream(directory.listFiles())
                .filter(file -> file.isFile() && Arrays.binarySearch(exts,getFileExtension(file))>=0 && file.length() <= maxSize)
                .filter(file -> file.lastModified() > lastTs)
                .map(File::getName)
                .collect(Collectors.toList());
    }

    /**
     * 获取文件的后缀名
     * @param file
     * @return
     */
    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return name.substring(lastDotIndex + 1);
    }
}
