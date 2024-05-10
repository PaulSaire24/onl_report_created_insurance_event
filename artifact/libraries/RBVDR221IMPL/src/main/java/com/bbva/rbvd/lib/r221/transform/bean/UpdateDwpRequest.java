package com.bbva.rbvd.lib.r221.transform.bean;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsuranceDTO;
import com.bbva.rbvd.dto.rbvdcomunicationdwp.service.saleforce.SalesForceBO;
import com.bbva.rbvd.dto.rbvdcomunicationdwp.service.saleforce.StatusBO;


public class UpdateDwpRequest {

    public static SalesForceBO mapRequestToSalesForceDwpBean(CreatedInsuranceDTO requestBody){
        SalesForceBO salesForceBO = new SalesForceBO();
        salesForceBO.setIdQuotation(requestBody.getQuotationId());
        salesForceBO.setIdProduct(requestBody.getProduct().getId());

        StatusBO statusBO = new StatusBO();
        statusBO.setId(requestBody.getStatus().getId());
        statusBO.setName(requestBody.getStatus().getName());
        salesForceBO.setStatus(statusBO);

        return salesForceBO;
    }
}
