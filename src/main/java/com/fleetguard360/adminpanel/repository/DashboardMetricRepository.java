package com.fleetguard360.adminpanel.repository;

import com.fleetguard360.adminpanel.model.DashboardMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DashboardMetricRepository extends JpaRepository<DashboardMetric, Long> {
    List<DashboardMetric> findByMetricType(String metricType);

    List<DashboardMetric> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<DashboardMetric> findByVehicleId(Long vehicleId);

    List<DashboardMetric> findByDriverId(Long driverId);

    @Query("SELECT m FROM DashboardMetric m WHERE m.metricType = ?1 ORDER BY m.timestamp DESC LIMIT 1")
    DashboardMetric findLatestByMetricType(String metricType);
}
