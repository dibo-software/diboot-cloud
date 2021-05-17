package com.example.service.impl;

import com.diboot.core.util.BeanUtils;
import com.example.entity.Demo;
import com.example.mapper.DemoMapper;
import com.example.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* 样例相关Service实现
* @author JerryMa
* @version 1.0
* @date 2021-01-21
 * Copyright © www.dibo.ltd
*/
@Service
@Slf4j
public class DemoServiceImpl extends BaseCustomServiceImpl<DemoMapper, Demo> implements DemoService {

}
