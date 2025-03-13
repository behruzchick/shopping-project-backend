package com.example.demo.repository;

import com.example.demo.model.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, Long> {
    Optional<CartItems> findByProductId(Long id);
    @Modifying
    @Query("DELETE FROM CartItems c WHERE c.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}
