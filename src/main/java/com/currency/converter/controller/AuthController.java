package com.currency.converter.controller;

import com.currency.converter.security.JwtUtil;
import com.currency.converter.vo.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

	private final JwtUtil jwtUtil;

	public AuthController(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@GetMapping("/auth")
	public ResponseEntity<AuthResponse> authenticate(@RequestParam String username) {
		return ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken(username)));
	}

}
