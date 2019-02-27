/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 *
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.controller.impl;


import java.util.List;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.controller.impl.AbstractBaseControllerImpl;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.ConditionalExpressionSearchTabModel;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.service.TagConditionService;

/**
 * ConditionalExpressionListControllerImpl.
 */
public class ConditionalExpressionListControllerImpl extends
		AbstractBaseControllerImpl<ConditionalExpression> {

	private ConditionalExpressionSearchTabModel model;

	@Override
	public void onEvent(final UIEvent<?> eventObject) {

		if (EventType.SEARCH == eventObject.getEventType()) {
			model = (ConditionalExpressionSearchTabModel) eventObject.getSource();
		}
		if (model == null) {
			return;
		}

		TagConditionService tagConditionService = this.getConditionalExpressionService();

		String name = null;
		if (model.getName() != null && model.getName().trim().length() > 0) {
			name = model.getName();
		}
		String tagDictionaryGuid = null;
		if (model.getTagDictionary() != null) {
			tagDictionaryGuid = model.getTagDictionary().getGuid();
		}
		String tagDefinitionGuid = null;
		if (model.getTagDefinition() != null) {
			tagDefinitionGuid = model.getTagDefinition().getGuid();
		}
		String sellingContextGuid = null;
		if (model.getDynamicContentDelivery() != null) {
			sellingContextGuid = model.getDynamicContentDelivery().getSellingContextGuid();
		}

		List<ConditionalExpression> collection =
				tagConditionService.getNamedConditionsByNameTagDictionaryConditionTagSellingContext(
						name, tagDictionaryGuid, tagDefinitionGuid, sellingContextGuid);

		SearchResultEvent<ConditionalExpression> resultEvent =
				new SearchResultEvent<>(this, collection, 0, collection.size(),
						eventObject.isStartFromFirstPage(), eventObject.getEventType());

		this.fireEvent(resultEvent);
	}

	private TagConditionService getConditionalExpressionService() {
		return ServiceLocator.getService(ContextIdNames.TAG_CONDITION_SERVICE);
	}
}
