package com.bbva.rbvd;

import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
import com.bbva.elara.domain.transaction.TransactionParameter;

import com.bbva.elara.domain.transaction.request.TransactionRequest;
import com.bbva.elara.domain.transaction.request.body.CommonRequestBody;
import com.bbva.elara.domain.transaction.request.header.CommonRequestHeader;

import com.bbva.elara.test.osgi.DummyBundleContext;

import java.util.ArrayList;
import java.util.Calendar;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsuranceDTO;
import com.bbva.rbvd.lib.r221.RBVDR221;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyObject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/elara-test.xml",
		"classpath:/META-INF/spring/RBVDT21101PETest.xml" })
public class RBVDT21101PETransactionTest {

	@Autowired
	private RBVDT21101PETransaction transaction;

	@Autowired
	private DummyBundleContext bundleContext;

	@Autowired
	private RBVDR221 rbvdR221;

	@Mock
	private CommonRequestHeader header;

	@Mock
	private TransactionRequest transactionRequest;

	@Before
	public void initializeClass() throws Exception {

		MockitoAnnotations.initMocks(this);

		this.transaction.start(bundleContext);
		this.transaction.setContext(new Context());

		CommonRequestBody commonRequestBody = new CommonRequestBody();
		commonRequestBody.setTransactionParameters(new ArrayList<>());

		this.transactionRequest.setBody(commonRequestBody);

		when(this.header.getHeaderParameter(RequestHeaderParamsName.AAP)).thenReturn("13000004");

		addParameter("createdInsurance", new CreatedInsuranceDTO());

		this.transactionRequest.setHeader(header);
		this.transaction.getContext().setTransactionRequest(transactionRequest);
	}

	@Test
	public void testTrue() {
		when(this.rbvdR221.executeCreatedInsrcEvent(anyObject())).
				thenReturn(true);
		this.transaction.execute();
	}

	@Test
	public void testFalse() {
		when(this.rbvdR221.executeCreatedInsrcEvent(anyObject())).
				thenReturn(false);
		this.transaction.execute();
	}

	private void addParameter(final String parameter, final Object value) {
		final TransactionParameter tParameter = new TransactionParameter(parameter, value);
		transaction.getContext().getParameterList().put(parameter, tParameter);
	}

}
