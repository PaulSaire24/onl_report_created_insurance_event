package com.bbva.rbvd.lib.r221.transform.bean;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsuranceDTO;
import com.bbva.rbvd.dto.rbvdcomunicationdwp.service.entities.QuotationEntity;
import com.bbva.rbvd.dto.rbvdcomunicationdwp.service.saleforce.ChannelBO;
import com.bbva.rbvd.dto.rbvdcomunicationdwp.service.saleforce.SalesForceBO;
import com.bbva.rbvd.dto.rbvdcomunicationdwp.service.saleforce.StatusBO;
import com.bbva.rbvd.dto.rbvdcomunicationdwp.service.saleforce.UserBO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;


public class MapperBean {

    public static SalesForceBO mapRequestToSalesForceDwpBean(CreatedInsuranceDTO requestBody,String status,String userCode){
        SalesForceBO salesForceBO = new SalesForceBO();
        salesForceBO.setCustomerId(requestBody.getHolder().getId());
        salesForceBO.setQuotationId(requestBody.getQuotationId());
        salesForceBO.setProductId("DWPPY00065");
        salesForceBO.setContractId(requestBody.getContractId());
        salesForceBO.setSourcePayroll("FU");

        UserBO user = new UserBO();
        user.setUser(userCode);
        LocalDate fechaActual = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String fechaFormateada = fechaActual.format(formatter);
        user.setDate(fechaFormateada);
        salesForceBO.setAuditUser(user);

        ChannelBO channel = new ChannelBO();
        channel.setId("FU");
        salesForceBO.setChannel(channel);

        StatusBO statusBO = new StatusBO();
        statusBO.setId(status);
        statusBO.setName(status);
        salesForceBO.setStatus(statusBO);
        return salesForceBO;
    }

    public static QuotationEntity mapRequestToQuotationEntity(Map<String,Object> objectMap){
        QuotationEntity quotationEntity = new QuotationEntity();

        quotationEntity.setRfqInternalId((String) objectMap.get("RFQ_INTERNAL_ID"));
        quotationEntity.setPayrollId((String) objectMap.get("PAYROLL_ID"));
        quotationEntity.setPolicyQuotaInternalId((String) objectMap.get("POLICY_QUOTA_INTERNAL_ID"));
        quotationEntity.setCreationDate((Date) objectMap.get("CREATION_DATE"));
        quotationEntity.setAuditDate((Date) objectMap.get("AUDIT_DATE"));
        return quotationEntity;
    }
}
