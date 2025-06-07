package org.example.plastinka2.repository;

import org.example.plastinka2.models.Order;
import org.example.plastinka2.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
