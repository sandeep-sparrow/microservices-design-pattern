package com.appsdeveloperblog.estore.OrdersService.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.appsdeveloperblog.estore.OrdersService.core.data.OrderStatus;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateOrderCommand {
	
	@TargetAggregateIdentifier
	private final String orderId;
	private final String userId;
	private final String productId;
	private final int quantity;
	private final String addressId;
	private final OrderStatus orderStatus;

}
