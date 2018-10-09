package com.easylinker.proxy.server.app.model.device;

import com.easylinker.proxy.server.app.model.base.BaseEntity;

import javax.persistence.*;

/**
 * @author liangfeng
 * @create 2018-09-26 19:44)
 */
@Entity
public class Water extends BaseEntity {
    @Lob
    @Column
    private String data;
    @ManyToOne(targetEntity = Device.class,fetch = FetchType.LAZY)
    private Device device;
    private String type;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
