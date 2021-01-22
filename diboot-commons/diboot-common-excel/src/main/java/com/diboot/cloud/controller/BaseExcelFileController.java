/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.cloud.controller;

import com.diboot.cloud.dto.UploadFileFormDTO;
import com.diboot.cloud.dto.UploadFileIncludeBytesDTO;
import com.diboot.cloud.entity.UploadFile;
import com.diboot.cloud.excel.listener.FixedHeadExcelListener;
import com.diboot.cloud.service.UploadFileApiService;
import com.diboot.cloud.util.ExcelHelper;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.controller.BaseController;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel导入基类Controller
 *
 * @author Mazc@dibo.ltd
 * @version 2.0
 * @date 2020/02/20
 */
@Slf4j
public abstract class BaseExcelFileController extends BaseController {
    // 初始文件名参数
    protected static final String ORIGIN_FILE_NAME = "originFileName";
    // 预览文件名参数
    protected static final String PREVIEW_FILE_NAME = "previewFileName";

    @Autowired
    private UploadFileApiService uploadFileApiService;

    @Value("${spring.application.name}")
    private String appModule = "default";
    /***
     * 获取对应的ExcelDataListener
     * @return
     */
    protected abstract FixedHeadExcelListener getExcelDataListener();

    /***
     * excel数据预览
     * @return
     * @throws Exception
     */
    public <T> JsonResult excelPreview(MultipartFile file, Class<T> clazz) throws Exception {
        checkIsExcel(file);
        // 连接服务器，保存文件
        UploadFileFormDTO uploadFileFormDTO = new UploadFileFormDTO()
                .setFile(file)
                .setRelObjType(clazz.getSimpleName())
                .setRelObjField("excel")
                .setAppModule(appModule);
        JsonResult<UploadFile> result = uploadFileApiService.upload(uploadFileFormDTO);
        // 构建预览数据Map
        Map<String, Object> dataMap = buildPreviewDataMap(result.getData(), file);
        return JsonResult.OK(dataMap);
    }

    /***
     * excel数据预览
     * @param uploadFileFormDTO 带有其他配置的上传
     * @return
     * @throws Exception
     */
    public JsonResult excelPreview(UploadFileFormDTO uploadFileFormDTO) throws Exception {
        checkIsExcel(uploadFileFormDTO.getFile());
        // 保存文件到本地
        JsonResult<UploadFile> result = uploadFileApiService.upload(uploadFileFormDTO);
        // 构建预览数据Map
        Map<String, Object> dataMap = buildPreviewDataMap(result.getData(), uploadFileFormDTO.getFile());
        return JsonResult.OK(dataMap);
    }

    /***
     * 预览后提交保存
     * @param
     * @return
     * @throws Exception
     */
    public <T> JsonResult excelPreviewSave(String previewFileName, String originFileName) throws Exception {
        if (V.isEmpty(previewFileName) || V.isEmpty(originFileName)) {
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "预览保存失败，参数 tempFileName 或 originFileName 未指定！");
        }
        String fileUid = S.substringBefore(previewFileName, ".");
        // 获取文件
        JsonResult<UploadFileIncludeBytesDTO> uploadFileIncludeBytes = uploadFileApiService.getUploadFileIncludeBytes(fileUid);
        // 计算文件的数量
        int dataCount = extractDataCount(fileUid, uploadFileIncludeBytes.getData().getContent());
        //更新uploadFile
        UploadFile uploadFile = BeanUtils.convert(uploadFileIncludeBytes, UploadFile.class);
        uploadFile.setDataCount(dataCount)
                .setDescription(getString("description"));
        // 更新excel内容
        uploadFileApiService.updateUploadFile(uploadFile);
        return JsonResult.OK();
    }

    /***
     * 直接上传excel
     * @param
     * @return
     * @throws Exception
     */
    public <T> JsonResult uploadExcelFile(MultipartFile file, Class<T> entityClass) throws Exception {
        checkIsExcel(file);
        // 连接服务器，保存文件
        UploadFileFormDTO uploadFileFormDTO = new UploadFileFormDTO()
                .setFile(file)
                .setRelObjType(entityClass.getSimpleName())
                .setRelObjField("excel")
                .setAppModule(appModule);
        JsonResult<UploadFile> result = uploadFileApiService.upload(uploadFileFormDTO);
        UploadFile uploadFile = result.getData();
        // 计算文件的数量
        int dataCount = extractDataCount(uploadFile.getUuid(), file.getBytes());
        //更新uploadFile
        uploadFile.setDataCount(dataCount)
                .setDescription(getString("description"));
        // 更新excel内容
        uploadFileApiService.updateUploadFile(uploadFile);
        return JsonResult.OK();
    }

    /***
     * 直接上传excel
     * @param
     * @return
     * @throws Exception
     */
    public <T> JsonResult uploadExcelFile(UploadFileFormDTO uploadFileFormDTO) throws Exception {
        checkIsExcel(uploadFileFormDTO.getFile());
        JsonResult<UploadFile> result = uploadFileApiService.upload(uploadFileFormDTO);
        UploadFile uploadFile = result.getData();
        // 计算文件的数量
        int dataCount = extractDataCount(uploadFile.getUuid(), uploadFileFormDTO.getFile().getBytes());
        //更新uploadFile
        uploadFile.setDataCount(dataCount)
                .setDescription(getString("description"));
        // 更新excel内容
        uploadFileApiService.updateUploadFile(uploadFile);
        return JsonResult.OK();
    }

    /**
     * 构建预览数据Map
     *
     * @param uploadFile
     * @param file
     * @return
     * @throws Exception
     */
    private Map<String, Object> buildPreviewDataMap(UploadFile uploadFile, MultipartFile file) throws Exception {
        Map<String, Object> dataMap = new HashMap<>(8);
        // 预览
        FixedHeadExcelListener listener = getExcelDataListener();
        listener.setRequestParams(super.getParamsMap());
        try {
            ExcelHelper.previewReadExcel(file.getBytes(), listener);
        } catch (Exception e) {
            log.warn("解析并校验excel文件失败", e);
            if (V.notEmpty(e.getMessage())) {
                throw new Exception(e.getMessage());
            }
            throw e;
        }
        //最后拦截，如果数据异常在listener中未被拦截抛出异常，此处进行处理
        if (V.notEmpty(listener.getErrorMsgs())) {
            throw new BusinessException(Status.FAIL_VALIDATION, S.join(listener.getErrorMsgs(), "; "));
        }
        // 绑定属性到model
        dataMap.put("header", listener.getFieldHeaders());
        dataMap.put(ORIGIN_FILE_NAME, file.getOriginalFilename());
        dataMap.put(PREVIEW_FILE_NAME, ExcelHelper.getFileName(uploadFile.getAccessUrl()));
        List dataList = listener.getDataList();
        if (V.notEmpty(dataList) && dataList.size() > BaseConfig.getPageSize()) {
            dataList = dataList.subList(0, BaseConfig.getPageSize());
        }
        //最多返回前端十条数据
        dataMap.put("dataList", dataList);
        return dataMap;
    }

    /**
     * 保存文件之后的处理逻辑，如解析excel
     */
    private int extractDataCount(String fileUuid, byte[] bytes) throws Exception {
        FixedHeadExcelListener listener = getExcelDataListener();
        listener.setUploadFileUuid(fileUuid);
        listener.setPreview(false);
        listener.setRequestParams(super.getParamsMap());
        try {
            ExcelHelper.readAndSaveExcel(bytes, listener);
        } catch (Exception e) {
            log.warn("上传数据错误: " + e.getMessage(), e);
            if (V.notEmpty(e.getMessage())) {
                throw new Exception(e.getMessage());
            }
            throw e;
        }
        //最后拦截，如果数据异常在listener中未被拦截抛出异常，此处进行处理
        if (V.notEmpty(listener.getErrorMsgs())) {
            throw new BusinessException(Status.FAIL_VALIDATION, S.join(listener.getErrorMsgs(), "; "));
        }
        return listener.getDataList().size();
    }

    /**
     * 检查是否为合法的excel文件
     *
     * @param file
     * @throws Exception
     */
    private void checkIsExcel(MultipartFile file) throws Exception {
        if (V.isEmpty(file)) {
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "未获取待处理的excel文件！");
        }
        String fileName = file.getOriginalFilename();
        if (V.isEmpty(fileName) || !ExcelHelper.isExcel(fileName)) {
            log.debug("非Excel类型: " + fileName);
            throw new BusinessException(Status.FAIL_VALIDATION, "请上传合法的Excel格式文件！");
        }
    }

}
