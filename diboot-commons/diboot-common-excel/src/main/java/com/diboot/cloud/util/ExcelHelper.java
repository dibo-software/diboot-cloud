/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.cloud.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.diboot.cloud.config.Cons;
import com.diboot.cloud.excel.BaseExcelModel;
import com.diboot.cloud.excel.listener.DynamicHeadExcelListener;
import com.diboot.cloud.excel.listener.FixedHeadExcelListener;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.D;
import com.diboot.core.util.PropertiesUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.opc.internal.FileHelper;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/***
 * excel数据导入导出工具类
 * @auther wangyl@dibo.ltd
 * @date 2019-10-9
 */
@Slf4j
public class ExcelHelper {


    public static final String POINT = ".";
    public static final String HTTP = "http";
    public static final String QUESTION_MARK = "?";


    /**
     * 文件和图片的后台存储路径
     */
    private static String fileStorageDirectory = null;


    /**
     * excel格式
     */
    private static final List<String> EXCEL_SUFFIX = Arrays.asList("xls", "xlsx", "xlsm");

    /**
     * 预览读取excel文件数据
     * @param content
     * @param listener
     * @return
     */
    public static <T extends BaseExcelModel> boolean previewReadExcel(byte[] content, FixedHeadExcelListener listener) throws Exception{
        listener.setPreview(true);
        return readAndSaveExcel(content, listener);
    }

    /**
     * 读取字节数组数据并保存到数据库
     * @param content
     * @param listener
     * @return
     */
    public static <T extends BaseExcelModel> boolean readAndSaveExcel(byte[] content, FixedHeadExcelListener listener) throws Exception{
        Class<T> headClazz = BeanUtils.getGenericityClass(listener, 0);
        EasyExcel.read(new BufferedInputStream(new ByteArrayInputStream(content))).registerReadListener(listener).head(headClazz).sheet().doRead();
        return true;
    }

    /**
     * 读取非确定/动态表头的excel文件数据
     * @param content
     * @return
     */
    public static boolean readDynamicHeadExcel(byte[] content, DynamicHeadExcelListener listener){
        EasyExcel.read(new BufferedInputStream(new ByteArrayInputStream(content))).registerReadListener(listener).sheet().doRead();
        return true;
    }

    /**
     * 简单将数据写入excel文件,列宽自适应数据长度
     * @param filePath
     * @param sheetName
     * @param dataList
     * @return
     */
    public static boolean writeDynamicData(String filePath, String sheetName, List<List<String>> dataList) throws Exception{
        try {
            EasyExcel.write(filePath).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet(sheetName).doWrite(dataList);
            return true;
        }
        catch (Exception e) {
            log.error("数据写入excel文件失败",e);
            return false;
        }
    }

    /**
     * 简单将数据写入excel文件,列宽自适应数据长度
     * @param filePath
     * @param sheetName
     * @param dataList
     * @param <T>
     * @return
     */
    public static <T extends BaseExcelModel> boolean writeData(String filePath, String sheetName, List<T> dataList) throws Exception{
        try {
            if(V.isEmpty(dataList)){
                return writeDynamicData(filePath, sheetName, Collections.emptyList());
            }
            Class<T> tClass = (Class<T>) dataList.get(0).getClass();
            EasyExcel.write(filePath, tClass).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet(sheetName).doWrite(dataList);
            return true;
        }
        catch (Exception e) {
            log.error("数据写入excel文件失败",e);
            return false;
        }
    }

    /**
     * web 导出excel
     *
     * @param response
     * @param clazz     导出的类
     * @param data      导出的数据
     * @param <T>
     * @throws Exception
     */
    public static <T extends BaseExcelModel> void exportExcel(HttpServletResponse response, String fileName, Class<T> clazz, List<T> data) throws Exception{
        try {
            response.setContentType("application/x-msdownload");
            response.setCharacterEncoding("utf-8");
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
            response.setHeader("Content-disposition", "attachment; filename=" + fileName);
            response.setHeader("filename", fileName);
            response.setHeader("err-code", String.valueOf(Status.OK.code()));
            response.setHeader("err-msg", URLEncoder.encode("操作成功",  StandardCharsets.UTF_8.name()));
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream(), clazz)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("sheet1")
                    .doWrite(data);
        } catch (Exception e) {
            log.error("下载文件失败：", e);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.setHeader("err-code", String.valueOf(Status.FAIL_OPERATION.code()));
            response.setHeader("err-msg", URLEncoder.encode("下载文件失败",  StandardCharsets.UTF_8.name()));
        }
    }


    /**
     * 是否是Excel文件
     * @param fileName
     * @return
     */
    public static boolean isExcel(String fileName){
        String ext = getFileExtByName(fileName);
        if (V.isEmpty(ext)) {
            return false;
        }
        return EXCEL_SUFFIX.contains(ext.toLowerCase());
    }

    /**
     * 检测上传的文件是否是是否是Excel文件
     * @param file
     * @return
     */
    public static boolean isExcel(MultipartFile file){
        if (file == null) {
            return false;
        }
        if (V.isEmpty(file.getOriginalFilename())) {
            return false;
        }
        String ext = getFileExtByName(file.getOriginalFilename());
        if (V.isEmpty(ext)) {
            return false;
        }
        return EXCEL_SUFFIX.contains(ext.toLowerCase());
    }


    /***
     * 根据名称取得后缀
     * @param fileName
     * @return
     */
    public static String getFileExtByName(String fileName){
        if(fileName.startsWith(HTTP) && fileName.contains(Cons.FILE_PATH_SEPARATOR)){
            fileName = getFileName(fileName);
        }
        if(fileName.lastIndexOf(POINT) > 0){
            return fileName.substring(fileName.lastIndexOf(POINT)+1).toLowerCase();
        }
        log.warn("检测到没有后缀的文件名:" + fileName);
        return "";
    }

    /**
     * 根据文件URL解析出其文件名
     * @param fileUrl
     * @return
     */
    public static String getFileName(String fileUrl){
        String temp = StringUtils.substring(fileUrl, fileUrl.lastIndexOf(Cons.FILE_PATH_SEPARATOR)+1);
        if(StringUtils.contains(fileUrl, QUESTION_MARK)){
            temp = StringUtils.substring(temp, 0, temp.lastIndexOf(QUESTION_MARK));
        }
        return temp;
    }


    /**
     * 获取远程excel文件
     * @param filePath
     * @return
     */
    private static File getExcelFile(String filePath){
        try {
            URI uri = new URI(filePath);
            File file = new File(uri);
            if(!file.exists()){
                log.error("找不到指定文件，路径："+filePath);
                throw new BusinessException(Status.FAIL_EXCEPTION, "找不到指定文件,导入excel失败");
            }
            return file;
        } catch (URISyntaxException e) {
            throw new BusinessException(Status.FAIL_EXCEPTION, "错误的文件路径");
        }
    }

}
