package com.appsdeveloperblog.estore.OrdersService.query;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.appsdeveloperblog.estore.OrdersService.core.data.OrderEntity;
import com.appsdeveloperblog.estore.OrdersService.core.data.OrdersRepository;
import com.appsdeveloperblog.estore.OrdersService.query.rest.OrderRestModel;
import com.appsdeveloperblog.estore.OrdersService.query.rest.OrderSummary;

@Component
public class OrderQueriesHandler {
	
	private final OrdersRepository ordersRepository;
	
	public OrderQueriesHandler(OrdersRepository ordersRepository) {
		this.ordersRepository = ordersRepository;
		
	}
	
	@QueryHandler
	public List<OrderRestModel> findOrders(FindOrdersQuery query)
	{
		
		List<OrderRestModel> orderRestModels = new ArrayList<>();
		
		List<OrderEntity> orderEntities = ordersRepository.findAll();
		
		for(OrderEntity orderEntity: orderEntities) {
			OrderRestModel orderRestModel = new OrderRestModel();
			BeanUtils.copyProperties(orderEntity, orderRestModel);
			orderRestModels.add(orderRestModel);
		}
		
		return orderRestModels;
	}
	
	@QueryHandler
	public OrderSummary findOrder(FindOrderQuery query) {
		
		OrderEntity orderEntity = ordersRepository.findByOrderId(query.getOrderId());
		
		OrderSummary orderSummary = new OrderSummary(orderEntity.getOrderId(), orderEntity.getOrderStatus(), "");
		return orderSummary;
	}
	

}
