/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.editors;

import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.conversion.EpIntToStringConverter;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.StoreUtils;
import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.rules.RuleParameter;

/**
 * This class implements the section of the Promotion editor that displays store information about a promotion.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.AvoidDeeplyNestedIfStmts" })
public class PromotionRulesSection extends AbstractPolicyAwareEditorPageSectionPart {

	private IEpDateTimePicker activeFromDateTimePicker;

	private IEpDateTimePicker activeToDateTimePicker;

	private Button visibleInStore;

	private Text ruleState;

	private final boolean catalogPromotion;

	private IPolicyTargetLayoutComposite mainPane;

	private Button limitedUsagePromotion;

	private Label allowedLimitLabel;

	private Spinner allowedLimitSpinner;

	private Label currentUsedLabel;

	private Text currentUsedText;

	private final Rule rule;

	private String limitedUsagePromotionID;

	/**
	 * Default constructor.
	 * 
	 * @param formPage the form page
	 * @param editor the editor
	 * @param catalogPromotion whether the promotion is a catalog promotion
	 */
	public PromotionRulesSection(final FormPage formPage, final AbstractCmClientFormEditor editor, final boolean catalogPromotion) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.COMPACT);
		this.catalogPromotion = catalogPromotion;
		this.rule = (Rule) editor.getModel();
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		if (catalogPromotion) {
			bindingProvider.bind(bindingContext, activeFromDateTimePicker.getSwtText(), EpValidatorFactory.DATE_TIME_REQUIRED, null,
					new ObservableUpdateValueStrategy() {
						@Override
						protected IStatus doSet(final IObservableValue observableValue, final Object value) {
							getModel().setStartDate(activeFromDateTimePicker.getDate());
							ruleState.setText(StoreUtils.getPromotionState(getModel()));
							return Status.OK_STATUS;
						}
					}, true);

			// active from date
			bindingProvider.bind(bindingContext, activeToDateTimePicker.getSwtText(), new CompoundValidator(new IValidator[] {
					EpValidatorFactory.DATE_TIME, value -> {
				if (activeToDateTimePicker.getDate() != null
						&& activeToDateTimePicker.getDate().before(activeFromDateTimePicker.getDate())) {
					return new Status(IStatus.ERROR, StorePlugin.PLUGIN_ID, IStatus.ERROR,
							PromotionsMessages.get().CreatePromotionWizardDetailsPage_Date_Error, null);
				}
				return Status.OK_STATUS;
			}}), null, new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object value) {
					getModel().setEndDate(activeToDateTimePicker.getDate());
					ruleState.setText(StoreUtils.getPromotionState(getModel()));
					return Status.OK_STATUS;
				}
			}, true);

		} else {
			bindingProvider.bind(bindingContext, visibleInStore, null, null, new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object value) {
					getModel().setEnabled(visibleInStore.getSelection());
					ruleState.setText(StoreUtils.getPromotionState(getModel()));
					return Status.OK_STATUS;
				}
			}, true);
			
			SelectionListener listener = getLimitedUsePromotionSelectionListener();
		
			limitedUsagePromotion.addSelectionListener(listener);
		}

		mainPane.setControlModificationListener(getEditor());
	}

	private SelectionListener getLimitedUsePromotionSelectionListener() {
		return new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
				// do nothing
			}

			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				Object object = selectionEvent.getSource();

				if (object instanceof Button) {
					Button limitedUsagePromotion = (Button) object;
					if (limitedUsagePromotion.getSelection()) {

						allowedLimitSpinner.setEnabled(true);
						allowedLimitSpinner.setVisible(true);
						allowedLimitLabel.setVisible(true);
						currentUsedLabel.setVisible(true);
						currentUsedText.setVisible(true);

						bindLimitedPromotionAllowedLimitInputField();

					} else {

						allowedLimitSpinner.setEnabled(false);
						allowedLimitSpinner.setVisible(false);
						allowedLimitLabel.setVisible(false);
						currentUsedLabel.setVisible(false);
						currentUsedText.setVisible(false);

						Set<RuleCondition> set = getModel().getConditions();

						for (RuleCondition ruleCondition : set) {

							if (RuleElementType.LIMITED_USAGE_PROMOTION_CONDITION.equals(ruleCondition.getElementType())) {
								getModel().removeCondition(ruleCondition);
								break;
							}
						}
					}
				}
			}
		};	
	}
	
	
	@Override
	protected void createControls(final Composite parentComposite, final FormToolkit toolkit) {
		final PolicyActionContainer overviewRuleDisplayControls = addPolicyActionContainer("overviewRuleDisplayControls"); //$NON-NLS-1$
		final PolicyActionContainer overviewRuleControls = addPolicyActionContainer("overviewRuleControls"); //$NON-NLS-1$		
		
		mainPane = PolicyTargetCompositeFactory.wrapLayoutComposite(
				CompositeFactory.createTableWrapLayoutComposite(parentComposite, 2, false));
		final TableWrapData data = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
		data.grabHorizontal = false;
		mainPane.setLayoutData(data);

		final IEpLayoutData labelData = mainPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, false);

		if (catalogPromotion) {
			mainPane.addLabelBoldRequired(PromotionsMessages.get().PromoDetailsOverview_Label_ActiveFrom, labelData, overviewRuleControls);
			activeFromDateTimePicker = mainPane.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, fieldData, overviewRuleControls);

			mainPane.addLabelBold(PromotionsMessages.get().PromoDetailsOverview_Label_ActiveTo, labelData, overviewRuleControls);
			activeToDateTimePicker = mainPane.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, fieldData, overviewRuleControls);
		} else {
			mainPane.addLabelBold(PromotionsMessages.get().PromoDetailsOverview_StoreVisible, labelData, overviewRuleControls);
			visibleInStore = mainPane.addCheckBoxButton("", fieldData, overviewRuleControls); //$NON-NLS-1$
		}

		mainPane.addLabelBold(PromotionsMessages.get().PromoStoreRules_State, labelData, overviewRuleDisplayControls);
		ruleState = mainPane.addTextField(fieldData, overviewRuleDisplayControls);

		if (!catalogPromotion) {

			String allowedLimitValue = this.isLimitedUsagePromotion();

			mainPane.addLabelBold(PromotionsMessages.get().PromoDetailsOverview_LimitedUsagePromotion, labelData, overviewRuleControls);
			limitedUsagePromotion = mainPane.addCheckBoxButton("", fieldData, overviewRuleControls); //$NON-NLS-1$

			allowedLimitLabel = mainPane.addLabelBold(PromotionsMessages.get().PromoDetailsOverview_AllowedLimit, labelData, overviewRuleControls);

			allowedLimitSpinner = mainPane.addSpinnerField(fieldData, overviewRuleControls);
			allowedLimitSpinner.setMinimum(1);
			allowedLimitSpinner.setMaximum(Integer.MAX_VALUE);
			
			currentUsedLabel = mainPane.addLabelBold(
					PromotionsMessages.get().PromoDetailsOverview_AllowedLimitCurentlyUsed, labelData, overviewRuleDisplayControls);
			currentUsedText = mainPane.addTextField(fieldData, overviewRuleDisplayControls);

			if (allowedLimitValue == null) {
				allowedLimitSpinner.setEnabled(false);
				allowedLimitSpinner.setVisible(false);
				allowedLimitLabel.setVisible(false);
				currentUsedLabel.setVisible(false);
				currentUsedText.setVisible(false);
				currentUsedLabel.setVisible(false);
			} else {
				limitedUsagePromotion.setSelection(true);
				currentUsedLabel.setVisible(true);
				currentUsedText.setVisible(true);
				allowedLimitSpinner.setSelection(Integer.parseInt(allowedLimitValue.replaceAll("\\D", "")));  //$NON-NLS-1$//$NON-NLS-2$
				bindLimitedPromotionAllowedLimitInputField();
			}
		}
		addCompositesToRefresh(mainPane.getSwtComposite().getParent(), mainPane.getSwtComposite());
	}

	private void bindLimitedPromotionAllowedLimitInputField() {

		RuleCondition lupCondition = getLUPCondition();

		if (lupCondition == null) {
			lupCondition = ServiceLocator.getService(
					RuleElementType.LIMITED_USAGE_PROMOTION_CONDITION.getPropertyKey());
		}

		// add the new condition to the rule
		getModel().addCondition(lupCondition);

		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		bindingProvider.bind(this.getBindingContext(), allowedLimitSpinner, getRuleParameterByKey(lupCondition, RuleParameter.ALLOWED_LIMIT),
				"value", null, new EpIntToStringConverter(), false); //$NON-NLS-1$

		RuleParameter lupParamater = getRuleParameterByKey(lupCondition, RuleParameter.LIMITED_USAGE_PROMOTION_ID);

		if (lupParamater == null) {
			lupParamater = ServiceLocator.getService(ContextIdNames.RULE_PARAMETER);
			lupParamater.setKey(RuleParameter.LIMITED_USAGE_PROMOTION_ID);
			lupCondition.addParameter(lupParamater);
		}

		if (lupParamater.getValue() == null) {
			limitedUsagePromotionID = getModel().getCode();
			

			lupParamater.setValue(this.limitedUsagePromotionID);
		}

		this.currentUsedText.setText(Long.toString((getModel().getCurrentLupNumber())));
	}

	@SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
	private String isLimitedUsagePromotion() {

		final RuleCondition currCondition = this.getLUPCondition();

		if (currCondition != null) {
			for (String currRuleParameterKey : currCondition.getParameterKeys()) {
				RuleParameter parameter = getRuleParameterByKey(currCondition, currRuleParameterKey);

				return parameter.getValue();
			}
		}
		return null;
	}

	private RuleCondition getLUPCondition() {
		final Set<RuleCondition> conditions = this.rule.getConditions();

		// iterate through the rule's conditions
		for (final RuleCondition currCondition : conditions) {

			if (RuleElementType.LIMITED_USAGE_PROMOTION_CONDITION.equals(currCondition.getElementType())) {

				return currCondition;
			}
		}

		return null;
	}

	@Override
	protected void populateControls() {

		if (catalogPromotion) {
			activeFromDateTimePicker.setDate(getModel().getStartDate());
			activeToDateTimePicker.setDate(getModel().getEndDate());
		} else {
			visibleInStore.setSelection(getModel().isEnabled());
		}

		ruleState.setText(StoreUtils.getPromotionState(getModel()));
	}

	@Override
	public Rule getModel() {
		return (Rule) getEditor().getModel();
	}

	@Override
	protected String getSectionTitle() {
		return PromotionsMessages.get().PromoDetailsCatalogRules_Title;
	}
}
