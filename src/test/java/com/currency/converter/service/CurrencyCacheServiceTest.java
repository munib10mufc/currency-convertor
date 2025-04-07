package com.currency.converter.service;

import com.currency.converter.exceptions.ExchangeRateConnectivityException;
import com.currency.converter.vo.external.CurrencyConversionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyCacheServiceTest {

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private CurrencyCacheService currencyCacheService;

	@BeforeEach
	void setUp() {
		currencyCacheService.setApiKey("test-api-key");
		currencyCacheService.setApiUrl("https://api.exchange.com");
	}

	@Test
	void getCachedExchangeRates_shouldReturnRatesForSuccessfulResponse() {
		// Mock response
		CurrencyConversionResponse mockResponse = new CurrencyConversionResponse();
		mockResponse.setConversionRates(Map.of("EUR", 0.85, "JPY", 110.0));

		when(restTemplate.getForEntity(anyString(), eq(CurrencyConversionResponse.class)))
				.thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

		// Test
		CurrencyConversionResponse result = currencyCacheService.getCachedExchangeRates("USD");

		// Verify
		assertNotNull(result);
		assertEquals(0.85, result.getRateForCurrency("EUR"));
		verify(restTemplate).getForEntity(
				"https://api.exchange.com/test-api-key/latest/USD",
				CurrencyConversionResponse.class
		);
	}

	@Test
	void getCachedExchangeRates_shouldThrowOnNon2xxResponse() {
		when(restTemplate.getForEntity(anyString(), any()))
				.thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_GATEWAY));

		assertThrows(RuntimeException.class, () ->
				currencyCacheService.getCachedExchangeRates("USD")
		);
	}

	@Test
	void getCachedExchangeRates_shouldThrowOnNullResponseBody() {
		when(restTemplate.getForEntity(anyString(), any()))
				.thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

		assertThrows(RuntimeException.class, () ->
				currencyCacheService.getCachedExchangeRates("USD")
		);
	}

	@Test
	void getCachedExchangeRates_shouldThrowConnectivityExceptionOnTimeout() {
		when(restTemplate.getForEntity(anyString(), any()))
				.thenThrow(new ResourceAccessException("Timeout", new SocketTimeoutException()));

		assertThrows(ExchangeRateConnectivityException.class, () ->
				currencyCacheService.getCachedExchangeRates("USD")
		);
	}

	@Test
	void getCachedExchangeRates_shouldThrowConnectivityExceptionOnRestClientError() {
		when(restTemplate.getForEntity(anyString(), any()))
				.thenThrow(new RestClientException("Service unavailable"));

		assertThrows(ExchangeRateConnectivityException.class, () ->
				currencyCacheService.getCachedExchangeRates("USD")
		);
	}

	@Test
	void getCachedExchangeRates_shouldUseDifferentCacheKeysForDifferentCurrencies() {
		// Mock responses
		CurrencyConversionResponse usdResponse = new CurrencyConversionResponse();
		usdResponse.setConversionRates(Map.of("EUR", 0.85));

		CurrencyConversionResponse eurResponse = new CurrencyConversionResponse();
		eurResponse.setConversionRates(Map.of("USD", 1.18));

		when(restTemplate.getForEntity(contains("/USD"), any()))
				.thenReturn(new ResponseEntity<>(usdResponse, HttpStatus.OK));
		when(restTemplate.getForEntity(contains("/EUR"), any()))
				.thenReturn(new ResponseEntity<>(eurResponse, HttpStatus.OK));

		// Call for different currencies
		CurrencyConversionResponse usdResult = currencyCacheService.getCachedExchangeRates("USD");
		CurrencyConversionResponse eurResult = currencyCacheService.getCachedExchangeRates("EUR");

		// Verify different results
		assertEquals(0.85, usdResult.getRateForCurrency("EUR"));
		assertEquals(1.18, eurResult.getRateForCurrency("USD"));
		verify(restTemplate, times(2)).getForEntity(anyString(), any());
	}

}
