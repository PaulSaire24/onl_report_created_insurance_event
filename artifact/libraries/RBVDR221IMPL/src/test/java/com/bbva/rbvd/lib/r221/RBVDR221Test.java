package com.bbva.rbvd.lib.r221;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.pisd.lib.r012.PISDR012;

import com.bbva.rbvd.dto.insrncsale.commons.HolderDTO;
import com.bbva.rbvd.dto.insrncsale.events.CreatedInsuranceDTO;
import com.bbva.rbvd.lib.r221.impl.RBVDR221Impl;

import com.bbva.rbvd.lib.r221.impl.util.HttpClient;
import com.bbva.rbvd.lib.r221.impl.util.MapperHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import static java.util.Collections.singletonList;

import static org.mockito.Matchers.anyString;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

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

	private MapperHelper mapperHelper;
	private HttpClient httpClient;
	private PISDR012 pisdR012;

	@Before
	public void setUp() {
		ThreadContext.set(new Context());

		mapperHelper = mock(MapperHelper.class);
		rbvdr221.setMapperHelper(mapperHelper);

		httpClient = mock(HttpClient.class);
		rbvdr221.setHttpClient(httpClient);

		pisdR012 = mock(PISDR012.class);
		rbvdr221.setPisdR012(pisdR012);
	}

	@Test
	public void executeCreatedInsrcEventWithMailBusinessException() {
		LOGGER.info("Executing RBVDR221Test - executeCreatedInsrcEventWithMailBusinessException ...");

		when(this.httpClient.executeMailSendService(anyObject())).thenThrow(new BusinessException("advice", false, "message"));

		CreatedInsuranceDTO createdInsuranceDTO = new CreatedInsuranceDTO();
		createdInsuranceDTO.setHolder(new HolderDTO());

		Boolean validation = this.rbvdr221.executeCreatedInsrcEvent(createdInsuranceDTO);
		assertFalse(validation);
	}

	@Test
	public void executeCreatedInsrcEvntBusinessLogicWithGifoleBusinessException() {
		LOGGER.info("Executing RBVDR221Test - executeCreatedInsrcEvntBusinessLogicWithGifoleBusinessException ...");

		when(this.httpClient.executeGifoleService(anyObject())).thenThrow(new BusinessException("advice", false, "message"));

		CreatedInsuranceDTO createdInsuranceDTO = new CreatedInsuranceDTO();
		createdInsuranceDTO.setHolder(new HolderDTO());

		Boolean validation = this.rbvdr221.executeCreatedInsrcEvent(createdInsuranceDTO);
		assertFalse(validation);
	}

	@Test
	public void executeCreatedInsrcEvntBusinessLogic_OK() {
		LOGGER.info("Executing RBVDR221Test - executeCreatedInsrcEvntBusinessLogic_OK ...");

		CustomerListASO customerInformation = new CustomerListASO();
		customerInformation.setData(singletonList(new CustomerBO()));

		when(this.httpClient.executeListCustomerService(anyString())).thenReturn(customerInformation.getData().get(0));

		CreatedInsuranceDTO createdInsuranceDTO = new CreatedInsuranceDTO();
		createdInsuranceDTO.setHolder(new HolderDTO());

		Boolean validation = this.rbvdr221.executeCreatedInsrcEvent(createdInsuranceDTO);
		assertTrue(validation);

		when(this.httpClient.executeListCustomerService(anyString())).thenReturn(null);

		validation = rbvdr221.executeCreatedInsrcEvent(createdInsuranceDTO);
		assertTrue(validation);
	}
	
}
