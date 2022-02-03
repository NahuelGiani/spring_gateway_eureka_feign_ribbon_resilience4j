package com.nahuel.service;

import java.util.List;

import com.nahuel.dto.ItemDTO;

public interface IItemService {

	public List<ItemDTO> findAll();
	public ItemDTO findById(Long id, Integer amount);
	
}
