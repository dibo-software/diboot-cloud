package com.diboot.cloud.iam.dto;

import com.diboot.cloud.iam.entity.IamOrg;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;

/**
 * 部门 DTO定义
 * @author Mazc
 * @version 2.0
 * @date 2020-11-17
 * Copyright © dibo.ltd
 */
public class IamOrgDTO extends IamOrg {

    private static final long serialVersionUID = 930458548906223479L;

    /**
     * 关联字段 IamOrg.shortName
     */
    @BindQuery(comparison = Comparison.EQ, entity = IamOrg.class, field = "shortName", condition = "this.parent_id=id")
    private String parentName;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
