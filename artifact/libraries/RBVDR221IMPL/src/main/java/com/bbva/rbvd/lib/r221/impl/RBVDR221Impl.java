package com.bbva.rbvd.lib.r221.impl;

import com.bbva.apx.exception.business.BusinessException;

import com.bbva.pisd.dto.insurance.aso.email.CreateEmailASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;

import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.pisd.dto.insurance.utils.PISDErrors;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;

import com.bbva.pisd.dto.insurance.utils.PISDValidation;
import com.bbva.rbvd.dto.insrncsale.dao.CreatedInsrcEventDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;
import com.bbva.rbvd.dto.insrncsale.events.CreatedInsuranceDTO;

import com.bbva.rbvd.dto.insrncsale.events.header.BankEventDTO;
import com.bbva.rbvd.dto.insrncsale.sigma.SigmaSetAlarmStatusDTO;

import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

public class RBVDR221Impl extends RBVDR221Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR221Impl.class);

	private static final String STATUS_ALARM = "CRITICAL";

	@Override
	public Boolean executeCreatedInsrcEvent(CreatedInsrcEventDTO createdInsrcEvent) {
		LOGGER.info("***** RBVDR221Impl - executeCreatedInsrcEvntBusinessLogic START *****");
		LOGGER.info("***** RBVDR221Impl - executeCreatedInsrcEvntBusinessLogic RequestBody {} *****", createdInsrcEvent);

		CreatedInsuranceDTO createdInsuranceDTO = createdInsrcEvent.getCreatedInsurance();

		Map<String, Object> argumentsForGetRequiredFields = new HashMap<>();
		argumentsForGetRequiredFields.put(RBVDProperties.FIELD_POLICY_QUOTA_INTERNAL_ID.getValue(), createdInsuranceDTO.getQuotationId());

		Map<String, Object> responseGetEmissionRequiredFields = this.pisdR012.
				executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentsForGetRequiredFields);

		RequiredFieldsEmissionDAO emissionDAO = buildEmissionRequiredFieldsDAO(responseGetEmissionRequiredFields);

		LOGGER.info("***** RBVDR221Impl - executeCreatedInsrcEvntBusinessLogic ***** Executing PISDR012 executeGetRequiredFieldsForCreatedInsrcEvnt method");
		Map<String, Object> responseGetCreatedInsrcEvntRequiredFields = this.pisdR012.
				executeGetRequiredFieldsForCreatedInsrcEvnt(createdInsuranceDTO.getQuotationId());

		CreatedInsrcEventDAO createdInsrcEventDAO = buildCreatedInsrcEvntRequiredFieldsDAO(responseGetCreatedInsrcEvntRequiredFields);

		CustomerBO customerInformation = this.httpClient.executeListCustomerService(createdInsuranceDTO.getHolder().getId());

		try {
			this.validateCustomerInformation(customerInformation);
		} catch (BusinessException ex) {
			this.addAdviceWithDescription(ex.getAdviceCode(), ex.getMessage());
			return false;
		}

		LOGGER.info("***** RBVDR221Impl - executeCreatedInsrcEvntBusinessLogic ***** Building CreateEmailASO object");

		try {
			CreateEmailASO emailRequest = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, emissionDAO, createdInsrcEventDAO, customerInformation);
			this.httpClient.executeMailSendService(emailRequest);
		} catch (BusinessException ex) {
			this.httpClient.executeSetAlarmStatus(this.createAlarmErrorRequest("createEmail"));
			this.addAdviceWithDescription(ex.getAdviceCode(), ex.getMessage());
			return false;
		}

		LOGGER.info("***** RBVDR221Impl - executeCreatedInsrcEvntBusinessLogic ***** Building GifoleInsuranceRequestASO object");

		try {
			BankEventDTO bank = createdInsrcEvent.getHeader().getOrigin().getBank();
			GifoleInsuranceRequestASO gifoleRequest = this.mapperHelper.createGifoleServiceRequest(createdInsuranceDTO, createdInsrcEventDAO,
					emissionDAO, customerInformation, bank);
			this.httpClient.executeGifoleService(gifoleRequest);
		} catch (BusinessException ex) {
			this.httpClient.executeSetAlarmStatus(this.createAlarmErrorRequest("createGifoleInsuranceRequest"));
			this.addAdviceWithDescription(ex.getAdviceCode(), ex.getMessage());
			return false;
		}

		LOGGER.info("***** RBVDR221Impl - executeCreatedInsrcEvntBusinessLogic END *****");
		return true;
	}

	private void validateCustomerInformation(CustomerBO customerInformation) {
		if(isNull(customerInformation)) {
			throw PISDValidation.build(PISDErrors.ERROR_CONNECTION_VALIDATE_CUSTOMER_SERVICE);
		}
	}

	private RequiredFieldsEmissionDAO buildEmissionRequiredFieldsDAO(Map<String, Object> responseGetRequiredFields) {
		RequiredFieldsEmissionDAO emissionDao = new RequiredFieldsEmissionDAO();
		emissionDao.setInsuranceProductId((BigDecimal) responseGetRequiredFields.get(RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue()));
		emissionDao.setContractDurationNumber((BigDecimal) responseGetRequiredFields.get(RBVDProperties.FIELD_CONTRACT_DURATION_NUMBER.getValue()));
		emissionDao.setContractDurationType((String) responseGetRequiredFields.get(RBVDProperties.FIELD_CONTRACT_DURATION_TYPE.getValue()));
		emissionDao.setPaymentFrequencyId((BigDecimal) responseGetRequiredFields.get(RBVDProperties.FIELD_PAYMENT_FREQUENCY_ID.getValue()));
		emissionDao.setInsuranceCompanyQuotaId((String) responseGetRequiredFields.get(RBVDProperties.FIELD_INSURANCE_COMPANY_QUOTA_ID.getValue()));
		emissionDao.setInsuranceProductDesc((String) responseGetRequiredFields.get(PISDProperties.FIELD_INSURANCE_PRODUCT_DESC.getValue()));
		emissionDao.setInsuranceModalityName((String) responseGetRequiredFields.get(PISDProperties.FIELD_INSURANCE_MODALITY_NAME.getValue()));
		emissionDao.setPaymentFrequencyName((String) responseGetRequiredFields.get(PISDProperties.FIELD_PAYMENT_FREQUENCY_NAME.getValue()));
		emissionDao.setVehicleBrandName((String) responseGetRequiredFields.get(PISDProperties.FIELD_VEHICLE_BRAND_NAME.getValue()));
		emissionDao.setVehicleModelName((String) responseGetRequiredFields.get(PISDProperties.FIELD_VEHICLE_MODEL_NAME.getValue()));
		emissionDao.setVehicleYearId((String) responseGetRequiredFields.get(PISDProperties.FIELD_VEHICLE_YEAR_ID.getValue()));
		emissionDao.setVehicleLicenseId((String) responseGetRequiredFields.get(PISDProperties.FIELD_VEHICLE_LICENSE_ID.getValue()));
		emissionDao.setGasConversionType((String) responseGetRequiredFields.get(PISDProperties.FIELD_VEHICLE_GAS_CONVERSION_TYPE.getValue()));
		emissionDao.setVehicleCirculationType((String) responseGetRequiredFields.get(PISDProperties.FIELD_VEHICLE_CIRCULATION_SCOPE_TYPE.getValue()));
		emissionDao.setCommercialVehicleAmount((BigDecimal) responseGetRequiredFields.get(PISDProperties.FIELD_COMMERCIAL_VEHICLE_AMOUNT.getValue()));

		return emissionDao;
	}

	private CreatedInsrcEventDAO buildCreatedInsrcEvntRequiredFieldsDAO(Map<String, Object> responseGetCreatedInsrcEvntRequiredFields) {
		CreatedInsrcEventDAO createdInsrcEventDAO = new CreatedInsrcEventDAO();
		createdInsrcEventDAO.setContractNumber((String) responseGetCreatedInsrcEvntRequiredFields.get("CONTRACT_NUMBER"));
		createdInsrcEventDAO.setRimacPolicy((String) responseGetCreatedInsrcEvntRequiredFields.get(PISDProperties.FIELD_POLICY_ID.getValue()));
		createdInsrcEventDAO.
				setPeriodName((String) responseGetCreatedInsrcEvntRequiredFields.get(PISDProperties.FIELD_PAYMENT_FREQUENCY_NAME.getValue()));
		createdInsrcEventDAO.
				setPaymentMethodId((String) responseGetCreatedInsrcEvntRequiredFields.get(RBVDProperties.FIELD_PAYMENT_METHOD_TYPE.getValue()));
		createdInsrcEventDAO.
				setInsuranceCompanyDesc((String) responseGetCreatedInsrcEvntRequiredFields.get(RBVDProperties.FIELD_INSURANCE_COMPANY_DESC.getValue()));
		return createdInsrcEventDAO;
	}

	private SigmaSetAlarmStatusDTO createAlarmErrorRequest(String serviceName) {
		SigmaSetAlarmStatusDTO sigmaSetAlarmStatusDTO = new SigmaSetAlarmStatusDTO();
		sigmaSetAlarmStatusDTO.setStatus(STATUS_ALARM);
		sigmaSetAlarmStatusDTO.setReason("Hubo un problema al consumir el servicio " + serviceName + ", revisar el log e identificar el problema.");
		return sigmaSetAlarmStatusDTO;
	}

}
