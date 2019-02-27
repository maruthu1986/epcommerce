/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.actions;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.model.DynamicContentDeliveryModelAdapter;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.sellingcontext.impl.SellingContextImpl;
import com.elasticpath.service.sellingcontext.SellingContextService;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.domain.impl.ConditionalExpressionImpl;
import com.elasticpath.tags.service.TagConditionService;

/**
 * Tests {@link SellingContextHelper}.
 */
@SuppressWarnings({ "restriction" })
public class SellingContextHelperTest {

	private static final String SELLING_CONTEXT_GUID = "sellingContextGuid"; //$NON-NLS-1$
	private static final String SHOPPER_CONDITION_GUID_ONE = "SHOPPER_CONDITION_GUID_ONE"; //$NON-NLS-1$
	private static final String TIME_CONDITION_GUID_ONE = "TIME_CONDITION_GUID_ONE"; //$NON-NLS-1$
	private static final String STORES_CONDITION_GUID_ONE = "STORES_CONDITION_GUID_ONE"; //$NON-NLS-1$
	private static final String SHOPPER_CONDITION_GUID_TWO = "SHOPPER_CONDITION_GUID_TWO"; //$NON-NLS-1$
	private static final String TIME_CONDITION_GUID_TWO = "TIME_CONDITION_GUID_TWO"; //$NON-NLS-1$
	private static final String STORES_CONDITION_GUID_TWO = "STORES_CONDITION_GUID_TWO"; //$NON-NLS-1$

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private BeanFactory beanFactory;

	@Mock
	private SellingContextService sellingContextService;

	@Mock
	private TagConditionService tagConditionService;

	@Mock
	private DynamicContentDeliveryModelAdapter dynamicContentDeliveryModelAdapter;

	/**
	 * Initialize mock objects.
	 */
	@Before
	public void initializeMockObjects() {
		ServiceLocator.setBeanFactory(beanFactory);

		when(beanFactory.getBean(ContextIdNames.TAG_CONDITION_SERVICE)).thenReturn(tagConditionService);
		when(beanFactory.getBean(ContextIdNames.SELLING_CONTEXT_SERVICE)).thenReturn(sellingContextService);
	}

	/**
	 * Delete selling context with null guid.
	 */
	@Test
	public void deleteSellingContextWithNullGuid() {
		when(sellingContextService.getByGuid(null)).thenReturn(null);
		SellingContextHelper.deleteSellingContextManually(null);
		verify(sellingContextService).getByGuid(null);
		verify(sellingContextService, never()).remove(any(SellingContext.class));
	}

	/**
	 * Do nothing on deletion of non existent selling context.
	 */
	@Test
	public void doNothingOnDeletionOfNonExistentSellingContext() {
		when(sellingContextService.getByGuid(SELLING_CONTEXT_GUID)).thenReturn(null);
		SellingContextHelper.deleteSellingContextManually(SELLING_CONTEXT_GUID);
		verify(sellingContextService).getByGuid(SELLING_CONTEXT_GUID);
		verify(sellingContextService, never()).remove(any(SellingContext.class));
	}

	/**
	 * Save persisted selling context with null conditions.
	 */
	@Test
	public void savePersistedSellingContextWithNullConditions() {
		final SellingContext transientSellingContext = getPersistedSellingContextWithNullConditions(SELLING_CONTEXT_GUID);

		when(dynamicContentDeliveryModelAdapter.getSellingContext()).thenReturn(transientSellingContext);
		when(sellingContextService.getByGuid(SELLING_CONTEXT_GUID)).thenReturn(transientSellingContext);

		SellingContextHelper.saveSellingContextManually(dynamicContentDeliveryModelAdapter);
		verify(sellingContextService).getByGuid(SELLING_CONTEXT_GUID);
		verify(dynamicContentDeliveryModelAdapter).clearSellingContext();
	}

	/**
	 * Save persisted selling context with not null, named conditions.
	 */
	@Test
	public void savePersistedSellingContextWithNotNullConditions() {

		final SellingContext persistedSellingContext = getPersistedSellingContextWithConditions(SELLING_CONTEXT_GUID,
				getNamedConditionalExpression(TagDictionary.DICTIONARY_SHOPPER_GUID),
				getNamedConditionalExpression(TagDictionary.DICTIONARY_TIME_GUID),
				getNamedConditionalExpression(TagDictionary.DICTIONARY_STORES_GUID));

		final SellingContext transientSellingContext = getPersistedSellingContextWithConditions(SELLING_CONTEXT_GUID,
				getNamedConditionalExpression(TagDictionary.DICTIONARY_SHOPPER_GUID),
				getNamedConditionalExpression(TagDictionary.DICTIONARY_TIME_GUID),
				getNamedConditionalExpression(TagDictionary.DICTIONARY_STORES_GUID));

		when(dynamicContentDeliveryModelAdapter.getSellingContext()).thenReturn(transientSellingContext);
		when(sellingContextService.getByGuid(SELLING_CONTEXT_GUID)).thenReturn(persistedSellingContext);
		when(sellingContextService.saveOrUpdate(persistedSellingContext)).thenReturn(persistedSellingContext);

		SellingContextHelper.saveSellingContextManually(dynamicContentDeliveryModelAdapter);
		verify(sellingContextService).getByGuid(SELLING_CONTEXT_GUID);
		verify(sellingContextService).saveOrUpdate(persistedSellingContext);
		verify(dynamicContentDeliveryModelAdapter).setSellingContext(persistedSellingContext);
	}

	/**
	 * Ensure unnamed conditionals are deleted.
	 */
	@Test
	public void ensureUnnamedConditionalsAreDeleted() {

		final ConditionalExpression unnamedShopperConditionalExpression = getUnnamedConditionalExpression(TagDictionary.DICTIONARY_SHOPPER_GUID);
		final ConditionalExpression unnamedTimeConditionalExpression = getUnnamedConditionalExpression(TagDictionary.DICTIONARY_TIME_GUID);
		final ConditionalExpression unnamedStoresConditionalExpression = getUnnamedConditionalExpression(TagDictionary.DICTIONARY_STORES_GUID);

		final SellingContext persistedSellingContext = getPersistedSellingContextWithConditions(SELLING_CONTEXT_GUID,
				unnamedShopperConditionalExpression,
				unnamedTimeConditionalExpression,
				unnamedStoresConditionalExpression);

		final ConditionalExpression namedShopperConditionalExpression = getNamedConditionalExpression(TagDictionary.DICTIONARY_SHOPPER_GUID);
		final ConditionalExpression namedTimeConditionalExpression = getNamedConditionalExpression(TagDictionary.DICTIONARY_TIME_GUID);
		final ConditionalExpression namedStoresConditionalExpression = getNamedConditionalExpression(TagDictionary.DICTIONARY_STORES_GUID);
		final SellingContext transientSellingContext = getPersistedSellingContextWithConditions(SELLING_CONTEXT_GUID,
				namedShopperConditionalExpression,
				namedTimeConditionalExpression,
				namedStoresConditionalExpression);

		when(dynamicContentDeliveryModelAdapter.getSellingContext()).thenReturn(transientSellingContext);
		when(sellingContextService.getByGuid(SELLING_CONTEXT_GUID)).thenReturn(persistedSellingContext);
		when(sellingContextService.saveOrUpdate(persistedSellingContext)).thenReturn(persistedSellingContext);

		SellingContextHelper.saveSellingContextManually(dynamicContentDeliveryModelAdapter);
		verify(sellingContextService).getByGuid(SELLING_CONTEXT_GUID);
		verify(tagConditionService).delete(unnamedShopperConditionalExpression);
		verify(tagConditionService).delete(unnamedTimeConditionalExpression);
		verify(tagConditionService).delete(unnamedStoresConditionalExpression);
		verify(sellingContextService).saveOrUpdate(persistedSellingContext);
		verify(dynamicContentDeliveryModelAdapter).setSellingContext(persistedSellingContext);
	}

	/**
	 * Ensure unnamed conditionals are replaced with named conditionals.
	 */
	@Test
	public void ensureUnnamedConditionalsAreReplacedWithNamedConditionals() {
		final ConditionalExpression namedShopperConditionalExpression = getNamedConditionalExpression(TagDictionary.DICTIONARY_SHOPPER_GUID);
		final ConditionalExpression namedTimeConditionalExpression = getNamedConditionalExpression(TagDictionary.DICTIONARY_TIME_GUID);
		final ConditionalExpression namedStoresConditionalExpression = getNamedConditionalExpression(TagDictionary.DICTIONARY_STORES_GUID);

		final SellingContext transientSellingContext = getUnpersistedSellingContextWithConditions(SELLING_CONTEXT_GUID,
				namedShopperConditionalExpression,
				namedTimeConditionalExpression,
				namedStoresConditionalExpression);

		when(dynamicContentDeliveryModelAdapter.getSellingContext()).thenReturn(transientSellingContext);
		when(sellingContextService.saveOrUpdate(any(SellingContext.class))).thenReturn(transientSellingContext);

		SellingContextHelper.saveSellingContextManually(dynamicContentDeliveryModelAdapter);
	}


	/**
	 * Ensure old conditionals are replaced with new ones when guids differ.
	 */
	@Test
	public void ensureConditionalsWithDifferingGuidsAreReplaced() {
		final SellingContext persistedSellingContext = getPersistedSellingContextWithConditions(SELLING_CONTEXT_GUID,
				getUnnamedConditionalExpression(SHOPPER_CONDITION_GUID_ONE),
				getUnnamedConditionalExpression(TIME_CONDITION_GUID_ONE),
				getUnnamedConditionalExpression(STORES_CONDITION_GUID_ONE));

		final SellingContext transientSellingContext = getPersistedSellingContextWithConditions(SELLING_CONTEXT_GUID,
				getUnnamedConditionalExpression(SHOPPER_CONDITION_GUID_TWO),
				getUnnamedConditionalExpression(TIME_CONDITION_GUID_TWO),
				getUnnamedConditionalExpression(STORES_CONDITION_GUID_TWO));

		when(dynamicContentDeliveryModelAdapter.getSellingContext()).thenReturn(transientSellingContext);
		when(sellingContextService.getByGuid(SELLING_CONTEXT_GUID)).thenReturn(persistedSellingContext);

//			allowing(tagConditionService).delete(with(any(ConditionalExpression.class)));

		when(sellingContextService.saveOrUpdate(persistedSellingContext)).thenReturn(persistedSellingContext);

		SellingContextHelper.saveSellingContextManually(dynamicContentDeliveryModelAdapter);
		verify(sellingContextService).getByGuid(SELLING_CONTEXT_GUID);
		verify(sellingContextService).saveOrUpdate(persistedSellingContext);
		verify(dynamicContentDeliveryModelAdapter).setSellingContext(persistedSellingContext);
	}

	/**
	 * Ensure a conditional is cleared if its replacement is null.
	 */
	@Test
	public void ensureAConditionalsIsClearedIfReplacementIsNull() {
		final ConditionalExpression unnamedShopperConditionalExpression = getUnnamedConditionalExpression(SHOPPER_CONDITION_GUID_ONE);
		final SellingContext persistedSellingContext = getPersistedSellingContextWithConditions(SELLING_CONTEXT_GUID,
				unnamedShopperConditionalExpression,
				getUnnamedConditionalExpression(TIME_CONDITION_GUID_ONE),
				getUnnamedConditionalExpression(STORES_CONDITION_GUID_ONE));

		final SellingContext transientSellingContext = getPersistedSellingContextWithConditions(SELLING_CONTEXT_GUID,
				unnamedShopperConditionalExpression,
				getUnnamedConditionalExpression(TIME_CONDITION_GUID_ONE),
				getUnnamedConditionalExpression(STORES_CONDITION_GUID_ONE));

		transientSellingContext.setCondition(TagDictionary.DICTIONARY_SHOPPER_GUID, null);

		when(dynamicContentDeliveryModelAdapter.getSellingContext()).thenReturn(transientSellingContext);
		when(sellingContextService.getByGuid(SELLING_CONTEXT_GUID)).thenReturn(persistedSellingContext);

//				allowing(tagConditionService).delete(unnamedShopperConditionalExpression);
		when(sellingContextService.saveOrUpdate(persistedSellingContext)).thenReturn(persistedSellingContext);

		SellingContextHelper.saveSellingContextManually(dynamicContentDeliveryModelAdapter);
		verify(sellingContextService).getByGuid(SELLING_CONTEXT_GUID);
		verify(sellingContextService).saveOrUpdate(persistedSellingContext);
		verify(dynamicContentDeliveryModelAdapter).setSellingContext(persistedSellingContext);
	}

	/**
	 * Do not save selling context if null.
	 */
	@Test
	public void doNotSaveSellingContextIfNull() {
		when(dynamicContentDeliveryModelAdapter.getSellingContext()).thenReturn(null);

		SellingContextHelper.saveSellingContextManually(dynamicContentDeliveryModelAdapter);
		verify(dynamicContentDeliveryModelAdapter).getSellingContext();
	}

	/**
	 * Clear selling context if conditions are null.
	 */
	@Test
	public void saveUnpersistedSellingContextWithNullConditions() {
		when(dynamicContentDeliveryModelAdapter.getSellingContext()).thenReturn(getUnpersistedSellingContextWithNullConditions(SELLING_CONTEXT_GUID));

		SellingContextHelper.saveSellingContextManually(dynamicContentDeliveryModelAdapter);
		verify(dynamicContentDeliveryModelAdapter).getSellingContext();
		verify(dynamicContentDeliveryModelAdapter).clearSellingContext();
	}

	private ConditionalExpression getNamedConditionalExpression(final String guid) {
		ConditionalExpression conditionalExpression = getUnnamedConditionalExpression(guid);
		conditionalExpression.setNamed(true);
		conditionalExpression.setName(guid);
		return conditionalExpression;
	}

	private ConditionalExpression getUnnamedConditionalExpression(final String guid) {
		ConditionalExpression conditionalExpression = new ConditionalExpressionImpl();
		conditionalExpression.setNamed(false);
		conditionalExpression.setGuid(guid);
		conditionalExpression.setUidPk(1);
		return conditionalExpression;
	}

	private SellingContext getPersistedSellingContextWithConditions(final String sellingContextGuid, final ConditionalExpression shopperCondition,
			final ConditionalExpression timeCondition, final ConditionalExpression storesCondition) {
		SellingContext sellingContext = getPersistedSellingContextWithNullConditions(sellingContextGuid);

		sellingContext.setCondition(TagDictionary.DICTIONARY_SHOPPER_GUID, shopperCondition);
		sellingContext.setCondition(TagDictionary.DICTIONARY_TIME_GUID, timeCondition);
		sellingContext.setCondition(TagDictionary.DICTIONARY_STORES_GUID, storesCondition);
		return sellingContext;
	}

	private SellingContext getUnpersistedSellingContextWithConditions(final String sellingContextGuid, final ConditionalExpression shopperCondition,
			final ConditionalExpression timeCondition, final ConditionalExpression storesCondition) {
		SellingContext sellingContext = getPersistedSellingContextWithConditions(sellingContextGuid, shopperCondition,
				timeCondition, storesCondition);
		sellingContext.setUidPk(0);
		return sellingContext;
	}

	private SellingContext getPersistedSellingContextWithNullConditions(final String sellingContextGuid) {
		SellingContext sellingContext = getUnpersistedSellingContextWithNullConditions(sellingContextGuid);
		sellingContext.setUidPk(1);
		return sellingContext;
	}

	private SellingContext getUnpersistedSellingContextWithNullConditions(final String sellingContextGuid) {
		SellingContext sellingContext = new SellingContextImpl();
		sellingContext.setGuid(sellingContextGuid);
		return sellingContext;
	}

}
