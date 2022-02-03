package com.nahuel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nahuel.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

}
