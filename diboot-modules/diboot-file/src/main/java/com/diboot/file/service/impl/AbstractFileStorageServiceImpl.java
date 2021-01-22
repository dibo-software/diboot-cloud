package com.diboot.file.service.impl;

import com.diboot.core.util.S;
import com.diboot.file.entity.UploadFile;
import com.diboot.file.service.FileStorageService;
import com.diboot.file.service.UploadFileService;
import com.diboot.file.util.FileHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

/**
 * 抽象实现
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/1/21  07:58
 */
public abstract class AbstractFileStorageServiceImpl implements FileStorageService {

    /**
     * 下载路径前缀
     */
    protected final static String DOWNLOAD_PREFIX = "/uploadFile/download/";

    @Autowired
    protected UploadFileService uploadFileService;

    /**
     * 构建文件访问/下载的url
     *
     * @param newFileName
     * @return
     */
    protected String buildAccessUrl(String newFileName) {
        return DOWNLOAD_PREFIX + newFileName;
    }

    /**
     * 从访问/下载的url获取文件名称
     *
     * @param accessUrl
     * @return
     */
    protected String getNewFileNameFromAccessUrl(String accessUrl) {
        return S.substringAfter(accessUrl, DOWNLOAD_PREFIX);
    }

    /**
     * 构建上传UploadFile
     * @param file
     * @return
     */
    protected UploadFile buildUploadFile(MultipartFile file) {
        UploadFile uploadFile = new UploadFile();
        // 文件后缀
        String fileUid = S.newUuid();
        String ext = FileHelper.getFileExtByName(file.getOriginalFilename());
        String newFileName =S.join(new String[]{fileUid, ext}, ".");
        uploadFile.setFileName(file.getOriginalFilename())
                .setFileType(ext)
                .setUuid(fileUid)
                .setAccessUrl(buildAccessUrl(newFileName));
        return uploadFile;
    }
}
