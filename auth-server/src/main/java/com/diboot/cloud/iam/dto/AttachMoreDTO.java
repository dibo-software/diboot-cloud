package com.diboot.cloud.iam.dto;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
* attachMore 传递的DTO格式
* <p>
* [{type: 'T', target: 'category', value: 'id', key: 'name’}, {type: 'D', target: 'GENDER'}]
* </p>
* @author Mazc
* @version 2.0
* @date 2020-11-16
* Copyright © dibo.ltd
*/
public class AttachMoreDTO implements Serializable {
    /**
     * 关联类型
     */
    public enum REF_TYPE {
        /**
         * 绑定的是对象
         */
        T,
        /**
         * 绑定的是字典
         */
        D
    }

    /**
     * 关联的类型
     */
    @NotNull(message = "绑定类型不能为空，且只能为:T或D类型！")
    private REF_TYPE type;

    /**
     * 指向的实体类 或者 字典的type
     * 当{@link REF_TYPE#T} target指向实体的小驼峰命名
     * 当{@link REF_TYPE#D} target指向{@link com.diboot.core.entity.Dictionary#type}
     */
    @NotNull(message = "查询类型不能为空！")
    private String target;

    /**
     * 需要的key字段
     * 当{@link REF_TYPE#T} key为表中字段名
     * 当{@link REF_TYPE#D} key为表中{@link com.diboot.core.entity.Dictionary#itemName}
     */
    private String key;

    /**
     * 需要查询的value字段
     * 当{@link REF_TYPE#T} value为表中字段名
     * 当{@link REF_TYPE#D} value为表中{@link com.diboot.core.entity.Dictionary#itemValue}
     */
    private String value;
    public REF_TYPE getType() {
        return type;
    }

    public void setType(REF_TYPE type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
