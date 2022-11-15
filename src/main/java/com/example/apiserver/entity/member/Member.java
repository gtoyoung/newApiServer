package com.example.apiserver.entity.member;


import com.example.apiserver.dto.MemberFormDto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_USERS")
public class Member implements UserDetails {
    @Id
    @Column(updatable = false, unique = true, nullable = false, name = "user_id")
    private String userId;

    @Column(nullable = false, name = "user_pwd")
    private String userPwd;

    @Column(nullable = false, name = "nick")
    private String nick;

    @OneToMany(mappedBy = "id", fetch = FetchType.LAZY)
    @JsonManagedReference(value = "user")
    @Builder.Default
    private List<MemberRoleMapping> roles = new ArrayList<>();

    @Builder
    public Member(String userId, String userPwd, String nick) {
        this.userId = userId;
        this.userPwd = userPwd;
        this.nick = nick;
    }

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        Member member = Member.builder().userId(memberFormDto.getUserId())
                .userPwd(passwordEncoder.encode(memberFormDto.getPassword()))
                .nick(memberFormDto.getNick())
                .build();
        return member;
    }

    public void addRole(MemberRoleMapping roleMapping) {
        this.roles.add(roleMapping);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(MemberRoleMapping::getMemberRole)
                .map(MemberRole::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return userPwd;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
