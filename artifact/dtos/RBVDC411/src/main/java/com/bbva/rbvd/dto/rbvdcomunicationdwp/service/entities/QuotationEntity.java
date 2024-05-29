package com.bbva.rbvd.dto.rbvdcomunicationdwp.service.entities;

import java.util.Date;

public class QuotationEntity {
    private String creationUserId;
    private Date creationDate;
    private String userAuditId;
    private Date auditDate;
    private String policyQuotaInternalId;
    private String insuranceSimulationId;
    private String insuranceCompanyQuotaId;
    private String quoteDate;
    private String quotaHmsDate;
    private String policyQuotaEndValidityDate;
    private String customerId;
    private String policyQuotaStatusType;
    private String lastChangeBranchId;
    private String sourceBranchId;
    private String personalDocType;
    private String participantPersonalId;
    private Date policyQuotaCancellationDate;
    private String insuredCustomerName;
    private String clientLasName;
    private String issuedReceiptNumber;
    private String lastFourPanId;
    private String rfqInternalId;
    private String payrollId;

    public String getCreationUserId() {
        return creationUserId;
    }

    public void setCreationUserId(String creationUserId) {
        this.creationUserId = creationUserId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getUserAuditId() {
        return userAuditId;
    }

    public void setUserAuditId(String userAuditId) {
        this.userAuditId = userAuditId;
    }

    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public String getPolicyQuotaInternalId() {
        return policyQuotaInternalId;
    }

    public void setPolicyQuotaInternalId(String policyQuotaInternalId) {
        this.policyQuotaInternalId = policyQuotaInternalId;
    }

    public String getInsuranceSimulationId() {
        return insuranceSimulationId;
    }

    public void setInsuranceSimulationId(String insuranceSimulationId) {
        this.insuranceSimulationId = insuranceSimulationId;
    }

    public String getInsuranceCompanyQuotaId() {
        return insuranceCompanyQuotaId;
    }

    public void setInsuranceCompanyQuotaId(String insuranceCompanyQuotaId) {
        this.insuranceCompanyQuotaId = insuranceCompanyQuotaId;
    }

    public String getQuoteDate() {
        return quoteDate;
    }

    public void setQuoteDate(String quoteDate) {
        this.quoteDate = quoteDate;
    }

    public String getQuotaHmsDate() {
        return quotaHmsDate;
    }

    public void setQuotaHmsDate(String quotaHmsDate) {
        this.quotaHmsDate = quotaHmsDate;
    }

    public String getPolicyQuotaEndValidityDate() {
        return policyQuotaEndValidityDate;
    }

    public void setPolicyQuotaEndValidityDate(String policyQuotaEndValidityDate) {
        this.policyQuotaEndValidityDate = policyQuotaEndValidityDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPolicyQuotaStatusType() {
        return policyQuotaStatusType;
    }

    public void setPolicyQuotaStatusType(String policyQuotaStatusType) {
        this.policyQuotaStatusType = policyQuotaStatusType;
    }

    public String getLastChangeBranchId() {
        return lastChangeBranchId;
    }

    public void setLastChangeBranchId(String lastChangeBranchId) {
        this.lastChangeBranchId = lastChangeBranchId;
    }

    public String getSourceBranchId() {
        return sourceBranchId;
    }

    public void setSourceBranchId(String sourceBranchId) {
        this.sourceBranchId = sourceBranchId;
    }

    public String getPersonalDocType() {
        return personalDocType;
    }

    public void setPersonalDocType(String personalDocType) {
        this.personalDocType = personalDocType;
    }

    public String getParticipantPersonalId() {
        return participantPersonalId;
    }

    public void setParticipantPersonalId(String participantPersonalId) {
        this.participantPersonalId = participantPersonalId;
    }

    public Date getPolicyQuotaCancellationDate() {
        return policyQuotaCancellationDate;
    }

    public void setPolicyQuotaCancellationDate(Date policyQuotaCancellationDate) {
        this.policyQuotaCancellationDate = policyQuotaCancellationDate;
    }

    public String getInsuredCustomerName() {
        return insuredCustomerName;
    }

    public void setInsuredCustomerName(String insuredCustomerName) {
        this.insuredCustomerName = insuredCustomerName;
    }

    public String getClientLasName() {
        return clientLasName;
    }

    public void setClientLasName(String clientLasName) {
        this.clientLasName = clientLasName;
    }

    public String getIssuedReceiptNumber() {
        return issuedReceiptNumber;
    }

    public void setIssuedReceiptNumber(String issuedReceiptNumber) {
        this.issuedReceiptNumber = issuedReceiptNumber;
    }

    public String getLastFourPanId() {
        return lastFourPanId;
    }

    public void setLastFourPanId(String lastFourPanId) {
        this.lastFourPanId = lastFourPanId;
    }

    public String getRfqInternalId() {
        return rfqInternalId;
    }

    public void setRfqInternalId(String rfqInternalId) {
        this.rfqInternalId = rfqInternalId;
    }

    public String getPayrollId() {
        return payrollId;
    }

    public void setPayrollId(String payrollId) {
        this.payrollId = payrollId;
    }

    @Override
    public String toString() {
        return "QuotationEntity{" +
                "creationUserId='" + creationUserId + '\'' +
                ", creationDate=" + creationDate +
                ", userAuditId='" + userAuditId + '\'' +
                ", auditDate=" + auditDate +
                ", policyQuotaInternalId='" + policyQuotaInternalId + '\'' +
                ", insuranceSimulationId='" + insuranceSimulationId + '\'' +
                ", insuranceCompanyQuotaId='" + insuranceCompanyQuotaId + '\'' +
                ", quoteDate='" + quoteDate + '\'' +
                ", quotaHmsDate='" + quotaHmsDate + '\'' +
                ", policyQuotaEndValidityDate='" + policyQuotaEndValidityDate + '\'' +
                ", customerId='" + customerId + '\'' +
                ", policyQuotaStatusType='" + policyQuotaStatusType + '\'' +
                ", lastChangeBranchId='" + lastChangeBranchId + '\'' +
                ", sourceBranchId='" + sourceBranchId + '\'' +
                ", personalDocType='" + personalDocType + '\'' +
                ", participantPersonalId='" + participantPersonalId + '\'' +
                ", policyQuotaCancellationDate=" + policyQuotaCancellationDate +
                ", insuredCustomerName='" + insuredCustomerName + '\'' +
                ", clientLasName='" + clientLasName + '\'' +
                ", issuedReceiptNumber='" + issuedReceiptNumber + '\'' +
                ", lastFourPanId='" + lastFourPanId + '\'' +
                ", rfqInternalId='" + rfqInternalId + '\'' +
                ", payrollId='" + payrollId + '\'' +
                '}';
    }
}
