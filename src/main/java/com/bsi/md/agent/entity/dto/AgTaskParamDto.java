package com.bsi.md.agent.entity.dto;

import lombok.Data;

/**
 * 计划任务参数传输对象
 * @author fish
 */
@Data
public class AgTaskParamDto {
    private String taskId;
    private String startTime;
    private String endTime;
    private String runParams;
}
