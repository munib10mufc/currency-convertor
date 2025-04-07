package com.currency.converter.service;

import com.currency.converter.vo.CalculateBillRequest;
import com.currency.converter.vo.enums.ItemCategory;
import com.currency.converter.vo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DiscountService {

	private final List<DiscountCalculator> discountCalculatorList;

	@Autowired
	public DiscountService(final List<DiscountCalculator> discountCalculatorList) {
		this.discountCalculatorList = discountCalculatorList;
	}

	public BigDecimal calculateDiscount(final BigDecimal amount, final CalculateBillRequest request) {
		if (containsGroceries(request.getItemsList())) {
			return BigDecimal.ZERO;
		}

		for (DiscountCalculator strategy : discountCalculatorList) {
			if (strategy.isApplicable(request, amount)) {
				return strategy.calculateDiscount(amount, request);
			}
		}

		return BigDecimal.ZERO;
	}

	private boolean containsGroceries(final List<Product> productsList) {
		return productsList.stream().anyMatch(item -> ItemCategory.GROCERY.equals(item));
	}
}
