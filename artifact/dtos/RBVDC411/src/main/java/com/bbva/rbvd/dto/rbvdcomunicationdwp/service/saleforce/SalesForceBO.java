package com.bbva.rbvd.dto.rbvdcomunicationdwp.service.saleforce;

public class SalesForceBO {
    private String quotationId;
    private String customerId;
    private String productId;
    private StatusBO status;
    private String sourcePayroll;
    private User auditUser;
    private Channel channel;
    private String contractId;

    public String getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(String quotationId) {
        this.quotationId = quotationId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public StatusBO getStatus() {
        return status;
    }

    public void setStatus(StatusBO status) {
        this.status = status;
    }

    public String getSourcePayroll() {
        return sourcePayroll;
    }

    public void setSourcePayroll(String sourcePayroll) {
        this.sourcePayroll = sourcePayroll;
    }

    public User getAuditUser() {
        return auditUser;
    }

    public void setAuditUser(User auditUser) {
        this.auditUser = auditUser;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    @Override
    public String toString() {
        return "SalesForceBO{" +
                "quotationId='" + quotationId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", productId='" + productId + '\'' +
                ", status=" + status +
                ", sourcePayroll='" + sourcePayroll + '\'' +
                ", auditUser=" + auditUser +
                ", channel=" + channel +
                ", contractId='" + contractId + '\'' +
                '}';
    }
}
