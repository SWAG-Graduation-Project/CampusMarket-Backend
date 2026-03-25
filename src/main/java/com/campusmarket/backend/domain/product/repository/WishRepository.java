package com.campusmarket.backend.domain.product.repository;

import com.campusmarket.backend.domain.product.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

    boolean existsByProductIdAndWishedMemberId(Long productId, Long wishedMemberId);
}