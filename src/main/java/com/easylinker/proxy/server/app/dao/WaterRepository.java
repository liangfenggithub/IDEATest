package com.easylinker.proxy.server.app.dao;

import com.easylinker.proxy.server.app.model.device.Device;
import com.easylinker.proxy.server.app.model.device.Water;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @suthor liangfeng
 * @create 2018-09-26 19:51)
 */
public interface WaterRepository extends JpaRepository<Water,Long> {
//    List<Water> findAllByDevice(Device device, Pageable pageable);
    Page<Water> findAllByDevice(Device device, Pageable pageable);
}
