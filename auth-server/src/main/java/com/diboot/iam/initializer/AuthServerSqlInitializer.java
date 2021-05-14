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
package com.diboot.iam.initializer;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.cloud.config.Cons;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.JSON;
import com.diboot.core.util.SqlFileInitializer;
import com.diboot.core.util.V;
import com.diboot.core.vo.DictionaryVO;
import com.diboot.iam.entity.*;
import com.diboot.iam.service.*;
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
 * IAM组件相关的初始化
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/23
 */
@Slf4j
@Component
@Order(920)
public class AuthServerSqlInitializer implements ApplicationRunner {

    @Autowired
    private Environment environment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        SqlFileInitializer.init(environment);
        // 验证SQL
        String initDetectSql = "SELECT id FROM ${SCHEMA}.iam_role WHERE id=0";
        if(SqlFileInitializer.checkSqlExecutable(initDetectSql) == false){
            log.info("auth-server 初始化SQL ...");
            // 执行初始化SQL
            SqlFileInitializer.initBootstrapSql(this.getClass(), environment, "iam");
            // 插入相关数据：Dict，Role等
            insertInitData();
            log.info("auth-server 初始化SQL完成.");
        }
    }

    /**
     * 插入初始化数据
     */
    private void insertInitData(){
        String applicationName = environment.getProperty("spring.application.name");
        // 插入iam组件所需的数据字典
        String[] DICT_INIT_DATA = {
                "{\"type\":\"AUTH_TYPE\", \"itemName\":\"登录认证方式\", \"description\":\"IAM用户登录认证方式\", \"children\":[{\"itemName\":\"用户名密码\", \"itemValue\":\"PWD\", \"sortId\":1},{\"itemName\":\"单点登录\", \"itemValue\":\"SSO\", \"sortId\":2},{\"itemName\":\"公众号\", \"itemValue\":\"WX_MP\", \"sortId\":3},{\"itemName\":\"企业微信\", \"itemValue\":\"WX_CP\", \"sortId\":4},{\"itemName\":\"其他\", \"itemValue\":\"OTHER\", \"sortId\":5}]}",
                "{\"type\":\"ACCOUNT_STATUS\", \"itemName\":\"账号状态\", \"description\":\"IAM登录账号状态\", \"children\":[{\"itemName\":\"有效\", \"itemValue\":\"A\", \"sortId\":1},{\"itemName\":\"无效\", \"itemValue\":\"I\", \"sortId\":2},{\"itemName\":\"锁定\", \"itemValue\":\"L\", \"sortId\":3}]}",
                "{\"type\":\"USER_STATUS\", \"itemName\":\"用户状态\", \"description\":\"IAM用户状态\", \"editable\":true, \"children\":[{\"itemName\":\"在职\", \"itemValue\":\"A\", \"sortId\":1},{\"itemName\":\"离职\", \"itemValue\":\"I\", \"sortId\":2}]}",
                "{\"itemName\":\"用户性别\",\"type\":\"GENDER\",\"description\":\"用户性别数据字典\",\"children\":[{\"itemValue\":\"F\",\"sortId\":99,\"itemName\":\"女\"},{\"itemValue\":\"M\",\"sortId\":99,\"itemName\":\"男\"}]}",
                "{\"type\":\"PERMISSION_TYPE\", \"itemName\":\"权限类型\", \"description\":\"IAM权限类型\", \"children\":[{\"itemName\":\"菜单\", \"itemValue\":\"MENU\", \"sortId\":1},{\"itemName\":\"操作\", \"itemValue\":\"OPERATION\", \"sortId\":2}]}",
                "{\"itemName\":\"前端按钮/权限编码\",\"type\":\"RESOURCE_PERMISSION_CODE\",\"description\":\"前端按钮/权限编码 常用选项\",\"children\":[{\"sortId\":1,\"itemName\":\"详情\",\"itemValue\":\"detail\"},{\"sortId\":2,\"itemName\":\"新建\",\"itemValue\":\"create\"},{\"sortId\":3,\"itemName\":\"更新\",\"itemValue\":\"update\"},{\"sortId\":4,\"itemName\":\"删除\",\"itemValue\":\"delete\"},{\"sortId\":5,\"itemName\":\"导出\",\"itemValue\":\"export\"},{\"sortId\":6,\"itemName\":\"导入\",\"itemValue\":\"import\"}]}",
                "{\"type\":\"ORG_TYPE\", \"itemName\":\"组织类型\", \"description\":\"组织节点类型\", \"editable\":false, \"children\":[{\"itemName\":\"部门\", \"itemValue\":\"DEPT\", \"sortId\":1},{\"itemName\":\"公司\", \"itemValue\":\"COMP\", \"sortId\":2}]}",
                "{\"type\":\"DATA_PERMISSION_TYPE\", \"itemName\":\"IAM数据权限类型\", \"description\":\"IAM数据权限类型定义\", \"editable\":true, \"children\":[{\"itemName\":\"本人\", \"itemValue\":\"SELF\", \"sortId\":1},{\"itemName\":\"本人及下属\", \"itemValue\":\"SELF_AND_SUB\", \"sortId\":2},{\"itemName\":\"本部门\", \"itemValue\":\"DEPT\", \"sortId\":3},{\"itemName\":\"本部门及下属部门\", \"itemValue\":\"DEPT_AND_SUB\", \"sortId\":4},{\"itemName\":\"全部\", \"itemValue\":\"ALL\", \"sortId\":5}]}",
                "{\"type\":\"POSITION_GRADE\", \"itemName\":\"职级定义\", \"description\":\"职务级别定义\", \"editable\":true, \"children\":[{\"itemName\":\"初级\", \"itemValue\":\"E1\", \"sortId\":1},{\"itemName\":\"中级\", \"itemValue\":\"E2\", \"sortId\":2},{\"itemName\":\"高级\", \"itemValue\":\"E3\", \"sortId\":3},{\"itemName\":\"专家\", \"itemValue\":\"E4\", \"sortId\":4}]}",
                "{\"type\":\"MESSAGE_STATUS\", \"itemName\":\"消息状态\", \"description\":\"message消息状态\",\"appModule\":\"message-server\", \"children\":[{\"itemName\":\"发送中\", \"itemValue\":\"SENDING\", \"sortId\":1,\"appModule\":\"message-server\"},{\"itemName\":\"发送异常\", \"itemValue\":\"EXCEPTION\", \"sortId\":2,\"appModule\":\"message-server\"},{\"itemName\":\"已送达\", \"itemValue\":\"DELIVERY\", \"sortId\":3,\"appModule\":\"message-server\"},{\"itemName\":\"未读\", \"itemValue\":\"UNREAD\", \"sortId\":4,\"appModule\":\"message-server\"},{\"itemName\":\"已读\", \"itemValue\":\"READ\", \"sortId\":5,\"appModule\":\"message-server\"}]}",
                "{\"type\":\"MESSAGE_CHANNEL\", \"itemName\":\"发送通道\", \"description\":\"message发送通道\",\"appModule\":\"message-server\", \"children\":[{\"itemName\":\"站内信\", \"itemValue\":\"WEBSOCKET\", \"sortId\":1,\"appModule\":\"message-server\"},{\"itemName\":\"短信\", \"itemValue\":\"TEXT_MESSAGE\", \"sortId\":2,\"appModule\":\"message-server\"},{\"itemName\":\"邮件\", \"itemValue\":\"EMAIL\", \"sortId\":3,\"appModule\":\"message-server\"}]}"
        };
        // 插入数据字典
        for(String dictJson : DICT_INIT_DATA){
            DictionaryVO dictVo = JSON.toJavaObject(dictJson, DictionaryVO.class);
            if (V.isEmpty(dictVo.getAppModule())) {
                dictVo.setAppModule(applicationName);
            }
            dictVo.getChildren().forEach(c->{
                if (V.isEmpty(c.getAppModule())) {
                    c.setAppModule(applicationName);
                }
            });
            ContextHelper.getBean(DictionaryService.class).createDictAndChildren(dictVo);
        }
        DICT_INIT_DATA = null;

        // 插入iam组件所需的初始权限数据
        String[] RESOURCE_PERMISSION_DATA = {
                "{\"displayType\":\"MENU\",\"displayName\":\"系统管理\",\"resourceCode\":\"system\",\"children\":[{\"displayType\":\"MENU\",\"displayName\":\"数据字典管理\",\"resourceCode\":\"Dictionary\",\"apiSet\":\"GET:/dictionary/list\",\"sortId\":\"10031\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"apiSet\":\"GET:/dictionary/{id}\",\"sortId\":\"6\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"apiSet\":\"POST:/dictionary/\",\"sortId\":\"5\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"apiSet\":\"PUT:/dictionary/{id}\",\"sortId\":\"4\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"apiSet\":\"DELETE:/dictionary/{id}\",\"sortId\":\"3\"}]},{\"displayType\":\"MENU\",\"displayName\":\"系统用户管理\",\"resourceCode\":\"IamUser\",\"apiSet\":\"GET:/iam/user/list\",\"sortId\":\"10030\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"部门查看\",\"resourceCode\":\"orgTree\",\"apiSet\":\"GET:/iam/org/tree\",\"sortId\":\"12\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"apiSet\":\"GET:/iam/user/{id}\",\"sortId\":\"11\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"apiSet\":\"POST:/iam/user/\",\"sortId\":\"10\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"apiSet\":\"PUT:/iam/user/{id}\",\"sortId\":\"9\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"apiSet\":\"DELETE:/iam/user/{id}\",\"sortId\":\"8\"}]},{\"displayType\":\"MENU\",\"displayName\":\"角色资源管理\",\"resourceCode\":\"IamRole\",\"apiSet\":\"GET:/iam/role/list\",\"sortId\":\"10023\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"apiSet\":\"GET:/iam/role/{id}\",\"sortId\":\"16\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"apiSet\":\"POST:/iam/role/\",\"sortId\":\"15\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"apiSet\":\"PUT:/iam/role/{id}\",\"sortId\":\"14\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"apiSet\":\"DELETE:/iam/role/{id}\",\"sortId\":\"13\"}]},{\"displayType\":\"MENU\",\"displayName\":\"资源权限管理\",\"resourceCode\":\"IamResourcePermission\",\"apiSet\":\"GET:/iam/resourcePermission/list\",\"sortId\":\"10017\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"apiSet\":\"GET:/iam/resourcePermission/{id}\",\"sortId\":\"23\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"apiSet\":\"POST:/resourcePermission/user/\",\"sortId\":\"21\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"apiSet\":\"PUT:/iam/resourcePermission/{id}\",\"sortId\":\"20\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"apiSet\":\"DELETE:/iam/resourcePermission/{id}\",\"sortId\":\"19\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"排序\",\"resourceCode\":\"sort\",\"apiSet\":\"POST:/iam/resourcePermission/sortList\",\"sortId\":\"18\"}]},{\"displayType\":\"MENU\",\"displayName\":\"消息模版管理\",\"resourceCode\":\"MessageTemplate\",\"apiSet\":\"GET:/messageTemplate/list\",\"sortId\":\"10017\",\"appModule\":\"message-server\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"apiSet\":\"GET:/messageTemplate/{id}\",\"sortId\":\"23\",\"appModule\":\"message-server\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"apiSet\":\"POST:/messageTemplate/\",\"sortId\":\"21\",\"appModule\":\"message-server\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"apiSet\":\"PUT:/messageTemplate/{id}\",\"sortId\":\"20\",\"appModule\":\"message-server\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"apiSet\":\"DELETE:/messageTemplate/{id}\",\"sortId\":\"19\",\"appModule\":\"message-server\"}]},{\"displayType\":\"MENU\",\"displayName\":\"消息记录管理\",\"resourceCode\":\"Message\",\"apiSet\":\"GET:/message/list\",\"sortId\":\"10017\",\"appModule\":\"message-server\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"apiSet\":\"GET:/message/{id}\",\"sortId\":\"23\",\"appModule\":\"message-server\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"apiSet\":\"POST:/message/\",\"sortId\":\"21\",\"appModule\":\"message-server\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"apiSet\":\"PUT:/message/{id}\",\"sortId\":\"20\",\"appModule\":\"message-server\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"apiSet\":\"DELETE:/message/{id}\",\"sortId\":\"19\",\"appModule\":\"message-server\"}]},{\"displayType\":\"MENU\",\"displayName\":\"定时任务管理\",\"resourceCode\":\"ScheduleJob\",\"apiSet\":\"GET:/scheduleJob/list\",\"appModule\":\"scheduler\",\"sortId\":\"10012\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"apiSet\":\"DELETE:/scheduleJob/{id}\",\"appModule\":\"scheduler\",\"sortId\":\"5\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"apiSet\":\"PUT:/scheduleJob/{id}/{action}\",\"appModule\":\"scheduler\",\"sortId\":\"4\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"apiSet\":\"POST:/scheduleJob/\",\"appModule\":\"scheduler\",\"sortId\":\"3\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"apiSet\":\"GET:/scheduleJob/{id},GET:/scheduleJob/log/list,GET:/scheduleJob/log/{id}\",\"appModule\":\"scheduler\",\"sortId\":\"2\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"运行一次\",\"resourceCode\":\"executeOnce\",\"apiSet\":\"PUT:/scheduleJob/executeOnce/{id}\",\"appModule\":\"scheduler\",\"sortId\":\"1\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"日志记录\",\"resourceCode\":\"logList\",\"apiSet\":\"GET:/scheduleJob/log/list,GET:/scheduleJob/log/{id}\",\"appModule\":\"scheduler\",\"sortId\":\"0\"}]},{\"displayType\":\"MENU\",\"displayName\":\"操作日志查看\",\"resourceCode\":\"IamOperationLog\",\"apiSet\":\"GET:/iam/operationLog/list\",\"sortId\":\"10006\",\"children\":[]},{\"displayType\":\"MENU\",\"displayName\":\"登录日志查看\",\"resourceCode\":\"IamLoginTrace\",\"apiSet\":\"GET:/iam/loginTrace/list\",\"sortId\":\"10001\",\"children\":[]}]}",
                "{\"displayType\":\"MENU\",\"displayName\":\"组织机构\",\"resourceCode\":\"orgStructure\",\"children\":[{\"displayType\":\"MENU\",\"displayName\":\"组织机构管理\",\"resourceCode\":\"IamOrg\",\"apiSet\":\"POST:/iam/org/sortList,GET:/iam/org/tree,GET:/iam/org/tree/{parentNodeId},GET:/iam/org/list\",\"sortId\":\"10045\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"排序\",\"resourceCode\":\"sort\",\"apiSet\":\"POST:/iam/org/sortList\",\"sortId\":\"106\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"apiSet\":\"DELETE:/iam/org/{id}\",\"sortId\":\"105\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"apiSet\":\"PUT:/iam/org/{id}\",\"sortId\":\"104\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"apiSet\":\"POST:/iam/org/\",\"sortId\":\"103\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"apiSet\":\"GET:/iam/org/{id}\",\"sortId\":\"102\"}]},{\"displayType\":\"MENU\",\"displayName\":\"岗位管理\",\"resourceCode\":\"IamPosition\",\"apiSet\":\"GET:/iam/position/list\",\"sortId\":\"10039\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"apiSet\":\"DELETE:/iam/position/{id}\",\"sortId\":\"112\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"apiSet\":\"GET:/iam/position/{id}\",\"sortId\":\"111\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"apiSet\":\"PUT:/iam/position/{id}\",\"sortId\":\"110\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"apiSet\":\"POST:/iam/position/\",\"sortId\":\"108\"}]},{\"displayType\":\"MENU\",\"displayName\":\"组织人员管理\",\"resourceCode\":\"IamOrgUser\",\"apiSet\":\"GET:/iam/org/tree,GET:/iam/user/list\",\"sortId\":\"10033\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"apiSet\":\"POST:/iam/user/\",\"sortId\":\"40\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"apiSet\":\"PUT:/iam/org/{id}\",\"sortId\":\"39\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"apiSet\":\"DELETE:/iam/user/{id}\",\"sortId\":\"38\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"apiSet\":\"GET:/iam/user/{id}\",\"sortId\":\"37\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"人员岗位设置\",\"resourceCode\":\"position\",\"apiSet\":\"POST:/iam/position/batchUpdateUserPositionRelations,GET:/iam/position/listUserPositions/{userType}/{userId},GET:/iam/positionkvList\",\"sortId\":\"36\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"添加岗位\",\"resourceCode\":\"addPosition\",\"apiSet\":\"POST:/iam/position/\",\"sortId\":\"35\"}]}]}"
        };
        // 插入多层级菜单权限初始数据
        try {
            for (String resourcePermissionJson : RESOURCE_PERMISSION_DATA) {
                IamResourcePermissionListVO permissionListVO = JSON.toJavaObject(resourcePermissionJson, IamResourcePermissionListVO.class);
                if (V.isEmpty(permissionListVO.getAppModule())) {
                    permissionListVO.setAppModule(applicationName);
                }
                permissionListVO.getChildren().forEach(c->{
                    if (V.isEmpty(c.getAppModule())) {
                        c.setAppModule(applicationName);
                    }
                    c.getChildren().forEach(c2->{
                        if (V.isEmpty(c2.getAppModule())) {
                            c2.setAppModule(applicationName);
                        }
                    });
                });
                ContextHelper.getBean(IamResourcePermissionService.class).deepCreatePermissionAndChildren(permissionListVO);
            }
            RESOURCE_PERMISSION_DATA = null;
        } catch (BusinessException e){
            log.error("初始化菜单权限数据出错，请手动配置菜单初始的权限数据", e.getMessage());
        }

        IamOrg iamOrg = new IamOrg();
        iamOrg.setCode("ROOT").setDepth(1).setTopOrgId(1L).setName("我的公司").setShortName("我的公司").setType(Cons.DICTCODE_ORG_TYPE.COMP.name()).setOrgComment("初始根节点，请按需修改")
                .setId(1L);
        ContextHelper.getBaseMapperByEntity(IamOrg.class).insert(iamOrg);

        // 插入超级管理员用户及角色
        IamRole iamRole = new IamRole();
        iamRole.setName("系统管理员").setCode(Cons.ROLE_SYSTEM_ADMIN);
        ContextHelper.getBean(IamRoleService.class).createEntity(iamRole);

        //绑定系统管理员角色权限
        Wrapper<IamResourcePermission> wrapper = new QueryWrapper<IamResourcePermission>().lambda()
                .eq(IamResourcePermission::getAppModule, applicationName);
        List<Long> resourceIds = ContextHelper.getBean(IamResourcePermissionService.class).getValuesOfField(wrapper, IamResourcePermission::getId);
        ContextHelper.getBean(IamRoleResourceService.class).createRoleResourceRelations(iamRole.getId(), resourceIds);

        // 添加系统管理员
        IamUser iamUser = new IamUser();
        iamUser.setOrgId(0L).setRealname("DIBOOT").setUserNum("0000").setGender("M").setMobilePhone("10000000000");
        ContextHelper.getBean(IamUserService.class).createEntity(iamUser);

        // 添加用户角色关联
        IamUserRole iamUserRole = new IamUserRole(IamUser.class.getSimpleName(), iamUser.getId(), iamRole.getId());
        ContextHelper.getBean(IamUserRoleService.class).getMapper().insert(iamUserRole);

        // 创建账号
        IamAccount iamAccount = new IamAccount();
        iamAccount.setUserType(IamUser.class.getSimpleName()).setUserId(iamUser.getId())
                .setAuthType(Cons.DICTCODE_AUTH_TYPE.PWD.name())
                .setAuthAccount("admin").setAuthSecret("123456");
        ContextHelper.getBean(IamAccountService.class).createEntity(iamAccount);
    }
}