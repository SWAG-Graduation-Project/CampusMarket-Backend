package com.campusmarket.backend.domain.product.repository;

import com.campusmarket.backend.domain.product.entity.ProductTempImage;
import com.campusmarket.backend.domain.product.entity.ProductTempImageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductTempImageRepository extends JpaRepository<ProductTempImage, Long> {

    Optional<ProductTempImage> findByIdAndMember_IdAndStatus(
            Long id,
            Long memberId,
            ProductTempImageStatus status
    );

    List<ProductTempImage> findAllByIdInAndMember_IdAndStatusOrderByDisplayOrderAsc(
            List<Long> ids,
            Long memberId,
            ProductTempImageStatus status
    );

    long countByMember_IdAndStatus(Long memberId, ProductTempImageStatus status);
}