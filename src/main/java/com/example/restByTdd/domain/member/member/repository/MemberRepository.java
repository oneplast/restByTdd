package com.example.restByTdd.domain.member.member.repository;

import com.example.restByTdd.domain.member.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    Optional<Member> findByApiKey(String apiKey);
}