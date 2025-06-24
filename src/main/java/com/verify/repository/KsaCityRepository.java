package com.verify.repository;
import com.verify.entity.KsaCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KsaCityRepository extends JpaRepository<KsaCity, Long> {

    // 根据 code 查询 tier_code
    KsaCity findByCode(String code);
}
