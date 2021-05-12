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
import com.diboot.core.vo.DictionaryVO;
import com.diboot.core.vo.JsonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 远程服务调用示例
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/09
 */
@FeignClient(value = "auth-server", configuration = FeignConfig.class)
public interface DictionaryApiService {

    @PostMapping("/dictionary/")
    JsonResult createDictAndChildren(@RequestBody @Valid DictionaryVO entityVO);

    @GetMapping("/dictionary/listDefinition")
    JsonResult getDictDefinitionList();

    @GetMapping("/dictionary/listDefinitionVO")
    JsonResult getDictDefinitionVOList();

    @GetMapping("/dictionary/items/{type}")
    JsonResult getKeyValueList(@PathVariable("type")String type);

}