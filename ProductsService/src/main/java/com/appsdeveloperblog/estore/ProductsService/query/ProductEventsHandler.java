package com.appsdeveloperblog.estore.ProductsService.query;


import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.appsdeveloperblog.estore.ProductsService.core.data.ProductEntity;
import com.appsdeveloperblog.estore.ProductsService.core.data.ProductsRepository;
import com.appsdeveloperblog.estore.ProductsService.core.events.ProductCreatedEvent;
import com.appsdeveloperblog.estore.core.events.ProductReservationCancelledEvent;
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
		
		LOGGER.debug("ProdcutReservedEvent: current product quantity: " + productEntity.getQuantity());
		
		System.out.println("Saving Product Entity on reserved Event - Entity to H2 Database");
		
		productEntity.setQuantity(productEntity.getQuantity() - event.getQuantity());
		
		try {
			productsRepository.save(productEntity);
			LOGGER.debug("ProdcutReservedEvent: new product quantity: " + productEntity.getQuantity());
			LOGGER.info("ProductReservedEvent is saved for producId: " + event.getProductId() + 
					" and orderId: " + event.getOrderId());
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		// for testing the Event Error propagation to controller advice class
		// if(true) throw new Exception("Forcing the exception in the event handler class");
		
	}
	
	@EventHandler
	public void on(ProductReservationCancelledEvent event) {
		
		ProductEntity currentProductEntity = new ProductEntity();
		
		LOGGER.debug("ProductReservationCancelledEvent: current product quantity: " + event.getQuantity());
		
		// fetch the existing order
		currentProductEntity = productsRepository.findByProductId(event.getProductId());
		
		int newQuantity = currentProductEntity.getQuantity() + event.getQuantity();
		
		currentProductEntity.setQuantity(newQuantity);
		
		productsRepository.save(currentProductEntity);
		
		LOGGER.debug("ProductReservationCancelledEvent: new product quantity: " + event.getQuantity());
	}

}
