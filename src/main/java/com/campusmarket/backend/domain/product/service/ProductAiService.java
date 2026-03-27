package com.campusmarket.backend.domain.product.service;

import com.campusmarket.backend.domain.category.entity.MajorCategory;
import com.campusmarket.backend.domain.category.entity.SubCategory;
import com.campusmarket.backend.domain.category.repository.MajorCategoryRepository;
import com.campusmarket.backend.domain.category.repository.SubCategoryRepository;
import com.campusmarket.backend.domain.product.client.ProductAiClient;
import com.campusmarket.backend.domain.product.dto.request.ProductRedraftReqDto;
import com.campusmarket.backend.domain.product.dto.response.BackgroundRemovalItemResDto;
import com.campusmarket.backend.domain.product.dto.response.BackgroundRemovalResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductDraftResDto;
import com.campusmarket.backend.domain.product.entity.ProductTempImage;
import com.campusmarket.backend.domain.product.entity.ProductTempImageStatus;
import com.campusmarket.backend.domain.product.repository.ProductTempImageRepository;
import com.campusmarket.backend.global.file.FileStorageService;
import com.campusmarket.backend.global.file.FileUploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductAiService {

    private final ProductTempImageRepository productTempImageRepository;
    private final MajorCategoryRepository majorCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ProductAiClient productAiClient;
    private final FileStorageService fileStorageService;

    @Transactional
    public ProductDraftResDto generateDraft(
            Long memberId,
            List<Long> tempImageIds
    ) {
        List<ProductTempImage> tempImages = getActiveTempImages(memberId, tempImageIds);

        List<ProductAiClient.ImagePayload> imagePayloads = tempImages.stream()
                .limit(1)
                .map(this::toImagePayload)
                .toList();

        ProductAiClient.ProductAiAnalyzeResponse aiResponse =
                productAiClient.analyzeProduct(imagePayloads);

        String normalizedMajor = normalizeMajorCategory(aiResponse.major());
        String normalizedSubCategory = normalizeSubCategory(
                aiResponse.sub_category(),
                normalizedMajor
        );

        MajorCategory majorCategory = majorCategoryRepository.findByName(normalizedMajor)
                .orElseThrow(() -> new IllegalArgumentException(
                        "AI가 반환한 대카테고리를 찾을 수 없습니다. major=" + aiResponse.major()
                ));

        SubCategory subCategory = subCategoryRepository.findByNameAndMajorCategory_Id(
                        normalizedSubCategory,
                        majorCategory.getId()
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "AI가 반환한 소카테고리를 찾을 수 없습니다. subCategory=" + aiResponse.sub_category()
                ));

        return ProductDraftResDto.of(
                majorCategory.getId(),
                majorCategory.getName(),
                subCategory.getId(),
                subCategory.getName(),
                aiResponse.product_name(),
                aiResponse.color(),
                mapCondition(aiResponse.condition()),
                aiResponse.description()
        );
    }

    @Transactional
    public ProductDraftResDto redraft(
            Long memberId,
            ProductRedraftReqDto reqDto
    ) {
        ProductDraftResDto aiDraft = generateDraft(memberId, reqDto.tempImageIds());

        String mergedProductName = shouldKeep(reqDto.keepProductName(), reqDto.productName())
                ? reqDto.productName()
                : aiDraft.productName();

        String mergedColor = shouldKeep(reqDto.keepColor(), reqDto.color())
                ? reqDto.color()
                : aiDraft.color();

        String mergedProductCondition = shouldKeep(reqDto.keepProductCondition(), reqDto.productCondition())
                ? reqDto.productCondition()
                : aiDraft.productCondition();

        String mergedDescription = shouldKeep(reqDto.keepDescription(), reqDto.description())
                ? reqDto.description()
                : aiDraft.description();

        return ProductDraftResDto.of(
                aiDraft.majorCategoryId(),
                aiDraft.majorCategoryName(),
                aiDraft.subCategoryId(),
                aiDraft.subCategoryName(),
                mergedProductName,
                mergedColor,
                mergedProductCondition,
                mergedDescription
        );
    }

    @Transactional
    public BackgroundRemovalResDto removeBackground(
            Long memberId,
            List<Long> tempImageIds
    ) {
        List<ProductTempImage> tempImages = getActiveTempImages(memberId, tempImageIds);

        List<ProductAiClient.ImagePayload> imagePayloads = tempImages.stream()
                .limit(1)
                .map(this::toImagePayload)
                .toList();

        try {
            ProductAiClient.ProductAiBackgroundRemovalResponse aiResponse =
                    productAiClient.removeBackground(imagePayloads);

            if (aiResponse.images() == null || aiResponse.images().size() != imagePayloads.size()) {
                throw new IllegalStateException("배경 제거 결과 수가 요청 이미지 수와 일치하지 않습니다.");
            }

            List<BackgroundRemovalItemResDto> items = new ArrayList<>();

            for (int index = 0; index < imagePayloads.size(); index++) {
                ProductTempImage tempImage = tempImages.get(index);
                String base64Image = aiResponse.images().get(index);

                String savedUrl = saveBase64Webp(base64Image, memberId);
                tempImage.updateBackgroundRemovedImage(savedUrl);

                items.add(
                        BackgroundRemovalItemResDto.of(tempImage.getId(), savedUrl)
                );
            }

            return BackgroundRemovalResDto.of(items);
        } catch (Exception exception) {
            List<BackgroundRemovalItemResDto> fallbackItems = new ArrayList<>();

            for (ProductTempImage tempImage : tempImages) {
                fallbackItems.add(
                        BackgroundRemovalItemResDto.of(
                                tempImage.getId(),
                                tempImage.getOriginalImageUrl()
                        )
                );
            }

            return BackgroundRemovalResDto.of(fallbackItems);
        }
    }

    private boolean shouldKeep(Boolean keepFlag, String value) {
        return Boolean.TRUE.equals(keepFlag) && StringUtils.hasText(value);
    }

    private List<ProductTempImage> getActiveTempImages(Long memberId, List<Long> tempImageIds) {
        List<ProductTempImage> tempImages =
                productTempImageRepository.findAllByIdInAndMember_IdAndStatusOrderByDisplayOrderAsc(
                        tempImageIds,
                        memberId,
                        ProductTempImageStatus.ACTIVE
                );

        if (tempImages.size() != tempImageIds.size()) {
            throw new IllegalArgumentException("유효하지 않은 tempImageId가 포함되어 있습니다.");
        }

        return tempImages;
    }

    private ProductAiClient.ImagePayload toImagePayload(ProductTempImage tempImage) {
        try {
            String imageUrl = resolveImageUrlForDraft(tempImage);
            byte[] bytes = fileStorageService.download(imageUrl);

            String fileName = extractFileName(imageUrl);
            String contentType = "image/jpeg";

            if (fileName.endsWith(".png")) {
                contentType = "image/png";
            } else if (fileName.endsWith(".webp")) {
                contentType = "image/webp";
            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            }

            return new ProductAiClient.ImagePayload(bytes, fileName, contentType);
        } catch (Exception exception) {
            throw new RuntimeException("임시 이미지 파일을 읽는 중 오류가 발생했습니다.", exception);
        }
    }

    private String resolveImageUrlForDraft(ProductTempImage tempImage) {
        if (tempImage.getBackgroundRemoved() != null
                && tempImage.getBackgroundRemoved()
                && StringUtils.hasText(tempImage.getBackgroundRemovedImageUrl())) {
            return tempImage.getBackgroundRemovedImageUrl();
        }

        return tempImage.getOriginalImageUrl();
    }

    private String saveBase64Webp(String base64Image, Long memberId) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image);

            String fileName = System.currentTimeMillis() + "_bg_removed.webp";

            FileUploadResult uploadResult = fileStorageService.uploadBytes(
                    decodedBytes,
                    "products/background-removed/" + memberId,
                    fileName,
                    "image/webp"
            );

            return uploadResult.fileUrl();
        } catch (Exception exception) {
            throw new RuntimeException("배경 제거 이미지 저장 중 오류가 발생했습니다.", exception);
        }
    }

    private String extractFileName(String imageUrl) {
        int lastSlashIndex = imageUrl.lastIndexOf("/");
        if (lastSlashIndex == -1) {
            return imageUrl;
        }
        return imageUrl.substring(lastSlashIndex + 1);
    }

    private String normalizeMajorCategory(String aiMajor) {
        if (!StringUtils.hasText(aiMajor)) {
            throw new IllegalArgumentException("AI가 대카테고리를 반환하지 않았습니다.");
        }

        String normalized = aiMajor.trim().replaceAll("\\s+", "");

        return switch (normalized) {
            case "전공책/교재", "전공책", "교재", "도서", "책" -> "전공책 / 교재";
            case "디지털기기", "전자기기", "전자제품", "IT기기" -> "디지털기기";
            case "문구", "학용품", "문구용품" -> "문구 / 학용품";
            case "패션", "의류", "옷", "의류제품" -> "패션";
            case "생활용품", "잡화", "생활잡화" -> "생활용품";
            case "자취", "원룸", "자취용품", "원룸용품" -> "자취 / 원룸용품";
            case "뷰티", "미용", "화장품", "코스메틱" -> "뷰티 / 미용";
            case "스포츠", "취미", "운동용품" -> "스포츠 / 취미";
            case "티켓", "이용권", "쿠폰" -> "티켓 / 이용권";
            case "생활가전", "가전", "전자제품(가전)" -> "생활가전";
            case "식품", "소모품", "음식", "먹거리" -> "식품 / 소모품";
            default -> "기타";
        };
    }

    private String normalizeSubCategory(String aiSubCategory, String majorCategory) {
        if (!StringUtils.hasText(aiSubCategory)) {
            return getDefaultSubCategory(majorCategory);
        }

        String normalized = aiSubCategory.trim().replaceAll("\\s+", "");

        return switch (majorCategory) {
            case "전공책 / 교재" -> switch (normalized) {
                case "전공서적", "전공책" -> "전공서적";
                case "교양서적", "교양책" -> "교양서적";
                case "문제집" -> "문제집";
                case "자격증교재", "자격증책", "수험서" -> "자격증 교재";
                case "어학교재", "어학책" -> "어학 교재";
                case "필기노트", "제본자료" -> "필기노트 / 제본 자료";
                default -> "기타 도서";
            };
            case "디지털기기" -> switch (normalized) {
                case "노트북" -> "노트북";
                case "태블릿", "아이패드" -> "태블릿";
                case "휴대폰", "스마트폰" -> "휴대폰";
                case "스마트워치" -> "스마트워치";
                case "이어폰", "헤드폰" -> "이어폰/헤드폰";
                case "키보드", "마우스" -> "키보드/마우스";
                case "충전기", "케이블" -> "충전기/케이블";
                case "모니터" -> "모니터";
                default -> "기타 전자기기";
            };
            case "문구 / 학용품" -> switch (normalized) {
                case "필기구", "펜" -> "필기구";
                case "노트", "다이어리" -> "노트 / 다이어리";
                case "파일", "바인더" -> "파일 / 바인더";
                case "계산기" -> "계산기";
                case "독서대" -> "독서대";
                case "필통" -> "필통";
                default -> "기타 학용품";
            };
            case "패션" -> switch (normalized) {
                case "아우터" -> "아우터";
                case "상의" -> "상의";
                case "하의" -> "하의";
                case "원피스", "스커트" -> "원피스 / 스커트";
                case "신발" -> "신발";
                case "가방" -> "가방";
                case "모자" -> "모자";
                case "액세서리" -> "액세서리";
                default -> "기타 패션잡화";
            };
            case "생활용품" -> switch (normalized) {
                case "수납용품" -> "수납용품";
                case "침구", "쿠션" -> "침구 / 쿠션";
                case "조명" -> "조명";
                case "거울" -> "거울";
                case "청소용품" -> "청소용품";
                case "세탁용품" -> "세탁용품";
                case "욕실용품" -> "욕실용품";
                default -> "기타 생활용품";
            };
            case "자취 / 원룸용품" -> switch (normalized) {
                case "행거" -> "행거";
                case "선반" -> "선반";
                case "테이블" -> "테이블";
                case "의자" -> "의자";
                case "주방도구", "주방용품" -> "주방도구";
                case "식기", "컵" -> "식기 / 컵";
                case "전기장판", "히터" -> "전기장판 / 히터";
                default -> "기타 자취용품";
            };
            case "뷰티 / 미용" -> switch (normalized) {
                case "화장품" -> "화장품";
                case "향수" -> "향수";
                case "헤어기기" -> "헤어기기";
                case "미용소품" -> "미용소품";
                case "네일용품" -> "네일용품";
                default -> "기타 뷰티용품";
            };
            case "스포츠 / 취미" -> switch (normalized) {
                case "운동기구" -> "운동기구";
                case "요가", "필라테스" -> "요가 / 필라테스 용품";
                case "자전거" -> "자전거";
                case "악기" -> "악기";
                case "게임용품" -> "게임용품";
                case "피규어", "굿즈" -> "피규어 / 굿즈";
                case "보드게임" -> "보드게임";
                default -> "기타 취미용품";
            };
            case "티켓 / 이용권" -> switch (normalized) {
                case "공연", "전시" -> "공연 / 전시";
                case "영화" -> "영화";
                case "헬스장", "운동권" -> "헬스장 / 운동권";
                default -> "기타 이용권";
            };
            case "생활가전" -> switch (normalized) {
                case "드라이기" -> "드라이기";
                case "고데기" -> "고데기";
                case "선풍기" -> "선풍기";
                case "가습기" -> "가습기";
                case "전자레인지" -> "전자레인지";
                case "밥솥" -> "밥솥";
                case "청소기" -> "청소기";
                default -> "기타 소형가전";
            };
            case "식품 / 소모품" -> switch (normalized) {
                case "음료" -> "음료";
                case "영양제" -> "영양제";
                case "생필품" -> "생필품";
                default -> "기타 소모품";
            };
            default -> getDefaultSubCategory(majorCategory);
        };
    }

    private String getDefaultSubCategory(String majorCategory) {
        return switch (majorCategory) {
            case "전공책 / 교재" -> "기타 도서";
            case "디지털기기" -> "기타 전자기기";
            case "문구 / 학용품" -> "기타 학용품";
            case "패션" -> "기타 패션잡화";
            case "생활용품" -> "기타 생활용품";
            case "자취 / 원룸용품" -> "기타 자취용품";
            case "뷰티 / 미용" -> "기타 뷰티용품";
            case "스포츠 / 취미" -> "기타 취미용품";
            case "티켓 / 이용권" -> "기타 이용권";
            case "생활가전" -> "기타 소형가전";
            case "식품 / 소모품" -> "기타 소모품";
            default -> "기타";
        };
    }

    private String mapCondition(String aiCondition) {
        return switch (aiCondition) {
            case "미개봉" -> "UNOPENED";
            case "최상" -> "BEST";
            case "양호" -> "GOOD";
            case "보통" -> "NORMAL";
            default -> "NORMAL";
        };
    }
}