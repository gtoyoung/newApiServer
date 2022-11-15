package com.example.apiserver.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Getter
public class MemberFormDto {
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String userId;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 4, max = 16, message = "비밀번호는 4자 이상, 16자 이하로 입력해주세요.")
    private String password;

    @NotEmpty(message = "닉네임은 필수 입력 값입니다.")
    private String nick;

    @Builder
    public MemberFormDto(String userId, String password, String nick){
        this.userId = userId;
        this.password = password;
        this.nick = nick;
    }
}
