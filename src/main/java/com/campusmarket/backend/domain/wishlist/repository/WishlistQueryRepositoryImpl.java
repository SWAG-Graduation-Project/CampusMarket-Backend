package com.campusmarket.backend.domain.wishlist.repository;

import com.campusmarket.backend.domain.product.entity.Product;
import com.campusmarket.backend.domain.wishlist.dto.response.WishlistProductResDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class WishlistQueryRepositoryImpl implements WishlistQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<WishlistProductResDto> findWishlistProducts(
            Long memberId,
            int offset,
            int limit
    ) {
        List<Product> products = entityManager.createQuery(
                        """
                        select p
                        from Wishlist w
                        join Product p on p.id = w.productId
                        where w.memberId = :memberId
                          and p.deletedAt is null
                        order by w.createdAt desc, w.id desc
                        """,
                        Product.class
                )
                .setParameter("memberId", memberId)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

        if (products.isEmpty()) {
            return List.of();
        }

        List<Long> productIds = products.stream()
                .map(Product::getId)
                .toList();

        Map<Long, String> thumbnailMap = findThumbnailMap(productIds);

        return products.stream()
                .map(product -> WishlistProductResDto.of(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getSaleStatus().name(),
                        thumbnailMap.get(product.getId()),
                        product.getWishCount(),
                        product.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public long countWishlistProducts(Long memberId) {
        return entityManager.createQuery(
                        """
                        select count(w)
                        from Wishlist w
                        join Product p on p.id = w.productId
                        where w.memberId = :memberId
                          and p.deletedAt is null
                        """,
                        Long.class
                )
                .setParameter("memberId", memberId)
                .getSingleResult();
    }

    private Map<Long, String> findThumbnailMap(List<Long> productIds) {
        List<Object[]> rows = entityManager.createQuery(
                        """
                        select pi.product.id, pi.imageUrl
                        from ProductImage pi
                        where pi.product.id in :productIds
                          and pi.displayOrder = (
                              select min(pi2.displayOrder)
                              from ProductImage pi2
                              where pi2.product.id = pi.product.id
                          )
                        """,
                        Object[].class
                )
                .setParameter("productIds", productIds)
                .getResultList();

        Map<Long, String> thumbnailMap = new HashMap<>();

        for (Object[] row : rows) {
            Long productId = (Long) row[0];
            String imageUrl = (String) row[1];
            thumbnailMap.put(productId, imageUrl);
        }

        return thumbnailMap;
    }
}