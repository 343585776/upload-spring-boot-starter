package me.sdevil507.upload.exceptions;

/**
 * 自定义文件类型不支持异常
 *
 * @author sdevil507
 */
public class FileTypeCanNotSupportException extends Exception {

    public FileTypeCanNotSupportException() {
    }

    public FileTypeCanNotSupportException(String message) {
        super(message);
    }
}
