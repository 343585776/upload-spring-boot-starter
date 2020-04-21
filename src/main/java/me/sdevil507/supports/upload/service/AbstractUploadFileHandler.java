package me.sdevil507.supports.upload.service;

import lombok.extern.slf4j.Slf4j;
import me.sdevil507.supports.upload.exceptions.FileTypeCanNotSupportException;
import me.sdevil507.supports.upload.helper.FileDetectionHelper;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 定义上传文件保存操作抽象类
 *
 * @author sdevil507
 */
@Slf4j
public abstract class AbstractUploadFileHandler {

    protected final FileDetectionHelper fileDetectionHelper;

    public AbstractUploadFileHandler(FileDetectionHelper fileDetectionHelper) {
        this.fileDetectionHelper = fileDetectionHelper;
    }

    /**
     * 1. 执行文件校验
     * 2. 执行自定义保存逻辑
     *
     * @return 文件保存后可访问的文件名或url+文件名
     */
    public String execute(MultipartFile multipartFile) throws IOException, FileTypeCanNotSupportException {
        // 获取文件原始名称
        String originalFileName = multipartFile.getOriginalFilename();
        assert originalFileName != null;
        // 创建临时文件
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), originalFileName.substring(originalFileName.lastIndexOf(".")), new File(System.getProperty("java.io.tmpdir")));
        // 将上传文件转为临时文件
        multipartFile.transferTo(tempFile);
        // 方法退出时删除临时文件
        tempFile.deleteOnExit();
        // 验证是否支持该类型文件上传
        if (fileDetectionHelper.canSupport(tempFile)) {
            // 如果支持,执行保存操作
            return doSave(tempFile, originalFileName);
        } else {
            // 如果不支持,抛出异常
            log.error("不支持{}文件上传,请查看配置中文件类型支持列表!", originalFileName);
            throw new FileTypeCanNotSupportException("上传列表中不支持该文件类型上传!");
        }
    }

    /**
     * 执行保存操作
     *
     * @param tempFile         由上传文件生成的临时文件(与原文件内容一致)
     * @param originalFileName 原始文件名
     * @return 文件保存后可访问文件名或url+文件名
     */
    protected String doSave(File tempFile, String originalFileName) {
        return save(tempFile, originalFileName);
    }

    /**
     * 子类重写文件保存逻辑(可保存至本地或者七牛云等云存储方式)
     *
     * @param tempFile         由上传文件生成的临时文件(与原文件内容一致)
     * @param originalFileName 原始文件名
     * @return 文件保存后可访问文件名或url+文件名
     */
    protected abstract String save(File tempFile, String originalFileName);
}
