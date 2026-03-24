package com.campusmarket.backend.domain.category.repository;

import com.campusmarket.backend.domain.category.entity.MajorCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MajorCategoryRepository extends JpaRepository<MajorCategory, Long> {

    List<MajorCategory> findAllByOrderBySortOrderAsc();
}
