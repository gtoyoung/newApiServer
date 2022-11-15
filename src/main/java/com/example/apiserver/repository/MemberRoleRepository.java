package com.example.apiserver.repository;

import com.example.apiserver.entity.member.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRoleRepository extends JpaRepository<MemberRole, Long> {
    MemberRole findByName(String name);
}
