package com.bbva.rbvd;

import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
import com.bbva.rbvd.lib.r211.RBVDR211;
import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RBVDT21101PETransaction extends AbstractRBVDT21101PETransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDT21101PETransaction.class);

	@Override
	public void execute() {

		LOGGER.info("***** RBVDT21101PETransaction - START *****");

		RBVDR211 rbvdR211 = this.getServiceLibrary(RBVDR211.class);

		CreatedInsrcEventDTO createdInsrcEventDTO = new CreatedInsrcEventDTO();
		createdInsrcEventDTO.setQuotationId(this.getQuotationid());
		createdInsrcEventDTO.setOperationDate(this.getOperationdate().getTime());
		createdInsrcEventDTO.setValidityPeriod(this.getValidityperiod());
		createdInsrcEventDTO.setHolder(this.getHolder());
		createdInsrcEventDTO.setProduct(this.getProduct());

		String aap = (String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.AAP);

		createdInsrcEventDTO.setAap(aap);

		Boolean successed = rbvdR211.executeCreatedInsrcEvent(createdInsrcEventDTO);

		if(successed) {
			LOGGER.info("***** CREATED INSURANCE EVENT WAS SUCCESSED *****");
		} else {
			LOGGER.info("***** CREATED INSURANCE EVENT WASN'T SUCCESSED *****");
		}

	}

}
