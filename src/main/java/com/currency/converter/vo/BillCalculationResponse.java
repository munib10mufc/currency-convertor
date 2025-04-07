package com.currency.converter.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class BillCalculationResponse {
	private BigDecimal amount;
	private String currency;
}
