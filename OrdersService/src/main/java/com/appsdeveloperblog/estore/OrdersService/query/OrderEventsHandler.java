package com.appsdeveloperblog.estore.OrdersService.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.appsdeveloperblog.estore.OrdersService.core.data.OrderEntity;
import com.appsdeveloperblog.estore.OrdersService.core.data.OrdersRepository;
import com.appsdeveloperblog.estore.OrdersService.core.event.OrderCreatedEvent;

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
		
		System.out.println("Saving Order Event - Entity to H2 Database");
		
		try {
			ordersRepository.save(orderEntity);
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	}
	
}
