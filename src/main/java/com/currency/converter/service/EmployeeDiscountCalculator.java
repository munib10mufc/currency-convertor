package com.currency.converter.service;

import com.currency.converter.vo.CalculateBillRequest;
import com.currency.converter.vo.enums.UserType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@Order(1)
public class EmployeeDiscountCalculator implements DiscountCalculator {

	@Value("${discount.employee.percentage:0.3}")
	private double discountEmployeePercentage;

	@Override
	public boolean isApplicable(final CalculateBillRequest request, final BigDecimal amount) {
		return UserType.EMPLOYEE.equals(request.getUserType());
	}

	@Override
	public BigDecimal calculateDiscount(final BigDecimal amount, final CalculateBillRequest request) {
		var discount = amount.multiply(BigDecimal.valueOf(discountEmployeePercentage));
		log.info("Employee discount = " + discount);
		return discount;
	}
}
