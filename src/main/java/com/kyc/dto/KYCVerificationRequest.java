package com.kyc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KYCVerificationRequest {
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    private Integer validityDays;
    
    private String remarks;
}
