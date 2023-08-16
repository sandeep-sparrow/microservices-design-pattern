package com.appsdeveloperblog.estore.ProductService.query.rest;

import java.util.List;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloperblog.estore.ProductService.query.FindProductsQuery;

@RestController
@RequestMapping("/api/products")
public class ProductsQueryController {
	
	private final Environment environment;
	private final QueryGateway queryGateway;
	
	@Autowired
	public ProductsQueryController(Environment environment, QueryGateway queryGateway) {
		this.environment = environment;
		this.queryGateway = queryGateway;
	}
	
	@GetMapping()
	public List<ProductRestModel> getProducts() {
		
		FindProductsQuery findProductsQuery = new FindProductsQuery();
		
		List<ProductRestModel> products = queryGateway.query(findProductsQuery, 
				ResponseTypes.multipleInstancesOf(ProductRestModel.class)).join();
		
		return products;
	}
	
	
	@GetMapping("/port")
	public String getPortNo() {
		
		return "Port No: " + environment.getProperty("local.server.port");
	}

}
