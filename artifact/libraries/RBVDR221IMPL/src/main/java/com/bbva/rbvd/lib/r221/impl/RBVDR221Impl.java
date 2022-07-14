package com.bbva.rbvd.lib.r221.impl;

import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;

import com.bbva.pisd.dto.insurance.utils.PISDProperties;

import com.bbva.rbvd.dto.insrncsale.dao.CreatedInsrcEventDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;

import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import java.util.Map;

public class RBVDR221Impl extends RBVDR221Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR221Impl.class);

	@Override
	public Boolean executeCreatedInsrcEvent(CreatedInsrcEventDTO createdInsrcEventDTO) {
		LOGGER.info("***** RBVDR221Impl - executeCreatedInsrcEvntBusinessLogic START *****");

		Map<String, Object> responseGetEmissionRequiredFields = this.pisdR012.executeGetRequiredFieldsForEmissionService(createdInsrcEventDTO.getQuotationId());

		RequiredFieldsEmissionDAO emissionDAO = buildEmissionRequiredFieldsDAO(responseGetEmissionRequiredFields);

		Map<String, Object> responseGetCreatedInsrcEvntRequiredFields = this.pisdR012.
				executeGetRequiredFieldsForCreatedInsrcEvnt(createdInsrcEventDTO.getQuotationId());

		CreatedInsrcEventDAO createdInsrcEventDAO = buildCreatedInsrcEvntRequiredFieldsDAO(responseGetCreatedInsrcEvntRequiredFields);

		GifoleInsuranceRequestASO gifoleRequest = this.mapperHelper.createGifoleServiceRequest(createdInsrcEventDTO, createdInsrcEventDAO, emissionDAO);

		Integer gifoleHttpStatus = this.httpClient.executeGifoleService(gifoleRequest);

		LOGGER.info("***** RBVDR221Impl - executeCreatedInsrcEvntBusinessLogic END *****");

		return true;
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
