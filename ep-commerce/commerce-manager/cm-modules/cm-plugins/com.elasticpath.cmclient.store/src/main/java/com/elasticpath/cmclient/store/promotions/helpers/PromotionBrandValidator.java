/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.service.catalog.BrandService;


/**
 *	Class that checks that all brands associated with a given rule actually exists.
 */
public class PromotionBrandValidator {

	
	private final BrandService brandService;

	/**
	 * Constructor for Brand Validator.
	 */
	public PromotionBrandValidator() {

		brandService = ServiceLocator.getService(ContextIdNames.BRAND_SERVICE);
	}
	/**
	 * Gets the invalid brands for the given rule.
	 *
	 * @param rule the rule which may contain a reference to a brand that doesn't exist.
	 * @return A List of Strings that the rule depends on which do not correspond to a brand in the system.
	 */
	public List<String> getInvalidBrandsFor(final Rule rule) {
		final ArrayList<String> invalidBrands = new ArrayList<>();
		final Set<RuleCondition> conditions = rule.getConditions();
		for (RuleCondition condition : conditions) {
			invalidBrands.addAll(getInvalidBrandsForCondition(condition));
		}
		return invalidBrands;
	}
	
	private List<String> getInvalidBrandsForCondition(final RuleCondition condition) {
		final ArrayList<String> invalidBrands = new ArrayList<>();
		String[] parameterKeys = condition.getParameterKeys();
		if (parameterKeys != null) {
			for (String paramKey : parameterKeys) {
				String invalidBrandForKey = getInvalidBrandForKey(condition, paramKey);
				if (invalidBrandForKey != null) {
					invalidBrands.add(invalidBrandForKey);
				}
			}
		}
		return invalidBrands;
	}

	private String getInvalidBrandForKey(final RuleCondition condition, final String paramKey) {
		if (RuleParameter.BRAND_CODE_KEY.equals(paramKey)) {
			String paramValue = condition.getParamValue(paramKey);
			if (!brandService.codeExists(paramValue)) {
				return paramValue;
			}
		}
		return null;
	}
	
}
