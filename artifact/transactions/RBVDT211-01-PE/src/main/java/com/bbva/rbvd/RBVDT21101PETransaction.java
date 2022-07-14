package com.bbva.rbvd;

import com.bbva.rbvd.lib.r221.RBVDR221;

import com.bbva.elara.domain.transaction.RequestHeaderParamsName;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RBVDT21101PETransaction extends AbstractRBVDT21101PETransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDT21101PETransaction.class);

	@Override
	public void execute() {

		RBVDR221 rbvdR221 = this.getServiceLibrary(RBVDR221.class);

		LOGGER.info("***** RBVDT21101PETransaction - START *****");

		CreatedInsrcEventDTO createdInsrcEventDTO = new CreatedInsrcEventDTO();
		createdInsrcEventDTO.setQuotationId(this.getQuotationid());
		createdInsrcEventDTO.setOperationDate(this.getOperationdate().getTime());
		createdInsrcEventDTO.setValidityPeriod(this.getValidityperiod());
		createdInsrcEventDTO.setHolder(this.getHolder());
		createdInsrcEventDTO.setProduct(this.getProduct());

		String aap = (String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.AAP);

		createdInsrcEventDTO.setAap(aap);

		Boolean successed = rbvdR221.executeCreatedInsrcEvent(createdInsrcEventDTO);

		if(successed) {
			LOGGER.info("***** CREATED INSURANCE EVENT WAS SUCCESSED *****");
		} else {
			LOGGER.info("***** CREATED INSURANCE EVENT WASN'T SUCCESSED *****");
		}

	}

}
