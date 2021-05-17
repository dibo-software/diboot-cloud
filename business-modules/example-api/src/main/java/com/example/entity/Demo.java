package com.example.entity;

import java.util.Date;
import java.lang.Double;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;
import java.util.List;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.util.D;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
* 样例 Entity定义
* @author JerryMa
* @version 1.0
* @date 2021-01-21
* Copyright © www.dibo.ltd
*/
@Getter @Setter @Accessors(chain = true)
public class Demo extends BaseCustomEntity {
    private static final long serialVersionUID = 7431591600573708662L;

    /**
    * gender字段的关联字典
    */
    public static final String DICT_GENDER = "GENDER";

    /**
    * 标题 
    */
    @NotNull(message = "标题不能为空")
    @Length(max=100, message="标题长度应小于100")
    @TableField()
    private String title;

    /**
    * 完成 
    */
    @TableField()
    private Boolean complete;

    /**
    * 性别 
    */
    @Length(max=100, message="性别长度应小于100")
    @TableField()
    private String gender;


} 
