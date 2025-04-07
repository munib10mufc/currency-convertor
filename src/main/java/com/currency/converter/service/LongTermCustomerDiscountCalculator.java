package com.currency.converter.service;

import com.currency.converter.vo.CalculateBillRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
@Order(3)
public class LongTermCustomerDiscountCalculator implements DiscountCalculator {

	@Value("${long.term.customer.discount.amount:5.00}")
	private BigDecimal longTermCustomerDiscountAmount;

	@Value(("${long.term.customer.tenure:2}"))
	private int longTermCustomerTenure;

	@Override
	public boolean isApplicable(final CalculateBillRequest request, final BigDecimal amount) {
		//apply log term customer discount if bill amount is greater that applied discount amount.
		return isLongTermCustomer(request.getCustomerSince()) &&
				amount.compareTo(longTermCustomerDiscountAmount) >= 0;
	}

	@Override
	public BigDecimal calculateDiscount(final BigDecimal amount, final CalculateBillRequest request) {
		log.info("Long term customer discount amount = " + longTermCustomerDiscountAmount);
		return longTermCustomerDiscountAmount;
	}

	boolean isLongTermCustomer(final LocalDate customerSince) {
		return customerSince != null &&
				customerSince.isBefore(LocalDate.now().minusYears(longTermCustomerTenure));
	}

}
