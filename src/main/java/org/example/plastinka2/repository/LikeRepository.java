package org.example.plastinka2.repository;

import org.example.plastinka2.models.Like;
import org.example.plastinka2.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
}
