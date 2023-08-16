package com.appsdeveloperblog.estore.PaymentsService.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.appsdeveloperblog.estore.core.commands.ProcessPaymentCommand;
import com.appsdeveloperblog.estore.core.events.PaymentProcessEvent;

import lombok.Data;

@Aggregate
@Data
public class PaymentAggregate {
	
	@AggregateIdentifier
	private String paymentId;
	
	private String orderId;
	
	public PaymentAggregate() {
		
	}
	
	@CommandHandler
	public PaymentAggregate(ProcessPaymentCommand processPaymentCommand) {
		
		PaymentProcessEvent paymentProcessEvent = 
				new PaymentProcessEvent(processPaymentCommand.getPaymentId(), processPaymentCommand.getOrderId());
		
		AggregateLifecycle.apply(paymentProcessEvent);		
	}
	
	@EventSourcingHandler
	public void on(PaymentProcessEvent event) {
		
		this.paymentId = event.getPaymentId();
		this.orderId = event.getOrderId();
	}

}
