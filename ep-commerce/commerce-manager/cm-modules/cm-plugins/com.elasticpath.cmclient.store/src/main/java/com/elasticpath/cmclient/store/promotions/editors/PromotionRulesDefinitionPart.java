/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.CmClientResources;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.helpers.CompositeLayoutUtility;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.promotions.LayoutRefresher;
import com.elasticpath.cmclient.store.promotions.PromotionRulesWidgetUtil;
import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.cmclient.store.promotions.helpers.PromotionBrandValidator;
import com.elasticpath.cmclient.store.promotions.wizard.ConditionOperatorConfiguration;
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
 * UI representation of the promotion rules definition section.
 */
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.TooManyFields", "PMD.TooManyMethods",
	"PMD.ExcessiveImports", "PMD.GodClass", "PMD.PrematureDeclaration" })
public class PromotionRulesDefinitionPart extends AbstractPolicyAwareEditorPageSectionPart implements DisposeListener, LayoutRefresher {

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

	private static final int REQUIRE_COND_VALIDATION_TEXT_LENGTH = 225;

	private static final int REQUIRE_ACTION_VALIDATION_TEXT_LENGTH = 225;

	private static final ConditionOperatorConfiguration CONDITION_OPERATOR_CONFIGURATION = new ConditionOperatorConfiguration();

	private final ControlModificationListener controlModificationListener;

	private final Rule rule;

	private IPolicyTargetLayoutComposite mainComposite;

	private Menu addConditionMenu;

	private Menu addActionMenu;

	private CCombo anyAllConditionCombo;

	private IPolicyTargetLayoutComposite conditionWrapperComposite;

	private IPolicyTargetLayoutComposite actionWrapperComposite;

	private IPolicyTargetLayoutComposite ruleConditionsComposite;

	private IPolicyTargetLayoutComposite ruleActionsComposite;

	private Text requireConditionValidationText;

	private Text requireActionValidationText;

	private final boolean catalogPromotion;

	private final EpControlFactory controlFactory = EpControlFactory.getInstance();

	/**
	 * Policy for UI components used to define promotion rules.
	 */
	private final PolicyActionContainer rulesEditControls;
	
	/**
	 * Policy for UI components only used for display and validation purposes.
	 */
	private final PolicyActionContainer rulesDisplayControls;
	
	private StatePolicy statePolicy;

	/**
	 * Constructor.
	 * 
	 * @param editor the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 * @param formPage the form page
	 * @param catalogPromotion whether to create the page for a catalog promotion
	 */
	public PromotionRulesDefinitionPart(final FormPage formPage, final AbstractCmClientFormEditor editor, final boolean catalogPromotion) {
		super(formPage, editor, ExpandableComposite.EXPANDED);
		this.rule = (Rule) editor.getModel();
		this.controlModificationListener = editor;
		this.catalogPromotion = catalogPromotion;
		
		rulesEditControls = addPolicyActionContainer("rulesEditControls"); //$NON-NLS-1$
		rulesDisplayControls = addPolicyActionContainer("rulesDisplayControls"); //$NON-NLS-1$
	}

	@Override
	protected void createControls(final Composite parentComposite, final FormToolkit toolkit) {
		this.mainComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(
				CompositeFactory.createGridLayoutComposite(parentComposite, WRAPPER_COMPOSITE_NUM_COLUMNS, false));
		this.mainComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
		
		createConditionControls();
		createActionControls();
		
		addCompositesToRefresh(mainComposite.getSwtComposite().getParent());
	}

	/**
	 * Creates the rule condition UI controls.
	 */
	private void createConditionControls() {
		final IEpLayoutData layoutData = this.mainComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER);
		this.conditionWrapperComposite = this.mainComposite.addGridLayoutComposite(CONDITION_COMPOSITE_NUM_COLUMNS, false, this.mainComposite
				.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false), rulesEditControls);

		this.conditionWrapperComposite.addLabelBold(PromotionsMessages.get().PromoRulesDefinition_Label_ConditionStart,
				layoutData, rulesEditControls);

		// any-all combo box
		this.anyAllConditionCombo = this.conditionWrapperComposite.addComboBox(null, rulesEditControls);
		this.anyAllConditionCombo.setItems(CONDITION_OPERATOR_CONFIGURATION.getAllLabels().toArray(new String[0]));
		this.anyAllConditionCombo.pack();

		this.conditionWrapperComposite.addLabelBold(PromotionsMessages.get().PromoRulesDefinition_Label_ConditionEnd, layoutData, rulesEditControls);

		// add condition icon
		final ImageHyperlink addConditionLink = this.conditionWrapperComposite.addHyperLinkImage(CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_ADD), layoutData, rulesEditControls);

		this.addConditionMenu = new Menu(this.conditionWrapperComposite.getSwtComposite());
		addConditionLink.setMenu(this.addConditionMenu);
		addConditionLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent mouseEvent) {
				addConditionMenu.setVisible(true);
				addConditionMenu.setLocation(CompositeLayoutUtility.getAbsoluteLocation(addConditionLink));
			}
		});

		this.ruleConditionsComposite = this.conditionWrapperComposite.addGridLayoutComposite(WRAPPER_COMPOSITE_NUM_COLUMNS, false,
				this.conditionWrapperComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false,
						CONDITION_COMPOSITE_NUM_COLUMNS, WRAPPER_COMPOSITE_NUM_COLUMNS), rulesEditControls);
	}

	/**
	 * Creates the rule action UI controls.
	 */
	private void createActionControls() {
		final IEpLayoutData layoutData = this.mainComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER);
		this.actionWrapperComposite = this.mainComposite.addGridLayoutComposite(ACTION_COMPOSITE_NUM_COLUMNS, false, this.mainComposite
				.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, true, false), rulesEditControls);

		String actionLabel;
		if (catalogPromotion) {
			actionLabel = PromotionsMessages.get().PromoRulesDefinition_Label_Action_CatalogPromos;
		} else {
			actionLabel = PromotionsMessages.get().PromoRulesDefinition_Label_Action_CartPromos;
		}
		this.actionWrapperComposite.addLabelBold(actionLabel, layoutData, rulesEditControls);

		// add action icon
		final ImageHyperlink addActionLink = this.actionWrapperComposite.addHyperLinkImage(
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD), layoutData, rulesEditControls);

		this.addActionMenu = new Menu(this.actionWrapperComposite.getSwtComposite());
		addActionLink.setMenu(this.addActionMenu);
		addActionLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent mouseEvent) {
				addActionMenu.setVisible(true);
				addActionMenu.setLocation(CompositeLayoutUtility.getAbsoluteLocation(addActionLink));
			}
		});

		this.ruleActionsComposite = this.actionWrapperComposite.addGridLayoutComposite(WRAPPER_COMPOSITE_NUM_COLUMNS, false,
				this.actionWrapperComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, true, false, ACTION_COMPOSITE_NUM_COLUMNS,
						WRAPPER_COMPOSITE_NUM_COLUMNS), rulesEditControls);
	}

	@Override
	protected void populateControls() {
		// populate any-all combo boxes
		this.anyAllConditionCombo.setText(CONDITION_OPERATOR_CONFIGURATION.getLabelForConditionOperator(rule.getConditionOperator()));

		populateAddConditionMenu(this.addConditionMenu, this.rule);
		populateAddActionMenu(this.addActionMenu, this.rule);

		populateRuleConditions();
		populateRuleActions();

		warnUserOfMissingBrands();

		// Set the modification listener *after* setting the control values
		// so that controls aren't considered to be modified when the initial value is set
		this.conditionWrapperComposite.setControlModificationListener(this.controlModificationListener);
	}


	private void warnUserOfMissingBrands() {

		// warns users if the brands can't be found
		// for products, change obejct type to product, and append to error message a new NLS bound message.

		final PromotionBrandValidator promotionBrandValidator = new PromotionBrandValidator();
		final List<String> invalidBrands = promotionBrandValidator.getInvalidBrandsFor(rule);
		final String comma = ", "; //$NON-NLS-1$
		if (!invalidBrands.isEmpty()) {
			String invalidBrandString = StringUtils.join(invalidBrands, comma);
			String objectTypeMessage = "Brand was"; //$NON-NLS-1$

			if (invalidBrands.size() > 1) {
				objectTypeMessage = "Brands were";  //$NON-NLS-1$
			}
			String errorMessage =
				NLS.bind(PromotionsMessages.get().PromoRulesDefinition_Error_Brand,
				new Object[]{objectTypeMessage, invalidBrandString});
			MessageDialog.openWarning(anyAllConditionCombo.getShell(), PromotionsMessages.get().PromoRulesDefintion_Error_Title,
					errorMessage);
		}
	}
	
	/**
	 * Populates add condition <code>Menu</code> with conditions supported by the rule.
	 * 
	 * @param menu the <code>Menu</code> to populate
	 */
	private void populateAddConditionMenu(final Menu menu, final Rule rule) {
		final RuleService ruleService = ServiceLocator.getService(ContextIdNames.RULE_SERVICE);
		final Map<Integer, List<RuleCondition>> allConditionsMap = ruleService.getAllConditionsMap();
		final List<RuleCondition> allConditionsList = allConditionsMap.get(rule.getRuleSet().getScenario());

		for (final RuleCondition currCondition : allConditionsList) {

			// If it's Limited Usage Promotion or other (will be created in future External Rule), miss it:
			if (this.isExternalRule(currCondition)) {
				continue;
			}

			final MenuItem menuItem = controlFactory.createMenuItem(menu, SWT.NONE,
				RulePresentationHelper.toMenuDisplayString(PromotionsMessages.get().getLocalizedName(currCondition.getElementType())),
				new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent event) {
						final RuleCondition newCondition = ServiceLocator.getService(currCondition.getType());
						// add the new condition to the rule
						rule.addCondition(newCondition);
						markDirty();
						updateActionRequirementWidget();
						refreshRuleConditionsComposite();
					}
				}
			);
			menuItem.setImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD));
		}
	}

	/*
	 * If it's Limited Usage Promotion toIndex == -1 or Coupon Code Condition, miss it.
	 */
	private boolean isExternalRule(final RuleCondition currCondition) {
		return currCondition instanceof ImpliedRuleCondition;
	}

	/**
	 * Populates the add action <code>Menu</code> with actions supported by the rule.
	 * 
	 * @param parentMenu the <code>Menu</code> to populate
	 */
	private void populateAddActionMenu(final Menu parentMenu, final Rule rule) {
		final RuleService ruleService = ServiceLocator.getService(ContextIdNames.RULE_SERVICE);
		final Map<Integer, List<RuleAction>> allActionsMap = ruleService.getAllActionsMap();
		final List<RuleAction> allActionsList = allActionsMap.get(rule.getRuleSet().getScenario());

		for (final DiscountType currDiscountType : DiscountType.values()) {
			// Create and populate a sub-menu for each discount type
			Menu subMenu = new Menu(parentMenu);

			// Add rule actions that are of this sub menu's discount type
			for (final RuleAction currAction : allActionsList) {
				if (currAction.getDiscountType().equals(currDiscountType)) {
					final MenuItem menuItem = controlFactory.createMenuItem(subMenu, SWT.NONE,
						RulePresentationHelper.toMenuDisplayString(PromotionsMessages.get().getLocalizedName(currAction.getElementType())),
						new SelectionAdapter() {
							@Override
							public void widgetSelected(final SelectionEvent event) {
								final RuleAction newAction = ServiceLocator.getService(currAction.getType());
								// add the new action to the rule
								rule.addAction(newAction);
								markDirty();
								refreshRuleActionsComposite();
							}
						});
					menuItem.setImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD));
				}
			}

			// Only add the sub menu if it contains menu items
			if (subMenu.getItemCount() > 0) {
				MenuItem currDiscountTypeMenuItem = controlFactory.createMenuItem(parentMenu, SWT.CASCADE,
					PromotionsMessages.get().getLocalizedName(currDiscountType));
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
				final RuleException ruleException = ServiceLocator.getService(currExceptionKey.getPropertyKey());

				final MenuItem menuItem = controlFactory.createMenuItem(menu, SWT.NONE,
					RulePresentationHelper.toMenuDisplayString(PromotionsMessages.get().getLocalizedName(ruleException.getExceptionType())),
					new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent event) {
							final RuleException newException = ServiceLocator.getService(
								ruleException.getType());
							// add the new exception to the rule element
							ruleElement.addException(newException);
							markDirty();
							// refresh the composite
							if (ruleElement instanceof RuleCondition) {
								refreshRuleConditionsComposite();
							} else if (ruleElement instanceof RuleAction) {
								refreshRuleActionsComposite();
							}
						}
					});
				menuItem.setImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD));
			}
		}
	}

	/**
	 * Populates the rule conditions. The rule conditions are created, populated, and binded by this method.
	 */
	private void populateRuleConditions() {
		final IEpLayoutData layoutData = this.ruleConditionsComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER);
		final Set<RuleCondition> conditions = this.rule.getConditions();

		// iterate through the rule's conditions
		for (final RuleCondition currCondition : conditions) {

			if (isExternalRule(currCondition)) {
				continue;
			}

			final IPolicyTargetLayoutComposite singleRuleWrapperComposite = this.ruleConditionsComposite.addGridLayoutComposite(
					WRAPPER_COMPOSITE_NUM_COLUMNS, false, 
					this.ruleConditionsComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, false, false), rulesEditControls);
			((GridLayout) singleRuleWrapperComposite.getSwtComposite().getLayout()).marginHeight = 1;
			
			final IPolicyTargetLayoutComposite singleRuleComposite = singleRuleWrapperComposite.addGridLayoutComposite(
					SINGLE_RULE_COMPOSITE_NUM_COLUMNS, false, 
					singleRuleWrapperComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER, true, false), rulesEditControls);
			((GridLayout) singleRuleComposite.getSwtComposite().getLayout()).marginHeight = 0;

			// remove condition icon
			final ImageHyperlink removeIcon = singleRuleComposite.addHyperLinkImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_REMOVE),
					layoutData, rulesEditControls);
			removeIcon.setToolTipText(PromotionsMessages.get().PromoRulesDefinition_Tooltip_RemoveCondition);
			removeIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(final MouseEvent mouseEvent) {
					markDirty();
					// remove the selected condition
					rule.removeCondition(currCondition);
					singleRuleWrapperComposite.getSwtComposite().dispose();
					refreshRootComposite();
					if (catalogPromotion) {
						updateConditionRequirementWidget();
					}
				}
			});

			final String displayText =
				NLS.bind(PromotionsMessages.get().getLocalizedName(currCondition.getElementType()),
				currCondition
				.getParameterKeys());
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

			// Set the modification listener *after* setting the control values
			// so that controls aren't considered to be modified when the initial value is set
			singleRuleComposite.setControlModificationListener(this.controlModificationListener);
			
			singleRuleComposite.getSwtComposite().pack(true);
		}

		if (catalogPromotion) {
			addRequireConditionValidationWidget(ruleConditionsComposite);
		}
	}

	/**
	 * Populates the rule actions. The rule actions are created, populated, and bound by this method.
	 */
	private void populateRuleActions() {
		final IEpLayoutData layoutData = this.ruleActionsComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER, false, false);
		final Set<RuleAction> actions = this.rule.getActions();

		// iterate through the rule's actions
		for (final RuleAction currAction : actions) {
			final IPolicyTargetLayoutComposite singleRuleWrapperComposite = this.ruleActionsComposite.addGridLayoutComposite(
				WRAPPER_COMPOSITE_NUM_COLUMNS, false,
					this.ruleActionsComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true), rulesEditControls);
			((GridLayout) singleRuleWrapperComposite.getSwtComposite().getLayout()).marginHeight = 1;

			final IPolicyTargetLayoutComposite singleRuleComposite = createSingleRuleComposite(singleRuleWrapperComposite);

			// remove action icon
			final ImageHyperlink removeIcon = singleRuleComposite.addHyperLinkImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_REMOVE),
					layoutData, rulesEditControls);
			removeIcon.setToolTipText(PromotionsMessages.get().PromoRulesDefinition_Tooltip_RemoveAction);
			removeIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(final MouseEvent mouseEvent) {
					// remove the selected action
					rule.removeAction(currAction);
					markDirty();
					singleRuleWrapperComposite.getSwtComposite().dispose();
					refreshRootComposite();
					updateActionRequirementWidget();
				}
			});

			final String displayText =

					NLS.bind(PromotionsMessages.get().getLocalizedName(currAction.getElementType()),
					currAction.getParameterKeys());
			int fromIndex = 0;
			int toIndex = 0;

			List<IPolicyTargetLayoutComposite> ruleComposites = new ArrayList<>();
			if (currAction.getParameterKeys() != null) {
				// iterate through the parameters of this rule action
				for (final String currRuleParameterKey : currAction.getParameterKeys()) {
					toIndex = displayText.indexOf(BEG_PARAM_DELIMITER + currRuleParameterKey + END_PARAM_DELIMITER);

					String subDisplayText = displayText.substring(fromIndex, toIndex);
					IPolicyTargetLayoutComposite ruleComposite;
					if (subDisplayText.startsWith("\n")) { //$NON-NLS-1$
						ruleComposite = createSingleRuleComposite(singleRuleWrapperComposite);
						ruleComposites.add(ruleComposite);
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

			// Set the modification listener *after* setting the control values
			// so that controls aren't considered to be modified when the initial value is set
			singleRuleComposite.setControlModificationListener(this.controlModificationListener);

			for (IPolicyTargetLayoutComposite ruleComposite : ruleComposites) {
				ruleComposite.setControlModificationListener(this.controlModificationListener);
			}
		}

		addRequireActionValidationWidget(this.ruleActionsComposite);
	}

	private IPolicyTargetLayoutComposite createSingleRuleComposite(
			final IPolicyTargetLayoutComposite singleRuleWrapperComposite) {
		IPolicyTargetLayoutComposite singleRuleComposite = singleRuleWrapperComposite.addGridLayoutComposite(
			SINGLE_RULE_COMPOSITE_NUM_COLUMNS, false,
				singleRuleWrapperComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true), rulesEditControls);
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
			public void mouseDown(final MouseEvent mouseEvent) {
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
					public void mouseDown(final MouseEvent mouseEvent) {
						// remove the selected exception
						exceptions.remove(currException);
						markDirty();
						singleExceptionComposite.getSwtComposite().dispose();
						refreshRootComposite();
					}
				});

				final String exceptionDisplayText =

						NLS.bind(PromotionsMessages.get().getLocalizedName(currException.getExceptionType()),
						currException.getParameterKeys());
				int fromIndex = 0;
				int toIndex = 0;

				if (currException.getParameterKeys() != null) {
					// iterate through the parameters of this rule exception
					for (final String currExceptionParameterKey : currException.getParameterKeys()) {
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
			public void mouseDown(final MouseEvent mouseEvent) {
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
					public void mouseDown(final MouseEvent mouseEvent) {
						// remove the selected exception
						exceptions.remove(currException);
						markDirty();
						singleExceptionComposite.getSwtComposite().dispose();
						refreshRootComposite();
					}
				});

				final String exceptionDisplayText =

						NLS.bind(PromotionsMessages.get().getLocalizedName(currException.getExceptionType()),
						currException.getParameterKeys());
				int fromIndex = 0;
				int toIndex = 0;

				if (currException.getParameterKeys() != null) {
					// iterate through the parameters of this rule exception
					for (final String currExceptionParameterKey : currException.getParameterKeys()) {
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
			}
		}
	}


	/**
	 * Refreshes the rule conditions composite by disposing, recreating, and then re-populating it.
	 */
	private void refreshRuleConditionsComposite() {
		this.mainComposite.getSwtComposite().setRedraw(false);
		// dispose the composite
		this.ruleConditionsComposite.getSwtComposite().dispose();
		// recreate the composite
		this.ruleConditionsComposite = this.conditionWrapperComposite.addGridLayoutComposite(WRAPPER_COMPOSITE_NUM_COLUMNS, false,
				this.conditionWrapperComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false,
						CONDITION_COMPOSITE_NUM_COLUMNS, WRAPPER_COMPOSITE_NUM_COLUMNS), rulesEditControls);
		// re-populate the composite
		populateRuleConditions();

		this.applyStatePolicy(statePolicy);

		refreshRootComposite();
		this.mainComposite.getSwtComposite().setRedraw(true);
	}

	/**
	 * Refreshes the rule actions composite by disposing, recreating, and then re-populating it.
	 */
	private void refreshRuleActionsComposite() {
		this.mainComposite.getSwtComposite().setRedraw(false);
		// dispose the composite
		this.ruleActionsComposite.getSwtComposite().dispose();
		// recreate the composite
		this.ruleActionsComposite = this.actionWrapperComposite.addGridLayoutComposite(WRAPPER_COMPOSITE_NUM_COLUMNS, false,
				this.actionWrapperComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, true, false, ACTION_COMPOSITE_NUM_COLUMNS,
						WRAPPER_COMPOSITE_NUM_COLUMNS), rulesEditControls);
		// re-populate the composite
		populateRuleActions();

		this.applyStatePolicy(statePolicy);

		refreshRootComposite();
		this.mainComposite.getSwtComposite().setRedraw(true);
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
		if (!this.getManagedForm().getForm().isDisposed()) {
			this.getManagedForm().getForm().layout(true, true); // TODO: find a better way to refresh
		}
	}


	/**
	 * Adds to the given parentComposite <code>IEpLayoutComposite</code> a Text widget used for condition requirement validity checks. Promotion
	 * rules must have at least 1 condition in the case of a catalog promotion. A binded, read-only Text widget is used to ensure this requirement.
	 * 
	 * @param parentComposite the <code>IEpLayoutComposite</code> to which the widget should be added to
	 */
	private void addRequireConditionValidationWidget(final IPolicyTargetLayoutComposite parentComposite) {
		final IPolicyTargetLayoutComposite errorRuleWrapperComposite = parentComposite.addGridLayoutComposite(WRAPPER_COMPOSITE_NUM_COLUMNS, false,
				parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true), rulesEditControls);
		((GridLayout) errorRuleWrapperComposite.getSwtComposite().getLayout()).marginHeight = 0;

		requireConditionValidationText = errorRuleWrapperComposite.addTextField(errorRuleWrapperComposite.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.CENTER, true, true), rulesDisplayControls);
		final GridData gridData = new GridData();
		gridData.widthHint = REQUIRE_COND_VALIDATION_TEXT_LENGTH;
		requireConditionValidationText.setLayoutData(gridData);

		updateConditionRequirementWidget();
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
		final EpValueBinding binding = bindingProvider.bind(this.getBindingContext(), requireConditionValidationText, null, null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object value) {
						return Status.OK_STATUS;
					}

					@Override
					public IStatus validateAfterGet(final Object value) {
						final IStatus validationStatus;
						if (!(value instanceof String)) {
							throw new IllegalArgumentException("This validation type only supports String"); //$NON-NLS-1$
						}
						if (value.equals(PromotionsMessages.get().PromoRulesDefintion_Error_Required_Condition)) {
							validationStatus = new Status(IStatus.ERROR, StorePlugin.PLUGIN_ID, IStatus.ERROR,
									PromotionsMessages.get().PromoRulesDefintion_Error_Required_Condition, null);
							requireConditionValidationText.setVisible(true);
							requireConditionValidationText.setForeground(CmClientResources.getColor(CmClientResources.COLOR_RED));
						} else {
							validationStatus = Status.OK_STATUS;
							requireConditionValidationText.setVisible(false);
						}

						return validationStatus;
					}
				}, true);

		requireConditionValidationText.setData(binding);
		requireConditionValidationText.addDisposeListener(this);
	}

	/**
	 * Adds to the given parentComposite <code>IEpLayoutComposite</code> a Text widget used for action requirement validity checks. Promotion rules
	 * must have at least 1 action. A binded, read-only Text widget is used to ensure this requirement.
	 * 
	 * @param parentComposite the <code>IEpLayoutComposite</code> to which the widget should be added to
	 */
	private void addRequireActionValidationWidget(final IPolicyTargetLayoutComposite parentComposite) {
		final IPolicyTargetLayoutComposite errorRuleWrapperComposite = parentComposite.addGridLayoutComposite(WRAPPER_COMPOSITE_NUM_COLUMNS, false,
				parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true), rulesEditControls);
		((GridLayout) errorRuleWrapperComposite.getSwtComposite().getLayout()).marginHeight = 0;

		this.requireActionValidationText = errorRuleWrapperComposite.addTextField(null, rulesDisplayControls);
		final GridData gridData = new GridData();
		gridData.widthHint = REQUIRE_ACTION_VALIDATION_TEXT_LENGTH;
		this.requireActionValidationText.setLayoutData(gridData);

		updateActionRequirementWidget();
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
		final EpValueBinding binding = bindingProvider.bind(this.getBindingContext(), this.requireActionValidationText, null, null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object value) {
						return Status.OK_STATUS;
					}

					@Override
					public IStatus validateAfterGet(final Object value) {
						final IStatus validationStatus;
						if (!(value instanceof String)) {
							throw new IllegalArgumentException("This validation type only supports String"); //$NON-NLS-1$
						}
						if (value.equals(PromotionsMessages.get().PromoRulesDefintion_Error_Required_Action)) {
							validationStatus = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR,
									PromotionsMessages.get().PromoRulesDefintion_Error_Required_Action, null);
							requireActionValidationText.setVisible(true);
							requireActionValidationText.setForeground(CmClientResources.getColor(CmClientResources.COLOR_RED));
							requireActionValidationText.pack();
						} else {
							validationStatus = Status.OK_STATUS;
							requireActionValidationText.setVisible(false);
						}

						return validationStatus;
					}
				}, true);

		this.requireActionValidationText.setData(binding);
		this.requireActionValidationText.addDisposeListener(this);
	}

	/**
	 * We use a binded read-only SWT Text object to keep track of condition rules; there must be at least one condition rule. Call this method to
	 * update the SWT Text object according to the number of eligibilities.
	 */
	private void updateConditionRequirementWidget() {
		if (getModel().getConditions().isEmpty()) {
			requireConditionValidationText.setText(PromotionsMessages.get().PromoRulesDefintion_Error_Required_Condition);
		} else {
			requireConditionValidationText.setText(""); //$NON-NLS-1$
		}
	}

	/**
	 * We use a binded read-only SWT Text object to keep track of action rules; there must be at least one action rule. Call this method to update
	 * the SWT Text object according to the number of action.
	 */
	private void updateActionRequirementWidget() {
		if (this.rule.getActions().isEmpty()) {
			this.requireActionValidationText.setText(PromotionsMessages.get().PromoRulesDefintion_Error_Required_Action);
		} else {
			this.requireActionValidationText.setText(""); //$NON-NLS-1$
		}
	}

	@Override
	protected void bindControls(final DataBindingContext context) {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		// condition operator
		bindingProvider.bind(context, this.anyAllConditionCombo, null, null, new ConditionOperatorMenuUpdateValueStrategy(getModel(),
				CONDITION_OPERATOR_CONFIGURATION), true);
	}

	@Override
	protected String getSectionDescription() {
		return PromotionsMessages.get().PromoRulesDefinition_Description;
	}

	@Override
	public void widgetDisposed(final DisposeEvent disposeEvent) {
		Object data = disposeEvent.widget.getData();
		if (data instanceof EpValueBinding) {
			getBindingContext().removeBinding(((EpValueBinding) data).getBinding());
		}
	}

	@Override
	public void commit(final boolean onSave) {
		// TODO Auto-generated method stub
		super.commit(false);
	}

	@Override
	public Rule getModel() {
		return rule;
	}

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		this.statePolicy = statePolicy;
		
		super.applyStatePolicy(statePolicy);
	}
	
	@Override
	public void refreshLayout() {
		super.refreshLayout();
		refreshRootComposite();
	}

}
