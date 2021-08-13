package com.bsi.md.agent.service;

import com.bsi.framework.core.service.FwService;
import com.bsi.md.agent.entity.AgConfig;
import com.bsi.md.agent.repository.AgConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 计划任务service
 */

@Service
@Transactional
public class AgConfigService extends FwService {
    @Autowired
    private AgConfigRepository agConfigRepository;

    /**
     * 查询所有集成配置
     * @return List<AgConfig>
     */
    public List<AgConfig> findAll(){
        return agConfigRepository.findAll();
    }
}
