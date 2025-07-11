package com.verify.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 是否偏远地区， 是的话加钱，
 *
 * @author Jeffery Xie
 * @since 2025/07/11
 */
@Data
@Entity
@Table(name = "remote_city")
public class RemoteCity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "country_iso_code3")
    private String countryIsoCode3;

    @Column(name = "region_code")
    private String regionCode;

    @Column(name = "city_code")
    private String cityCode;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "is_remote")
    private Boolean isRemote;

}
