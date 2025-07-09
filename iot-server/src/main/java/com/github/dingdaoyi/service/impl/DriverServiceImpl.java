package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.entity.Driver;
import com.github.dingdaoyi.mapper.DriverMapper;
import com.github.dingdaoyi.service.DriverService;
import org.springframework.stereotype.Service;

/**
 * @author dingyunwei
 */
@Service
public class DriverServiceImpl extends ServiceImpl<DriverMapper, Driver> implements DriverService {
} 