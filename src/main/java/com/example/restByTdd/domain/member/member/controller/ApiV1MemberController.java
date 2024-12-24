package com.example.restByTdd.domain.member.member.controller;

import com.example.restByTdd.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class ApiV1MemberController {
    @PostMapping("/join")
    public RsData<Void> join() {
        return new RsData<>("201-1", "무명님 환영합니다.");
    }
}
