package com.diboot.cloud.iam.controller.iam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.cloud.entity.LoginUserDetail;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.cloud.annotation.Log;
import com.diboot.cloud.annotation.Operation;
import com.diboot.cloud.dto.IamPositionFormDTO;
import com.diboot.cloud.dto.IamUserPositionBatchDTO;
import com.diboot.cloud.entity.IamPosition;
import com.diboot.cloud.entity.IamUser;
import com.diboot.cloud.entity.IamUserPosition;
import com.diboot.cloud.iam.service.IamPositionService;
import com.diboot.cloud.util.IamSecurityUtils;
import com.diboot.cloud.vo.IamPositionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
* 岗位 相关Controller
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
@RestController
@RequestMapping("/iam/position")
@Slf4j
public class IamPositionController extends BaseCrudRestController<IamPosition> {

    @Autowired
    private IamPositionService iamPositionService;

    /***
     * 获取所有岗位列表
     * @param entity
     * @param pagination
     * @return
     * @throws Exception
     */
    @GetMapping("/list")
    public JsonResult getViewObjectListMapping(IamPosition entity, Pagination pagination) throws Exception{
        return super.getViewObjectList(entity, pagination, IamPositionVO.class);
    }

    /***
     * 根据用户信息获取岗位列表
     * @param userType
     * @param userId
     * @return
     */
    @GetMapping("/listUserPositions/{userType}/{userId}")
    public JsonResult listUserPositionsByUser(@PathVariable("userType") String userType, @PathVariable("userId") Long userId) {
        List<IamUserPosition> userPositionList = iamPositionService.getUserPositionListByUser(userType, userId);
        return JsonResult.OK(userPositionList);
    }

    /***
     * 根据资源id查询ViewObject
     * @param id ID
     * @return
     * @throws Exception
     */
    @GetMapping("/{id}")
    public JsonResult getViewObjectWithMapping(@PathVariable("id") Long id) throws Exception{
        return super.getViewObject(id, IamPositionVO.class);
    }

    /***
     * 创建资源对象
     * @param entity
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = Operation.LABEL_CREATE)
    @PostMapping("/")
    public JsonResult createEntityWithMapping(@RequestBody @Valid IamPositionFormDTO entity) throws Exception {
        return super.createEntity(entity);
    }

    /***
     * 根据ID更新资源对象
     * @param entity
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = Operation.LABEL_UPDATE)
    @PutMapping("/{id}")
    public JsonResult updateEntityWithMapping(@PathVariable("id")Long id, @Valid @RequestBody IamPositionFormDTO entity) throws Exception {
        return super.updateEntity(id, entity);
    }


    /***
     * 删除岗位
     * @param id
     * @return
     * @throws Exception
     */
    @Log(operation = Operation.LABEL_DELETE)
    @DeleteMapping("/{id}")
    public JsonResult deleteEntity(@PathVariable("id")Long id) throws Exception {
        return super.deleteEntity(id);
    }

    /***
     * 获取所有键值列表
     * @return
     * @throws Exception
     */
    @GetMapping("kvList")
    public JsonResult getKvList() throws Exception{
        List<KeyValue> kvList = iamPositionService.getKeyValueList(Wrappers.<IamPosition>lambdaQuery().select(IamPosition::getId, IamPosition::getName));
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
            LambdaQueryWrapper<IamPosition> wrapper = Wrappers.<IamPosition>lambdaQuery()
                    .select(IamPosition::getId).eq(IamPosition::getCode, code);
            if (V.notEmpty(id)) {
                wrapper.ne(IamPosition::getId, id);
            }
            boolean exists = iamPositionService.exists(wrapper);
            if (exists) {
                return new JsonResult(Status.FAIL_OPERATION, "编码已存在: "+code);
            }
        }
        return JsonResult.OK();
    }

    /***
     * 批量更新关联关系
     * @param userPositionBatchDTO
     * @return
     * @throws Exception
     */
    @Log(operation = "设置用户岗位关系")
    @PostMapping("/batchUpdateUserPositionRelations")
    public JsonResult batchUpdate(@RequestBody IamUserPositionBatchDTO userPositionBatchDTO) throws Exception {
        iamPositionService.updateUserPositionRelations(userPositionBatchDTO.getUserType(), userPositionBatchDTO.getUserId(), userPositionBatchDTO.getUserPositionList());
        return JsonResult.OK();
    }

    /***
     * 切换当前岗位
     * @return
     * @throws Exception
     */
    @GetMapping("/userPositions")
    public JsonResult currentUserPositions() throws Exception {
        LoginUserDetail currentUser = IamSecurityUtils.getCurrentUser();
        List<IamPosition> userPositions = iamPositionService.getPositionListByUser(IamUser.class.getSimpleName(), currentUser.getUserId());
        return JsonResult.OK(userPositions);
    }

}