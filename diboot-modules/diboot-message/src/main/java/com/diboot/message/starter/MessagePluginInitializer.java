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
public class MessagePluginInitializer implements ApplicationRunner {

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
                // 插入相关数据：Dict等
//                insertInitData();
                log.info("diboot-message 初始化SQL完成.");
            }
        }
    }

    /**
     * 插入初始化数据
     */
    private void insertInitData() {
        String applicationName = environment.getProperty("spring.application.name");

        // 插入iam组件所需的数据字典
        String[] DICT_INIT_DATA = {

        };
        // 插入数据字典
        for (String dictJson : DICT_INIT_DATA) {
            DictionaryVO dictVo = JSON.toJavaObject(dictJson, DictionaryVO.class);
            dictVo.setAppModule(applicationName);
            dictVo.getChildren().forEach(c->{c.setAppModule(applicationName);});
            ContextHelper.getBean(DictionaryService.class).createDictAndChildren(dictVo);

//            ContextHelper.getBean(DictionaryServiceExtProvider.class).createDictAndChildren(dictVo);
        }

        DICT_INIT_DATA = null;

        // 插入iam组件所需的初始权限数据
//        String[] RESOURCE_PERMISSION_DATA = {
//                "{\"displayType\":\"MENU\",\"displayName\":\"系统管理\",\"resourceCode\":\"system\",\"children\":[{\"displayType\":\"MENU\",\"displayName\":\"数据字典管理\",\"resourceCode\":\"Dictionary\",\"apiSet\":\"GET:/dictionary/list\",\"sortId\":\"10031\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"apiSet\":\"GET:/dictionary/{id}\",\"sortId\":\"6\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"apiSet\":\"POST:/dictionary/\",\"sortId\":\"5\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"apiSet\":\"PUT:/dictionary/{id}\",\"sortId\":\"4\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"apiSet\":\"DELETE:/dictionary/{id}\",\"sortId\":\"3\"}]},{\"displayType\":\"MENU\",\"displayName\":\"系统用户管理\",\"resourceCode\":\"IamUser\",\"apiSet\":\"GET:/iam/user/list\",\"sortId\":\"10030\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"部门查看\",\"resourceCode\":\"orgTree\",\"apiSet\":\"GET:/iam/org/tree\",\"sortId\":\"12\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"apiSet\":\"GET:/iam/user/{id}\",\"sortId\":\"11\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"apiSet\":\"POST:/iam/user/\",\"sortId\":\"10\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"apiSet\":\"PUT:/iam/user/{id}\",\"sortId\":\"9\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"apiSet\":\"DELETE:/iam/user/{id}\",\"sortId\":\"8\"}]},{\"displayType\":\"MENU\",\"displayName\":\"角色资源管理\",\"resourceCode\":\"IamRole\",\"apiSet\":\"GET:/iam/role/list\",\"sortId\":\"10023\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"apiSet\":\"GET:/iam/role/{id}\",\"sortId\":\"16\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"apiSet\":\"POST:/iam/role/\",\"sortId\":\"15\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"apiSet\":\"PUT:/iam/role/{id}\",\"sortId\":\"14\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"apiSet\":\"DELETE:/iam/role/{id}\",\"sortId\":\"13\"}]},{\"displayType\":\"MENU\",\"displayName\":\"资源权限管理\",\"resourceCode\":\"IamResourcePermission\",\"apiSet\":\"GET:/iam/resourcePermission/list\",\"sortId\":\"10017\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"apiSet\":\"GET:/iam/resourcePermission/{id}\",\"sortId\":\"23\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"apiSet\":\"POST:/resourcePermission/user/\",\"sortId\":\"21\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"apiSet\":\"PUT:/iam/resourcePermission/{id}\",\"sortId\":\"20\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"apiSet\":\"DELETE:/iam/resourcePermission/{id}\",\"sortId\":\"19\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"排序\",\"resourceCode\":\"sort\",\"apiSet\":\"POST:/iam/resourcePermission/sortList\",\"sortId\":\"18\"}]},{\"displayType\":\"MENU\",\"displayName\":\"定时任务管理\",\"resourceCode\":\"ScheduleJob\",\"apiSet\":\"GET:/scheduleJob/list\",\"appModule\":\"scheduler\",\"sortId\":\"10012\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"apiSet\":\"DELETE:/scheduleJob/{id}\",\"appModule\":\"scheduler\",\"sortId\":\"5\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"apiSet\":\"PUT:/scheduleJob/{id}/{action}\",\"appModule\":\"scheduler\",\"sortId\":\"4\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"apiSet\":\"POST:/scheduleJob/\",\"appModule\":\"scheduler\",\"sortId\":\"3\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"apiSet\":\"GET:/scheduleJob/{id},GET:/scheduleJob/log/list,GET:/scheduleJob/log/{id}\",\"appModule\":\"scheduler\",\"sortId\":\"2\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"运行一次\",\"resourceCode\":\"executeOnce\",\"apiSet\":\"PUT:/scheduleJob/executeOnce/{id}\",\"appModule\":\"scheduler\",\"sortId\":\"1\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"日志记录\",\"resourceCode\":\"logList\",\"apiSet\":\"GET:/scheduleJob/log/list,GET:/scheduleJob/log/{id}\",\"appModule\":\"scheduler\",\"sortId\":\"0\"}]},{\"displayType\":\"MENU\",\"displayName\":\"操作日志查看\",\"resourceCode\":\"IamOperationLog\",\"apiSet\":\"GET:/iam/operationLog/list\",\"sortId\":\"10006\",\"children\":[]},{\"displayType\":\"MENU\",\"displayName\":\"登录日志查看\",\"resourceCode\":\"IamLoginTrace\",\"apiSet\":\"GET:/iam/loginTrace/list\",\"sortId\":\"10001\",\"children\":[]}]}",
//                "{\"displayType\":\"MENU\",\"displayName\":\"组织机构\",\"resourceCode\":\"orgStructure\",\"children\":[{\"displayType\":\"MENU\",\"displayName\":\"组织机构管理\",\"resourceCode\":\"IamOrg\",\"apiSet\":\"POST:/iam/org/sortList,GET:/iam/org/tree,GET:/iam/org/tree/{parentNodeId},GET:/iam/org/list\",\"sortId\":\"10045\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"排序\",\"resourceCode\":\"sort\",\"apiSet\":\"POST:/iam/org/sortList\",\"sortId\":\"106\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"apiSet\":\"DELETE:/iam/org/{id}\",\"sortId\":\"105\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"apiSet\":\"PUT:/iam/org/{id}\",\"sortId\":\"104\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"apiSet\":\"POST:/iam/org/\",\"sortId\":\"103\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"apiSet\":\"GET:/iam/org/{id}\",\"sortId\":\"102\"}]},{\"displayType\":\"MENU\",\"displayName\":\"岗位管理\",\"resourceCode\":\"IamPosition\",\"apiSet\":\"GET:/iam/position/list\",\"sortId\":\"10039\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"apiSet\":\"DELETE:/iam/position/{id}\",\"sortId\":\"112\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"apiSet\":\"GET:/iam/position/{id}\",\"sortId\":\"111\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"apiSet\":\"PUT:/iam/position/{id}\",\"sortId\":\"110\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"apiSet\":\"POST:/iam/position/\",\"sortId\":\"108\"}]},{\"displayType\":\"MENU\",\"displayName\":\"组织人员管理\",\"resourceCode\":\"IamOrgUser\",\"apiSet\":\"GET:/iam/org/tree,GET:/iam/user/list\",\"sortId\":\"10033\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"apiSet\":\"POST:/iam/user/\",\"sortId\":\"40\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"apiSet\":\"PUT:/iam/org/{id}\",\"sortId\":\"39\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"apiSet\":\"DELETE:/iam/user/{id}\",\"sortId\":\"38\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"apiSet\":\"GET:/iam/user/{id}\",\"sortId\":\"37\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"人员岗位设置\",\"resourceCode\":\"position\",\"apiSet\":\"POST:/iam/position/batchUpdateUserPositionRelations,GET:/iam/position/listUserPositions/{userType}/{userId},GET:/iam/positionkvList\",\"sortId\":\"36\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"添加岗位\",\"resourceCode\":\"addPosition\",\"apiSet\":\"POST:/iam/position/\",\"sortId\":\"35\"}]}]}"
//        };
//        // 插入多层级菜单权限初始数据
//        try {
//            for (String resourcePermissionJson : RESOURCE_PERMISSION_DATA) {
//                IamResourcePermissionListVO permissionListVO = JSON.toJavaObject(resourcePermissionJson, IamResourcePermissionListVO.class);
//                if (V.isEmpty(permissionListVO.getAppModule())) {
//                    permissionListVO.setAppModule(applicationName);
//                }
//                permissionListVO.getChildren().forEach(c->{
//                    if (V.isEmpty(c.getAppModule())) {
//                        c.setAppModule(applicationName);
//                    }
//                    c.getChildren().forEach(c2->{
//                        if (V.isEmpty(c2.getAppModule())) {
//                            c2.setAppModule(applicationName);
//                        }
//                    });
//                });
//                ContextHelper.getBean(IamResourcePermissionService.class).deepCreatePermissionAndChildren(permissionListVO);
//            }
//            RESOURCE_PERMISSION_DATA = null;
//        } catch (BusinessException e){
//            log.error("初始化菜单权限数据出错，请手动配置菜单初始的权限数据", e.getMessage());
//        }

    }
}
