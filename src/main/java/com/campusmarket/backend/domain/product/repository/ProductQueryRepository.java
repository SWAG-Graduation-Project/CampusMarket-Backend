package com.campusmarket.backend.domain.product.repository;

import com.campusmarket.backend.domain.product.dto.request.SearchProductsReqDto;
import com.campusmarket.backend.domain.product.dto.response.ProductDetailInfo;
import com.campusmarket.backend.domain.product.dto.response.ProductListItemInfo;
import com.campusmarket.backend.domain.product.entity.ProductCondition;
import com.campusmarket.backend.domain.product.entity.ProductSaleStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<ProductListItemInfo> searchProducts(
            SearchProductsReqDto reqDto,
            int page,
            int size,
            String sort
    ) {
        StringBuilder sql = new StringBuilder();
        sql.append("""
                SELECT
                    p.상품PK,
                    p.상품명,
                    p.브랜드,
                    p.가격,
                    p.무료나눔여부,
                    p.상품상태,
                    p.판매상태,
                    p.조회수,
                    p.찜수,
                    (
                        SELECT pi.이미지URL
                        FROM 상품이미지 pi
                        WHERE pi.상품ID = p.상품PK
                        ORDER BY pi.표시순서 ASC
                        LIMIT 1
                    ) AS thumbnailImageUrl,
                    p.생성일
                FROM 상품 p
                WHERE p.판매상태 <> 'DELETED'
                """);

        if (reqDto.keyword() != null && !reqDto.keyword().isBlank()) {
            sql.append("""
                    AND (
                        p.상품명 LIKE CONCAT('%', :keyword, '%')
                        OR p.브랜드 LIKE CONCAT('%', :keyword, '%')
                    )
                    """);
        }

        if (reqDto.majorCategoryId() != null) {
            sql.append(" AND p.대카테고리ID = :majorCategoryId ");
        }

        if (reqDto.subCategoryId() != null) {
            sql.append(" AND p.소카테고리ID = :subCategoryId ");
        }

        sql.append(buildOrderBy(sort));
        sql.append(" LIMIT :limit OFFSET :offset ");

        Query query = entityManager.createNativeQuery(sql.toString());
        bindSearchParams(query, reqDto);
        query.setParameter("limit", size);
        query.setParameter("offset", page * size);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        List<ProductListItemInfo> result = new ArrayList<>();

        for (Object[] row : rows) {
            result.add(new ProductListItemInfo(
                    toLong(row[0]),
                    toStringValue(row[1]),
                    toStringValue(row[2]),
                    toInteger(row[3]),
                    toBoolean(row[4]),
                    toProductCondition(row[5]),
                    toProductSaleStatus(row[6]),
                    toInteger(row[7]),
                    toInteger(row[8]),
                    toStringValue(row[9]),
                    toLocalDateTime(row[10])
            ));
        }

        return result;
    }

    public Long countProducts(SearchProductsReqDto reqDto) {
        StringBuilder sql = new StringBuilder();
        sql.append("""
                SELECT COUNT(*)
                FROM 상품 p
                WHERE p.판매상태 <> 'DELETED'
                """);

        if (reqDto.keyword() != null && !reqDto.keyword().isBlank()) {
            sql.append("""
                    AND (
                        p.상품명 LIKE CONCAT('%', :keyword, '%')
                        OR p.브랜드 LIKE CONCAT('%', :keyword, '%')
                    )
                    """);
        }

        if (reqDto.majorCategoryId() != null) {
            sql.append(" AND p.대카테고리ID = :majorCategoryId ");
        }

        if (reqDto.subCategoryId() != null) {
            sql.append(" AND p.소카테고리ID = :subCategoryId ");
        }

        Query query = entityManager.createNativeQuery(sql.toString());
        bindSearchParams(query, reqDto);

        Object result = query.getSingleResult();
        return ((Number) result).longValue();
    }

    public ProductDetailInfo findProductDetailInfo(Long productId) {
        String sql = """
                SELECT
                    p.상품PK,
                    p.판매자ID,
                    p.대카테고리ID,
                    mc.카테고리명,
                    p.소카테고리ID,
                    sc.카테고리명,
                    p.상품명,
                    p.브랜드,
                    p.색상,
                    p.상품상태,
                    p.상품설명,
                    p.가격,
                    p.무료나눔여부,
                    p.판매상태,
                    p.조회수,
                    p.찜수,
                    p.생성일,
                    mp.닉네임,
                    mp.프로필이미지URL,
                    mp.상점시작일,
                    mp.판매거래횟수
                FROM 상품 p
                INNER JOIN 대카테고리 mc ON mc.대카테고리PK = p.대카테고리ID
                INNER JOIN 소카테고리 sc ON sc.소카테고리PK = p.소카테고리ID
                LEFT JOIN 멤버프로필 mp ON mp.회원ID = p.판매자ID
                WHERE p.상품PK = :productId
                  AND p.판매상태 <> 'DELETED'
                """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("productId", productId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        if (rows.isEmpty()) {
            return null;
        }

        Object[] row = rows.get(0);

        return new ProductDetailInfo(
                toLong(row[0]),
                toLong(row[1]),
                toLong(row[2]),
                toStringValue(row[3]),
                toLong(row[4]),
                toStringValue(row[5]),
                toStringValue(row[6]),
                toStringValue(row[7]),
                toStringValue(row[8]),
                toProductCondition(row[9]),
                toStringValue(row[10]),
                toInteger(row[11]),
                toBoolean(row[12]),
                toProductSaleStatus(row[13]),
                toInteger(row[14]),
                toInteger(row[15]),
                toLocalDateTime(row[16]),
                toStringValue(row[17]),
                toStringValue(row[18]),
                toLocalDateTime(row[19]),
                toInteger(row[20])
        );
    }

    private void bindSearchParams(Query query, SearchProductsReqDto reqDto) {
        if (reqDto.keyword() != null && !reqDto.keyword().isBlank()) {
            query.setParameter("keyword", reqDto.keyword().trim());
        }

        if (reqDto.majorCategoryId() != null) {
            query.setParameter("majorCategoryId", reqDto.majorCategoryId());
        }

        if (reqDto.subCategoryId() != null) {
            query.setParameter("subCategoryId", reqDto.subCategoryId());
        }
    }

    private String buildOrderBy(String sort) {
        return switch (sort) {
            case "priceAsc" -> " ORDER BY p.가격 ASC, p.생성일 DESC ";
            case "priceDesc" -> " ORDER BY p.가격 DESC, p.생성일 DESC ";
            case "views" -> " ORDER BY p.조회수 DESC, p.생성일 DESC ";
            default -> " ORDER BY p.생성일 DESC ";
        };
    }

    private Long toLong(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    private Integer toInteger(Object value) {
        return value == null ? null : ((Number) value).intValue();
    }

    private Boolean toBoolean(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }

        if (value instanceof Number numberValue) {
            return numberValue.intValue() == 1;
        }

        return Boolean.parseBoolean(value.toString());
    }

    private String toStringValue(Object value) {
        return value == null ? null : value.toString();
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        }

        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }

        return null;
    }

    private ProductCondition toProductCondition(Object value) {
        if (value == null) {
            return null;
        }

        return ProductCondition.valueOf(value.toString());
    }

    private ProductSaleStatus toProductSaleStatus(Object value) {
        if (value == null) {
            return null;
        }

        return ProductSaleStatus.valueOf(value.toString());
    }
}