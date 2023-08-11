package com.appsdeveloperblog.estore.ProductService.command.rest;

import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloperblog.estore.ProductService.command.CreateProductCommand;

@RestController
@RequestMapping("/api/products") //http://localhost:8080/api/products
public class ProductCommandController {
	

	private final Environment environment;
	private final CommandGateway commandGateway;
	
	@Autowired
	public ProductCommandController(Environment environment, CommandGateway commandGateway) {
		this.environment = environment;
		this.commandGateway = commandGateway;
	}
	
	@PostMapping
	public String createProduct(@RequestBody CreateProductRestModel createProductRestModel) {
		
		CreateProductCommand createProductCommand = CreateProductCommand.builder()
		.price(createProductRestModel.getPrice())
		.quantity(createProductRestModel.getQuantity())
		.title(createProductRestModel.getTitle())
		.productId(UUID.randomUUID().toString()).build();
		
		String returnValue;
		
		try {
			returnValue = commandGateway.sendAndWait(createProductCommand);
		}catch (Exception e) {
			returnValue = e.getLocalizedMessage();
		}
		
		// return "HTTP POST Handled " + createProductCommand.getTitle();
		return "Product Id: " + returnValue + "Port No: " + environment.getProperty("local.server.port");
	}
	
	@PutMapping
	public String updateProduct() {
		return "HTTP PUT Handled";
	}
	
	@DeleteMapping
	public String deleteProduct() {
		return "HTTP DELETE Handled";
	}
	
}
