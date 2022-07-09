package com.bbva.rbvd.lib.r211;

import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;
import com.bbva.rbvd.lib.r211.impl.RBVDR211Impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/RBVDR211-app.xml",
		"classpath:/META-INF/spring/RBVDR211-app-test.xml",
		"classpath:/META-INF/spring/RBVDR211-arc.xml",
		"classpath:/META-INF/spring/RBVDR211-arc-test.xml" })
public class RBVDR211Test {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR211Test.class);

	private final RBVDR211Impl rbvdr211 = new RBVDR211Impl();

	@Before
	public void setUp() {
		ThreadContext.set(new Context());
	}
	
	@Test
	public void executeCreatedInsrcEvntBusinessLogic_OK(){
		LOGGER.info("Executing RBVDR211Test - executeCreatedInsrcEvntBusinessLogic_OK ...");
		Boolean validation = rbvdr211.executeCreatedInsrcEvntBusinessLogic(new CreatedInsrcEventDTO());
		assertTrue(validation);
	}
	
}
