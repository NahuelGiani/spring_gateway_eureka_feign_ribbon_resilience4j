package com.nahuel.dto;

public class ItemDTO {

	private ProductDTO product;
	private Integer amount;

	public ItemDTO() {}
	
	public ItemDTO(ProductDTO product, Integer amount) {
		super();
		this.product = product;
		this.amount = amount;
	}

	public Double getTotal() {
		return product.getPrice() * amount;
	}
	
	public ProductDTO getProduct() {
		return product;
	}

	public void setProduct(ProductDTO product) {
		this.product = product;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	
}
