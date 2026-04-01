package com.campusmarket.backend.domain.product.service;

import com.campusmarket.backend.domain.category.entity.MajorCategory;
import com.campusmarket.backend.domain.category.entity.SubCategory;
import com.campusmarket.backend.domain.category.repository.MajorCategoryRepository;
import com.campusmarket.backend.domain.category.repository.SubCategoryRepository;
import com.campusmarket.backend.domain.chat.service.ChatSystemMessageService;
import com.campusmarket.backend.domain.member.constant.MemberErrorCode;
import com.campusmarket.backend.domain.member.entity.Member;
import com.campusmarket.backend.domain.member.exception.MemberException;
import com.campusmarket.backend.domain.member.repository.MemberRepository;
import com.campusmarket.backend.domain.product.dto.request.ProductCreateReqDto;
import com.campusmarket.backend.domain.product.dto.request.ProductImageItemReqDto;
import com.campusmarket.backend.domain.product.dto.request.ProductUpdateReqDto;
import com.campusmarket.backend.domain.product.dto.request.SearchProductsReqDto;
import com.campusmarket.backend.domain.product.dto.response.ProductCreateResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductDetailInfo;
import com.campusmarket.backend.domain.product.dto.response.ProductDetailResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductImageResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductListItemInfo;
import com.campusmarket.backend.domain.product.dto.response.ProductListResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductTempImageResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductViewIncreaseResDto;
import com.campusmarket.backend.domain.product.entity.Product;
import com.campusmarket.backend.domain.product.entity.ProductImage;
import com.campusmarket.backend.domain.product.entity.ProductSaleStatus;
import com.campusmarket.backend.domain.product.entity.ProductTempImage;
import com.campusmarket.backend.domain.product.entity.ProductTempImageStatus;
import com.campusmarket.backend.domain.product.exception.ProductErrorCode;
import com.campusmarket.backend.domain.product.exception.ProductException;
import com.campusmarket.backend.domain.product.mapper.ProductMapper;
import com.campusmarket.backend.domain.product.repository.ProductImageRepository;
import com.campusmarket.backend.domain.product.repository.ProductQueryRepository;
import com.campusmarket.backend.domain.product.repository.ProductRepository;
import com.campusmarket.backend.domain.product.repository.ProductTempImageRepository;
import com.campusmarket.backend.domain.product.repository.WishRepository;
import com.campusmarket.backend.global.file.FileStorageService;
import com.campusmarket.backend.global.file.FileUploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final MemberRepository memberRepository;
    private final MajorCategoryRepository majorCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ProductTempImageRepository productTempImageRepository;
    private final ProductDisplayAssetService productDisplayAssetService;
    private final FileStorageService fileStorageService;
    private final ChatSystemMessageService chatSystemMessageService;

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

    @Transactional
    public ProductCreateResDto createProduct(String guestUuid, ProductCreateReqDto reqDto) {
        Member seller = memberRepository.findByGuestUuid(guestUuid)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        MajorCategory majorCategory = majorCategoryRepository.findById(reqDto.majorCategoryId())
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_MAJOR_CATEGORY_NOT_FOUND));

        SubCategory subCategory = subCategoryRepository.findById(reqDto.subCategoryId())
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_SUB_CATEGORY_NOT_FOUND));

        String displayAssetImageUrl = productDisplayAssetService.resolveDisplayAssetImage(
                reqDto.majorCategoryId(),
                reqDto.subCategoryId(),
                reqDto.color(),
                reqDto.name()
        );

        validateCreateProductRequest(reqDto, majorCategory, subCategory);

        Product product = Product.builder()
                .sellerId(seller.getId())
                .majorCategoryId(reqDto.majorCategoryId())
                .subCategoryId(reqDto.subCategoryId())
                .name(reqDto.name())
                .brand(reqDto.brand())
                .color(reqDto.color())
                .productCondition(reqDto.productCondition())
                .description(reqDto.description())
                .price(reqDto.price())
                .isFree(reqDto.isFree())
                .saleStatus(ProductSaleStatus.ON_SALE)
                .viewCount(0)
                .wishCount(0)
                .displayAssetImageUrl(displayAssetImageUrl)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .soldAt(null)
                .deletedAt(null)
                .build();

        Product savedProduct = productRepository.save(product);

        saveProductImages(savedProduct, reqDto.images());

        return ProductCreateResDto.of(savedProduct.getId());
    }

    private void validateCreateProductRequest(
            ProductCreateReqDto reqDto,
            MajorCategory majorCategory,
            SubCategory subCategory
    ) {
        if (Boolean.TRUE.equals(reqDto.isFree()) && reqDto.price() != 0) {
            throw new ProductException(ProductErrorCode.PRODUCT_INVALID_PRICE);
        }

        if (reqDto.images() == null || reqDto.images().isEmpty()) {
            throw new ProductException(ProductErrorCode.PRODUCT_IMAGES_REQUIRED);
        }

        if (!subCategory.getMajorCategory().getId().equals(majorCategory.getId())) {
            throw new ProductException(ProductErrorCode.PRODUCT_INVALID_CATEGORY_RELATION);
        }
    }

    private void saveProductImages(Product product, List<ProductImageItemReqDto> images) {
        List<ProductImage> productImages = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            ProductImageItemReqDto imageItem = images.get(i);

            ProductImage productImage = ProductImage.builder()
                    .product(product)
                    .imageUrl(imageItem.imageUrl())
                    .originalImageUrl(imageItem.originalImageUrl())
                    .backgroundRemoved(!imageItem.imageUrl().equals(imageItem.originalImageUrl()))
                    .displayOrder(i + 1)
                    .createdAt(LocalDateTime.now())
                    .build();

            productImages.add(productImage);
        }

        productImageRepository.saveAll(productImages);
    }

    @Transactional
    public void updateProduct(Long productId, String guestUuid, ProductUpdateReqDto reqDto) {
        Product product = getOwnedActiveProduct(productId, guestUuid);

        if (Boolean.TRUE.equals(reqDto.isFree()) && reqDto.price() != 0) {
            throw new ProductException(ProductErrorCode.PRODUCT_INVALID_PRICE);
        }

        product.update(
                reqDto.name(),
                reqDto.brand(),
                reqDto.color(),
                reqDto.productCondition(),
                reqDto.description(),
                reqDto.price(),
                reqDto.isFree()
        );
    }

    @Transactional
    public void deleteProduct(Long productId, String guestUuid) {
        Product product = getOwnedActiveProduct(productId, guestUuid);
        product.delete();
    }

    @Transactional
    public void completeSale(Long productId, String guestUuid) {
        Member seller = getSellerByGuestUuid(guestUuid);
        Product product = getActiveProduct(productId);
        validateOwner(product, seller);

        LocalDateTime now = LocalDateTime.now();

        int updatedRows = productRepository.markAsSoldIfOnSale(
                productId,
                seller.getId(),
                ProductSaleStatus.SOLD,
                ProductSaleStatus.ON_SALE,
                now
        );

        if (updatedRows == 0) {
            throw new ProductException(ProductErrorCode.PRODUCT_ALREADY_SOLD);
        }

        chatSystemMessageService.sendProductSoldMessage(productId);
    }

    @Transactional(readOnly = true)
    public ProductDetailResDto getMyProductDetail(Long productId, String guestUuid) {
        Product product = getOwnedActiveProduct(productId, guestUuid);
        List<ProductImage> images = productImageRepository.findAllByProduct_IdOrderByDisplayOrderAsc(productId);

        return ProductDetailResDto.of(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getColor(),
                product.getProductCondition(),
                product.getDescription(),
                product.getPrice(),
                product.getIsFree(),
                product.getSaleStatus(),
                product.getViewCount(),
                product.getWishCount(),
                product.getDisplayAssetImageUrl(),
                product.getCreatedAt(),
                null,
                null,
                images.stream()
                        .map(img -> ProductImageResDto.of(
                                img.getId(),
                                img.getImageUrl(),
                                img.getOriginalImageUrl(),
                                img.getBackgroundRemoved(),
                                img.getDisplayOrder()
                        ))
                        .toList(),
                false,
                false
        );
    }

    private void validateOwner(Product product, Member seller) {
        if (!product.getSellerId().equals(seller.getId())) {
            throw new ProductException(ProductErrorCode.PRODUCT_FORBIDDEN);
        }
    }

    private Member getSellerByGuestUuid(String guestUuid) {
        return memberRepository.findByGuestUuid(guestUuid)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private Product getActiveProduct(Long productId) {
        return productRepository.findByIdAndDeletedAtIsNullAndSaleStatusNot(productId, ProductSaleStatus.DELETED)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    private Product getOwnedActiveProduct(Long productId, String guestUuid) {
        Member seller = getSellerByGuestUuid(guestUuid);
        Product product = getActiveProduct(productId);
        validateOwner(product, seller);
        return product;
    }

    @Transactional
    public ProductTempImageResDto uploadTempImage(
            Long memberId,
            MultipartFile file
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        FileUploadResult uploadResult = fileStorageService.upload(
                file,
                "products/temp/" + memberId
        );

        int nextDisplayOrder = (int) productTempImageRepository.countByMember_IdAndStatus(
                memberId,
                ProductTempImageStatus.ACTIVE
        ) + 1;

        ProductTempImage tempImage = ProductTempImage.builder()
                .member(member)
                .originalImageUrl(uploadResult.fileUrl())
                .backgroundRemovedImageUrl(null)
                .backgroundRemoved(false)
                .displayOrder(nextDisplayOrder)
                .status(ProductTempImageStatus.ACTIVE)
                .build();

        ProductTempImage saved = productTempImageRepository.save(tempImage);

        return ProductTempImageResDto.of(
                saved.getId(),
                saved.getOriginalImageUrl(),
                saved.getBackgroundRemovedImageUrl(),
                saved.getBackgroundRemoved(),
                saved.getDisplayOrder()
        );
    }

    @Transactional
    public ProductTempImageResDto replaceTempImage(
            Long memberId,
            Long tempImageId,
            MultipartFile file
    ) {
        ProductTempImage tempImage = productTempImageRepository.findByIdAndMember_IdAndStatus(
                        tempImageId,
                        memberId,
                        ProductTempImageStatus.ACTIVE
                )
                .orElseThrow(() -> new IllegalArgumentException("유효한 임시 이미지가 없습니다."));

        fileStorageService.deleteByUrl(tempImage.getOriginalImageUrl());
        fileStorageService.deleteByUrl(tempImage.getBackgroundRemovedImageUrl());

        FileUploadResult uploadResult = fileStorageService.upload(
                file,
                "products/temp/" + memberId
        );

        tempImage.replaceOriginalImage(uploadResult.fileUrl());

        return ProductTempImageResDto.of(
                tempImage.getId(),
                tempImage.getOriginalImageUrl(),
                tempImage.getBackgroundRemovedImageUrl(),
                tempImage.getBackgroundRemoved(),
                tempImage.getDisplayOrder()
        );
    }

    @Transactional
    public void deleteTempImage(
            Long memberId,
            Long tempImageId
    ) {
        ProductTempImage tempImage = productTempImageRepository.findByIdAndMember_IdAndStatus(
                        tempImageId,
                        memberId,
                        ProductTempImageStatus.ACTIVE
                )
                .orElseThrow(() -> new IllegalArgumentException("유효한 임시 이미지가 없습니다."));

        fileStorageService.deleteByUrl(tempImage.getOriginalImageUrl());
        fileStorageService.deleteByUrl(tempImage.getBackgroundRemovedImageUrl());

        tempImage.softDelete();
    }
}