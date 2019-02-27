/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.conditionbuilder.wizard.pages;

import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;

import com.elasticpath.cmclient.conditionbuilder.component.TopLevelComposite;
import com.elasticpath.cmclient.conditionbuilder.impl.tag.ConditionBuilderFactoryImpl;
import com.elasticpath.cmclient.conditionbuilder.plugin.ConditionBuilderMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.LogicalOperatorType;

/**
 * Class represents page on Dynamic Content Assignment wizard with definition of
 * SHOPPER condition.
 * 
 *  @param <T> - class that extends {@link SellingContext}.
 */
public class SellingContextConditionShopperWizardPage<T extends SellingContext> extends
		AbstractSellingContextConditionWizardPage<SellingContext> {

	private static final int NAM_OF_COLUMNS_ON_THE_PAGE = 1;
	private static final int HEIGHT_HINT = 250;

	private TopLevelComposite<LogicalOperator, LogicalOperatorType> topLevelComposite;
	
	/**
	 * Constructor.
	 * 
	 * @param pageName -
	 *            name of the page
	 * @param title -
	 *            title of the page
	 * @param description -
	 *            description of the page
	 * @param conditionsList conditions list
	 * @param sellingContext - selling context
	 * @param dictionaryGuid - guid of the dictionary to be used
	 */
	public SellingContextConditionShopperWizardPage(
			final String pageName,
			final String title, final String description, 
			final List<ConditionalExpression> conditionsList, 
			final SellingContext sellingContext,
			final String dictionaryGuid) {
		super(NAM_OF_COLUMNS_ON_THE_PAGE, pageName, title, description,
				dictionaryGuid, conditionsList, sellingContext);
	}

	@Override
	protected void createConditionExpressionComposite(final IPolicyTargetLayoutComposite parent, final PolicyActionContainer container) {

		ConditionBuilderFactoryImpl conditionBuilderFactory = new ConditionBuilderFactoryImpl();
		conditionBuilderFactory.setLocale(CorePlugin.getDefault().getDefaultLocale());
		conditionBuilderFactory.setDataBindingContext(getDataBindingContext());
		conditionBuilderFactory.setAddButtonText("ConditionBuilder_AddConditionButton"); 		 //$NON-NLS-1$
		conditionBuilderFactory.setConditionBuilderTitle("ConditionBuilder_Title"); //$NON-NLS-1$
		conditionBuilderFactory.setTagDictionary(getTagDictionaryGuid());

		conditionBuilderFactory.getResourceAdapterFactory().setResourceAdapterForLogicalOperator(
            object -> {
				//TODO Shall we put OR  AND etc localization into DB ?
				return ConditionBuilderMessages.get().getMessage(object.getMessageKey());
			});
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		layoutData.heightHint = HEIGHT_HINT;

		final ScrolledComposite scrolledComposite = 
			new ScrolledComposite(parent.getSwtComposite(), SWT.FLAT | SWT.H_SCROLL | SWT.V_SCROLL) {

				@Override
				public void setEnabled(final boolean enabled) {
					super.setEnabled(enabled);
					for (Control child : getChildren()) {
						if (!child.isDisposed()) {
							child.setEnabled(enabled);
						}
					}
				}
		};
		scrolledComposite.setLayoutData(layoutData);
		scrolledComposite.setLayout(new GridLayout());

        LogicalOperator logicalOperator = this.getLogicalOperatorForCurrentTagDictionary(getTagDictionaryGuid());
		topLevelComposite = conditionBuilderFactory.createFullUiFromModel(
													scrolledComposite,
													SWT.FLAT, 
													logicalOperator);
		
		conditionBuilderFactory.setListenerForRefreshParentComposite(
            object -> {
				scrolledComposite.setRedraw(false);

				topLevelComposite.layout();
				Rectangle rect = scrolledComposite.getClientArea();
				scrolledComposite.setMinSize(topLevelComposite.computeSize(rect.width, SWT.DEFAULT));

				scrolledComposite.layout();
				scrolledComposite.setRedraw(true);
				((AbstractEpWizard < ? >) getWizard()).getWizardDialog().updateButtons();
			});
		
		scrolledComposite.setContent(topLevelComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent event) {
				Rectangle rect = scrolledComposite.getClientArea();
				scrolledComposite.setMinSize(topLevelComposite.computeSize(rect.width, SWT.DEFAULT));
			}
		});

		container.addTarget(state -> EpControlFactory.changeEpStateForComposite(topLevelComposite, state));

	}

	@Override
	protected void bindControls() {
		// TODO Auto-generated method stub
	}

	@Override
	protected String getLabelForRadioButtonSavedConditions() {
		return ConditionBuilderMessages.get().Wizard_ShopperPage_RadioButtonSavedConditions;
	}
	@Override
	protected String getLabelForRadioButtonCreateConditions() {
		return ConditionBuilderMessages.get().Wizard_ShopperPage_RadioButtonConditions;
	}
	@Override
	protected String getLabelForRadioButtonsAll() {
		return ConditionBuilderMessages.get().Wizard_ShopperPage_RadioButtonAll;
	}

	@Override
	public boolean isPageComplete() {
		
		if (isCreateConditionButtonSelected()) {
			boolean conditionExist = false;
			Set<LogicalOperator> operators = this.topLevelComposite.getModel().getModel().getLogicalOperators(); 
			for (LogicalOperator logicalOperator : operators) {
				if (logicalOperator.hasChildren()) {
					conditionExist = true;
				}
			}
			if (conditionExist) {
				return super.isPageComplete();
			}
		} else {
			return super.isPageComplete();
		}
		return false;
	}

	@Override
	protected boolean isCreateCompositeBlock() {
		return false;
	}
}