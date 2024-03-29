package com.appsdeveloperblog.estore.OrdersService.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<OrderEntity, String> {
	
	public OrderEntity findByOrderId(String orderId);

}
