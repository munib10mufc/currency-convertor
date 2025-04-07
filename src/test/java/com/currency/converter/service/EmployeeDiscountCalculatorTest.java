package com.currency.converter.service;


import com.currency.converter.vo.CalculateBillRequest;
import com.currency.converter.vo.enums.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EmployeeDiscountCalculatorTest {

	@InjectMocks
	private EmployeeDiscountCalculator discountCalculator;

	private CalculateBillRequest request;

	@BeforeEach
	void setUp() {
		request = new CalculateBillRequest();
		ReflectionTestUtils.setField(discountCalculator, "discountEmployeePercentage", 0.1);
	}

	@Test
	void isApplicable_shouldReturnTrueForEmployeeUser() {
		request.setUserType(UserType.EMPLOYEE);
		assertTrue(discountCalculator.isApplicable(request, BigDecimal.TEN));
	}

	@Test
	void isApplicable_shouldReturnFalseForNonEmployeeUser() {
		request.setUserType(UserType.AFFILIATE);
		assertFalse(discountCalculator.isApplicable(request, BigDecimal.TEN));

		request.setUserType(UserType.CUSTOMER);
		assertFalse(discountCalculator.isApplicable(request, BigDecimal.TEN));

		request.setUserType(null);
		assertFalse(discountCalculator.isApplicable(request, BigDecimal.TEN));
	}

	@Test
	void calculateDiscount_shouldCalculateCorrectPercentage() {
		request.setUserType(UserType.EMPLOYEE);
		BigDecimal amount = BigDecimal.valueOf(100);

		BigDecimal discount = discountCalculator.calculateDiscount(amount, request);

		assertEquals(BigDecimal.valueOf(10.00), discount);
	}

	@Test
	void calculateDiscount_shouldHandleZeroAmount() {
		request.setUserType(UserType.EMPLOYEE);
		BigDecimal amount = BigDecimal.ZERO;

		BigDecimal discount = discountCalculator.calculateDiscount(amount, request);

		assertEquals(0, BigDecimal.ZERO.compareTo(discount));
	}

	@Test
	void calculateDiscount_shouldHandleCustomDiscountPercentage() {
		ReflectionTestUtils.setField(discountCalculator, "discountEmployeePercentage", 0.15);
		request.setUserType(UserType.EMPLOYEE);
		BigDecimal amount = BigDecimal.valueOf(200);

		BigDecimal discount = discountCalculator.calculateDiscount(amount, request);

		assertEquals(0, new BigDecimal("30.0").compareTo(discount));
	}

	@Test
	void calculateDiscount_shouldRoundCorrectly() {
		request.setUserType(UserType.EMPLOYEE);
		BigDecimal amount = BigDecimal.valueOf(99.99);

		BigDecimal discount = discountCalculator.calculateDiscount(amount, request);

		assertEquals(new BigDecimal("9.999"), discount);
	}
}
