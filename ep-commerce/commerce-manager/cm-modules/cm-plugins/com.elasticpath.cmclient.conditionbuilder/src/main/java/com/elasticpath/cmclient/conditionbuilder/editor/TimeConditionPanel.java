/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.conditionbuilder.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import com.elasticpath.cmclient.conditionbuilder.component.ActionEventListener;
import com.elasticpath.cmclient.conditionbuilder.wizard.conditioncomposite.TimeConditionComposite;
import com.elasticpath.cmclient.conditionbuilder.wizard.model.impl.TimeConditionModelAdapterImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.LogicalOperator;

/**
 * A shopper panel.
 * @param <T> a selling context
 */
public class TimeConditionPanel<T extends SellingContext> extends AbstractConditionPanel<T> {

	private static final int NUM_OF_COLUMNS_ON_THE_PAGE = 4;
	private TimeConditionComposite timeConditionComposite;
	private final ActionEventListener<Object> markEditorStateListener;
	
	private final PropertyChangeListener changeListener = 
		new PropertyChangeListener() {
			public void propertyChange(final PropertyChangeEvent event) {
				markEditorStateListener.onEvent(event);
			}
	};
	private TimeConditionModelAdapterImpl timeConditionModelAdapter;
	
	/**
	 * Default constructor.
	 * @param model a model
	 * @param namedConditionsList a named conditions list
	 * @param tagDictionaryGuid tag dictionary guid
	 * @param dataBindingContextListener a data binding listener
	 * @param markEditorStateListener a listener to mark editor state
	 * @param labelsArray an labels array
	 */
	protected TimeConditionPanel(
			final ModelWrapper<T> model, 
			final List<ConditionalExpression> namedConditionsList,
			final String tagDictionaryGuid,
			final DataBindingContextListener dataBindingContextListener,
			final ActionEventListener<Object> markEditorStateListener,
			final String... labelsArray) {
		super(model, tagDictionaryGuid, namedConditionsList, 
				dataBindingContextListener, markEditorStateListener, labelsArray);
		this.markEditorStateListener = markEditorStateListener;
	}


	@Override
	protected void createConditionExpressionComposite(final IPolicyTargetLayoutComposite parent, final PolicyActionContainer container) {
		
		LogicalOperator logicalOperator = this.getLogicalOperatorForCurrentTagDictionary(getTagDictionaryGuid());

		timeConditionModelAdapter = new TimeConditionModelAdapterImpl(logicalOperator);

		timeConditionComposite = new TimeConditionComposite(
				timeConditionModelAdapter, 
				NUM_OF_COLUMNS_ON_THE_PAGE, 
				parent,
				container,
				getDataBindingContext(), getBindingProvider(), false);

		timeConditionComposite.bindControls();
		timeConditionModelAdapter.addPropertyChangeListener(changeListener);
	}

	@Override
	public void dispose() {
		this.timeConditionModelAdapter.removePropertyChangeListener(changeListener);
		super.dispose();
	}

}
