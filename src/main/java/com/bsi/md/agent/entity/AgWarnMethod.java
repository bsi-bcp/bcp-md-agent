package com.bsi.md.agent.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 告警方式
 */
@Entity
@Data
@Table(name="md_agent_warn_method")
public class AgWarnMethod extends AgAbstractEntity {
    //名称
    private String name;
    //类型
    private String type;
    //配置数据json串
    @Column(name = "configValue",columnDefinition="ntext")
    private String configValue;

    private Boolean enable;
}
