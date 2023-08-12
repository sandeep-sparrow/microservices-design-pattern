package com.appsdeveloperblog.estore.OrdersService.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import com.appsdeveloperblog.estore.OrdersService.core.data.OrderStatus;
import com.appsdeveloperblog.estore.OrdersService.core.event.OrderCreatedEvent;

import lombok.Data;

@Aggregate
@Data
public class OrderAggregate {

	@AggregateIdentifier
	private String orderId;
	private String userId;
	private String productId;
	private int quantity;
	private String addressId;
	private OrderStatus orderStatus;
	
	public OrderAggregate() {
		
	}
	
	@CommandHandler
	public OrderAggregate(CreateOrderCommand createOrderCommand) {
		
		OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
		BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);
		
		AggregateLifecycle.apply(orderCreatedEvent);
	}
	
	@EventSourcingHandler
	public void on(OrderCreatedEvent orderCreatedEvent) {
		
		this.orderId = orderCreatedEvent.getOrderId();
		this.userId = orderCreatedEvent.getUserId();
		this.productId = orderCreatedEvent.getProductId();
		this.quantity = orderCreatedEvent.getQuantity();
		this.addressId = orderCreatedEvent.getAddressId();
		this.orderStatus = orderCreatedEvent.getOrderStatus();
	}
}
