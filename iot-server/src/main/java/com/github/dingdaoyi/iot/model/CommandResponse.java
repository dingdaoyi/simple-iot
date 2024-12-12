package com.github.dingdaoyi.iot.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dwx
 */
@Data
public class CommandResponse{


   private Map<String, Object> resultData = new HashMap<>();
}
