package org.example.plastinka2.services;


import org.example.plastinka2.models.User;
import org.example.plastinka2.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    @Autowired
    private UserRepository userRepository;


    @Override
    public void deleteConfirmedCodeAndSave(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(IllegalStateException::new);
        user.setConfirmed("CONFIRMED");
        user.setConfirmCode(null);
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(IllegalStateException::new);
        return user;
    }

    @Override
    public User getUserFromSession() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("Attempting to get user from session with email: {}", email);

            return userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.error("User not found for email: {}", email);
                        return new UsernameNotFoundException("User not found with email: " + email);
                    });
        } catch (Exception e) {
            logger.error("Error getting user from session", e);
            throw e;
        }
    }

    @Override
    public User findByConfirmCode(String code) {
        User user = userRepository.findByConfirmCode(code).orElseThrow(IllegalStateException::new);
        return user;
    }
}
