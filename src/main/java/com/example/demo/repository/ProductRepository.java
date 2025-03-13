package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p ORDER BY p.price DESC")
    Page<Product> findByHighPrice(Pageable pageable);

    @Query("SELECT p FROM Product p ORDER BY p.price")
    Page<Product> findByLowPrice(Pageable pageable);

    @Query("SELECT p FROM Product  p ORDER BY p.rating DESC")
    Page<Product> findByTopRating(Pageable pageable);

    @Query("SELECT p FROM Product p  WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Product> findALlProductByName(@Param("name") String name , Pageable pageable);

}
