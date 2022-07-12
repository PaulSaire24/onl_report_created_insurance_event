package com.bbva.rbvd.lib.r211.impl.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;

import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.elara.utility.api.connector.APIConnectorBuilder;

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

    private ApplicationConfigurationService applicationConfigurationService;

    private APIConnector internalApiConnector;

    private APIConnectorBuilder apiConnectorBuilder;

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

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = new MediaType("application","json", StandardCharsets.UTF_8);
        headers.setContentType(mediaType);
        return headers;
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    public void setInternalApiConnector(APIConnector internalApiConnector) {
        this.internalApiConnector = internalApiConnector;
    }

    public void setApiConnectorBuilder(APIConnectorBuilder apiConnectorBuilder) {
        this.apiConnectorBuilder = apiConnectorBuilder;
    }

}