package com.appsdeveloperblog.estore.OrdersService.command.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RejectOrderCommand {
	
	@TargetAggregateIdentifier
	private final String orderId;
	private final String reason;

}
