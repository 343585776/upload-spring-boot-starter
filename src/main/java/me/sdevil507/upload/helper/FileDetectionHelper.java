package me.sdevil507.upload.helper;

import lombok.extern.slf4j.Slf4j;
import me.sdevil507.upload.properties.UploadProperties;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * 文件检测工具
 *
 * @author sdevil507
 */
@Slf4j
public class FileDetectionHelper {

    private final UploadProperties uploadProperties;

    public FileDetectionHelper(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    /**
     * 通过检测文件内容获取实际文件后缀名(防止文件伪造)
     *
     * @param file 文件
     * @return 文件后缀名
     */
    public String getExtension(File file) {
        try {
            // 使用tika提供的外观工具,进行检测
            Tika tika = new Tika();
            // 此处检测文件内容,返回文件MimeType名称
            String detect = tika.detect(file);
            // 获取tika提供的默认参照表
            // 可以进行自定义,参照https://stackoverflow.com/questions/13650372/how-to-determine-appropriate-file-extension-from-mime-type-in-java
            MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
            // 根据MimeType名称获取MimeType类型
            MimeType mimeType = allTypes.forName(detect);
            // 根据MimeType类型获取对应的后缀名
            return mimeType.getExtension();
        } catch (IOException | MimeTypeException e) {
            log.error("tika检测文件实际后缀名失败:", e);
            return null;
        }
    }

    /**
     * 是否支持该文件类型
     *
     * @param file 文件
     * @return 支持/不支持
     */
    public boolean canSupport(File file) {
        // 查看是否在上传文件类型列表中被支持
        return fileTypeContains(getExtension(file));
    }

    /**
     * 检查是否在允许上传的文件类型列表中
     *
     * @param extension 文件拓展名
     * @return 包含/不包含
     */
    @SuppressWarnings("SimplifiableIfStatement")
    private boolean fileTypeContains(String extension) {
        if (null == uploadProperties.getIncludeExtensions()) {
            return true;
        }
        String pattern = "*";
        if (uploadProperties.getIncludeExtensions().length < 1 || pattern.equals(uploadProperties.getIncludeExtensions()[0].trim())) {
            // 如果不设置上传文件类型限制或设置为"*",则默认都支持
            return true;
        } else {
            return Arrays.asList(uploadProperties.getIncludeExtensions()).contains(extension.substring(1));
        }
    }
}
