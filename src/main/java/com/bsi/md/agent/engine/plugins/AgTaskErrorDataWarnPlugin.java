package com.bsi.md.agent.engine.plugins;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bsi.framework.core.utils.CollectionUtils;
import com.bsi.framework.core.utils.EHCacheUtil;
import com.bsi.framework.core.utils.FwSpringContextUtil;
import com.bsi.framework.core.utils.StringUtils;
import com.bsi.md.agent.constant.AgConstant;
import com.bsi.md.agent.email.AgEmailEntity;
import com.bsi.md.agent.email.AgEmailService;
import com.bsi.md.agent.engine.integration.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 任务异常告警插件
 */
@Component
public class AgTaskErrorDataWarnPlugin implements AgAfterOutputPlugin{

    private static Logger info_log = LoggerFactory.getLogger("TASK_INFO_LOG");
    private static Pattern dynamicLimitCount = Pattern.compile("\\$\\{([A-Za-z0-9]+)\\}");

    @Override
    public void handlerMsg(Context context) {
        String warnMethodId = context.getWarnMethodId();
        String taskId = context.getTaskId();
        String errorData = context.getTaskErrorData();
        //告警方式为空或者异常数据为空，则不需要继续执行
        if(StringUtils.isNullOrBlank(warnMethodId) || StringUtils.isNullOrBlank(errorData)){
            return;
        }
        Object cnf = EHCacheUtil.getValue(AgConstant.AG_EHCACHE_WARN,warnMethodId);
        String warnConf = cnf==null?"":cnf.toString();
        //告警配置不存在
        if(StringUtils.isNullOrBlank(warnConf)){
            return;
        }

        JSONObject warn = JSON.parseObject(warnConf);
        JSONObject warnObj = JSON.parseObject(warn.getString("configValue"));
        AgEmailService agEmailService = FwSpringContextUtil.getBean("agEmailService",AgEmailService.class);

        JavaMailSenderImpl javaMailSenderImpl = FwSpringContextUtil.getBean(JavaMailSenderImpl.class);
        javaMailSenderImpl.setHost(warnObj.getString("host"));
        javaMailSenderImpl.setUsername(warnObj.getString("userName"));
        javaMailSenderImpl.setPassword(warnObj.getString("password"));
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.timeout", "60000");
        properties.put("bcp.mail.from", warnObj.getString("userName"));
        javaMailSenderImpl.setJavaMailProperties(properties);

        AgEmailEntity email = new AgEmailEntity();
        email.setSubject(warnObj.getString("title"));
        email.setReceiver(warnObj.getString("receiver"));
        String content = warnObj.getString("content");
        email.setContent( setContentParams(content,errorData) );


        //发送邮件
        agEmailService.sendEmail(email);
    }

    /**
     * 修改内容中的参数
     * @param content
     */
    private String setContentParams(String content,String errorData){
        List<String> list = getKeyListByContent(content);
        JSONObject obj = JSON.parseObject(errorData);
        if(CollectionUtils.isNotEmpty(list)){
            for (String k : list) {
                content = content.replaceAll("\\$\\{"+k+"\\}",obj.getString(k));
            }

        }
        return content;
    }

    /**
     * 按照动态内容的参数出现顺序,将参数放到List中
     *
     * @param content
     * @return
     */
    private List<String> getKeyListByContent(String content) {
        Set<String> paramSet = new LinkedHashSet<>();
        Matcher m = dynamicLimitCount.matcher(content);
        while (m.find()) {
            paramSet.add(m.group(1));
        }
        return new ArrayList<>(paramSet);
    }
}
