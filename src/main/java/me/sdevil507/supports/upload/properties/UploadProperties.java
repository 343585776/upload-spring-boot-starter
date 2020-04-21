package me.sdevil507.supports.upload.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 上传通用配置
 *
 * @author sdevil507
 */
@ConfigurationProperties("file.upload")
@Data
public class UploadProperties {

    /**
     * 文件上传支持的文件拓展名列表(不设置默认全部支持)
     */
    private String[] includeExtensions;

    /**
     * 如果是本地保存,设置本地保存路径(路径为绝对路径)
     */
    private String localSavePath;
}
