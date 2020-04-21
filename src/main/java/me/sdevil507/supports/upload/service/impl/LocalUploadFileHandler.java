package me.sdevil507.supports.upload.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.sdevil507.supports.upload.helper.FileDetectionHelper;
import me.sdevil507.supports.upload.properties.UploadProperties;
import me.sdevil507.supports.upload.service.AbstractUploadFileHandler;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 上传文件本地保存操作
 *
 * @author sdevil507
 */
@Slf4j
public class LocalUploadFileHandler extends AbstractUploadFileHandler {

    private final UploadProperties uploadProperties;

    public LocalUploadFileHandler(FileDetectionHelper fileDetectionHelper, UploadProperties uploadProperties) {
        super(fileDetectionHelper);
        this.uploadProperties = uploadProperties;
    }

    @Override
    protected String save(File tempFile, String originalFileName) {
        // 获取文件拓展名
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        // 文件名设置为UUID+extension
        String fileName = UUID.randomUUID() + extension;
        File saveFile = new File(uploadProperties.getLocalSavePath() + File.separator + fileName);
        try {
            FileCopyUtils.copy(tempFile, saveFile);
        } catch (IOException e) {
            log.error("文件保存失败:", e);
            fileName = null;
        }
        return fileName;
    }
}
