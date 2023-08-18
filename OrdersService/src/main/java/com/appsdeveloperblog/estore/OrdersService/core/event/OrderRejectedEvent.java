package com.appsdeveloperblog.estore.OrdersService.core.event;

import com.appsdeveloperblog.estore.OrdersService.core.data.OrderStatus;


import lombok.Value;


@Value
public class OrderRejectedEvent {
	
	public final String orderId;
	private final OrderStatus orderStatus = OrderStatus.REJECTED;

}
