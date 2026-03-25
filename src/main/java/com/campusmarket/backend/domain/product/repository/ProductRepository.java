package com.campusmarket.backend.domain.product.repository;

import com.campusmarket.backend.domain.product.entity.Product;
import com.campusmarket.backend.domain.product.entity.ProductSaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndSaleStatusNot(Long productId, ProductSaleStatus saleStatus);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    update Product p
    set p.viewCount = p.viewCount + 1
    where p.id = :productId
      and p.saleStatus <> :saleStatus
""")
    int increaseViewCount(@Param("productId") Long productId,
                          @Param("saleStatus") ProductSaleStatus saleStatus);
}