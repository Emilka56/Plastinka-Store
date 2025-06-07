package org.example.plastinka2.services;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.example.plastinka2.models.FileInfo;
import org.example.plastinka2.repository.FileInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class FileInfoServiceImpl implements FileInfoService {
    @Value("${file.url}")
    private String fileUrl;

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Override
    public FileInfo saveImage(MultipartFile uploadFile) {
        String imageStorageName = UUID.randomUUID() + "_" + uploadFile.getOriginalFilename();

        FileInfo image = FileInfo.builder()
                .contentType(uploadFile.getContentType())
                .size(uploadFile.getSize())
                .originalName(uploadFile.getOriginalFilename())
                .storageName(imageStorageName)
                .url(fileUrl + "/" + imageStorageName)
                .build();

        try {
            Files.copy(uploadFile.getInputStream(), Paths.get(fileUrl, imageStorageName));

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        fileInfoRepository.save(image);

        return image;
    }

    @Override
    public List<FileInfo> saveImages(MultipartFile[] uploadFiles) {
        List<FileInfo> imageInfoList = new ArrayList<>();

        for (MultipartFile file : uploadFiles) {
            if (!file.isEmpty()) {
                FileInfo image = saveImage(file);
                imageInfoList.add(image);
            }
        }
        return imageInfoList;
    }



    @Override
    public void writeImageToResponse(String fileName, HttpServletResponse response) {
        FileInfo file = fileInfoRepository.findByStorageName(fileName);
        if (file == null) {
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;

        }
        response.setContentType(file.getContentType());

        try {
            IOUtils.copy(new FileInputStream(file.getUrl()), response.getOutputStream());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
