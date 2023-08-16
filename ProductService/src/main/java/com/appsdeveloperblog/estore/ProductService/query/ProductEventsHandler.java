package com.appsdeveloperblog.estore.ProductService.query;


import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.appsdeveloperblog.estore.ProductService.core.data.ProductEntity;
import com.appsdeveloperblog.estore.ProductService.core.data.ProductsRepository;
import com.appsdeveloperblog.estore.ProductService.core.events.ProductCreatedEvent;
import com.appsdeveloperblog.estore.core.events.ProductReservedEvent;

@Component
@ProcessingGroup("product-group")
public class ProductEventsHandler {
	
	private final ProductsRepository productsRepository;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductEventsHandler.class);
	
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
		// if(true) throw new Exception("Forcing the exception in the event handler class");
		
	}
	
	@EventHandler
	public void on(ProductReservedEvent event) throws Exception {
		
		ProductEntity productEntity = productsRepository.findByProductId(event.getProductId());

		System.out.println("Saving Product Entity on reserved Event - Entity to H2 Database");
		
		productEntity.setQuantity(productEntity.getQuantity() - event.getQuantity());
		
		try {
			productsRepository.save(productEntity);
			LOGGER.info("ProductReservedEvent is saved for producId: " + event.getProductId() + 
					" and orderId: " + event.getOrderId());
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		// for testing the Event Error propagation to controller advice class
		// if(true) throw new Exception("Forcing the exception in the event handler class");
		
	}

}
