/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.order.helper.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import static com.elasticpath.commons.constants.ContextIdNames.EMAIL_PROPERTIES;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.domain.impl.EmailPropertiesImpl;
import com.elasticpath.service.store.StoreService;

/**
 * Test class for {@link OrderEmailPropertyHelperImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderEmailPropertyHelperImplTest {

	@Mock
	private StoreService storeService;

	@Mock
	private BeanFactory beanFactory;

	private Store store;
	private Order order;

	@InjectMocks
	private OrderEmailPropertyHelperImpl helper;

	@Before
	public void setUp() {
		store = new StoreImpl();
		store.setCode("store");
		store.setStoreAdminEmailAddress("barney.rubble@flintstones.com");

		order = new OrderImpl();
		order.setStoreCode(store.getCode());

		when(storeService.findStoreWithCode(store.getCode()))
				.thenReturn(store);

		when(beanFactory.getBean(EMAIL_PROPERTIES))
				.thenReturn(new EmailPropertiesImpl());
	}

	@Test
	public void testGetFailedShipmentPaymentEmailProperties() {
		OrderShipment shipment = new PhysicalOrderShipmentImpl();
		shipment.setOrder(order);

		EmailProperties props = helper.getFailedShipmentPaymentEmailProperties(shipment, "Oh noze!");

		assertThat(props.getStoreCode())
				.as("Store code should be populated")
				.isEqualTo(store.getCode());

		assertThat(props.getRecipientAddress())
				.as("Should send email to store admin")
				.isEqualTo(store.getStoreAdminEmailAddress());
	}

}
