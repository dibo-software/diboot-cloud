/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.message.starter;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.cloud.config.Cons;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.service.DictionaryServiceExtProvider;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.JSON;
import com.diboot.core.util.SqlFileInitializer;
import com.diboot.core.util.V;
import com.diboot.core.vo.DictionaryVO;
import com.diboot.iam.entity.*;
import com.diboot.iam.vo.IamResourcePermissionListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 组件初始化
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/11/28
 * @Copyright © diboot.com
 */
@Slf4j
@Component
@Order(950)
public class MessageSqlInitializer implements ApplicationRunner {

    @Autowired
    private MessageProperties messageProperties;

    @Autowired
    private Environment environment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 检查数据库字典是否已存在
        if (messageProperties.isInitSql()) {
            // 初始化SCHEMA
            SqlFileInitializer.init(environment);
            String initDetectSql = "SELECT id FROM ${SCHEMA}.message_template WHERE id=0";
            if (SqlFileInitializer.checkSqlExecutable(initDetectSql) == false) {
                SqlFileInitializer.initBootstrapSql(this.getClass(), environment, "message");
                log.info("diboot-message 初始化SQL完成.");
            }
        }
    }

}
