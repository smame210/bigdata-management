package org.bigdata.server.service;

import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

public interface IFileService {
    String uploadFile(MultipartFile file);

    void downloadFile(String url, HttpServletResponse response);
}
