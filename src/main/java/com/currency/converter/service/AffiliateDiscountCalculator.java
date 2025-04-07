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
@Order(2)
public class AffiliateDiscountCalculator implements DiscountCalculator {

	@Value("${discount.affiliate.percentage:0.1}")
	private double discountAffiliatePercentage;


	@Override
	public boolean isApplicable(final CalculateBillRequest request, final BigDecimal amount) {
		return UserType.AFFILIATE.equals(request.getUserType());
	}

	@Override
	public BigDecimal calculateDiscount(final BigDecimal amount, final CalculateBillRequest request) {
		var discount = amount.multiply(BigDecimal.valueOf(discountAffiliatePercentage));
		log.info("Affiliate discount = " + discount);
		return discount;
	}
}
