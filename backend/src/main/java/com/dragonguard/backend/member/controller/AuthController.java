package com.dragonguard.backend.member.controller;

import com.dragonguard.backend.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 김승진
 * @description OAuth2를 위한 임시 컨트롤러
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/refresh")
    public ResponseEntity<String> authorize(
            @RequestHeader String refreshToken,
            @RequestHeader String accessToken,
            HttpServletRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken, accessToken, request, response));
    }
}
