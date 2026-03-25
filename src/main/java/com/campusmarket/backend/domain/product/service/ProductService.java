package com.campusmarket.backend.domain.product.service;

import com.campusmarket.backend.domain.product.dto.request.SearchProductsReqDto;
import com.campusmarket.backend.domain.product.dto.response.ProductDetailInfo;
import com.campusmarket.backend.domain.product.dto.response.ProductDetailResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductListItemInfo;
import com.campusmarket.backend.domain.product.dto.response.ProductListResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductViewIncreaseResDto;
import com.campusmarket.backend.domain.product.entity.Product;
import com.campusmarket.backend.domain.product.entity.ProductImage;
import com.campusmarket.backend.domain.product.entity.ProductSaleStatus;
import com.campusmarket.backend.domain.product.exception.ProductErrorCode;
import com.campusmarket.backend.domain.product.exception.ProductException;
import com.campusmarket.backend.domain.product.mapper.ProductMapper;
import com.campusmarket.backend.domain.product.repository.ProductImageRepository;
import com.campusmarket.backend.domain.product.repository.ProductQueryRepository;
import com.campusmarket.backend.domain.product.repository.ProductRepository;
import com.campusmarket.backend.domain.product.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 50;

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final WishRepository wishRepository;
    private final ProductQueryRepository productQueryRepository;
    private final ProductMapper productMapper;

    public ProductListResDto searchProducts(SearchProductsReqDto reqDto) {
        int page = normalizePage(reqDto.page());
        int size = normalizeSize(reqDto.size());
        String sort = normalizeSort(reqDto.sort());

        List<ProductListItemInfo> products = productQueryRepository.searchProducts(reqDto, page, size, sort);
        Long totalCount = productQueryRepository.countProducts(reqDto);

        return productMapper.toProductListResDto(products, page, size, totalCount);
    }

    public ProductDetailResDto getProductDetail(Long productId, Long memberId) {
        validateProductId(productId);

        ProductDetailInfo productDetailInfo = productQueryRepository.findProductDetailInfo(productId);

        if (productDetailInfo == null) {
            throw new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        List<ProductImage> images = productImageRepository.findAllByProduct_IdOrderByDisplayOrderAsc(productId);

        boolean isWished = memberId != null
                && wishRepository.existsByProductIdAndWishedMemberId(productId, memberId);

        boolean canChat = memberId != null
                && !memberId.equals(productDetailInfo.sellerId())
                && productDetailInfo.saleStatus() == ProductSaleStatus.ON_SALE;

        return productMapper.toProductDetailResDto(productDetailInfo, images, isWished, canChat);
    }

    @Transactional
    public ProductViewIncreaseResDto increaseViewCount(Long productId) {
        int updatedCount = productRepository.increaseViewCount(productId, ProductSaleStatus.DELETED);

        if (updatedCount == 0) {
            throw new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        Product product = productRepository.findByIdAndSaleStatusNot(productId, ProductSaleStatus.DELETED)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        return ProductViewIncreaseResDto.of(product.getId(), product.getViewCount());
    }

    private void validateProductId(Long productId) {
        if (productId == null || productId <= 0) {
            throw new ProductException(ProductErrorCode.INVALID_PRODUCT_ID);
        }
    }

    private int normalizePage(Integer page) {
        if (page == null) {
            return DEFAULT_PAGE;
        }

        if (page < 0) {
            throw new ProductException(ProductErrorCode.INVALID_PRODUCT_PAGE);
        }

        return page;
    }

    private int normalizeSize(Integer size) {
        if (size == null) {
            return DEFAULT_SIZE;
        }

        if (size <= 0 || size > MAX_SIZE) {
            throw new ProductException(ProductErrorCode.INVALID_PRODUCT_SIZE);
        }

        return size;
    }

    private String normalizeSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return "latest";
        }

        if (!sort.equals("latest")
                && !sort.equals("priceAsc")
                && !sort.equals("priceDesc")
                && !sort.equals("views")) {
            throw new ProductException(ProductErrorCode.INVALID_PRODUCT_SORT);
        }

        return sort;
    }
}