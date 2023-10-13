package com.bbva.rbvd.lib.r221.util;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.email.CreateEmailASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;

import com.bbva.pisd.dto.insurance.bo.ContactDetailsBO;
import com.bbva.pisd.dto.insurance.bo.ContactTypeBO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.pisd.lib.r021.PISDR021;
import com.bbva.rbvd.dto.homeinsrc.utils.HomeInsuranceProperty;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.BusinessASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;
import com.bbva.rbvd.dto.insrncsale.dao.CreatedInsrcEventDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;
import com.bbva.rbvd.dto.insrncsale.events.CreatedInsuranceDTO;
import com.bbva.rbvd.dto.insrncsale.events.InstallmentPlansCreatedInsrcEvent;

import com.bbva.rbvd.dto.insrncsale.events.header.BankEventDTO;
import com.bbva.rbvd.dto.insrncsale.events.header.BranchEventDTO;
import com.bbva.rbvd.dto.insrncsale.mock.MockData;

import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.lib.r221.impl.util.HttpClient;
import com.bbva.rbvd.lib.r221.impl.util.MapperHelper;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.math.BigDecimal.valueOf;

import static java.util.Collections.singletonList;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.anyMap;

public class MapperHelperTest {

    private final MapperHelper mapperHelper = new MapperHelper();

    private final MockData mockData = MockData.getInstance();

    private CreatedInsrcEventDAO createdInsrcEventDAO;
    private RequiredFieldsEmissionDAO requiredFieldsEmissionDAO;
    private ApplicationConfigurationService applicationConfigurationService;

    private CreatedInsuranceDTO createdInsuranceDTO;

    private HttpClient httpClient;
    private PISDR012 pisdR012;
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
        when(createdInsrcEventDAO.getInsuranceProductDesc()).thenReturn("VEHICULAR OPTATIVO");
        when(createdInsrcEventDAO.getInsuranceModalityName()).thenReturn("PLAN BASICO");
        when(createdInsrcEventDAO.getInsrncCompanySimulationId()).thenReturn("4afc460b-5158-45ff-a08c-3047b1756031");
        when(applicationConfigurationService.getDefaultProperty("gifole.registro.simulacion.code","")).thenReturn("830,833,834");

        when(requiredFieldsEmissionDAO.getInsuranceModalityName()).thenReturn("PLAN BASICO");
        when(requiredFieldsEmissionDAO.getPaymentFrequencyName()).thenReturn("Mensual");

        CreatedInsrcEventDTO createdInsrcEventDTO = mockData.getCreatedInsrcEventRequest();

        createdInsuranceDTO = createdInsrcEventDTO.getCreatedInsurance();

        httpClient = mock(HttpClient.class);
        mapperHelper.setHttpClient(httpClient);

        pisdR012 = mock(PISDR012.class);
        mapperHelper.setPisdR012(pisdR012);

        pisdR021 = mock(PISDR021.class);
        mapperHelper.setPisdR021(pisdR021);

        responseQueryGetHomeInfo = mock(Map.class);
        responseQueryGetHomeRiskDirection = mock(Map.class);
    }

    @Test
    public void createGifoleServiceRequestWithCardAndAccountPaymentMethod() {
        createdInsuranceDTO.getProduct().setId("830");
        when(applicationConfigurationService.getProperty(anyString())).thenReturn("INSURANCE_CREATION");

        CustomerBO customer = new CustomerBO();
        customer.setFirstName("Pepe");
        customer.setLastName("Fulano");
        customer.setSecondLastName("Sultano");

        BankEventDTO bank = new BankEventDTO();
        bank.setBankId("bankId");
        BranchEventDTO branch = new BranchEventDTO();
        branch.setBranchId("branchId");
        bank.setBranch(branch);

        GifoleInsuranceRequestASO validation = this.mapperHelper.createGifoleServiceRequest(createdInsuranceDTO, createdInsrcEventDAO, customer, bank);

        assertNotNull(validation);

        assertNotNull(validation.getQuotation());

        assertNotNull(validation.getQuotation().getId());
        assertEquals(createdInsuranceDTO.getQuotationId(), validation.getQuotation().getId());

        assertNotNull(validation.getOperationDate());
        assertNotNull(validation.getExternalSimulationId());
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

        assertEquals(createdInsrcEventDAO.getInsrncCompanySimulationId(), validation.getExternalSimulationId());

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
        assertEquals(createdInsrcEventDAO.getInsuranceProductDesc(), validation.getProduct().getName());

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

        assertNotNull(validation.getBank().getId());
        assertNotNull(validation.getBank().getBranch().getId());

        assertEquals("bankId", validation.getBank().getId());
        assertEquals("branchId", validation.getBank().getBranch().getId());

        assertEquals("INSURANCE_CREATION", validation.getOperationType());

        /* CASO PARA TIPO DE PAGO CON TARJETA Y CON UN PRODUCTO DIFERENTE A HOGAR TOTAL*/
        when(createdInsrcEventDAO.getPaymentMethodId()).thenReturn("T");
        createdInsuranceDTO.getHolder().getContactDetails().clear();
        createdInsuranceDTO.getProduct().setId("832");

        validation = this.mapperHelper.createGifoleServiceRequest(createdInsuranceDTO, createdInsrcEventDAO, null, bank);

        assertNull(validation.getExternalSimulationId());

        assertTrue(validation.getHolder().getHasCreditCard());
        assertFalse(validation.getHolder().getHasBankAccount());

        assertEquals("", validation.getHolder().getFirstName());
        assertEquals("", validation.getHolder().getLastName());
        assertEquals("TARJETA", validation.getInsurance().getPaymentMethod().getId());
        assertEquals("Sin nro", validation.getHolder().getContactDetails().get(0).getContact().getPhoneNumber());
        assertEquals("Sin email", validation.getHolder().getContactDetails().get(1).getContact().getAddress());

    }

    @Test
    public void createEmailServiceRequestVehicle() {

        when(this.applicationConfigurationService.getProperty("mail.subject.vehicle")).thenReturn("!Genial! Acabas de comprar tu seguro vehicular con éxito");

        createdInsuranceDTO.getProduct().setId("830");

        when(requiredFieldsEmissionDAO.getVehicleLicenseId()).thenReturn("license");
        when(requiredFieldsEmissionDAO.getVehicleBrandName()).thenReturn("brand");
        when(requiredFieldsEmissionDAO.getVehicleModelName()).thenReturn("model");
        when(requiredFieldsEmissionDAO.getVehicleYearId()).thenReturn("vehicleYear");
        when(requiredFieldsEmissionDAO.getGasConversionType()).thenReturn("S");
        when(requiredFieldsEmissionDAO.getVehicleCirculationType()).thenReturn("L");
        when(requiredFieldsEmissionDAO.getCommercialVehicleAmount()).thenReturn(BigDecimal.valueOf(1000));

        CustomerBO customer = new CustomerBO();
        customer.setFirstName("PEPE");
        customer.setLastName("FULA#O");
        customer.setSecondLastName("SULTA#O");

        CreateEmailASO validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, customer);

        assertNotNull(validation);
        assertNotNull(validation.getApplicationId());
        assertNotNull(validation.getRecipient());
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

        validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, null);

        assertNotNull(validation);

    }

    @Test
    public void createEmailServiceRequestHome() {

        when(this.applicationConfigurationService.getProperty("mail.subject.home")).thenReturn("!Genial! Acabas de comprar tu Seguro Hogar Total con éxito");

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

        CustomerBO customer = new CustomerBO();
        customer.setFirstName("PEPE");
        customer.setLastName("FULA#O");
        customer.setSecondLastName("SULTA#O");

        CreateEmailASO validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, customer);

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

        validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, null);

        assertNotNull(validation);

        createdInsuranceDTO.getProduct().getPlan().setId("05");

        validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, null);

        assertNotNull(validation);
    }

    @Test
    public void createEmailServiceRequestFlexipymeOK() {

        createdInsuranceDTO.getProduct().setId("833");

        when(this.applicationConfigurationService.getProperty("mail.subject.flexipyme")).thenReturn("FLEXIPYME_SUBJECT");

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

        when(this.applicationConfigurationService.getProperty("L")).thenReturn("DNI");

        Map<String, Object> responseGettingLegalRep = new HashMap<>();
        responseGettingLegalRep.put(RBVDProperties.FIELD_PERSONAL_DOC_TYPE.getValue(), "L");
        responseGettingLegalRep.put(RBVDProperties.FIELD_PARTICIPANT_PERSONAL_ID.getValue(), "12345678");

        when(this.pisdR012.executeGetASingleRow(anyString(), anyMap())).thenReturn(responseGettingLegalRep);

        CustomerBO customer = new CustomerBO();
        customer.setFirstName("PEPE");
        customer.setLastName("FULA#O");
        customer.setSecondLastName("SULTA#O");

        ContactDetailsBO contactDetail = new ContactDetailsBO();
        ContactTypeBO contactType = new ContactTypeBO();
        contactType.setId("EMAIL");
        contactDetail.setContact("correo@gmail.com");
        contactDetail.setContactType(contactType);

        customer.setContactDetails(singletonList(contactDetail));
        createdInsuranceDTO.getHolder().getContactDetails().get(0).getContact().setContactType("");

        CreateEmailASO validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, customer);

        assertNotNull(validation);

        when(responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_HOUSING_TYPE.getValue())).thenReturn("P");
        when(responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_EDIFICATION_LOAN_AMOUNT.getValue())).thenReturn(null);

        when(createdInsrcEventDAO.getRimacPolicy()).thenReturn(null);

        when(this.pisdR012.executeGetASingleRow(anyString(), anyMap())).thenReturn(null);

        when(this.applicationConfigurationService.getProperty("policyWithoutNumber")).thenReturn("En proceso");

        customer.setContactDetails(new ArrayList<>());

        validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, customer);

        assertNotNull(validation);

        createdInsuranceDTO.getHolder().getIdentityDocument().getDocumentType().setId("RUC");
        createdInsuranceDTO.getHolder().getIdentityDocument().setDocumentNumber("2088893512");
        createdInsuranceDTO.getHolder().getContactDetails().get(0).getContact().setContactType("EMAIL");

        when(this.httpClient.executeCypherService(anyObject())).thenReturn("encryptedValue");

        ListBusinessesASO listBusinesses = new ListBusinessesASO();
        BusinessASO business = new BusinessASO();
        business.setLegalName("legalName");
        listBusinesses.setData(singletonList(business));

        when(this.httpClient.executeGetListBusinesses(anyString(), anyString())).thenReturn(listBusinesses);

        validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, customer);

        assertNotNull(validation);

    }

    @Test(expected = BusinessException.class)
    public void createEmailServiceRequestFlexipymeWithCypherRestException() {

        createdInsuranceDTO.getProduct().setId("833");

        when(this.applicationConfigurationService.getProperty("mail.subject.flexipyme")).thenReturn("FLEXIPYME_SUBJECT");

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

        createdInsuranceDTO.getHolder().getIdentityDocument().getDocumentType().setId("RUC");
        createdInsuranceDTO.getHolder().getIdentityDocument().setDocumentNumber("2088893512");

        when(this.httpClient.executeCypherService(anyObject())).thenReturn(null);

        this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, null);

    }

    @Test(expected = BusinessException.class)
    public void createEmailServiceRequestFlexipymeWithListBusinessRestException() {

        createdInsuranceDTO.getProduct().setId("833");

        when(this.applicationConfigurationService.getProperty("mail.subject.flexipyme")).thenReturn("FLEXIPYME_SUBJECT");

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

        createdInsuranceDTO.getHolder().getIdentityDocument().getDocumentType().setId("RUC");
        createdInsuranceDTO.getHolder().getIdentityDocument().setDocumentNumber("2088893512");

        when(this.httpClient.executeCypherService(anyObject())).thenReturn("encryptedValue");

        when(this.httpClient.executeGetListBusinesses(anyString(), anyString())).thenReturn(null);

        this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, null);

    }

    @Test
    public void createEmailServiceRequestLifeWithListBusinessRestException() {

        createdInsuranceDTO.getProduct().setId("840");
        when(applicationConfigurationService.getProperty(anyString())).thenReturn("Genial Tu solicitud de Seguro de Vida fue ingresada con exito");
        CreateEmailASO validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, null);

        assertNotNull(validation);
        assertNotNull(validation.getApplicationId());
        assertNotNull(validation.getRecipient());
        assertNotNull(validation.getSubject());
        assertNotNull(validation.getSubject());
        assertNotNull(validation.getBody());
        assertNotNull(validation.getSender());

        assertEquals("0,ronald.dolores@bbva.com", validation.getRecipient());
        assertEquals("Genial Tu solicitud de Seguro de Vida fue ingresada con exito", validation.getSubject());
        assertEquals("procesos@bbva.com.pe", validation.getSender());

        when(createdInsrcEventDAO.getRimacPolicy()).thenReturn(null);

        validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, null);
    }
    @Test
    public void createEmailServiceRequestDesempleoWithListBusinessRestException() {

        createdInsuranceDTO.getProduct().setId("836");
        when(applicationConfigurationService.getDefaultProperty(anyString(),anyString())).thenReturn("Genial Tu solicitud de Seguro Desempleo fue ingresada con exito");
        CreateEmailASO validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, null);

        assertNotNull(validation);
        assertNotNull(validation.getApplicationId());
        assertNotNull(validation.getRecipient());
        assertNotNull(validation.getSubject());
        assertNotNull(validation.getSubject());
        assertNotNull(validation.getBody());
        assertNotNull(validation.getSender());

        assertEquals("0,ronald.dolores@bbva.com", validation.getRecipient());
        assertEquals("Genial Tu solicitud de Seguro Desempleo fue ingresada con exito", validation.getSubject());
        assertEquals("procesos@bbva.com.pe", validation.getSender());

        when(createdInsrcEventDAO.getRimacPolicy()).thenReturn(null);

        validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, null);
    }
    @Test
    public void createEmailServiceRequestDefault() {

        createdInsuranceDTO.getProduct().setId("000");
        when(applicationConfigurationService.getDefaultProperty(anyString(),anyString())).thenReturn("Genial Tu solicitud de Seguro fue ingresada con exito");
        CreateEmailASO validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, null);

        assertNotNull(validation);
        assertNotNull(validation.getApplicationId());
        assertNotNull(validation.getRecipient());
        assertNotNull(validation.getSubject());
        assertNotNull(validation.getSubject());
        assertNotNull(validation.getBody());
        assertNotNull(validation.getSender());

        assertEquals("0,ronald.dolores@bbva.com", validation.getRecipient());
        assertEquals("Genial Tu solicitud de Seguro fue ingresada con exito", validation.getSubject());
        assertEquals("procesos@bbva.com.pe", validation.getSender());

        when(createdInsrcEventDAO.getRimacPolicy()).thenReturn(null);

        validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, null);
    }

    @Test
    public void createGeneralEmailServiceRequest() {

        createdInsuranceDTO.getProduct().setId("834");

        CreateEmailASO validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, null);

        assertNotNull(validation);
        assertNotNull(validation.getApplicationId());
        assertNotNull(validation.getRecipient());
        assertNotNull(validation.getSubject());
        assertNotNull(validation.getSubject());
        assertNotNull(validation.getBody());
        assertNotNull(validation.getSender());

        assertEquals("0,ronald.dolores@bbva.com", validation.getRecipient());
        assertEquals("Genial Tu solicitud de Seguro de Proteccion de Tarjetas fue ingresada con éxito", validation.getSubject());
        assertEquals("procesos@bbva.com.pe", validation.getSender());

        when(createdInsrcEventDAO.getRimacPolicy()).thenReturn(null);

        validation = this.mapperHelper.createEmailServiceRequest(createdInsuranceDTO, requiredFieldsEmissionDAO, createdInsrcEventDAO, null);
    }
}
