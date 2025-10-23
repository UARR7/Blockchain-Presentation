//package com.kyc.dto;
//
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import lombok.Data;
//
//@Data
//public class KYCSubmissionRequest {
//    @NotBlank(message = "Wallet address is required")
//    private String walletAddress;
//
//    @NotBlank(message = "Full name is required")
//    private String fullName;
//
//    @Email(message = "Valid email is required")
//    @NotBlank(message = "Email is required")
//    private String email;
//
//    private String phoneNumber;
//
//    @NotBlank(message = "Document type is required")
//    private String documentType;
//
//    @NotBlank(message = "Document number is required")
//    private String documentNumber;
//}

package com.kyc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KYCSubmissionRequest {
    @NotBlank(message = "Wallet address is required")
    private String walletAddress;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;

    private String phoneNumber;

    @NotBlank(message = "Document type is required")
    private String documentType;

    @NotBlank(message = "Document number is required")
    private String documentNumber;

    // New fields for wallet verification
    @NotBlank(message = "Signature is required")
    private String signature;

    @NotBlank(message = "Timestamp is required")
    private Long timestamp;
}

////
//package com.kyc.dto;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import lombok.*;
//
//@Data @NoArgsConstructor @AllArgsConstructor @Builder
//public class KYCSubmissionRequest {
//    @NotBlank(message = "Document type is required")
//    private String documentType;
//
//    @NotBlank(message = "Document number is required")
//    private String documentNumber;
//
//    @NotBlank(message = "Document hash is required")
//    private String documentHash;
//
//    @NotBlank(message = "Full name is required")
//    private String fullName;
//
//    private String dateOfBirth;
//
//    @NotBlank(message = "Nationality is required")
//    private String nationality;
//
//    @NotBlank(message = "Address is required")
//    private String address;
//
//    // NEW FIELDS FOR WALLET VERIFICATION
//    @NotBlank(message = "Wallet address is required")
//    private String walletAddress;
//
//    @NotBlank(message = "Wallet signature is required")
//    private String walletSignature;
//
//    @NotNull(message = "Signature timestamp is required")
//    private Long signatureTimestamp;
//
//    @Email(message = "Valid email is required")
//    @NotBlank(message = "Email is required")
//    private String email;
//
//    private String phoneNumber;
//}
