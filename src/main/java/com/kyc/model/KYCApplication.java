package com.kyc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KYCApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String userId;
    
    @Column(nullable = false)
    private String walletAddress;
    
    @Column(nullable = false)
    private String fullName;
    
    @Column(nullable = false)
    private String email;
    
    private String phoneNumber;
    
    @Column(nullable = false)
    private String documentType;
    
    @Column(nullable = false)
    private String documentNumber;
    
    @Column(columnDefinition = "TEXT")
    private String documentIpfsHash;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KYCStatus status;
    
    private String blockchainTxHash;
    
    @Column(columnDefinition = "TEXT")
    private String remarks;
    
    private String verifiedBy;
    
    @Column(nullable = false)
    private LocalDateTime submittedAt;
    
    private LocalDateTime verifiedAt;
    
    private LocalDateTime expiryDate;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        submittedAt = LocalDateTime.now();
        status = KYCStatus.PENDING;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum KYCStatus {
        PENDING,
        UNDER_REVIEW,
        APPROVED,
        REJECTED,
        EXPIRED
    }
}
