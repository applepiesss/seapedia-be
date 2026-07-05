package com.seapedia.be.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "buyer_addresses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BuyerAddress {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false, unique = true)
    private User buyer;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false, length = 1000)
    private String fullAddress;
}