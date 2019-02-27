/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.actions;

import org.apache.commons.collections.MapUtils;

import com.elasticpath.cmclient.conditionbuilder.wizard.pages.AbstractSellingContextAdapter;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.service.sellingcontext.SellingContextService;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.service.TagConditionService;

/**
 * helper to deal with selling context manually when we work with delivery objects.
 * this class to be removed when we add separate wizards for selling context and conditions.
 */
public final class SellingContextHelper {

	private SellingContextHelper() {

	}

	/**
	 * must save selling context manually and preserve guid or unset it if required, this
	 * is to avoid orphaned selling contexts when saving deliveries.
	 *
	 * @param dcaWrapper the delivery wrapper containing selling context to persist
	 */
	public static void saveSellingContextManually(final AbstractSellingContextAdapter dcaWrapper) {

		SellingContext sellingContext = dcaWrapper.getSellingContext();

		if (sellingContext != null) {

			if (sellingContext.isPersisted()) {
				SellingContext originalSellingContext =
						getSellingContextService().getByGuid(dcaWrapper.getSellingContext().getGuid());

				ConditionalExpression shopper = originalSellingContext.getCondition(TagDictionary.DICTIONARY_SHOPPER_GUID);
				ConditionalExpression time = originalSellingContext.getCondition(TagDictionary.DICTIONARY_TIME_GUID);
				ConditionalExpression stores = originalSellingContext.getCondition(TagDictionary.DICTIONARY_STORES_GUID);

				ConditionalExpression newShopper = sellingContext.getCondition(TagDictionary.DICTIONARY_SHOPPER_GUID);
				ConditionalExpression newTime = sellingContext.getCondition(TagDictionary.DICTIONARY_TIME_GUID);
				ConditionalExpression newStores = sellingContext.getCondition(TagDictionary.DICTIONARY_STORES_GUID);

				toDeletePreviousConditionalExpression(newShopper, shopper);
				toDeletePreviousConditionalExpression(newTime, time);
				toDeletePreviousConditionalExpression(newStores, stores);
			}

			ConditionalExpression namedShopper;
			ConditionalExpression namedTime;
			ConditionalExpression namedStores;

			if (MapUtils.isNotEmpty(sellingContext.getConditions())) {

				namedShopper = backupShopper(sellingContext);

				namedTime = backupTime(sellingContext);

				namedStores = backupStores(sellingContext);

				sellingContext = getSellingContextService().saveOrUpdate(sellingContext);

				sellingContext = saveConditions(sellingContext, namedShopper,
						namedTime, namedStores);

				dcaWrapper.setSellingContext(sellingContext);
			} else {
				dcaWrapper.clearSellingContext();
			}
		}
	}

	private static boolean toDeletePreviousConditionalExpression(
			final ConditionalExpression newCE, final ConditionalExpression oldCE) {
		boolean result = false;
		if (oldCE != null && !oldCE.isNamed()) { // process if we have an old conditional expression 
			if (newCE == null) { // clear if we do not have a new one
				result = true;
			} else if (newCE.isNamed()) {
				result = true;
			} else if (!oldCE.getGuid().equals(newCE.getGuid())) {
				result = true;
			}
		}
		if (result) {
			getTagConditionService().delete(oldCE);
		}
		return result;
	}

	private static ConditionalExpression backupStores(final SellingContext sellingContext) {
		ConditionalExpression namedStore = null;
		if (null != sellingContext.getStoresCondition()
				&& sellingContext.getStoresCondition().isPersisted()
				&& !sellingContext.isPersisted()) {
			namedStore = sellingContext.getStoresCondition();
			sellingContext.setCondition(TagDictionary.DICTIONARY_STORES_GUID, null);
		}
		return namedStore;
	}

	private static ConditionalExpression backupTime(
			final SellingContext sellingContext) {
		ConditionalExpression namedTime = null;
		if (null != sellingContext.getTimeCondition()
				&& sellingContext.getTimeCondition().isPersisted()
				&& !sellingContext.isPersisted()) {
			namedTime = sellingContext.getTimeCondition();
			sellingContext.setCondition(TagDictionary.DICTIONARY_TIME_GUID, null);
		}
		return namedTime;
	}

	private static ConditionalExpression backupShopper(
			final SellingContext sellingContext) {
		ConditionalExpression namedShopper = null;
		if (null != sellingContext.getShopperCondition()
				&& sellingContext.getShopperCondition().isPersisted()
				&& !sellingContext.isPersisted()) {
			namedShopper = sellingContext.getShopperCondition();
			sellingContext.setCondition(TagDictionary.DICTIONARY_SHOPPER_GUID, null);
		}
		return namedShopper;
	}

	private static SellingContext saveConditions(final SellingContext sellingContext,
												 final ConditionalExpression namedShopper, final ConditionalExpression namedTime,
												 final ConditionalExpression namedStores) {
		if (null != namedShopper || null != namedTime
				|| null != namedStores) {
			if (null != namedShopper) {
				sellingContext.setCondition(TagDictionary.DICTIONARY_SHOPPER_GUID, namedShopper);
			}

			if (null != namedTime) {
				sellingContext.setCondition(TagDictionary.DICTIONARY_TIME_GUID, namedTime);
			}

			if (null != namedStores) {
				sellingContext.setCondition(TagDictionary.DICTIONARY_STORES_GUID, namedStores);
			}
			return getSellingContextService().saveOrUpdate(sellingContext);
		}
		return sellingContext;
	}

	/**
	 * must delete selling context manually.
	 *
	 * @param sellingContextGuid the guid of the context to delete
	 */
	public static void deleteSellingContextManually(final String sellingContextGuid) {
		final SellingContext sellingContext = getSellingContextByGuid(sellingContextGuid);
		if (sellingContext != null) {
			getSellingContextService().remove(sellingContext);
		}
	}

	private static SellingContext getSellingContextByGuid(final String sellingContextGuid) {
		return getSellingContextService().getByGuid(sellingContextGuid);
	}

	private static SellingContextService getSellingContextService() {
		return ServiceLocator.getService(ContextIdNames.SELLING_CONTEXT_SERVICE);
	}

	private static TagConditionService getTagConditionService() {
		return ServiceLocator.getService(ContextIdNames.TAG_CONDITION_SERVICE);
	}

}
