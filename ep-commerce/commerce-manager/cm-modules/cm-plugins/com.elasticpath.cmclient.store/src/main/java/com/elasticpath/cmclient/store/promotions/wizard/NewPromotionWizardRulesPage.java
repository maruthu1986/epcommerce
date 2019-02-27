/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions.wizard;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.helpers.CompositeLayoutUtility;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyDelegate;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.DefaultStatePolicyDelegateImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.cmclient.store.promotions.LayoutRefresher;
import com.elasticpath.cmclient.store.promotions.PromotionRulesWidgetUtil;
import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.cmclient.store.promotions.editors.ConditionOperatorMenuUpdateValueStrategy;
import com.elasticpath.cmclient.store.promotions.editors.RulePresentationHelper;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.DiscountType;
import com.elasticpath.domain.rules.ImpliedRuleCondition;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleException;
import com.elasticpath.domain.rules.RuleExceptionType;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.service.rules.RuleService;

/**
 * The new promotions details wizard page.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.ConstructorCallsOverridableMethod",
	"PMD.TooManyMethods", "PMD.ExcessiveImports", "PMD.GodClass"})
public class NewPromotionWizardRulesPage extends AbstractEPWizardPage<Rule> implements StatePolicyDelegate, LayoutRefresher {

	private static final String BEG_PARAM_DELIMITER = "["; //$NON-NLS-1$

	private static final String END_PARAM_DELIMITER = "]"; //$NON-NLS-1$

	private static final int WRAPPER_COMPOSITE_NUM_COLUMNS = 1;

	private static final int CONDITION_COMPOSITE_NUM_COLUMNS = 4;

	private static final int ACTION_COMPOSITE_NUM_COLUMNS = 2;

	// This value is set very high, so that it can accommodate rules with any number of controls/labels
	private static final int SINGLE_RULE_COMPOSITE_NUM_COLUMNS = 20;

	// This value is set very high, so that it can accommodate rule exceptions with any number of controls/labels
	private static final int SINGLE_EXCEPTION_COMPOSITE_NUM_COLUMNS = 20;

	private static final int EXCEPTIONS_COMPOSITE_INDENT = 25;

	private static final ConditionOperatorConfiguration CONDITION_OPERATOR_CONFIGURATION = new ConditionOperatorConfiguration();

	private IPolicyTargetLayoutComposite mainComposite;

	private CCombo conditionCombo;

	private IPolicyTargetLayoutComposite conditionWrapperComposite;

	private IPolicyTargetLayoutComposite actionWrapperComposite;

	private IPolicyTargetLayoutComposite ruleConditionsComposite;

	private IPolicyTargetLayoutComposite ruleActionsComposite;

	private final boolean catalogPromotion;
	
	private final StatePolicyDelegate statePolicyGovernable = new DefaultStatePolicyDelegateImpl();
	
	/**
	 * Policy for UI components used to define promotion rules.
	 */
	private final PolicyActionContainer rulesEditControls;
	
	private StatePolicy statePolicy;	

	private static final int HEIGHT_HINT_250 = 250;
	
	/**
	 * Constructor.
	 * 
	 * @param pageName the name of the page
	 * @param title the page title
	 * @param catalogPromotion whether the page should be created for a catalog promotion
	 */
	protected NewPromotionWizardRulesPage(final String pageName, final String title, final boolean catalogPromotion) {
		super(1, false, pageName, title, PromotionsMessages.get().CreatePromotionWizardRulesPage_Description, new DataBindingContext());
		this.catalogPromotion = catalogPromotion;
		
		rulesEditControls = addPolicyActionContainer("catalogPromotionEditor"); //$NON-NLS-1$
		
		statePolicy = new AbstractStatePolicyImpl() {

			@Override
			public EpState determineState(final PolicyActionContainer targetContainer) {
				return EpState.EDITABLE;
			}

			@Override
			public void init(final Object dependentObject) {
				// not applicable
			}
			
		};
	}

	@Override
	protected void createPageArea(final Composite parent) {

		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		layoutData.heightHint = HEIGHT_HINT_250;

		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.FLAT | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(layoutData);
		
		super.createPageArea(scrolledComposite);
		
		scrolledComposite.setContent(mainComposite.getSwtComposite());
		mainComposite.getSwtComposite().pack(true);
		mainComposite.getSwtComposite().layout(true, true);
		
		/* MUST be called */
		setControl(scrolledComposite);
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite parentComposite) {
		mainComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(parentComposite);

		createConditionControls();
		createActionControls();

		this.populateControls();
		this.bindControls();

		statePolicyGovernable.applyStatePolicy(statePolicy);
	}

	/**
	 * Creates the rule condition UI controls.
	 */
	private void createConditionControls() {
		final IEpLayoutData layoutData = this.mainComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER);
		this.conditionWrapperComposite = this.mainComposite.addGridLayoutComposite(CONDITION_COMPOSITE_NUM_COLUMNS, false, this.mainComposite
				.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL), rulesEditControls);

		this.conditionWrapperComposite.addLabelBold(PromotionsMessages.
				get().PromoRulesDefinition_Label_ConditionStart, layoutData, rulesEditControls);

		// any-all combo box
		this.conditionCombo = this.conditionWrapperComposite.addComboBox(null, rulesEditControls);
		this.conditionCombo.setItems(CONDITION_OPERATOR_CONFIGURATION.getAllLabels().toArray(new String[0]));
		this.conditionCombo.pack();

		this.conditionWrapperComposite.addLabelBold(PromotionsMessages.get().PromoRulesDefinition_Label_ConditionEnd, layoutData, rulesEditControls);

		// add condition icon
		final ImageHyperlink addConditionLink = this.conditionWrapperComposite.addHyperLinkImage(CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_ADD), layoutData, rulesEditControls);

		final Menu addConditionMenu = new Menu(conditionWrapperComposite.getSwtComposite());
		populateAddConditionMenu(addConditionMenu, getModel());
		addConditionLink.setMenu(addConditionMenu);
		// add listener so that menu will open on left click (as well as right click)
		addConditionLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(final MouseEvent mouseEvent) {
				addConditionMenu.setVisible(true);
				addConditionMenu.setLocation(CompositeLayoutUtility.getAbsoluteLocation(addConditionLink));
			}
		});

		this.ruleConditionsComposite = this.conditionWrapperComposite.addGridLayoutComposite(WRAPPER_COMPOSITE_NUM_COLUMNS, false,
			this.conditionWrapperComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false,
				CONDITION_COMPOSITE_NUM_COLUMNS, WRAPPER_COMPOSITE_NUM_COLUMNS), rulesEditControls);
	}

	/**
	 * Creates the rule action UI controls.
	 */
	private void createActionControls() {
		final IEpLayoutData layoutData = this.mainComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER);
		this.actionWrapperComposite = this.mainComposite.addGridLayoutComposite(ACTION_COMPOSITE_NUM_COLUMNS, false, this.mainComposite
			.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL), rulesEditControls);

		String actionLabel;
		if (catalogPromotion) {
			actionLabel = PromotionsMessages.get().PromoRulesDefinition_Label_Action_CatalogPromos;
		} else {
			actionLabel = PromotionsMessages.get().PromoRulesDefinition_Label_Action_CartPromos;
		}
		this.actionWrapperComposite.addLabelBold(actionLabel, layoutData, rulesEditControls);

		// add action icon
		final ImageHyperlink addActionLink = this.actionWrapperComposite.addHyperLinkImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD),
				layoutData, rulesEditControls);

		final Menu addActionMenu = new Menu(actionWrapperComposite.getSwtComposite());
		populateAddActionMenu(addActionMenu, getModel());
		addActionLink.setMenu(addActionMenu);
		// add listener so that menu will open on left click (as well as right click)
		addActionLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(final MouseEvent mouseEvent) {
				addActionMenu.setVisible(true);
				addActionMenu.setLocation(CompositeLayoutUtility.getAbsoluteLocation(addActionLink));
			}
		});

		this.ruleActionsComposite = this.actionWrapperComposite.addGridLayoutComposite(WRAPPER_COMPOSITE_NUM_COLUMNS, false,
				this.actionWrapperComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false, ACTION_COMPOSITE_NUM_COLUMNS,
						WRAPPER_COMPOSITE_NUM_COLUMNS), rulesEditControls);
	}

	@Override
	protected void populateControls() {
		this.conditionCombo.setText(CONDITION_OPERATOR_CONFIGURATION.getDefaultLabel());

		// Set the default value to the model for consistency between the UI and the model
		getModel().setConditionOperator(CONDITION_OPERATOR_CONFIGURATION.getDefaultConditionOperator());

		populateRuleConditions();
		populateRuleActions();
	}

	/**
	 * Populates add condition <code>Menu</code> with conditions supported by the rule.
	 * 
	 * @param menu the <code>Menu</code> to populate
	 */
	private void populateAddConditionMenu(final Menu menu, final Rule rule) {
		final RuleService ruleService = ServiceLocator.getService(ContextIdNames.RULE_SERVICE);
		final Map<Integer, List<RuleCondition>> allConditionsMap = ruleService.getAllConditionsMap();
		final List<RuleCondition> allConditionsList = allConditionsMap.get(getModel().getRuleSet().getScenario());

		for (final RuleCondition currCondition : allConditionsList) {

			// If it's Limited Usage Promotion or other (will be created in future External Rule), miss it:
			if (this.isExternalRule(currCondition)) {
				continue;
			}
			
			final MenuItem menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD));
			menuItem.setText(RulePresentationHelper.toMenuDisplayString(PromotionsMessages.get().getLocalizedName(currCondition.getElementType())));
			menuItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent event) {
					final RuleCondition newCondition = ServiceLocator.getService(currCondition.getType());
					// add the new condition to the rule
					rule.addCondition(newCondition);
					refreshRuleConditionsComposite();
				}
			});
		}
	}

	/**
	 * Populates the add action <code>Menu</code> with actions supported by the rule.
	 * 
	 * @param parentMenu the <code>Menu</code> to populate
	 */
	private void populateAddActionMenu(final Menu parentMenu, final Rule rule) {
		final RuleService ruleService = ServiceLocator.getService(ContextIdNames.RULE_SERVICE);
		final Map<Integer, List<RuleAction>> allActionsMap = ruleService.getAllActionsMap();
		final List<RuleAction> allActionsList = allActionsMap.get(getModel().getRuleSet().getScenario());

		for (final DiscountType currDiscountType : DiscountType.values()) {
			// Create and populate a sub-menu for each discount type
			Menu subMenu = new Menu(parentMenu);

			// Add rule actions that are of this sub menu's discount type
			for (final RuleAction currAction : allActionsList) {
				if (currAction.getDiscountType().equals(currDiscountType)) {
					final MenuItem menuItem = new MenuItem(subMenu, SWT.NONE);
					menuItem.setImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD));
					menuItem.setText(RulePresentationHelper.toMenuDisplayString(PromotionsMessages.
							get().getLocalizedName(currAction.getElementType())));
					menuItem.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent event) {
							final RuleAction newAction = ServiceLocator.getService(currAction.getType());
							// add the new action to the rule
							rule.addAction(newAction);
							refreshRuleActionsComposite();
						}
					});
				}
			}

			// Only add the sub menu if it contains menu items
			if (subMenu.getItemCount() > 0) {
				MenuItem currDiscountTypeMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
				currDiscountTypeMenuItem.setText(PromotionsMessages.get().getLocalizedName(currDiscountType));
				currDiscountTypeMenuItem.setMenu(subMenu);
			}
		}
	}

	/**
	 * Populates the add exception <code>Menu</code> with exceptions supported by the rule element.
	 * 
	 * @param menu the <code>Menu</code> to populate
	 * @param ruleElement the <code>RuleElement</code> that the menu is to be created for
	 */
	private void populateAddExceptionMenu(final Menu menu, final RuleElement ruleElement) {
		final RuleExceptionType[] exceptionKeys = ruleElement.getAllowedExceptions();

		if ((exceptionKeys != null) && (exceptionKeys.length > 0)) {
			for (final RuleExceptionType currExceptionKey : exceptionKeys) {
				final MenuItem menuItem = new MenuItem(menu, SWT.NONE);
				menuItem.setImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD));

				final RuleException ruleException = ServiceLocator.getService(
						currExceptionKey.getPropertyKey());

				menuItem.setText(RulePresentationHelper.toMenuDisplayString(PromotionsMessages.
						get().getLocalizedName(ruleException.getExceptionType())));
				menuItem.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent event) {
						final RuleException newException = ServiceLocator.getService(
								ruleException.getType());
						// add the new exception to the rule element
						ruleElement.addException(newException);

						// refresh the composite
						if (ruleElement instanceof RuleCondition) {
							refreshRuleConditionsComposite();
						} else if (ruleElement instanceof RuleAction) {
							refreshRuleActionsComposite();
						}
					}
				});
			}
		}
	}

	/**
	 * Populates the rule conditions. The rule conditions are created, populated, and binded by this method.
	 */
	private void populateRuleConditions() {
		final IEpLayoutData layoutData = this.ruleConditionsComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER);
		final Set<RuleCondition> conditions = getModel().getConditions();

		// iterate through the rule's conditions
		for (final RuleCondition currCondition : conditions) {

			// If it's Limited Usage Promotion or other (will be created in future External Rule), miss it:
			if (this.isExternalRule(currCondition)) {
				continue;
			}

			final IPolicyTargetLayoutComposite singleRuleWrapperComposite = this.ruleConditionsComposite.addGridLayoutComposite(
					WRAPPER_COMPOSITE_NUM_COLUMNS, false, 
					this.ruleConditionsComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL), rulesEditControls);
			((GridLayout) singleRuleWrapperComposite.getSwtComposite().getLayout()).marginHeight = 1;
			final IPolicyTargetLayoutComposite singleRuleComposite = singleRuleWrapperComposite.addGridLayoutComposite(
					SINGLE_RULE_COMPOSITE_NUM_COLUMNS, false, 
					singleRuleWrapperComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL), rulesEditControls);
			((GridLayout) singleRuleComposite.getSwtComposite().getLayout()).marginHeight = 0;

			// remove condition icon
			final ImageHyperlink removeIcon = singleRuleComposite.addHyperLinkImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_REMOVE),
					layoutData, rulesEditControls);
			removeIcon.setToolTipText(PromotionsMessages.get().PromoRulesDefinition_Tooltip_RemoveCondition);
			removeIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseUp(final MouseEvent mouseEvent) {
					// remove the selected condition
					getModel().removeCondition(currCondition);
					singleRuleWrapperComposite.getSwtComposite().dispose();
					refreshRootComposite();
				}
			});

			String displayText =
				NLS.bind(PromotionsMessages.get().getLocalizedName(currCondition.getElementType()),
					currCondition.getParameterKeys());

			int fromIndex = 0;
			int toIndex = 0;

			if (currCondition.getParameterKeys() != null) {
				// iterate through the parameters of this rule condition
				for (String currRuleParameterKey : currCondition.getParameterKeys()) {
					toIndex = displayText.indexOf(BEG_PARAM_DELIMITER + currRuleParameterKey + END_PARAM_DELIMITER);

					// display eligibility text as a label
					singleRuleComposite.addLabel(displayText.substring(fromIndex, toIndex), layoutData, rulesEditControls);
					fromIndex = toIndex + BEG_PARAM_DELIMITER.length() + currRuleParameterKey.length() + END_PARAM_DELIMITER.length();
					// create the appropriate control widget for each parameter
					createRuleParameterControl(getRuleParameterByKey(currCondition, currRuleParameterKey), singleRuleComposite);
				}
			}

			if (toIndex < displayText.length()) {
				toIndex = displayText.length();
				// display eligibility text as a label
				singleRuleComposite.addLabel(displayText.substring(fromIndex, toIndex), layoutData, rulesEditControls);
			}

			// populate the rule exceptions
			final RuleExceptionType[] allowedExceptions = currCondition.getAllowedExceptions();
			if ((allowedExceptions != null) && (allowedExceptions.length != 0)) {
				populateConditionExceptions(currCondition, singleRuleWrapperComposite, singleRuleComposite);
			}

			// state policy must be the last action to enable all buttons drawn on composites
			if (statePolicy != null) {
				statePolicyGovernable.applyStatePolicy(statePolicy);
			}
		}
	}

	/*
	 * If it's Limited Usage Promotion toIndex == -1 or Coupon Code Condition, miss it:
	 */
	private boolean isExternalRule(final RuleCondition currCondition) {
		return currCondition instanceof ImpliedRuleCondition;
	}

	/**
	 * Populates the rule actions. The rule actions are created, populated, and bound by this method.
	 */
	private void populateRuleActions() {
		final IEpLayoutData layoutData = this.ruleActionsComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER);
		final Set<RuleAction> actions = getModel().getActions();

		// iterate through the rule's actions
		for (final RuleAction currAction : actions) {
			final IPolicyTargetLayoutComposite singleRuleWrapperComposite = this.ruleActionsComposite.addGridLayoutComposite(
				WRAPPER_COMPOSITE_NUM_COLUMNS, false,
				this.ruleActionsComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL), rulesEditControls);
			((GridLayout) singleRuleWrapperComposite.getSwtComposite().getLayout()).marginHeight = 1;
			final IPolicyTargetLayoutComposite singleRuleComposite = createSingleRuleComposite(singleRuleWrapperComposite);

			// remove action icon
			final ImageHyperlink removeIcon = singleRuleComposite.addHyperLinkImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_REMOVE),
				layoutData, rulesEditControls);
			removeIcon.setToolTipText(PromotionsMessages.get().PromoRulesDefinition_Tooltip_RemoveAction);
			removeIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseUp(final MouseEvent mouseEvent) {
					// remove the selected action
					getModel().removeAction(currAction);
					singleRuleWrapperComposite.getSwtComposite().dispose();
					setErrorMessage(null);
					refreshRootComposite();
				}
			});

			String displayText =
				NLS.bind(PromotionsMessages.get().getLocalizedName(currAction.getElementType()),
					currAction.getParameterKeys());

			int fromIndex = 0;
			int toIndex = 0;


			if (currAction.getParameterKeys() != null) {
				// iterate through the parameters of this rule action
				for (String currRuleParameterKey : currAction.getParameterKeys()) {
					toIndex = displayText.indexOf(BEG_PARAM_DELIMITER + currRuleParameterKey + END_PARAM_DELIMITER);
					// display eligibility text as a label
					String subDisplayText = displayText.substring(fromIndex, toIndex);
					IPolicyTargetLayoutComposite ruleComposite;
					if (subDisplayText.startsWith("\n")) { //$NON-NLS-1$
						ruleComposite = createSingleRuleComposite(singleRuleWrapperComposite);
						subDisplayText = subDisplayText.substring(1); //get rid of "\n" from the display text
					} else {
						ruleComposite = singleRuleComposite;
					}

					ruleComposite.addLabel(subDisplayText, layoutData, rulesEditControls);
					fromIndex = toIndex + BEG_PARAM_DELIMITER.length() + currRuleParameterKey.length() + END_PARAM_DELIMITER.length();
					// create the appropriate control widget for each parameter
					createRuleParameterControl(getRuleParameterByKey(currAction, currRuleParameterKey), ruleComposite);
				}
			}

			if (toIndex < displayText.length()) {
				toIndex = displayText.length();
				// display eligibility text as a label
				singleRuleComposite.addLabel(displayText.substring(fromIndex, toIndex), layoutData, rulesEditControls);
			}

			// populate the rule exceptions
			final RuleExceptionType[] allowedExceptions = currAction.getAllowedExceptions();
			if ((allowedExceptions != null) && (allowedExceptions.length != 0)) {
				populateActionExceptions(currAction, singleRuleWrapperComposite, singleRuleComposite);
			}

			// state policy must be the last action to enable all buttons drawn on composites
			if (statePolicy != null) {
				statePolicyGovernable.applyStatePolicy(statePolicy);
			}
		}
	}

	private IPolicyTargetLayoutComposite createSingleRuleComposite(
		final IPolicyTargetLayoutComposite singleRuleWrapperComposite) {
		IPolicyTargetLayoutComposite singleRuleComposite = singleRuleWrapperComposite.addGridLayoutComposite(
			SINGLE_RULE_COMPOSITE_NUM_COLUMNS, false,
			singleRuleWrapperComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL), rulesEditControls);
		((GridLayout) singleRuleComposite.getSwtComposite().getLayout()).marginHeight = 0;

		return singleRuleComposite;
	}

	/**
	 * Populates the rule condition exceptions. The exceptions are created, populated, and bound by this method.
	 */
	private void populateConditionExceptions(final RuleCondition condition, final IPolicyTargetLayoutComposite singleRuleWrapperComposite,
		final IPolicyTargetLayoutComposite singleRuleComposite) {
		final IEpLayoutData layoutData = singleRuleComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER);

		singleRuleComposite.addLabel(PromotionsMessages.get().PromoRulesDefinition_Label_Excluding, layoutData, rulesEditControls);

		// add exception menu
		final ImageHyperlink addExceptionIcon = singleRuleComposite.addHyperLinkImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD),
			layoutData, rulesEditControls);

		final Menu addExceptionMenu = new Menu(singleRuleComposite.getSwtComposite());
		addExceptionIcon.setMenu(addExceptionMenu);
		addExceptionIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(final MouseEvent mouseEvent) {
				addExceptionMenu.setVisible(true);
				addExceptionMenu.setLocation(CompositeLayoutUtility.getAbsoluteLocation(addExceptionIcon));
			}
		});

		populateAddExceptionMenu(addExceptionMenu, condition);

		final Set<RuleException> exceptions = condition.getExceptions();

		if (!exceptions.isEmpty()) {
			final IPolicyTargetLayoutComposite exceptionsWrapperComposite = singleRuleWrapperComposite.addGridLayoutComposite(
				WRAPPER_COMPOSITE_NUM_COLUMNS, false,
				singleRuleWrapperComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true), rulesEditControls);
			GridLayout gridLayout = (GridLayout) exceptionsWrapperComposite.getSwtComposite().getLayout();
			gridLayout.marginLeft = EXCEPTIONS_COMPOSITE_INDENT;

			// iterate through the rule condition's exceptions
			for (final RuleException currException : exceptions) {
				final IPolicyTargetLayoutComposite singleExceptionComposite = exceptionsWrapperComposite.addGridLayoutComposite(
					SINGLE_EXCEPTION_COMPOSITE_NUM_COLUMNS, false, exceptionsWrapperComposite.createLayoutData(IEpLayoutData.FILL,
						IEpLayoutData.FILL, true, true), rulesEditControls);
				((GridLayout) singleExceptionComposite.getSwtComposite().getLayout()).marginHeight = 0;
				// remove exception icon
				final ImageHyperlink removeExceptionIcon = singleExceptionComposite.addHyperLinkImage(CoreImageRegistry
					.getImage(CoreImageRegistry.IMAGE_REMOVE), layoutData, rulesEditControls);
				removeExceptionIcon.setToolTipText(PromotionsMessages.get().PromoRulesDefinition_Tooltip_RemoveException);
				removeExceptionIcon.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseUp(final MouseEvent mouseEvent) {
						// remove the selected exception
						exceptions.remove(currException);
						// markDirty();
						singleExceptionComposite.getSwtComposite().dispose();
						refreshRootComposite();

					}
				});

				String exceptionDisplayText =
					NLS.bind(PromotionsMessages.get().getLocalizedName(currException.getExceptionType()),
						currException.getParameterKeys());

				int fromIndex = 0;
				int toIndex = 0;

				if (currException.getParameterKeys() != null) {
					// iterate through the parameters of this rule exception
					for (String currExceptionParameterKey : currException.getParameterKeys()) {
						toIndex = exceptionDisplayText.indexOf(BEG_PARAM_DELIMITER + currExceptionParameterKey + END_PARAM_DELIMITER);
						singleExceptionComposite.addLabel(exceptionDisplayText.substring(fromIndex, toIndex), layoutData, rulesEditControls);
						fromIndex = toIndex + BEG_PARAM_DELIMITER.length() + currExceptionParameterKey.length() + END_PARAM_DELIMITER.length();
						// create the appropriate control widget for each parameter
						createRuleParameterControl(getRuleParameterByKey(currException, currExceptionParameterKey), singleExceptionComposite);
					}
				}

				if (toIndex < exceptionDisplayText.length()) {
					toIndex = exceptionDisplayText.length();
					singleExceptionComposite.addLabel(exceptionDisplayText.substring(fromIndex, toIndex), layoutData, rulesEditControls);
				}

				// state policy must be the last action to enable all buttons drawn on composites
				if (statePolicy != null) {
					statePolicyGovernable.applyStatePolicy(statePolicy);
				}
			}
		}
	}

	/**
	 * Populates the rule action exceptions. The exceptions are created, populated, and bound by this method.
	 */
	private void populateActionExceptions(final RuleAction action, final IPolicyTargetLayoutComposite singleRuleWrapperComposite,
		final IPolicyTargetLayoutComposite singleRuleComposite) {
		final IEpLayoutData layoutData = singleRuleComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER);

		singleRuleComposite.addLabel(PromotionsMessages.get().PromoRulesDefinition_Label_Excluding, layoutData, rulesEditControls);

		// add exception menu
		final ImageHyperlink addExceptionIcon = singleRuleComposite.addHyperLinkImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD),
			layoutData, rulesEditControls);

		final Menu addExceptionMenu = new Menu(singleRuleComposite.getSwtComposite());
		addExceptionIcon.setMenu(addExceptionMenu);
		addExceptionIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(final MouseEvent mouseEvent) {
				addExceptionMenu.setVisible(true);
				addExceptionMenu.setLocation(CompositeLayoutUtility.getAbsoluteLocation(addExceptionIcon));
			}
		});

		populateAddExceptionMenu(addExceptionMenu, action);

		final Set<RuleException> exceptions = action.getExceptions();

		if (!exceptions.isEmpty()) {
			final IPolicyTargetLayoutComposite exceptionsWrapperComposite = singleRuleWrapperComposite.addGridLayoutComposite(
				WRAPPER_COMPOSITE_NUM_COLUMNS, false,
				singleRuleWrapperComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true), rulesEditControls);
			GridLayout gridLayout = (GridLayout) exceptionsWrapperComposite.getSwtComposite().getLayout();
			gridLayout.marginLeft = EXCEPTIONS_COMPOSITE_INDENT;

			// iterate through the rule action's exceptions
			for (final RuleException currException : exceptions) {
				final IPolicyTargetLayoutComposite singleExceptionComposite = exceptionsWrapperComposite.addGridLayoutComposite(
						SINGLE_EXCEPTION_COMPOSITE_NUM_COLUMNS, false, exceptionsWrapperComposite.createLayoutData(IEpLayoutData.FILL,
								IEpLayoutData.FILL, true, true), rulesEditControls);
				((GridLayout) singleExceptionComposite.getSwtComposite().getLayout()).marginHeight = 0;
				// remove exception icon
				final ImageHyperlink removeExceptionIcon = singleExceptionComposite.addHyperLinkImage(CoreImageRegistry
						.getImage(CoreImageRegistry.IMAGE_REMOVE), layoutData, rulesEditControls);
				removeExceptionIcon.setToolTipText(PromotionsMessages.get().PromoRulesDefinition_Tooltip_RemoveException);
				removeExceptionIcon.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseUp(final MouseEvent mouseEvent) {
						// remove the selected exception
						exceptions.remove(currException);
						// markDirty();
						singleExceptionComposite.getSwtComposite().dispose();
						refreshRootComposite();
					}
				});

				String exceptionDisplayText = NLS.bind(
					PromotionsMessages.get().getLocalizedName(currException.getExceptionType()), currException.getParameterKeys());

				int fromIndex = 0;
				int toIndex = 0;

				if (currException.getParameterKeys() != null) {
					// iterate through the parameters of this rule exception
					for (String currExceptionParameterKey : currException.getParameterKeys()) {
						toIndex = exceptionDisplayText.indexOf(BEG_PARAM_DELIMITER + currExceptionParameterKey + END_PARAM_DELIMITER);
						singleExceptionComposite.addLabel(exceptionDisplayText.substring(fromIndex, toIndex), layoutData, rulesEditControls);
						fromIndex = toIndex + BEG_PARAM_DELIMITER.length() + currExceptionParameterKey.length() + END_PARAM_DELIMITER.length();
						// create the appropriate control widget for each parameter
						createRuleParameterControl(getRuleParameterByKey(currException, currExceptionParameterKey), singleExceptionComposite);
					}
				}

				if (toIndex < exceptionDisplayText.length()) {
					toIndex = exceptionDisplayText.length();
					singleExceptionComposite.addLabel(exceptionDisplayText.substring(fromIndex, toIndex), layoutData, rulesEditControls);
				}

				// state policy must be the last action to enable all buttons drawn on composites
				if (statePolicy != null) {
					statePolicyGovernable.applyStatePolicy(statePolicy);
				}
			}
		}
	}

	/**
	 * Refreshes the rule conditions composite by disposing, recreating, and then re-populating it.
	 */
	private void refreshRuleConditionsComposite() {
		// dispose the composite
		this.ruleConditionsComposite.getSwtComposite().dispose();
		// recreate the composite
		this.ruleConditionsComposite = this.conditionWrapperComposite.addGridLayoutComposite(WRAPPER_COMPOSITE_NUM_COLUMNS, false,
			this.conditionWrapperComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false,
				CONDITION_COMPOSITE_NUM_COLUMNS, WRAPPER_COMPOSITE_NUM_COLUMNS), rulesEditControls);
		// re-populate the composite
		populateRuleConditions();

		refreshRootComposite();
	}

	/**
	 * Refreshes the rule actions composite by disposing, recreating, and then re-populating it.
	 */
	private void refreshRuleActionsComposite() {
		// dispose the composite
		this.ruleActionsComposite.getSwtComposite().dispose();
		// recreate the composite
		this.ruleActionsComposite = this.actionWrapperComposite.addGridLayoutComposite(WRAPPER_COMPOSITE_NUM_COLUMNS, false,
			this.actionWrapperComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false, ACTION_COMPOSITE_NUM_COLUMNS,
				WRAPPER_COMPOSITE_NUM_COLUMNS), rulesEditControls);
		// re-populate the composite
		populateRuleActions();

		refreshRootComposite();
	}

	/**
	 * Creates the appropriate SWT widget depending on the type of rule parameter passed.
	 *
	 * @param ruleParameter the <code>RuleParameter</code> object to bind the widget to
	 * @param ruleComposite the <code>Composite</code> to which the number of items link should be added to
	 */
	private void createRuleParameterControl(final RuleParameter ruleParameter, final IPolicyTargetLayoutComposite ruleComposite) {
		PromotionRulesWidgetUtil.getInstance(getModel()).createRuleParameterControl(this, ruleParameter, ruleComposite, rulesEditControls);
	}

	/**
	 * Returns the RuleParamter with the given paramKey inside the given ruleException.
	 *
	 * @param ruleException the RuleException object to scan
	 * @param paramKey the String key of the RuleParameter object to retrieve
	 * @return the RuleParameter with the given paramKey
	 */
	private RuleParameter getRuleParameterByKey(final RuleException ruleException, final String paramKey) {
		for (RuleParameter currRuleParameter : ruleException.getParameters()) {
			if (paramKey.equals(currRuleParameter.getKey())) {
				return currRuleParameter;
			}
		}
		return null;
	}

	/**
	 * Calls layout(true) on the root composite. Used to refresh the main composite.
	 */
	private void refreshRootComposite() {
		this.mainComposite.getSwtComposite().setRedraw(false);
		this.mainComposite.getSwtComposite().pack(true);
		this.mainComposite.getSwtComposite().layout(true, true); // FIXME: find a better way to refresh
		this.mainComposite.getSwtComposite().setRedraw(true);
	}

	@Override
	protected void bindControls() {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		// condition operator
		bindingProvider.bind(getDataBindingContext(), this.conditionCombo, null, null, new ConditionOperatorMenuUpdateValueStrategy(getModel(),
			CONDITION_OPERATOR_CONFIGURATION), true);

		EpWizardPageSupport.create(NewPromotionWizardRulesPage.this, getDataBindingContext());
	}

	@Override
	public boolean isPageComplete() {
		if (hasRequiredRules() && checkProductCategorySkuPromotionSelected()) {
			return super.isPageComplete();
		}
		return false;

	}

	private boolean hasRequiredRules() {
		// ensure that there is at least 1 rule condition or action defined
		return !(getModel().getConditions().size() < 1 || getModel().getActions().size() < 1);
	}

	/**
	 * Returns true if all rule elements with Product/Category/SKU values have valid values (i.e. valid means the user has selected a specific
	 * Product/Category/SKU; false otherwise.
	 *
	 * @return true if all rule elements with Product/Category/SKU values have valid values (i.e. valid means the user has selected a specific
	 *         Product/Category/SKU; false otherwise.
	 */
	private boolean checkProductCategorySkuPromotionSelected() {
		// Iterate through the Rule's rule-elements
		for (RuleElement currRuleElement : getModel().getRuleElements()) {

			// Check the parameters of the rule-elements
			for (RuleParameter currElementParam : currRuleElement.getParameters()) {
				if (!isRuleParameterComplete(currElementParam)) {
					return false;
				}
			}

			// Check the parameters of rule-exceptions
			for (RuleException currRuleException : currRuleElement.getExceptions()) {
				for (RuleParameter currExceptionParam : currRuleException.getParameters()) {
					if (!isRuleParameterComplete(currExceptionParam)) {
						return false;
					}
				}
			}
		}
		//if we got this far without returning false and we have previously set the error message
		// then remove it and refresh the rule action composite
		//see PromotionRulesWidget.addSkuFinderLink
		if (StringUtils.equals(getErrorMessage(),
			PromotionsMessages.get().CreatePromotion_GiftCertificateError)) {
			setErrorMessage(null);
			refreshRuleActionsComposite();
		}
		return true;
	}

	/**
	 * Returns true if all Product/Category/SKU values have been selected by the user; false otherwise.
	 *
	 * @param parameter the RuleParameter to check
	 * @return true if all Product/Category/SKU values have been selected by the user; false otherwise.
	 */
	private boolean isRuleParameterComplete(final RuleParameter parameter) {
		final boolean productIsNull = parameter.getKey().equals(RuleParameter.PRODUCT_CODE_KEY) && (parameter.getValue() == null);
		final boolean categoryIsNull = parameter.getKey().equals(RuleParameter.CATEGORY_CODE_KEY) && (parameter.getValue() == null);
		final boolean skuIsNull = parameter.getKey().equals(RuleParameter.SKU_CODE_KEY) && (parameter.getValue() == null);
		final boolean promotionisNull = parameter.getKey().equals(RuleParameter.RULE_CODE_KEY) && (parameter.getValue() == null);

		return !productIsNull && !categoryIsNull && !skuIsNull && !promotionisNull;
	}

	@Override
	public PolicyActionContainer addPolicyActionContainer(final String name) {
		return statePolicyGovernable.addPolicyActionContainer(name);
	}

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		this.statePolicy = statePolicy;

		statePolicyGovernable.applyStatePolicy(statePolicy);
	}

	@Override
	public Map<String, PolicyActionContainer> getPolicyActionContainers() {
		return statePolicyGovernable.getPolicyActionContainers();
	}

	@Override
	public void refreshLayout() {
		refreshRootComposite();
	}
}