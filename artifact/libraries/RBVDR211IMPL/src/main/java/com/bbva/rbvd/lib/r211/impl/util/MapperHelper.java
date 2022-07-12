package com.bbva.rbvd.lib.r211.impl.util;

import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.pisd.dto.insurance.aso.gifole.QuotationASO;
import com.bbva.pisd.dto.insurance.aso.gifole.ValidityPeriodASO;
import com.bbva.pisd.dto.insurance.aso.gifole.HolderASO;
import com.bbva.pisd.dto.insurance.aso.gifole.DocumentTypeASO;
import com.bbva.pisd.dto.insurance.aso.gifole.IdentityDocumentASO;
import com.bbva.pisd.dto.insurance.aso.gifole.ContactDetailASO;
import com.bbva.pisd.dto.insurance.aso.gifole.ContactASO;
import com.bbva.pisd.dto.insurance.aso.gifole.InsuranceASO;
import com.bbva.pisd.dto.insurance.aso.gifole.PaymentMethodASO;
import com.bbva.pisd.dto.insurance.aso.gifole.RelatedContractASO;
import com.bbva.pisd.dto.insurance.aso.gifole.ProductASO;
import com.bbva.pisd.dto.insurance.aso.gifole.PlanASO;
import com.bbva.pisd.dto.insurance.aso.gifole.InstallmentPlanASO;
import com.bbva.pisd.dto.insurance.aso.gifole.PeriodASO;
import com.bbva.pisd.dto.insurance.aso.gifole.AmountASO;

import com.bbva.rbvd.dto.insrncsale.dao.CreatedInsrcEventDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;

import com.bbva.rbvd.dto.insrncsale.events.InstallmentPlansCreatedInsrcEvent;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.math.BigDecimal;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class MapperHelper {

    private static final DateTimeZone AMERICA_LIMA_ZONE = DateTimeZone.forID("America/Lima");

    public GifoleInsuranceRequestASO createGifoleServiceRequest(CreatedInsrcEventDTO createdInsrcEventDTO, CreatedInsrcEventDAO createdInsrcEventDAO,
                                                                RequiredFieldsEmissionDAO emissionDAO) {
        GifoleInsuranceRequestASO gifoleRequest = new GifoleInsuranceRequestASO();

        QuotationASO quotation = new QuotationASO();
        quotation.setId(createdInsrcEventDTO.getQuotationId());

        gifoleRequest.setQuotation(quotation);

        gifoleRequest.setChannel(createdInsrcEventDTO.getAap());

        DateTime operationDate = new DateTime(createdInsrcEventDTO.getOperationDate(), AMERICA_LIMA_ZONE);
        gifoleRequest.setOperationDate(operationDate.toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));

        String validityPeriodStartDate = createdInsrcEventDTO.getValidityPeriod().getStartDate().toInstant()
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        String validityPeriodEndDate = createdInsrcEventDTO.getValidityPeriod().getEndDate().toInstant()
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));

        ValidityPeriodASO validityPeriod = new ValidityPeriodASO(validityPeriodStartDate, validityPeriodEndDate);

        gifoleRequest.setValidityPeriod(validityPeriod);

        HolderASO holder = new HolderASO();
        holder.setFirstName("NOMBRE DEL CLIENTE - BD");
        holder.setLastName("APELLIDO DEL CLIENTE - BD");

        holder.setIsBankCustomer(true);
        holder.setIsDataTreatment(true);

        DocumentTypeASO documentType = new DocumentTypeASO();
        documentType.setId(createdInsrcEventDTO.getHolder().getIdentityDocument().getDocumentType().getId());

        IdentityDocumentASO identityDocument = new IdentityDocumentASO();
        identityDocument.setDocumentType(documentType);
        identityDocument.setDocumentNumber(createdInsrcEventDTO.getHolder().getIdentityDocument().getDocumentNumber());

        holder.setIdentityDocument(identityDocument);

        String EMAIL_CONTACT_TYPE_ID = "EMAIL";

        String phoneNumber = createdInsrcEventDTO.getHolder().getContactDetails().stream()
                .filter(contactDetail -> contactDetail.getContact().getContactType().equals("MOBILE")).findAny()
                .map(contactDetail -> contactDetail.getContact().getValue()).orElse("Sin nro");

        String emailAddress = createdInsrcEventDTO.getHolder().getContactDetails().stream()
                .filter(contactDetail -> contactDetail.getContact().getContactType().equals(EMAIL_CONTACT_TYPE_ID)).findAny()
                .map(contactDetail -> contactDetail.getContact().getValue()).orElse("Sin email");

        ContactDetailASO phoneContact = new ContactDetailASO();
        ContactASO phContact = new ContactASO();
        phContact.setContactType("PHONE");
        phContact.setPhoneNumber(phoneNumber);
        phoneContact.setContact(phContact);

        ContactDetailASO emailContact = new ContactDetailASO();
        ContactASO emContact = new ContactASO();
        emContact.setContactType(EMAIL_CONTACT_TYPE_ID);
        emContact.setAddress(emailAddress);
        emailContact.setContact(emContact);

        holder.setContactDetails(Stream.of(phoneContact, emailContact).collect(toList()));

        InsuranceASO insurance = new InsuranceASO();
        insurance.setId(createdInsrcEventDAO.getContractNumber());

        PaymentMethodASO paymentMethod = new PaymentMethodASO();
        if(createdInsrcEventDAO.getPaymentMethodId().equals("C")) {
            paymentMethod.setId("CUENTA");
            holder.setHasCreditCard(false);
            holder.setHasBankAccount(true);
        } else {
            paymentMethod.setId("TARJETA");
            holder.setHasCreditCard(true);
            holder.setHasBankAccount(false);
        }

        RelatedContractASO relatedContract = new RelatedContractASO();
        relatedContract.setNumber("****1234");

        paymentMethod.setRelatedContracts(singletonList(relatedContract));

        insurance.setPaymentMethod(paymentMethod);

        gifoleRequest.setHolder(holder);

        gifoleRequest.setInsurance(insurance);

        gifoleRequest.setPolicyNumber(createdInsrcEventDAO.getRimacPolicy());

        ProductASO product = new ProductASO();
        product.setId(createdInsrcEventDTO.getProduct().getId());
        product.setName(emissionDAO.getInsuranceProductDesc());

        PlanASO plan = new PlanASO();
        plan.setId(createdInsrcEventDTO.getProduct().getPlan().getId());
        plan.setName(emissionDAO.getInsuranceModalityName());

        product.setPlan(plan);

        gifoleRequest.setProduct(product);

        InstallmentPlansCreatedInsrcEvent installmentPlanEvnt = createdInsrcEventDTO.getProduct().getPlan().getInstallmentPlans().get(0);

        InstallmentPlanASO installmentPlan = new InstallmentPlanASO();
        PeriodASO period = new PeriodASO();
        period.setId(installmentPlanEvnt.getPeriod().getId());
        period.setName(createdInsrcEventDAO.getPeriodName());

        installmentPlan.setPeriod(period);

        AmountASO premiumAmount = new AmountASO();
        premiumAmount.setAmount(BigDecimal.valueOf(installmentPlanEvnt.getPaymentAmount().getAmount()));
        premiumAmount.setCurrency(installmentPlanEvnt.getPaymentAmount().getCurrency());

        installmentPlan.setPremiumAmount(premiumAmount);
        installmentPlan.setTotalInstallmentsNumber(installmentPlanEvnt.getPaymentsTotalNumber().longValue());

        gifoleRequest.setInstallmentPlan(installmentPlan);

        AmountASO totalPremiumAmount = new AmountASO();
        totalPremiumAmount.setAmount(BigDecimal.valueOf(createdInsrcEventDTO.getProduct().getPlan().getTotalInstallment().getAmount()));
        totalPremiumAmount.setCurrency(createdInsrcEventDTO.getProduct().getPlan().getTotalInstallment().getCurrency());

        gifoleRequest.setTotalPremiumAmount(totalPremiumAmount);

        return gifoleRequest;
    }

}
