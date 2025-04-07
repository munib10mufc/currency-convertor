package com.currency.converter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Configs {

	@Value("${exchange.api.connect.timeout.ms:3000}")
	private int exchangeApiConnectTimeout;
	@Value("${exchange.api.read.timeout.ms:3000}")
	private int exchangeApiReadTimeout;

	@Bean
	public RestTemplate restTemplate() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(exchangeApiConnectTimeout);
		factory.setReadTimeout(exchangeApiReadTimeout);
		return new RestTemplate(factory);
	}

}
