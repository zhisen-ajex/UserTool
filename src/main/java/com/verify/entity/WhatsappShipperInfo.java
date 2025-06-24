package com.verify.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "whatsapp_shipper_info")
public class WhatsappShipperInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_ar")
    private String nameAr;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "status")
    private Boolean status;

    // Getters & Setters
}
