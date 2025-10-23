// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

contract KYCRegistry {
    
    enum KYCStatus { PENDING, APPROVED, REJECTED, EXPIRED }
    
    struct KYCRecord {
        address userAddress;
        string documentHash;
        KYCStatus status;
        uint256 submittedAt;
        uint256 verifiedAt;
        address verifiedBy;
        uint256 expiryDate;
        string remarks;
        bool exists;
    }
    
    mapping(address => KYCRecord) private kycRecords;
    mapping(address => bool) public verifiers;
    address public admin;
    
    event KYCSubmitted(address indexed user, string documentHash, uint256 timestamp);
    event KYCVerified(address indexed user, KYCStatus status, address indexed verifier, uint256 timestamp);
    event KYCUpdated(address indexed user, string documentHash, uint256 timestamp);
    event VerifierAdded(address indexed verifier, uint256 timestamp);
    event VerifierRemoved(address indexed verifier, uint256 timestamp);
    
    modifier onlyAdmin() {
        require(msg.sender == admin, "Only admin can perform this action");
        _;
    }
    
    modifier onlyVerifier() {
        require(verifiers[msg.sender] || msg.sender == admin, "Only verifiers can perform this action");
        _;
    }
    
    constructor() {
        admin = msg.sender;
        verifiers[msg.sender] = true;
    }
    
    function submitKYC(string memory _documentHash) public {
        require(bytes(_documentHash).length > 0, "Document hash cannot be empty");
        
        KYCRecord storage record = kycRecords[msg.sender];
        
        if (record.exists) {
            record.documentHash = _documentHash;
            record.status = KYCStatus.PENDING;
            record.submittedAt = block.timestamp;
            emit KYCUpdated(msg.sender, _documentHash, block.timestamp);
        } else {
            kycRecords[msg.sender] = KYCRecord({
                userAddress: msg.sender,
                documentHash: _documentHash,
                status: KYCStatus.PENDING,
                submittedAt: block.timestamp,
                verifiedAt: 0,
                verifiedBy: address(0),
                expiryDate: 0,
                remarks: "",
                exists: true
            });
            emit KYCSubmitted(msg.sender, _documentHash, block.timestamp);
        }
    }
    
    function verifyKYC(
        address _user, 
        KYCStatus _status, 
        uint256 _validityDays,
        string memory _remarks
    ) public onlyVerifier {
        require(_status == KYCStatus.APPROVED || _status == KYCStatus.REJECTED, "Invalid status");
        require(kycRecords[_user].exists, "KYC record does not exist");
        
        KYCRecord storage record = kycRecords[_user];
        record.status = _status;
        record.verifiedAt = block.timestamp;
        record.verifiedBy = msg.sender;
        record.remarks = _remarks;
        
        if (_status == KYCStatus.APPROVED && _validityDays > 0) {
            record.expiryDate = block.timestamp + (_validityDays * 1 days);
        }
        
        emit KYCVerified(_user, _status, msg.sender, block.timestamp);
    }
    
    function getKYCStatus(address _user) public view returns (KYCStatus) {
        require(kycRecords[_user].exists, "KYC record does not exist");
        
        KYCRecord memory record = kycRecords[_user];
        
        if (record.status == KYCStatus.APPROVED && 
            record.expiryDate > 0 && 
            block.timestamp > record.expiryDate) {
            return KYCStatus.EXPIRED;
        }
        
        return record.status;
    }
    
    function getKYCRecord(address _user) public view returns (
        string memory documentHash,
        KYCStatus status,
        uint256 submittedAt,
        uint256 verifiedAt,
        address verifiedBy,
        uint256 expiryDate,
        string memory remarks
    ) {
        require(
            msg.sender == _user || verifiers[msg.sender] || msg.sender == admin,
            "Not authorized to view this record"
        );
        require(kycRecords[_user].exists, "KYC record does not exist");
        
        KYCRecord memory record = kycRecords[_user];
        
        KYCStatus currentStatus = record.status;
        if (currentStatus == KYCStatus.APPROVED && 
            record.expiryDate > 0 && 
            block.timestamp > record.expiryDate) {
            currentStatus = KYCStatus.EXPIRED;
        }
        
        return (
            record.documentHash,
            currentStatus,
            record.submittedAt,
            record.verifiedAt,
            record.verifiedBy,
            record.expiryDate,
            record.remarks
        );
    }
    
    function isKYCVerified(address _user) public view returns (bool) {
        if (!kycRecords[_user].exists) {
            return false;
        }
        
        KYCStatus status = getKYCStatus(_user);
        return status == KYCStatus.APPROVED;
    }
    
    function addVerifier(address _verifier) public onlyAdmin {
        require(_verifier != address(0), "Invalid address");
        verifiers[_verifier] = true;
        emit VerifierAdded(_verifier, block.timestamp);
    }
    
    function removeVerifier(address _verifier) public onlyAdmin {
        require(_verifier != admin, "Cannot remove admin");
        verifiers[_verifier] = false;
        emit VerifierRemoved(_verifier, block.timestamp);
    }
    
    function transferAdmin(address _newAdmin) public onlyAdmin {
        require(_newAdmin != address(0), "Invalid address");
        admin = _newAdmin;
        verifiers[_newAdmin] = true;
    }
}
