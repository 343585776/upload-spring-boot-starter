package me.sdevil507.supports.upload.controller;

import lombok.extern.slf4j.Slf4j;
import me.sdevil507.supports.result.ApiResultDTO;
import me.sdevil507.supports.result.ApiResultGenerator;
import me.sdevil507.supports.upload.dto.FileUploadResultDTO;
import me.sdevil507.supports.upload.exceptions.FileTypeCanNotSupportException;
import me.sdevil507.supports.upload.service.AbstractUploadFileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件上传通用Controller
 *
 * @author sdevil507
 */
@RequestMapping("/supports/upload")
@Slf4j
public class UploadController extends BaseMarkController {

    @Autowired
    private AbstractUploadFileHandler abstractUploadFileHandler;

    /**
     * 单文件上传
     *
     * @param file 上传文件
     * @return 文件上传结果
     */
    @PostMapping("/single")
    public ApiResultDTO singleUpload(MultipartFile file) {
        ApiResultDTO apiResultDTO;
        try {
            String fileName = abstractUploadFileHandler.execute(file);
            if (!StringUtils.isEmpty(fileName)) {
                apiResultDTO = ApiResultGenerator.create("success", "文件上传成功!", new FileUploadResultDTO(file.getOriginalFilename(), fileName));
            } else {
                log.error("文件上传失败:{}", "执行保存时发生异常!");
                apiResultDTO = ApiResultGenerator.create("failure", "文件上传失败!");
            }
        } catch (FileTypeCanNotSupportException e) {
            log.error("文件不在支持文件类型列表中:", e);
            apiResultDTO = ApiResultGenerator.create("failure", "不支持该拓展名文件类型上传!");
        } catch (IOException e) {
            log.error("文件上传失败:", e);
            apiResultDTO = ApiResultGenerator.create("failure", "文件上传失败!");
        }
        return apiResultDTO;
    }

    /**
     * 多文件上传
     *
     * @param files 多文件列表
     * @return 文件上传结果
     */
    @PostMapping("/multiple")
    public ApiResultDTO multipleUpload(MultipartFile[] files) {
        List<FileUploadResultDTO> resultDtos = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String fileName = abstractUploadFileHandler.execute(file);
                if (!StringUtils.isEmpty(fileName)) {
                    resultDtos.add(new FileUploadResultDTO(file.getOriginalFilename(), fileName, "文件上传成功!"));
                } else {
                    log.error("文件上传失败:{}", "执行保存时发生异常!");
                    resultDtos.add(new FileUploadResultDTO(file.getOriginalFilename(), null, "文件上传失败!"));
                }
            } catch (IOException e) {
                log.error("文件{}上传失败", file.getOriginalFilename());
                resultDtos.add(new FileUploadResultDTO(file.getOriginalFilename(), null, "文件上传失败!"));
            } catch (FileTypeCanNotSupportException e) {
                log.error("文件{}不在支持文件类型列表中!", file.getOriginalFilename());
                resultDtos.add(new FileUploadResultDTO(file.getOriginalFilename(), null, "不支持该拓展名文件类型上传!"));
            }
        }
        return ApiResultGenerator.create("success", "请求执行成功!", resultDtos);
    }
}
