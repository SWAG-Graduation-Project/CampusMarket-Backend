package com.campusmarket.backend.domain.store.mapper;

import com.campusmarket.backend.domain.member.entity.MemberProfile;
import com.campusmarket.backend.domain.store.dto.response.MyStoreLatestProductResDto;
import com.campusmarket.backend.domain.store.dto.response.MyStoreMainResDto;
import com.campusmarket.backend.domain.store.dto.response.StoreDetailResDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StoreMapper {

    public StoreDetailResDto toStoreDetailResDto(
            MemberProfile memberProfile,
            Long totalProductCount
    ) {
        return StoreDetailResDto.of(
                memberProfile.getMember().getId(),
                memberProfile.getNickname(),
                memberProfile.getProfileImageUrl(),
                memberProfile.getStoreStartedAt(),
                memberProfile.getSaleCount(),
                memberProfile.getPurchaseCount(),
                totalProductCount
        );
    }

    public MyStoreMainResDto toMyStoreMainResDto(
            MemberProfile memberProfile,
            List<MyStoreLatestProductResDto> latestProducts
    ) {
        return MyStoreMainResDto.of(
                memberProfile.getMember().getId(),
                memberProfile.getNickname(),
                memberProfile.getProfileImageUrl(),
                memberProfile.getSaleCount(),
                memberProfile.getPurchaseCount(),
                latestProducts
        );
    }
}