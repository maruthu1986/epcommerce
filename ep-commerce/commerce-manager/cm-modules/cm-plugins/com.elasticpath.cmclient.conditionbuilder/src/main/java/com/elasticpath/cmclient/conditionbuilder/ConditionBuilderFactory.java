/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.conditionbuilder;

import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.conditionbuilder.adapter.ConditionModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.LogicalOperatorModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.component.ConditionBlockComposite;
import com.elasticpath.cmclient.conditionbuilder.component.ConditionRowComposite;
import com.elasticpath.cmclient.conditionbuilder.component.TopLevelComposite;

/**
 * ConditionBuilderFactory.
 * @param <LO> logical operator type
 * @param <C> condition type
 * @param <V> tag object
 * @param <O> condition operator
 * @param <LOT> logical operator
 * @param <GROUP> tag group type
 */
public interface ConditionBuilderFactory<LO, C, V, O, LOT, GROUP> {

	/**
	 * Create TopLevelComposite.
	 * @param parent parent
	 * @param swtStyle SWT style
	 * @param logicalOperatorModel model
	 * @return TopLevelComposite
	 */
	TopLevelComposite<LO, LOT> createFullUiFromModel(Composite parent, int swtStyle, LO logicalOperatorModel);

	/**
	 * Create TopLevelComposite.
	 * @param parent parent
	 * @param swtStyle SWT style
	 * @param logicalOperatorModel model
	 * @return TopLevelComposite
	 */
	TopLevelComposite<LO, LOT> createTopLevelComposite(Composite parent, int swtStyle, LO logicalOperatorModel);
	
	/**
	 * Create ConditionBlockComposite.
	 * @param parent parent
	 * @param swtStyle SWT style
	 * @param logicalOperatorModel model
	 * @return ConditionBlockComposite
	 */
	ConditionBlockComposite<LO, V, LOT, GROUP> 
		createConditionBlockComposite(TopLevelComposite<LO, LOT> parent, int swtStyle, LO logicalOperatorModel);
	
	/**
	 * Create ConditionRowComposite. 
	 * @param parent parent
	 * @param swtStyle SWT style
	 * @param condition condition
	 * @return ConditionRowComposite
	 */
	ConditionRowComposite<C, O, LO, LOT> createConditionRowComposite(ConditionBlockComposite<LO, V, LOT, GROUP> parent, int swtStyle, C condition);

	/**
	 * Create LogicalOperatorModelAdapter.
	 * @param model model
	 * @return LogicalOperatorModelAdapter
	 */
	LogicalOperatorModelAdapter<LO, LOT> createLogicalOperatorModelAdapter(LO model);
	
	/**
	 * Create ConditionModelAdapter.
	 * @param model model
	 * @return ConditionModelAdapter
	 */
	ConditionModelAdapter<C, O> createConditionModelAdapter(C model);
}
