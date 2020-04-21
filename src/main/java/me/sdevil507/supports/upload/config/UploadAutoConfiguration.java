package me.sdevil507.supports.upload.config;

import me.sdevil507.supports.upload.helper.FileDetectionHelper;
import me.sdevil507.supports.upload.properties.UploadProperties;
import me.sdevil507.supports.upload.service.AbstractUploadFileHandler;
import me.sdevil507.supports.upload.service.impl.LocalUploadFileHandler;
import me.sdevil507.supports.upload.controller.UploadController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云短信通道自动暴露配置
 *
 * @author sdevil507
 */
@Configuration
@EnableConfigurationProperties(UploadProperties.class)
public class UploadAutoConfiguration {

    /**
     * 根据文件内容解析类型帮助类
     *
     * @param uploadProperties 上传文件配置
     * @return 内容解析帮助类
     */
    @Bean
    public FileDetectionHelper fileDetectionHelper(UploadProperties uploadProperties) {
        return new FileDetectionHelper(uploadProperties);
    }

    /**
     * 如果存在本地保存路径属性配置,则初始化项目提供的本地保存方案
     * 如果不存在,用户需自己在项目中继承{@link AbstractUploadFileHandler}类并实现自定义的save方法
     *
     * @param uploadProperties 上传文件配置
     * @return 文件保存方案
     */
    @Bean
    @ConditionalOnProperty(prefix = "file.upload", name = {"local-save-path"})
    public AbstractUploadFileHandler abstractUploadFileHandler(UploadProperties uploadProperties) {
        return new LocalUploadFileHandler(fileDetectionHelper(uploadProperties), uploadProperties);
    }

    /**
     * 创建通用上传的Controller给调用者调用
     *
     * @return 上传Controller
     */
    @Bean
    public UploadController uploadController() {
        return new UploadController();
    }
}
