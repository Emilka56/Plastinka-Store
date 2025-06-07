package org.example.plastinka2.dto;


import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductForm {
   private String album;
    private String artist;
    private String genre;
    private BigDecimal price;
    private MultipartFile[] images;
}
