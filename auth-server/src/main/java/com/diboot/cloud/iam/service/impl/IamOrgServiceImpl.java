package com.diboot.cloud.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.cloud.iam.entity.IamOrg;
import com.diboot.cloud.iam.mapper.IamOrgMapper;
import com.diboot.cloud.iam.service.IamOrgService;
import com.diboot.cloud.iam.vo.IamOrgDetailVO;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门相关Service实现
 * @author Mazc
 * @version 2.0
 * @date 2020-11-17
 * Copyright © dibo.ltd
 */
@Service
public class IamOrgServiceImpl extends BaseServiceImpl<IamOrgMapper, IamOrg> implements IamOrgService {

    private static final Logger log = LoggerFactory.getLogger(IamOrgServiceImpl.class);

    @Override
    public List<IamOrgDetailVO> getIamOrgTree(Long rootId) {
        QueryWrapper<IamOrg> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .orderByDesc(IamOrg::getSortId)
                .orderByDesc(IamOrg::getId);
        List<IamOrg> iamOrgList = getEntityList(queryWrapper);
        List<IamOrgDetailVO> iamOrgDetailVOList = BeanUtils.convertList(iamOrgList, IamOrgDetailVO.class);
        return BeanUtils.buildTree(iamOrgDetailVOList, rootId);
    }

    @Override
    public void sortList(List<IamOrg> orgList) {
        if (V.isEmpty(orgList)) {
            throw new BusinessException(Status.FAIL_OPERATION, "排序列表不能为空");
        }
        List<Long> sortIdList = new ArrayList();
        // 先将所有序号重新设置为自身当前id
        for (IamOrg item : orgList) {
            item.setSortId(item.getId());
            sortIdList.add(item.getSortId());
        }
        // 将序号列表倒序排序
        sortIdList = sortIdList.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        // 整理需要更新的列表
        List<IamOrg> updateList = new ArrayList<>();
        for (int i=0; i<orgList.size(); i++) {
            IamOrg item = orgList.get(i);
            IamOrg updateItem = new IamOrg();
            updateItem.setId(item.getId());
            updateItem.setSortId(sortIdList.get(i));
            updateList.add(updateItem);
        }
        if (updateList.size() > 0) {
            super.updateBatchById(updateList);
        }
    }
}
