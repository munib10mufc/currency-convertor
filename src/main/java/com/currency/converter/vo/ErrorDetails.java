package com.currency.converter.vo;

import java.util.List;

public record ErrorDetails(
		List<String> messages
) {
	public ErrorDetails(List<String> messages) {
		this.messages = messages;
	}
}
