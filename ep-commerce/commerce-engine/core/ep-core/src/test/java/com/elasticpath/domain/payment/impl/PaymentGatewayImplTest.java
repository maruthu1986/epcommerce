/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.payment.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.payment.PaymentGatewayFactory;
import com.elasticpath.domain.payment.PaymentGatewayProperty;
import com.elasticpath.domain.store.CreditCardType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.CreditCardTypeImpl;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.plugin.payment.PaymentGatewayPlugin;
import com.elasticpath.plugin.payment.capabilities.CreditCardCapability;
import com.elasticpath.plugin.payment.capabilities.DirectPostAuthCapability;
import com.elasticpath.plugin.payment.capabilities.FinalizeShipmentCapability;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.dto.impl.OrderShipmentDtoImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test cases for <code>PaymentGatewayImpl</code>.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class PaymentGatewayImplTest {

	private static final String TEST_PAYMENT_GATEWAY_TYPE = "test payment gateway type";
	private static final String DEFAULT_PROPERTY_VALUE = "default property value";
	private static final String DEFAULT_PROPERTY_KEY = "default property key";
	private static final String GATEWAY_NAME = "test gateway name";
	private static final String CARD_TYPE_VISA = "Visa";
	private static final String CARD_TYPE_MASTERCARD = "MasterCard";
	private static final String CARD_TYPE_AMEX = "American Express";
	private static final String CARD_TYPE_FAKE = "Fake Card Type";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private PaymentGatewayImpl gateway;
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private PaymentGatewayFactory paymentGatewayFactory;
	private final Map<String, PaymentGatewayProperty> propertiesMap = createDefaultPropertiesMap();
	@Mock private PaymentGatewayPlugin mockPaymentGatewayPluginInvoker;
	@Mock private ConversionService mockConversionService;

	/**
	 * Prepare for the tests.
	 *
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		paymentGatewayFactory = context.mock(PaymentGatewayFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		gateway = new PaymentGatewayImpl();
		gateway.setType(TEST_PAYMENT_GATEWAY_TYPE);
		gateway.setPropertiesMap(propertiesMap);

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PAYMENT_GATEWAY_FACTORY, paymentGatewayFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CONVERSION_SERVICE, mockConversionService);

		context.checking(new Expectations() {
			{
				allowing(paymentGatewayFactory).createConfiguredPaymentGatewayPlugin(with(TEST_PAYMENT_GATEWAY_TYPE), with(propertiesMap));
				will(returnValue(mockPaymentGatewayPluginInvoker));
			}
		});

	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}


	/**
	 * Test name can be set and retrieved.
	 */
	@Test
	public void testNameCanBeSetAndRetrieved() {
		gateway.setName(GATEWAY_NAME);
		assertEquals(GATEWAY_NAME, gateway.getName());
	}

	/**
	 * Test supported currencies can be set and retrieved.
	 */
	@Test
	public void testSupportedCurrenciesCanBeSetAndRetrieved() {
		List<String> currencyList = Arrays.asList("CAD", "USD");
		gateway.setSupportedCurrencies(currencyList);
		assertEquals(currencyList, gateway.getSupportedCurrencies());
	}

	/**
	 * Ensure finalize shipment ignores shipment when capability is not present.
	 */
	@Test
	public void ensureFinalizeShipmentIgnoresShipmentWhenCapabilityIsNotPresent() {
		final OrderShipment mockOrderShipment = context.mock(OrderShipment.class);
		context.checking(new Expectations() {
			{
				oneOf(mockPaymentGatewayPluginInvoker).getCapability(FinalizeShipmentCapability.class);
				will(returnValue(null));

				oneOf(mockOrderShipment).getShipmentNumber();
			}
		});

		gateway.finalizeShipment(mockOrderShipment);
	}

	/**
	 * Ensure finalize shipment delegates to capability.
	 */
	@Test
	public void ensureFinalizeShipmentDelegatesToCapability() {
		final OrderShipment mockOrderShipment = context.mock(OrderShipment.class);

		final FinalizeShipmentCapability mockFinalizeShipmentCapability = context.mock(FinalizeShipmentCapability.class);

		context.checking(new Expectations() {
			{
				oneOf(mockPaymentGatewayPluginInvoker).getCapability(FinalizeShipmentCapability.class);
				will(returnValue(mockFinalizeShipmentCapability));

				ignoring(mockOrderShipment);

				final OrderShipmentDtoImpl orderShipmentDto = new OrderShipmentDtoImpl();
				allowing(mockConversionService).convert(mockOrderShipment, OrderShipmentDto.class);
				will(returnValue(orderShipmentDto));

				oneOf(mockFinalizeShipmentCapability).finalizeShipment(orderShipmentDto);

			}
		});

		gateway.finalizeShipment(mockOrderShipment);

	}

	private Map<String, PaymentGatewayProperty> createDefaultPropertiesMap() {
		Map<String, PaymentGatewayProperty> propertiesMap = new TreeMap<>();
		PaymentGatewayProperty value = new PaymentGatewayPropertyImpl();
		value.setKey(DEFAULT_PROPERTY_KEY);
		value.setValue(DEFAULT_PROPERTY_VALUE);
		propertiesMap.put(DEFAULT_PROPERTY_KEY, value);

		return propertiesMap;
	}

	@Test
	public void testGetEnabledCardTypesWithStandardAuthPlugin() {
		context.checking(new Expectations() {
			{
				CreditCardCapability mockCapability = context.mock(CreditCardCapability.class);
				oneOf(mockCapability).getSupportedCardTypes();
				will(returnValue(Arrays.asList(CARD_TYPE_VISA, CARD_TYPE_MASTERCARD, CARD_TYPE_AMEX)));

				oneOf(mockPaymentGatewayPluginInvoker).getCapability(CreditCardCapability.class);
				will(returnValue(mockCapability));
				oneOf(mockPaymentGatewayPluginInvoker).getCapability(DirectPostAuthCapability.class);
				will(returnValue(null));
			}
		});

		Store store = new StoreImpl();
		Set<CreditCardType> creditCardTypes = new HashSet<>();
		creditCardTypes.add(createCreditCardType(CARD_TYPE_VISA));
		creditCardTypes.add(createCreditCardType(CARD_TYPE_AMEX));
		creditCardTypes.add(createCreditCardType(CARD_TYPE_FAKE));
		store.setCreditCardTypes(creditCardTypes);

		Set<String> enabledCardTypes = gateway.getEnabledCardTypes(store).keySet();

		assertEquals(2, enabledCardTypes.size());
		assertTrue(enabledCardTypes.contains(CARD_TYPE_VISA));
		assertTrue(enabledCardTypes.contains(CARD_TYPE_AMEX));
		assertFalse(enabledCardTypes.contains(CARD_TYPE_MASTERCARD));
		assertFalse(enabledCardTypes.contains(CARD_TYPE_FAKE));
	}

	@Test
	public void testGetEnabledCardTypesWithDirectPostPlugin() {
		context.checking(new Expectations() {
			{
				CreditCardCapability creditCardCapability = context.mock(CreditCardCapability.class);
				oneOf(creditCardCapability).getSupportedCardTypes();
				will(returnValue(Arrays.asList(CARD_TYPE_VISA, CARD_TYPE_MASTERCARD, CARD_TYPE_AMEX)));

				DirectPostAuthCapability directPostAuthCapability = context.mock(DirectPostAuthCapability.class);
				oneOf(directPostAuthCapability).getCardTypeInternalCode(CARD_TYPE_VISA);
				will(returnValue("001"));
				oneOf(directPostAuthCapability).getCardTypeInternalCode(CARD_TYPE_AMEX);
				will(returnValue("002"));

				oneOf(mockPaymentGatewayPluginInvoker).getCapability(CreditCardCapability.class);
				will(returnValue(creditCardCapability));
				oneOf(mockPaymentGatewayPluginInvoker).getCapability(DirectPostAuthCapability.class);
				will(returnValue(directPostAuthCapability));
			}
		});

		Store store = new StoreImpl();
		Set<CreditCardType> creditCardTypes = new HashSet<>();
		creditCardTypes.add(createCreditCardType(CARD_TYPE_VISA));
		creditCardTypes.add(createCreditCardType(CARD_TYPE_AMEX));
		creditCardTypes.add(createCreditCardType(CARD_TYPE_FAKE));
		store.setCreditCardTypes(creditCardTypes);

		Set<String> enabledCardTypes = gateway.getEnabledCardTypes(store).keySet();

		assertEquals(2, enabledCardTypes.size());
		assertTrue(enabledCardTypes.contains(CARD_TYPE_VISA));
		assertTrue(enabledCardTypes.contains(CARD_TYPE_AMEX));
		assertFalse(enabledCardTypes.contains(CARD_TYPE_MASTERCARD));
		assertFalse(enabledCardTypes.contains(CARD_TYPE_FAKE));
	}

	private CreditCardType createCreditCardType(final String cardType) {
		CreditCardType result = new CreditCardTypeImpl();
		result.setCreditCardType(cardType);
		return result;
	}
}
