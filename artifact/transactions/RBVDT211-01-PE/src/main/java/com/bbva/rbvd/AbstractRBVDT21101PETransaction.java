package com.bbva.rbvd;

import com.bbva.elara.transaction.AbstractTransaction;
import com.bbva.rbvd.dto.insrncsale.commons.HolderDTO;
import com.bbva.rbvd.dto.insrncsale.commons.ValidityPeriodDTO;
import com.bbva.rbvd.dto.insrncsale.events.ProductCreatedInsrcEventDTO;
import java.util.Calendar;

/**
 * In this class, the input and output data is defined automatically through the setters and getters.
 */
public abstract class AbstractRBVDT21101PETransaction extends AbstractTransaction {

	public AbstractRBVDT21101PETransaction(){
	}


	/**
	 * Return value for input parameter quotationId
	 */
	protected String getQuotationid(){
		return (String)this.getParameter("quotationId");
	}

	/**
	 * Return value for input parameter operationDate
	 */
	protected Calendar getOperationdate(){
		return (Calendar)this.getParameter("operationDate");
	}

	/**
	 * Return value for input parameter validityPeriod
	 */
	protected ValidityPeriodDTO getValidityperiod(){
		return (ValidityPeriodDTO)this.getParameter("validityPeriod");
	}

	/**
	 * Return value for input parameter holder
	 */
	protected HolderDTO getHolder(){
		return (HolderDTO)this.getParameter("holder");
	}

	/**
	 * Return value for input parameter product
	 */
	protected ProductCreatedInsrcEventDTO getProduct(){
		return (ProductCreatedInsrcEventDTO)this.getParameter("product");
	}
}
