package com.bbva.rbvd.lib.r221;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;

import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.pdwy.dto.auth.salesforce.SalesforceResponseDTO;
import com.bbva.pdwy.lib.r008.PDWYR008;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.pisd.dto.insurancedao.entities.QuotationEntity;
import com.bbva.pisd.lib.r012.PISDR012;

import com.bbva.pisd.lib.r601.PISDR601;
import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;
import com.bbva.rbvd.dto.insrncsale.events.StatusDTO;
import com.bbva.rbvd.dto.insrncsale.mock.MockData;
import com.bbva.rbvd.dto.rbvdcomunicationdwp.service.saleforce.SalesForceBO;
import com.bbva.rbvd.lib.r221.impl.RBVDR221Impl;

import com.bbva.rbvd.lib.r221.impl.util.HttpClient;
import com.bbva.rbvd.lib.r221.impl.util.MapperHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/RBVDR221-app.xml",
		"classpath:/META-INF/spring/RBVDR221-app-test.xml",
		"classpath:/META-INF/spring/RBVDR221-arc.xml",
		"classpath:/META-INF/spring/RBVDR221-arc-test.xml" })
public class RBVDR221Test {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR221Test.class);

	private final RBVDR221Impl rbvdr221 = new RBVDR221Impl();

	private CreatedInsrcEventDTO createdInsrcEvent;
	private MapperHelper mapperHelper;
	private HttpClient httpClient;
	private PISDR012 pisdR012;
	private PDWYR008 pdwyr008;
	private PISDR601 pisdr601;
	private APIConnector externalApiConnector;

	@Before
	public void setUp() throws IOException {
		ThreadContext.set(new Context());

		createdInsrcEvent = MockData.getInstance().getCreatedInsrcEventRequest();

		mapperHelper = mock(MapperHelper.class);
		rbvdr221.setMapperHelper(mapperHelper);

		httpClient = mock(HttpClient.class);
		rbvdr221.setHttpClient(httpClient);

		pisdR012 = mock(PISDR012.class);
		pdwyr008 = mock(PDWYR008.class);
		pisdr601 = mock(PISDR601.class);
		externalApiConnector = mock(APIConnector.class);
		rbvdr221.setPisdR012(pisdR012);
		rbvdr221.setPdwyR008(pdwyr008);
		rbvdr221.setPisdR601(pisdr601);
		rbvdr221.setExternalApiConnector(externalApiConnector);

		when(this.httpClient.executeListCustomerService(anyString())).thenReturn(new CustomerBO());
	}

	@Test
	public void executeCreatedInsrcEventWithCustomerInfoBusinessException() {
		LOGGER.info("Executing RBVDR221Test - executeCreatedInsrcEventWithCustomerInfoBusinessException ...");

		when(this.httpClient.executeListCustomerService(anyString())).thenReturn(null);
		String user = "ZG13001";
		Boolean validation = this.rbvdr221.executeCreatedInsrcEvent(createdInsrcEvent,user);

		assertFalse(validation);
		assertEquals("PISD00120033", this.rbvdr221.getAdviceList().get(0).getCode());
		assertEquals("No se pudo realizar la conexion con el servicio Listar Customer.", this.rbvdr221.getAdviceList().get(0).getDescription());
	}

	@Test
	public void executeCreatedInsrcEventWithMailBusinessException() {
		LOGGER.info("Executing RBVDR221Test - executeCreatedInsrcEventWithMailBusinessException ...");

		when(this.httpClient.executeMailSendService(anyObject())).
				thenThrow(new BusinessException("RBVD01020014", false, "CONSUMO DEL SERVICIO DE ENVIO DE CORREO SIN EXITO"));
		String user = "ZG13001";
		Boolean validation = this.rbvdr221.executeCreatedInsrcEvent(createdInsrcEvent,user);

		assertFalse(validation);
		assertEquals("RBVD01020014", this.rbvdr221.getAdviceList().get(0).getCode());
		assertEquals("CONSUMO DEL SERVICIO DE ENVIO DE CORREO SIN EXITO", this.rbvdr221.getAdviceList().get(0).getDescription());
	}

	@Test
	public void executeCreatedInsrcEvntBusinessLogicWithGifoleBusinessException() {
		LOGGER.info("Executing RBVDR221Test - executeCreatedInsrcEvntBusinessLogicWithGifoleBusinessException ...");
		SalesforceResponseDTO salesforceResponseDTO = new SalesforceResponseDTO();
		salesforceResponseDTO.setAccessToken("accessToken");
		salesforceResponseDTO.setTokenType("Bearer");
		when(pdwyr008.executeGetAuthenticationData(Mockito.anyString())).thenReturn(salesforceResponseDTO);
		when(externalApiConnector.postForEntity(anyString(), anyObject(), (Class<SalesForceBO>) any())).thenReturn(new ResponseEntity<>(new SalesForceBO(), HttpStatus.OK));

		when(this.httpClient.executeGifoleService(anyObject())).
				thenThrow(new BusinessException("RBVD01020013", false, "CONSUMO DEL SERVICIO DE GIFOLE SIN EXITO"));

		createdInsrcEvent.getCreatedInsurance().setStatus(new StatusDTO());
		String user = "ZG13001";
		Boolean validation = this.rbvdr221.executeCreatedInsrcEvent(createdInsrcEvent,user);

		assertFalse(validation);
		assertEquals("RBVD01020013", this.rbvdr221.getAdviceList().get(0).getCode());
		assertEquals("CONSUMO DEL SERVICIO DE GIFOLE SIN EXITO", this.rbvdr221.getAdviceList().get(0).getDescription());
	}

	@Test
	public void executeCreatedInsrcEvntBusinessLogic_OK() {
		LOGGER.info("Executing RBVDR221Test - executeCreatedInsrcEvntBusinessLogic_OK ...");
		SalesforceResponseDTO salesforceResponseDTO = new SalesforceResponseDTO();
		salesforceResponseDTO.setAccessToken("accessToken");
		salesforceResponseDTO.setTokenType("Bearer");
		SalesForceBO salesForceBO = new SalesForceBO();
		createdInsrcEvent.getCreatedInsurance().setStatus(new StatusDTO());
		createdInsrcEvent.getCreatedInsurance().getStatus().setId("Contratada");
		createdInsrcEvent.getCreatedInsurance().getStatus().setName("Contratada name");
		createdInsrcEvent.getCreatedInsurance().setContractId("CONID");
		createdInsrcEvent.getCreatedInsurance().getProduct().setId("842");
		QuotationEntity quotationEntity = new QuotationEntity();
		quotationEntity.setRfqInternalId("RFQID");
		when(pdwyr008.executeGetAuthenticationData(Mockito.anyString())).thenReturn(salesforceResponseDTO);
		when(pisdr601.executeFindQuotationByReferenceAndPayrollId(Mockito.anyString())).thenReturn(quotationEntity);
		when(externalApiConnector.postForEntity(anyString(), anyObject(), (Class<SalesForceBO>) any())).thenReturn(new ResponseEntity<>(salesForceBO, HttpStatus.OK));
		String user = "ZG13001";
		Boolean validation = this.rbvdr221.executeCreatedInsrcEvent(createdInsrcEvent,user);
		assertTrue(validation);
	}
	@Test
	public void executeCreatedInsrcEvntBusinessLogic_NullExternalApiConnector() {
		LOGGER.info("Executing RBVDR221Test - executeCreatedInsrcEvntBusinessLogic_OK ...");
		SalesforceResponseDTO salesforceResponseDTO = new SalesforceResponseDTO();
		salesforceResponseDTO.setAccessToken("accessToken");
		salesforceResponseDTO.setTokenType("Bearer");
		SalesForceBO salesForceBO = new SalesForceBO();
		createdInsrcEvent.getCreatedInsurance().setStatus(new StatusDTO());
		createdInsrcEvent.getCreatedInsurance().getStatus().setId("Contratada");
		createdInsrcEvent.getCreatedInsurance().getStatus().setName("Contratada name");
		createdInsrcEvent.getCreatedInsurance().setContractId("CONID");
		createdInsrcEvent.getCreatedInsurance().getProduct().setId("842");
		QuotationEntity quotationEntity = new QuotationEntity();
		quotationEntity.setRfqInternalId("RFQID");
		when(pdwyr008.executeGetAuthenticationData(Mockito.anyString())).thenReturn(salesforceResponseDTO);
		when(pisdr601.executeFindQuotationByReferenceAndPayrollId(Mockito.anyString())).thenReturn(quotationEntity);
		when(externalApiConnector.postForEntity(anyString(), anyObject(), (Class<SalesForceBO>) any())).thenReturn(new ResponseEntity<>(null, HttpStatus.NO_CONTENT));
		String user = "ZG13001";
		Boolean validation = this.rbvdr221.executeCreatedInsrcEvent(createdInsrcEvent,user);
		assertFalse(validation);
	}
}
