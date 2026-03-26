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

import java.util.List;

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

        return profiles.stream()
                .map(profile -> StoreSummaryResDto.of(
                        profile.getMember().getId(),
                        profile.getNickname(),
                        profile.getProfileImageUrl(),
                        findLatestProductImageUrlBySellerId(profile.getMember().getId()),
                        profile.getSaleCount(),
                        profile.getPurchaseCount()
                ))
                .toList();
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
                        order by p.createdAt desc
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

        jpql += " order by p.createdAt desc ";

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

    private String findLatestProductImageUrlBySellerId(Long sellerId) {
        List<String> imageUrls = entityManager.createQuery(
                        """
                        select pi.imageUrl
                        from ProductImage pi
                        join pi.product p
                        where p.sellerId = :sellerId
                          and p.deletedAt is null
                        order by p.createdAt desc, pi.displayOrder asc
                        """,
                        String.class
                )
                .setParameter("sellerId", sellerId)
                .setMaxResults(1)
                .getResultList();

        if (imageUrls.isEmpty()) {
            return null;
        }

        return imageUrls.get(0);
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