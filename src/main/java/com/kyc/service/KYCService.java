//package com.kyc.service;
//
//import com.kyc.dto.*;
//import com.kyc.model.KYCApplication;
//import com.kyc.repository.KYCRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class KYCService {
//
//    private final KYCRepository kycRepository;
//    private final BlockchainService blockchainService;
//    private final IPFSService ipfsService;
//
//    @Transactional
//    public KYCApplicationResponse submitKYC(KYCSubmissionRequest request, MultipartFile document) {
//        try {
//            KYCApplication existing = kycRepository.findByWalletAddress(request.getWalletAddress());
//            if (existing != null &&
//                (existing.getStatus() == KYCApplication.KYCStatus.APPROVED ||
//                 existing.getStatus() == KYCApplication.KYCStatus.UNDER_REVIEW)) {
//                throw new RuntimeException("KYC application already exists for this wallet");
//            }
//
//            String ipfsHash = null;
//            if (document != null && !document.isEmpty()) {
//                ipfsHash = ipfsService.uploadFile(document);
//                log.info("Document uploaded to IPFS: {}", ipfsHash);
//            }
//
//            KYCApplication application = new KYCApplication();
//            application.setUserId(UUID.randomUUID().toString());
//            application.setWalletAddress(request.getWalletAddress());
//            application.setFullName(request.getFullName());
//            application.setEmail(request.getEmail());
//            application.setPhoneNumber(request.getPhoneNumber());
//            application.setDocumentType(request.getDocumentType());
//            application.setDocumentNumber(request.getDocumentNumber());
//            application.setDocumentIpfsHash(ipfsHash);
//            application.setStatus(KYCApplication.KYCStatus.PENDING);
//
//            application = kycRepository.save(application);
//
//            try {
//                String txHash = blockchainService.submitKYC(
//                    request.getWalletAddress(),
//                    ipfsHash != null ? ipfsHash : "no-document"
//                );
//                application.setBlockchainTxHash(txHash);
//                application.setStatus(KYCApplication.KYCStatus.UNDER_REVIEW);
//                application = kycRepository.save(application);
//                log.info("KYC submitted to blockchain with tx: {}", txHash);
//            } catch (Exception e) {
//                log.error("Failed to submit to blockchain: {}", e.getMessage());
//            }
//
//            return mapToResponse(application);
//
//        } catch (Exception e) {
//            log.error("Error submitting KYC: {}", e.getMessage());
//            throw new RuntimeException("Failed to submit KYC application", e);
//        }
//    }
//
//    @Transactional
//    public KYCApplicationResponse verifyKYC(KYCVerificationRequest request, String verifierAddress) {
//        try {
//            KYCApplication application = kycRepository.findByUserId(request.getUserId());
//            if (application == null) {
//                throw new RuntimeException("KYC application not found");
//            }
//
//            KYCApplication.KYCStatus newStatus =
//                "APPROVED".equals(request.getStatus())
//                    ? KYCApplication.KYCStatus.APPROVED
//                    : KYCApplication.KYCStatus.REJECTED;
//
//            application.setStatus(newStatus);
//            application.setRemarks(request.getRemarks());
//            application.setVerifiedBy(verifierAddress);
//            application.setVerifiedAt(LocalDateTime.now());
//
//            if (newStatus == KYCApplication.KYCStatus.APPROVED && request.getValidityDays() != null) {
//                application.setExpiryDate(LocalDateTime.now().plusDays(request.getValidityDays()));
//            }
//
//            try {
//                int blockchainStatus = newStatus == KYCApplication.KYCStatus.APPROVED ? 1 : 2;
//                String txHash = blockchainService.verifyKYC(
//                    application.getWalletAddress(),
//                    blockchainStatus,
//                    request.getValidityDays() != null ? request.getValidityDays() : 0,
//                    request.getRemarks() != null ? request.getRemarks() : ""
//                );
//                application.setBlockchainTxHash(txHash);
//                log.info("KYC verification recorded on blockchain with tx: {}", txHash);
//            } catch (Exception e) {
//                log.error("Failed to verify on blockchain: {}", e.getMessage());
//            }
//
//            application = kycRepository.save(application);
//            return mapToResponse(application);
//
//        } catch (Exception e) {
//            log.error("Error verifying KYC: {}", e.getMessage());
//            throw new RuntimeException("Failed to verify KYC application", e);
//        }
//    }
//
//    public KYCApplicationResponse getKYCByUserId(String userId) {
//        KYCApplication application = kycRepository.findByUserId(userId);
//        if (application == null) {
//            throw new RuntimeException("KYC application not found");
//        }
//        return mapToResponse(application);
//    }
//
//    public KYCApplicationResponse getKYCByWallet(String walletAddress) {
//        KYCApplication application = kycRepository.findByWalletAddress(walletAddress);
//        if (application == null) {
//            throw new RuntimeException("KYC application not found");
//        }
//        return mapToResponse(application);
//    }
//
//    public List<KYCApplicationResponse> getPendingApplications() {
//        return kycRepository.findByStatus(KYCApplication.KYCStatus.PENDING)
//            .stream()
//            .map(this::mapToResponse)
//            .collect(Collectors.toList());
//    }
//
//    public List<KYCApplicationResponse> getAllApplications() {
//        return kycRepository.findAll()
//            .stream()
//            .map(this::mapToResponse)
//            .collect(Collectors.toList());
//    }
//
//    public BlockchainStatusResponse checkBlockchainStatus(String walletAddress) {
//        try {
//            BlockchainService.KYCRecordResponse record =
//                blockchainService.getKYCRecord(walletAddress);
//
//            BlockchainStatusResponse response = new BlockchainStatusResponse();
//            response.setVerified(record.getStatus() == 1);
//            response.setStatus(mapBlockchainStatus(record.getStatus()));
//            response.setDocumentHash(record.getDocumentHash());
//            response.setSubmittedAt(LocalDateTime.ofEpochSecond(record.getSubmittedAt(), 0, java.time.ZoneOffset.UTC));
//
//            if (record.getVerifiedAt() > 0) {
//                response.setVerifiedAt(LocalDateTime.ofEpochSecond(record.getVerifiedAt(), 0, java.time.ZoneOffset.UTC));
//            }
//
//            response.setVerifiedBy(record.getVerifiedBy());
//
//            if (record.getExpiryDate() > 0) {
//                response.setExpiryDate(LocalDateTime.ofEpochSecond(record.getExpiryDate(), 0, java.time.ZoneOffset.UTC));
//            }
//
//            response.setRemarks(record.getRemarks());
//
//            return response;
//        } catch (Exception e) {
//            log.error("Error checking blockchain status: {}", e.getMessage());
//            throw new RuntimeException("Failed to check blockchain status", e);
//        }
//    }
//
//    public byte[] getDocument(String userId) {
//        try {
//            KYCApplication application = kycRepository.findByUserId(userId);
//            if (application == null || application.getDocumentIpfsHash() == null) {
//                throw new RuntimeException("Document not found");
//            }
//
//            return ipfsService.retrieveFile(application.getDocumentIpfsHash());
//        } catch (Exception e) {
//            log.error("Error retrieving document: {}", e.getMessage());
//            throw new RuntimeException("Failed to retrieve document", e);
//        }
//    }
//
//    private KYCApplicationResponse mapToResponse(KYCApplication application) {
//        KYCApplicationResponse response = new KYCApplicationResponse();
//        response.setId(application.getId());
//        response.setUserId(application.getUserId());
//        response.setWalletAddress(application.getWalletAddress());
//        response.setFullName(application.getFullName());
//        response.setEmail(application.getEmail());
//        response.setPhoneNumber(application.getPhoneNumber());
//        response.setDocumentType(application.getDocumentType());
//        response.setDocumentNumber(application.getDocumentNumber());
//        response.setDocumentIpfsHash(application.getDocumentIpfsHash());
//        response.setStatus(application.getStatus());
//        response.setBlockchainTxHash(application.getBlockchainTxHash());
//        response.setRemarks(application.getRemarks());
//        response.setVerifiedBy(application.getVerifiedBy());
//        response.setSubmittedAt(application.getSubmittedAt());
//        response.setVerifiedAt(application.getVerifiedAt());
//        response.setExpiryDate(application.getExpiryDate());
//        return response;
//    }
//
//    private KYCApplication.KYCStatus mapBlockchainStatus(int status) {
//        switch (status) {
//            case 0: return KYCApplication.KYCStatus.PENDING;
//            case 1: return KYCApplication.KYCStatus.APPROVED;
//            case 2: return KYCApplication.KYCStatus.REJECTED;
//            case 3: return KYCApplication.KYCStatus.EXPIRED;
//            default: return KYCApplication.KYCStatus.PENDING;
//        }
//    }
//}
//
//


package com.kyc.service;

import com.kyc.dto.*;
import com.kyc.model.KYCApplication;
import com.kyc.repository.KYCRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KYCService {

    private final KYCRepository kycRepository;
    private final BlockchainService blockchainService;
    private final IPFSService ipfsService;
    private final WalletVerificationService walletVerificationService;

    @Transactional
    public KYCApplicationResponse submitKYC(KYCSubmissionRequest request, MultipartFile document) {
        try {
            // Verify wallet ownership before proceeding
            boolean isVerified = walletVerificationService.verifyWalletWithGeneratedMessage(
                    request.getWalletAddress(),
                    request.getSignature(),
                    request.getEmail(),
                    request.getFullName(),
                    request.getTimestamp()
            );

            if (!isVerified) {
                throw new RuntimeException("Wallet verification failed. Cannot submit KYC.");
            }

            KYCApplication existing = kycRepository.findByWalletAddress(request.getWalletAddress());
            if (existing != null &&
                    (existing.getStatus() == KYCApplication.KYCStatus.APPROVED ||
                            existing.getStatus() == KYCApplication.KYCStatus.UNDER_REVIEW)) {
                throw new RuntimeException("KYC application already exists for this wallet");
            }

            String ipfsHash = null;
            if (document != null && !document.isEmpty()) {
                ipfsHash = ipfsService.uploadFile(document);
                log.info("Document uploaded to IPFS: {}", ipfsHash);
            }

            KYCApplication application = new KYCApplication();
            application.setUserId(UUID.randomUUID().toString());
            application.setWalletAddress(request.getWalletAddress());
            application.setFullName(request.getFullName());
            application.setEmail(request.getEmail());
            application.setPhoneNumber(request.getPhoneNumber());
            application.setDocumentType(request.getDocumentType());
            application.setDocumentNumber(request.getDocumentNumber());
            application.setDocumentIpfsHash(ipfsHash);
            application.setStatus(KYCApplication.KYCStatus.PENDING);

            application = kycRepository.save(application);

            try {
                String txHash = blockchainService.submitKYC(
                        request.getWalletAddress(),
                        ipfsHash != null ? ipfsHash : "no-document"
                );
                application.setBlockchainTxHash(txHash);
                application.setStatus(KYCApplication.KYCStatus.UNDER_REVIEW);
                application = kycRepository.save(application);
                log.info("KYC submitted to blockchain with tx: {}", txHash);
            } catch (Exception e) {
                log.error("Failed to submit to blockchain: {}", e.getMessage());
            }

            return mapToResponse(application);

        } catch (Exception e) {
            log.error("Error submitting KYC: {}", e.getMessage());
            throw new RuntimeException("Failed to submit KYC application", e);
        }
    }

    @Transactional
    public KYCApplicationResponse verifyKYC(KYCVerificationRequest request, String verifierAddress) {
        try {
            KYCApplication application = kycRepository.findByUserId(request.getUserId());
            if (application == null) {
                throw new RuntimeException("KYC application not found");
            }

            KYCApplication.KYCStatus newStatus =
                    "APPROVED".equals(request.getStatus())
                            ? KYCApplication.KYCStatus.APPROVED
                            : KYCApplication.KYCStatus.REJECTED;

            application.setStatus(newStatus);
            application.setRemarks(request.getRemarks());
            application.setVerifiedBy(verifierAddress);
            application.setVerifiedAt(LocalDateTime.now());

            if (newStatus == KYCApplication.KYCStatus.APPROVED && request.getValidityDays() != null) {
                application.setExpiryDate(LocalDateTime.now().plusDays(request.getValidityDays()));
            }

            try {
                int blockchainStatus = newStatus == KYCApplication.KYCStatus.APPROVED ? 1 : 2;
                String txHash = blockchainService.verifyKYC(
                        application.getWalletAddress(),
                        blockchainStatus,
                        request.getValidityDays() != null ? request.getValidityDays() : 0,
                        request.getRemarks() != null ? request.getRemarks() : ""
                );
                application.setBlockchainTxHash(txHash);
                log.info("KYC verification recorded on blockchain with tx: {}", txHash);
            } catch (Exception e) {
                log.error("Failed to verify on blockchain: {}", e.getMessage());
            }

            application = kycRepository.save(application);
            return mapToResponse(application);

        } catch (Exception e) {
            log.error("Error verifying KYC: {}", e.getMessage());
            throw new RuntimeException("Failed to verify KYC application", e);
        }
    }

    public KYCApplicationResponse getKYCByUserId(String userId) {
        KYCApplication application = kycRepository.findByUserId(userId);
        if (application == null) {
            throw new RuntimeException("KYC application not found");
        }
        return mapToResponse(application);
    }

    public KYCApplicationResponse getKYCByWallet(String walletAddress) {
        KYCApplication application = kycRepository.findByWalletAddress(walletAddress);
        if (application == null) {
            throw new RuntimeException("KYC application not found");
        }
        return mapToResponse(application);
    }

    public List<KYCApplicationResponse> getPendingApplications() {
        return kycRepository.findByStatus(KYCApplication.KYCStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<KYCApplicationResponse> getAllApplications() {
        return kycRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public BlockchainStatusResponse checkBlockchainStatus(String walletAddress) {
        try {
            BlockchainService.KYCRecordResponse record =
                    blockchainService.getKYCRecord(walletAddress);

            BlockchainStatusResponse response = new BlockchainStatusResponse();
            response.setVerified(record.getStatus() == 1);
            response.setStatus(mapBlockchainStatus(record.getStatus()));
            response.setDocumentHash(record.getDocumentHash());
            response.setSubmittedAt(LocalDateTime.ofEpochSecond(record.getSubmittedAt(), 0, java.time.ZoneOffset.UTC));

            if (record.getVerifiedAt() > 0) {
                response.setVerifiedAt(LocalDateTime.ofEpochSecond(record.getVerifiedAt(), 0, java.time.ZoneOffset.UTC));
            }

            response.setVerifiedBy(record.getVerifiedBy());

            if (record.getExpiryDate() > 0) {
                response.setExpiryDate(LocalDateTime.ofEpochSecond(record.getExpiryDate(), 0, java.time.ZoneOffset.UTC));
            }

            response.setRemarks(record.getRemarks());

            return response;
        } catch (Exception e) {
            log.error("Error checking blockchain status: {}", e.getMessage());
            throw new RuntimeException("Failed to check blockchain status", e);
        }
    }

    public byte[] getDocument(String userId) {
        try {
            KYCApplication application = kycRepository.findByUserId(userId);
            if (application == null || application.getDocumentIpfsHash() == null) {
                throw new RuntimeException("Document not found");
            }

            return ipfsService.retrieveFile(application.getDocumentIpfsHash());
        } catch (Exception e) {
            log.error("Error retrieving document: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve document", e);
        }
    }

    private KYCApplicationResponse mapToResponse(KYCApplication application) {
        KYCApplicationResponse response = new KYCApplicationResponse();
        response.setId(application.getId());
        response.setUserId(application.getUserId());
        response.setWalletAddress(application.getWalletAddress());
        response.setFullName(application.getFullName());
        response.setEmail(application.getEmail());
        response.setPhoneNumber(application.getPhoneNumber());
        response.setDocumentType(application.getDocumentType());
        response.setDocumentNumber(application.getDocumentNumber());
        response.setDocumentIpfsHash(application.getDocumentIpfsHash());
        response.setStatus(application.getStatus());
        response.setBlockchainTxHash(application.getBlockchainTxHash());
        response.setRemarks(application.getRemarks());
        response.setVerifiedBy(application.getVerifiedBy());
        response.setSubmittedAt(application.getSubmittedAt());
        response.setVerifiedAt(application.getVerifiedAt());
        response.setExpiryDate(application.getExpiryDate());
        return response;
    }

    private KYCApplication.KYCStatus mapBlockchainStatus(int status) {
        switch (status) {
            case 0: return KYCApplication.KYCStatus.PENDING;
            case 1: return KYCApplication.KYCStatus.APPROVED;
            case 2: return KYCApplication.KYCStatus.REJECTED;
            case 3: return KYCApplication.KYCStatus.EXPIRED;
            default: return KYCApplication.KYCStatus.PENDING;
        }
    }
}