package com.kyc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import jakarta.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class BlockchainService {
    
    @Value("${blockchain.ethereum.rpc-url}")
    private String rpcUrl;
    
    @Value("${blockchain.ethereum.contract-address}")
    private String contractAddress;
    
    @Value("${blockchain.ethereum.admin-private-key}")
    private String privateKey;
    
    @Value("${blockchain.ethereum.chain-id}")
    private long chainId;
    
    private Web3j web3j;
    private Credentials credentials;
    private TransactionManager txManager;
    
    @PostConstruct
    public void init() {
        this.web3j = Web3j.build(new HttpService(rpcUrl));
        if (privateKey != null && !privateKey.isEmpty()) {
            this.credentials = Credentials.create(privateKey);
            this.txManager = new RawTransactionManager(web3j, credentials, chainId);
        }
        log.info("Blockchain service initialized with RPC: {}", rpcUrl);
    }
    
    public String submitKYC(String userAddress, String documentHash) throws Exception {
        Function function = new Function(
            "submitKYC",
            Arrays.asList(new Utf8String(documentHash)),
            Arrays.asList(new TypeReference<Bool>() {})
        );
        
        String encodedFunction = FunctionEncoder.encode(function);
        
        EthSendTransaction transactionResponse = txManager.sendTransaction(
            DefaultGasProvider.GAS_PRICE,
            DefaultGasProvider.GAS_LIMIT,
            contractAddress,
            encodedFunction,
            BigInteger.ZERO
        );
        
        if (transactionResponse.hasError()) {
            throw new Exception("Transaction failed: " + transactionResponse.getError().getMessage());
        }
        
        return transactionResponse.getTransactionHash();
    }
    
    public String verifyKYC(String userAddress, int status, int validityDays, String remarks) throws Exception {
        Function function = new Function(
            "verifyKYC",
            Arrays.asList(
                new Address(userAddress),
                new Uint8(status),
                new Uint256(validityDays),
                new Utf8String(remarks)
            ),
            Arrays.asList(new TypeReference<Bool>() {})
        );
        
        String encodedFunction = FunctionEncoder.encode(function);
        
        EthSendTransaction transactionResponse = txManager.sendTransaction(
            DefaultGasProvider.GAS_PRICE,
            DefaultGasProvider.GAS_LIMIT,
            contractAddress,
            encodedFunction,
            BigInteger.ZERO
        );
        
        if (transactionResponse.hasError()) {
            throw new Exception("Transaction failed: " + transactionResponse.getError().getMessage());
        }
        
        return transactionResponse.getTransactionHash();
    }
    
    public KYCRecordResponse getKYCRecord(String userAddress) throws Exception {
        Function function = new Function(
            "getKYCRecord",
            Arrays.asList(new Address(userAddress)),
            Arrays.asList(
                new TypeReference<Utf8String>() {},
                new TypeReference<Uint8>() {},
                new TypeReference<Uint256>() {},
                new TypeReference<Uint256>() {},
                new TypeReference<Address>() {},
                new TypeReference<Uint256>() {},
                new TypeReference<Utf8String>() {}
            )
        );
        
        String encodedFunction = FunctionEncoder.encode(function);
        
        EthCall response = web3j.ethCall(
            Transaction.createEthCallTransaction(
                credentials.getAddress(),
                contractAddress,
                encodedFunction
            ),
            DefaultBlockParameterName.LATEST
        ).send();
        
        List<Type> result = FunctionReturnDecoder.decode(
            response.getValue(),
            function.getOutputParameters()
        );
        
        KYCRecordResponse record = new KYCRecordResponse();
        record.setDocumentHash(((Utf8String) result.get(0)).getValue());
        record.setStatus(((Uint8) result.get(1)).getValue().intValue());
        record.setSubmittedAt(((Uint256) result.get(2)).getValue().longValue());
        record.setVerifiedAt(((Uint256) result.get(3)).getValue().longValue());
        record.setVerifiedBy(((Address) result.get(4)).getValue());
        record.setExpiryDate(((Uint256) result.get(5)).getValue().longValue());
        record.setRemarks(((Utf8String) result.get(6)).getValue());
        
        return record;
    }
    
    @lombok.Data
    public static class KYCRecordResponse {
        private String documentHash;
        private int status;
        private long submittedAt;
        private long verifiedAt;
        private String verifiedBy;
        private long expiryDate;
        private String remarks;
    }
}
