package com.example.demo.model;

import jakarta.persistence.*;

import java.io.Serializable;
@Entity
@Table(name = "product_comments")
public class Comments implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;

}
