package com.bbva.rbvd.dto.rbvdcomunicationdwp.service.saleforce;

public class SalesForceBO {
    private String quotationId;
    private String customerId;
    private String productId;
    private StatusBO status;
    private String sourcePayroll;
    private UserBO auditUser;
    private ChannelBO channel;
    private String contractId;

    private String message;

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

    public UserBO getAuditUser() {
        return auditUser;
    }

    public void setAuditUser(UserBO auditUser) {
        this.auditUser = auditUser;
    }

    public ChannelBO getChannel() {
        return channel;
    }

    public void setChannel(ChannelBO channel) {
        this.channel = channel;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
                ", message='" + message + '\'' +
                '}';
    }
}
