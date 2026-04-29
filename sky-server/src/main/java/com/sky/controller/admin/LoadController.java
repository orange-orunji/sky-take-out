package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/common")
@ApiOperation("通用接口-文件上传")
public class LoadController {

    @Resource
    private AliOssUtil aliOssUtil;
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){


        try {
            log.info("文件上传:{}", file.getOriginalFilename());
            String end = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
            String name = UUID.randomUUID().toString()+end;
            return Result.success(aliOssUtil.upload(file.getBytes(), name));
        } catch (IOException e) {
            log.info("文件上传异常:{}", e);
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }

    }
}
