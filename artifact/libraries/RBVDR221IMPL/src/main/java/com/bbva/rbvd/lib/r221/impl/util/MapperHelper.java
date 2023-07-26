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
import com.bbva.pisd.dto.insurance.aso.gifole.RelatedContractASO;
import com.bbva.pisd.dto.insurance.aso.gifole.InsuranceASO;
import com.bbva.pisd.dto.insurance.aso.gifole.PaymentMethodASO;
import com.bbva.pisd.dto.insurance.aso.gifole.PeriodASO;
import com.bbva.pisd.dto.insurance.aso.gifole.InstallmentPlanASO;
import com.bbva.pisd.dto.insurance.aso.gifole.PlanASO;
import com.bbva.pisd.dto.insurance.aso.gifole.ProductASO;
import com.bbva.pisd.dto.insurance.aso.gifole.AmountASO;
import com.bbva.pisd.dto.insurance.aso.gifole.BankASO;
import com.bbva.pisd.dto.insurance.aso.gifole.BranchASO;

import com.bbva.pisd.dto.insurance.bo.ContactDetailsBO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;

import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.pisd.lib.r021.PISDR021;

import com.bbva.rbvd.dto.homeinsrc.dao.SimltInsuredHousingDAO;

import com.bbva.rbvd.dto.homeinsrc.utils.HomeInsuranceProperty;

import com.bbva.rbvd.dto.insrncsale.aso.cypher.CypherASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;

import com.bbva.rbvd.dto.insrncsale.commons.ContactDTO;
import com.bbva.rbvd.dto.insrncsale.commons.ContactDetailDTO;
import com.bbva.rbvd.dto.insrncsale.dao.CreatedInsrcEventDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsuranceDTO;
import com.bbva.rbvd.dto.insrncsale.events.InstallmentPlansCreatedInsrcEvent;

import com.bbva.rbvd.dto.insrncsale.events.header.BankEventDTO;
import com.bbva.rbvd.dto.insrncsale.utils.HolderTypeEnum;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDErrors;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDValidation;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;

import org.joda.time.LocalDate;
import org.joda.time.DateTimeZone;

import java.math.BigDecimal;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Calendar;
import java.util.stream.Stream;

import static java.math.BigDecimal.valueOf;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

import static com.google.common.base.Strings.nullToEmpty;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.isEmpty;

public class MapperHelper {

    private static final String MAIL_SENDER = "procesos@bbva.com.pe";
    private static final String MASK_VALUE = "****";
    private static final String NONE = "none";
    private static final String IN_PROCCESS_KEY = "policyWithoutNumber";
    private static final String MAIL_SUJECT_VEHICLE = "mail.subject.vehicle";
    private static final String MAIL_SUJECT_HOME = "mail.subject.home";
    private static final String MAIL_SUJECT_LIFE = "mail.subject.life";

    private static final String MAIL_SUJECT_GENERIC = "mail.subject.generic.product";
    private static final String MAIL_SUJECT_FLEXIPYME = "mail.subject.flexipyme";

    private final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

    private ApplicationConfigurationService applicationConfigurationService;

    private HttpClient httpClient;

    private PISDR021 pisdR021;

    private PISDR012 pisdR012;

    public GifoleInsuranceRequestASO createGifoleServiceRequest(final CreatedInsuranceDTO createdInsuranceDTO, final CreatedInsrcEventDAO createdInsrcEventDAO,
                                                                final CustomerBO customerInformation, final BankEventDTO bank) {
        GifoleInsuranceRequestASO gifoleRequest = new GifoleInsuranceRequestASO();

        ProductASO product = new ProductASO();
        product.setId(createdInsuranceDTO.getProduct().getId());
        product.setName(createdInsrcEventDAO.getInsuranceProductDesc());

        PlanASO plan = new PlanASO();
        plan.setId(createdInsuranceDTO.getProduct().getPlan().getId());
        plan.setName(createdInsrcEventDAO.getInsuranceModalityName());

        product.setPlan(plan);

        gifoleRequest.setProduct(product);

        settingRimacSimulationId(gifoleRequest, createdInsrcEventDAO.getInsrncCompanySimulationId());

        QuotationASO quotation = new QuotationASO();
        quotation.setId(createdInsuranceDTO.getQuotationId());

        gifoleRequest.setQuotation(quotation);

        gifoleRequest.setChannel(createdInsuranceDTO.getAap());

        createdInsuranceDTO.getOperationDate().add(Calendar.HOUR, 5);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        gifoleRequest.setOperationDate(dateFormat.format(createdInsuranceDTO.getOperationDate().getTime()));

        String validityPeriodStartDate = createdInsuranceDTO.getValidityPeriod().getStartDate().toInstant()
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        String validityPeriodEndDate = createdInsuranceDTO.getValidityPeriod().getEndDate().toInstant()
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));

        ValidityPeriodASO validityPeriod = new ValidityPeriodASO(validityPeriodStartDate, validityPeriodEndDate);

        gifoleRequest.setValidityPeriod(validityPeriod);

        String name = "";

        String lastName = "";

        if(nonNull(customerInformation)) {
            name = nullToEmpty(customerInformation.getFirstName());
            lastName = nullToEmpty(customerInformation.getLastName()) + " " + nullToEmpty(customerInformation.getSecondLastName());
        }

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

        int beginIndex = createdInsuranceDTO.getPaymentMethod().getRelatedContracts().get(0).getNumber().length() - 4;

        RelatedContractASO relatedContract = new RelatedContractASO();
        relatedContract.setNumber(MASK_VALUE.concat(createdInsuranceDTO.getPaymentMethod().getRelatedContracts().get(0).getNumber().substring(beginIndex)));

        paymentMethod.setRelatedContracts(singletonList(relatedContract));

        insurance.setPaymentMethod(paymentMethod);

        gifoleRequest.setHolder(holder);

        gifoleRequest.setInsurance(insurance);

        gifoleRequest.setPolicyNumber(createdInsrcEventDAO.getRimacPolicy());

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

        BankASO bankGifole = new BankASO();
        bankGifole.setId(bank.getBankId());
        BranchASO branchGifole = new BranchASO();
        branchGifole.setId(bank.getBranch().getBranchId());
        bankGifole.setBranch(branchGifole);

        gifoleRequest.setBank(bankGifole);

        String operationType = this.applicationConfigurationService.getProperty("emission-gifole-operation");

        gifoleRequest.setOperationType(operationType);

        return gifoleRequest;
    }

    private void settingRimacSimulationId(final GifoleInsuranceRequestASO gifoleRequest, final String insuranceCompanySimulationId) {
        String productCode = gifoleRequest.getProduct().getId();
        if( !"830".equals(productCode) && !"833".equals(productCode) && !"834".equals(productCode) ) {
            gifoleRequest.setExternalSimulationId(insuranceCompanySimulationId);
        }
    }

    public CreateEmailASO createEmailServiceRequest(CreatedInsuranceDTO requestBody, RequiredFieldsEmissionDAO emissionDao,
                                                    CreatedInsrcEventDAO createdInsrcEventDao, CustomerBO customerInformation) {

        CreateEmailASO createEmailASO = null;

        String productId = requestBody.getProduct().getId();

        String name = "";

        String lastName = "";

        String fullName = "N/A";

        if(nonNull(customerInformation)) {
            name = nullToEmpty(customerInformation.getFirstName());
            lastName = nullToEmpty(customerInformation.getLastName()) + " " + nullToEmpty(customerInformation.getSecondLastName());
            fullName = name + " " + lastName;
            fullName = fullName.replace("#", "Ñ");
        }

        switch (productId) {
            case "830":
                createEmailASO = buildVehicleEmailRequest(requestBody, emissionDao, createdInsrcEventDao);
                break;
            case "832":
            case "833":
                Map<String, Object> responseQueryGetHomeInfo = pisdR021.executeGetHomeInfoForEmissionService(requestBody.getQuotationId());
                Map<String, Object> responseQueryGetHomeRiskDirection= pisdR021.executeGetHomeRiskDirection(requestBody.getQuotationId());

                SimltInsuredHousingDAO simltInsuredHousingDAO = buildSimltInsuredHousingDAO(responseQueryGetHomeInfo);
                String riskDirection = (String) responseQueryGetHomeRiskDirection.get(HomeInsuranceProperty.FIELD_LEGAL_ADDRESS_DESC.getValue());

                if("832".equals(productId)) {
                    createEmailASO = buildHomeEmailRequest(requestBody, createdInsrcEventDao, simltInsuredHousingDAO, fullName, riskDirection);
                } else {
                    String legalName = this.getLegalName(requestBody);
                    createEmailASO = buildFlexiPymeEmailRequest(requestBody, createdInsrcEventDao,
                            simltInsuredHousingDAO, riskDirection, customerInformation, legalName);
                }

                break;
            case "840":
                String lifeSubjectEmission = this.applicationConfigurationService.getProperty(MAIL_SUJECT_LIFE);
                createEmailASO = buildGeneralEmailRequest(requestBody, createdInsrcEventDao, fullName, lifeSubjectEmission, "PLT01018");
                break;
            case "834":
                createEmailASO = buildGeneralEmailRequest(requestBody, createdInsrcEventDao, fullName, "Genial Tu solicitud de Seguro de Proteccion de Tarjetas fue ingresada con exito", "PLT01011");
                break;
            default:
                String subjectEmission = this.applicationConfigurationService.getDefaultProperty(MAIL_SUJECT_GENERIC.replace("product",productId),"Genial Tu solicitud de Seguro fue ingresada con exito");
                createEmailASO = buildGeneralEmailRequest(requestBody, createdInsrcEventDao, fullName, subjectEmission, "PLT01011");
                break;
        }

        return createEmailASO;
    }

    private CreateEmailASO buildVehicleEmailRequest(CreatedInsuranceDTO requestBody, RequiredFieldsEmissionDAO emissionDao, CreatedInsrcEventDAO createdInsrcEventDao) {
        String vehicleLayoutCode = "PLT00945";

        CreateEmailASO vehicleEmail = new CreateEmailASO();
        vehicleEmail.setApplicationId(vehicleLayoutCode.concat(format.format(new Date())));
        vehicleEmail.setRecipient("0,".concat(requestBody.getHolder().getContactDetails().get(0).getContact().getValue()));
        vehicleEmail.setSubject(this.applicationConfigurationService.getProperty(MAIL_SUJECT_VEHICLE));

        String[] data = this.getMailBodyDataVeh(requestBody, emissionDao, createdInsrcEventDao);

        vehicleEmail.setBody(this.getMailBodyVeh(data, vehicleLayoutCode));
        vehicleEmail.setSender(MAIL_SENDER);

        return vehicleEmail;
    }

    private CreateEmailASO buildHomeEmailRequest(CreatedInsuranceDTO requestBody, CreatedInsrcEventDAO createdInsrcEventDao,
                                                 SimltInsuredHousingDAO simltInsuredHousingDAO, String customerName, String riskDirection) {
        String homeLayoutCode = "PLT00968";

        CreateEmailASO homeEmail = new CreateEmailASO();
        homeEmail.setApplicationId(homeLayoutCode.concat(format.format(new Date())));
        homeEmail.setRecipient("0,".concat(requestBody.getHolder().getContactDetails().get(0).getContact().getValue()));
        homeEmail.setSubject(this.applicationConfigurationService.getProperty(MAIL_SUJECT_HOME));

        String[] data = this.getMailBodyDataHome(requestBody, createdInsrcEventDao, simltInsuredHousingDAO, customerName, riskDirection);

        homeEmail.setBody(this.getMailBodyHome(data, homeLayoutCode));
        homeEmail.setSender(MAIL_SENDER);

        return homeEmail;
    }

    private CreateEmailASO buildFlexiPymeEmailRequest(final CreatedInsuranceDTO requestBody, final CreatedInsrcEventDAO createdInsrcEventDao,
                                                      final SimltInsuredHousingDAO simltInsuredHousingDAO, final String riskDirection,
                                                      final CustomerBO customerInformation, final String legalName) {

        String flexiPymeCode = "PLT00991";

        CreateEmailASO email = new CreateEmailASO();
        email.setApplicationId(flexiPymeCode.concat(format.format(new Date())));

        String mainEmail = "";

        String customerName = "";

        if(nonNull(requestBody)){
            mainEmail=requestBody.getHolder().getContactDetails().stream()
                    .filter(contactDetail -> "EMAIL".equalsIgnoreCase(contactDetail.getContact().getContactType()))
                    .findFirst()
                    .map(ContactDetailDTO::getContact)
                    .map(ContactDTO::getValue)
                    .orElse(null);
        }

        if(nonNull(customerInformation)) {
            mainEmail=Objects.isNull(mainEmail) ? customerInformation.getContactDetails().stream()
                    .filter(contactDetail -> "EMAIL".equalsIgnoreCase(contactDetail.getContactType().getId()))
                    .findFirst()
                    .map(ContactDetailsBO::getContact)
                    .orElse(null) : mainEmail;

            customerName = nullToEmpty(customerInformation.getFirstName()) + " " +
                    nullToEmpty(customerInformation.getLastName()) + " " +
                    nullToEmpty(customerInformation.getSecondLastName());

            customerName = customerName.replace("#", "Ñ");
        }

        String incomingMail = requestBody.getHolder().getContactDetails().get(0).getContact().getValue();

        String emailToUse = BooleanUtils.toString(isEmpty(mainEmail), incomingMail, mainEmail);

        email.setRecipient("0,".concat(emailToUse));

        email.setSubject(this.applicationConfigurationService.getProperty(MAIL_SUJECT_FLEXIPYME));
        String[] data = this.getMailBodyDataFlexipyme(requestBody, createdInsrcEventDao,
                customerName, simltInsuredHousingDAO, riskDirection, legalName);
        email.setBody(getEmailBodySructure2(data,flexiPymeCode));
        email.setSender(MAIL_SENDER);
        return email;
    }

    private String[] getMailBodyDataFlexipyme(CreatedInsuranceDTO requestBody, CreatedInsrcEventDAO createdInsrcEventDao, String customerName,
                                              SimltInsuredHousingDAO homeInfo, String riskDirection, String legalName) {
        String[] bodyData = new String[13];

        if("P".equals(homeInfo.getHousingType())) {
            bodyData[0] = ObjectUtils.defaultIfNull(legalName, customerName) ;
            bodyData[2] = HolderTypeEnum.OWNER.getName();
        }else{
            bodyData[0] = customerName;
            bodyData[2] = HolderTypeEnum.TENANT.getName();
        }
        bodyData[1] = createdInsrcEventDao.getInsuranceModalityName();
        bodyData[3] = riskDirection;
        bodyData[4] = homeInfo.getPropSeniorityYearsNumber().toString();
        bodyData[5] = homeInfo.getFloorNumber().toString();
        bodyData[6] = this.getContractNumber(createdInsrcEventDao.getContractNumber());
        bodyData[7] = this.setPolicyNumber(createdInsrcEventDao.getRimacPolicy());
        Locale locale = new Locale ("en", "UK");
        NumberFormat numberFormat = NumberFormat.getInstance (locale);
        bodyData[8] = Objects.nonNull(homeInfo.getEdificationLoanAmount()) ? numberFormat.format(homeInfo.getEdificationLoanAmount()) : "";
        bodyData[9] = createdInsrcEventDao.getPeriodName();
        bodyData[10] = numberFormat.format(requestBody.getProduct().getPlan().getInstallmentPlans().get(0).getPaymentAmount().getAmount());

        String intAccountId = createdInsrcEventDao.getContractNumber().substring(10);

        Map<String, Object> parameterGettingLegalRep = singletonMap(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), intAccountId);

        Map<String, Object> responseGettingLegalRepresentant = this.pisdR012.executeGetASingleRow("PISD.SELECT_LEGAL_PARTICIPANT", parameterGettingLegalRep);

        if(nonNull(responseGettingLegalRepresentant)) {
            String documentType = (String) responseGettingLegalRepresentant.get(RBVDProperties.FIELD_PERSONAL_DOC_TYPE.getValue());
            bodyData[11] = this.applicationConfigurationService.getProperty(documentType);
            bodyData[12] = (String) responseGettingLegalRepresentant.get(RBVDProperties.FIELD_PARTICIPANT_PERSONAL_ID.getValue());
        } else {
            bodyData[11] = NONE;
            bodyData[12] = NONE;
        }

        return bodyData;
    }

    private String getEmailBodySructure2(String[] data, String emailCode) {
        StringBuilder body = new StringBuilder();
        for(int i = 0; i < data.length; i++) {
            body.append(generateCode(String.valueOf(i+1))).append(data[i]).append("|");
        }
        body.append(emailCode);
        return body.toString();
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
        bodyData[9] = this.setPolicyNumber(createdInsrcEventDao.getRimacPolicy());
        bodyData[10] = createdInsrcEventDao.getInsuranceModalityName();

        BigDecimal monthlyPay = valueOf(requestBody.getProduct().getPlan().getInstallmentPlans().get(0).getPaymentAmount().getAmount());

        bodyData[11] = "US$".concat(" ").concat(numberFormat.format(monthlyPay));
        bodyData[12] = createdInsrcEventDao.getPeriodName();

        return bodyData;
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

    private String[] getMailBodyDataHome(CreatedInsuranceDTO requestBody, CreatedInsrcEventDAO createdInsrcEventDao,
                                         SimltInsuredHousingDAO simltInsuredHousingDAO, String customerName, String riskDirection) {
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

        bodyData[12] = this.getContractNumber(createdInsrcEventDao.getContractNumber());
        bodyData[13] = this.setPolicyNumber(createdInsrcEventDao.getRimacPolicy());
        bodyData[14] = numberFormat.format(requestBody.getProduct().getPlan().getInstallmentPlans().get(0).getPaymentAmount().getAmount());
        bodyData[15] = createdInsrcEventDao.getPeriodName();
        bodyData[16] = createdInsrcEventDao.getInsuranceModalityName();
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

    private String getContractNumber(String id) {
        StringBuilder contract = new StringBuilder();
        contract.append(id, 0, 4).append("-")
                .append(id, 4, 8).append("-")
                .append(id, 8, 10).append("-")
                .append(id.substring(10));
        return contract.toString();
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

    private String setPolicyNumber(String policyNumber) {
        return nonNull(policyNumber) ? policyNumber : this.applicationConfigurationService.getProperty(IN_PROCCESS_KEY);
    }

    private String getLegalName(CreatedInsuranceDTO requestBody) {
        String documentType = requestBody.getHolder().getIdentityDocument().getDocumentType().getId();
        String documentNumber = requestBody.getHolder().getIdentityDocument().getDocumentNumber();
        if("RUC".equalsIgnoreCase(documentType) && StringUtils.startsWith(documentNumber, "20")) {
            String encryptedCode = this.httpClient.executeCypherService(new CypherASO(requestBody.getHolder().getId(), "apx-pe-fpextff1-do"));
            this.validateObject(encryptedCode);
            ListBusinessesASO listBussinesses = this.httpClient.executeGetListBusinesses(encryptedCode, null);
            this.validateObject(listBussinesses);
            return listBussinesses.getData().get(0).getLegalName();
        } else {
            return null;
        }
    }

    private void validateObject(Object obj) {
        if(isNull(obj)) {
            throw RBVDValidation.build(RBVDErrors.ERROR_CONNECTION_LIST_BUSINESSES_ASO);
        }
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

    private CreateEmailASO buildGeneralEmailRequest(CreatedInsuranceDTO requestBody, CreatedInsrcEventDAO createdInsrcEventDao, String customerName, String subject, String layoutCode) {
        CreateEmailASO generalEmail = new CreateEmailASO();
        generalEmail.setApplicationId(layoutCode.concat(format.format(new Date())));
        generalEmail.setRecipient("0,".concat(requestBody.getHolder().getContactDetails().get(0).getContact().getValue()));
        generalEmail.setSubject(subject);

        String[] data = this.getGeneralMailBodyData(requestBody, createdInsrcEventDao, customerName);

        generalEmail.setBody(this.getMailBodyHome(data, layoutCode));
        generalEmail.setSender(MAIL_SENDER);

        return generalEmail;
    }

    private String[] getGeneralMailBodyData(CreatedInsuranceDTO requestBody, CreatedInsrcEventDAO createdInsrcEventDao, String customerName) {

        String[] bodyData = new String[7];

        bodyData[0] = customerName;
        bodyData[1] = this.setPolicyNumber(createdInsrcEventDao.getRimacPolicy());
        bodyData[2] = createdInsrcEventDao.getInsuranceModalityName();
        bodyData[3] = createdInsrcEventDao.getInsuranceCompanyDesc();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        LocalDate startDate = new LocalDate(requestBody.getValidityPeriod().getStartDate(), DateTimeZone.forID("GMT"));
        LocalDate endDate = new LocalDate(requestBody.getValidityPeriod().getEndDate(), DateTimeZone.forID("GMT"));

        bodyData[4] = dateFormat.format(startDate.toDateTimeAtStartOfDay().toDate());
        bodyData[5] = dateFormat.format(endDate.toDateTimeAtStartOfDay().toDate());

        int beginIndex = requestBody.getPaymentMethod().getRelatedContracts().get(0).getNumber().length() - 4;

        bodyData[6] = MASK_VALUE.concat(requestBody.getPaymentMethod().getRelatedContracts().get(0).getNumber().substring(beginIndex));
        
        return bodyData;
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setPisdR021(PISDR021 pisdR021) {
        this.pisdR021 = pisdR021;
    }

    public void setPisdR012(PISDR012 pisdR012) {
        this.pisdR012 = pisdR012;
    }

}
