package org.bigdata.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.service.IFileService;
import org.bigdata.server.util.R;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 文件管理
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {
    private final IFileService fileService;

    /**
     * 文件上传
     *
     * @param file 文件
     * @return url
     */
    @PostMapping("/upload")
    public R<String> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("文件上传");
        String url = fileService.uploadFile(file);
        return R.ok(url);
    }

    /**
     * 文件下载
     *
     * @param url 文件路径
     * @return 文件资源
     */
    @PostMapping("/download")
    public R<Void> downloadFile(@RequestParam("url") String url, HttpServletResponse response) {
        log.info("文件下载: {}", url);
        fileService.downloadFile(url, response);
        return R.ok();
    }
}