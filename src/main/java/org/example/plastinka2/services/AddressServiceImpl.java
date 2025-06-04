package org.example.plastinka2.services;

import org.example.plastinka2.dto.OrderForm;
import org.example.plastinka2.dto.ProductForm;
import org.example.plastinka2.models.Address;
import org.example.plastinka2.models.FileInfo;
import org.example.plastinka2.models.Product;
import org.example.plastinka2.repository.AddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

    @Autowired
    AddressRepository addressRepository;

    @Override
    @Transactional
    public void save(Address address) {
        try {
            addressRepository.save(address);
            logger.info("Successfully saved address with ID: {}", address.getId());
        } catch (DataAccessException e) {
            logger.error("Database error while saving address", e);
            throw new RuntimeException("Error saving address to database", e);
        } catch (Exception e) {
            logger.error("Unexpected error while saving address", e);
            throw new RuntimeException("Error saving address", e);
        }
    }
}
