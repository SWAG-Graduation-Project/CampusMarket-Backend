package com.campusmarket.backend.controller;

import com.campusmarket.backend.dto.MemberResponse;
import com.campusmarket.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/api/members")
    public List<MemberResponse> getMembers(){
        return memberService.getAllMembers();
    }
}
