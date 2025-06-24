package com.verify.repository;

import com.verify.entity.OrderRevenue;
import com.verify.entity.OrderRevenueV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRevenueRepositoryV2 extends JpaRepository<OrderRevenueV2, Long> {
}
