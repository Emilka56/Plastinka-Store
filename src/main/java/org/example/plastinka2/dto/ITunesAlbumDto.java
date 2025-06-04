package org.example.plastinka2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ITunesAlbumDto {
    @JsonProperty("trackId")
    private Long id;
    
    @JsonProperty("artistName")
    private String artist;
    
    @JsonProperty("collectionName")
    private String album;
    
    @JsonProperty("primaryGenreName")
    private String genre;
    
    @JsonProperty("trackPrice")
    private Double price;
    
    @JsonProperty("artworkUrl100")
    private String artworkUrl;
    
    @JsonProperty("releaseDate")
    private String releaseDate;
    
    @JsonProperty("trackCount")
    private Integer trackCount;
    
    @JsonProperty("country")
    private String country;
    
    @JsonProperty("currency")
    private String currency;
    
    @JsonProperty("previewUrl")
    private String previewUrl;
} 