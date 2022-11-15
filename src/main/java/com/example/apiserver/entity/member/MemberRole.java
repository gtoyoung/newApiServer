package com.example.apiserver.entity.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "TB_USER_ROLE")
public class MemberRole implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id")
    private Long userRoleId;

    private String name;

    @OneToMany(mappedBy = "id", fetch = FetchType.LAZY)
    private List<MemberRoleMapping> userRoleMappings = new ArrayList<>();



    @Override
    public String getAuthority() {
        return name;
    }
}
