package com.irctc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.irctc.response.ErrorResponse;

@RestControllerAdvice
public class CommonExceptionHandler 
{
	@ExceptionHandler(InsufficientBalanceException.class)
	public ResponseEntity<ErrorResponse> handleInsufficientBalanceException(InsufficientBalanceException balanceException)
	{
		// This block will be executed when there is InsufficientBalanceException
		
		ErrorResponse errorResponse=new ErrorResponse("BE-120",balanceException.getMessage());
		
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		
	}
	
	@ExceptionHandler(Exception.class)
	public void handleInException(Exception exception)
	{
		// this block will get executed if Exception
	}
}

