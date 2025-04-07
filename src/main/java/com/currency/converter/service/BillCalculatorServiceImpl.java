package com.currency.converter.service;

import com.currency.converter.vo.CalculateBillRequest;
import com.currency.converter.vo.CalculatedBill;
import com.currency.converter.vo.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class BillCalculatorServiceImpl implements BillCalculator {

	@Autowired
	private CurrencyConverter currencyConverterService;
	@Autowired
	private DiscountService discountService;

	public CalculatedBill calculateBill(final CalculateBillRequest request) {
		// Calculate base amount
		BigDecimal amount = calculateBaseAmount(request.getItemsList());
		log.info("Base amount = " + amount);

		BigDecimal discount = null;
		// Apply discounts
		if (amount.compareTo(BigDecimal.ZERO) > 0) {
			discount = discountService.calculateDiscount(amount, request);
			log.info("Calculated discount amount = " + discount);
		}
		BigDecimal discountedAmount = amount;

		if (discount != null) {
			discountedAmount = amount.subtract(discount);
		}

		// Apply $5 per $100 discount
		BigDecimal bulkDiscount = calculateBulkDiscount(discountedAmount);
		log.info("Calculated bulkDiscount amount = " + bulkDiscount);
		discountedAmount = discountedAmount.subtract(bulkDiscount);

		log.info("discountedAmount left = " + discountedAmount);

		// Convert currency
		BigDecimal convertedAmount = currencyConverterService.convertAmount(
				discountedAmount,
				request.getOriginalCurrency(),
				request.getTargetCurrency()
		);

		log.info(String.format("targegtCurrency = [%s], convertedAmount = [%s] ", request.getTargetCurrency(), convertedAmount));
		return new CalculatedBill(request.getTargetCurrency(), convertedAmount);
	}

	BigDecimal calculateBulkDiscount(final BigDecimal amount) {
		int hundredMultiples = amount.divide(BigDecimal.valueOf(100), RoundingMode.DOWN).intValue();
		return BigDecimal.valueOf(hundredMultiples * 5);
	}

	/**
	 * Calculates the sum of all item prices in the bill.
	 *
	 * @param productList List of items in the bill
	 * @return Total amount before discounts
	 */
	BigDecimal calculateBaseAmount(final List<Product> productList) {
		if (CollectionUtils.isEmpty(productList)) {
			return BigDecimal.ZERO;
		}

		return productList.stream()
				.map(Product::getPrice)          // Extract each item's price
				.filter(Objects::nonNull)      // Ignore null prices
				.reduce(BigDecimal.ZERO, BigDecimal::add);  // Sum all prices
	}
}
