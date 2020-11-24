package com.diboot.cloud.iam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 部门 Entity定义
 * @author Mazc
 * @version 2.0
 * @date 2020-11-17
 * Copyright © dibo.ltd
 */
public class IamOrg extends BaseEntity {

    private static final long serialVersionUID = -9006189591419798269L;

    /**
     * 上级ID
     */
    @TableField()
    private Long parentId;

    /**
     * 名称
     */
    @NotNull(message = "名称不能为空")
    @Length(max = 100, message = "名称长度应小于100")
    @TableField()
    private String name;

    /**
     * 简称
     */
    @NotNull(message = "简称不能为空")
    @Length(max = 50, message = "简称长度应小于50")
    @TableField()
    private String shortName;

    /**
     * 备注
     */
    @Length(max = 255, message = "备注长度应小于255")
    @TableField()
    private String orgComment;

    /**
     * 层级
     */
    @TableField()
    private Integer level;

    /**
     * 排序号
     */
    @TableField()
    private Long sortId;

    public Long getParentId() {
        return parentId;
    }

    public IamOrg setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getName() {
        return name;
    }

    public IamOrg setName(String name) {
        this.name = name;
        return this;
    }

    public String getShortName() {
        return shortName;
    }

    public IamOrg setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public Integer getLevel() {
        return level;
    }

    public IamOrg setLevel(Integer level) {
        this.level = level;
        return this;
    }

    public Long getSortId() {
        return sortId;
    }

    public IamOrg setSortId(Long sortId) {
        this.sortId = sortId;
        return this;
    }

    public String getOrgComment() {
        return (orgComment);
    }

    public IamOrg setOrgComment(String orgComment) {
        this.orgComment = orgComment;
        return this;
    }
}
