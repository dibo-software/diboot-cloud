package com.example.dto;

import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.example.entity.Demo;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.Date;
import com.diboot.core.util.D;

/**
 * 样例 DTO定义
 * @author JerryMa
 * @version 1.0
 * @date 2021-01-21
 * Copyright © www.dibo.ltd
 */
@Getter
@Setter
@Accessors(chain = true)
public class DemoDTO extends Demo {

    private static final long serialVersionUID = 2850214742513101729L;

    /**
     * 创建时间-起始
     */
    @BindQuery(comparison = Comparison.GE, field = "createTime")
    private Date createTime;

    /**
     * 创建时间-截止
     */
    @BindQuery(comparison = Comparison.LT, field = "createTime")
    private Date createTimeEnd;

    @Override()
    public Date getCreateTime() {
        return this.createTime;
    }

    @Override()
    public Demo setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getCreateTimeEnd() {
        return D.nextDay(createTime);
    }

    public Demo setCreateTimeEnd(Date createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
        return this;
    }
}
