package com.diboot.cloud.iam.service;

import com.diboot.cloud.iam.entity.IamOrg;
import com.diboot.cloud.iam.vo.IamOrgDetailVO;
import com.diboot.core.service.BaseService;

import java.util.List;

/**
 * 部门相关Service
 * @author Mazc
 * @version 2.0
 * @date 2020-11-17
 * Copyright © dibo.ltd
 */
public interface IamOrgService extends BaseService<IamOrg> {

    /**
     * 获取指定部门下的全部节点的组织树
     *
     * @param rootId
     * @return
     */
    List<IamOrgDetailVO> getIamOrgTree(Long rootId);

    /**
     *
     * @param orgList
     */
    void sortList(List<IamOrg> orgList);
}
