package com.bbva.rbvd.lib.r221;

import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;

import com.bbva.pisd.lib.r012.PISDR012;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;

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

import static org.junit.Assert.assertTrue;

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
	public void executeCreatedInsrcEvntBusinessLogic_OK(){
		LOGGER.info("Executing RBVDR221Test - executeCreatedInsrcEvntBusinessLogic_OK ...");

		when(httpClient.executeGifoleService(anyObject())).thenReturn(201);

		Boolean validation = rbvdr221.executeCreatedInsrcEvent(new CreatedInsrcEventDTO());
		assertTrue(validation);
	}
	
}
