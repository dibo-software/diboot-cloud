package com.diboot.file.vo;

import com.diboot.file.entity.UploadFile;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 包含文件字节的VO
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/1/21  18:02
 */
@Getter
@Setter
@Accessors(chain = true)
public class UploadFileIncludeBytesVO extends UploadFile {

    /**
     * 文件字节流
     */
    private byte[] content;
}
