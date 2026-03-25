package com.campusmarket.backend.domain.product.repository;

import com.campusmarket.backend.domain.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findAllByProduct_IdOrderByDisplayOrderAsc(Long productId);
}