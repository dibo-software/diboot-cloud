package com.example.vo;

import com.diboot.core.binding.annotation.*;
import com.example.entity.Demo;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
* 样例 DetailVO定义
* @author JerryMa
* @version 1.0
* @date 2021-01-21
 * Copyright © www.dibo.ltd
*/
@Getter @Setter @Accessors(chain = true)
public class DemoDetailVO extends Demo  {
    private static final long serialVersionUID = 6735004051643002034L;

    /**
    * 关联字典：GENDER
    */
    @BindDict(type=DICT_GENDER, field="gender")
    private String genderLabel;

}