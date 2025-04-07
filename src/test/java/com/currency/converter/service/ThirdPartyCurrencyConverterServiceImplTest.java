package com.currency.converter.service;
import com.currency.converter.exceptions.ConversionRateNotFoundException;
import com.currency.converter.exceptions.MandatoryConfigsMissingException;
import com.currency.converter.vo.external.CurrencyConversionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThirdPartyCurrencyConverterServiceImplTest {

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private CurrencyCacheService currencyCacheService;

	@InjectMocks
	private ThirdPartyCurrencyConverterServiceImpl currencyConverter;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(currencyConverter, "apiKey", "test-api-key");
		ReflectionTestUtils.setField(currencyConverter, "apiUrl", "https://api.exchange.com");
	}

	@Test
	void convertAmount_shouldReturnSameAmountForSameCurrency() {
		BigDecimal amount = new BigDecimal("100.00");
		BigDecimal result = currencyConverter.convertAmount(amount, "USD", "USD");

		assertEquals(amount, result);
		verifyNoInteractions(currencyCacheService);
		verifyNoInteractions(restTemplate);
	}

	@Test
	void convertAmount_shouldThrowExceptionWhenConfigMissing() {
		ReflectionTestUtils.setField(currencyConverter, "apiKey", "");
		ReflectionTestUtils.setField(currencyConverter, "apiUrl", "");

		BigDecimal amount = new BigDecimal("100.00");

		assertThrows(MandatoryConfigsMissingException.class, () ->
				currencyConverter.convertAmount(amount, "USD", "EUR")
		);
	}

	@Test
	void convertAmount_shouldConvertUsingExchangeRate() {
		CurrencyConversionResponse mockResponse = createMockResponse("EUR", 0.85);
		when(currencyCacheService.getCachedExchangeRates("USD"))
				.thenReturn(mockResponse);

		BigDecimal amount = new BigDecimal("100.00");
		BigDecimal result = currencyConverter.convertAmount(amount, "USD", "EUR");

		assertEquals(new BigDecimal("85.00000"), result);
		verify(currencyCacheService).getCachedExchangeRates("USD");
	}

	@Test
	void convertAmount_shouldThrowWhenRateNotFound() {

		CurrencyConversionResponse mockResponse = createMockResponse("GBP", 0.75);
		when(currencyCacheService.getCachedExchangeRates("USD"))
				.thenReturn(mockResponse);

		BigDecimal amount = new BigDecimal("100.00");

		assertThrows(ConversionRateNotFoundException.class, () ->
				currencyConverter.convertAmount(amount, "USD", "EUR")
		);
	}

	@Test
	void getExchangeRate_shouldReturnRoundedRate() {
		CurrencyConversionResponse mockResponse = createMockResponse("EUR", 0.851234);
		when(currencyCacheService.getCachedExchangeRates("USD"))
				.thenReturn(mockResponse);

		BigDecimal rate = currencyConverter.getExchangeRate("USD", "EUR");

		assertEquals(new BigDecimal("0.851"), rate);
	}

	@Test
	void getExchangeRate_shouldUseCacheServiceProperly() {
		CurrencyConversionResponse mockResponse = createMockResponse("EUR", 0.85);

		when(currencyCacheService.getCachedExchangeRates("USD"))
				.thenReturn(mockResponse);

		BigDecimal rate1 = currencyConverter.getExchangeRate("USD", "EUR");
		BigDecimal rate2 = currencyConverter.getExchangeRate("USD", "EUR");

		assertEquals(rate1, rate2);
		verify(currencyCacheService, atLeastOnce()).getCachedExchangeRates("USD");
	}

	@Test
	void convertAmount_shouldHandleDifferentPrecisions() {
		CurrencyConversionResponse mockResponse = createMockResponse("JPY", 110.1234);
		when(currencyCacheService.getCachedExchangeRates("USD"))
				.thenReturn(mockResponse);

		BigDecimal amount = new BigDecimal("50.50");
		BigDecimal result = currencyConverter.convertAmount(amount, "USD", "JPY");

		assertEquals(0, new BigDecimal("5561.21150").compareTo(result));
	}

	private CurrencyConversionResponse createMockResponse(String currency, double rate) {
		Map<String, Double> rates = new HashMap<>();
		rates.put(currency, rate);
		CurrencyConversionResponse response = new CurrencyConversionResponse();
		response.setConversionRates(rates);
		return response;
	}
}
