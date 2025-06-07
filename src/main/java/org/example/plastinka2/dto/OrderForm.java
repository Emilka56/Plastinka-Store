package org.example.plastinka2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderForm {
    private long id;
    private String ordering_time;
    private String delivery_time;
    private int price;
    private int id_user;
    private int id_address;

    private String city;
    private String street;
    private int house_number;
    private int apartment_number;
    private String index;
}