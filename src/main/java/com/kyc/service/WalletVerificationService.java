package com.kyc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletVerificationService {

    private static final long SIGNATURE_VALIDITY_SECONDS = 300; // 5 minutes

    /**
     * Verify that the signature was created by the owner of the wallet address
     * Process:
     * 1. User signs a message with their private key (done in frontend)
     * 2. Backend receives: message, signature, wallet address
     * 3. Backend recovers the address from signature
     * 4. Compare recovered address with claimed address
     */
    public boolean verifyWalletOwnership(String walletAddress, String signature, String message, Long timestamp) {
        try {
            // 1. Validate timestamp to prevent replay attacks
            if (!isSignatureTimestampValid(timestamp)) {
                log.warn("Signature timestamp expired or invalid");
                return false;
            }

            // 2. Prepare the message (Ethereum signed message format)
            String prefixedMessage = getEthereumSignedMessagePrefix(message);
            byte[] messageHash = org.web3j.crypto.Hash.sha3(prefixedMessage.getBytes(StandardCharsets.UTF_8));

            // 3. Extract signature components (v, r, s)
            byte[] signatureBytes = Numeric.hexStringToByteArray(signature);

            if (signatureBytes.length != 65) {
                log.error("Invalid signature length: {}", signatureBytes.length);
                return false;
            }

            byte v = signatureBytes[64];
            if (v < 27) {
                v += 27; // Normalize v value
            }

            byte[] r = new byte[32];
            byte[] s = new byte[32];
            System.arraycopy(signatureBytes, 0, r, 0, 32);
            System.arraycopy(signatureBytes, 32, s, 0, 32);

            // 4. Recover the public key from signature
            Sign.SignatureData signatureData = new Sign.SignatureData(
                    v,
                    r,
                    s
            );

            BigInteger publicKey = Sign.signedMessageHashToKey(messageHash, signatureData);

            // 5. Derive address from public key
            String recoveredAddress = "0x" + Keys.getAddress(publicKey);

            // 6. Compare addresses (case-insensitive)
            boolean isValid = recoveredAddress.equalsIgnoreCase(walletAddress);

            if (isValid) {
                log.info("Wallet ownership verified for address: {}", walletAddress);
            } else {
                log.warn("Wallet verification failed. Expected: {}, Got: {}", walletAddress, recoveredAddress);
            }

            return isValid;

        } catch (SignatureException e) {
            log.error("Signature verification failed", e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error during wallet verification", e);
            return false;
        }
    }

    /**
     * Generate the message to be signed (for frontend reference)
     * Message includes user info + timestamp to prevent replay attacks
     */
    public String generateVerificationMessage(String email, String fullName, Long timestamp) {
        return String.format(
                "I am verifying my wallet for KYC submission.\n\n" +
                        "Email: %s\n" +
                        "Name: %s\n" +
                        "Timestamp: %d\n\n" +
                        "This signature will expire in 5 minutes.",
                email, fullName, timestamp
        );
    }

    /**
     * Ethereum uses a specific prefix for signed messages
     */
    private String getEthereumSignedMessagePrefix(String message) {
        return "\u0019Ethereum Signed Message:\n" + message.length() + message;
    }

    /**
     * Check if signature timestamp is within valid range
     */
    private boolean isSignatureTimestampValid(Long timestamp) {
        if (timestamp == null) {
            return false;
        }

        long currentTime = Instant.now().getEpochSecond();
        long timeDifference = Math.abs(currentTime - timestamp);

        return timeDifference <= SIGNATURE_VALIDITY_SECONDS;
    }

    /**
     * Verify wallet ownership with generated message format
     */
    public boolean verifyWalletWithGeneratedMessage(
            String walletAddress,
            String signature,
            String email,
            String fullName,
            Long timestamp) {

        String expectedMessage = generateVerificationMessage(email, fullName, timestamp);
        return verifyWalletOwnership(walletAddress, signature, expectedMessage, timestamp);
    }
}
