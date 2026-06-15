package org.bigdata.server.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.exception.BizException;
import org.bigdata.server.service.IFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements IFileService {
    @Value("${file.upload.path:/tmp/uploads}")
    private String uploadPath;
    
    @Override
    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        try {
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String newFileName = UUID.randomUUID() + fileExtension;
            Path filePath = uploadDir.resolve(newFileName);

            Files.copy(file.getInputStream(), filePath);
            log.info("File uploaded successfully: {}", filePath);

            return filePath.toString();
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new BizException("上传文件发生错误！");
        }
    }

    @Override
    public void downloadFile(String url, HttpServletResponse response) {
        Path filePath = Paths.get(url);
        if (!Files.exists(filePath)) {
            throw new BizException("文件不存在！");
        }

        try (InputStream inputStream = Files.newInputStream(filePath.toFile().toPath());
             OutputStream outputStream = response.getOutputStream()) {

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + filePath.getFileName());

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();

            log.info("File downloaded successfully: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to download file", e);
            throw new BizException("下载文件发生错误！");
        }
    }
}
