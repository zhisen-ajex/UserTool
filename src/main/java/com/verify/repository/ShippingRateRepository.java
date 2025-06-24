package com.verify.repository;

import com.verify.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import com.verify.entity.ShippingRate;

public interface ShippingRateRepository extends JpaRepository<ShippingRate, Long> {

    @Query("""
        SELECT sr FROM ShippingRate sr 
        WHERE sr.countryCode = :countryCode
          AND sr.serviceType = :serviceType
          AND :weight > sr.weightMin AND :weight <=sr.weightMax
    """)
    Optional<ShippingRate> findSuitableRate(
            @Param("countryCode") String countryCode,
            @Param("serviceType") ServiceType serviceType,
            @Param("weight") BigDecimal weight
    );
}