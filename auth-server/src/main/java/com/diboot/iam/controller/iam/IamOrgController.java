package com.diboot.iam.controller.iam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.Operation;
import com.diboot.cloud.config.Cons;
import com.diboot.iam.dto.IamOrgDTO;
import com.diboot.iam.entity.IamOrg;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.service.IamOrgService;
import com.diboot.iam.service.IamUserService;
import com.diboot.iam.vo.IamOrgVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 组织机构 相关Controller
 *
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019-12-03
 */
@RestController
@RequestMapping("/iam/org")
@Slf4j
public class IamOrgController extends BaseCrudRestController<IamOrg> {

    @Autowired
    private IamOrgService iamOrgService;
    @Autowired
    private IamUserService iamUserService;

    /**
     * 查询ViewObject的分页数据
     * <p>
     * url请求参数示例: /list?field=abc&pageSize=20&pageIndex=1&orderBy=id
     * </p>
     * @return
     * @throws Exception
     */
    @Log(operation = Operation.LABEL_LIST)
    @BindPermission(name = Operation.LABEL_LIST, code = Operation.CODE_LIST)
    @GetMapping("/list")
    public JsonResult getViewObjectListWithMapping(IamOrg entity, Pagination pagination) throws Exception {
        QueryWrapper<IamOrg> queryWrapper = super.buildQueryWrapper(entity);
        queryWrapper.lambda().orderByDesc(IamOrg::getSortId).orderByDesc(IamOrg::getId);
        List<IamOrgVO> voList = this.getService().getViewObjectList(queryWrapper, pagination, IamOrgVO.class);
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
        return super.getViewObject(id, IamOrgVO.class);
    }

    /**
     * 新建组织信息
     * @param entity
     * @return
     * @throws Exception
     */
    @Log(operation = Operation.LABEL_CREATE)
    @BindPermission(name = Operation.LABEL_CREATE, code = Operation.CODE_CREATE)
    @PostMapping("/")
    public JsonResult createEntityMapping(@Valid @RequestBody IamOrg entity) throws Exception {
        return super.createEntity(entity);
    }

    /**
     * 更新组织信息
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
     * 删除组织结构
     * @param id
     * @return
     * @throws Exception
     */
    @Log(operation = Operation.LABEL_DELETE)
    @BindPermission(name = Operation.LABEL_DELETE, code = Operation.CODE_DELETE)
    @DeleteMapping("/{id}")
    public JsonResult deleteEntityWithMapping(@PathVariable("id") Long id) throws Exception {
        // 仅允许叶子节点的节点进行删除操作
        boolean existChildren = iamOrgService.exists(IamOrg::getParentId, id);
        if (existChildren) {
            return JsonResult.FAIL_OPERATION("该部门存在子部门，不允许删除");
        }
        boolean hasUser = iamUserService.exists(IamUser::getOrgId, id);
        if (hasUser) {
            return JsonResult.FAIL_OPERATION("该部门下存在有效用户，不允许删除");
        }
        return deleteEntity(id);
    }

    /**
     * 获取根节点的组织树
     * @return
     * @throws Exception
     */
    @BindPermission(name = "获取组织树", code = "tree")
    @GetMapping("/tree")
    public JsonResult getRootNodeOrgTree() throws Exception {
        List<IamOrgVO> orgVOList = iamOrgService.getOrgTree(IamOrg.VIRTUAL_ROOT_ID);
        return JsonResult.OK(orgVOList);
    }

    /**
     * 获取指定节点的子节点
     * @param parentNodeId
     * @return
     * @throws Exception
     */
    @BindPermission(name = "获取指定节点组织树", code = "subTree")
    @GetMapping("/tree/{parentNodeId}")
    public JsonResult getOrgChildNodes(@PathVariable("parentNodeId") Long parentNodeId) throws Exception {
        List<IamOrgVO> orgVOList = iamOrgService.getOrgTree(parentNodeId);
        return JsonResult.OK(orgVOList);
    }

    /**
     * 获取指定节点的子列表
     * @return
     * @throws Exception
     */
    @BindPermission(name = "获取子组织列表", code = "children")
    @GetMapping("/childrenList/{parentNodeId}")
    public JsonResult getOrgChildList(@PathVariable("parentNodeId") Long parentNodeId, IamOrgDTO iamOrgDTO, Pagination pagination) throws Exception {
        QueryWrapper<IamOrg> wrapper = super.buildQueryWrapperByQueryParams(iamOrgDTO);
        if (parentNodeId != null && !V.equals(parentNodeId, 0L)) {
            wrapper.lambda().eq(IamOrg::getParentId, parentNodeId);
        }
        return super.getEntityListWithPaging(wrapper, pagination);
    }

    /**
     * 列表排序
     * @param orgList
     * @return
     * @throws Exception
     */
    @Log(operation = "列表排序")
    @BindPermission(name = "列表排序", code = "sort")
    @PostMapping("/sortList")
    public JsonResult sortList(@RequestBody List<IamOrg> orgList) throws Exception {
        iamOrgService.sortList(orgList);
        return JsonResult.OK().msg("更新成功");
    }

    /**
     * 检查code是否重复
     * @param id
     * @param code
     * @return
     */
    @GetMapping("/checkCodeDuplicate")
    public JsonResult checkCodeDuplicate(@RequestParam(required = false) Long id, @RequestParam String code) {
        if (V.notEmpty(code)) {
            LambdaQueryWrapper<IamOrg> wrapper = Wrappers.<IamOrg>lambdaQuery().select(IamOrg::getId).eq(IamOrg::getCode, code);
            if (V.notEmpty(id)) {
                wrapper.ne(IamOrg::getId, id);
            }
            boolean exists = iamOrgService.exists(wrapper);
            if (exists) {
                return new JsonResult(Status.FAIL_OPERATION, "编码已存在: " + code);
            }
        }
        return JsonResult.OK();
    }

    /**
     * 加载更多数据
     * @return
     * @throws Exception
     */
    @GetMapping("/attachMore")
    public JsonResult attachMore(ModelMap modelMap) throws Exception {
        // 获取关联数据字典ORG_TYPE的KV
        List<KeyValue> orgTypeKvList = dictionaryService.getKeyValueList(Cons.DICTTYPE.ORG_TYPE.name());
        modelMap.put("orgTypeKvList", orgTypeKvList);
        Map<String, KeyValue> orgTypeKvMap = BeanUtils.convertToStringKeyObjectMap(orgTypeKvList, BeanUtils.convertToFieldName(KeyValue::getV));
        modelMap.put("orgTypeKvMap", orgTypeKvMap);

        return JsonResult.OK(modelMap);
    }

}
