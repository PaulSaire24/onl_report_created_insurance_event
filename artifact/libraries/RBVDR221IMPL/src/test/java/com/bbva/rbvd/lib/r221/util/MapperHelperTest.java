package com.bbva.rbvd.lib.r221.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.email.CreateEmailASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;

import com.bbva.pisd.lib.r021.PISDR021;
import com.bbva.rbvd.dto.homeinsrc.utils.HomeInsuranceProperty;
import com.bbva.rbvd.dto.insrncsale.dao.CreatedInsrcEventDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;
import com.bbva.rbvd.dto.insrncsale.events.CreatedInsuranceDTO;
import com.bbva.rbvd.dto.insrncsale.events.InstallmentPlansCreatedInsrcEvent;

import com.bbva.rbvd.dto.insrncsale.mock.MockData;

import com.bbva.rbvd.lib.r221.impl.util.MapperHelper;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import static java.math.BigDecimal.valueOf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;

public class MapperHelperTest {

    private final MapperHelper mapperHelper = new MapperHelper();

    private final MockData mockData = MockData.getInstance();

    private CreatedInsrcEventDAO createdInsrcEventDAO;
    private RequiredFieldsEmissionDAO requiredFieldsEmissionDAO;
    private ApplicationConfigurationService applicationConfigurationService;

    private CreatedInsrcEventDTO createdInsrcEventDTO;
    private CreatedInsuranceDTO createdInsuranceDTO;

    private PISDR021 pisdR021;
    private Map<String, Object> responseQueryGetHomeInfo;
    private Map<String, Object> responseQueryGetHomeRiskDirection;

    @Before
    public void setUp() throws IOException {
        createdInsrcEventDAO = mock(CreatedInsrcEventDAO.class);
        requiredFieldsEmissionDAO = mock(RequiredFieldsEmissionDAO.class);

        applicationConfigurationService = mock(ApplicationConfigurationService.class);
        mapperHelper.setApplicationConfigurationService(applicationConfigurationService);

        when(createdInsrcEventDAO.getContractNumber()).thenReturn("0814000015111");
        when(createdInsrcEventDAO.getRimacPolicy()).thenReturn("510772");
        when(createdInsrcEventDAO.getPaymentMethodId()).thenReturn("C");
        when(createdInsrcEventDAO.getPeriodName()).thenReturn("MENSUAL");
        when(createdInsrcEventDAO.getInsuranceCompanyDesc()).thenReturn("RIMAC");

        when(requiredFieldsEmissionDAO.getInsuranceModalityName()).thenReturn("PLAN BASICO");
        when(requiredFieldsEmissionDAO.getPaymentFrequencyName()).thenReturn("Mensual");

        createdInsrcEventDTO = mockData.getCreatedInsrcEventRequest();

        createdInsuranceDTO = createdInsrcEventDTO.getCreatedInsurance();

        pisdR021 = mock(PISDR021.class);
        mapperHelper.setPisdR021(pisdR021);

        responseQueryGetHomeInfo = mock(Map.class);
        responseQueryGetHomeRiskDirection = mock(Map.class);
    }

    @Test
    public void createGifoleServiceRequestWithCardAndAccountPaymentMethod() {

        when(requiredFieldsEmissionDAO.getInsuranceProductDesc()).thenReturn("VEHICULAR OPTATIVO");
        when(requiredFieldsEmissionDAO.getInsuranceModalityName()).thenReturn("PLAN BASICO");

        when(applicationConfigurationService.getProperty(anyString())).thenReturn("INSURANCE_CREATION");

        GifoleInsuranceRequestASO validation = this.mapperHelper.createGifoleServiceRequest(createdInsuranceDTO, createdInsrcEventDAO, requiredFieldsEmissionDAO,
                                            "name", "lastName");

        assertNotNull(validation);

        assertNotNull(validation.getQuotation());

        assertNotNull(validation.getQuotation().getId());
        assertEquals(createdInsuranceDTO.getQuotationId(), validation.getQuotation().getId());

        assertNotNull(validation.getOperationDate());
        assertNotNull(validation.getValidityPeriod());
        assertNotNull(validation.getValidityPeriod().getStartDate());
        assertNotNull(validation.getValidityPeriod().getEndDate());
        assertNotNull(validation.getHolder());
        assertNotNull(validation.getHolder().getFirstName());
        assertNotNull(validation.getHolder().getLastName());
        assertNotNull(validation.getHolder().getIdentityDocument());
        assertNotNull(validation.getHolder().getIdentityDocument().getDocumentType());
        assertNotNull(validation.getHolder().getIdentityDocument().getDocumentType().getId());
        assertNotNull(validation.getHolder().getIdentityDocument().getDocumentNumber());

        /* CASO PARA TIPO DE PAGO CON CUENTA BANCARIA */
        assertTrue(validation.getHolder().getHasBankAccount());
        assertFalse(validation.getHolder().getHasCreditCard());

        assertFalse(validation.getHolder().getContactDetails().isEmpty());
        assertEquals(2, validation.getHolder().getContactDetails().size());

        assertNotNull(validation.getInsurance());

        assertNotNull(validation.getInsurance().getId());
        assertEquals(createdInsrcEventDAO.getContractNumber(), validation.getInsurance().getId());

        assertNotNull(validation.getInsurance().getPaymentMethod());
        assertEquals("CUENTA", validation.getInsurance().getPaymentMethod().getId());
        assertEquals(1, validation.getInsurance().getPaymentMethod().getRelatedContracts().size());
        assertNotNull(validation.getInsurance().getPaymentMethod().getRelatedContracts().get(0).getNumber());
        assertEquals("****0516", validation.getInsurance().getPaymentMethod().getRelatedContracts().get(0).getNumber());

        assertNotNull(validation.getPolicyNumber());
        assertEquals(createdInsrcEventDAO.getRimacPolicy(), validation.getPolicyNumber());

        assertNotNull(validation.getProduct());

        assertEquals(createdInsuranceDTO.getProduct().getId(), validation.getProduct().getId());
        assertEquals(requiredFieldsEmissionDAO.getInsuranceProductDesc(), validation.getProduct().getName());

        assertNotNull(validation.getProduct().getPlan());
        assertEquals(createdInsuranceDTO.getProduct().getPlan().getId(), validation.getProduct().getPlan().getId());
        assertEquals(requiredFieldsEmissionDAO.getInsuranceModalityName(), validation.getProduct().getPlan().getName());

        assertNotNull(validation.getInstallmentPlan());
        assertNotNull(validation.getInstallmentPlan().getPeriod());

        InstallmentPlansCreatedInsrcEvent installmentPlanEvnt = createdInsuranceDTO.getProduct().getPlan().getInstallmentPlans().get(0);

        assertEquals(installmentPlanEvnt.getPeriod().getId(), validation.getInstallmentPlan().getPeriod().getId());
        assertEquals(createdInsrcEventDAO.getPeriodName(), validation.getInstallmentPlan().getPeriod().getName());

        assertNotNull(validation.getInstallmentPlan().getPremiumAmount());
        assertEquals(valueOf(installmentPlanEvnt.getPaymentAmount().getAmount()), validation.getInstallmentPlan().getPremiumAmount().getAmount());
        assertEquals(installmentPlanEvnt.getPaymentAmount().getCurrency(), validation.getInstallmentPlan().getPremiumAmount().getCurrency());

        Long totalInstallmentsNumber = installmentPlanEvnt.getPaymentsTotalNumber().longValue();

        assertEquals(totalInstallmentsNumber, validation.getInstallmentPlan().getTotalInstallmentsNumber());

        assertNotNull(validation.getTotalPremiumAmount());
        assertEquals(valueOf(createdInsuranceDTO.getProduct().getPlan().getTotalInstallment().getAmount()),
                validation.getTotalPremiumAmount().getAmount());
        assertEquals(createdInsuranceDTO.getProduct().getPlan().getTotalInstallment().getCurrency(),
                validation.getTotalPremiumAmount().getCurrency());

        assertEquals("INSURANCE_CREATION", validation.getOperationType());

        /* CASO PARA TIPO DE PAGO CON TARJETA */
        when(createdInsrcEventDAO.getPaymentMethodId()).thenReturn("T");
        createdInsuranceDTO.getHolder().getContactDetails().clear();

        validation = this.mapperHelper.createGifoleServiceRequest(createdInsuranceDTO, createdInsrcEventDAO, requiredFieldsEmissionDAO,
                    "name", "lastName");

        assertTrue(validation.getHolder().getHasCreditCard());
        assertFalse(validation.getHolder().getHasBankAccount());

        assertEquals("TARJETA", validation.getInsurance().getPaymentMethod().getId());
        assertEquals("Sin nro", validation.getHolder().getContactDetails().get(0).getContact().getPhoneNumber());
        assertEquals("Sin email", validation.getHolder().getContactDetails().get(1).getContact().getAddress());

    }

    @Test
    public void createEmailServiceRequestVehicle() {

        createdInsuranceDTO.getProduct().setId("830");

        when(requiredFieldsEmissionDAO.getVehicleLicenseId()).thenReturn("license");
        when(requiredFieldsEmissionDAO.getVehicleBrandName()).thenReturn("brand");
        when(requiredFieldsEmissionDAO.getVehicleModelName()).thenReturn("model");
        when(requiredFieldsEmissionDAO.getVehicleYearId()).thenReturn("vehicleYear");
        when(requiredFieldsEmissionDAO.getGasConversionType()).thenReturn("S");
        when(requiredFieldsEmissionDAO.getVehicleCirculationType()).thenReturn("L");
        when(requiredFieldsEmissionDAO.getCommercialVehicleAmount()).thenReturn(BigDecimal.valueOf(1000));

        CreateEmailASO validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, "");

        assertNotNull(validation);
        assertNotNull(validation.getApplicationId());
        assertNotNull(validation.getRecipient());
        assertNotNull(validation.getSubject());
        assertNotNull(validation.getSubject());
        assertNotNull(validation.getBody());
        assertNotNull(validation.getSender());

        assertEquals("0,ronald.dolores@bbva.com", validation.getRecipient());
        assertEquals("!Genial! Acabas de comprar tu seguro vehicular con éxito", validation.getSubject());
        assertEquals("procesos@bbva.com.pe", validation.getSender());

        when(requiredFieldsEmissionDAO.getVehicleLicenseId()).thenReturn(null);
        when(createdInsrcEventDAO.getRimacPolicy()).thenReturn(null);
        when(requiredFieldsEmissionDAO.getGasConversionType()).thenReturn("N");
        when(requiredFieldsEmissionDAO.getVehicleCirculationType()).thenReturn("P");

        validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, "");

        assertNotNull(validation);

    }

    @Test
    public void createEmailServiceRequestHome() {
        when(responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_DEPARTMENT_NAME.getValue())).thenReturn("department_name");
        when(responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_PROVINCE_NAME.getValue())).thenReturn("province_name");
        when(responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_DISTRICT_NAME.getValue())).thenReturn("district_name");
        when(responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_HOUSING_TYPE.getValue())).thenReturn("housing_type");
        when(responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_AREA_PROPERTY_1_NUMBER.getValue())).thenReturn(BigDecimal.valueOf(200));
        when(responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_PROP_SENIORITY_YEARS_NUMBER.getValue())).thenReturn(BigDecimal.valueOf(16));
        when(responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_FLOOR_NUMBER.getValue())).thenReturn(BigDecimal.valueOf(3));
        when(responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_EDIFICATION_LOAN_AMOUNT.getValue())).thenReturn(BigDecimal.valueOf(50000));
        when(responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_HOUSING_ASSETS_LOAN_AMOUNT.getValue())).thenReturn(BigDecimal.valueOf(15000));

        when(pisdR021.executeGetHomeInfoForEmissionService(anyString())).thenReturn(responseQueryGetHomeInfo);

        when(responseQueryGetHomeRiskDirection.get(HomeInsuranceProperty.FIELD_LEGAL_ADDRESS_DESC.getValue())).thenReturn("riskDirection");

        when(pisdR021.executeGetHomeRiskDirection(anyString())).thenReturn(responseQueryGetHomeRiskDirection);

        CreateEmailASO validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, "customerName");

        assertNotNull(validation);
        assertNotNull(validation.getApplicationId());
        assertNotNull(validation.getRecipient());
        assertNotNull(validation.getSubject());
        assertNotNull(validation.getBody());
        assertNotNull(validation.getSender());

        assertEquals("0,ronald.dolores@bbva.com", validation.getRecipient());
        assertEquals("!Genial! Acabas de comprar tu Seguro Hogar Total con éxito", validation.getSubject());
        assertEquals("procesos@bbva.com.pe", validation.getSender());

        createdInsuranceDTO.getProduct().getPlan().setId("04");

        when(responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_HOUSING_TYPE.getValue())).thenReturn("P");
        when(responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_EDIFICATION_LOAN_AMOUNT.getValue())).thenReturn(null);
        when(responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_HOUSING_ASSETS_LOAN_AMOUNT.getValue())).thenReturn(null);
        when(createdInsrcEventDAO.getRimacPolicy()).thenReturn(null);

        validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, "customerName");

        assertNotNull(validation);

        createdInsuranceDTO.getProduct().getPlan().setId("05");

        validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, "customerName");

        assertNotNull(validation);
    }

    @Test
    public void createGeneralEmailServiceRequest() {

        createdInsuranceDTO.getProduct().setId("834");

        CreateEmailASO validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, "customerName");

        assertNotNull(validation);
        assertNotNull(validation.getApplicationId());
        assertNotNull(validation.getRecipient());
        assertNotNull(validation.getSubject());
        assertNotNull(validation.getSubject());
        assertNotNull(validation.getBody());
        assertNotNull(validation.getSender());

        assertEquals("0,ronald.dolores@bbva.com", validation.getRecipient());
        assertEquals("Genial Tu solicitud de Seguro de Proteccion de Tarjetas fue ingresada con exito", validation.getSubject());
        assertEquals("procesos@bbva.com.pe", validation.getSender());

        when(createdInsrcEventDAO.getRimacPolicy()).thenReturn(null);

        validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, "customerName");
    }
}
