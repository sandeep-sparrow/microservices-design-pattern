package com.appsdeveloperblog.estore.OrdersService.core.event;

import com.appsdeveloperblog.estore.OrdersService.core.data.OrderStatus;


import lombok.Value;


@Value
public class OrderApprovedEvent {
	
	public final String orderId;
	private final OrderStatus orderStatus = OrderStatus.APPROVED;

}
