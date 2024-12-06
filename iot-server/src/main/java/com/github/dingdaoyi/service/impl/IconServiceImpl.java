package com.github.dingdaoyi.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.entity.Icon;
import com.github.dingdaoyi.mapper.IconMapper;
import com.github.dingdaoyi.service.IconService;
@Service
public class IconServiceImpl extends ServiceImpl<IconMapper, Icon> implements IconService{

}
