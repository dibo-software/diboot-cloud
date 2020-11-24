package com.diboot.cloud.iam.controller.iam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.cloud.annotation.BindPermission;
import com.diboot.cloud.annotation.Log;
import com.diboot.cloud.annotation.Operation;
import com.diboot.cloud.config.Cons;
import com.diboot.cloud.dto.IamResourcePermissionDTO;
import com.diboot.cloud.entity.IamResourcePermission;
import com.diboot.cloud.iam.service.IamResourcePermissionService;
import com.diboot.cloud.redis.RedisCons;
import com.diboot.cloud.vo.IamResourcePermissionListVO;
import com.diboot.cloud.vo.IamResourcePermissionVO;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
* 前端资源权限相关Controller
* @author Mazc
* @version 2.0
* @date 2020-11-06
* Copyright © dibo.ltd
*/
@RestController
@RequestMapping("/iam/resourcePermission")
@BindPermission(name = "系统资源权限")
public class IamResourcePermissionController extends BaseCrudRestController<IamResourcePermission> {
    private static final Logger log = LoggerFactory.getLogger(IamResourcePermissionController.class);

    @Autowired
    private IamResourcePermissionService iamResourcePermissionService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

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
    public JsonResult getViewObjectListMapping(IamResourcePermission entity) throws Exception{
        QueryWrapper<IamResourcePermission> queryWrapper = super.buildQueryWrapper(entity);
        queryWrapper.lambda().orderByDesc(IamResourcePermission::getSortId, IamResourcePermission::getId);
        List<IamResourcePermissionListVO> voList = iamResourcePermissionService.getViewObjectList(queryWrapper, null, IamResourcePermissionListVO.class);
        voList = BeanUtils.buildTree(voList);
        return JsonResult.OK(voList);
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
        return super.getViewObject(id, IamResourcePermissionVO.class);
    }

    /***
    * 新建菜单项、按钮/权限列表
    * @param iamResourcePermissionDTO
    * @return
    * @throws Exception
    */
    @Log(operation = Operation.LABEL_CREATE)
    @BindPermission(name = Operation.LABEL_CREATE, code = Operation.CODE_CREATE)
    @PostMapping("/")
    public JsonResult createEntityMapping(@Valid @RequestBody IamResourcePermissionDTO iamResourcePermissionDTO) throws Exception {
        iamResourcePermissionService.createMenuAndPermissions(iamResourcePermissionDTO);
        return JsonResult.OK();
    }

    /***
    * 更新用户、账号和用户角色关联列表
    * @param iamResourcePermissionDTO
    * @return JsonResult
    * @throws Exception
    */
    @Log(operation = Operation.LABEL_UPDATE)
    @BindPermission(name = Operation.LABEL_UPDATE, code = Operation.CODE_UPDATE)
    @PutMapping("/{id}")
    public JsonResult updateEntityMapping(@PathVariable("id") Long id, @Valid @RequestBody IamResourcePermissionDTO iamResourcePermissionDTO) throws Exception {
        iamResourcePermissionService.updateMenuAndPermissions(iamResourcePermissionDTO);
        return JsonResult.OK();
    }

    /***
    * 删除用户、账号和用户角色关联列表
    * @param id
    * @return
    * @throws Exception
    */
    @Log(operation = Operation.LABEL_DELETE)
    @BindPermission(name = Operation.LABEL_DELETE, code = Operation.CODE_DELETE)
    @DeleteMapping("/{id}")
    public JsonResult deleteEntityMapping(@PathVariable("id")Long id) throws Exception {
        iamResourcePermissionService.deleteMenuAndPermissions(id);
        return JsonResult.OK();
    }

    /***
    * 加载更多数据
    * @return
    * @throws Exception
    */
    @GetMapping("/attachMore")
    public JsonResult attachMore(ModelMap modelMap) throws Exception{
        // 获取关联表数据IamResourcePermission的树状列表
        List<IamResourcePermissionListVO> menuList = iamResourcePermissionService.getViewObjectList(
            Wrappers.<IamResourcePermission>lambdaQuery().eq(IamResourcePermission::getDisplayType, Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.MENU.name()),
            null,
            IamResourcePermissionListVO.class
        );
        menuList = BeanUtils.buildTree(menuList);
        modelMap.put("menuList", menuList);
        // 获取关联数据字典USER_STATUS的KV
        List<KeyValue> resourcePermissionCodeKvList = dictionaryService.getKeyValueList(Cons.DICTTYPE.RESOURCE_PERMISSION_CODE.name());
        modelMap.put("resourcePermissionCodeKvList", resourcePermissionCodeKvList);
        return JsonResult.OK(modelMap);
    }

    /**
    * 列表排序
    * @param permissionList
    * @return
    * @throws Exception
    */
    @PostMapping("/sortList")
    @BindPermission(name="列表排序", code = Operation.CODE_UPDATE)
    public JsonResult sortList(@RequestBody List<IamResourcePermission> permissionList) throws Exception {
        iamResourcePermissionService.sortList(permissionList);
        return JsonResult.OK().msg("更新成功");
    }

    /***
    * api接口列表（供前端选择）
    * @return
    * @throws Exception
    */
    @GetMapping("/apiList")
    public JsonResult apiList() throws Exception{
        Map<Object, Object> moduleToPermissionsMap = redisTemplate.opsForHash().entries(RedisCons.KEY_APP_MODULE_PERMISSIONS_MAP);
        return JsonResult.OK(moduleToPermissionsMap);
    }

    /***
    * 检查菜单编码是否重复
    * @param id
    * @param code
    * @return
    */
    @GetMapping("/checkCodeDuplicate")
    public JsonResult checkCodeDuplicate(@RequestParam(required = false) Long id, @RequestParam String code) {
        if (V.notEmpty(code)) {
            LambdaQueryWrapper<IamResourcePermission> wrapper = Wrappers.<IamResourcePermission>lambdaQuery()
                .select(IamResourcePermission::getId)
                .eq(IamResourcePermission::getResourceCode, code)
                .eq(IamResourcePermission::getDisplayType, Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.MENU.name());
            if (V.notEmpty(id)) {
                wrapper.ne(IamResourcePermission::getId, id);
            }
            boolean exists = iamResourcePermissionService.exists(wrapper);
            if (exists) {
                return JsonResult.FAIL_VALIDATION("编码已存在: "+code);
            }
        }
        return JsonResult.OK();
    }

}