package com.verify.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ksa_city")
public class KsaCity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String aliases;

    @Column(name = "locales_name")
    private String localesName;

    @Column(unique = true)
    private String code;

    @Column(name = "country_iso_code3")
    private String countryIsoCode3;

    @Column(name = "region_code")
    private String regionCode;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "zone_code")
    private String zoneCode;

    @Column(name = "fallback_zone_code")
    private String fallbackZoneCode;

    @Column(name = "is_remote")
    private Boolean isRemote;

    @Column(name = "tier_code")
    private String tierCode;
}
