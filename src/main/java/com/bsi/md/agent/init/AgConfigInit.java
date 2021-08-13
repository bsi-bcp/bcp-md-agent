package com.bsi.md.agent.init;

import com.alibaba.fastjson.JSONObject;
import com.bsi.framework.core.schedule.FwScheduleUtils;
import com.bsi.framework.core.utils.CollectionUtils;
import com.bsi.md.agent.engine.integration.input.AgInput;
import com.bsi.md.agent.entity.AgApiProxy;
import com.bsi.md.agent.entity.AgConfig;
import com.bsi.md.agent.entity.AgDataSource;
import com.bsi.md.agent.entity.AgJob;
import com.bsi.md.agent.entity.vo.AgIntegrationConfigVo;
import com.bsi.md.agent.entity.vo.AgNodeVo;
import com.bsi.md.agent.service.AgApiProxyService;
import com.bsi.md.agent.service.AgConfigService;
import com.bsi.md.agent.service.AgDataSourceService;
import com.bsi.md.agent.service.AgJobService;
import com.bsi.md.agent.task.AgTaskRun;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 配置初始化
 * @author fish
 */
@Component
@Order(1)
@Slf4j
public class AgConfigInit implements ApplicationRunner {
	@Autowired
	private AgJobService agJobService;

	@Autowired
	private AgDataSourceService agDataSourceService;

	/**
	 * 初始化计划任务
	 * @param args
	 */
	@Override
	public void run(ApplicationArguments args) {
		//1、初始化数据源
		Boolean flag1 = agDataSourceService.refreshDataSource();

        //2、初始化定时任务和实时接口
		Boolean flag2 = agJobService.refreshJob();

		if( !flag1 || !flag2 ){
			log.error("初始化配置失败，系统退出。。。。。。。");
			System.exit(0);
		}
	}
}
