package com.currency.converter.service;

import com.currency.converter.vo.CalculateBillRequest;
import com.currency.converter.vo.CalculatedBill;
import com.currency.converter.vo.Product;
import com.currency.converter.vo.enums.ItemCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillCalculatorServiceImplTest {

	@Mock
	private CurrencyConverter currencyConverterService;

	@Mock
	private DiscountService discountService;

	@InjectMocks
	private BillCalculatorServiceImpl billCalculatorService;

	private CalculateBillRequest request;
	private List<Product> products;

	@BeforeEach
	void setUp() {
		products = Arrays.asList(
				new Product("TShirt", BigDecimal.valueOf(50), ItemCategory.CLOTHING),
				new Product("Drinks", BigDecimal.valueOf(75), ItemCategory.GROCERY)
		);

		request = new CalculateBillRequest();
		request.setItemsList(products);
		request.setOriginalCurrency("USD");
		request.setTargetCurrency("EUR");
	}

	@Test
	void calculateBill_shouldCalculateCorrectlyWithAllSteps() {
		// Setup mocks
		when(discountService.calculateDiscount(any(BigDecimal.class), any(CalculateBillRequest.class)))
				.thenReturn(BigDecimal.valueOf(10));
		when(currencyConverterService.convertAmount(any(BigDecimal.class), anyString(), anyString()))
				.thenReturn(BigDecimal.valueOf(100));

		// Execute
		CalculatedBill result = billCalculatorService.calculateBill(request);

		// Verify
		assertEquals("EUR", result.getCurrency());
		assertEquals(BigDecimal.valueOf(100), result.getRate());

		// Verify interactions
		verify(discountService).calculateDiscount(BigDecimal.valueOf(125), request);
		verify(currencyConverterService).convertAmount(
				BigDecimal.valueOf(110),  // 125 (base) - 10 (discount) - 5 (bulk) = 110
				"USD",
				"EUR"
		);
	}

	@Test
	void calculateBill_shouldHandleEmptyProductList() {
		request.setItemsList(Collections.emptyList());

		when(currencyConverterService.convertAmount(eq(BigDecimal.ZERO), anyString(), anyString()))
				.thenReturn(BigDecimal.ZERO);

		CalculatedBill result = billCalculatorService.calculateBill(request);

		assertEquals("EUR", result.getCurrency());
		assertEquals(BigDecimal.ZERO, result.getRate());
		verifyNoInteractions(discountService);
	}

	@Test
	void calculateBill_shouldApplyBulkDiscountCorrectly() {
		// Setup products with amount that qualifies for bulk discount
		products = List.of(new Product("Wrist Watch", BigDecimal.valueOf(350), ItemCategory.OTHER));
		request.setItemsList(products);

		when(discountService.calculateDiscount(any(), any())).thenReturn(BigDecimal.ZERO);
		when(currencyConverterService.convertAmount(any(), anyString(), anyString()))
				.thenReturn(BigDecimal.valueOf(315));

		CalculatedBill result = billCalculatorService.calculateBill(request);

		verify(currencyConverterService).convertAmount(
				BigDecimal.valueOf(335),
				"USD",
				"EUR"
		);
	}

	@Test
	void calculateBaseAmount_shouldReturnZeroForEmptyList() {
		BigDecimal result = billCalculatorService.calculateBaseAmount(Collections.emptyList());
		assertEquals(BigDecimal.ZERO, result);
	}

	@Test
	void calculateBaseAmount_shouldReturnZeroForNullList() {
		BigDecimal result = billCalculatorService.calculateBaseAmount(null);
		assertEquals(BigDecimal.ZERO, result);
	}

	@Test
	void calculateBaseAmount_shouldSumProductPrices() {
		List<Product> testProducts = Arrays.asList(
				new Product("Shampoo", BigDecimal.valueOf(10), ItemCategory.GROCERY),
				new Product("Soap", BigDecimal.valueOf(20), ItemCategory.GROCERY),
				new Product("Perfume", BigDecimal.valueOf(30), ItemCategory.GROCERY)
		);

		BigDecimal result = billCalculatorService.calculateBaseAmount(testProducts);
		assertEquals(BigDecimal.valueOf(60), result);
	}

	@Test
	void calculateBaseAmount_shouldIgnoreNullPrices() {
		List<Product> testProducts = Arrays.asList(
				new Product("Bread", BigDecimal.valueOf(10), ItemCategory.GROCERY),
				new Product("Oil", null, ItemCategory.GROCERY),
				new Product("Milk", BigDecimal.valueOf(30), ItemCategory.GROCERY)
		);

		BigDecimal result = billCalculatorService.calculateBaseAmount(testProducts);
		assertEquals(BigDecimal.valueOf(40), result);
	}

	@Test
	void calculateBulkDiscount_shouldReturnCorrectDiscount() {
		assertEquals(BigDecimal.ZERO, billCalculatorService.calculateBulkDiscount(BigDecimal.valueOf(99)));
		assertEquals(BigDecimal.valueOf(5), billCalculatorService.calculateBulkDiscount(BigDecimal.valueOf(100)));
		assertEquals(BigDecimal.valueOf(5), billCalculatorService.calculateBulkDiscount(BigDecimal.valueOf(199)));
		assertEquals(BigDecimal.valueOf(10), billCalculatorService.calculateBulkDiscount(BigDecimal.valueOf(200)));
		assertEquals(BigDecimal.valueOf(15), billCalculatorService.calculateBulkDiscount(BigDecimal.valueOf(350)));
	}
}
