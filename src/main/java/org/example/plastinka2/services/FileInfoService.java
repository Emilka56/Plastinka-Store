package org.example.plastinka2.services;

import org.example.plastinka2.models.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface FileInfoService {
    FileInfo saveImage(MultipartFile uploadImage);
    List<FileInfo> saveImages(MultipartFile[] uploadImages);

    void writeImageToResponse(String imageName, HttpServletResponse response);
}