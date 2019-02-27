/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.commons.util.impl.StoreThemeMessageSource;
import com.elasticpath.domain.catalogview.impl.SeoUrlBuilderImpl;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.email.domain.impl.EmailPropertiesImpl;
import com.elasticpath.money.MoneyFormatter;

/**
 * Unit test for {@link EmailContextFactoryImpl}.
 */
public class EmailContextFactoryImplTest {
	@Rule
	public JUnitRuleMockery context = new JUnitRuleMockery();

	private EmailContextFactoryImpl contextFactory;
	private StoreImpl store;
	private EmailPropertiesImpl emailProperties;
	private StoreThemeMessageSource storeThemeMessageSource;
	private BeanFactory beanFactory;
	private MoneyFormatter moneyFormatter;
	private Geography geography;

	@Before
	public void setUp() throws Exception {
		context.setImposteriser(ClassImposteriser.INSTANCE);

		beanFactory = context.mock(BeanFactory.class);
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.SEO_URL_BUILDER);
				will(returnValue(new SeoUrlBuilderImpl()));
			}
		});

		geography = context.mock(Geography.class);
		moneyFormatter = context.mock(MoneyFormatter.class);
		storeThemeMessageSource = context.mock(StoreThemeMessageSource.class);

		contextFactory = new EmailContextFactoryImpl();
		contextFactory.setBeanFactory(beanFactory);
		contextFactory.setGeography(geography);
		contextFactory.setMoneyFormatter(moneyFormatter);
		contextFactory.setStoreThemeMessageSource(storeThemeMessageSource);

		store = new StoreImpl();
		store.setCode("store");

		emailProperties = new EmailPropertiesImpl();
	}

	/**
	 * testCreateVelocityContextInjectsServices.
	 */
	@Test
	public void testCreateVelocityContextInjectsServices() {
		Map<String, Object> context = contextFactory.createVelocityContext(store, emailProperties);

		assertSame("Context should be created with spring-injected services", moneyFormatter, context.get(WebConstants.MONEY_FORMATTER));
		assertNotNull("Geography should be on the map", context.get(WebConstants.GEOGRAPHY_HELPER));
	}

	/**
	 * testCreateVelocityContextInjectsStoreInfo.
	 */
	@Test
	public void testCreateVelocityContextInjectsStoreInfo() {
		//  Given
		emailProperties.setStoreCode(store.getCode());
		store.setUrl("/snap-it-up/");

		//  When
		Map<String, Object> context = contextFactory.createVelocityContext(store, emailProperties);

		//  Then
		assertSame("Context should include store", store, context.get("store"));
		assertEquals("Context should include store url", store.getUrl(), context.get("baseImgUrl"));
		assertEquals("Context should include trimmed store url", "/snap-it-up", context.get("storeUrl"));
	}
}
