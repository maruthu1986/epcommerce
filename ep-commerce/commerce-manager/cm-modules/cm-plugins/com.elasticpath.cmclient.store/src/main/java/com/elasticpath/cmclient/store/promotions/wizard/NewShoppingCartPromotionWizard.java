/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions.wizard;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangingEvent;

import com.elasticpath.cmclient.conditionbuilder.wizard.pages.AbstractSellingContextConditionWizardPage;
import com.elasticpath.cmclient.conditionbuilder.wizard.pages.SellingContextConditionShopperWizardPage;
import com.elasticpath.cmclient.conditionbuilder.wizard.pages.SellingContextConditionTimeWizardPage;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.promotions.CouponCollectionModel;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.store.AbstractEPCampaignWizard;
import com.elasticpath.cmclient.store.promotions.PromotionsImageRegistry;
import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.DuplicateNameException;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.rules.RuleSetService;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.service.InvalidConditionTreeException;

/**
 * The wizard for creating promotions.
 */
@SuppressWarnings({ "PMD.NPathComplexity" })
public class NewShoppingCartPromotionWizard extends AbstractEPCampaignWizard<Rule> {

	private static final String COUPON_CONFIGURATION_PAGE_NAME = "CouponConfigurationPages"; //$NON-NLS-1$

	private static final String DETAILS_PAGE_NAME = "NewPromotionWizardStep1Page"; //$NON-NLS-1$
	
	private static final String RULES_PAGE_NAME = "NewPromotionWizardRulesPage"; //$NON-NLS-1$
	
	private Rule model;
	
	private AbstractSellingContextConditionWizardPage<SellingContext> shopperPage;
	private AbstractSellingContextConditionWizardPage<SellingContext> timePage;

	private static final String SHOPPER_CONDITION_PAGE = "WHO_CONDITION_PAGE"; //$NON-NLS-1$
	private static final String DATES_RANGE_SELECT_PAGE = "DATES_RANGE_SELECT_PAGE"; //$NON-NLS-1$

	private CouponConfigWizardPage couponConfigurationPage;
	
	private final RuleService ruleService;
	
	private final CouponConfigService couponConfigService;
	
	private final CouponUsageController couponUsageController = new CouponUsageController(); 
	
	private final ChangeSetHelper changeSetHelper;
	
	/**
	 * @return the couponConfigurationPage
	 */
	public CouponConfigWizardPage getCouponConfigurationPage() {
		return couponConfigurationPage;
	}

	@Override
	public boolean performFinish() {
	
		final StringBuilder errorMessage = new StringBuilder();
		boolean hasInvalidConditions = false;
		
		ConditionalExpression shopperCE = null;
		ConditionalExpression timeCE = null;
		try {
			timeCE = this.timePage.getModelAdapter().getModel();
		} catch (InvalidConditionTreeException icte) {
			hasInvalidConditions = true;
			errorMessage.append(icte.getLocalizedMessage());
		}
		try {
			shopperCE = this.shopperPage.getModelAdapter().getModel();
		} catch (InvalidConditionTreeException icte) {
			hasInvalidConditions = true;
			errorMessage.append(icte.getLocalizedMessage());
		}
		if (shopperCE == null && timeCE == null) {
			this.getModel().setSellingContext(null);
		} else {
			this.getModel().getSellingContext().setName(model.getName());
			this.getModel().getSellingContext().setCondition(TagDictionary.DICTIONARY_PROMOTIONS_SHOPPER_GUID, shopperCE);
			this.getModel().getSellingContext().setCondition(TagDictionary.DICTIONARY_TIME_GUID, timeCE);
		}

		if (hasInvalidConditions) {
			showErrorDialog(errorMessage.toString());
			return false;
		}
		
		if (!(isConditionalExpressionValid(shopperCE)
				&& isConditionalExpressionValid(timeCE))) {
			showErrorDialog(TargetedSellingMessages.get().TotalLengthOfConditionsReached);
			return false;
		}
		
		// TODO: Refactor this code out into the respective pages
		// ensure rule name uniqueness
		if (ruleNameExists(model.getName())) {
			NewPromotionWizardDetailsPage newPromoDetailsPage = ((NewPromotionWizardDetailsPage) this.getPage(DETAILS_PAGE_NAME));
			newPromoDetailsPage.setErrorMessage(PromotionsMessages.get().CreatePromotionWizardDetailsPage_Name_Uniqueness_Error);
			this.getContainer().showPage(newPromoDetailsPage);
			return false;
		}
		
		// validates the rule on the client side.
		try {
			getModel().validate();
		} catch (Exception exception) {
			NewPromotionWizardRulesPage newPromoRulesPage = ((NewPromotionWizardRulesPage) this.getPage(RULES_PAGE_NAME));
			newPromoRulesPage.setErrorMessage(PromotionsMessages.get().CreatePromotionWizardRulesPage_Validation_Error
					+ exception.getLocalizedMessage());
			this.getContainer().showPage(newPromoRulesPage);
			return false;
		}
		
		persistModel();

		return true;
	}

	private void persistModel() {
		model = ruleService.add(model);
		changeSetHelper.addObjectToChangeSet(model, ChangeSetMemberAction.ADD);

		CouponConfig couponConfig = couponConfigurationPage.getModel().getCouponConfig();
		if (couponConfig.getUsageType() != null) {
			RuleCondition limitedUseCouponCondition = ServiceLocator.getService(
					RuleElementType.LIMITED_USE_COUPON_CODE_CONDITION.getPropertyKey());
			model.addCondition(limitedUseCouponCondition);
			model = ruleService.update(model);
			
			couponConfig.setRuleCode(model.getCode());
			couponConfig = couponConfigService.add(couponConfig);
		}
		
		CouponCollectionModel couponUsageCollectionModel = couponConfigurationPage.getModel().getCouponUsageCollectionModel();
		
		couponUsageCollectionModel.setCouponConfig(couponConfig);
		
		couponUsageController.updateDatabase(couponUsageCollectionModel);		
	}
	
	private boolean ruleNameExists(final String ruleName) {
		final RuleService ruleService = ServiceLocator.getService(ContextIdNames.RULE_SERVICE);
		try {
			if (ruleService.findByName(ruleName) != null) {
				return true;
			}
		} catch (DuplicateNameException e) {
			return true;
		}
		return false;
	}
	
	/**
	 * Constructor.
	 */
	public NewShoppingCartPromotionWizard() {
		super(PromotionsMessages.get().CreatePromotionsWizard_Title, null, PromotionsImageRegistry
				.getImage(PromotionsImageRegistry.PROMOTION_SHOPPING_CREATE));

		SellingContext sellingContext = ServiceLocator.getService(ContextIdNames.SELLING_CONTEXT);

		final RuleSetService ruleSetService = ServiceLocator.getService(ContextIdNames.RULE_SET_SERVICE);

		model = ServiceLocator.getService(ContextIdNames.PROMOTION_RULE);
		model.setRuleSet(ruleSetService.findByScenarioId(RuleScenarios.CART_SCENARIO));
		model.setEnabled(true);
		model.setSellingContext(sellingContext);

		ruleService = ServiceLocator.getService(ContextIdNames.RULE_SERVICE);
		couponConfigService = ServiceLocator.getService(ContextIdNames.COUPON_CONFIG_SERVICE);
		changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);
	}
	
	@Override
	public Rule getModel() {
		return model;
	}
	
	@Override
	public void addPages() {
		PolicyActionContainer policyActionContainer = addPolicyActionContainer(getTargetIdentifier());
		List<ConditionalExpression> shopperNameConditions = null; // tagConditionService.getNamedConditions(TagDictionary.DICTIONARY_SHOPPER_GUID);
		List<ConditionalExpression> timeNameConditions = null; //tagConditionService.getNamedConditions(TagDictionary.DICTIONARY_TIME_GUID);

		

		addPage(new NewPromotionWizardDetailsPage(DETAILS_PAGE_NAME, PromotionsMessages.get().CreatePromotionWizardDetailsPage_Title, false),
				policyActionContainer);

		shopperPage = new SellingContextConditionShopperWizardPage<>(
				SHOPPER_CONDITION_PAGE,
				PromotionsMessages.get().CreatePromotionWizard_Page_Step2_Title,
				PromotionsMessages.get().CreatePromotionWizard_Page_Step2_Description,
				shopperNameConditions,
				getModel().getSellingContext(),
				TagDictionary.DICTIONARY_PROMOTIONS_SHOPPER_GUID);
		addPage(shopperPage, policyActionContainer);

		timePage = new SellingContextConditionTimeWizardPage<>(DATES_RANGE_SELECT_PAGE,
				PromotionsMessages.get().CreatePromotionWizard_Page_Step3_Title, PromotionsMessages.
				get().CreatePromotionWizard_Page_Step3_Description, timeNameConditions, getModel().getSellingContext());
		this.addPage(timePage, policyActionContainer);

		addPage(new NewPromotionWizardRulesPage(RULES_PAGE_NAME, PromotionsMessages.get().CreatePromotionWizardRulesPage_Title, false),
				policyActionContainer);

		couponConfigurationPage = new CouponConfigWizardPage(COUPON_CONFIGURATION_PAGE_NAME,
				PromotionsMessages.get().CreatePromotionWizardCouponsPage_Title);
		addPage(couponConfigurationPage, policyActionContainer);
	}

	@Override
	public String getNameFromModel() {
		return model.getName();
	}

	@Override
	public String getTargetIdentifier() {
		return "newShoppingCartPromotionWizard"; //$NON-NLS-1$
	}
	
	private void showErrorDialog(final String message) {
		MessageDialog.openError(this.getShell(), PromotionsMessages.get().CreatePromotionsWizard_Error_Dialog_Title, message);
	}

	private boolean isConditionalExpressionValid(final ConditionalExpression conditionalExpression) {
		if (conditionalExpression != null) {
			return isConditionLengthValid(conditionalExpression.getConditionString());
		}
		return true;
	}
	
	private boolean isConditionLengthValid(final String conditionString) {
		return (conditionString == null || conditionString.length() <= ConditionalExpression.CONDITION_STRING_MAX_LENGTH);
	}
	
	@Override
	public void handlePageChanging(final PageChangingEvent event) {
		reApplyStatePolicy();
		super.handlePageChanging(event);
	}

}
