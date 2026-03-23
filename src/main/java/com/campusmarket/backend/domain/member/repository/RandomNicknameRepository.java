package com.campusmarket.backend.domain.member.repository;

import com.campusmarket.backend.domain.member.constant.RandomNicknameWordType;
import com.campusmarket.backend.domain.member.entity.RandomNickname;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RandomNicknameRepository extends JpaRepository<RandomNickname,Long> {

    List<RandomNickname> findByWordTypeAndActiveTrue(RandomNicknameWordType wordType);
}
