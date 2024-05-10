package com.bbva.rbvd.dto.rbvdcomunicationdwp.service.saleforce;

public class SalesForceBO {
    private String idQuotation;
    private String idCustomer;
    private String idProduct;
    private String employeeCode;
    private String source;
    private StatusBO status;

    private String message;

    public String getIdQuotation() {
        return idQuotation;
    }

    public void setIdQuotation(String idQuotation) {
        this.idQuotation = idQuotation;
    }

    public String getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(String idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public StatusBO getStatus() {
        return status;
    }

    public void setStatus(StatusBO status) {
        this.status = status;
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
                "idQuotation='" + idQuotation + '\'' +
                ", idCustomer='" + idCustomer + '\'' +
                ", idProduct='" + idProduct + '\'' +
                ", employeeCode='" + employeeCode + '\'' +
                ", source='" + source + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
