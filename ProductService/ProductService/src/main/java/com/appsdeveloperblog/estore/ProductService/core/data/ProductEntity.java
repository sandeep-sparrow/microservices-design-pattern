package com.appsdeveloperblog.estore.ProductService.core.data;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;


@Data
@Entity
@Table(name = "products")
public class ProductEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1042277019361386443L;
	
	@Id
	@Column(unique = true)
	private String productId;
	
	private String title;
	
	private BigDecimal price;
	
	private Integer quantity;

}
