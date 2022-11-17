package com.example.apiserver.controller;

import com.example.apiserver.dto.MemberFormDto;
import com.example.apiserver.dto.TokenInfo;
import com.example.apiserver.dto.UserLoginRequestDto;
import com.example.apiserver.entity.member.Member;
import com.example.apiserver.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final MemberService userService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public TokenInfo login(@RequestBody UserLoginRequestDto userLoginRequestDto) {
        String userId = userLoginRequestDto.getUserId();
        String password = userLoginRequestDto.getPassword();
        TokenInfo tokenInfo = userService.login(userId, password);
        return tokenInfo;
    }

    @PostMapping("/sign")
    public String sign(@RequestBody MemberFormDto memberFormDto) {
        Member signMember = Member.createMember(memberFormDto, passwordEncoder);

        Member result = userService.saveMember(signMember);

        if (result != null) {
            return "success";
        }

        return "fail";
    }

    @PostMapping("/refreshToken")
    public String refreshToken(@RequestBody TokenInfo tokenInfo) {
        return userService.refreshToken(tokenInfo).orElseGet(() -> {
            return "invalid";
        });
    }

    @PostMapping("/test")
    public String test() {
        return "success";
    }
}
