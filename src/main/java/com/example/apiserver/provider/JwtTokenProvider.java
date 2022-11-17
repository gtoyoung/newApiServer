package com.example.apiserver.provider;

import com.example.apiserver.dto.TokenInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    private final long ACCESS_TOKEN_VALID_TIME = 1 * 60 * 1000L;   // 1분
    private final long REFRESH_TOKEN_VALID_TIME = 60 * 60 * 24 * 1 * 1000L;   // 1일

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 유저 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
     *
     * @param authentication
     * @return
     */
    public TokenInfo generateToken(Authentication authentication) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Claims claims = Jwts.claims().setSubject(authentication.getName());//JWT payload에 저장되는 정보단위
        claims.put("auth", authorities);// Key/Value 쌍으로 저장된다.

        long now = (new Date()).getTime();

        // Acess Token 생성
        String accessToken = createJwtAccessToken(key, now, claims);

        // Refresh Token 생성
        String refreshToken = createJwtRefreshToken(key, now, claims);

        return TokenInfo.builder().grantType("Bearer").accessToken(accessToken).refreshToken(refreshToken).build();
    }

    /**
     * Access Token 생성
     *
     * @param key
     * @param now
     * @param claims
     * @return
     */
    public String createJwtAccessToken(Key key, long now, Claims claims) {
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(now + ACCESS_TOKEN_VALID_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return accessToken;
    }

    /**
     * Access Token 재발급
     *
     * @param userName
     * @param roles
     * @return
     */
    public String recreateAccessToken(String userName, Object roles) {
        Claims claims = Jwts.claims().setSubject(userName);//JWT payload에 저장되는 정보단위
        claims.put("auth", roles);// Key/Value 쌍으로 저장된다.
        Date now = new Date();

        String accessToken = Jwts.builder().setClaims(claims)//정보저장
                .setIssuedAt(now)//토큰 발생시간 정보
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return accessToken;
    }

    /**
     * Refresh Token 생성
     *
     * @param key
     * @param now
     * @param claims
     * @return
     */
    public String createJwtRefreshToken(Key key, long now, Claims claims) {
        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(now + REFRESH_TOKEN_VALID_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return refreshToken;
    }

    /**
     * JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
     *
     * @param accessToken
     * @return
     */
    public Authentication getAuthentication(String accessToken) {
        //토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        //클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * Access토큰 정보를 검증하는 메서드
     *
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    /**
     * Refresh 토큰 검증 및 Access 토큰 재발급 메서드
     *
     * @param tokenInfo
     * @return
     */
    public Optional<String> validateRefreshToken(TokenInfo tokenInfo) {
        try {
            //검증
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(tokenInfo.getRefreshToken());

            //refresh 토큰의 만료시간이 지나지 않았을 경우, 새로운 Access 토큰을 생성
            if (!claims.getBody().getExpiration().before(new Date())) {
                return Optional.ofNullable(recreateAccessToken(claims.getBody().get("sub").toString(), claims.getBody().get("auth")));
            }
        } catch (Exception e) {
            //refresh 토큰이 만료되었을 경우 ,로그인이 필요
            return null;
        }

        return null;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
