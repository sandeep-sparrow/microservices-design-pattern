package com.appsdeveloperblog.estore.OrdersService.saga;

import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.appsdeveloperblog.estore.OrdersService.core.event.OrderCreatedEvent;
import com.appsdeveloperblog.estore.core.commands.ReserveProductCommand;
import com.appsdeveloperblog.estore.core.events.ProductReservedEvent;
import com.appsdeveloperblog.estore.core.model.User;
import com.appsdeveloperblog.estore.core.query.FetchUserPaymentDetailsQuery;

@Saga
public class OrderSaga {
	
	@Autowired
	private transient CommandGateway commandGateway;
	
	@Autowired
	private transient QueryGateway queryGateway;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);
	
	@StartSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderCreatedEvent event) {
		
		ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
				.orderId(event.getOrderId())
				.productId(event.getProductId())
				.quantity(event.getQuantity())
				.userId(event.getUserId())
				.build();
		
		LOGGER.info("OrderCreatedEvent handled for orderId: " + reserveProductCommand.getOrderId() + 
				" and productId: " + reserveProductCommand.getProductId());
		
		commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>(){

			@Override
			public void onResult(CommandMessage<? extends ReserveProductCommand> commandMessage,
					CommandResultMessage<? extends Object> commandResultMessage) {
				if(commandResultMessage.isExceptional()) {
					// start a compensating transaction
					System.out.println(commandResultMessage.exceptionResult());
				}
			}
		});
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(ProductReservedEvent productReservedEvent) {
		// Process User payment..
		LOGGER.info("ProductReservedEvent is called for producId: " + productReservedEvent.getProductId() + 
				" and orderId: " + productReservedEvent.getOrderId());
		
		FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery = 
				new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());
		
		User userPaymenDetials = null;
		
		try{
			userPaymenDetials = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
		}catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			
			// Start compensating transaction
			return;
		}
		
		if(userPaymenDetials == null) {
			
			// Start compensating transaction
			return;
		}
		
		LOGGER.info("Successfully fetched the user payment details for user " + userPaymenDetials.getFirstName());
	}

}