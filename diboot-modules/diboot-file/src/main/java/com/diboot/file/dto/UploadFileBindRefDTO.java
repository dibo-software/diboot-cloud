package com.diboot.file.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 绑定类DTO
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/1/22  12:43
 */
@Getter@Setter
public class UploadFileBindRefDTO implements Serializable {

    private static final long serialVersionUID = 7357662861059277160L;

    /**
     * 主表的id
     */
    private Object relObjId;
    /**
     * 绑定的类型
     */
    private String relObjType;
    /**
     * 需要操作的uploadFile
     */
    private List<String> fileUuidList;
}
