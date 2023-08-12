package com.appsdeveloperblog.estore.OrdersService.core.event;

import com.appsdeveloperblog.estore.OrdersService.core.data.OrderStatus;

import lombok.Data;

@Data
public class OrderCreatedEvent {
	
	private String orderId;
	private String userId;
	private String productId;
	private int quantity;
	private String addressId;
	private OrderStatus orderStatus;

}
