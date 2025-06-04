package org.example.plastinka2.services;

import org.example.plastinka2.dto.ITunesAlbumDto;
import org.example.plastinka2.models.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ITunesService {
    Product convertToProduct(ITunesAlbumDto album);
    BigDecimal generateRandomPrice();
    List<ITunesAlbumDto> searchAlbums(String query);
    List<Product> importAlbumsByGenre(String genre, int count);
}
