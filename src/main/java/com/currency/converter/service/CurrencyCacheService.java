package com.currency.converter.service;

import com.currency.converter.exceptions.ExchangeRateConnectivityException;
import com.currency.converter.vo.external.CurrencyConversionResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
@Setter
public class CurrencyCacheService {

	private final RestTemplate restTemplate;

	@Value("${exchange.api.key}")
	String apiKey;

	@Value("${exchange.api.url}")
	String apiUrl;

	@Cacheable("exchangeRates")
	@SneakyThrows
	public CurrencyConversionResponse getCachedExchangeRates(String fromCurrency) {
		log.info("Fetching FRESH exchange rates for currency: {}", fromCurrency);

		String url = String.format("%s/%s/latest/%s", apiUrl, apiKey, fromCurrency);

		try {
			ResponseEntity<CurrencyConversionResponse> response = restTemplate.getForEntity(
					url, CurrencyConversionResponse.class);

			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				return response.getBody();
			}
		} catch (Exception e) {
			if (e instanceof SocketTimeoutException || e instanceof RestClientException || e instanceof ResourceAccessException) {
				throw new ExchangeRateConnectivityException(String.format("Unable to connect to url = [%s]", apiUrl));
			}
		}

		throw new RuntimeException("Failed to fetch exchange rates");
	}
}
