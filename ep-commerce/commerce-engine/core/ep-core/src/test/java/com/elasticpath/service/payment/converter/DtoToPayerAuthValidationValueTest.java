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
import com.elasticpath.domain.misc.PayerAuthValidationValue;
import com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl;
import com.elasticpath.plugin.payment.dto.PayerAuthValidationValueDto;
import com.elasticpath.plugin.payment.dto.impl.PayerAuthValidationValueDtoImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

public class DtoToPayerAuthValidationValueTest {
	private static final String PARES = "paRes";
	private static final boolean VALIDATED = true;
	private static final String XID = "xId";
	private static final String CAVV = "cavv";
	private static final String AAV = "aav";
	private static final String COMMERCEINDICATOR = "commerceIndicator";
	private static final String ECI = "eci";
	private static final String UCAFCOLLECTIONINDICATOR = "ucaCollectionIndicator";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private final DtoToPayerAuthValidationValue dtoToPayerAuthValidationValue = new DtoToPayerAuthValidationValue();

	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PAYER_AUTH_VALIDATION_VALUE, PayerAuthValidationValueImpl.class);
		dtoToPayerAuthValidationValue.setBeanFactory(beanFactory);
	}

	@Test
	public void testConvert() throws Exception {
		PayerAuthValidationValueDto source = new PayerAuthValidationValueDtoImpl();
		source.setPaRES(PARES);
		source.setValidated(VALIDATED);
		source.setXID(XID);
		source.setCAVV(CAVV);
		source.setAAV(AAV);
		source.setCommerceIndicator(COMMERCEINDICATOR);
		source.setECI(ECI);
		source.setUcafCollectionIndicator(UCAFCOLLECTIONINDICATOR);

		PayerAuthValidationValue target = dtoToPayerAuthValidationValue.convert(source);
		assertEquals(PARES, target.getPaRES());
		assertEquals(VALIDATED, target.isValidated());
		assertEquals(XID, target.getXID());
		assertEquals(CAVV, target.getCAVV());
		assertEquals(AAV, target.getAAV());
		assertEquals(COMMERCEINDICATOR, target.getCommerceIndicator());
		assertEquals(ECI, target.getECI());
		assertEquals(UCAFCOLLECTIONINDICATOR, target.getUcafCollectionIndicator());
	}
}
