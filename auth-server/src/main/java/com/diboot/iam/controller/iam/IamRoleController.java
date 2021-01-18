package com.diboot.iam.controller.iam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.Operation;
import com.diboot.cloud.config.Cons;
import com.diboot.iam.dto.IamRoleFormDTO;
import com.diboot.iam.entity.IamResourcePermission;
import com.diboot.iam.entity.IamRole;
import com.diboot.iam.service.IamResourcePermissionService;
import com.diboot.iam.service.IamRoleResourceService;
import com.diboot.iam.service.IamRoleService;
import com.diboot.iam.vo.IamResourcePermissionListVO;
import com.diboot.iam.vo.IamRoleVO;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
* 角色相关Controller
* @author Mazc
* @version 2.0
* @date 2020-11-16
* Copyright © dibo.ltd
*/
@RestController
@RequestMapping("/iam/role")
@BindPermission(name = "角色")
public class IamRoleController extends BaseCrudRestController<IamRole> {
    private static final Logger log = LoggerFactory.getLogger(IamRoleController.class);

    @Autowired
    private IamRoleService iamRoleService;

    @Autowired
    private IamResourcePermissionService iamResourcePermissionService;

    @Autowired
    private IamRoleResourceService iamRoleResourceService;

    /***
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
    public JsonResult getViewObjectListMapping(IamRole entity, Pagination pagination) throws Exception{
        return super.getViewObjectList(entity, pagination, IamRoleVO.class);
    }

    /***
    * 根据资源id查询ViewObject
    * @param id ID
    * @return
    * @throws Exception
    */
    @Log(operation = Operation.LABEL_DETAIL)
    @BindPermission(name = Operation.LABEL_DETAIL, code = Operation.CODE_DETAIL)
    @GetMapping("/{id}")
    public JsonResult getViewObjectMapping(@PathVariable("id")Long id) throws Exception{
        IamRoleVO roleVO = iamRoleService.getViewObject(id, IamRoleVO.class);
        if (V.notEmpty(roleVO.getPermissionList())){
            List<Long> permissionIdList = BeanUtils.collectIdToList(roleVO.getPermissionList());
            List<IamResourcePermissionListVO> permissionVOList = iamResourcePermissionService.getViewObjectList(
                Wrappers.<IamResourcePermission>lambdaQuery().in(IamResourcePermission::getId, permissionIdList),
                null,
                IamResourcePermissionListVO.class
            );
            permissionVOList = BeanUtils.buildTree(permissionVOList);
            roleVO.setPermissionVOList(permissionVOList);
        }
        return JsonResult.OK(roleVO);
    }

    /***
    * 新建角色和角色权限关联列表
    * @param roleFormDTO
    * @return
    * @throws Exception
    */
		@Log(operation = Operation.LABEL_CREATE)
    @BindPermission(name = Operation.LABEL_CREATE, code = Operation.CODE_CREATE)
    @PostMapping("/")
    public JsonResult createEntityMapping(@Valid @RequestBody IamRoleFormDTO roleFormDTO) throws Exception {
        return super.createEntity(roleFormDTO);
    }

    /***
    * 更新角色和角色权限关联列表
    * @param roleFormDTO
    * @return JsonResult
    * @throws Exception
    */
		@Log(operation = Operation.LABEL_UPDATE)
    @BindPermission(name = Operation.LABEL_UPDATE, code = Operation.CODE_UPDATE)
    @PutMapping("/{id}")
    public JsonResult updateEntityMapping(@PathVariable("id") Long id, @Valid @RequestBody IamRoleFormDTO roleFormDTO) throws Exception {
        return super.updateEntity(id, roleFormDTO);
    }

    /***
    * 根据id删除资源对象
    * @param id
    * @return
    * @throws Exception
    */
		@Log(operation = Operation.LABEL_DELETE)
    @BindPermission(name = Operation.LABEL_DELETE, code = Operation.CODE_DELETE)
    @DeleteMapping("/{id}")
    public JsonResult deleteEntityMapping(@PathVariable("id")Long id) throws Exception {
        return super.deleteEntity(id);
    }

    /***
    * 获取所有角色键值列表
    * @return
    * @throws Exception
    */
    @GetMapping("kvList")
    public JsonResult getKvList() throws Exception{
    List<KeyValue> kvList = iamRoleService.getKeyValueList(Wrappers.<IamRole>lambdaQuery().select(IamRole::getId, IamRole::getName));
        return JsonResult.OK(kvList);
    }

    /***
    * 检查编码是否重复
    * @param id
    * @param code
    * @return
    */
    @GetMapping("/checkCodeDuplicate")
    public JsonResult checkCodeDuplicate(@RequestParam(required = false) Long id, @RequestParam String code) {
        if (V.notEmpty(code)) {
            LambdaQueryWrapper<IamRole> wrapper = Wrappers.<IamRole>lambdaQuery()
                .select(IamRole::getId).eq(IamRole::getCode, code);
            if (V.notEmpty(id)) {
                wrapper.ne(IamRole::getId, id);
            }
            boolean exists = iamRoleService.exists(wrapper);
            if (exists) {
                return JsonResult.FAIL_VALIDATION("编码已存在: "+code);
            }
        }
        return JsonResult.OK();
    }

    @Override
    protected String beforeUpdate(Object entity) throws Exception {
        IamRoleFormDTO roleFormDTO = (IamRoleFormDTO) entity;
        if (Cons.ROLE_SYSTEM_ADMIN.equals(roleFormDTO.getCode())){
            throw new BusinessException(Status.FAIL_OPERATION, "不能更新系统管理员角色");
        }
        return null;
    }

    @Override
    protected String beforeDelete(Object entity) throws Exception {
        IamRole role = (IamRole)entity;
        if (Cons.ROLE_SYSTEM_ADMIN.equals(role.getCode())){
            throw new BusinessException(Status.FAIL_OPERATION, "不能删除系统管理员角色");
        }
        return null;
    }

    @Override
    protected void afterCreated(Object entity) throws Exception {
        IamRoleFormDTO roleFormDTO = (IamRoleFormDTO) entity;
        iamRoleResourceService.createRoleResourceRelations(roleFormDTO.getId(), roleFormDTO.getPermissionIdList());
    }

    @Override
    protected void afterUpdated(Object entity) throws Exception {
        IamRoleFormDTO roleFormDTO = (IamRoleFormDTO) entity;
        iamRoleResourceService.updateRoleResourceRelations(roleFormDTO.getId(), roleFormDTO.getPermissionIdList());
    }
}