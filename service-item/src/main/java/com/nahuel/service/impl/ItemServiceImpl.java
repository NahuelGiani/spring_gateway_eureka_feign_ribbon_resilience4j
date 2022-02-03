package com.nahuel.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.nahuel.dto.ItemDTO;
import com.nahuel.dto.ProductDTO;
import com.nahuel.service.IItemService;

@Service
public class ItemServiceImpl implements IItemService{

	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	@Transactional(readOnly = true)
	public List<ItemDTO> findAll() {
		List<ProductDTO> products = Arrays.asList(restTemplate.getForObject("http://service-product/product", ProductDTO[].class));
		return products.stream()
					.map(product -> new ItemDTO(product, 1))
					.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public ItemDTO findById(Long id, Integer amount) {
		Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put("id", id.toString());
		ProductDTO product = restTemplate.getForObject("http://service-product/product/{id}", ProductDTO.class, pathVariables);
		return new ItemDTO(product, amount);
	}


}
