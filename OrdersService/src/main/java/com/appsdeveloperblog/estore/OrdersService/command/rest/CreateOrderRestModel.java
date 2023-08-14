package com.appsdeveloperblog.estore.OrdersService.command.rest;

import javax.validation.constraints.Max;

import com.appsdeveloperblog.estore.OrdersService.core.data.OrderStatus;

import lombok.Data;

@Data
public class CreateOrderRestModel {
	
	private String orderId;
	private String userId;
	private String productId;
	
	@Max(value = 5, message = "Quantity cannot be greater than 5")
	private int quantity;
	
	private String addressId;
	private OrderStatus orderStatus;

}
