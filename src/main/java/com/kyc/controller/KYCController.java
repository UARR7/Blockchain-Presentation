//package com.kyc.controller;
//
//import com.kyc.dto.*;
//import com.kyc.service.KYCService;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/kyc")
//@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:5173")
//@Tag(name = "KYC Management", description = "Handles KYC submission, verification, retrieval, and blockchain status.")
//public class KYCController {
//
//    private final KYCService kycService;
//
//    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> submitKYC(
//            @RequestParam("walletAddress") String walletAddress,
//            @RequestParam("fullName") String fullName,
//            @RequestParam("email") String email,
//            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
//            @RequestParam("documentType") String documentType,
//            @RequestParam("documentNumber") String documentNumber,
//            @RequestParam(value = "document", required = false) MultipartFile document
//    ) {
//        try {
//            KYCSubmissionRequest request = new KYCSubmissionRequest();
//            request.setWalletAddress(walletAddress);
//            request.setFullName(fullName);
//            request.setEmail(email);
//            request.setPhoneNumber(phoneNumber);
//            request.setDocumentType(documentType);
//            request.setDocumentNumber(documentNumber);
//
//            KYCApplicationResponse response = kycService.submitKYC(request, document);
//
//            return ResponseEntity.ok()
//                .body(new ApiResponse<>(true, "KYC application submitted successfully", response));
//        } catch (Exception e) {
//            log.error("Error submitting KYC: {}", e.getMessage());
//            return ResponseEntity.badRequest()
//                .body(new ApiResponse<>(false, e.getMessage(), null));
//        }
//    }
//
//    @PostMapping("/verify")
//    public ResponseEntity<?> verifyKYC(
//            @Valid @RequestBody KYCVerificationRequest request,
//            @RequestHeader(value = "X-Verifier-Address", required = false) String verifierAddress
//    ) {
//        try {
//            if (verifierAddress == null) {
//                verifierAddress = "system-admin";
//            }
//
//            KYCApplicationResponse response = kycService.verifyKYC(request, verifierAddress);
//
//            return ResponseEntity.ok()
//                .body(new ApiResponse<>(true, "KYC verification completed", response));
//        } catch (Exception e) {
//            log.error("Error verifying KYC: {}", e.getMessage());
//            return ResponseEntity.badRequest()
//                .body(new ApiResponse<>(false, e.getMessage(), null));
//        }
//    }
//
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<?> getKYCByUserId(@PathVariable String userId) {
//        try {
//            KYCApplicationResponse response = kycService.getKYCByUserId(userId);
//            return ResponseEntity.ok()
//                .body(new ApiResponse<>(true, "KYC application retrieved", response));
//        } catch (Exception e) {
//            log.error("Error retrieving KYC: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(new ApiResponse<>(false, e.getMessage(), null));
//        }
//    }
//
//    @GetMapping("/wallet/{walletAddress}")
//    public ResponseEntity<?> getKYCByWallet(@PathVariable String walletAddress) {
//        try {
//            KYCApplicationResponse response = kycService.getKYCByWallet(walletAddress);
//            return ResponseEntity.ok()
//                .body(new ApiResponse<>(true, "KYC application retrieved", response));
//        } catch (Exception e) {
//            log.error("Error retrieving KYC: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(new ApiResponse<>(false, e.getMessage(), null));
//        }
//    }
//
//    @GetMapping("/pending")
//    public ResponseEntity<?> getPendingApplications() {
//        try {
//            List<KYCApplicationResponse> responses = kycService.getPendingApplications();
//            return ResponseEntity.ok()
//                .body(new ApiResponse<>(true, "Pending applications retrieved", responses));
//        } catch (Exception e) {
//            log.error("Error retrieving pending applications: {}", e.getMessage());
//            return ResponseEntity.badRequest()
//                .body(new ApiResponse<>(false, e.getMessage(), null));
//        }
//    }
//
//    @GetMapping("/all")
//    public ResponseEntity<?> getAllApplications() {
//        try {
//            List<KYCApplicationResponse> responses = kycService.getAllApplications();
//            return ResponseEntity.ok()
//                .body(new ApiResponse<>(true, "All applications retrieved", responses));
//        } catch (Exception e) {
//            log.error("Error retrieving all applications: {}", e.getMessage());
//            return ResponseEntity.badRequest()
//                .body(new ApiResponse<>(false, e.getMessage(), null));
//        }
//    }
//
//    @GetMapping("/blockchain/status/{walletAddress}")
//    public ResponseEntity<?> checkBlockchainStatus(@PathVariable String walletAddress) {
//        try {
//            BlockchainStatusResponse response = kycService.checkBlockchainStatus(walletAddress);
//            return ResponseEntity.ok()
//                .body(new ApiResponse<>(true, "Blockchain status retrieved", response));
//        } catch (Exception e) {
//            log.error("Error checking blockchain status: {}", e.getMessage());
//            return ResponseEntity.badRequest()
//                .body(new ApiResponse<>(false, e.getMessage(), null));
//        }
//    }
//
//    @GetMapping("/document/{userId}")
//    public ResponseEntity<?> getDocument(@PathVariable String userId) {
//        try {
//            byte[] document = kycService.getDocument(userId);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//            headers.setContentDispositionFormData("attachment", "kyc-document-" + userId);
//
//            return ResponseEntity.ok()
//                .headers(headers)
//                .body(document);
//        } catch (Exception e) {
//            log.error("Error retrieving document: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(new ApiResponse<>(false, e.getMessage(), null));
//        }
//    }
//
//    @GetMapping("/health")
//    public ResponseEntity<?> healthCheck() {
//        return ResponseEntity.ok()
//            .body(new ApiResponse<>(true, "KYC service is running", null));
//    }
//
//    @lombok.Data
//    @lombok.AllArgsConstructor
//    public static class ApiResponse<T> {
//        private boolean success;
//        private String message;
//        private T data;
//    }
//}

////use it
//package com.kyc.controller;
//
//import com.kyc.dto.*;
//import com.kyc.service.KYCService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/kyc")
//@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:5173")
//@Tag(name = "KYC Management", description = "Handles KYC submission, verification, retrieval, and blockchain status.")
//public class KYCController {
//
//    private final KYCService kycService;
//
//    @Operation(summary = "Submit a KYC application", description = "Uploads KYC details and document for a user.")
//    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> submitKYC(
//            @RequestParam("walletAddress") String walletAddress,
//            @RequestParam("fullName") String fullName,
//            @RequestParam("email") String email,
//            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
//            @RequestParam("documentType") String documentType,
//            @RequestParam("documentNumber") String documentNumber,
//            @RequestParam(value = "document", required = false) MultipartFile document
//    ) {
//        try {
//            KYCSubmissionRequest request = new KYCSubmissionRequest();
//            request.setWalletAddress(walletAddress);
//            request.setFullName(fullName);
//            request.setEmail(email);
//            request.setPhoneNumber(phoneNumber);
//            request.setDocumentType(documentType);
//            request.setDocumentNumber(documentNumber);
//
//            KYCApplicationResponse response = kycService.submitKYC(request, document);
//            return ResponseEntity.ok()
//                    .body(new ApiResponse<>(true, "KYC application submitted successfully", response));
//        } catch (Exception e) {
//            log.error("Error submitting KYC: {}", e.getMessage());
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse<>(false, e.getMessage(), null));
//        }
//    }
//
//    @Operation(summary = "Verify KYC application", description = "Verifier approves or rejects a submitted KYC application.")
//    @PostMapping("/verify")
//    public ResponseEntity<?> verifyKYC(
//            @Valid @RequestBody KYCVerificationRequest request,
//            @RequestHeader(value = "X-Verifier-Address", required = false) String verifierAddress
//    ) {
//        try {
//            if (verifierAddress == null) verifierAddress = "system-admin";
//            KYCApplicationResponse response = kycService.verifyKYC(request, verifierAddress);
//            return ResponseEntity.ok()
//                    .body(new ApiResponse<>(true, "KYC verification completed", response));
//        } catch (Exception e) {
//            log.error("Error verifying KYC: {}", e.getMessage());
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse<>(false, e.getMessage(), null));
//        }
//    }
//
//    @Operation(summary = "Get KYC by user ID", description = "Fetches the KYC application details for a specific user.")
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<?> getKYCByUserId(@PathVariable String userId) {
//        try {
//            KYCApplicationResponse response = kycService.getKYCByUserId(userId);
//            return ResponseEntity.ok()
//                    .body(new ApiResponse<>(true, "KYC application retrieved", response));
//        } catch (Exception e) {
//            log.error("Error retrieving KYC: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new ApiResponse<>(false, e.getMessage(), null));
//        }
//    }
//
//    @Operation(summary = "Get KYC by wallet address", description = "Fetches the KYC application using a user's blockchain wallet address.")
//    @GetMapping("/wallet/{walletAddress}")
//    public ResponseEntity<?> getKYCByWallet(@PathVariable String walletAddress) {
//        try {
//            KYCApplicationResponse response = kycService.getKYCByWallet(walletAddress);
//            return ResponseEntity.ok()
//                    .body(new ApiResponse<>(true, "KYC application retrieved", response));
//        } catch (Exception e) {
//            log.error("Error retrieving KYC: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new ApiResponse<>(false, e.getMessage(), null));
//        }
//    }
//
//    @Operation(summary = "List pending KYC applications", description = "Retrieves all KYC applications that are pending verification.")
//    @GetMapping("/pending")
//    public ResponseEntity<?> getPendingApplications() {
//        try {
//            List<KYCApplicationResponse> responses = kycService.getPendingApplications();
//            return ResponseEntity.ok()
//                    .body(new ApiResponse<>(true, "Pending applications retrieved", responses));
//        } catch (Exception e) {
//            log.error("Error retrieving pending applications: {}", e.getMessage());
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse<>(false, e.getMessage(), null));
//        }
//    }
//
//    @Operation(summary = "List all KYC applications", description = "Retrieves all submitted KYC applications (verified + pending).")
//    @GetMapping("/all")
//    public ResponseEntity<?> getAllApplications() {
//        try {
//            List<KYCApplicationResponse> responses = kycService.getAllApplications();
//            return ResponseEntity.ok()
//                    .body(new ApiResponse<>(true, "All applications retrieved", responses));
//        } catch (Exception e) {
//            log.error("Error retrieving all applications: {}", e.getMessage());
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse<>(false, e.getMessage(), null));
//        }
//    }
//
//    @Operation(summary = "Check blockchain verification status", description = "Checks if a user's KYC status is updated on the blockchain.")
//    @GetMapping("/blockchain/status/{walletAddress}")
//    public ResponseEntity<?> checkBlockchainStatus(@PathVariable String walletAddress) {
//        try {
//            BlockchainStatusResponse response = kycService.checkBlockchainStatus(walletAddress);
//            return ResponseEntity.ok()
//                    .body(new ApiResponse<>(true, "Blockchain status retrieved", response));
//        } catch (Exception e) {
//            log.error("Error checking blockchain status: {}", e.getMessage());
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse<>(false, e.getMessage(), null));
//        }
//    }
//
//    @Operation(summary = "Download KYC document", description = "Downloads the uploaded document for a specific user.")
//    @GetMapping("/document/{userId}")
//    public ResponseEntity<?> getDocument(@PathVariable String userId) {
//        try {
//            byte[] document = kycService.getDocument(userId);
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//            headers.setContentDispositionFormData("attachment", "kyc-document-" + userId);
//            return ResponseEntity.ok().headers(headers).body(document);
//        } catch (Exception e) {
//            log.error("Error retrieving document: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new ApiResponse<>(false, e.getMessage(), null));
//        }
//    }
//
//    @Operation(summary = "Health check", description = "Checks if the KYC service is running.")
//    @GetMapping("/health")
//    public ResponseEntity<?> healthCheck() {
//        return ResponseEntity.ok()
//                .body(new ApiResponse<>(true, "KYC service is running", null));
//    }
//
//    @lombok.Data
//    @lombok.AllArgsConstructor
//    public static class ApiResponse<T> {
//        private boolean success;
//        private String message;
//        private T data;
//    }
//}


package com.kyc.controller;

import com.kyc.dto.*;
import com.kyc.service.KYCService;
import com.kyc.service.WalletVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/kyc")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "KYC Management", description = "Handles KYC submission, verification, retrieval, and blockchain status.")
public class KYCController {

    private final KYCService kycService;
    private final WalletVerificationService walletVerificationService;

    @Operation(summary = "Generate verification message",
            description = "Generate a message for the user to sign with their wallet")
    @PostMapping("/generate-message")
    public ResponseEntity<?> generateVerificationMessage(
            @RequestParam("email") String email,
            @RequestParam("fullName") String fullName
    ) {
        try {
            Long timestamp = Instant.now().getEpochSecond();
            String message = walletVerificationService.generateVerificationMessage(
                    email, fullName, timestamp);

            return ResponseEntity.ok()
                    .body(new MessageResponse(message, timestamp));
        } catch (Exception e) {
            log.error("Error generating verification message: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "Submit a KYC application",
            description = "Uploads KYC details and document for a user with wallet verification.")
    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitKYC(
            @RequestParam("walletAddress") String walletAddress,
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam("documentType") String documentType,
            @RequestParam("documentNumber") String documentNumber,
            @RequestParam("signature") String signature,
            @RequestParam("timestamp") Long timestamp,
            @RequestParam(value = "document", required = false) MultipartFile document
    ) {
        try {
            // Verify wallet ownership first
            boolean isVerified = walletVerificationService.verifyWalletWithGeneratedMessage(
                    walletAddress, signature, email, fullName, timestamp);

            if (!isVerified) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false,
                                "Wallet verification failed. Please ensure you signed the correct message.",
                                null));
            }

            KYCSubmissionRequest request = new KYCSubmissionRequest();
            request.setWalletAddress(walletAddress);
            request.setFullName(fullName);
            request.setEmail(email);
            request.setPhoneNumber(phoneNumber);
            request.setDocumentType(documentType);
            request.setDocumentNumber(documentNumber);
            request.setSignature(signature);
            request.setTimestamp(timestamp);

            KYCApplicationResponse response = kycService.submitKYC(request, document);
            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "KYC application submitted successfully", response));
        } catch (Exception e) {
            log.error("Error submitting KYC: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "Verify wallet signature",
            description = "Verify a wallet signature before KYC submission")
    @PostMapping("/verify-wallet")
    public ResponseEntity<?> verifyWallet(@Valid @RequestBody WalletVerificationDTO request) {
        try {
            boolean isValid = walletVerificationService.verifyWalletOwnership(
                    request.getWalletAddress(),
                    request.getSignature(),
                    request.getMessage(),
                    request.getTimestamp()
            );

            return ResponseEntity.ok()
                    .body(new ApiResponse<>(isValid,
                            isValid ? "Wallet verified successfully" : "Wallet verification failed",
                            null));
        } catch (Exception e) {
            log.error("Error verifying wallet: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "Verify KYC application",
            description = "Verifier approves or rejects a submitted KYC application.")
    @PostMapping("/verify")
    public ResponseEntity<?> verifyKYC(
            @Valid @RequestBody KYCVerificationRequest request,
            @RequestHeader(value = "X-Verifier-Address", required = false) String verifierAddress
    ) {
        try {
            if (verifierAddress == null) verifierAddress = "system-admin";
            KYCApplicationResponse response = kycService.verifyKYC(request, verifierAddress);
            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "KYC verification completed", response));
        } catch (Exception e) {
            log.error("Error verifying KYC: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "Get KYC by user ID",
            description = "Fetches the KYC application details for a specific user.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getKYCByUserId(@PathVariable String userId) {
        try {
            KYCApplicationResponse response = kycService.getKYCByUserId(userId);
            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "KYC application retrieved", response));
        } catch (Exception e) {
            log.error("Error retrieving KYC: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "Get KYC by wallet address",
            description = "Fetches the KYC application using a user's blockchain wallet address.")
    @GetMapping("/wallet/{walletAddress}")
    public ResponseEntity<?> getKYCByWallet(@PathVariable String walletAddress) {
        try {
            KYCApplicationResponse response = kycService.getKYCByWallet(walletAddress);
            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "KYC application retrieved", response));
        } catch (Exception e) {
            log.error("Error retrieving KYC: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "List pending KYC applications",
            description = "Retrieves all KYC applications that are pending verification.")
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingApplications() {
        try {
            List<KYCApplicationResponse> responses = kycService.getPendingApplications();
            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "Pending applications retrieved", responses));
        } catch (Exception e) {
            log.error("Error retrieving pending applications: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "List all KYC applications",
            description = "Retrieves all submitted KYC applications (verified + pending).")
    @GetMapping("/all")
    public ResponseEntity<?> getAllApplications() {
        try {
            List<KYCApplicationResponse> responses = kycService.getAllApplications();
            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "All applications retrieved", responses));
        } catch (Exception e) {
            log.error("Error retrieving all applications: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "Check blockchain verification status",
            description = "Checks if a user's KYC status is updated on the blockchain.")
    @GetMapping("/blockchain/status/{walletAddress}")
    public ResponseEntity<?> checkBlockchainStatus(@PathVariable String walletAddress) {
        try {
            BlockchainStatusResponse response = kycService.checkBlockchainStatus(walletAddress);
            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "Blockchain status retrieved", response));
        } catch (Exception e) {
            log.error("Error checking blockchain status: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "Download KYC document",
            description = "Downloads the uploaded document for a specific user.")
    @GetMapping("/document/{userId}")
    public ResponseEntity<?> getDocument(@PathVariable String userId) {
        try {
            byte[] document = kycService.getDocument(userId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "kyc-document-" + userId);
            return ResponseEntity.ok().headers(headers).body(document);
        } catch (Exception e) {
            log.error("Error retrieving document: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "Health check",
            description = "Checks if the KYC service is running.")
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, "KYC service is running", null));
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class MessageResponse {
        private String message;
        private Long timestamp;
    }
}
