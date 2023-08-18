package com.appsdeveloperblog.estore.OrdersService.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.appsdeveloperblog.estore.OrdersService.core.data.OrderEntity;
import com.appsdeveloperblog.estore.OrdersService.core.data.OrdersRepository;
import com.appsdeveloperblog.estore.OrdersService.core.event.OrderApprovedEvent;
import com.appsdeveloperblog.estore.OrdersService.core.event.OrderCreatedEvent;
import com.appsdeveloperblog.estore.OrdersService.core.event.OrderRejectedEvent;

@Component
@ProcessingGroup("order-group")
public class OrderEventsHandler {

	private final OrdersRepository ordersRepository;
	
	public OrderEventsHandler(OrdersRepository ordersRepository) {
		this.ordersRepository = ordersRepository;
	}
	
	@EventHandler
	public void on(OrderCreatedEvent event) {
		
		OrderEntity orderEntity = new OrderEntity();
		BeanUtils.copyProperties(event, orderEntity);
		
		System.out.println("Saving Order Created Event - Entity to H2 Database");
		
		try {
			ordersRepository.save(orderEntity);
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void on(OrderApprovedEvent event) {
		
		OrderEntity orderEntity = new OrderEntity();
		
		// fetch the existing order
		orderEntity = ordersRepository.findByOrderId(event.getOrderId());
		
		if(orderEntity == null) {
			// TODO: Do something about it
			return;
		}
		
		orderEntity.setOrderStatus(event.getOrderStatus());
		
		System.out.println("Updating Order Approved event - Entity to H2 Database");
		ordersRepository.save(orderEntity);
	}
	
	@EventHandler
	public void on(OrderRejectedEvent event) {
		
		OrderEntity orderEntity = new OrderEntity();
		
		// fetch the existing order
		orderEntity = ordersRepository.findByOrderId(event.getOrderId());
		
		if(orderEntity == null) {
			// TODO: Do something about it
			return;
		}
		
		orderEntity.setOrderStatus(event.getOrderStatus());
		
		System.out.println("Updating Order Rejected event - Entity to H2 Database");
		ordersRepository.save(orderEntity);
	}
	
}
