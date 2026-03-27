package com.campusmarket.backend.domain.report.repository;

import com.campusmarket.backend.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReporterIdAndChatRoomId(Long reporterId, Long chatRoomId);
}
