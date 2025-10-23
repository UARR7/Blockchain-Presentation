package com.kyc.service;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
public class IPFSService {
    
    @Value("${blockchain.ipfs.host}")
    private String ipfsHost;
    
    @Value("${blockchain.ipfs.port}")
    private int ipfsPort;
    
    private IPFS ipfs;
    
    @PostConstruct
    public void init() {
        this.ipfs = new IPFS(ipfsHost, ipfsPort);
        log.info("IPFS service initialized: {}:{}", ipfsHost, ipfsPort);
    }
    
    public String uploadFile(MultipartFile file) throws IOException {
        try {
            NamedStreamable.ByteArrayWrapper fileWrapper = 
                new NamedStreamable.ByteArrayWrapper(
                        Objects.requireNonNull(file.getOriginalFilename()),
                    file.getBytes()
                );
            
            MerkleNode response = ipfs.add(fileWrapper).get(0);
            String hash = response.hash.toString();
            
            log.info("File uploaded to IPFS: {} with hash: {}", 
                file.getOriginalFilename(), hash);
            
            return hash;
        } catch (Exception e) {
            log.error("Error uploading file to IPFS: {}", e.getMessage());
            throw new IOException("Failed to upload file to IPFS", e);
        }
    }
    
    public byte[] retrieveFile(String hash) throws IOException {
        try {
            byte[] fileContents = ipfs.cat(io.ipfs.multihash.Multihash.fromBase58(hash));
            log.info("File retrieved from IPFS with hash: {}", hash);
            return fileContents;
        } catch (Exception e) {
            log.error("Error retrieving file from IPFS: {}", e.getMessage());
            throw new IOException("Failed to retrieve file from IPFS", e);
        }
    }
    
    public boolean isIPFSRunning() {
        try {
            ipfs.version();
            return true;
        } catch (Exception e) {
            log.error("IPFS is not running: {}", e.getMessage());
            return false;
        }
    }
}
