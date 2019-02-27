/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.conditionbuilder.extenders;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.core.databinding.DataBindingContext;

import com.elasticpath.cmclient.conditionbuilder.adapter.ConditionModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.LogicalOperatorModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.component.ConditionRowComposite;
import com.elasticpath.cmclient.conditionbuilder.valueeditor.ConditionRowValueFactory;

/**
 * Interface to be implemented by extensions to create a row composite for a TagDefinition.
 * @param <M>  model type
 * @param <OP>  operator type
 * @param <M2> parent model adapter type
 * @param <O2> parent operator type
 */
public interface ConditionRowCompositeCreator<M, OP, M2, O2> {
	
	/**
	 * Creates a ConditionRowComposite for the set of parameters.
	 *
	 * @param parent                   parent composite
	 * @param swtStyle                 SWT style
	 * @param modelAdapter             model adapter
	 * @param parentModelAdapter       parent model adapter
	 * @param dataBindingContext       the DataBindingContext
	 * @param conditionRowValueFactory the {@link ConditionRowValueFactory}
	 * @return the ConditionRowComposite
	 */
	ConditionRowComposite<M, OP, M2, O2> createConditionRowComposite(
			Composite parent, 
			int swtStyle,
			ConditionModelAdapter<M, OP> modelAdapter, 
			LogicalOperatorModelAdapter<M2, O2> parentModelAdapter,
			DataBindingContext dataBindingContext, 
			ConditionRowValueFactory conditionRowValueFactory);
}
