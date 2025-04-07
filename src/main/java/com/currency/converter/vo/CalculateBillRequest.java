package com.currency.converter.vo;

import com.currency.converter.vo.enums.UserType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CalculateBillRequest {
	private List<@Valid Product> itemsList;
	private UserType userType;
	private LocalDate customerSince;
	@NotBlank(message = "Original currency is required")
	@Size(min = 3, max = 3, message = "Original currency code must be 3 characters")
	private String originalCurrency;
	@NotBlank(message = "Target currency is required")
	@Size(min = 3, max = 3, message = "Target currency code must be 3 characters")
	private String targetCurrency;
}
