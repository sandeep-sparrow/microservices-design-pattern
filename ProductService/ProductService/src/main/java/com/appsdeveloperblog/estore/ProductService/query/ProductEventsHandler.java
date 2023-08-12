package com.appsdeveloperblog.estore.ProductService.query;


import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.appsdeveloperblog.estore.ProductService.core.data.ProductEntity;
import com.appsdeveloperblog.estore.ProductService.core.data.ProductsRepository;
import com.appsdeveloperblog.estore.ProductService.core.events.ProductCreatedEvent;

@Component
@ProcessingGroup("product-group")
public class ProductEventsHandler {
	
	private final ProductsRepository productsRepository;
	
	public ProductEventsHandler(ProductsRepository productsRepository) {
		this.productsRepository = productsRepository;
	}
	
	@ExceptionHandler(resultType = Exception.class)
	public void handle(Exception exception) throws Exception{
		throw exception;
	}
	
	
	@ExceptionHandler(resultType = IllegalArgumentException.class)
	public void handle(IllegalArgumentException exception) {
		
	}
	
	@EventHandler
	public void on(ProductCreatedEvent event) throws Exception {
		
		ProductEntity productEntity = new ProductEntity();
		BeanUtils.copyProperties(event, productEntity);
		
		System.out.println("Saving Product Event - Entity to H2 Database");
		
		try {
			productsRepository.save(productEntity);
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		// for testing the Event Error propagation to controller advice class
		if(true) throw new Exception("Forcing the exception in the event handler class");
		
	}

}
