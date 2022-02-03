package com.nahuel.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nahuel.dto.ProductDTO;

@FeignClient(name = "service-product")
@RequestMapping("/product")
public interface ProductClientRest {
		
	@GetMapping
	public List<ProductDTO> getAll();
	
	@GetMapping("/{id}")
	public ProductDTO getById(@PathVariable Long id);
	
}
