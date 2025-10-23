package com.kyc.dto;

import lombok.Data;
import com.kyc.model.KYCApplication.KYCStatus;
import java.time.LocalDateTime;

@Data
public class KYCApplicationResponse {
    private Long id;
    private String userId;
    private String walletAddress;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String documentType;
    private String documentNumber;
    private String documentIpfsHash;
    private KYCStatus status;
    private String blockchainTxHash;
    private String remarks;
    private String verifiedBy;
    private LocalDateTime submittedAt;
    private LocalDateTime verifiedAt;
    private LocalDateTime expiryDate;
}
