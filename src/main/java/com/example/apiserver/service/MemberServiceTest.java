package com.example.apiserver.service;

import com.example.apiserver.dto.MemberFormDto;
import com.example.apiserver.entity.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
//@Transactional
@TestPropertySource(properties = {"spring.config.location=classpath:application.yml"})
public class MemberServiceTest {
    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember() {
        MemberFormDto memberFormDto = MemberFormDto.builder()
                .userId("userA").password("1234").nick("Ban").build();

        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void saveMemberTest() {
        Member member = createMember();
        Member savedMember = memberService.saveMember(member);

        assertEquals(member.getUserId(), savedMember.getUserId());
    }

    @Test
    @DisplayName("회원 호출 테스트")
    public void callMember() {
        Member findMember = memberService.getMember("userA");

        assertEquals("userA", findMember.getUserId());

    }
}
