package com.appsdeveloperblog.estore.OrdersService.core.errorhandling;

import java.util.Date;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.appsdeveloperblog.estore.core.errorhandling.ErrorMessage;

@ControllerAdvice
public class OrderServiceErrorHandler {
	
	@ExceptionHandler(value = {IllegalStateException.class})
	public ResponseEntity<ErrorMessage> handleIllegalStateException(IllegalStateException ex, WebRequest request){
		
		ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	
	@ExceptionHandler(value = {Exception.class})
	public ResponseEntity<Object> handleOtherException(Exception ex, WebRequest request){
		
		ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
