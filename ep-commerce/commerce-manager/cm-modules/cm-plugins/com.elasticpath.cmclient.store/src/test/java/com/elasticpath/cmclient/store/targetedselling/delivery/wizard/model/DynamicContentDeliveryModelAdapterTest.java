/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.wizard.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.cmclient.core.ServiceLocator;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.sellingcontext.impl.SellingContextImpl;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.domain.targetedselling.impl.DynamicContentDeliveryImpl;
import com.elasticpath.service.sellingcontext.SellingContextService;

/**
 * Tests clearing {@link SellingContext} on {@link DynamicContentDeliveryModelAdapter}.
 */
@SuppressWarnings({ "restriction" })
public class DynamicContentDeliveryModelAdapterTest {

	private static final String SELLING_CONTEXT_SHOULD_BE_NULL = "Selling Context should be null"; //$NON-NLS-1$
	private static final String SELLING_CONTEXT_SHOULD_NOT_BE_NULL = "Selling Context should not be null"; //$NON-NLS-1$
	private static final String DYNAMIC_CONTENT_DELIVERY_NAME = "dynamicContentDeliveryName"; //$NON-NLS-1$
	private static final String DYNAMIC_CONTENT_DELIVERY_GUID = "dynamicContentDeliveryGuid"; //$NON-NLS-1$
	private static final String SELLING_CONTEXT_GUID = "sellingContextGuid"; //$NON-NLS-1$
	private DynamicContentDelivery dynamicContentDelivery;
	private SellingContext sellingContext;
	private DynamicContentDeliveryModelAdapter dynamicContentDeliveryModelAdapter;

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private BeanFactory beanFactory;

	@Mock
	private SellingContextService sellingContextService;

	/**
	 * Initialize mock objects.
	 */
	@Before
	public void initializeMockObjects() {

		ServiceLocator.setBeanFactory(beanFactory);
		when(beanFactory.getBean(ContextIdNames.SELLING_CONTEXT_SERVICE)).thenReturn(sellingContextService);

		sellingContext = new SellingContextImpl();
		sellingContext.setGuid(SELLING_CONTEXT_GUID);
		dynamicContentDelivery = new DynamicContentDeliveryImpl();
		dynamicContentDelivery.setGuid(DYNAMIC_CONTENT_DELIVERY_GUID);
		dynamicContentDelivery.setName(DYNAMIC_CONTENT_DELIVERY_NAME);
		dynamicContentDelivery.setSellingContext(sellingContext);

		when(sellingContextService.getByGuid(SELLING_CONTEXT_GUID)).thenReturn(sellingContext);

		dynamicContentDeliveryModelAdapter = new DynamicContentDeliveryModelAdapter(dynamicContentDelivery);
	}

	/**
	 * Check clear selling context set selling context to null.
	 */
	@Test
	public void checkClearSellingContextSetSellingContextToNull() {
		assertEquals(SELLING_CONTEXT_SHOULD_NOT_BE_NULL, dynamicContentDeliveryModelAdapter.getDynamicContentDelivery().getSellingContext(),
				sellingContext);
		dynamicContentDeliveryModelAdapter.clearSellingContext();
		assertNull(SELLING_CONTEXT_SHOULD_BE_NULL, dynamicContentDeliveryModelAdapter.getDynamicContentDelivery().getSellingContext()); 
	}
}
