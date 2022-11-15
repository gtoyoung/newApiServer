package com.example.apiserver.service;

import com.example.apiserver.dto.TokenInfo;
import com.example.apiserver.entity.member.Member;
import com.example.apiserver.entity.member.MemberRole;
import com.example.apiserver.entity.member.MemberRoleMapping;
import com.example.apiserver.provider.JwtTokenProvider;
import com.example.apiserver.repository.MemberRepository;
import com.example.apiserver.repository.MemberRoleMappingRepository;
import com.example.apiserver.repository.MemberRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository userRepository;
    private final MemberRoleRepository roleRepository;
    private final MemberRoleMappingRepository mappingRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenInfo login(String userId, String password) {
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, password);

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        return tokenInfo;
    }


    public Member  saveMember(Member member) {
        validateDuplicateMember(member);
        Member response = userRepository.save(member);
        addRoleToUser(response, "USER");

        return response;
    }

    public Member getMember(String userId){
        return userRepository.findByUserId(userId).orElseGet(()->{return null;});
//        return userRepository.getMemberByUserId(userId);
    }

    public void addRoleToUser(Member member, String roleName) {
        MemberRole role = roleRepository.findByName(roleName);
        MemberRoleMapping mapping = MemberRoleMapping.builder()
                .member(member)
                .memberRole(role)
                .build();


        member.addRole(mapping);
        mappingRepository.save(mapping);
    }

    private void validateDuplicateMember(Member member) {
        Optional<Member> findMember = userRepository.findByUserId(member.getUserId());

        if(!findMember.isEmpty()) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }
}
