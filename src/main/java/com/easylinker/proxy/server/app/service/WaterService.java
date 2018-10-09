package com.easylinker.proxy.server.app.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.dao.WaterRepository;
import com.easylinker.proxy.server.app.model.device.Device;
import com.easylinker.proxy.server.app.model.device.Water;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @suthor liangfeng
 * @create 2018-09-26 19:49)
 */
@Service
public class WaterService {
    @Autowired
    WaterRepository waterRepository;
    public void save(Water water){
        waterRepository.save(water);
    }
//    public JSONArray getAllDeviceDataByDevice(Device device, Pageable pageable){
//        JSONArray data = new JSONArray();
//        List<Water> daltaList = waterRepository.findAllByDevice(device,pageable);
//        for(Water waterdata : daltaList){
//            JSONObject dataJson = new JSONObject();
//            dataJson.put("data",JSONObject.parse(waterdata.getData()));
//            dataJson.put("create_time",waterdata.getCreateTime());
//            dataJson.put("ID",waterdata.getId());
//            data.add(dataJson);
//        }
//        return data;
//    }
    /**
     * 分页根据device查询设备传输信息
     *
     */
    public JSONObject getAllDeviceDataByDevice(Device device,Pageable pageable){
        Page<Water> dataPage = waterRepository.findAllByDevice(device,pageable);
        JSONArray pageArray = new JSONArray();
        for(Water waterData : dataPage.getContent()){
            JSONObject jsonObject = new JSONObject();
            String str= waterData.getData();//保存的原始字符串格式的传感器数据

            JSONObject detailData = JSONObject.parseObject(str);// 把JSON文本parse成JSONObject

            detailData = detailData.getJSONObject("data");//获取data对象
            JSONObject gpsData = detailData.getJSONObject("gpsData");
            JSONObject gps = gpsData.getJSONObject("gps");
            JSONObject agps = gpsData.getJSONObject("agps");
            System.out.println("detailData is: " +detailData);
            System.out.println("gps: "+ gps);
            System.out.println("agps: "+ agps);
            jsonObject.put("createTime",waterData.getCreateTime());
            jsonObject.put("deviceId",waterData.getDevice().getId());
//            jsonObject.put("data",waterData.getData());

            jsonObject.put("GPS_Longitude",gps.getFloat("GPS_Longitude"));
            jsonObject.put("GPS_Latitude",gps.getFloat("GPS_Latitude"));
            jsonObject.put("AGPS_Longitude",agps.getFloat("AGPS_Longitude"));
            jsonObject.put("AGPS_Latitude",agps.getFloat("AGPS_Latitude"));
            jsonObject.put("PH",detailData.getFloat("PH"));
            jsonObject.put("Conductivity",detailData.getFloat("Conductivity"));
            jsonObject.put("ORP",detailData.getFloat("ORP"));
            jsonObject.put("Turbidity",detailData.getFloat("Turbidity"));
            jsonObject.put("O2",detailData.getFloat("O2"));
            JSONObject voltage = detailData.getJSONObject("voltage");//取得电压子对象
            jsonObject.put("BatVoltage",voltage.getFloat("BatVoltage"));
            jsonObject.put("SolarVoltage",voltage.getFloat("SolarVoltage"));
            jsonObject.put("SensorVoltage",voltage.getFloat("SensorVoltage"));


            if(waterData.getType() != null){
                jsonObject.put("type",waterData.getType());
            }
            pageArray.add(jsonObject);
        }

        JSONObject pageJson = new JSONObject();
        pageJson.put("page",dataPage.getNumber());
        pageJson.put("total",dataPage.getTotalPages());
        pageJson.put("size",dataPage.getSize());
        pageJson.put("isLast",dataPage.isLast());
        pageJson.put("totalPages",dataPage.getTotalPages());
        pageJson.put("isFirst",dataPage.isFirst());
        pageJson.put("totalElements",dataPage.getTotalElements());
        pageJson.put("data",pageArray);
        return pageJson;
    }

}
