package com.campusmarket.backend.domain.store.service;

import com.campusmarket.backend.domain.member.constant.MemberErrorCode;
import com.campusmarket.backend.domain.member.entity.Member;
import com.campusmarket.backend.domain.member.entity.MemberProfile;
import com.campusmarket.backend.domain.member.exception.MemberException;
import com.campusmarket.backend.domain.member.repository.MemberProfileRepository;
import com.campusmarket.backend.domain.member.repository.MemberRepository;
import com.campusmarket.backend.domain.product.entity.ProductSaleStatus;
import com.campusmarket.backend.domain.store.constant.StoreErrorCode;
import com.campusmarket.backend.domain.store.dto.response.*;
import com.campusmarket.backend.domain.store.exception.StoreException;
import com.campusmarket.backend.domain.store.mapper.StoreMapper;
import com.campusmarket.backend.domain.store.repository.StoreQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final StoreQueryRepository storeQueryRepository;
    private final StoreMapper storeMapper;

    public StoreListResDto getStores(Integer page, Integer size) {
        validatePage(page, size);

        long offsetLong = (long) page * size;
        int offset = Math.toIntExact(offsetLong);

        List<StoreSummaryResDto> stores =
                storeQueryRepository.findStoreSummaries(offset, size);

        long totalCount = storeQueryRepository.countStores();
        long nextBoundary = ((long) page + 1) * size;
        boolean hasNext = totalCount > nextBoundary;

        return StoreListResDto.of(stores, page, size, hasNext);
    }

    public StoreDetailResDto getStoreDetail(Long sellerId) {
        Member member = memberRepository.findById(sellerId)
                .orElseThrow(() ->
                        new StoreException(StoreErrorCode.STORE_OWNER_NOT_FOUND)
                );

        MemberProfile memberProfile = memberProfileRepository.findByMember(member)
                .orElseThrow(() ->
                        new StoreException(StoreErrorCode.STORE_PROFILE_NOT_FOUND)
                );

        long totalProductCount =
                storeQueryRepository.countProductsBySellerId(member.getId());

        return storeMapper.toStoreDetailResDto(memberProfile, totalProductCount);
    }

    public MyStoreMainResDto getMyStore(String guestUuid) {
        Member member = memberRepository.findByGuestUuid(guestUuid)
                .orElseThrow(() ->
                        new MemberException(MemberErrorCode.MEMBER_NOT_FOUND)
                );

        MemberProfile memberProfile = memberProfileRepository.findByMember(member)
                .orElseThrow(() ->
                        new StoreException(StoreErrorCode.STORE_PROFILE_NOT_FOUND)
                );

        List<MyStoreLatestProductResDto> latestProducts =
                storeQueryRepository.findLatestProductsBySellerId(member.getId(), 3);

        return storeMapper.toMyStoreMainResDto(memberProfile, latestProducts);
    }

    public MyStoreProductListResDto getMyStoreProducts(
            String guestUuid,
            String saleStatus,
            Integer page,
            Integer size
    ) {
        validatePage(page, size);

        Member member = memberRepository.findByGuestUuid(guestUuid)
                .orElseThrow(() ->
                        new MemberException(MemberErrorCode.MEMBER_NOT_FOUND)
                );

        ProductSaleStatus productSaleStatus = toProductSaleStatus(saleStatus);

        long offsetLong = (long) page * size;
        int offset = Math.toIntExact(offsetLong);

        List<MyStoreProductSummaryResDto> products =
                storeQueryRepository.findMyStoreProducts(
                        member.getId(),
                        productSaleStatus,
                        offset,
                        size
                );

        long totalCount =
                storeQueryRepository.countMyStoreProducts(
                        member.getId(),
                        productSaleStatus
                );

        long nextBoundary = ((long) page + 1) * size;
        boolean hasNext = totalCount > nextBoundary;

        return MyStoreProductListResDto.of(products, page, size, hasNext);
    }

    private void validatePage(Integer page, Integer size) {
        if (page == null || size == null || page < 0 || size <= 0) {
            throw new StoreException(StoreErrorCode.INVALID_PAGE_REQUEST);
        }
    }

    private ProductSaleStatus toProductSaleStatus(String saleStatus) {
        if (saleStatus == null || saleStatus.isBlank()) {
            return null;
        }

        try {
            return ProductSaleStatus.valueOf(saleStatus);
        } catch (IllegalArgumentException e) {
            throw new StoreException(StoreErrorCode.INVALID_SALE_STATUS);
        }
    }

    public StoreProductListResDto getStoreProducts(
            Long sellerId,
            Integer page,
            Integer size
    ) {
        validatePage(page, size);

        Member member = memberRepository.findById(sellerId)
                .orElseThrow(() ->
                        new StoreException(StoreErrorCode.STORE_OWNER_NOT_FOUND)
                );

        long offsetLong = (long) page * size;
        int offset = Math.toIntExact(offsetLong);

        List<StoreProductSummaryResDto> products =
                storeQueryRepository.findStoreProductsBySellerId(
                        member.getId(),
                        offset,
                        size
                );

        long totalCount = storeQueryRepository.countStoreProductsBySellerId(member.getId());
        long nextBoundary = ((long) page + 1) * size;
        boolean hasNext = totalCount > nextBoundary;

        return StoreProductListResDto.of(products, page, size, hasNext);
    }
}