package com.diboot.scheduler.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.Operation;
import com.diboot.iam.entity.LoginUserDetail;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.scheduler.entity.ScheduleJob;
import com.diboot.scheduler.entity.ScheduleJobLog;
import com.diboot.scheduler.service.ScheduleJobLogService;
import com.diboot.scheduler.service.ScheduleJobService;
import com.diboot.scheduler.vo.ScheduleJobLogVO;
import com.diboot.scheduler.vo.ScheduleJobVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
* 定时任务相关Controller
*
* @author JerryMa
* @version 1.0
* @date 2020-12-10
* * Copyright © www.dibo.ltd
*/
@RestController
@RequestMapping("/scheduleJob")
@Slf4j
public class ScheduleJobController extends BaseCrudRestController<ScheduleJob> {
    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleJobLogService scheduleJobLogService;

    /***
     * 查询ViewObject的分页数据
     * <p>
     * url请求参数示例: /list?field=abc&pageSize=20&pageIndex=1&orderBy=id
     * </p>
     */
    @Log(operation = Operation.LABEL_LIST)
    @BindPermission(name = Operation.LABEL_LIST, code = Operation.CODE_LIST)
    @GetMapping("/list")
    public JsonResult getJobVOListMapping(ScheduleJob entity, Pagination pagination) throws Exception {
        return super.getViewObjectList(entity, pagination, ScheduleJobVO.class);
    }

    /***
     * 根据资源id查询ViewObject
     * @param id ID
     */
    @Log(operation = Operation.LABEL_DETAIL)
    @BindPermission(name = Operation.LABEL_DETAIL, code = Operation.CODE_DETAIL)
    @GetMapping("/{id}")
    public JsonResult getJobVOMapping(@PathVariable("id") Long id) throws Exception {
        ScheduleJobVO scheduleJob = scheduleJobService.getViewObject(id, ScheduleJobVO.class);
        return JsonResult.OK(scheduleJob);
    }

    /***
     * 新建job
     * @param scheduleJob
     */
    @Log(operation = Operation.LABEL_CREATE)
    @BindPermission(name = Operation.LABEL_CREATE, code = Operation.CODE_CREATE)
    @PostMapping("/")
    public JsonResult createEntityMapping(@Valid @RequestBody ScheduleJob scheduleJob) throws Exception {
        LoginUserDetail currentUser = IamSecurityUtils.getCurrentUser();
        scheduleJob.setCreateBy(currentUser.getUserId());
        return super.createEntity(scheduleJob);
    }

    /***
     * 更新定时任务job
     * @param scheduleJob
     */
    @Log(operation = Operation.LABEL_UPDATE)
    @BindPermission(name = Operation.LABEL_UPDATE, code = Operation.CODE_UPDATE)
    @PutMapping("/{id}")
    public JsonResult updateEntityMapping(@PathVariable("id") Long id, @Valid @RequestBody ScheduleJob scheduleJob) throws Exception {
        return super.updateEntity(id, scheduleJob);
    }

    /***
     * 更新定时任务job状态
     * @param id
     * @param action
     */
    @Log(operation = "更新定时任务状态")
    @BindPermission(name = Operation.LABEL_UPDATE, code = Operation.CODE_UPDATE)
    @PutMapping("/{id}/{action}")
    public JsonResult updateJobStateMapping(@PathVariable("id") Long id, @PathVariable("action") String action) throws Exception {
        scheduleJobService.changeScheduleJobStatus(id, action);
        return JsonResult.OK();
    }

    @Log(operation = "执行一次定时任务")
    @BindPermission(name = Operation.LABEL_UPDATE, code = Operation.CODE_UPDATE)
    @PutMapping("/executeOnce/{id}")
    public JsonResult executeOnce(@PathVariable("id") Long id) throws Exception {
        scheduleJobService.executeOnceJob(id);
        return JsonResult.OK();
    }

    /***
     * 根据id删除资源对象
     * @param id
     */
    @Log(operation = Operation.LABEL_DELETE)
    @BindPermission(name = Operation.LABEL_DELETE, code = Operation.CODE_DELETE)
    @DeleteMapping("/{id}")
    public JsonResult deleteEntityMapping(@PathVariable("id") Long id) throws Exception {
        return super.deleteEntity(id);
    }

    /**
     * job名称列表
     *
     * @return
     */
    @GetMapping("/allJobs")
    public JsonResult getAllJobs() throws Exception {
        return JsonResult.OK(scheduleJobService.getAllJobs());
    }

    /**
     * 获取定时任务日志list
     * @param entity
     * @param pagination
     * @return
     * @throws Exception
     */
    @Log(operation = "定时日志列表")
    @BindPermission(name = "定时日志列表", code = "JOB_LOG_LIST")
    @GetMapping("/log/list")
    public JsonResult getJobLogVOListMapping(ScheduleJobLog entity, Pagination pagination) throws Exception {
        QueryWrapper<ScheduleJobLog> queryWrapper = super.buildQueryWrapper(entity);
        List<ScheduleJobLogVO> logList = scheduleJobLogService.getViewObjectList(queryWrapper, pagination, ScheduleJobLogVO.class);
        return JsonResult.OK(logList).bindPagination(pagination);
    }

    /***
     * 根据定时任务日志id查询ViewObject
     * @param id ID
     * @return
     * @throws Exception
     */
    @Log(operation = "定时日志详情")
    @BindPermission(name = "定时日志详情", code = "JOB_LOG_DETAIL")
    @GetMapping("/log/{id}")
    public JsonResult getJobLogVOMapping(@PathVariable("id") Long id) throws Exception {
        ScheduleJobLogVO jobLog = scheduleJobLogService.getViewObject(id, ScheduleJobLogVO.class);
        return JsonResult.OK(jobLog);
    }
}