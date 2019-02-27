/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.conditionbuilder.editor;

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.conditionbuilder.adapter.BaseModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.impl.tag.BaseModelAdapterImpl;
import com.elasticpath.cmclient.conditionbuilder.component.ActionEventListener;
import com.elasticpath.cmclient.conditionbuilder.plugin.ConditionBuilderMessages;
import com.elasticpath.cmclient.conditionbuilder.wizard.conditioncomposite.SavedConditionComposite;
import com.elasticpath.cmclient.conditionbuilder.wizard.conditions.handlers.ConditionHandler;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.service.InvalidConditionTreeException;

/**
 * A base class for condition panel.
 * @param <MODEL> a SellingContext implementation
 */
@SuppressWarnings({ "PMD.GodClass" })
public abstract class AbstractConditionPanel<MODEL extends  SellingContext> {
	private static final int WIDTH_HINT_100 = 100;
	private static final int HORIZONTAL_INDENT_10 = 10;

	private final String tagDictionaryGuid;
	
	private final ConditionHandler conditionHandler = new ConditionHandler();
	private LogicalOperator logicalOperator;
	private EpControlBindingProvider bindingProvider;

	private Composite mainComposite;
	private SavedConditionComposite savedConditionsComposite;
	private IPolicyTargetLayoutComposite conditionBuilderComposite;

	private Button radioButtonAll;
	private Button radioButtonSavedConditions;
	private Button radioButtonCreateConditions;

	private final DataBindingContext dataBindingContextForAllButton = new DataBindingContext();
	private final DataBindingContext dataBindingContextForSavedButton = new DataBindingContext();
	private final DataBindingContext dataBindingContextForCreateButton = new DataBindingContext();

	private BaseModelAdapter<ConditionalExpression> modelAdapter;

	private final String[] labelsArray;
	private final ModelWrapper<MODEL> modelWrapper;
	private final List<ConditionalExpression> conditionsList;

	private final DataBindingContextListener dataBindingContextListener;
	
	private final SelectionListener radioButtonSelectionListener;
	
	/**
	 * A default constructor.
	 * @param modelWrapper a model wrapper
	 * @param tagDictionaryGuid a tag dictionary guid
	 * @param namedConditionsList a named conditions list 
	 * @param dataBindingContextListener a data binding context listener
	 * @param markEditorStateListener a listener to mark editor state
	 * @param labelsArray a labels array
	 */
	protected AbstractConditionPanel(final ModelWrapper<MODEL> modelWrapper,
			final String tagDictionaryGuid,
			final List<ConditionalExpression> namedConditionsList,
			final DataBindingContextListener dataBindingContextListener,
			final ActionEventListener<Object> markEditorStateListener,
			final String... labelsArray) {
		this.modelWrapper = modelWrapper;
		this.tagDictionaryGuid = tagDictionaryGuid;
		this.conditionsList = namedConditionsList;
		this.labelsArray = labelsArray;
		this.dataBindingContextListener = dataBindingContextListener;
		
		radioButtonSelectionListener = new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent event) {
				// empty
			}
			public void widgetSelected(final SelectionEvent event) {
				markEditorStateListener.onEvent(event.getSource());
			}
		};
	}
	
	/**
	 * Creates required composite to create new condition expression.
	 * 
	 * @param parent - composite on which new composite will be located.
	 * @param container the policy action container
	 */
	protected abstract void createConditionExpressionComposite(final IPolicyTargetLayoutComposite parent, PolicyActionContainer container);

	/**
	 * Return a tag dictionary Guid.
	 * @return a guid 
	 */
	protected String getTagDictionaryGuid() {
		return tagDictionaryGuid;
	}
	
	/**
	 * Get logical operator.
	 * @param tagDictionaryGuid tag dictionary GUID
	 * @return LogicalOperator instance
	 */
	protected LogicalOperator getLogicalOperatorForCurrentTagDictionary(final String tagDictionaryGuid) {
		
		ConditionalExpression conditionalExpression = modelWrapper.getModel().getCondition(tagDictionaryGuid);
		if (conditionalExpression != null && conditionalExpression.isNamed()) {
			conditionalExpression = null;
		}
		this.logicalOperator = conditionHandler.convertConditionExpressionStringToLogicalOperator(conditionalExpression, tagDictionaryGuid);
		return this.logicalOperator;
	}

	/**
	 * Return a model.
	 * @return a SellingContext model 
	 */
	protected ModelWrapper<MODEL> getModelWrapper() {
		return modelWrapper;
	}
	
	/**
	 * Make layout for the main composite. 
	 */
	protected void layout() {
		this.conditionBuilderComposite.getSwtComposite().layout(true);
		this.mainComposite.layout();
	}

	/**
	 * Create a page.
	 * @param parent a parent composite
	 * @param policyContainer a policy container
	 */
	public void createPageContents(final IPolicyTargetLayoutComposite parent, final PolicyActionContainer policyContainer) {

		mainComposite = parent.getSwtComposite();
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// creating composite for two radio buttons
		ConditionalExpression conditionalExpression = this.modelWrapper.getModel().getCondition(tagDictionaryGuid);

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

	private String getLabelForRadioButtonCreateConditions() {
		return this.labelsArray[2];
	}

	private String getLabelForRadioButtonSavedConditions() {
		return this.labelsArray[1];
	}

	private String getLabelForRadioButtonsAll() {
		return this.labelsArray[0];
	}

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
			modelWrapper.getModel().getCondition(tagDictionaryGuid);

		if (conditionalExpression == null || conditionalExpression.isNamed()) {
			conditionalExpression = ServiceLocator.getService(ContextIdNames.CONDITIONAL_EXPRESSION);
			conditionalExpression.setName(String.valueOf(System.nanoTime()));
			conditionalExpression.setTagDictionaryGuid(tagDictionaryGuid);
		}

		this.modelAdapter = new BaseModelAdapterImpl<>(conditionalExpression);
		
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

	/**
	 * Creates EpWizardPageSupport for this page.
	 */
	private void createEpWizardPageSupport(final DataBindingContext dataBindingContext) {
		if (null != this.dataBindingContextListener) {
			this.dataBindingContextListener.changed(DataBindingContextListener.Status.ADD, dataBindingContext);
		}
	}

	/**
	 * Removes EpWizardPageSupport for this page.
	 */
	private void removesEpWizardPageSupport() {
		DataBindingContext activeDataBindingContext = getActiveDataBindingContext();
		if (null != this.dataBindingContextListener) {
			this.dataBindingContextListener.changed(DataBindingContextListener.Status.REMOVE, activeDataBindingContext);
		}
	}

	/**
	 * @return A current data binding context
	 */
	public DataBindingContext getActiveDataBindingContext() {
		if (this.radioButtonAll.getSelection()) {
			return this.dataBindingContextForAllButton;
		} else if (this.radioButtonSavedConditions != null && this.radioButtonSavedConditions.getSelection()) {
			return this.dataBindingContextForSavedButton;
		} else if (this.radioButtonCreateConditions.getSelection()) {
			return this.dataBindingContextForCreateButton;
		} 
		return null;
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

	/**
	 * Get the model adapter.
	 * @return the modelAdapter
	 * @throws InvalidConditionTreeException if the condition tree has an invalid format or data
	 */
	public BaseModelAdapter<ConditionalExpression> getModelAdapter() throws InvalidConditionTreeException {
		if (this.radioButtonCreateConditions.getSelection()) {
			String conditionString = conditionHandler.convertLogicalOperatorToConditionExpressionString(logicalOperator);
			modelAdapter.getModel().setConditionString(conditionString);
		}
		return modelAdapter;
	}

	/**
	 * @return the data binding context for new condition creation.
	 */
	public DataBindingContext getDataBindingContext() {
		return this.dataBindingContextForCreateButton;
	}

	/**
	 * Bind controls.
	 * @param bindingContext a data binding context
	 */
	protected void bindControls(final DataBindingContext bindingContext) {
		this.radioButtonAll.addSelectionListener(radioButtonSelectionListener);
		this.radioButtonCreateConditions.addSelectionListener(radioButtonSelectionListener);
		if (this.radioButtonSavedConditions != null) {
			this.radioButtonAll.addSelectionListener(radioButtonSelectionListener);
		}
	}
	
	/**
	 * Clear object. 
	 */
	public void dispose() {
		// empty
	}
}
