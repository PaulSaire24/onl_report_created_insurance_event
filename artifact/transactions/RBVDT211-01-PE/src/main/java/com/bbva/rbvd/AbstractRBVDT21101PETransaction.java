package com.bbva.rbvd;

import com.bbva.elara.transaction.AbstractTransaction;
import com.bbva.rbvd.dto.insrncsale.events.CreatedInsuranceDTO;
import com.bbva.rbvd.dto.insrncsale.events.header.HeaderDTO;

/**
 * In this class, the input and output data is defined automatically through the setters and getters.
 */
public abstract class AbstractRBVDT21101PETransaction extends AbstractTransaction {

	public AbstractRBVDT21101PETransaction(){
	}

	/**
	 * Return value for input parameter header
	 */
	protected HeaderDTO getHeader(){
		return (HeaderDTO)this.getParameter("header");
	}

	/**
	 * Return value for input parameter createdInsurance
	 */
	protected CreatedInsuranceDTO getCreatedinsurance(){
		return (CreatedInsuranceDTO)this.getParameter("createdInsurance");
	}
}
