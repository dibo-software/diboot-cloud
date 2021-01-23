package com.diboot.file.controller;

import com.diboot.core.entity.Dictionary;
import com.diboot.core.util.S;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.file.dto.UploadFileBindRefDTO;
import com.diboot.file.dto.UploadFileFormDTO;
import com.diboot.file.entity.UploadFile;
import com.diboot.file.service.FileStorageService;
import com.diboot.file.vo.UploadFileIncludeBytesVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * 文件上传相关Controller
 *
 * @author uu
 * @version 2.0
 * @date 2021-01-05
 */
@Slf4j
@RestController
@RequestMapping("/uploadFile")
public class UploadFileController extends BaseFileController {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/upload")
    public JsonResult uploadMapping(@RequestParam("file") MultipartFile file, @RequestParam("appModule") String appModule) throws Exception {
        return super.uploadFile(file, Dictionary.class, appModule);
    }

    /**
     * 上传文件 by uploadFileFormDTO
     *
     * @param uploadFileFormDTO
     * @return
     * @throws Exception
     */
    @PostMapping("/upload/dto")
    public JsonResult uploadMapping(@Valid UploadFileFormDTO uploadFileFormDTO) throws Exception {
        return super.uploadFile(uploadFileFormDTO);
    }

    /**
     * 下载文件
     **/
    @GetMapping("/download/{fileUuid}")
    public JsonResult download(@PathVariable("fileUuid") String fileUuid, HttpServletResponse response) throws Exception {
        if (S.contains(fileUuid, ".")) {
            fileUuid = S.substringBefore(fileUuid, ".");
        }
        UploadFile uploadFile = uploadFileService.getEntity(fileUuid);
        if (uploadFile == null) {
            return new JsonResult(Status.FAIL_VALIDATION, "文件不存在");
        }
        fileStorageService.download(uploadFile, response);
        return null;
    }

    /**
     * 显示图片
     *
     * @param fileUuid
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping("/download/{fileUuid}/image")
    public JsonResult writeImage(@PathVariable("fileUuid") String fileUuid, HttpServletResponse response) throws Exception {
        if (S.contains(fileUuid, ".")) {
            fileUuid = S.substringBefore(fileUuid, ".");
        }
        UploadFile uploadFile = uploadFileService.getEntity(fileUuid);
        if (uploadFile == null) {
            return new JsonResult(Status.FAIL_VALIDATION, "图片不存在");
        }
        fileStorageService.download(uploadFile, response);
        return null;
    }

    /**
     * <h3>获取文件通用接口</h3>
     * <p>
     * 其中当relObjField不传递的时候，表示获取当前业务ID和业务类型下的所有文件<br/>
     * 当传递relObjField的时候，获取指定类型的文件
     * </p>
     *
     * @param relObjId    业务ID   <strong style="color:red;">必传字段</strong>
     * @param relObjType  业务类型 <strong style="color:red;">必传字段</strong>
     * @param relObjField 对应的具体类型   <strong style="color:blue;">非必传字段(同一种业务下可能有多种文件)</strong>
     * @return {@link List <UploadFile>} 返回文件对象的集合
     * @throws Exception
     */
    @GetMapping(value = {"/getList/{relObjId}/{relObjType}", "/getList/{relObjId}/{relObjType}/{relObjField}"})
    public JsonResult getFileList(@PathVariable("relObjId") Object relObjId, @PathVariable("relObjType") String relObjType,
                                  @PathVariable(value = "relObjField", required = false) String relObjField) throws Exception {
        List<UploadFile> uploadFiles = getUploadFileList(relObjId, relObjType, relObjField);
        return JsonResult.OK(uploadFiles);
    }

    /**
     * 文件绑定主表
     *
     * @param uploadFileBindRefDTO
     * @return
     * @throws Exception
     */
    @PostMapping("/bindRelObjId")
    public JsonResult bindRelObjId(@RequestBody UploadFileBindRefDTO uploadFileBindRefDTO) throws Exception {
        uploadFileService.bindRelObjId(uploadFileBindRefDTO);
        return JsonResult.OK();
    }

    /**
     * 更新uploadFile
     *
     * @param uploadFile
     * @return
     * @throws Exception
     */
    @PostMapping("/update")
    public JsonResult updateUploadFile(@RequestBody UploadFile uploadFile) throws Exception {
        boolean success = uploadFileService.updateEntity(uploadFile);
        return JsonResult.OK(success);
    }

    /**
     * 获取包含字节数组的uploadFile
     *
     * @param uuid
     * @return
     * @throws Exception
     */
    @PostMapping("/getUploadFileIncludeBytes")
    public JsonResult<UploadFileIncludeBytesVO> getUploadFileIncludeBytes(@RequestParam("uuid") String uuid) throws Exception {
        return JsonResult.OK(fileStorageService.getUploadFileIncludeBytes(uuid));
    }
}