package org.example.plastinka2.repository;

import org.example.plastinka2.models.Order;
import org.example.plastinka2.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByArtistAndAlbum(String artist, String album);

    @Query("SELECT p FROM Product p WHERE LOWER(p.genre) = LOWER(:genre)")
    List<Product> findByGenreCaseInsensitive(@Param("genre") String genre);
}
