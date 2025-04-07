package com.currency.converter.controller;

import com.currency.converter.security.JwtUtil;
import com.currency.converter.service.BillCalculator;
import com.currency.converter.vo.CalculatedBill;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CurrencyConverterControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BillCalculator billCalculator;

	@Autowired  // Inject your actual JwtUtil
	private JwtUtil jwtUtil;

	private String validToken;

	@BeforeEach
	void setUp() {
		// Generate a fresh valid token before each test
		validToken = jwtUtil.generateToken("testUser");
	}

	@Test
	void calculateBill_shouldReturn200WithRuntimeGeneratedToken() throws Exception {
		when(billCalculator.calculateBill(any()))
				.thenReturn(new CalculatedBill("USD", BigDecimal.valueOf(100)));

		mockMvc.perform(post("/api/calculate")
						.header("Authorization", "Bearer " + validToken)
						.contentType("application/json")
						.content("{\"itemsList\":[],\"originalCurrency\":\"USD\",\"targetCurrency\":\"EUR\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.currency").value("USD"))
				.andExpect(jsonPath("$.rate").value(100));
	}

	@Test
	void calculateBill_shouldReturn403WithExpiredToken() throws Exception {
		// Manually create expired token
		String expiredToken = Jwts.builder()
				.setSubject("testUser")
				.setIssuedAt(new Date(System.currentTimeMillis() - 10000))
				.setExpiration(new Date(System.currentTimeMillis() - 5000))
				.signWith(SignatureAlgorithm.HS256, "yourSecretKey")
				.compact();

		mockMvc.perform(post("/api/calculate")
						.header("Authorization", "Bearer " + expiredToken)
						.contentType("application/json")
						.content("{\"itemsList\":[],\"originalCurrency\":\"USD\",\"targetCurrency\":\"EUR\"}"))
				.andExpect(status().isForbidden());
	}
}
