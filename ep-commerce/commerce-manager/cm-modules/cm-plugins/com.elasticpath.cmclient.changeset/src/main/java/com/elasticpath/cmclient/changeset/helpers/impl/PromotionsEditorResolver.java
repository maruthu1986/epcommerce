/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.helpers.impl;

import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.changeset.helpers.EditorResolver;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.service.rules.RuleService;

/**
 *  Default implementation of the {@link EditorResolver} interface.
 */
public class PromotionsEditorResolver implements EditorResolver {

	/**
	 * Prefix string of both Catalog and Shopping Cart promotions editors.
	 */
	public static final String PROMOTIONS_EDITOR_PREFIX = "com.elasticpath.cmclient.store.promotions.editors.PromotionsEditor"; //$NON-NLS-1$

	private final RuleService ruleService = ServiceLocator.getService(ContextIdNames.RULE_SERVICE); //$NON-NLS-1$

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String resolveEditorId(final BusinessObjectDescriptor objectDescriptor) {
		StringBuilder editorId = new StringBuilder(PROMOTIONS_EDITOR_PREFIX);

		if ("Promotion".equals(objectDescriptor.getObjectType())) { //$NON-NLS-1$
			Rule rule = ruleService.findByRuleCode(objectDescriptor.getObjectIdentifier());
			if (rule == null) {
				throw new IllegalArgumentException(

						NLS.bind(CoreMessages.get().Given_Object_Not_Exist,
						new String[]{"Promotion", objectDescriptor.getObjectIdentifier()})); //$NON-NLS-1$
			}
			if (rule.getCatalog() == null) {
				editorId.append(".ShoppingCart"); //$NON-NLS-1$
			} else {
				editorId.append(".Catalog"); //$NON-NLS-1$
			}
		}
		
		return editorId.toString();
	}

}
