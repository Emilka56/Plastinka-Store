package org.example.plastinka2.services;

import org.example.plastinka2.dto.ProductForm;
import org.example.plastinka2.models.Product;

import java.util.List;

public interface ProductService {
    List<Product> findAll();

    boolean saveProduct(ProductForm productForm);

    Product getProductById(Long id);

    Product findById(Long productId);

    void removeById(Long id);

    void save(Product product);

    boolean existsByArtistAndAlbum(String artist, String album);

    List<Product> findByGenre(String genre);
}
