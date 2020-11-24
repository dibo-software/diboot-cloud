package com.diboot.cloud.iam.vo;

import com.diboot.cloud.iam.entity.IamOrg;
import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.binding.annotation.BindField;

import java.util.List;

/**
 * 部门 DetailVO定义
 * @author Mazc
 * @version 2.0
 * @date 2020-11-17
 * Copyright © dibo.ltd
 */
public class IamOrgDetailVO extends IamOrg {

    private static final long serialVersionUID = -4970426744871827252L;

    /**
     * 关联字段：IamOrg.shortName
     */
    @BindField(entity = IamOrg.class, field = "shortName", condition = "this.parent_id=id")
    private String parentName;

    // 绑定parentList
    @BindEntityList(entity = IamOrg.class, condition = "this.id=parent_id")
    private List<IamOrg> children;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public List<IamOrg> getChildren() {
        return (children);
    }

    public IamOrgDetailVO setChildren(List<IamOrg> children) {
        this.children = children;
        return this;
    }
}
