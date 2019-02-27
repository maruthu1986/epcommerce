/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store.promotions.editors;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.promotions.wizard.ConditionOperatorConfiguration;
import com.elasticpath.domain.rules.Rule;

/**
 * Strategy that updates a {@link Rule} based on updates to a UI control.
 */
public class ConditionOperatorMenuUpdateValueStrategy extends ObservableUpdateValueStrategy {

	private final Rule rule;

	private final ConditionOperatorConfiguration conditionOperatorConfiguration;

	/**
	 * Public constructor.
	 *
	 * @param rule the rule on which to propagate the updated values
	 * @param conditionOperatorConfiguration the condition operator configuration used to interpret and translate the updated value
	 */
	public ConditionOperatorMenuUpdateValueStrategy(final Rule rule, final ConditionOperatorConfiguration conditionOperatorConfiguration) {
		super();
		this.rule = rule;
		this.conditionOperatorConfiguration = conditionOperatorConfiguration;
	}

	@Override
	protected IStatus doSet(final IObservableValue observableValue, final Object value) {
		final int selectionIndex = (Integer) value;

		try {
			rule.setConditionOperator(conditionOperatorConfiguration.getConditionOperatorByIndex(selectionIndex));
			return Status.OK_STATUS;
		} catch (final EpServiceException e) {
			return new Status(IStatus.WARNING, StorePlugin.PLUGIN_ID, "Cannot set the condition operator."); //$NON-NLS-1$
		}
	}
}
