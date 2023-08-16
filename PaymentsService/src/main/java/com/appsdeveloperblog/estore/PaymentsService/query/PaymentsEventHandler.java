package com.appsdeveloperblog.estore.PaymentsService.query;


import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.appsdeveloperblog.estore.PaymentsService.core.data.PaymentEntity;
import com.appsdeveloperblog.estore.PaymentsService.core.data.PaymentsRepository;
import com.appsdeveloperblog.estore.core.events.PaymentProcessEvent;

@Component
@ProcessingGroup("payment-group")
public class PaymentsEventHandler {
	
	private final PaymentsRepository paymentsRepository;
	
	public PaymentsEventHandler(PaymentsRepository paymentsRepository) {
		this.paymentsRepository = paymentsRepository;
	}
	
	@EventHandler
	public void on(PaymentProcessEvent event) {
		
		
		PaymentEntity paymentEntity = new PaymentEntity();
		BeanUtils.copyProperties(event, paymentEntity);
		
		System.out.println("Saving Payment process event - Entity to H2 Database");
		
		try {
			paymentsRepository.save(paymentEntity);
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	

}
