const KYCRegistry = artifacts.require("KYCRegistry");

module.exports = function (deployer) {
  deployer.deploy(KYCRegistry);
};


