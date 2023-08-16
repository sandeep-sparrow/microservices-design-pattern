package com.appsdeveloperblog.estore.ProductsService;


import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import com.appsdeveloperblog.estore.ProductsService.command.interceptor.CreateProductCommandInterceptor;
import com.appsdeveloperblog.estore.ProductsService.core.errorhandling.ProductsServiceEventsErrorHandler;
import com.appsdeveloperblog.estore.core.config.AxonConfig;

@EnableDiscoveryClient
@SpringBootApplication
@Import({AxonConfig.class})
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}
	
	@Autowired
	public void registerCreateProductCommandInterceptor(ApplicationContext context, CommandBus commandBus) {
		commandBus.registerDispatchInterceptor(context.getBean(CreateProductCommandInterceptor.class));
	}
	
	@Autowired
	public void configure(EventProcessingConfigurer configurer) {
		configurer.registerListenerInvocationErrorHandler("product-group", conf -> new ProductsServiceEventsErrorHandler());
	}

}
