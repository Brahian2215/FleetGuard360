package com.fleetguard360.adminpanel.repository;

import com.fleetguard360.adminpanel.model.FleetReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FleetReportRepository extends JpaRepository<FleetReport, Long> {
    List<FleetReport> findByReportDateBetween(LocalDateTime start, LocalDateTime end);

    List<FleetReport> findByReportType(String reportType);

    List<FleetReport> findByCreatedById(Long userId);
}
