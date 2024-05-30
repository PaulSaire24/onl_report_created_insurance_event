package com.bbva.rbvd.lib.r221.transform.bean;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsuranceDTO;
import com.bbva.rbvd.dto.rbvdcomunicationdwp.service.saleforce.ChannelBO;
import com.bbva.rbvd.dto.rbvdcomunicationdwp.service.saleforce.SalesForceBO;
import com.bbva.rbvd.dto.rbvdcomunicationdwp.service.saleforce.StatusBO;
import com.bbva.rbvd.dto.rbvdcomunicationdwp.service.saleforce.UserBO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class UpdateDwpRequest {

    public static SalesForceBO mapRequestToSalesForceDwpBean(CreatedInsuranceDTO requestBody, String status){
        SalesForceBO salesForceBO = new SalesForceBO();
        salesForceBO.setCustomerId(requestBody.getHolder().getId());
        salesForceBO.setQuotationId(requestBody.getQuotationId());
        salesForceBO.setProductId("DWPPY00065");
        salesForceBO.setContractId(requestBody.getContractId());
        salesForceBO.setSourcePayroll("FU");

        UserBO user = new UserBO();
        user.setUser("XP12321");
        LocalDate fechaActual = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String fechaFormateada = fechaActual.format(formatter);
        user.setDate(LocalDate.parse(fechaFormateada));
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
}
