/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers.extenders;

import com.elasticpath.domain.rules.RuleParameter;
import org.eclipse.core.databinding.DataBindingContext;

/**
 * Interface for Promotion Widget Creator.
 */
public interface PromotionWidgetCreator {

    /**
     * Execute rule.
     *
     * @param parent the parent.
     * @param ruleParameter the rule parameter.
     * @param parentComposite the parent composite.
     * @param bindingContext the binding context.
     * @param policyActionContainer the policy action container.
     */
    void execute(Object parent, RuleParameter ruleParameter,
                 Object parentComposite, DataBindingContext bindingContext,
                 Object policyActionContainer);

    /**
     * Check if a given key is valid.
     *
     * @param key the key
     * @return the validity of the key.
     */
    boolean isValid(String key);

}
