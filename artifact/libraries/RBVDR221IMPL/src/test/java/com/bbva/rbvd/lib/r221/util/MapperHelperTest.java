package com.bbva.rbvd.lib.r221.util;

import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;

import com.bbva.rbvd.dto.insrncsale.dao.CreatedInsrcEventDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;
import com.bbva.rbvd.dto.insrncsale.events.InstallmentPlansCreatedInsrcEvent;

import com.bbva.rbvd.dto.insrncsale.mock.MockData;

import com.bbva.rbvd.lib.r221.impl.util.MapperHelper;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static java.math.BigDecimal.valueOf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MapperHelperTest {

    private final MapperHelper mapperHelper = new MapperHelper();

    private final MockData mockData = MockData.getInstance();

    private CreatedInsrcEventDAO createdInsrcEventDAO;
    private RequiredFieldsEmissionDAO requiredFieldsEmissionDAO;

    @Before
    public void setUp() {
        createdInsrcEventDAO = mock(CreatedInsrcEventDAO.class);
        requiredFieldsEmissionDAO = mock(RequiredFieldsEmissionDAO.class);
    }

    @Test
    public void createGifoleServiceRequestWithCardAndAccountPaymentMethod() throws IOException {

        CreatedInsrcEventDTO createdInsrcEventDTO = mockData.getCreatedInsrcEventRequest();

        when(createdInsrcEventDAO.getContractNumber()).thenReturn("0814000015111");
        when(createdInsrcEventDAO.getRimacPolicy()).thenReturn("510772");
        when(createdInsrcEventDAO.getPaymentMethodId()).thenReturn("C");
        when(createdInsrcEventDAO.getPeriodName()).thenReturn("MENSUAL");

        when(requiredFieldsEmissionDAO.getInsuranceProductDesc()).thenReturn("VEHICULAR OPTATIVO");
        when(requiredFieldsEmissionDAO.getInsuranceModalityName()).thenReturn("PLAN BASICO");

        GifoleInsuranceRequestASO validation = this.mapperHelper.createGifoleServiceRequest(createdInsrcEventDTO, createdInsrcEventDAO, requiredFieldsEmissionDAO);

        assertNotNull(validation);

        assertNotNull(validation.getQuotation());

        assertNotNull(validation.getQuotation().getId());
        assertEquals(createdInsrcEventDTO.getQuotationId(), validation.getQuotation().getId());

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
        //ESTE CAMPO TIENE UN DETALLE, POR AHORA VA EN DURO
        assertNotNull(validation.getInsurance().getPaymentMethod().getRelatedContracts().get(0).getNumber());

        assertNotNull(validation.getPolicyNumber());
        assertEquals(createdInsrcEventDAO.getRimacPolicy(), validation.getPolicyNumber());

        assertNotNull(validation.getProduct());

        assertEquals(createdInsrcEventDTO.getProduct().getId(), validation.getProduct().getId());
        assertEquals(requiredFieldsEmissionDAO.getInsuranceProductDesc(), validation.getProduct().getName());

        assertNotNull(validation.getProduct().getPlan());
        assertEquals(createdInsrcEventDTO.getProduct().getPlan().getId(), validation.getProduct().getPlan().getId());
        assertEquals(requiredFieldsEmissionDAO.getInsuranceModalityName(), validation.getProduct().getPlan().getName());

        assertNotNull(validation.getInstallmentPlan());
        assertNotNull(validation.getInstallmentPlan().getPeriod());

        InstallmentPlansCreatedInsrcEvent installmentPlanEvnt = createdInsrcEventDTO.getProduct().getPlan().getInstallmentPlans().get(0);

        assertEquals(installmentPlanEvnt.getPeriod().getId(), validation.getInstallmentPlan().getPeriod().getId());
        assertEquals(createdInsrcEventDAO.getPeriodName(), validation.getInstallmentPlan().getPeriod().getName());

        assertNotNull(validation.getInstallmentPlan().getPremiumAmount());
        assertEquals(valueOf(installmentPlanEvnt.getPaymentAmount().getAmount()), validation.getInstallmentPlan().getPremiumAmount().getAmount());
        assertEquals(installmentPlanEvnt.getPaymentAmount().getCurrency(), validation.getInstallmentPlan().getPremiumAmount().getCurrency());

        Long totalInstallmentsNumber = installmentPlanEvnt.getPaymentsTotalNumber().longValue();

        assertEquals(totalInstallmentsNumber, validation.getInstallmentPlan().getTotalInstallmentsNumber());

        assertNotNull(validation.getTotalPremiumAmount());
        assertEquals(valueOf(createdInsrcEventDTO.getProduct().getPlan().getTotalInstallment().getAmount()),
                validation.getTotalPremiumAmount().getAmount());
        assertEquals(createdInsrcEventDTO.getProduct().getPlan().getTotalInstallment().getCurrency(),
                validation.getTotalPremiumAmount().getCurrency());

        /* CASO PARA TIPO DE PAGO CON TARJETA */
        when(createdInsrcEventDAO.getPaymentMethodId()).thenReturn("T");
        createdInsrcEventDTO.getHolder().getContactDetails().clear();

        validation = this.mapperHelper.createGifoleServiceRequest(createdInsrcEventDTO, createdInsrcEventDAO, requiredFieldsEmissionDAO);

        assertTrue(validation.getHolder().getHasCreditCard());
        assertFalse(validation.getHolder().getHasBankAccount());

        assertEquals("TARJETA", validation.getInsurance().getPaymentMethod().getId());
        assertEquals("Sin nro", validation.getHolder().getContactDetails().get(0).getContact().getPhoneNumber());
        assertEquals("Sin email", validation.getHolder().getContactDetails().get(1).getContact().getAddress());

    }

}
