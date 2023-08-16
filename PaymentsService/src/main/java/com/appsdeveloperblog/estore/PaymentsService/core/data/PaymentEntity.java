package com.appsdeveloperblog.estore.PaymentsService.core.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


import lombok.Data;

@Data
@Entity
@Table(name = "payments")
public class PaymentEntity implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5453471968129673503L;
	
	@Id
	private String paymentId;
	
	@Column
	public String orderId;


}
