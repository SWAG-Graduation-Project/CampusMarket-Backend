package com.campusmarket.backend.domain.product.repository;

import com.campusmarket.backend.domain.product.entity.Product;
import com.campusmarket.backend.domain.product.entity.ProductSaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndSaleStatusNot(Long productId, ProductSaleStatus saleStatus);

    Optional<Product> findByIdAndDeletedAtIsNullAndSaleStatusNot(Long productId, ProductSaleStatus saleStatus);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Product p
           set p.viewCount = p.viewCount + 1
         where p.id = :productId
           and p.saleStatus <> :saleStatus
    """)
    int increaseViewCount(
            @Param("productId") Long productId,
            @Param("saleStatus") ProductSaleStatus saleStatus
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Product p
           set p.saleStatus = :soldStatus,
               p.soldAt = :now,
               p.updatedAt = :now
         where p.id = :productId
           and p.sellerId = :sellerId
           and p.deletedAt is null
           and p.saleStatus = :onSaleStatus
    """)
    int markAsSoldIfOnSale(
            @Param("productId") Long productId,
            @Param("sellerId") Long sellerId,
            @Param("soldStatus") ProductSaleStatus soldStatus,
            @Param("onSaleStatus") ProductSaleStatus onSaleStatus,
            @Param("now") LocalDateTime now
    );

    Optional<Product> findByIdAndDeletedAtIsNull(Long productId);
}