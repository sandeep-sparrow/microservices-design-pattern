package com.appsdeveloperblog.estore.ProductsService.core.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_lookup")
public class ProductLookupEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7321433826108748957L;


	@Id
	private String productId;
	
	@Column(unique = true)
	private String title;

}
