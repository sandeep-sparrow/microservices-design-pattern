package com.appsdeveloperblog.estore.OrdersService.query.rest;

import java.util.List;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloperblog.estore.OrdersService.query.FindOrdersQuery;

import lombok.Data;

@RestController
@RequestMapping("/api/orders")
@Data
public class OrdersQueryController {
	
	private Environment environment;
	private QueryGateway queryGateway;
	
	@Autowired
	public OrdersQueryController(Environment environment, QueryGateway queryGateway) {
		this.environment = environment;
		this.queryGateway = queryGateway;
	}
	
	@GetMapping
	public List<OrderRestModel> getOrders(){
		
		FindOrdersQuery findOrdersQuery = new FindOrdersQuery();
		
		List<OrderRestModel> orders = queryGateway.query(findOrdersQuery,
				ResponseTypes.multipleInstancesOf(OrderRestModel.class)).join();
		
		return orders;
	}
	
	@GetMapping("/port")
	public String getPortNo() {
		
		return "Port No: " + environment.getProperty("local.server.port");
	}

}
