package com.appsdeveloperblog.estore.OrdersService.query.rest;

import com.appsdeveloperblog.estore.OrdersService.core.data.OrderStatus;

import lombok.Value;

@Value
public class OrderSummary {
	
	private final String orderId;
	private final OrderStatus orderStatus;
	private final String message;

}
