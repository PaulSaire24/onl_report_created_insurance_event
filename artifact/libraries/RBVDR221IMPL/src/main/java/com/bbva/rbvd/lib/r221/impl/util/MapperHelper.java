package com.bbva.rbvd.lib.r221.impl.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;

import com.bbva.pisd.dto.insurance.aso.email.CreateEmailASO;

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

import com.bbva.pisd.lib.r021.PISDR021;

import com.bbva.rbvd.dto.homeinsrc.dao.SimltInsuredHousingDAO;
import com.bbva.rbvd.dto.homeinsrc.utils.HomeInsuranceProperty;
import com.bbva.rbvd.dto.insrncsale.dao.CreatedInsrcEventDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsuranceDTO;
import com.bbva.rbvd.dto.insrncsale.events.InstallmentPlansCreatedInsrcEvent;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.math.BigDecimal.valueOf;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class MapperHelper {

    private static final DateTimeZone AMERICA_LIMA_ZONE = DateTimeZone.forID("America/Lima");
    private static final String MAIL_SENDER = "procesos@bbva.com.pe";

    private final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

    private ApplicationConfigurationService applicationConfigurationService;

    private PISDR021 pisdR021;

    public GifoleInsuranceRequestASO createGifoleServiceRequest(CreatedInsuranceDTO createdInsuranceDTO, CreatedInsrcEventDAO createdInsrcEventDAO,
                                                                RequiredFieldsEmissionDAO emissionDAO, String name, String lastName) {
        GifoleInsuranceRequestASO gifoleRequest = new GifoleInsuranceRequestASO();

        QuotationASO quotation = new QuotationASO();
        quotation.setId(createdInsuranceDTO.getQuotationId());

        gifoleRequest.setQuotation(quotation);

        gifoleRequest.setChannel(createdInsuranceDTO.getAap());

        DateTime operationDate = new DateTime(createdInsuranceDTO.getOperationDate(), AMERICA_LIMA_ZONE);
        gifoleRequest.setOperationDate(operationDate.toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));

        String validityPeriodStartDate = createdInsuranceDTO.getValidityPeriod().getStartDate().toInstant()
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        String validityPeriodEndDate = createdInsuranceDTO.getValidityPeriod().getEndDate().toInstant()
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));

        ValidityPeriodASO validityPeriod = new ValidityPeriodASO(validityPeriodStartDate, validityPeriodEndDate);

        gifoleRequest.setValidityPeriod(validityPeriod);

        HolderASO holder = new HolderASO();
        holder.setFirstName(name);
        holder.setLastName(lastName);

        holder.setIsBankCustomer(true);
        holder.setIsDataTreatment(true);

        DocumentTypeASO documentType = new DocumentTypeASO();
        documentType.setId(createdInsuranceDTO.getHolder().getIdentityDocument().getDocumentType().getId());

        IdentityDocumentASO identityDocument = new IdentityDocumentASO();
        identityDocument.setDocumentType(documentType);
        identityDocument.setDocumentNumber(createdInsuranceDTO.getHolder().getIdentityDocument().getDocumentNumber());

        holder.setIdentityDocument(identityDocument);

        String emailContactTypeId = "EMAIL";

        String phoneNumber = createdInsuranceDTO.getHolder().getContactDetails().stream()
                .filter(contactDetail -> contactDetail.getContact().getContactType().equals("MOBILE")).findAny()
                .map(contactDetail -> contactDetail.getContact().getValue()).orElse("Sin nro");

        String emailAddress = createdInsuranceDTO.getHolder().getContactDetails().stream()
                .filter(contactDetail -> contactDetail.getContact().getContactType().equals(emailContactTypeId)).findAny()
                .map(contactDetail -> contactDetail.getContact().getValue()).orElse("Sin email");

        ContactDetailASO phoneContact = new ContactDetailASO();
        ContactASO phContact = new ContactASO();
        phContact.setContactType("PHONE");
        phContact.setPhoneNumber(phoneNumber);
        phoneContact.setContact(phContact);

        ContactDetailASO emailContact = new ContactDetailASO();
        ContactASO emContact = new ContactASO();
        emContact.setContactType(emailContactTypeId);
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
        product.setId(createdInsuranceDTO.getProduct().getId());
        product.setName(emissionDAO.getInsuranceProductDesc());

        PlanASO plan = new PlanASO();
        plan.setId(createdInsuranceDTO.getProduct().getPlan().getId());
        plan.setName(emissionDAO.getInsuranceModalityName());

        product.setPlan(plan);

        gifoleRequest.setProduct(product);

        InstallmentPlansCreatedInsrcEvent installmentPlanEvnt = createdInsuranceDTO.getProduct().getPlan().getInstallmentPlans().get(0);

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
        totalPremiumAmount.setAmount(BigDecimal.valueOf(createdInsuranceDTO.getProduct().getPlan().getTotalInstallment().getAmount()));
        totalPremiumAmount.setCurrency(createdInsuranceDTO.getProduct().getPlan().getTotalInstallment().getCurrency());

        gifoleRequest.setTotalPremiumAmount(totalPremiumAmount);

        String operationType = this.applicationConfigurationService.getProperty("emission-gifole-operation");

        gifoleRequest.setOperationType(operationType);

        return gifoleRequest;
    }

    public CreateEmailASO createEmailServiceRequest(CreatedInsuranceDTO requestBody, RequiredFieldsEmissionDAO emissionDao,
                                                    CreatedInsrcEventDAO createdInsrcEventDao, String customerName) {

        CreateEmailASO createEmailASO = null;

        String productId = requestBody.getProduct().getId();

        if(productId.equals("830")) {
            createEmailASO = buildVehicleEmailRequest(requestBody, emissionDao, createdInsrcEventDao);
        } else if(productId.equals("834")) {
            createEmailASO = buildGeneralEmailRequest(requestBody, emissionDao, createdInsrcEventDao, customerName);
        } else {
            Map<String, Object> responseQueryGetHomeInfo = pisdR021.executeGetHomeInfoForEmissionService(requestBody.getQuotationId());
            Map<String, Object> responseQueryGetHomeRiskDirection= pisdR021.executeGetHomeRiskDirection(requestBody.getQuotationId());

            SimltInsuredHousingDAO simltInsuredHousingDAO = buildSimltInsuredHousingDAO(responseQueryGetHomeInfo);
            String riskDirection = (String) responseQueryGetHomeRiskDirection.get(HomeInsuranceProperty.FIELD_LEGAL_ADDRESS_DESC.getValue());

            createEmailASO = buildHomeEmailRequest(requestBody, emissionDao, createdInsrcEventDao, simltInsuredHousingDAO, customerName, riskDirection);
        }
        return createEmailASO;
    }

    private CreateEmailASO buildVehicleEmailRequest(CreatedInsuranceDTO requestBody, RequiredFieldsEmissionDAO emissionDao, CreatedInsrcEventDAO createdInsrcEventDao) {
        String vehicleLayoutCode = "PLT00945";

        CreateEmailASO vehicleEmail = new CreateEmailASO();
        vehicleEmail.setApplicationId(vehicleLayoutCode.concat(format.format(new Date())));
        vehicleEmail.setRecipient("0,".concat(requestBody.getHolder().getContactDetails().get(0).getContact().getValue()));
        vehicleEmail.setSubject("!Genial! Acabas de comprar tu seguro vehicular con éxito");

        String[] data = this.getMailBodyDataVeh(requestBody, emissionDao, createdInsrcEventDao);

        vehicleEmail.setBody(this.getMailBodyVeh(data, vehicleLayoutCode));
        vehicleEmail.setSender(MAIL_SENDER);

        return vehicleEmail;
    }

    private String[] getMailBodyDataVeh(CreatedInsuranceDTO requestBody, RequiredFieldsEmissionDAO emissionDao, CreatedInsrcEventDAO createdInsrcEventDao) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);

        String[] bodyData = new String[13];

        bodyData[0] = "";
        bodyData[1] = Objects.nonNull(emissionDao.getVehicleLicenseId()) ? emissionDao.getVehicleLicenseId() : "EN TRAMITE";
        bodyData[2] = emissionDao.getVehicleBrandName();
        bodyData[3] = emissionDao.getVehicleModelName();
        bodyData[4] = emissionDao.getVehicleYearId();
        bodyData[5] = emissionDao.getGasConversionType().equals("S") ? "Sí" : "No";
        bodyData[6] = emissionDao.getVehicleCirculationType().equals("L") ? "Lima" : "Provincia";
        bodyData[7] = numberFormat.format(emissionDao.getCommercialVehicleAmount());
        bodyData[8] = this.getContractNumber(createdInsrcEventDao.getContractNumber());
        bodyData[9] = createdInsrcEventDao.getRimacPolicy();
        bodyData[10] = emissionDao.getInsuranceModalityName();

        BigDecimal monthlyPay = valueOf(requestBody.getProduct().getPlan().getInstallmentPlans().get(0).getPaymentAmount().getAmount());

        bodyData[11] = "US$".concat(" ").concat(numberFormat.format(monthlyPay));
        bodyData[12] = createdInsrcEventDao.getPeriodName();

        return bodyData;
    }

    private String getContractNumber(String id) {
        StringBuilder contract = new StringBuilder();
        contract.append(id, 0, 4).append("-")
                .append(id, 4, 8).append("-")
                .append(id, 8, 10).append("-")
                .append(id.substring(10));
        return contract.toString();
    }

    private String getMailBodyVeh(String[] data, String vehicleLayoutCode) {
        StringBuilder body = new StringBuilder();
        int hundredCode = 100;
        for(int i = 0; i < data.length; i++) {
            if(i > 7) {
                body.append(hundredCode).append(data[i]).append("|");
                hundredCode++;
                continue;
            }
            body.append(this.generateCode(i+1)).append(data[i]).append("|");
        }
        body.append(vehicleLayoutCode);
        return body.toString();
    }

    private String generateCode(Integer index) {
        return "00".concat(index.toString());
    }

    private SimltInsuredHousingDAO buildSimltInsuredHousingDAO(Map<String, Object> responseQueryGetHomeInfo) {
        SimltInsuredHousingDAO emissionDao = new SimltInsuredHousingDAO();
        emissionDao.setDepartmentName((String) responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_DEPARTMENT_NAME.getValue()));
        emissionDao.setProvinceName((String) responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_PROVINCE_NAME.getValue()));
        emissionDao.setDistrictName((String) responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_DISTRICT_NAME.getValue()));
        emissionDao.setHousingType((String) responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_HOUSING_TYPE.getValue()));
        emissionDao.setAreaPropertyNumber((BigDecimal) responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_AREA_PROPERTY_1_NUMBER.getValue()));
        emissionDao.setPropSeniorityYearsNumber((BigDecimal) responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_PROP_SENIORITY_YEARS_NUMBER.getValue()));
        emissionDao.setFloorNumber((BigDecimal) responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_FLOOR_NUMBER.getValue()));
        emissionDao.setEdificationLoanAmount((BigDecimal) responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_EDIFICATION_LOAN_AMOUNT.getValue()));
        emissionDao.setHousingAssetsLoanAmount((BigDecimal) responseQueryGetHomeInfo.get(HomeInsuranceProperty.FIELD_HOUSING_ASSETS_LOAN_AMOUNT.getValue()));
        return emissionDao;
    }

    private CreateEmailASO buildHomeEmailRequest(CreatedInsuranceDTO requestBody, RequiredFieldsEmissionDAO emissionDao,
                                                 CreatedInsrcEventDAO createdInsrcEventDao, SimltInsuredHousingDAO simltInsuredHousingDAO,
                                                 String customerName, String riskDirection) {
        String homeLayoutCode = "PLT00968";

        CreateEmailASO homeEmail = new CreateEmailASO();
        homeEmail.setApplicationId(homeLayoutCode.concat(format.format(new Date())));
        homeEmail.setRecipient("0,".concat(requestBody.getHolder().getContactDetails().get(0).getContact().getValue()));
        homeEmail.setSubject("!Genial! Acabas de comprar tu Seguro Hogar Total con éxito");

        String[] data = this.getMailBodyDataHome(requestBody, emissionDao, createdInsrcEventDao, simltInsuredHousingDAO, customerName, riskDirection);

        homeEmail.setBody(this.getMailBodyHome(data, homeLayoutCode));
        homeEmail.setSender(MAIL_SENDER);

        return homeEmail;
    }

    private String[] getMailBodyDataHome(CreatedInsuranceDTO requestBody, RequiredFieldsEmissionDAO emissionDao,
                                         CreatedInsrcEventDAO createdInsrcEventDao, SimltInsuredHousingDAO simltInsuredHousingDAO,
                                         String customerName, String riskDirection) {
        String[] bodyData = new String[18];

        String noneValue = "none";
        String slashValue = "/";
        String penCurrency = "S/";
        String customerType = simltInsuredHousingDAO.getHousingType();
        String planId = requestBody.getProduct().getPlan().getId();

        if("P".equals(customerType)) {
            bodyData[0] = customerName;
            bodyData[1] = " de tu inmueble";
            bodyData[3] = "";
        } else {
            bodyData[0] = customerName;
            bodyData[1] = " del inmueble que alquilas";
            bodyData[3] = noneValue;
        }

        bodyData[2] = simltInsuredHousingDAO.getDepartmentName().concat(slashValue).
                concat(simltInsuredHousingDAO.getProvinceName()).concat(slashValue).
                concat(simltInsuredHousingDAO.getDistrictName());
        bodyData[4] = simltInsuredHousingDAO.getAreaPropertyNumber().toString();
        bodyData[5] = simltInsuredHousingDAO.getPropSeniorityYearsNumber().toString();
        bodyData[6] = simltInsuredHousingDAO.getFloorNumber().toString();

        switch (planId) {
            case "04":
                bodyData[7] = noneValue;
                bodyData[10] = "";
                break;
            case "05":
                bodyData[7] = "";
                bodyData[10] = noneValue;
                break;
            default:
                bodyData[7] = "";
                bodyData[10] = "";
        }

        bodyData[8] = penCurrency;

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);
        bodyData[9] = Objects.nonNull(simltInsuredHousingDAO.getEdificationLoanAmount()) ?
                numberFormat.format(simltInsuredHousingDAO.getEdificationLoanAmount()) : "";
        bodyData[11] = Objects.nonNull(simltInsuredHousingDAO.getHousingAssetsLoanAmount()) ?
                numberFormat.format(simltInsuredHousingDAO.getHousingAssetsLoanAmount()) : "";

        bodyData[12] = getContractNumber(createdInsrcEventDao.getContractNumber());
        bodyData[13] = createdInsrcEventDao.getRimacPolicy();
        bodyData[14] = numberFormat.format(requestBody.getProduct().getPlan().getInstallmentPlans().get(0).getPaymentAmount().getAmount());
        bodyData[15] = createdInsrcEventDao.getPeriodName();
        bodyData[16] = emissionDao.getInsuranceModalityName();
        bodyData[17] = riskDirection;

        return bodyData;
    }

    private String getMailBodyHome(String[] data, String homeLayoutCode) {
        StringBuilder body = new StringBuilder();
        for(int i = 0; i < data.length; i++) {
            body.append(generateCode(String.valueOf(i+1))).append(data[i]).append("|");
        }
        body.append(homeLayoutCode);
        return body.toString();
    }

    private String generateCode(String index) {
        StringBuilder code = new StringBuilder();
        int numberOfZeros = 3 - index.length();
        for(int i = 0; i < numberOfZeros; i++) {
            code.append("0");
        }
        code.append(index);
        return code.toString();
    }

    private CreateEmailASO buildGeneralEmailRequest(CreatedInsuranceDTO requestBody, RequiredFieldsEmissionDAO emissionDao, CreatedInsrcEventDAO createdInsrcEventDao, String customerName) {
        String layoutCode = "PLT01011";

        CreateEmailASO generalEmail = new CreateEmailASO();
        generalEmail.setApplicationId(layoutCode.concat(format.format(new Date())));
        generalEmail.setRecipient("0,".concat(requestBody.getHolder().getContactDetails().get(0).getContact().getValue()));
        generalEmail.setSubject("Genial Tu solicitud de Seguro de Proteccion de Tarjetas fue ingresada con exito");

        String[] data = this.getGeneralMailBodyData(requestBody, emissionDao, createdInsrcEventDao, customerName);

        generalEmail.setBody(this.getMailBodyHome(data, layoutCode));
        generalEmail.setSender(MAIL_SENDER);

        return generalEmail;
    }

    private String[] getGeneralMailBodyData(CreatedInsuranceDTO requestBody, RequiredFieldsEmissionDAO emissionDao, CreatedInsrcEventDAO createdInsrcEventDao, String customerName) {

        String[] bodyData = new String[7];

        bodyData[0] = customerName;
        bodyData[1] = createdInsrcEventDao.getRimacPolicy();
        bodyData[2] = emissionDao.getInsuranceModalityName();
        bodyData[3] = createdInsrcEventDao.getInsuranceCompanyDesc();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("es", "ES"));

        bodyData[4] = dateFormat.format(requestBody.getValidityPeriod().getStartDate());
        bodyData[5] = dateFormat.format(requestBody.getValidityPeriod().getEndDate());
        bodyData[6] = "Tarjeta Clasica ***123";
        
        return bodyData;
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    public void setPisdR021(PISDR021 pisdR021) {
        this.pisdR021 = pisdR021;
    }

}
