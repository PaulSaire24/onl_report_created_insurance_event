package com.bbva.rbvd.lib.r221.impl.util;

import com.bbva.elara.utility.api.connector.APIConnector;

import com.bbva.pisd.dto.insurance.aso.email.CreateEmailASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;

import com.bbva.pisd.dto.insurance.utils.PISDProperties;

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

public class HttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    private final Gson gson;

    private APIConnector internalApiConnector;

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
            return null;
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
            return null;
        }

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

}