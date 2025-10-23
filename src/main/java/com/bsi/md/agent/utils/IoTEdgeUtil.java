package com.bsi.md.agent.utils;


import com.bsi.framework.core.utils.ExceptionUtils;
import com.bsi.utils.JSONUtils;
import com.huaweicloud.sdk.iot.module.DriverClient;
import com.huaweicloud.sdk.iot.module.GatewayCallback;
import com.huaweicloud.sdk.iot.module.ItClient;
import com.huaweicloud.sdk.iot.module.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;

/**
 * @author fish
 * IoTEdge工具类
 */
@Slf4j
public class IoTEdgeUtil {
    private static ItClient itClient = null;

    private static DriverClient driverClient = null;

    static {
        try {
            itClient = ItClient.createFromEnv();
            itClient.open();
        } catch (Exception e) {
            log.error("初始化itClient报错，错误信息:{}", ExceptionUtils.getFullStackTrace(e));
        }

        try {
            driverClient = DriverClient.createFromEnv();
            driverClient.setGatewayCallback(new GatewayCallback() {
                /**
                 * 收到子设备下行消息的处理
                 */
                @Override
                public void onDeviceMessageReceived(Message message) {
                    //不支持
                }

                /**
                 * 收到子设备命令的处理，不支持
                 */
                @Override
                public CommandRsp onDeviceCommandCalled(String s, Command command) {
                    //不支持
                    return new CommandRsp(1, "not supported", null);
                }

                //设备设置属性时触发，用来反控
                @Override
                public IotResult onDevicePropertiesSet(String s, PropsSet propsSet) {
                    log.info("s:{}",s);
                    log.info("PropsSet:{}", JSONUtils.toJson(propsSet));
                    //不支持
                    return new IotResult(1, "not supported");
                }

                @Override
                public PropsGetRsp onDevicePropertiesGet(String s, PropsGet propsGet) {
                    //不支持
                    return new PropsGetRsp();
                }

                @Override
                public void onDeviceShadowReceived(String s, ShadowGetRsp shadowGetRsp) {
                }

                @Override
                public void onDeviceEventReceived(Event event) {
                }

                @Override
                public void onSubDevicesAdded(String s, AddSubDevicesEvent addSubDevicesEvent) {
                }

                @Override
                public void onSubDevicesDeleted(String s, DeleteSubDevicesEvent deleteSubDevicesEvent) {
                }

                @Override
                public void onGetProductsResponse(String s, GetProductsRspEvent getProductsRspEvent) {
                }

                @Override
                public void onStartScan(String s, StartScanEvent startScanEvent) {
                }
            });
            driverClient.open();
        } catch (Exception e) {
            log.error("初始化driverClient报错，错误信息:{}", ExceptionUtils.getFullStackTrace(e));
        }
    }

    public static DriverClient getDriverClient(){
        return driverClient;
    }
    /**
     * 获取httpClient
     * @return
     */
    public static HttpClient getHttpClient(){
        return itClient.getHttpClient();
    }

    /**
     * 获取ItClient对象
     * @return
     */
    public static ItClient getItClient(){
        return itClient;
    }
}
