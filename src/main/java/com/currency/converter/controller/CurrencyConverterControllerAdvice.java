package com.currency.converter.controller;

import com.currency.converter.exceptions.ConversionRateNotFoundException;
import com.currency.converter.exceptions.ExchangeRateConnectivityException;
import com.currency.converter.exceptions.MandatoryConfigsMissingException;
import com.currency.converter.vo.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class CurrencyConverterControllerAdvice {

	@ExceptionHandler(MandatoryConfigsMissingException.class)
	public ResponseEntity<ErrorDetails> handleMandatoryConfigsMissingException(MandatoryConfigsMissingException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorDetails(List.of(ex.getMessage())));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorDetails> handleValidationExceptions(MethodArgumentNotValidException ex) {
		List<String> errorsList = new ArrayList<>();
		ex.getBindingResult().getFieldErrors().forEach(error -> {
			errorsList.add("{" + error.getField() + "} " + error.getDefaultMessage());
		});
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorDetails(errorsList));
	}

	@ExceptionHandler(ConversionRateNotFoundException.class)
	public ResponseEntity<ErrorDetails> handleConversionRateNotFoundException(ConversionRateNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorDetails(List.of(ex.getMessage())));
	}

	@ExceptionHandler(ExchangeRateConnectivityException.class)
	public ResponseEntity<ErrorDetails> handleExchangeRateConnectivityException(ExchangeRateConnectivityException ex) {
		return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
				.body(new ErrorDetails(List.of(ex.getMessage())));
	}
}
