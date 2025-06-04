package org.example.plastinka2.services;

import lombok.extern.slf4j.Slf4j;
import org.example.plastinka2.dto.ITunesAlbumDto;
import org.example.plastinka2.models.FileInfo;
import org.example.plastinka2.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.*;


@Service
@Slf4j
public class ITunesServiceImpl implements ITunesService{
    @Value("${itunes.url}")
    private String ITUNES_API_URL;

    @Autowired
    private RestTemplate itunesRestTemplate;
    @Autowired
    private ProductService productService;
    @Autowired
    private ImageDownloadService imageDownloadService;
    private final Random random = new Random();

    public ITunesServiceImpl(RestTemplate itunesRestTemplate, ProductService productService, ImageDownloadService imageDownloadService) {
        this.itunesRestTemplate = itunesRestTemplate;
        this.productService = productService;
        this.imageDownloadService = imageDownloadService;
    }

    public List<Product> importAlbumsByGenre(String genre, int count) {
        try {
            List<ITunesAlbumDto> albums = searchAlbums(genre);
            
            List<Product> importedProducts = new ArrayList<>();
            
            for (ITunesAlbumDto album : albums) {
                if (importedProducts.size() >= count) {
                    break;
                }
                
                try {
                    
                    if (!productService.existsByArtistAndAlbum(album.getArtist(), album.getAlbum())) {
                        Product product = convertToProduct(album);
                        if (product != null) {
                            productService.save(product);
                            importedProducts.add(product);
                        }
                    }
                } catch (Exception e) {
                }
            }
            

            if (importedProducts.isEmpty()) {
                List<Product> existingProducts = productService.findByGenre(genre);
                if (!existingProducts.isEmpty()) {
                    importedProducts.addAll(existingProducts.subList(0, Math.min(count, existingProducts.size())));
                }
            }
            
            return importedProducts;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при импорте альбомов: " + e.getMessage());
        }
    }

    public List<ITunesAlbumDto> searchAlbums(String query) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(ITUNES_API_URL)
                    .queryParam("term", query)
                    .queryParam("entity", "song")
                    .queryParam("attribute", "genreTerm")
                    .queryParam("limit", "50")
                    .build()
                    .encode()
                    .toUriString();
            
            log.info("Searching iTunes with URL: {}", url);
            ResponseEntity<String> response = itunesRestTemplate.getForEntity(url, String.class);
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            List<ITunesAlbumDto> albums = new ArrayList<>();
            
            if (!root.has("results")) {
                log.warn("No results found in iTunes response");
                return albums;
            }
            
            root.get("results").forEach(result -> {
                try {
                    ITunesAlbumDto album = mapper.treeToValue(result, ITunesAlbumDto.class);
                    if (album.getArtist() != null && album.getAlbum() != null) {
                        log.debug("Found album: {} by {} with preview URL: {}", 
                            album.getAlbum(), album.getArtist(), album.getPreviewUrl());
                        albums.add(album);
                    }
                } catch (Exception e) {
                    log.error("Error parsing album from iTunes response", e);
                }
            });
            
            return albums;
        } catch (Exception e) {
            log.error("Error searching iTunes", e);
            throw new RuntimeException("Ошибка при поиске в iTunes: " + e.getMessage());
        }
    }
    
    public Product convertToProduct(ITunesAlbumDto album) {
        if (album == null || album.getAlbum() == null) {
            log.warn("Invalid album data received");
            return null;
        }
        
        try {
            log.debug("Converting album to product. Preview URL: {}", album.getPreviewUrl());
            Product product = Product.builder()
                    .artist(album.getArtist())
                    .album(album.getAlbum())
                    .genre(album.getGenre())
                    .price(generateRandomPrice())
                    .previewUrl(album.getPreviewUrl())
                    .images(new ArrayList<>())
                    .singleOrders(new ArrayList<>())
                    .carts(new ArrayList<>())
                    .reviews(new ArrayList<>())
                    .build();


            if (album.getArtworkUrl() != null && !album.getArtworkUrl().isEmpty()) {
                String highResUrl = album.getArtworkUrl().replace("100x100", "600x600");
                FileInfo imageInfo = imageDownloadService.downloadAndSaveImage(highResUrl);
                if (imageInfo != null) {
                    imageInfo.setProduct(product);
                    product.getImages().add(imageInfo);
                }
            }
            
            return product;
        } catch (Exception e) {
            log.error("Error converting album to product", e);
            return null;
        }
    }
    
    public BigDecimal generateRandomPrice() {
        return BigDecimal.valueOf(1000 + random.nextInt(4001));
    }
} 