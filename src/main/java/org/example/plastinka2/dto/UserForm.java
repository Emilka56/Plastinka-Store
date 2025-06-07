package org.example.plastinka2.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserForm {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}