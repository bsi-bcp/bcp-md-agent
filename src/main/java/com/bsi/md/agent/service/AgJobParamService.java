package com.bsi.md.agent.service;

import com.bsi.framework.core.service.FwService;
import com.bsi.md.agent.repository.AgJobParamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
@Slf4j
public class AgJobParamService extends FwService {
    @Autowired
    private AgJobParamRepository agJobParamRepository;
}
