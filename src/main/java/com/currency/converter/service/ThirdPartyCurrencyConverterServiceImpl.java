package com.currency.converter.service;

import com.currency.converter.exceptions.ConversionRateNotFoundException;
import com.currency.converter.exceptions.MandatoryConfigsMissingException;
import com.currency.converter.vo.external.CurrencyConversionResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class ThirdPartyCurrencyConverterServiceImpl implements CurrencyConverter {

	@Value("${exchange.api.key}")
	private String apiKey;

	@Value("${exchange.api.url}")
	private String apiUrl;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private CurrencyCacheService currencyCacheService;

	@SneakyThrows
	public BigDecimal convertAmount(final BigDecimal amount, String fromCurrency, String toCurrency) {
		if (fromCurrency.equals(toCurrency)) {
			return amount;
		}
		if (StringUtils.isBlank(apiKey) || StringUtils.isBlank(apiUrl)) {
			log.error("apiKey or apiUrl not configured");
			throw new MandatoryConfigsMissingException("apiKey or apiUrl not configured");
		}

		BigDecimal rate = getExchangeRate(fromCurrency, toCurrency);
		return amount.multiply(rate);
	}

	@SneakyThrows
	BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
		CurrencyConversionResponse currencyConversionResponse =
				currencyCacheService.getCachedExchangeRates(fromCurrency);

		var rate = currencyConversionResponse.getRateForCurrency(toCurrency);
		if (rate == -1) {
			throw new ConversionRateNotFoundException(String.format(
					"Unable to find conversion rate between from currency = [%s] and to currency = [%s]",
					fromCurrency, toCurrency));
		}
		return BigDecimal.valueOf(rate).setScale(3, RoundingMode.HALF_UP);
	}
}
