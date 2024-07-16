package com.example.apiserver.controller;

import com.example.apiserver.dto.MemberFormDto;
import com.example.apiserver.dto.TokenInfo;
import com.example.apiserver.dto.UserLoginRequestDto;
import com.example.apiserver.entity.member.Member;
import com.example.apiserver.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@ApiIgnore
@RestController
@RequiredArgsConstructor
public class UserController {
    private final MemberService userService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<TokenInfo> login(@RequestBody UserLoginRequestDto userLoginRequestDto) {
        String userId = userLoginRequestDto.getUserId();
        String password = userLoginRequestDto.getPassword();
        TokenInfo tokenInfo = userService.login(userId, password);
        return new ResponseEntity(tokenInfo, HttpStatus.OK);
    }

    @PostMapping("/sign")
    public ResponseEntity sign(@RequestBody MemberFormDto memberFormDto) {
        Member signMember = Member.createMember(memberFormDto, passwordEncoder);

        Member result = userService.saveMember(signMember);

        if (result != null) {
            return new ResponseEntity("success", HttpStatus.OK);
        }

        return new ResponseEntity("fail", HttpStatus.OK);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity refreshToken(@RequestBody TokenInfo tokenInfo) {
        String result = userService.refreshToken(tokenInfo).orElseGet(() -> {
            return "invalid";
        });

        return new ResponseEntity(result, HttpStatus.OK);
    }
}
