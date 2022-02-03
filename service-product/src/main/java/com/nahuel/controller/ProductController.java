package com.nahuel.controller;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nahuel.entity.Product;
import com.nahuel.service.IProductService;

@RestController
@RequestMapping("/product")
public class ProductController {

	@Autowired
	private Environment env;

	@Autowired
	private IProductService productService;

	@GetMapping
	public List<Product> getAll() {
		return productService.findAll().stream().map(product -> {
					product.setPort(Integer.valueOf(env.getProperty("local.server.port")));
			return product;
		}).collect(Collectors.toList());

	}

	@GetMapping("/{id}")
	public Product getById(@PathVariable Long id) throws InterruptedException {
		if(id.equals(10L)) {  // PARA SIMULAR UN ERROR Y PROBAR EL CIRCUIT BREAKER DE ITEM
			throw new IllegalStateException("Product not founded");
		}
		if(id.equals(7L)) {  // PARA SIMULAR UN IMEOUT Y PROBAR EL CIRCUIT BREAKER DE ITEM
			TimeUnit.SECONDS.sleep(5L);
		}
		Product product = productService.findById(id);
		product.setPort(Integer.valueOf(env.getProperty("local.server.port")));
		return product;
	}

}
