package com.bbva.rbvd.lib.r221.impl.util;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.utility.api.connector.APIConnector;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.email.CreateEmailASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;

import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;

import com.bbva.pisd.dto.insurance.utils.PISDProperties;

import com.bbva.rbvd.dto.insrncsale.sigma.SigmaSetAlarmStatusDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    private final Gson gson;

    private APIConnector internalApiConnector;

    private APIConnector internalApiConnectorImpersonation;

    public HttpClient() {
        gson = new GsonBuilder().create();
    }

    public Integer executeGifoleService(GifoleInsuranceRequestASO gifoleInsuranceRequest) {
        LOGGER.info("***** HttpClient - executeGifoleService START *****");

        String json = this.gson.toJson(gifoleInsuranceRequest);

        LOGGER.info("***** HttpClient - executeGifoleService ***** Request body: {}", json);

        HttpEntity<String> entity = new HttpEntity<>(json, createHttpHeaders());

        try {
            ResponseEntity<Void> response = this.internalApiConnector.exchange(PISDProperties.ID_API_GIFOLE_ROYAL_INSURANCE_REQUEST_SERVICE.getValue(),
                    HttpMethod.POST, entity, Void.class);
            Integer httpStatus = response.getStatusCode().value();
            LOGGER.info("***** HttpClient - executeGifoleService ***** Http status code: {}", httpStatus);
            LOGGER.info("***** HttpClient - executeGifoleService END *****");
            return httpStatus;
        } catch (RestClientException ex) {
            LOGGER.debug("***** HttpClient - executeGifoleService ***** Something went wrong: {} !!!", ex.getMessage());
            throw new BusinessException("RBVD01020013", true, "CONSUMO DEL SERVICIO DE GIFOLE SIN EXITO");
        }

    }

    public Integer executeMailSendService(CreateEmailASO emailRequest) {
        LOGGER.info("***** HttpClient - executeMailSendService START *****");

        String json = this.gson.toJson(emailRequest);

        LOGGER.info("***** HttpClient - executeMailSendService ***** Request body: {}", json);

        HttpEntity<String> entity = new HttpEntity<>(json, createHttpHeaders());

        try {
            ResponseEntity<Void> response = this.internalApiConnector.exchange(PISDProperties.ID_API_NOTIFICATIONS_GATEWAY_CREATE_EMAIL_SERVICE.getValue(),
                    HttpMethod.POST, entity, Void.class);
            Integer httpStatus = response.getStatusCode().value();
            LOGGER.info("***** HttpClient - executeMailSendService ***** Http status code: {}", httpStatus);
            LOGGER.info("***** HttpClient - executeMailSendService END *****");
            return httpStatus;
        } catch (RestClientException ex) {
            LOGGER.debug("***** HttpClient - executeMailSendService ***** Something went wrong: {} !!!", ex.getMessage());
            throw new BusinessException("RBVD01020014", true, "CONSUMO DEL SERVICIO DE ENVIO DE CORREO SIN EXITO");
        }

    }

    public CustomerBO executeListCustomerService(String customerId) {
        LOGGER.info("***** HttpClient - executeListCustomerService START *****");

        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("customerId", customerId);

        try {
            CustomerListASO customerInformationASO = this.internalApiConnector.getForObject(PISDProperties.ID_API_CUSTOMER_INFORMATION.getValue(),
                    CustomerListASO.class, pathParams);
            CustomerBO customerInformation = customerInformationASO.getData().get(0);
            LOGGER.info("***** HttpClient - executeListCustomerService END *****");
            return customerInformation;
        } catch (RestClientException ex) {
            LOGGER.info("***** HttpClient - executeListCustomerService ***** Something went wrong: {} !!!", ex.getMessage());
            return null;
        }
    }

    public void executeSetAlarmStatus(SigmaSetAlarmStatusDTO alarmStatus) {
        LOGGER.info("***** HttpClient - executing Upsilon setAlarmStatus service START *****");

        String json = this.gson.toJson(alarmStatus);

        HttpEntity<String> entity = new HttpEntity(json, createHttpHeaders());

        try {
            ResponseEntity<Void> response = this.internalApiConnectorImpersonation.exchange("upsilonSetAlarmStatus", HttpMethod.POST,
                    entity, Void.class);
            LOGGER.info("***** HttpClient - Upsilon setAlarmStatus http response code: {}", response.getStatusCode().value());
        } catch (RestClientException ex) {
            LOGGER.info("***** HttpClient - executeMailSendService ***** Something went wrong: {} !!!", ex.getMessage());
        }

        LOGGER.info("***** HttpClient - executing Upsilon setAlarmStatus service END *****");
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = new MediaType("application","json", StandardCharsets.UTF_8);
        headers.setContentType(mediaType);
        return headers;
    }

    public void setInternalApiConnector(APIConnector internalApiConnector) {
        this.internalApiConnector = internalApiConnector;
    }

    public void setInternalApiConnectorImpersonation(APIConnector internalApiConnectorImpersonation) {
        this.internalApiConnectorImpersonation = internalApiConnectorImpersonation;
    }

}