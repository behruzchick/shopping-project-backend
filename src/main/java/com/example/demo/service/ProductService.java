package com.example.demo.service;

import com.example.demo.model.CartItems;
import com.example.demo.model.Product;
import com.example.demo.repository.CartItemsRepository;
import com.example.demo.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final CartItemsRepository cartItemsRepository;

    public ProductService(ProductRepository productRepository, CartItemsRepository cartItemsRepository) {
        this.productRepository = productRepository;
        this.cartItemsRepository = cartItemsRepository;
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products;
    }
    public Product getProduct(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        return product;
    }
    public Product updateProduct(Product current_product) {

        return productRepository.save(current_product);
    }

    public void deleteProduct(Long id) {
        cartItemsRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }

    public Page<Product> getAllProductByHighPrice(Pageable pageable) {
        Page<Product> products  = productRepository.findByHighPrice(pageable);
        return products;
    }

    public Page<Product> getAllProductByLowPrice(Pageable pageable) {
        Page<Product> products  = productRepository.findByLowPrice(pageable);
        return products;
    }
    public Product updateRating(Product current_product , Double rating) {
        Long totalVotes = current_product.getTotalVotes();

        totalVotes++;

        double updated_rating = (current_product.getRating() * (totalVotes - 1) + rating) / totalVotes;

        current_product.setTotalVotes(totalVotes);
        current_product.setRating(updated_rating);

        return productRepository.save(current_product);
    }

    public Page<Product> getAllProductByTopRated(Pageable pageable) {
        Page<Product> products = productRepository.findByTopRating(pageable);
        return products;
    }

    public Page<Product> getAllProductByName(Pageable pageable, String productName) {
        Page<Product> products = productRepository.findALlProductByName(productName , pageable);
        return products;
    }

}
