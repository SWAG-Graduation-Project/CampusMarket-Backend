package com.campusmarket.backend.domain.store.repository;

import com.campusmarket.backend.domain.member.entity.MemberProfile;
import com.campusmarket.backend.domain.product.entity.Product;
import com.campusmarket.backend.domain.product.entity.ProductSaleStatus;
import com.campusmarket.backend.domain.store.dto.response.MyStoreLatestProductResDto;
import com.campusmarket.backend.domain.store.dto.response.MyStoreProductSummaryResDto;
import com.campusmarket.backend.domain.store.dto.response.StoreSummaryResDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StoreQueryRepositoryImpl implements StoreQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<StoreSummaryResDto> findStoreSummaries(int offset, int limit) {
        List<MemberProfile> profiles = entityManager.createQuery(
                        """
                        select mp
                        from MemberProfile mp
                        order by mp.storeStartedAt asc, mp.memberId asc
                        """,
                        MemberProfile.class
                )
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

        if (profiles.isEmpty()) {
            return List.of();
        }

        List<Long> sellerIds = profiles.stream()
                .map(profile -> profile.getMember().getId())
                .toList();

        Map<Long, String> representativeImageMap =
                findRepresentativeImageMapBySellerIds(sellerIds);

        List<StoreSummaryResDto> result = new ArrayList<>();

        for (MemberProfile profile : profiles) {
            Long sellerId = profile.getMember().getId();

            result.add(StoreSummaryResDto.of(
                    sellerId,
                    profile.getNickname(),
                    profile.getProfileImageUrl(),
                    representativeImageMap.get(sellerId),
                    profile.getSaleCount(),
                    profile.getPurchaseCount()
            ));
        }

        return result;
    }

    @Override
    public long countStores() {
        return entityManager.createQuery(
                        "select count(mp) from MemberProfile mp",
                        Long.class
                )
                .getSingleResult();
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

    private Map<Long, String> findRepresentativeImageMapBySellerIds(List<Long> sellerIds) {
        List<Object[]> rows = entityManager.createQuery(
                        """
                        select p.sellerId, pi.imageUrl, p.createdAt, p.id, pi.displayOrder
                        from ProductImage pi
                        join pi.product p
                        where p.sellerId in :sellerIds
                          and p.deletedAt is null
                        order by p.sellerId asc, p.createdAt desc, p.id desc, pi.displayOrder asc
                        """,
                        Object[].class
                )
                .setParameter("sellerIds", sellerIds)
                .getResultList();

        Map<Long, String> imageMap = new HashMap<>();

        for (Object[] row : rows) {
            Long sellerId = (Long) row[0];
            String imageUrl = (String) row[1];

            imageMap.putIfAbsent(sellerId, imageUrl);
        }

        return imageMap;
    }

    private String findThumbnailImageUrl(Long productId) {
        List<String> imageUrls = entityManager.createQuery(
                        """
                        select pi.imageUrl
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