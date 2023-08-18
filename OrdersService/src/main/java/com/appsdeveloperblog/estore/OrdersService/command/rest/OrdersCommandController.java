package com.appsdeveloperblog.estore.OrdersService.command.rest;

import java.util.UUID;

import javax.validation.Valid;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloperblog.estore.OrdersService.command.CreateOrderCommand;
import com.appsdeveloperblog.estore.OrdersService.core.data.OrderStatus;
import com.appsdeveloperblog.estore.OrdersService.query.FindOrderQuery;
import com.appsdeveloperblog.estore.OrdersService.query.rest.OrderSummary;


@RestController
@RequestMapping("/api/orders")
public class OrdersCommandController {
	
	private final Environment environment;
	private final CommandGateway commandGateway;
	private final QueryGateway queryGateway;
	
	@Autowired
	public OrdersCommandController(Environment environment, CommandGateway commandGateway, QueryGateway queryGateway) {
		this.environment = environment;
		this.commandGateway = commandGateway;
		this.queryGateway = queryGateway;
	}
	
	@PostMapping
	public OrderSummary createOrder(@Valid @RequestBody CreateOrderRestModel createOrderRestModel) {
		
		
		String userId = "27b95829-4f3f-4ddf-8983-151ba010e35b";
		String orderId = UUID.randomUUID().toString();
		
		CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
		.orderId(orderId)
		.userId(userId)
		.productId(createOrderRestModel.getProductId())
		.quantity(createOrderRestModel.getQuantity())
		.addressId(createOrderRestModel.getAddressId())
		.orderStatus(OrderStatus.CREATED)
		.build();
		
		
		SubscriptionQueryResult<OrderSummary, OrderSummary> queryResult =  queryGateway.subscriptionQuery(new FindOrderQuery(orderId), 
				ResponseTypes.instanceOf(OrderSummary.class),
				ResponseTypes.instanceOf(OrderSummary.class));
		
		String returnValue;

		try {
			returnValue = commandGateway.sendAndWait(createOrderCommand);
			System.out.println("Order Id: " + returnValue + " Port No: " + environment.getProperty("local.server.port"));
			return queryResult.updates().blockFirst();
		}finally {
			queryResult.close();
		}
	}

}
