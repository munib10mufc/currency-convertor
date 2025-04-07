package com.currency.converter.service;

import com.currency.converter.vo.CalculateBillRequest;
import com.currency.converter.vo.CalculatedBill;

public interface BillCalculator {

	CalculatedBill calculateBill(final CalculateBillRequest request);
}
