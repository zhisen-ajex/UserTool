package com.verify.repository;

import com.verify.entity.OrderRevenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRevenueRepository extends JpaRepository<OrderRevenue, Long> {
}
