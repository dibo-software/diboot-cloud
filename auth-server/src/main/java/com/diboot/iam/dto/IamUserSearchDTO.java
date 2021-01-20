package com.diboot.iam.dto;

import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.iam.entity.IamUser;

/**
* INIT DTO定义
* @author Mazc
* @version 2.0
* @date 2021-01-20
 * Copyright © dibo.ltd
*/
public class IamUserSearchDTO extends IamUser {
    private static final long serialVersionUID = 3276697558948234945L;

    @BindQuery(comparison = Comparison.LIKE)
    private String realname;

    @BindQuery(comparison = Comparison.LIKE)
    private String userNum;

    @BindQuery(comparison = Comparison.LIKE)
    private String email;

    @BindQuery(comparison = Comparison.LIKE)
    private String mobilePhone;
}
