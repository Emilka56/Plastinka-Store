package org.example.plastinka2.services;

import lombok.extern.slf4j.Slf4j;
import org.example.plastinka2.models.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class ImageDownloadService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${file.url}")
    private String uploadDir;
    
    public FileInfo downloadAndSaveImage(String imageUrl) {
        try {
            // Создаем директорию, если её нет
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Генерируем уникальное имя файла
            String originalFileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String storageName = UUID.randomUUID().toString() + fileExtension;
            
            // Скачиваем изображение
            byte[] imageBytes = restTemplate.getForObject(imageUrl, byte[].class);
            if (imageBytes == null) {
                throw new IOException("Failed to download image");
            }
            
            // Сохраняем файл
            Path filePath = uploadPath.resolve(storageName);
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                fos.write(imageBytes);
            }
            
            // Создаем FileInfo
            return FileInfo.builder()
                    .originalName(originalFileName)
                    .storageName(storageName)
                    .contentType("image/jpeg") // iTunes всегда возвращает JPEG
                    .size(imageBytes.length)
                    .url(uploadDir + "/" + storageName)
                    .build();
            
        } catch (Exception e) {
            log.error("Error downloading image from iTunes: {}", e.getMessage());
            return null;
        }
    }
} 