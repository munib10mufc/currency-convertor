package com.currency.converter.service;

import com.currency.converter.vo.CalculateBillRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LongTermCustomerDiscountCalculatorTest {

	@InjectMocks
	private LongTermCustomerDiscountCalculator discountCalculator;

	private CalculateBillRequest request;

	@BeforeEach
	void setUp() {
		request = new CalculateBillRequest();
		// Set default values
		ReflectionTestUtils.setField(discountCalculator, "longTermCustomerDiscountAmount", new BigDecimal("5.00"));
		ReflectionTestUtils.setField(discountCalculator, "longTermCustomerTenure", 2);
	}

	@Test
	void isApplicable_shouldReturnTrueForEligibleCustomer() {
		request.setCustomerSince(LocalDate.now().minusYears(3));
		assertTrue(discountCalculator.isApplicable(request, new BigDecimal("10.00")));
	}

	@Test
	void isApplicable_shouldReturnFalseForNewCustomer() {
		request.setCustomerSince(LocalDate.now().minusYears(1));
		assertFalse(discountCalculator.isApplicable(request, new BigDecimal("10.00")));
	}

	@Test
	void isApplicable_shouldReturnFalseForNullCustomerSince() {
		request.setCustomerSince(null);
		assertFalse(discountCalculator.isApplicable(request, new BigDecimal("10.00")));
	}

	@Test
	void isApplicable_shouldReturnFalseWhenAmountLessThanDiscount() {
		request.setCustomerSince(LocalDate.now().minusYears(3));
		assertFalse(discountCalculator.isApplicable(request, new BigDecimal("4.99")));
	}

	@Test
	void isApplicable_shouldReturnTrueWhenAmountEqualsDiscount() {
		request.setCustomerSince(LocalDate.now().minusYears(3));
		assertTrue(discountCalculator.isApplicable(request, new BigDecimal("5.00")));
	}

	@Test
	void calculateDiscount_shouldReturnFixedDiscountAmount() {
		// Set custom discount amount for this test
		ReflectionTestUtils.setField(discountCalculator, "longTermCustomerDiscountAmount", new BigDecimal("7.50"));

		BigDecimal discount = discountCalculator.calculateDiscount(new BigDecimal("100.00"), request);

		assertEquals(new BigDecimal("7.50"), discount);
	}

	@Test
	void calculateDiscount_shouldReturnDiscountRegardlessOfAmount() {
		// Should return discount even for small amount (though isApplicable would prevent this in practice)
		BigDecimal discount = discountCalculator.calculateDiscount(new BigDecimal("1.00"), request);

		assertEquals(new BigDecimal("5.00"), discount);
	}

	@Test
	void isLongTermCustomer_shouldReturnTrueForCustomerOlderThanTenure() {
		LocalDate threeYearsAgo = LocalDate.now().minusYears(3);
		assertTrue(discountCalculator.isLongTermCustomer(threeYearsAgo));
	}

	@Test
	void isLongTermCustomer_shouldReturnFalseForCustomerNewerThanTenure() {
		LocalDate oneYearAgo = LocalDate.now().minusYears(1);
		assertFalse(discountCalculator.isLongTermCustomer(oneYearAgo));
	}

	@Test
	void isLongTermCustomer_shouldReturnFalseForNullDate() {
		assertFalse(discountCalculator.isLongTermCustomer(null));
	}

	@Test
	void isLongTermCustomer_shouldHandleCustomTenure() {
		// Change tenure to 5 years
		ReflectionTestUtils.setField(discountCalculator, "longTermCustomerTenure", 5);

		LocalDate fourYearsAgo = LocalDate.now().minusYears(4);
		assertFalse(discountCalculator.isLongTermCustomer(fourYearsAgo));

		LocalDate sixYearsAgo = LocalDate.now().minusYears(6);
		assertTrue(discountCalculator.isLongTermCustomer(sixYearsAgo));
	}
}
