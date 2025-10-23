package com.kyc.dto;

import lombok.Data;
import com.kyc.model.KYCApplication.KYCStatus;
import java.time.LocalDateTime;

@Data
public class BlockchainStatusResponse {
    private boolean verified;
    private KYCStatus status;
    private String documentHash;
    private LocalDateTime submittedAt;
    private LocalDateTime verifiedAt;
    private String verifiedBy;
    private LocalDateTime expiryDate;
    private String remarks;
}
