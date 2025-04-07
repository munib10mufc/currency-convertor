package com.currency.converter.controller;

import com.currency.converter.security.JwtUtil;
import com.currency.converter.vo.AuthResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

	@Mock
	private JwtUtil jwtUtil;

	@InjectMocks
	private AuthController authController;

	private final String TEST_USERNAME = "testUser";
	private final String TEST_TOKEN = "test.jwt.token";

	@Test
	void authenticate_shouldReturnTokenInResponse() {
		// Arrange
		when(jwtUtil.generateToken(TEST_USERNAME)).thenReturn(TEST_TOKEN);

		// Act
		ResponseEntity<AuthResponse> response = authController.authenticate(TEST_USERNAME);

		// Assert
		assertAll(
				() -> assertEquals(HttpStatus.OK, response.getStatusCode()),
				() -> assertEquals(TEST_TOKEN, response.getBody().getJwt()),
				() -> verify(jwtUtil, times(1)).generateToken(TEST_USERNAME)
		);
	}

	@Test
	void authenticate_shouldCallJwtUtilWithCorrectUsername() {
		// Arrange
		when(jwtUtil.generateToken(anyString())).thenReturn(TEST_TOKEN);

		// Act
		authController.authenticate(TEST_USERNAME);

		// Assert
		verify(jwtUtil).generateToken(TEST_USERNAME);
	}

	@Test
	void authenticate_shouldReturn200OnSuccess() {
		// Arrange
		when(jwtUtil.generateToken(anyString())).thenReturn(TEST_TOKEN);

		// Act
		ResponseEntity<AuthResponse> response = authController.authenticate(TEST_USERNAME);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void authenticate_shouldReturnCorrectResponseStructure() {
		// Arrange
		when(jwtUtil.generateToken(anyString())).thenReturn(TEST_TOKEN);

		// Act
		ResponseEntity<AuthResponse> response = authController.authenticate(TEST_USERNAME);

		// Assert
		assertNotNull(response.getBody());
		assertInstanceOf(AuthResponse.class, response.getBody());
		assertEquals(TEST_TOKEN, response.getBody().getJwt());
	}
}
