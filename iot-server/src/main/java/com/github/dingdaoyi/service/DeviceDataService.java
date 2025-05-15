package com.github.dingdaoyi.service;


import com.github.dingdaoyi.model.query.DeviceDataQuery;
import com.github.dingdaoyi.model.query.DeviceEventDataVo;
import com.github.dingdaoyi.proto.model.DeviceEventData;
import com.github.dingdaoyi.proto.model.KeyValue;

import java.util.List;

/**
 * @author dingyunwei
 */
public interface DeviceDataService {

     List<KeyValue<String, Object>> getLatestData(String deviceKey);

     List<KeyValue<String, Object>> metric(DeviceDataQuery query);

     List<DeviceEventDataVo> eventLogs(DeviceDataQuery query);
}
