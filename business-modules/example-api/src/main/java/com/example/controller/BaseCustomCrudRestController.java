package com.example.controller;

import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;

/**
* 自定义通用CRUD父类RestController
* @author JerryMa
* @version 1.0
* @date 2021-01-21
* Copyright © www.dibo.ltd
*/
@Slf4j
public class BaseCustomCrudRestController<E extends BaseEntity> extends BaseCrudRestController {

}