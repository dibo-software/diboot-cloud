package com.diboot.file.service.impl;

import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.file.entity.UploadFile;
import com.diboot.file.service.UploadFileService;
import com.diboot.file.util.HttpHelper;
import com.diboot.file.vo.UploadFileIncludeBytesVO;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * fastDfs实现
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/1/20  10:14
 */
@Slf4j
public class FastdfsAbstractFileStorageServiceImpl extends AbstractFileStorageServiceImpl {

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Override
    public UploadFile upload(MultipartFile file) throws Exception {
        // 构建UploadFile
        UploadFile uploadFile = buildUploadFile(file);
        // 上传数据
        // StorePath [group=group1, path=M00/00/00/CgAAY2AH80SALwtIAAHYvV7fJrE256.jpg]
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), uploadFile.getFileType(), null);
        // 设置存储路径
        uploadFile.setStoragePath(storePath.getFullPath());
        return uploadFile;
    }

    @Override
    public void download(UploadFile uploadFile, HttpServletResponse response) throws Exception{
        String group = S.substringBefore(uploadFile.getStoragePath(), File.separator);
        String path = S.substringAfter(uploadFile.getStoragePath(), File.separator);

        byte[] bytes = appendFileStorageClient.downloadFile(group, path, new DownloadByteArray());
        String fileName = URLEncoder.encode(uploadFile.getFileName(), StandardCharsets.UTF_8.name());
        response.setContentType(HttpHelper.getContextType(fileName));
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        response.setHeader("Content-Length", String.valueOf(bytes.length));
        response.setHeader("filename", URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
        BufferedInputStream bufferedInputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(bytes));
            IOUtils.copyLarge(bufferedInputStream, response.getOutputStream());
        } catch (Exception e) {
            log.error("下载文件{}失败:", uploadFile.getStoragePath(), e);
        } finally {
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public UploadFileIncludeBytesVO getUploadFileIncludeBytes(String uuid) throws Exception {
        UploadFile uploadFile = uploadFileService.getEntity(uuid);
        if (V.isEmpty(uploadFile)) {
            return null;
        }
        UploadFileIncludeBytesVO bytesVO = BeanUtils.convert(uploadFile, UploadFileIncludeBytesVO.class);
        String group = S.substringBefore(uploadFile.getStoragePath(), File.separator);
        String path = S.substringAfter(uploadFile.getStoragePath(), File.separator);
        // 获取文件的字节流
        byte[] content = appendFileStorageClient.downloadFile(group, path, new DownloadByteArray());
        bytesVO.setContent(content);
        return bytesVO;
    }
}
