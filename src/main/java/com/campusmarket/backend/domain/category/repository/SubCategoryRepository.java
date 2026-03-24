package com.campusmarket.backend.domain.category.repository;

import com.campusmarket.backend.domain.category.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    List<SubCategory> findAllByMajorCategoryIdOrderBySortOrderAsc(Long majorCategoryId);
}
