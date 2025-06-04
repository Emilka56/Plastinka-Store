package org.example.plastinka2.services;

import org.example.plastinka2.models.User;
import org.example.plastinka2.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public void deleteConfirmedCodeAndSave(Long userId) {
        logger.info("Attempting to confirm and save user with ID: {}", userId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        logger.error("User not found with ID: {}", userId);
                        return new IllegalStateException("User not found with ID: " + userId);
                    });

            logger.debug("Found user: {} (email: {})", user.getId(), user.getEmail());
            user.setConfirmed("CONFIRMED");
            user.setConfirmCode(null);

            User savedUser = userRepository.save(user);
            logger.info("Successfully confirmed and saved user with ID: {}", savedUser.getId());
        } catch (Exception e) {
            logger.error("Error confirming user with ID: {}", userId, e);
            throw e;
        }
    }

    @Override
    public User findByEmail(String email) {
        logger.info("Searching for user by email: {}", email);
        try {
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                logger.debug("Found user with email: {}", email);
                return userOptional.get();
            } else {
                logger.warn("No user found with email: {}", email);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error searching for user by email: {}", email, e);
            throw e;
        }
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
        logger.info("Searching for user by confirmation code");
        try {
            User user = userRepository.findByConfirmCode(code)
                    .orElseThrow(() -> {
                        logger.error("Invalid confirmation code: {}", code);
                        return new IllegalStateException("Invalid confirmation code");
                    });
            logger.debug("Found user with ID: {} for confirmation code", user.getId());
            return user;
        } catch (Exception e) {
            logger.error("Error finding user by confirmation code: {}", code, e);
            throw e;
        }
    }

    @Override
    public void save(User user) {
        logger.info("Attempting to save user with email: {}", user.getEmail());
        try {
            User savedUser = userRepository.save(user);
            logger.info("Successfully saved user with ID: {}", savedUser.getId());
        } catch (Exception e) {
            logger.error("Error saving user with email: {}", user.getEmail(), e);
            throw e;
        }
    }
}