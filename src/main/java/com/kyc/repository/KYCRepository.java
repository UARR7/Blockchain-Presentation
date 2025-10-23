package com.kyc.repository;

import com.kyc.model.KYCApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KYCRepository extends JpaRepository<KYCApplication, Long> {
    
    KYCApplication findByUserId(String userId);
    
    KYCApplication findByWalletAddress(String walletAddress);
    
    List<KYCApplication> findByStatus(KYCApplication.KYCStatus status);
    
    List<KYCApplication> findByEmail(String email);
    
    boolean existsByWalletAddress(String walletAddress);
}
