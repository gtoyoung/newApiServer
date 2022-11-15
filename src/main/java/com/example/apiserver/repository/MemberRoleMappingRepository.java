package com.example.apiserver.repository;

import com.example.apiserver.entity.member.Member;
import com.example.apiserver.entity.member.MemberRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRoleMappingRepository extends JpaRepository<MemberRoleMapping, Long> {

//    List<MemberRoleMapping> getMemberRoleMappingByMemberUserId(String userId);

    List<MemberRoleMapping> findAllByMember(Member member);
}
