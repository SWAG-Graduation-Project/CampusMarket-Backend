package com.campusmarket.backend.domain.category.repository;

import com.campusmarket.backend.domain.category.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    List<SubCategory> findAllByMajorCategory_IdOrderBySortOrderAsc(Long majorCategoryId);

    Optional<SubCategory> findByName(String name);

    Optional<SubCategory> findByNameAndMajorCategory_Id(String name, Long majorCategoryId);
}