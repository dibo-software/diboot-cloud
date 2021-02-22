package com.example.service.impl;

import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.core.config.Cons;
import com.diboot.core.util.BeanUtils;
import com.diboot.iam.entity.LoginUserDetail;
import com.diboot.iam.util.IamSecurityUtils;
import com.example.service.BaseCustomService;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Field;
/**
* 自定义 BaseService 接口实现
* @author JerryMa
* @version 1.0
* @date 2021-01-21
 * Copyright © www.dibo.ltd
*/
@Slf4j
public class BaseCustomServiceImpl<M extends BaseCrudMapper<T>, T> extends BaseServiceImpl<M, T> implements BaseCustomService<T> {

    @Override
    protected void beforeCreateEntity(T entity){
        LoginUserDetail currentUser = IamSecurityUtils.getCurrentUser();
        if(currentUser != null){
            // 填充创建人
            Field field = BeanUtils.extractField(entityClass, Cons.FieldName.createBy.name());
            if(field != null){
                BeanUtils.setProperty(entity, Cons.FieldName.createBy.name(), currentUser.getUserId());
            }
        }
    }
}
