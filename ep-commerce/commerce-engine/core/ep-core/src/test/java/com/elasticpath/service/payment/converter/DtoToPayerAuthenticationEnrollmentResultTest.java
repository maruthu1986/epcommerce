/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.payment.converter;

import static org.junit.Assert.assertEquals;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.PayerAuthenticationEnrollmentResult;
import com.elasticpath.domain.misc.impl.PayerAuthenticationEnrollmentResultImpl;
import com.elasticpath.plugin.payment.dto.PayerAuthenticationEnrollmentResultDto;
import com.elasticpath.plugin.payment.dto.impl.PayerAuthenticationEnrollmentResultDtoImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

public class DtoToPayerAuthenticationEnrollmentResultTest {
	private static final String PAREQ = "paReq";
	private static final String ACSURL = "acsUrl";
	private static final String MERCHANTDATA = "merchantData";
	private static final String TERMURL = "termUrl";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private final DtoToPayerAuthenticationEnrollmentResult dtoToPayerAuthenticationEnrollmentResult = new DtoToPayerAuthenticationEnrollmentResult();


	/**
	 * Set up and mocking.
	 */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PAYER_AUTHENTICATION_ENROLLMENT_RESULT,
				PayerAuthenticationEnrollmentResultImpl.class);
		dtoToPayerAuthenticationEnrollmentResult.setBeanFactory(beanFactory);
	}

	@Test
	public void testConvert() throws Exception {
		PayerAuthenticationEnrollmentResultDto source = new PayerAuthenticationEnrollmentResultDtoImpl();
		source.setPaREQ(PAREQ);
		source.setAcsURL(ACSURL);
		source.setMerchantData(MERCHANTDATA);
		source.setTermURL(TERMURL);

		PayerAuthenticationEnrollmentResult target = dtoToPayerAuthenticationEnrollmentResult.convert(source);
		assertEquals(TERMURL, target.getTermURL());
		assertEquals(MERCHANTDATA, target.getMerchantData());
		assertEquals(ACSURL, target.getAcsURL());
		assertEquals(PAREQ, target.getPaREQ());
	}
}
