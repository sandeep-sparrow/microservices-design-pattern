package com.appsdeveloperblog.estore.OrdersService.command.rest;

import java.util.UUID;

import javax.validation.Valid;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloperblog.estore.OrdersService.command.CreateOrderCommand;
import com.appsdeveloperblog.estore.OrdersService.core.data.OrderStatus;

@RestController
@RequestMapping("/api/orders")
public class OrdersCommandController {
	
	private final Environment environment;
	private final CommandGateway commandGateway;
	
	@Autowired
	public OrdersCommandController(Environment environment, CommandGateway commandGateway) {
		this.environment = environment;
		this.commandGateway = commandGateway;
	}
	
	@PostMapping
	public String createOrder(@Valid @RequestBody CreateOrderRestModel createOrderRestModel) {
		
		CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
		.orderId(UUID.randomUUID().toString())
		.userId("27b95829-4f3f-4ddf-8983-151ba010e35b")
		.productId(createOrderRestModel.getProductId())
		.quantity(createOrderRestModel.getQuantity())
		.addressId(createOrderRestModel.getAddressId())
		.orderStatus(OrderStatus.CREATED)
		.build();
		
		String returnValue;
		
		returnValue = commandGateway.sendAndWait(createOrderCommand);
	
		return "Order Id: " + returnValue + " Port No: " + environment.getProperty("local.server.port");
	}

}
