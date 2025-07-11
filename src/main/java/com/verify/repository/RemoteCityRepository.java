package com.verify.repository;

import com.verify.entity.RemoteCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * RemoteCityRepository
 *
 * @author Jeffery Xie
 * @since 2025/07/11
 */
@Repository
public interface RemoteCityRepository extends JpaRepository<RemoteCity, Long> {


}
