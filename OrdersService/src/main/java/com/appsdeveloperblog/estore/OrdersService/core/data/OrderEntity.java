package com.appsdeveloperblog.estore.OrdersService.core.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "orders")
public class OrderEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1157685819164230117L;
	
	@Id
	@Column(unique = true)
	private String orderId;
	private String userId;
	private String productId;
	private int quantity;
	private String addressId;
	
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

}
