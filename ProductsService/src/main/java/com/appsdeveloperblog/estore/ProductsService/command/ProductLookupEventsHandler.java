package com.appsdeveloperblog.estore.ProductsService.command;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import com.appsdeveloperblog.estore.ProductsService.core.data.ProductLookupEntity;
import com.appsdeveloperblog.estore.ProductsService.core.data.ProductLookupRepository;
import com.appsdeveloperblog.estore.ProductsService.core.events.ProductCreatedEvent;

@Component
@ProcessingGroup("product-group")
public class ProductLookupEventsHandler {
	
	private final ProductLookupRepository productLookupRepository;
	
	public ProductLookupEventsHandler(ProductLookupRepository productLookupRepository) {
		this.productLookupRepository = productLookupRepository;
	}
	
	@EventHandler
	public void on(ProductCreatedEvent event) {
		
		ProductLookupEntity productLookupEntity = new ProductLookupEntity(event.getProductId(), event.getTitle());
		
		System.out.println("Saving Product Lookup Event - Entity to H2 Database");
		productLookupRepository.save(productLookupEntity);
		
	}
}
