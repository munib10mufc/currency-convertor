package com.currency.converter.service;

import com.currency.converter.vo.CalculateBillRequest;

import java.math.BigDecimal;

public interface DiscountCalculator {

	boolean isApplicable(final CalculateBillRequest request, final BigDecimal amount);

	BigDecimal calculateDiscount(final BigDecimal amount, final CalculateBillRequest request);

}
