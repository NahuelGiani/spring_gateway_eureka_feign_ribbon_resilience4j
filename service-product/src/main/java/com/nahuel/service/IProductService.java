package com.nahuel.service;

import java.util.List;

import com.nahuel.entity.Product;

public interface IProductService {

	public List<Product> findAll();
	public Product findById(Long id);
	
}
