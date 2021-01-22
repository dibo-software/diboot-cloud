/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.cloud.service;

import com.diboot.cloud.config.FeignConfig;
import com.diboot.cloud.dto.UploadFileBindRefDTO;
import com.diboot.cloud.dto.UploadFileFormDTO;
import com.diboot.cloud.dto.UploadFileIncludeBytesDTO;
import com.diboot.cloud.entity.UploadFile;
import com.diboot.core.vo.JsonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * 远程调用接口
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/1/14  18:20
 */
@FeignClient(value = "file-server", configuration = FeignConfig.class)
public interface UploadFileApiService {

    /**
     * 基于上传组件上传
     *
     * @param uploadFileFormDTO
     * @return
     */
    @PostMapping(value="/uploadFile/upload/dto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    JsonResult<UploadFile> upload(@RequestBody UploadFileFormDTO uploadFileFormDTO);

    /**
     * 更新UploadFile, 基于uuid
     *
     * @param uploadFile
     * @return
     */
    @PostMapping("/uploadFile/update")
    JsonResult<Boolean> updateUploadFile(@RequestBody UploadFile uploadFile);

    /**
     * 获取uploadFile文件（包含文件的字节）
     *
     * @param uuid
     * @return
     */
    @PostMapping("/uploadFile/getUploadFileIncludeBytes")
    JsonResult<UploadFileIncludeBytesDTO> getUploadFileIncludeBytes(@RequestParam("uuid") String uuid);

    /**
     * 文件绑定主表
     *
     * @param uploadFileBindRefDTO
     * @return
     * @throws Exception
     */
    @PostMapping("/uploadFile/bindRelObjId")
    JsonResult bindRelObjId(@RequestBody UploadFileBindRefDTO uploadFileBindRefDTO);


}
