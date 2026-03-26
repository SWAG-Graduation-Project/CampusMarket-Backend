package com.campusmarket.backend.domain.wishlist.service;

import com.campusmarket.backend.domain.member.constant.MemberErrorCode;
import com.campusmarket.backend.domain.member.entity.Member;
import com.campusmarket.backend.domain.member.exception.MemberException;
import com.campusmarket.backend.domain.member.repository.MemberRepository;
import com.campusmarket.backend.domain.product.entity.Product;
import com.campusmarket.backend.domain.product.entity.ProductSaleStatus;
import com.campusmarket.backend.domain.product.repository.ProductRepository;
import com.campusmarket.backend.domain.wishlist.constant.WishlistErrorCode;
import com.campusmarket.backend.domain.wishlist.dto.response.WishlistProductListResDto;
import com.campusmarket.backend.domain.wishlist.dto.response.WishlistProductResDto;
import com.campusmarket.backend.domain.wishlist.dto.response.WishlistToggleResDto;
import com.campusmarket.backend.domain.wishlist.entity.Wishlist;
import com.campusmarket.backend.domain.wishlist.exception.WishlistException;
import com.campusmarket.backend.domain.wishlist.repository.WishlistQueryRepository;
import com.campusmarket.backend.domain.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;
    private final WishlistQueryRepository wishlistQueryRepository;

    @Transactional
    public WishlistToggleResDto toggleWishlist(String guestUuid, Long productId) {
        validateGuestUuid(guestUuid);

        Member member = memberRepository.findByGuestUuid(guestUuid)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Product product = productRepository.findByIdAndDeletedAtIsNullForUpdate(productId)
                .orElseThrow(() -> new WishlistException(WishlistErrorCode.PRODUCT_NOT_FOUND));

        if (product.getSaleStatus() == ProductSaleStatus.DELETED) {
            throw new WishlistException(WishlistErrorCode.WISHLIST_FORBIDDEN_PRODUCT);
        }

        return wishlistRepository.findByMemberIdAndProductId(member.getId(), productId)
                .map(existingWishlist -> removeWishlist(existingWishlist, productId))
                .orElseGet(() -> addWishlist(member.getId(), productId));
    }

    public WishlistProductListResDto getWishlistProducts(
            String guestUuid,
            Integer page,
            Integer size
    ) {
        validateGuestUuid(guestUuid);
        validatePage(page, size);

        Member member = memberRepository.findByGuestUuid(guestUuid)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        long offsetLong = (long) page * size;
        int offset = Math.toIntExact(offsetLong);

        List<WishlistProductResDto> products = wishlistQueryRepository.findWishlistProducts(
                member.getId(),
                offset,
                size
        );

        long totalCount = wishlistQueryRepository.countWishlistProducts(member.getId());
        long nextBoundary = ((long) page + 1) * size;
        boolean hasNext = totalCount > nextBoundary;

        return WishlistProductListResDto.of(products, page, size, hasNext);
    }

    @Transactional
    protected WishlistToggleResDto addWishlist(Long memberId, Long productId) {
        Wishlist wishlist = Wishlist.builder()
                .memberId(memberId)
                .productId(productId)
                .createdAt(LocalDateTime.now())
                .build();

        try {
            wishlistRepository.saveAndFlush(wishlist);
        } catch (DataIntegrityViolationException e) {
            Wishlist existingWishlist = wishlistRepository.findByMemberIdAndProductId(memberId, productId)
                    .orElseThrow(() -> e);

            return removeWishlist(existingWishlist, productId);
        }

        productRepository.increaseWishCount(productId);

        Integer wishCount = productRepository.findByIdAndDeletedAtIsNull(productId)
                .map(Product::getWishCount)
                .orElse(0);

        return WishlistToggleResDto.of(
                productId,
                true,
                wishCount
        );
    }

    @Transactional
    protected WishlistToggleResDto removeWishlist(Wishlist wishlist, Long productId) {
        wishlistRepository.delete(wishlist);
        wishlistRepository.flush();

        productRepository.decreaseWishCount(productId);

        Integer wishCount = productRepository.findByIdAndDeletedAtIsNull(productId)
                .map(Product::getWishCount)
                .orElse(0);

        return WishlistToggleResDto.of(
                productId,
                false,
                wishCount
        );
    }

    private void validatePage(Integer page, Integer size) {
        if (page == null || size == null || page < 0 || size <= 0) {
            throw new WishlistException(WishlistErrorCode.INVALID_PAGE_REQUEST);
        }

        long offsetLong = (long) page * size;
        if (offsetLong > Integer.MAX_VALUE) {
            throw new WishlistException(WishlistErrorCode.INVALID_PAGE_REQUEST);
        }
    }

    private void validateGuestUuid(String guestUuid) {
        if (guestUuid == null || guestUuid.isBlank()) {
            throw new MemberException(MemberErrorCode.INVALID_GUEST_UUID);
        }
    }
}