package me.sdevil507.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 文件上传结果DTO
 *
 * @author sdevil507
 */
@Data
@AllArgsConstructor
public class FileUploadResultDTO {

    /**
     * 文件原始名称
     */
    private String originalFileName;

    /**
     * 文件对应新名称
     */
    private String newFileName;

    /**
     * 消息提示
     */
    private String message;

    public FileUploadResultDTO(String originalFileName, String newFileName) {
        this.originalFileName = originalFileName;
        this.newFileName = newFileName;
    }
}
