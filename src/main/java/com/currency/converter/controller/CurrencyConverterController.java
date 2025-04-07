package com.currency.converter.controller;

import com.currency.converter.service.BillCalculator;
import com.currency.converter.vo.CalculateBillRequest;
import com.currency.converter.vo.CalculatedBill;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyConverterController {
	@Autowired
	private BillCalculator billCalculator;

	@PostMapping("/api/calculate")
	public ResponseEntity<CalculatedBill> calculateBill(final
														@Valid @RequestBody CalculateBillRequest calculateBillRequest) {

		CalculatedBill response = billCalculator.calculateBill(calculateBillRequest);
		return ResponseEntity.ok(response);
	}

}
