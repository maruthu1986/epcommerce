/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.conditionbuilder.wizard.pages;

import java.util.HashSet;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.conditionbuilder.adapter.BaseModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.impl.tag.BaseModelAdapterImpl;
import com.elasticpath.cmclient.conditionbuilder.plugin.ConditionBuilderMessages;
import com.elasticpath.cmclient.conditionbuilder.wizard.conditioncomposite.SavedConditionComposite;
import com.elasticpath.cmclient.conditionbuilder.wizard.conditions.handlers.ConditionHandler;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareWizardPage;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.service.InvalidConditionTreeException;

/**
 * 
 * Parent class for the wizards that use selling context.
 *
 * @param <T> - class that extends {@link SellingContext}.
 */
@SuppressWarnings("PMD.GodClass")
public abstract class AbstractSellingContextConditionWizardPage<T extends  SellingContext> extends AbstractPolicyAwareWizardPage<SellingContext> {

	private static final int WIDTH_HINT_100 = 100;

	private static final int HORIZONTAL_INDENT_10 = 10;

	private static final String SELLING_CHANNEL = "SELLING_CHANNEL";

	private static final String EQUAL_TO = "equalTo";

	private EpWizardPageSupport epWizardPageSupport;

	private EpControlBindingProvider bindingProvider;

	private SavedConditionComposite savedConditionsComposite;

	private Composite mainComposite;

	private IPolicyTargetLayoutComposite conditionBuilderComposite;

	private Button radioButtonAll;
	private Button radioButtonSavedConditions;
	private Button radioButtonCreateConditions;
	
	private final String tagDictionaryGuid;
	
	private final List<ConditionalExpression> conditionsList;
	
	private BaseModelAdapter<ConditionalExpression> modelAdapter;

	private final ConditionHandler conditionHandler = new ConditionHandler();
	
	private LogicalOperator logicalOperator;
	
	private final DataBindingContext dataBindingContextForAllButton = new DataBindingContext();
	private final DataBindingContext dataBindingContextForSavedButton = new DataBindingContext();
	private final DataBindingContext dataBindingContextForCreateButton = new DataBindingContext();

	private final T model;
	
	/**
	 * Constructs the wizard page.
	 * 
	 * @param numColumns columns count for the GridLayout.
	 * @param pageName name of the page.
	 * @param titleName the titleName
	 * @param message the message
	 * @param tagDictionaryGuid tag dictionary Guid
	 * @param conditionsList named conditions list
	 * @param model model
	 */
	public AbstractSellingContextConditionWizardPage(final int numColumns, 
			final String pageName,
			final String titleName, 
			final String message, 
			final String tagDictionaryGuid,
			final List<ConditionalExpression> conditionsList,
			final T model) {
		super(numColumns, false, pageName, titleName, message, new DataBindingContext());
		this.tagDictionaryGuid = tagDictionaryGuid;
		this.conditionsList = conditionsList;
		this.model = model;
	}

	/**
	 * Get logical operator.
	 * @param tagDictionaryGuid tag dictionary GUID
	 * @return LogicalOperator instance
	 */
	protected LogicalOperator getLogicalOperatorForCurrentTagDictionary(final String tagDictionaryGuid) {
		
		ConditionalExpression conditionalExpression = 
			getModel().getCondition(tagDictionaryGuid);
		if (conditionalExpression != null && conditionalExpression.isNamed()) {
			conditionalExpression = null;
		}
		this.logicalOperator = conditionHandler.convertConditionExpressionStringToLogicalOperator(conditionalExpression, tagDictionaryGuid);
		
		return this.logicalOperator;
	}
	
	@Override
	protected void populateControls() {
		// TODO Auto-generated method stub
	}

	/**
	 * Returns EpControlBindingProvider for this page.
	 * 
	 * @return EpControlBindingProvider for this page
	 */
	protected EpControlBindingProvider getBindingProvider() {
		if (null == bindingProvider) {
			bindingProvider = EpControlBindingProvider.getInstance();
		}
		return bindingProvider;
	}

	@Override
	protected void createPageContents(final IPolicyTargetLayoutComposite parent) {

		PolicyActionContainer policyContainer = addPolicyActionContainer("sellingContextConditionWizardPage"); //$NON-NLS-1$

		mainComposite = parent.getSwtComposite();
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(1, false);
		mainComposite.setLayout(layout);

		// creating composite for two radio buttons
		ConditionalExpression conditionalExpression = 
			this.getModel().getCondition(tagDictionaryGuid);

		// create radio button for "Select All"
		radioButtonAll = parent.addRadioButton(getLabelForRadioButtonsAll(), null, policyContainer);
		
		GridData gridData = new GridData(GridData.FILL, GridData.BEGINNING, false, false);
		gridData.heightHint = 0;
		gridData.horizontalIndent = HORIZONTAL_INDENT_10;
		gridData.widthHint = WIDTH_HINT_100;

		if (this.conditionsList != null) {
			// create radio button "Select one of the existing conditions"
			radioButtonSavedConditions = parent.addRadioButton(getLabelForRadioButtonSavedConditions(), null, policyContainer);
		}
		
		savedConditionsComposite = 
			new SavedConditionComposite(mainComposite,
					this.dataBindingContextForSavedButton,
					this.getBindingProvider(),
					new BaseModelAdapterImpl<ConditionalExpression>(conditionalExpression), 
					conditionsList,
					new ISelectionChangedListener() {
						public void selectionChanged(final SelectionChangedEvent event) {
							ConditionalExpression expression = 
								(ConditionalExpression) ((IStructuredSelection) event.getSelection()).getFirstElement();
							modelAdapter = new BaseModelAdapterImpl<>(expression);
						} }, ConditionBuilderMessages.get().PleaseSelect);
		savedConditionsComposite.setLayoutData(gridData);
		
		// create radio button for condition that will be created by user
		// using specific conditions composite
		radioButtonCreateConditions = parent.addRadioButton(getLabelForRadioButtonCreateConditions(), null, policyContainer);

		IEpLayoutData layoutData = parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		conditionBuilderComposite = parent.addGridLayoutComposite(1, false, layoutData, policyContainer);
		conditionBuilderComposite.getSwtComposite().setVisible(false);
		
		createConditionExpressionComposite(conditionBuilderComposite, policyContainer);

		// method adds respective listeners to all radio buttons
		this.addListenerForRadioButtons();

		setControl(mainComposite);

		if (conditionalExpression == null) {
			this.radioButtonAll.setSelection(true);
			this.radioButtonAllAction();
		} else if (conditionalExpression.isNamed()) {
			gridData.heightHint = SWT.DEFAULT;
			this.radioButtonSavedConditions.setSelection(true);
			this.radioButtonSavedConditionsAction();
		} else {
			conditionBuilderComposite.getSwtComposite().setVisible(true);
			radioButtonCreateConditions.setSelection(true);
			this.radioButtonCreateConditionsAction();
		}
	}

	/**
	 * Returns label for radio button "Create conditions".
	 * 
	 * @return string - label
	 */
	protected abstract String getLabelForRadioButtonCreateConditions();

	/**
	 * Returns label for radio button "Select Saved condition".
	 * 
	 * @return string - label
	 */
	protected abstract String getLabelForRadioButtonSavedConditions();

	/**
	 * Returns label for radio button "All".
	 * 
	 * @return string - label
	 */
	protected abstract String getLabelForRadioButtonsAll();

	/**
	 * Creates required composite to create new condition expression.
	 * 
	 * @param parent - composite on which new composite will be located.
	 * @param container the policy action container
	 */
	protected abstract void createConditionExpressionComposite(final IPolicyTargetLayoutComposite parent, PolicyActionContainer container);


	private void addListenerForRadioButtons() {
		if (radioButtonAll != null) {
			radioButtonAll.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent event) {
					// On wizard user clicks "All" button
					Button button = (Button) event.getSource();
					if (button.getSelection()) {
						radioButtonAllAction();
					}
				}
			});
		}
		if (radioButtonSavedConditions != null) {
			radioButtonSavedConditions.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent event) {
					// On wizard user clicks "Select Saved condition" button
					Button button = (Button) event.getSource();
					if (button.getSelection()) {
						radioButtonSavedConditionsAction();
					}
				}
			});
		}
		if (radioButtonCreateConditions != null) {
			radioButtonCreateConditions.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent event) {
					// On wizard user clicks "Specify conditions" button
					Button button = (Button) event.getSource();
					if (button.getSelection()) {
						radioButtonCreateConditionsAction();
					}
				}
			});
		}
	}
	
	/**
	 * Hook for custom initialisation of model adapter for all radio button option.
	 * @return By default will create an empty BaseModelAdapterImpl(null).
	 */
	protected BaseModelAdapter<ConditionalExpression> radioButtonAllActionHook() {
		return new BaseModelAdapterImpl<>(null);
	}
	
	private void radioButtonAllAction() {

		this.modelAdapter = radioButtonAllActionHook();
		
		// do not need "saved conditions" composite any more
		GridData layoutData = (GridData) savedConditionsComposite.getLayoutData();
		layoutData.heightHint = 0;
		
		// hiding conditions composite
		this.conditionBuilderComposite.getSwtComposite().setVisible(false);
		
		// remove and recreate EpWizardPageSupport
		removesEpWizardPageSupport();
		// rearrange controls
		createEpWizardPageSupport(dataBindingContextForAllButton);
		mainComposite.layout();
	}

	private void radioButtonSavedConditionsAction() {
		
		this.modelAdapter = this.savedConditionsComposite.getModelAdapter();
		
		this.conditionBuilderComposite.getSwtComposite().setVisible(false);

		// remove and recreate EpWizardPageSupport later - to avoid controls decorations
		removesEpWizardPageSupport();
		
		GridData layoutData = (GridData) savedConditionsComposite.getLayoutData();
		layoutData.heightHint = SWT.DEFAULT;

		savedConditionsComposite.bindNamedConditionToModel();
		// goes as last - not to have any error decorations when this button is selected
		createEpWizardPageSupport(dataBindingContextForSavedButton);
		mainComposite.layout();
	}

	private void radioButtonCreateConditionsAction() {
		
		ConditionalExpression conditionalExpression = 
			getModel().getCondition(tagDictionaryGuid);

		createModelAdapter(conditionalExpression);
		
		// do not need "saved conditions" composite any more
		GridData layoutData = (GridData) savedConditionsComposite.getLayoutData();
		layoutData.heightHint = 0;

		// remove and recreate EpWizardPageSupport later - to avoid controls decorations
		removesEpWizardPageSupport();

		// create to enable binding
		createEpWizardPageSupport(dataBindingContextForCreateButton);
		this.conditionBuilderComposite.getSwtComposite().setVisible(true);
		
		mainComposite.layout();
	}

	private void createModelAdapter(final ConditionalExpression conditionalExpression) {
		ConditionalExpression newConditionalExpression = conditionalExpression;
		if (newConditionalExpression == null || newConditionalExpression.isNamed()) {
			newConditionalExpression = ServiceLocator.getService(ContextIdNames.CONDITIONAL_EXPRESSION);
			newConditionalExpression.setName(String.valueOf(System.nanoTime()));
			newConditionalExpression.setTagDictionaryGuid(tagDictionaryGuid);
		}

		this.modelAdapter = new BaseModelAdapterImpl<>(newConditionalExpression);
	}

	/**
	 * Creates EpWizardPageSupport for this page.
	 */
	private void createEpWizardPageSupport(final DataBindingContext dataBindingContext) {
		if (null == epWizardPageSupport) {
			epWizardPageSupport = EpWizardPageSupport.create(this, dataBindingContext);
		}
	}

	/**
	 * Removes EpWizardPageSupport for this page.
	 */
	private void removesEpWizardPageSupport() {
		if (null != epWizardPageSupport) {
			epWizardPageSupport.dispose();
			epWizardPageSupport = null;
		}
	}

	/**
	 * Get the model adapter.
	 * @return the modelAdapter
	 * @throws InvalidConditionTreeException if the condition tree has an invalid format or data
	 */
	public BaseModelAdapter<ConditionalExpression> getModelAdapter() throws InvalidConditionTreeException {
		if (this.radioButtonCreateConditions.getSelection()) {
			setConditions();
		}
		return modelAdapter;
	}

	private void setConditions() throws InvalidConditionTreeException {
		String conditionString = conditionHandler.convertLogicalOperatorToConditionExpressionString(logicalOperator);
		modelAdapter.getModel().setConditionString(conditionString);
	}

	/**
	 * For the Stores page, we want to restrict store assignment to the stores that the cm user can access.
	 * @return a ConditionalExpression containing the conditions
	 * @throws InvalidConditionTreeException when the conditions are invalid
	 */
	public ConditionalExpression getConditionalExpressionForStores() throws InvalidConditionTreeException {
		if (this.radioButtonAll.getSelection() && logicalOperator != null) {
			for (Condition condition : new HashSet<>(logicalOperator.getConditions())) {
				logicalOperator.removeCondition(condition);
			}
			StoreService storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);
			List<Store> stores = storeService.findAllStores(LoginManager.getCmUser());
			for (Store store : stores) {
				Condition condition = conditionHandler.buildCondition(SELLING_CHANNEL, EQUAL_TO, store.getCode());
				logicalOperator.addCondition(condition);
			}
			createModelAdapter(modelAdapter.getModel());
		}
		setConditions();
		return modelAdapter.getModel();
	}
	
	/**
	 * Check if create condition button is selected.
	 * @return boolean value
	 */
	protected boolean isCreateConditionButtonSelected() {
		return this.radioButtonCreateConditions.getSelection();
	}
	
	/**
	 * Check if all button selected.
	 * @return boolean value
	 */
	protected boolean isAllConditionButtonSelected() {
		return this.radioButtonAll.getSelection();
	}

	@Override
	public DataBindingContext getDataBindingContext() {
		return this.dataBindingContextForCreateButton;
	}

	@Override
	public T getModel() {
		return model;
	}

	/**
	 * Returns TagDictionary guid for this page.
	 * @return tag dictionary guid for the page
	 */
	protected String getTagDictionaryGuid() {
		return tagDictionaryGuid;
	}
	
}
