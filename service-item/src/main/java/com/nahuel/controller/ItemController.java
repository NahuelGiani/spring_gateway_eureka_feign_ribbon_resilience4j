package com.nahuel.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nahuel.dto.ItemDTO;
import com.nahuel.dto.ProductDTO;
import com.nahuel.service.IItemService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@RestController
@RequestMapping("/item")
public class ItemController {
	
	private final Logger logger = LoggerFactory.getLogger(ItemController.class);
	
	@Autowired
	private CircuitBreakerFactory circuitBreakerFactory;

	@Autowired
	private IItemService itemService;
	
	@GetMapping
	public List<ItemDTO> getAll(@RequestParam(name = "nombre", required = false) String nombre, 
			@RequestHeader(name = "Token-Request", required = false) String header){
		System.out.println(nombre);
		System.out.println(header);
		return itemService.findAll();
	}
	
	//ESTA CON EL FACTORY Y LA DE ABAJO CON ANOTACIONES. DEJO LAS DOS PARA DESPUÉS RECORDAR COMO SON
	@GetMapping("/{id}/amount/{amount}")
	public ItemDTO getById(@PathVariable Long id, @PathVariable Integer amount){
		return circuitBreakerFactory.create("itemsBreaker")
				.run(() -> itemService.findById(id, amount), e -> alternativeMethod(id, amount, e));
	}
	
	// ESTA CON ANOTACIONES DE CIRCUIT BREAKER Y LA DE ARRIBA CON FACTORY. DEJO LAS DOS PARA DESPUÉS RECORDAR COMO SON
	// COSA IMPORTANTE: SI USA ANOTACIONES, SOLO SIRVE LA CONFIGURACION POR ARCHIVO (Application.yml), NO TOMA LO DEL APPCONFIG.JAVA
	@CircuitBreaker(name = "itemsBreaker", fallbackMethod = "alternativeMethod")
	@GetMapping("/{id}/amount2/{amount}")
	public ItemDTO getById2(@PathVariable Long id, @PathVariable Integer amount){
		return itemService.findById(id, amount);
	}
	
	// ESTA CON ANOTACIONES DE TIMELIIMITER Y LA DE ARRIBA CON FACTORY. DEJO LAS DOS PARA DESPUÉS RECORDAR COMO SON
	// COSA IMPORTANTE: SI USA ANOTACIONES, SOLO SIRVE LA CONFIGURACION POR ARCHIVO (Application.yml), NO TOMA LO DEL APPCONFIG.JAVA
	// CON @TimeLimiter NECESITÁS USAR CompletableFuture PORQUE SINO NO TIENE COMO CALCULAR EL TIEMPO DE EJECUCIÓN
	// LA DIFERENCIA ENTRE ESTE Y @CIRCUITBREAKER ES QUE ESTE NO CONTABILIZA LOS ERRORES. ES DECIR, SIEMPRE VA A ESTAR EN ESTADO CERRADO
	// SOLO ES PARA TIMEOUT
	@TimeLimiter(name = "itemsBreaker",  fallbackMethod = "alternativeMethod2")
	@GetMapping("/{id}/amount3/{amount}")
	public CompletableFuture<ItemDTO> getById3(@PathVariable Long id, @PathVariable Integer amount){
		return CompletableFuture.supplyAsync(() -> itemService.findById(id, amount));
	}
	
	// TAMBIÉN SE PUEDEN COMBINAR, EL FALLBACK VA EN CIRCUITBREAKER O NINGUNO, SI LO PONES EN TIMELIMITER NO FUNCIONA EL CIRCUITBREAKER
	@CircuitBreaker(name = "itemsBreaker", fallbackMethod = "alternativeMethod2")
	@TimeLimiter(name = "itemsBreaker")
	@GetMapping("/{id}/amount4/{amount}")
	public CompletableFuture<ItemDTO> getById4(@PathVariable Long id, @PathVariable Integer amount){
		return CompletableFuture.supplyAsync(() -> itemService.findById(id, amount));
	}

	public ItemDTO alternativeMethod(Long id, Integer amount, Throwable e){
		logger.info(e.getMessage());
		ItemDTO item = new ItemDTO();
		ProductDTO product = new ProductDTO();
		item.setAmount(amount);
		product.setId(id);
		product.setName("Prueba");
		product.setPrice(10000.0);
		item.setProduct(product);
		return item;
	}
	
	public CompletableFuture<ItemDTO> alternativeMethod2(Long id, Integer amount, Throwable e){
		logger.info(e.getMessage());
		ItemDTO item = new ItemDTO();
		ProductDTO product = new ProductDTO();
		item.setAmount(amount);
		product.setId(id);
		product.setName("Prueba");
		product.setPrice(10000.0);
		item.setProduct(product);
		return CompletableFuture.supplyAsync(() -> item);
	}
}
