package com.example.apiserver.service;

import com.example.apiserver.entity.member.Member;
import com.example.apiserver.entity.member.MemberRoleMapping;
import com.example.apiserver.repository.MemberRepository;
import com.example.apiserver.repository.MemberRoleMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository userRepository;
    private final MemberRoleMappingRepository mappingRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 사용자 조회
        Optional<Member> findMember = userRepository.findByUserId(username);

        // 사용자 role 조회 및 셋팅
        List<MemberRoleMapping> mapping = mappingRepository.findAllByMember(findMember.orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다.")));
        findMember.get().setRoles(mapping);

        return findMember.map(this::createUserDetails).orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
    }

    /**
     * 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 리턴
     *
     * @param user
     * @return
     */
    private UserDetails createUserDetails(Member user) {
        // 사용자 Role값 이어붙이기
        String roles = user.getRoles().stream().map((d) -> {
            return d.getMemberRole().getName();
        }).collect(Collectors.joining(","));
        return User.builder()
                .username(user.getUserId())
                .password(user.getPassword())
                .roles(roles)
                .build();
    }
}
