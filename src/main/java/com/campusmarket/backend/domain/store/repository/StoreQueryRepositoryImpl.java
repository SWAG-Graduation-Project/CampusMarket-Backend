package com.campusmarket.backend.domain.store.repository;

import com.campusmarket.backend.domain.member.entity.MemberProfile;
import com.campusmarket.backend.domain.product.entity.Product;
import com.campusmarket.backend.domain.product.entity.ProductSaleStatus;
import com.campusmarket.backend.domain.store.dto.response.MyStoreLatestProductResDto;
import com.campusmarket.backend.domain.store.dto.response.MyStoreProductSummaryResDto;
import com.campusmarket.backend.domain.store.dto.response.StoreProductSummaryResDto;
import com.campusmarket.backend.domain.store.dto.response.StoreSummaryResDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StoreQueryRepositoryImpl implements StoreQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<StoreSummaryResDto> findStoreSummaries(int offset, int limit) {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery(
                        """
                        SELECT
                            x.latestProductId,
                            x.sellerId,
                            x.sellerNickname,
                            x.latestProductDisplayAssetImageUrl,
                            x.latestProductCreatedAt
                        FROM (
                            SELECT
                                p.상품PK AS latestProductId,
                                p.판매자ID AS sellerId,
                                m.닉네임 AS sellerNickname,
                                p.대표에셋이미지URL AS latestProductDisplayAssetImageUrl,
                                p.생성일 AS latestProductCreatedAt,
                                ROW_NUMBER() OVER (
                                    PARTITION BY p.판매자ID
                                    ORDER BY p.생성일 DESC, p.상품PK DESC
                                ) AS rn
                            FROM 상품 p
                            JOIN 회원 m
                              ON p.판매자ID = m.회원PK
                            WHERE p.삭제일 IS NULL
                              AND p.판매상태 <> 'DELETED'
                        ) x
                        WHERE x.rn = 1
                        ORDER BY x.latestProductCreatedAt DESC, x.sellerId DESC
                        LIMIT :limit OFFSET :offset
                        """
                )
                .setParameter("limit", limit)
                .setParameter("offset", offset)
                .getResultList();

        return rows.stream()
                .map(row -> StoreSummaryResDto.of(
                        ((Number) row[1]).longValue(),
                        row[2] != null ? row[2].toString() : null,
                        ((Number) row[0]).longValue(),
                        row[3] != null ? row[3].toString() : null,
                        row[4] != null
                                ? ((java.sql.Timestamp) row[4]).toLocalDateTime()
                                : null
                ))
                .toList();
    }

    @Override
    public long countStores() {
        Object result = entityManager.createNativeQuery(
                        """
                        SELECT COUNT(*)
                        FROM (
                            SELECT
                                p.판매자ID,
                                ROW_NUMBER() OVER (
                                    PARTITION BY p.판매자ID
                                    ORDER BY p.생성일 DESC, p.상품PK DESC
                                ) AS rn
                            FROM 상품 p
                            WHERE p.삭제일 IS NULL
                              AND p.판매상태 <> 'DELETED'
                        ) x
                        WHERE x.rn = 1
                        """
                )
                .getSingleResult();

        return ((Number) result).longValue();
    }

    @Override
    public long countProductsBySellerId(Long sellerId) {
        return entityManager.createQuery(
                        """
                        select count(p)
                        from Product p
                        where p.sellerId = :sellerId
                          and p.deletedAt is null
                        """,
                        Long.class
                )
                .setParameter("sellerId", sellerId)
                .getSingleResult();
    }

    @Override
    public List<MyStoreLatestProductResDto> findLatestProductsBySellerId(Long sellerId, int limit) {
        List<Product> products = entityManager.createQuery(
                        """
                        select p
                        from Product p
                        where p.sellerId = :sellerId
                          and p.deletedAt is null
                        order by p.createdAt desc, p.id desc
                        """,
                        Product.class
                )
                .setParameter("sellerId", sellerId)
                .setMaxResults(limit)
                .getResultList();

        return products.stream()
                .map(product -> MyStoreLatestProductResDto.of(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        findThumbnailImageUrl(product.getId())
                ))
                .toList();
    }

    @Override
    public List<MyStoreProductSummaryResDto> findMyStoreProducts(
            Long sellerId,
            ProductSaleStatus saleStatus,
            int offset,
            int limit
    ) {
        String jpql = """
                select p
                from Product p
                where p.sellerId = :sellerId
                  and p.deletedAt is null
                """;

        if (saleStatus != null) {
            jpql += " and p.saleStatus = :saleStatus ";
        }

        jpql += " order by p.createdAt desc, p.id desc ";

        var query = entityManager.createQuery(jpql, Product.class)
                .setParameter("sellerId", sellerId)
                .setFirstResult(offset)
                .setMaxResults(limit);

        if (saleStatus != null) {
            query.setParameter("saleStatus", saleStatus);
        }

        List<Product> products = query.getResultList();

        return products.stream()
                .map(product -> MyStoreProductSummaryResDto.of(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getIsFree(),
                        product.getSaleStatus().name(),
                        findThumbnailImageUrl(product.getId()),
                        product.getViewCount(),
                        product.getWishCount(),
                        product.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public long countMyStoreProducts(Long sellerId, ProductSaleStatus saleStatus) {
        String jpql = """
                select count(p)
                from Product p
                where p.sellerId = :sellerId
                  and p.deletedAt is null
                """;

        if (saleStatus != null) {
            jpql += " and p.saleStatus = :saleStatus ";
        }

        var query = entityManager.createQuery(jpql, Long.class)
                .setParameter("sellerId", sellerId);

        if (saleStatus != null) {
            query.setParameter("saleStatus", saleStatus);
        }

        return query.getSingleResult();
    }

    @Override
    public List<StoreProductSummaryResDto> findStoreProductsBySellerId(
            Long sellerId,
            int offset,
            int limit
    ) {
        List<Product> products = entityManager.createQuery(
                        """
                        select p
                        from Product p
                        where p.sellerId = :sellerId
                          and p.deletedAt is null
                        order by p.createdAt desc, p.id desc
                        """,
                        Product.class
                )
                .setParameter("sellerId", sellerId)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

        return products.stream()
                .map(product -> StoreProductSummaryResDto.of(
                        product.getId(),
                        product.getSellerId(),
                        product.getName(),
                        product.getPrice(),
                        product.getIsFree(),
                        product.getSaleStatus().name(),
                        product.getWishCount(),
                        findThumbnailImageUrl(product.getId()),
                        product.getDisplayAssetImageUrl(),
                        product.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public long countStoreProductsBySellerId(Long sellerId) {
        return entityManager.createQuery(
                        """
                        select count(p)
                        from Product p
                        where p.sellerId = :sellerId
                          and p.deletedAt is null
                        """,
                        Long.class
                )
                .setParameter("sellerId", sellerId)
                .getSingleResult();
    }

    private String findThumbnailImageUrl(Long productId) {
        List<String> imageUrls = entityManager.createQuery(
                        """
                        select pi.originalImageUrl
                        from ProductImage pi
                        where pi.product.id = :productId
                        order by pi.displayOrder asc
                        """,
                        String.class
                )
                .setParameter("productId", productId)
                .setMaxResults(1)
                .getResultList();

        if (imageUrls.isEmpty()) {
            return null;
        }

        return imageUrls.get(0);
    }
}