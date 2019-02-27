/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.conditionbuilder.editor;

import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

import com.elasticpath.cmclient.conditionbuilder.component.ActionEventListener;
import com.elasticpath.cmclient.conditionbuilder.component.TopLevelComposite;
import com.elasticpath.cmclient.conditionbuilder.impl.tag.ConditionBuilderFactoryImpl;
import com.elasticpath.cmclient.conditionbuilder.plugin.ConditionBuilderMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.LogicalOperatorType;

/**
 * A shopper panel.
 * @param <T> a selling context
 */
public class ShopperConditionPanel<T extends SellingContext> extends AbstractConditionPanel<T> {

	private TopLevelComposite<LogicalOperator, LogicalOperatorType> topLevelComposite;
	private final ConditionBuilderFactoryImpl conditionBuilderFactory;
	
	/**
	 * Default constructor.
	 * @param model a model
	 * @param namedConditionsList a named conditions list
	 * @param tagDictionaryGuid tag dictionary guid
	 * @param dataBindingContextListener a data binding listener
	 * @param markEditorStateListener a listener to mark editor state
	 * @param labelsArray an labels array
	 */
	protected ShopperConditionPanel(
			final ModelWrapper<T> model, 
			final List<ConditionalExpression> namedConditionsList,
			final String tagDictionaryGuid,
			final DataBindingContextListener dataBindingContextListener,
			final ActionEventListener<Object> markEditorStateListener,
			final String... labelsArray) {
		super(model, tagDictionaryGuid, namedConditionsList, 
				dataBindingContextListener, markEditorStateListener, labelsArray);

		conditionBuilderFactory = new ConditionBuilderFactoryImpl();
		conditionBuilderFactory.setLocale(CorePlugin.getDefault().getDefaultLocale());
		conditionBuilderFactory.setDataBindingContext(getDataBindingContext());
		conditionBuilderFactory.setAddButtonText("ConditionBuilder_AddConditionButton"); //$NON-NLS-1$
		conditionBuilderFactory.setConditionBuilderTitle("ConditionBuilder_Title"); //$NON-NLS-1$
		conditionBuilderFactory.setTagDictionary(tagDictionaryGuid);

		conditionBuilderFactory.getResourceAdapterFactory().setResourceAdapterForLogicalOperator(
            object -> ConditionBuilderMessages.get().getMessage(object.getMessageKey()));
		conditionBuilderFactory.setListenerForMarkEditorState(markEditorStateListener);
	}


	@Override
	protected void createConditionExpressionComposite(final IPolicyTargetLayoutComposite parent, final PolicyActionContainer container) {
		
		LogicalOperator logicalOperator = this.getLogicalOperatorForCurrentTagDictionary(getTagDictionaryGuid());
		
		topLevelComposite = conditionBuilderFactory.createFullUiFromModel(
				parent.getSwtComposite(),
				SWT.FLAT, 
				logicalOperator);
		topLevelComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		conditionBuilderFactory.setListenerForRefreshParentComposite(
            object -> {
				topLevelComposite.layout();
				ShopperConditionPanel.this.layout();
			});

		container.addTarget(state -> EpControlFactory.changeEpStateForComposite(topLevelComposite, state));
	}

	/**
	 * @return true if the model contains a condition and it is not empty. Return false otherwise.
	 */
	public boolean isConditionNotEmpty() {
		boolean conditionExist = false;
		Set<LogicalOperator> operators = this.topLevelComposite.getModel().getModel().getLogicalOperators();
		for (LogicalOperator logicalOperator : operators) {
			if (logicalOperator.hasChildren()) {
				conditionExist = true;
			}
		}
		return conditionExist;
	}

}
