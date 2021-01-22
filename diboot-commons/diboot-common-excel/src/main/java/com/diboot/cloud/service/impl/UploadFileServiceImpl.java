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
package com.diboot.cloud.service.impl;

import com.diboot.cloud.dto.UploadFileBindRefDTO;
import com.diboot.cloud.service.UploadFileService;
import com.diboot.cloud.service.UploadFileApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文件service
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/1/20  10:14
 */
@Service
public class UploadFileServiceImpl implements UploadFileService {

    @Autowired
    private UploadFileApiService uploadFileApiService;

    @Override
    public void bindRelObjId(Object relObjId, Class<?> relObjTypeClass, List<String> fileUuidList) throws Exception {
        UploadFileBindRefDTO uploadFileBindRefDTO = new UploadFileBindRefDTO();
        uploadFileBindRefDTO.setFileUuidList(fileUuidList);
        uploadFileBindRefDTO.setRelObjId(relObjId);
        uploadFileBindRefDTO.setRelObjType(relObjTypeClass.getClass().getSimpleName());
        uploadFileApiService.bindRelObjId(uploadFileBindRefDTO);
    }

}
