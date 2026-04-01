package com.campusmarket.backend.domain.product.mapper;

import com.campusmarket.backend.domain.product.dto.response.ProductCategoryResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductDetailInfo;
import com.campusmarket.backend.domain.product.dto.response.ProductDetailResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductImageResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductListItemInfo;
import com.campusmarket.backend.domain.product.dto.response.ProductListItemResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductListResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductPageInfoResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductSellerResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductViewIncreaseResDto;
import com.campusmarket.backend.domain.product.entity.Product;
import com.campusmarket.backend.domain.product.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    public ProductListItemResDto toProductListItemResDto(ProductListItemInfo info) {
        return ProductListItemResDto.of(
                info.productId(),
                info.name(),
                info.brand(),
                info.price(),
                info.isFree(),
                info.productCondition(),
                info.saleStatus(),
                info.viewCount(),
                info.wishCount(),
                info.displayAssetImageUrl(),
                info.thumbnailImageUrl(),
                info.createdAt(),
                info.sellerId(),
                info.sellerNickname()
        );
    }

    public ProductListResDto toProductListResDto(
            List<ProductListItemInfo> productInfos,
            Integer page,
            Integer size,
            Long totalElements
    ) {
        List<ProductListItemResDto> products = productInfos.stream()
                .map(this::toProductListItemResDto)
                .toList();

        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        boolean hasNext = (long) (page + 1) * size < totalElements;

        ProductPageInfoResDto pageInfo = ProductPageInfoResDto.of(
                page,
                size,
                totalElements,
                totalPages,
                hasNext
        );

        return ProductListResDto.of(products, pageInfo);
    }

    public ProductImageResDto toProductImageResDto(ProductImage productImage) {
        return ProductImageResDto.of(
                productImage.getId(),
                productImage.getImageUrl(),
                productImage.getOriginalImageUrl(),
                productImage.getBackgroundRemoved(),
                productImage.getDisplayOrder()
        );
    }

    public ProductDetailResDto toProductDetailResDto(
            ProductDetailInfo info,
            List<ProductImage> images,
            Boolean isWished,
            Boolean canChat
    ) {
        ProductCategoryResDto category = ProductCategoryResDto.of(
                info.majorCategoryId(),
                info.majorCategoryName(),
                info.subCategoryId(),
                info.subCategoryName()
        );

        ProductSellerResDto seller = ProductSellerResDto.of(
                info.sellerId(),
                info.sellerNickname(),
                info.sellerProfileImageUrl(),
                info.storeStartDate(),
                info.saleCount()
        );

        List<ProductImageResDto> imageDtos = images.stream()
                .map(this::toProductImageResDto)
                .toList();

        return ProductDetailResDto.of(
                info.productId(),
                info.name(),
                info.brand(),
                info.color(),
                info.productCondition(),
                info.description(),
                info.price(),
                info.isFree(),
                info.saleStatus(),
                info.viewCount(),
                info.wishCount(),
                info.displayAssetImageUrl(),
                info.createdAt(),
                category,
                seller,
                imageDtos,
                isWished,
                canChat
        );
    }

    public ProductViewIncreaseResDto toProductViewIncreaseResDto(Product product) {
        return ProductViewIncreaseResDto.of(
                product.getId(),
                product.getViewCount()
        );
    }
}