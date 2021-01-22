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
package com.diboot.file.service.impl;

import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.file.entity.UploadFile;
import com.diboot.file.util.FileHelper;
import com.diboot.file.util.HttpHelper;
import com.diboot.file.vo.UploadFileIncludeBytesVO;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;

/**
 * 本地存储
 *
 * @author : uu
 * @version : v2.0
 * @Date 2021/1/11  11:58
 */
public class LocalFileStorageServiceImpl extends AbstractFileStorageServiceImpl {

    @Override
    public UploadFile upload(MultipartFile file) throws Exception {
        UploadFile uploadFile = buildUploadFile(file);
        String storagePath = FileHelper.saveFile(file, getNewFileNameFromAccessUrl(uploadFile.getAccessUrl()));
        uploadFile.setStoragePath(storagePath);
        return uploadFile;
    }

    @Override
    public void download(UploadFile uploadFile, HttpServletResponse response) throws Exception {
        HttpHelper.downloadLocalFile(uploadFile.getStoragePath(), uploadFile.getFileName(), response);
    }

    @Override
    public UploadFileIncludeBytesVO getUploadFileIncludeBytes(String uuid) throws Exception {
        UploadFile uploadFile = uploadFileService.getEntity(uuid);
        if (V.isEmpty(uploadFile)) {
            return null;
        }
        UploadFileIncludeBytesVO bytesVO = BeanUtils.convert(uploadFile, UploadFileIncludeBytesVO.class);
        // 读取文件内容
        byte[] content = IOUtils.toByteArray(new FileInputStream(new File(uploadFile.getStoragePath())));
        bytesVO.setContent(content);
        return bytesVO;
    }
}
