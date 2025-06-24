package com.verify.repository;


import com.verify.entity.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {
    List<PricingRule> findByCustomerCodeAndProductsLike(String customerCode, String products);
    List<PricingRule> findByCustomerCodeAndProductsLikeAndCountryAndType(String customerCode, String products,String country, String type);
    List<PricingRule> findByCustomerCode(String customerCode);
}
