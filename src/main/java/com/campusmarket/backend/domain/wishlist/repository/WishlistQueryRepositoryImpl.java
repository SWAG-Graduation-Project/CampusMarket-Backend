package com.campusmarket.backend.domain.wishlist.repository;

import com.campusmarket.backend.domain.product.entity.Product;
import com.campusmarket.backend.domain.wishlist.dto.response.WishlistProductResDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

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

        return products.stream()
                .map(product -> WishlistProductResDto.of(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getSaleStatus().name(),
                        findThumbnailImageUrl(product.getId()),
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