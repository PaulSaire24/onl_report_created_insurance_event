package com.bbva.rbvd.lib.r221.util;

import com.bbva.elara.utility.api.connector.APIConnector;

import com.bbva.pisd.dto.insurance.aso.email.CreateEmailASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;

import com.bbva.rbvd.lib.r221.factory.ApiConnectorFactoryMock;
import com.bbva.rbvd.lib.r221.impl.util.HttpClient;

import com.bbva.rbvd.mock.MockBundleContext;

import org.junit.Before;
import org.junit.Test;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.any;

public class HttpClientTest {

    private final HttpClient httpClient = new HttpClient();

    private APIConnector internalApiConnector;

    @Before
    public void setUp() {
        MockBundleContext mockBundleContext = mock(MockBundleContext.class);

        ApiConnectorFactoryMock apiConnectorFactoryMock = new ApiConnectorFactoryMock();
        internalApiConnector = apiConnectorFactoryMock.getAPIConnector(mockBundleContext);
        httpClient.setInternalApiConnector(internalApiConnector);
    }

    @Test
    public void executeGifoleServiceOK() {
        when(internalApiConnector.exchange(anyString(), any(HttpMethod.class), anyObject(), (Class<Void>)any())).
                thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        Integer validation = this.httpClient.executeGifoleService(new GifoleInsuranceRequestASO());

        Integer statusCreated = 201;

        assertEquals(statusCreated, validation);
    }

    @Test
    public void executeGifoleServiceWithRestClientException() {
        when(internalApiConnector.exchange(anyString(), any(HttpMethod.class), anyObject(), (Class<Void>)any())).
                thenThrow(new RestClientException("Something went wrong"));

        Integer validation = this.httpClient.executeGifoleService(new GifoleInsuranceRequestASO());

        assertNull(validation);
    }

    @Test
    public void executeMailSendServiceOK() {
        when(internalApiConnector.exchange(anyString(), any(HttpMethod.class), anyObject(), (Class<Void>) any())).
                thenReturn(new ResponseEntity<>(HttpStatus.OK));

        Integer validation = this.httpClient.executeMailSendService(new CreateEmailASO());

        Integer statusOk = 200;

        assertEquals(statusOk, validation);
    }

    @Test
    public void executeMailSendServiceWithRestClientException() {
        when(internalApiConnector.exchange(anyString(), any(HttpMethod.class), anyObject(), (Class<Void>) any())).
                thenThrow(new RestClientException("Something went wrong"));

        Integer validation = this.httpClient.executeMailSendService(new CreateEmailASO());

        assertNull(validation);
    }
}
