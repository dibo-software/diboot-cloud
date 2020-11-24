package com.diboot.cloud.iam.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.cloud.annotation.BindPermission;
import com.diboot.cloud.annotation.Log;
import com.diboot.cloud.annotation.Operation;
import com.diboot.cloud.iam.dto.IamOrgDTO;
import com.diboot.cloud.iam.entity.IamOrg;
import com.diboot.cloud.iam.service.IamOrgService;
import com.diboot.cloud.iam.vo.IamOrgDetailVO;
import com.diboot.cloud.iam.vo.IamOrgListVO;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 部门 相关Controller
 * @author Mazc
 * @version 2.0
 * @date 2020-11-17
 * Copyright © dibo.ltd
 */
@RestController
@RequestMapping("/iam/org")
@BindPermission(name = "部门")
public class IamOrgController extends BaseCrudRestController<IamOrg> {

    private static final Logger log = LoggerFactory.getLogger(IamOrgController.class);

    @Autowired
    private IamOrgService iamOrgService;

    /**
     * 查询ViewObject的分页数据
     * <p>
     * url请求参数示例: /list?field=abc&pageIndex=1&orderBy=abc:DESC
     * </p>
     * @return
     * @throws Exception
     */
    @Log(operation = Operation.LABEL_LIST)
    @BindPermission(name = Operation.LABEL_LIST, code = Operation.CODE_LIST)
    @GetMapping("/list")
    public JsonResult getViewObjectListMapping(IamOrgDTO queryDto, Pagination pagination) throws Exception {
        QueryWrapper<IamOrg> queryWrapper = super.buildQueryWrapper(queryDto);
        queryWrapper.lambda().eq(IamOrg::getParentId, 0L).orderByDesc(IamOrg::getSortId).orderByDesc(IamOrg::getId);
        List<IamOrgListVO> voList = this.getService().getViewObjectList(queryWrapper, pagination, IamOrgListVO.class);
        return JsonResult.OK(voList).bindPagination(pagination);
    }

    /**
     * 根据资源id查询ViewObject
     * @param id ID
     * @return
     * @throws Exception
     */
    @Log(operation = Operation.LABEL_DETAIL)
    @BindPermission(name = Operation.LABEL_DETAIL, code = Operation.CODE_DETAIL)
    @GetMapping("/{id}")
    public JsonResult getViewObjectMapping(@PathVariable("id") Long id) throws Exception {
        return super.getViewObject(id, IamOrgDetailVO.class);
    }

    /**
     * 创建资源对象
     * @param entity
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = Operation.LABEL_CREATE)
    @BindPermission(name = Operation.LABEL_CREATE, code = Operation.CODE_CREATE)
    @PostMapping("/")
    public JsonResult createEntityMapping(@Valid @RequestBody IamOrg entity) throws Exception {
        return super.createEntity(entity);
    }

    /**
     * 根据ID更新资源对象
     * @param entity
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = Operation.LABEL_UPDATE)
    @BindPermission(name = Operation.LABEL_UPDATE, code = Operation.CODE_UPDATE)
    @PutMapping("/{id}")
    public JsonResult updateEntityMapping(@PathVariable("id") Long id, @Valid @RequestBody IamOrg entity) throws Exception {
        return super.updateEntity(id, entity);
    }

    /**
     * 根据id删除资源对象
     * @param id
     * @return
     * @throws Exception
     */
    @Log(operation = Operation.LABEL_DELETE)
    @BindPermission(name = Operation.LABEL_DELETE, code = Operation.CODE_DELETE)
    @DeleteMapping("/{id}")
    public JsonResult deleteEntityMapping(@PathVariable("id") Long id) throws Exception {
        return super.deleteEntity(id);
    }

    /**
     * 获取部门根节点的组织树
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/tree")
    @BindPermission(name = "查看树列表", code = "tree")
    public JsonResult getRootNodeTree() throws Exception {
        List<IamOrgDetailVO> iamOrgVOList = iamOrgService.getIamOrgTree(0L);
        return JsonResult.OK(iamOrgVOList);
    }

    @PostMapping("/sortList")
    @BindPermission(name = "列表排序", code = "sortList")
    public JsonResult sortList(@RequestBody List<IamOrg> orgList) throws Exception {
        iamOrgService.sortList(orgList);
        return JsonResult.OK().msg("更新成功");
    }

    @Override
    protected String beforeCreate(Object entity) throws Exception {
        initIamOrg(entity);
        return null;
    }

    @Override
    protected String beforeUpdate(Object entity) throws Exception {
        initIamOrg(entity);
        return null;
    }

    @Override
    protected String beforeDelete(Object entityOrDto) throws Exception {
        IamOrg iamOrg = (IamOrg) entityOrDto;
        // 判断是否具有叶子节点（仅允许没有叶子节点的节点进行删除操作）
        LambdaQueryWrapper<IamOrg> wrapper = new LambdaQueryWrapper<IamOrg>();
        wrapper.eq(IamOrg::getParentId, iamOrg.getPrimaryKey());
        Integer count = iamOrgService.getEntityListCount(wrapper);
        if (count > 0) {
            return "请先删除当前节点下的所有下级节点再重试";
        }
        return null;
    }

    /**
     * 初始化IamOrg实体
     * @param entity
     * @throws Exception
     */
    private void initIamOrg(Object entity) throws Exception {
        IamOrg iamOrg = (IamOrg) entity;
        // 设置层级及公司ID
        if (iamOrg.getParentId() != null && !V.equals(iamOrg.getParentId(), 0L)) {
            IamOrg parentOrg = iamOrgService.getEntity(iamOrg.getParentId());
            if (parentOrg != null) {
                // 设置层级
                int parentLevel = parentOrg.getLevel().intValue();
                iamOrg.setLevel(++parentLevel);
            }
        }
    }
}
