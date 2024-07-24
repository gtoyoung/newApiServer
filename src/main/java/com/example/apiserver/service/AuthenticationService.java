package com.example.apiserver.service;

import org.apache.http.auth.AUTH;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * packageName    : com.example.apiserver.service
 * fileName       : AuthenticationService
 * author         : user
 * date           : 2024-07-24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-24        user       최초 생성
 */
public class AuthenticationService {

    private static final String AUTH_TOKEN_HEADER_NAME = "NEWS-API-KEY";
    private static final String AUTH_TOKEN = "DovbAuth"; /* 추후에 DB로 키 발급 관련 처리하면 좋을듯 아니면 hash키로 변환하던가 ㅇㅇ*/

    public static Authentication getAuthentication(HttpServletRequest request) {
        String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);
        if(apiKey == null || !apiKey.equals(AUTH_TOKEN)) {
            throw new BadCredentialsException("Invalid API Key");
        }

        return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
    }
}
