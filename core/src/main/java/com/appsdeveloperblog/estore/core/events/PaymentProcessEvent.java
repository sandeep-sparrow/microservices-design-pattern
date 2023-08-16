package com.appsdeveloperblog.estore.core.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PaymentProcessEvent {

	
	private final String paymentId;
	private final String orderId;
	
}
