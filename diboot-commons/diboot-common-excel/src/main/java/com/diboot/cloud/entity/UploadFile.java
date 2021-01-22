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
package com.diboot.cloud.entity;

import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * file文件
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/1/14  18:20
 */
@Getter
@Setter
@Accessors(chain = true)
public class UploadFile extends BaseEntity {
    private static final long serialVersionUID = 201L;

    private String uuid;

    /**
     * 应用模块
     */
    private String appModule;

    /**
     * 关联对象类
     */
    private String relObjType = null;

    /**
     * 关联对象ID
     */
    private String relObjId;

    /**
     * 关联对象属性
     */
    private String relObjField;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 完整的存储路径
     */
    private String storagePath;

    /**
     * 访问URL
     */
    private String accessUrl;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件包含记录数
     */
    private int dataCount = 0;

    /**
     * 备注
     */
    private String description;

}
