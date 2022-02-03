package com.nahuel.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.nahuel.client.ProductClientRest;
import com.nahuel.dto.ItemDTO;
import com.nahuel.service.IItemService;

@Service
@Primary
public class ItemServiceFeign implements IItemService {

	@Autowired
	private ProductClientRest productClientFeign;
	
	@Override
	public List<ItemDTO> findAll() {
		return productClientFeign.getAll()
				.stream()
				.map(product -> new ItemDTO(product, 1))
				.collect(Collectors.toList());
	}

	@Override
	public ItemDTO findById(Long id, Integer amount) {
		return new ItemDTO(productClientFeign.getById(id), amount);
	}

}
