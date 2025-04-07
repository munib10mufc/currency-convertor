package com.currency.converter.vo.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CurrencyConversionResponse {
	@JsonProperty("conversion_rates")
	private Map<String, Double> conversionRates;

	public double getRateForCurrency(String targetCurrency) {
		return conversionRates.getOrDefault(targetCurrency, -1.0);
	}
}
