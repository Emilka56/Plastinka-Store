package org.example.plastinka2.services;


import org.example.plastinka2.models.User;

public interface UserService {

    void deleteConfirmedCodeAndSave(Long userId);
    User findByConfirmCode(String code);
    User findByEmail(String email);
    User getUserFromSession();
    void save(User user);
}
