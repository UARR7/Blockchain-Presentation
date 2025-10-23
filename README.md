# Blockchain-Presentation
Complete Project Includes:
Core Components:

Maven Configuration - Complete pom.xml with all dependencies
Project Structure - Full folder hierarchy
Main Application - Spring Boot entry point

Security Layer:

JWT Token Provider with JJWT 0.12.3
JWT Authentication Filter
Security Configuration with role-based access
User Details Service implementation

Entities & Database:

User entity with roles
KYC Record entity with blockchain references
Blockchain Transaction entity for audit trail
All JPA repositories with custom queries

Business Logic:

AuthService - Registration and login
KycService - KYC submission, verification, and retrieval
BlockchainService - Web3j integration for Ethereum interaction
UserDetailsService - Spring Security integration

REST APIs:

AuthController - /auth/register, /auth/login
KycController - Submit, verify, and retrieve KYC records
BlockchainController - Blockchain transaction management

Blockchain Integration:

Solidity Smart Contract - Complete KYC contract with verifier management
Web3j Configuration - Ethereum connection setup
Transaction recording and verification

Configuration Files:

application.yml - Main configuration
application-dev.yml - Development setup with H2
CORS configuration
Comprehensive exception handling

link:http://localhost:8080/swagger-ui/index.html#/KYC%20Management
for swagger documentation
