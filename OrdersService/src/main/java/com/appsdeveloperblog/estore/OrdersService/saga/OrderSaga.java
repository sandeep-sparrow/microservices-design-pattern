package com.appsdeveloperblog.estore.OrdersService.saga;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.appsdeveloperblog.estore.OrdersService.command.commands.ApproveOrderCommand;
import com.appsdeveloperblog.estore.OrdersService.command.commands.RejectOrderCommand;
import com.appsdeveloperblog.estore.OrdersService.core.event.OrderApprovedEvent;
import com.appsdeveloperblog.estore.OrdersService.core.event.OrderCreatedEvent;
import com.appsdeveloperblog.estore.OrdersService.core.event.OrderRejectedEvent;
import com.appsdeveloperblog.estore.OrdersService.query.FindOrderQuery;
import com.appsdeveloperblog.estore.OrdersService.query.rest.OrderSummary;
import com.appsdeveloperblog.estore.core.commands.CancelProductReservationCommand;
import com.appsdeveloperblog.estore.core.commands.ProcessPaymentCommand;
import com.appsdeveloperblog.estore.core.commands.ReserveProductCommand;
import com.appsdeveloperblog.estore.core.events.PaymentProcessEvent;
import com.appsdeveloperblog.estore.core.events.ProductReservationCancelledEvent;
import com.appsdeveloperblog.estore.core.events.ProductReservedEvent;
import com.appsdeveloperblog.estore.core.model.User;
import com.appsdeveloperblog.estore.core.query.FetchUserPaymentDetailsQuery;

@Saga
public class OrderSaga {
	
	@Autowired
	private transient CommandGateway commandGateway;
	
	@Autowired
	private transient QueryGateway queryGateway;
	
	@Autowired
	private transient DeadlineManager deadlineManager;
	
	@Autowired
	private transient QueryUpdateEmitter queryUpdateEmitter;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);
	
	private final String PAYMENT_PROCESSING_TIMEOUT_DEADLINE = "payment-processing-deadline";
	
	private String scheduleId;
	
	@StartSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderCreatedEvent orderCreatedEvent) {
		
		ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
				.orderId(orderCreatedEvent.getOrderId())
				.productId(orderCreatedEvent.getProductId())
				.quantity(orderCreatedEvent.getQuantity())
				.userId(orderCreatedEvent.getUserId())
				.build();
		
		LOGGER.info("OrderCreatedEvent handled for orderId: " + reserveProductCommand.getOrderId() + 
				" and productId: " + reserveProductCommand.getProductId());
		
		commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>(){

			@Override
			public void onResult(CommandMessage<? extends ReserveProductCommand> commandMessage,
					CommandResultMessage<? extends Object> commandResultMessage) {
				if(commandResultMessage.isExceptional()) {
					// start a compensating transaction
					RejectOrderCommand rejectOrderCommand = 
							new RejectOrderCommand(orderCreatedEvent.getOrderId(),
									commandResultMessage.exceptionResult().getMessage());
					
					commandGateway.send(rejectOrderCommand);
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
			cancelProductReservation(productReservedEvent, ex.getMessage());
			return;
		}
		
		if(userPaymenDetials == null) {
			// Start compensating transaction
			cancelProductReservation(productReservedEvent, "Could not fetch user payment details");
			return;
		}
		
		LOGGER.info("Successfully fetched the user payment details for user " + userPaymenDetials.getFirstName());
		
		scheduleId = deadlineManager.schedule(Duration.of(120, ChronoUnit.SECONDS), PAYMENT_PROCESSING_TIMEOUT_DEADLINE, productReservedEvent);
		
		// TODO: remove in no test region
		// if(true) return;
		
		ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
				.orderId(productReservedEvent.getOrderId())
				.paymentDetails(userPaymenDetials.getPaymentDetails())
				.paymentId(UUID.randomUUID().toString())
				.build();
		
		String result = null;
		
		try {
		result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
		}catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			// Start compensating transaction
			cancelProductReservation(productReservedEvent, ex.getMessage());
			return;
		}
	
		if(result == null) {
			// Start compensating transaction
			cancelProductReservation(productReservedEvent, "Could not process user payment with provided payment details");
			LOGGER.info("The Process Payment resulted in NULL. Initiating a compensating transaction.");
			return;
		}
				
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(PaymentProcessEvent paymentProcessEvent) {
		
		// Cancel the deadline if the payment processing event is created
		cancelDeadline();
		
		// Invoke - Send approve order command
		ApproveOrderCommand approveOrderCommand = 
				new ApproveOrderCommand(paymentProcessEvent.getOrderId());
		
		commandGateway.send(approveOrderCommand);
	}
	
	private void cancelDeadline() {
		
		if(scheduleId != null) {
			deadlineManager.cancelSchedule(PAYMENT_PROCESSING_TIMEOUT_DEADLINE, scheduleId);
			scheduleId = null;
		}
		
	}
	
	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderApprovedEvent orderApprovedEvent) {
		
		LOGGER.info("The Order is approved, Order Saga is completed with orderId " + orderApprovedEvent.getOrderId());
		queryUpdateEmitter.emit(FindOrderQuery.class, query -> true, 
				new OrderSummary(orderApprovedEvent.getOrderId(), 
						orderApprovedEvent.getOrderStatus(),
						""));
	}
	
	@DeadlineHandler(deadlineName = PAYMENT_PROCESSING_TIMEOUT_DEADLINE)
	public void handlePaymentDeadline(ProductReservedEvent productReservedEvent) {
		
		LOGGER.info("Payment processing deadline took place. Sending a compensating transaction for order Id: " + productReservedEvent.getOrderId());
		cancelProductReservation(productReservedEvent, "Payment Timeout");
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(ProductReservationCancelledEvent productReservationCancelledEvent) {
		
		// Create and send a reject order command
		RejectOrderCommand rejectOrderCommand = 
				new RejectOrderCommand(productReservationCancelledEvent.getOrderId(), productReservationCancelledEvent.getReason());
		
		commandGateway.send(rejectOrderCommand);
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderRejectedEvent orderRejectedEvent) {
		
		LOGGER.info("The Order is rejected, Order Saga is completed with orderId " + orderRejectedEvent.getOrderId());
		queryUpdateEmitter.emit(FindOrderQuery.class, query -> true, 
				new OrderSummary(orderRejectedEvent.getOrderId(), 
						orderRejectedEvent.getOrderStatus(),
						orderRejectedEvent.getReason()));
		
		SagaLifecycle.end();
	}
	
	private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason) {
		
		cancelDeadline();
		
		CancelProductReservationCommand cancelProductReservationCommand =
				CancelProductReservationCommand.builder()
				.orderId(productReservedEvent.getOrderId())
				.productId(productReservedEvent.getProductId())
				.quantity(productReservedEvent.getQuantity())
				.userId(productReservedEvent.getUserId())
				.reason(reason)
				.build();
		
		commandGateway.send(cancelProductReservationCommand);
	}

}