package com.bbva.rbvd.lib.r221.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.dto.insurance.aso.email.CreateEmailASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;

import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;

import com.bbva.rbvd.dto.insrncsale.dao.CreatedInsrcEventDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsuranceDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import java.util.Map;

import static com.google.common.base.Strings.nullToEmpty;
import static java.util.Objects.nonNull;

public class RBVDR221Impl extends RBVDR221Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR221Impl.class);

	@Override
	public void executeCreatedInsrcEvent(CreatedInsuranceDTO createdInsuranceDTO) {
		LOGGER.info("***** RBVDR221Impl - executeCreatedInsrcEvntBusinessLogic START *****");

		LOGGER.info("***** RBVDR221Impl - executeCreatedInsrcEvntBusinessLogic ***** Executing PISDR012 executeGetRequiredFieldsForEmissionService method");
		Map<String, Object> responseGetEmissionRequiredFields = this.pisdR012.executeGetRequiredFieldsForEmissionService(createdInsuranceDTO.getQuotationId());

		RequiredFieldsEmissionDAO emissionDAO = buildEmissionRequiredFieldsDAO(responseGetEmissionRequiredFields);

		LOGGER.info("***** RBVDR221Impl - executeCreatedInsrcEvntBusinessLogic ***** Executing PISDR012 executeGetRequiredFieldsForCreatedInsrcEvnt method");
		Map<String, Object> responseGetCreatedInsrcEvntRequiredFields = this.pisdR012.
				executeGetRequiredFieldsForCreatedInsrcEvnt(createdInsuranceDTO.getQuotationId());

		CreatedInsrcEventDAO createdInsrcEventDAO = buildCreatedInsrcEvntRequiredFieldsDAO(responseGetCreatedInsrcEvntRequiredFields);

		CustomerBO customerInformation = this.httpClient.executeListCustomerService(createdInsuranceDTO.getHolder().getId());

		String name = "";

		String lastName = "";

		String fullName = "N/A";

		if(nonNull(customerInformation)) {
			name = nullToEmpty(customerInformation.getFirstName());
			lastName = nullToEmpty(customerInformation.getLastName()) + " " + nullToEmpty(customerInformation.getSecondLastName());
			fullName = name + " " + lastName;
			fullName = fullName.replace("#", "Ñ");
		}

		LOGGER.info("***** RBVDR221Impl - executeCreatedInsrcEvntBusinessLogic ***** Building CreateEmailASO object");
		CreateEmailASO emailRequest = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, emissionDAO, createdInsrcEventDAO, fullName);

		try {
			this.httpClient.executeMailSendService(emailRequest);
		} catch (BusinessException ex) {
			this.addAdviceWithDescription(ex.getAdviceCode(), ex.getMessage());
			return;
		}

		LOGGER.info("***** RBVDR221Impl - executeCreatedInsrcEvntBusinessLogic ***** Building GifoleInsuranceRequestASO object");
		GifoleInsuranceRequestASO gifoleRequest = this.mapperHelper.createGifoleServiceRequest(createdInsuranceDTO, createdInsrcEventDAO,
														emissionDAO, name, lastName);

		try {
			this.httpClient.executeGifoleService(gifoleRequest);
		} catch (BusinessException ex) {
			this.addAdviceWithDescription(ex.getAdviceCode(), ex.getMessage());
			return;
		}

		LOGGER.info("***** RBVDR221Impl - executeCreatedInsrcEvntBusinessLogic END *****");
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
		return createdInsrcEventDAO;
	}

}
