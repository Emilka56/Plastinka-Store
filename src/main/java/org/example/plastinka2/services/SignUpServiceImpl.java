package org.example.plastinka2.services;


import org.example.plastinka2.dto.UserForm;
import org.example.plastinka2.models.Role;
import org.example.plastinka2.models.User;
import org.example.plastinka2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SignUpServiceImpl implements SignUpService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Override
    public void addUser(UserForm userForm) {
        User user = User.builder()
                .email(userForm.getEmail())
                .password(passwordEncoder.encode(userForm.getPassword()))
                .firstName((userForm.getFirstName()))
                .lastName((userForm.getLastName()))
                .confirmed("NOT_CONFIRMED")
                .role(Role.USER)
                .confirmCode(UUID.randomUUID().toString())
                .build();
        userRepository.save(user);
        mailService.sendEmailForConfirm(user.getEmail(), user.getConfirmCode());
    }
}
