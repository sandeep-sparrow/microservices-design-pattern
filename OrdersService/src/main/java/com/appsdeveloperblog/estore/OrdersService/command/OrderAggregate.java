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
		
		// validate create order command
				if(createOrderCommand.getProductId() == null) {
			throw new IllegalStateException("Product Id cannot be empty");
		}
		if(createOrderCommand.getAddressId() == null) {
			throw new IllegalStateException("Address Id cannot be empty");
		}
		
		OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
		BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);
		
		AggregateLifecycle.apply(orderCreatedEvent);
		
		// if(true) throw new Exception("An Error took place in the CreateOrderCommand @CommandHandler method");
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
