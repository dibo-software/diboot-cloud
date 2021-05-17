package com.example.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.example.entity.Demo;
import org.apache.ibatis.annotations.Mapper;

/**
* 样例Mapper
* @author JerryMa
* @version 1.0
* @date 2021-01-21
 * Copyright © www.dibo.ltd
*/
@Mapper
public interface DemoMapper extends BaseCrudMapper<Demo> {

}

