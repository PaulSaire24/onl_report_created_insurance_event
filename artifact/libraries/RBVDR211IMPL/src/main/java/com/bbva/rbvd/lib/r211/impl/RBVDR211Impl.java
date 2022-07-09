package com.bbva.rbvd.lib.r211.impl;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RBVDR211Impl extends RBVDR211Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR211Impl.class);

	@Override
	public Boolean executeCreatedInsrcEvntBusinessLogic(CreatedInsrcEventDTO createdInsrcEventDTO) {
		LOGGER.info("***** RBVDR211Impl - executeCreatedInsrcEvntBusinessLogic START *****");

		LOGGER.info("***** RBVDR211Impl - executeCreatedInsrcEvntBusinessLogic END *****");

		return true;
	}

}
