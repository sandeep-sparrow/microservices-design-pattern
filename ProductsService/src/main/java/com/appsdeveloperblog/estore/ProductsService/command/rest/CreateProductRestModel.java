package com.appsdeveloperblog.estore.ProductsService.command.rest;

import java.math.BigDecimal;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Data;

@Data
public class CreateProductRestModel {
	
	private String title;
	
	@Min(value = 1, message = "Price cannot be lower than 1")
	private BigDecimal price;
	
	@Min(value = 1, message = "Quantity cannot be lower than 1")
	@Max(value = 5, message = "Quantity cannot be greater than 5")
	private Integer quantity;
	
}
