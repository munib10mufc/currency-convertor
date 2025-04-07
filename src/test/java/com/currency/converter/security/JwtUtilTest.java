package com.currency.converter.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

	@InjectMocks
	private JwtUtil jwtUtil;

	private static final String TEST_USERNAME = "testUser";
	private static final String INVALID_TOKEN = "invalid.token.here";

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(jwtUtil, "tokenExpirationSeconds", 1800000L);
	}

	@Test
	void generateToken_shouldReturnValidToken() {
		String token = jwtUtil.generateToken(TEST_USERNAME);

		assertNotNull(token);
		assertFalse(token.isEmpty());
		assertEquals(TEST_USERNAME, jwtUtil.extractUsername(token));
	}

	@Test
	void extractUsername_shouldReturnCorrectUsername() {
		String token = jwtUtil.generateToken(TEST_USERNAME);
		String username = jwtUtil.extractUsername(token);

		assertEquals(TEST_USERNAME, username);
	}

	@Test
	void extractUsername_shouldThrowForInvalidToken() {
		assertThrows(MalformedJwtException.class, () ->
				jwtUtil.extractUsername(INVALID_TOKEN)
		);
	}

	@Test
	void validateToken_shouldReturnTrueForValidToken() {
		String token = jwtUtil.generateToken(TEST_USERNAME);
		assertTrue(jwtUtil.validateToken(token));
	}

	@Test
	void validateToken_shouldReturnFalseForInvalidToken() {
		assertFalse(jwtUtil.validateToken(INVALID_TOKEN));
	}

	@Test
	void validateToken_shouldReturnFalseForExpiredToken() {
		try (MockedStatic<Jwts> mockedJwts = mockStatic(Jwts.class)) {
			// Mock JWT parser to throw ExpiredJwtException
			when(Jwts.parser()).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

			assertFalse(jwtUtil.validateToken(INVALID_TOKEN));
		}
	}

	@Test
	void validateToken_shouldReturnFalseForMalformedToken() {
		assertFalse(jwtUtil.validateToken("malformed.token"));
	}

	@Test
	void validateToken_shouldReturnFalseForEmptyToken() {
		assertFalse(jwtUtil.validateToken(""));
		assertFalse(jwtUtil.validateToken(null));
	}

	@Test
	void tokenExpiration_shouldUseConfiguredValue() {
		long customExpiration = 3600000L; // 1 hour
		ReflectionTestUtils.setField(jwtUtil, "tokenExpirationSeconds", customExpiration);

		String token = jwtUtil.generateToken(TEST_USERNAME);
		assertTrue(jwtUtil.validateToken(token));
	}

}
