/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.scheduler.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.diboot.core.util.BeanUtils;
import com.diboot.iam.entity.LoginUserDetail;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.scheduler.entity.ScheduleJob;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/***
 * mybatis-plus 自动填充字段值
 * @author JerryMa
 * @version 1.0
 * @date 2020-12-10
 * * Copyright © www.dibo.ltd
 */
@Component
public class CustomMetaObjectHandler implements MetaObjectHandler {

    private static String createByName = BeanUtils.convertToFieldName(ScheduleJob::getCreateByName);

    @Override
    public void insertFill(MetaObject metaObject) {
        // 填充create_by_name冗余字段
        LoginUserDetail currentUser = IamSecurityUtils.getCurrentUser();
        if(currentUser != null){
            this.strictInsertFill(metaObject, createByName, String.class, currentUser.getDisplayName());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
    }

}