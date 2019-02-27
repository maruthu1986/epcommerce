/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.payment.converter;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Currency;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.order.impl.OrderAddressImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.ShoppingCartDto;
import com.elasticpath.plugin.payment.dto.impl.AddressDtoImpl;
import com.elasticpath.plugin.payment.dto.impl.ShoppingCartDtoImpl;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;
import com.elasticpath.test.factory.ShoppingCartStubBuilder;

public class ShoppingCartToDtoTest {
	private static final String CURRENCYCODE = "USD";
	private static final Currency USD = Currency.getInstance(CURRENCYCODE);
	private static final Money TOTAL = Money.valueOf(new BigDecimal("100.25"), USD);
	private static final boolean REQUIRES_SHIPPING = false;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;

	private BeanFactoryExpectationsFactory expectationsFactory;
	private final ShoppingCartToDto shoppingCartToDto = new ShoppingCartToDto();
	private final AddressDto addressDto = new AddressDtoImpl();
	@Mock private ConversionService mockConversionService;
	@Mock private PricingSnapshotService pricingSnapshotService;

	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CONVERSION_SERVICE, mockConversionService);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.SHOPPING_CART_DTO, ShoppingCartDtoImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICING_SNAPSHOT_SERVICE, pricingSnapshotService);
		shoppingCartToDto.setBeanFactory(beanFactory);
	}

	@Test
	public void testConvert() throws Exception {
		Address address = new OrderAddressImpl();
		ShoppingCart source = ShoppingCartStubBuilder.aCart(context)
				.withCurrency(Currency.getInstance(CURRENCYCODE))
				.withShippingAddress(address)
				.withRequiresShipping(REQUIRES_SHIPPING)
				.build();

		context.checking(new Expectations() {
			{
				final ShoppingCartPricingSnapshot pricingSnapshot = context.mock(ShoppingCartPricingSnapshot.class);

				oneOf(pricingSnapshotService).getPricingSnapshotForCart(source);
				will(returnValue(pricingSnapshot));

				oneOf(pricingSnapshot).getBeforeTaxTotal();
				will(returnValue(TOTAL));

				oneOf(mockConversionService).convert(address, AddressDto.class);
				will(returnValue(addressDto));
			}
		});

		ShoppingCartDto target = shoppingCartToDto.convert(source);
		assertEquals(CURRENCYCODE, target.getCurrencyCode());
		assertEquals(TOTAL.getAmount(), target.getTotalAmount());
		assertEquals(addressDto, target.getShippingAddress());
		assertEquals(REQUIRES_SHIPPING, target.isRequiresShipping());
	}
}
