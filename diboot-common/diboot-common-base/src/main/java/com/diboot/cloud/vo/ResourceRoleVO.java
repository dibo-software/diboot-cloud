package com.diboot.cloud.vo;

import com.diboot.core.binding.annotation.BindFieldList;
import com.diboot.cloud.entity.IamResourcePermission;
import com.diboot.cloud.entity.IamRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 前端资源与角色集合的VO
 * @author mazc
 * @version v1.0
 * @date 2020/09/09
 */
@Getter @Setter
public class ResourceRoleVO extends IamResourcePermission {

    @BindFieldList(entity = IamRole.class, field = "code", condition = "this.id=iam_role_resource.resource_id AND iam_role_resource.role_id=id")
    private List<String> roleCodes;

}
