package com.easylinker.proxy.server.app.config.mqttconfig.handler;

import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.mqttconfig.MqttMessageSender;
import com.easylinker.proxy.server.app.constants.mqtt.RealTimeType;
import com.easylinker.proxy.server.app.model.device.Device;
import com.easylinker.proxy.server.app.model.device.DeviceData;
import com.easylinker.proxy.server.app.model.device.Water;
import com.easylinker.proxy.server.app.service.DeviceDataService;
import com.easylinker.proxy.server.app.service.DeviceService;
import com.easylinker.proxy.server.app.service.WaterService;
import com.easylinker.proxy.server.app.utils.HttpTool;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.messaging.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/**
 * @suthor liangfeng
 * @create 2018-09-26 11:49)
 */
@Component
public class InReportMessageHandler implements MessageHandler {
    Logger logger = LoggerFactory.getLogger(InReportMessageHandler.class);

    @Autowired
    DeviceService deviceService;

    @Autowired
    DeviceDataService deviceDataService;

    @Autowired
    WaterService waterService;


    @Autowired
    MqttMessageSender mqttMessageSender;

    @Autowired
    HttpTool httpTool;

    @Value("${emq.api.host}")
    String apiHost;

    @Override
    public void handleMessage(Message<?>message) throws MessagingException{
        String topic  = message.getHeaders().get("mqtt_topic").toString();
        try {
            if(topic.startsWith("IN/DEVICE/")){
                Long openId = Long.parseLong(topic.split("/")[4]);
                //去掉引号前面多余的转义字符符号
                String payload = message.getPayload().toString().replace("\\","");
                System.out.println(payload);


//                JSONObject dataJson = JSONObject.parseObject(message.getPayload().toString());
                JSONObject dataJson = JSONObject.parseObject(payload);
                Device device = deviceService.findADevice(openId);
                if(device != null){
                    //开始传输数据
                    if(device.getAppUser() == null){
                        logger.info("默认分组的设备，数据不记录");
                    }else{
                        /**
                         * 这里是自定义的数据格式，数据暂时全部保存到WaterData中
                         */
                        Water waterDate = new Water();
                        waterDate.setDevice(device);
                        waterDate.setData(dataJson.toString());
                        waterDate.setType("REPORT");
                        waterService.save(waterDate);
                        logger.info("WATER数据保存成功");

                        JSONObject realTimeJson  = new JSONObject();
                        realTimeJson.put("type",RealTimeType.DATA_RECEIVED);
                        realTimeJson.put("device",device.getId());
                        mqttMessageSender.sendRealTimePureMessage(realTimeJson);


//                        DeviceData deviceData = new DeviceData();
//                        deviceData.setDevice(device);
//                        deviceData.setData(dataJson.toString());
//                        deviceData.setType("REPORT");
//                        deviceDataService.save(deviceData);
//
//                        logger.info("数据保存成功");
//                        JSONObject realTimeJson = new JSONObject();
//                        realTimeJson.put("type",RealTimeType.DATA_RECEIVED);
//                        realTimeJson.put("device",device.getId());
//                        mqttMessageSender.sendRealTimePureMessage(realTimeJson);
                    }
                }else{
                    logger.info("设备不存在!");
                }
            }
        }catch(Exception e){
            //提交的格式不正确
            //只接受from:IN/DEVICE/DEFAULT_USER/DEFAULT_GROUP/USER ID
            logger.error("数据格式出错!只接受 from:IN/DEVICE/DEFAULT_USER/DEFAULT_GROUP/DEFAULT_DEVICE_ID");
            logger.error("收到的topic是：" + topic + "数据是："+ message.getPayload().toString());
        }
    }
}
