package com.currency.converter.vo;

import com.currency.converter.vo.enums.ItemCategory;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
public class Product {
	@NotNull(message = "Product name is required")
	private String name;
	@NotNull(message = "Product price is required")
	private BigDecimal price;
	@NotNull(message = "Product category is required")
	private ItemCategory category;
}
