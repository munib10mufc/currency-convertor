package com.currency.converter.service;

import java.math.BigDecimal;

public interface CurrencyConverter {
	BigDecimal convertAmount(final BigDecimal amount, String fromCurrency, String toCurrency);
}
