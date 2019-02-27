/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions.editors;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.editor.IFormPage;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.editors.CancelSaveException;
import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.cmclient.core.promotions.CouponCollectionModel;
import com.elasticpath.cmclient.core.security.Authorizable;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareFormEditor;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.promotions.CouponConfigPageModel;
import com.elasticpath.cmclient.store.promotions.CouponPageModel;
import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.cmclient.store.promotions.PromotionsPermissions;
import com.elasticpath.cmclient.store.promotions.event.PromotionsChangeEvent;
import com.elasticpath.cmclient.store.promotions.event.PromotionsEventService;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleException;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.DuplicateCouponException;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.sellingcontext.SellingContextService;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.TagDictionary;

/**
 * Implements a multi-page editor for displaying and editing promotions.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class ShoppingCartPromotionsEditor extends AbstractPolicyAwareFormEditor implements Authorizable {

	/**
	 * ID of the editor. It is the same as the class name.
	 */
	public static final String ID_EDITOR = "com.elasticpath.cmclient.store.promotions.editors.PromotionsEditor.ShoppingCart"; //$NON-NLS-1$

	private static final int TOTAL_WORK_UNITS = 3;

	private static final int RULES_PAGE_INDEX = 1;

	private Rule rule;

	private final RuleService ruleService =
		ServiceLocator.getService(ContextIdNames.RULE_SERVICE);
	private final SellingContextService sellingContextService =
		ServiceLocator.getService(ContextIdNames.SELLING_CONTEXT_SERVICE);

	private List<IFormPage> pagesList = new LinkedList<>();

	private SellingContext sellingContextForDelete;

	private CouponConfigPageModel couponModel;

	private final CouponConfigService couponConfigService = ServiceLocator.getService(ContextIdNames.COUPON_CONFIG_SERVICE);

	private CouponEditorPage couponEditorPage;

	private final CouponController couponController = new CouponController();

	private final CouponUsageController couponUsageController = new CouponUsageController();

	@Override
	public void initEditor(final IEditorSite site, final IEditorInput input) throws PartInitException {
		// empty
	}

	private Rule getRule(final String guid) {
		Rule rule = ruleService.findByRuleCode(guid);
		SellingContext sellingContext = rule.getSellingContext();
		if (sellingContext == null) {
			sellingContext = ServiceLocator.getService(ContextIdNames.SELLING_CONTEXT);
			sellingContext.setName(rule.getName());
			rule.setSellingContext(sellingContext);
		}
		return rule;
	}

	private CouponConfig retriveCouponConfig(final String ruleCode) {
		CouponConfig couponConfig = couponConfigService.findByRuleCode(ruleCode);
		if (couponConfig == null) {
			couponConfig = ServiceLocator.getService(ContextIdNames.COUPON_CONFIG);
		}

		return couponConfig;
	}

	@Override
	public Rule getModel() {
		return rule;
	}

	@Override
	protected void addPages() {
		final PolicyActionContainer pageContainer = addPolicyActionContainer("shoppingCartPromotionEditor"); //$NON-NLS-1$

		try {
			FormPage page = new PromotionDetailsPage(this, false);
			addPage(page, pageContainer);
			page = new PromotionShopperPage(this, false);
			addPage(page, pageContainer);
			page = new PromotionTimePage(this, false);
			addPage(page, pageContainer);
			page = new PromotionRulesPage(this, false);
			addPage(page, pageContainer);

			// only shows the coupon pages when there are coupon usage type.
			CouponUsageType couponUsageType = couponModel.getCouponConfig().getUsageType();
			if (couponUsageType != null) {
				page = new CouponConfigEditorPage(this);
				addPage(page, pageContainer);

				couponEditorPage = new CouponEditorPage(this);
				addPage(couponEditorPage, pageContainer);
			}
			getCustomData().put("catalogPromotion", false);
			getCustomData().put("isLocaleDependent", false);
			addExtensionPages(getClass().getSimpleName(), StorePlugin.PLUGIN_ID);
		} catch (final PartInitException e) {
			throw new EpUiException(e);
		}
	}

	@Override
	public int addPage(final IFormPage page, final PolicyActionContainer container) throws PartInitException {
		this.pagesList.add(page);
		return super.addPage(page, container);
	}

	@Override
	protected void saveModel(final IProgressMonitor monitor) {
		monitor.beginTask(PromotionsMessages.get().PromotionEditor_Save_StatusBarMsg, TOTAL_WORK_UNITS);
		try {
			if (!checkProductCategorySkuSelected()) {
				monitor.setCanceled(true);
				return;
			}
			monitor.worked(1);

			if (!checkSellingContext()) {
				throw new CancelSaveException("Failed to check Shopper."); //$NON-NLS-1$
			}
			monitor.worked(1);

			// validates the rule on the client side.
			try {
				rule.validate();
			} catch (Exception exception) {
				throw new CancelSaveException(PromotionsMessages.get().CreatePromotionWizardRulesPage_Validation_Error
						+ exception.getLocalizedMessage(), exception);
			}

			monitor.worked(1);

			try {
				validateCoupons();
			} catch (DuplicateCouponException exception) {
				throw new CancelSaveException(PromotionsMessages.get().CreatePromotionWizardRulesPage_Coupon_Validation_Error
						+ exception.getCouponCode(), exception);
			}

			// update rule
			Rule updatedRule = ruleService.update(rule);
			monitor.worked(1);

			// update selling context
			if (this.sellingContextForDelete != null) {
				this.sellingContextService.remove(sellingContextForDelete);
			}
			monitor.worked(1);

			// update coupon config
			updateCouponConfig();
			monitor.worked(1);

			// update coupons
			updateCoupons();
			monitor.worked(1);

			PromotionsEventService.getInstance().firePromotionsChangeEvent(new PromotionsChangeEvent(this, updatedRule));
			monitor.worked(1);

			this.reloadModel();
			this.reinitStatePolicy();
			this.refreshEditorPages();
		} finally {
			monitor.done();
			this.sellingContextForDelete = null;
		}
	}

	private void updateCoupons() {
		CouponUsageType usageType = couponModel.getCouponConfig().getUsageType();
		if (couponEditorPage != null && couponEditorPage.getModel() != null) {
			CouponPageModel couponPageModel = couponEditorPage.getModel();
			if (isPrivateCoupon(usageType)) {
				couponUsageController.add(couponPageModel.getAddedCouponUsageItems(), rule.getCode());
				couponUsageController.update(couponPageModel.getUpdatedCouponUsageItems());
			} else {
				couponController.add(couponPageModel.getAddedCouponItems(), rule.getCode());
				couponController.update(couponPageModel.getUpdatedCouponItems());
			}
		}
	}

	private void validateCoupons() {
		CouponUsageType usageType = couponModel.getCouponConfig().getUsageType();
		if (couponEditorPage != null && couponEditorPage.getModel() != null) {
			CouponPageModel couponPageModel = couponEditorPage.getModel();
			if (isPrivateCoupon(usageType)) {
				couponUsageController.validate(couponPageModel.getAddedCouponUsageItems(), rule.getCode());
			} else {
				couponController.validate(couponPageModel.getAddedCouponItems(), rule.getCode());
			}
		}
	}

	private boolean isPrivateCoupon(final CouponUsageType usageType) {
		return usageType.equals(CouponUsageType.LIMIT_PER_SPECIFIED_USER);
	}

	private void updateCouponConfig() {
		CouponConfig couponConfig = couponModel.getCouponConfig();
		if (couponConfig.getUsageType() != null) {
			couponConfigService.update(couponConfig);
		}
	}

	/**
	 * Returns true if all rule elements with Product/Category/SKU values have valid values (i.e. valid means the user has selected a specific
	 * Product/Category/SKU; false otherwise.
	 *
	 * @return true if all rule elements with Product/Category/SKU values have valid values (i.e. valid means the user has selected a specific
	 *         Product/Category/SKU; false otherwise.
	 */
	private boolean checkProductCategorySkuSelected() {
		// Iterate through the Rule's rule-elements
		for (RuleElement currRuleElement : getModel().getRuleElements()) {
			// Check the parameters of the rule-elements
			for (RuleParameter currElementParam : currRuleElement.getParameters()) {
				if (!isRuleParameterComplete(currElementParam)) {
					this.setActivePage(RULES_PAGE_INDEX);
					MessageDialog.openWarning(this.getSite().getShell(), PromotionsMessages.get().PromoRulesDefintion_Error_Title,
							PromotionsMessages.get().PromoRulesDefintion_Error_Select_Links);
					return false;
				}
			}

			// Check the parameters of rule-exceptions
			for (RuleException currRuleException : currRuleElement.getExceptions()) {
				for (RuleParameter currExceptionParam : currRuleException.getParameters()) {
					if (!isRuleParameterComplete(currExceptionParam)) {
						this.setActivePage(RULES_PAGE_INDEX);
						MessageDialog.openWarning(this.getSite().getShell(), PromotionsMessages.get().PromoRulesDefintion_Error_Title,
								PromotionsMessages.get().PromoRulesDefintion_Error_Select_Links);
						return false;
					}
				}
			}
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

		return !productIsNull && !categoryIsNull && !skuIsNull;
	}



	@Override
	public void reloadModel() {
		this.setInput(getEditorInput());
	}

	/**
	 * Checks whether the user is authorized to edit this promotion.
	 *
	 * @return boolean
	 */
	@Override
	public boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(PromotionsPermissions.PROMOTION_MANAGE)
				&& AuthorizationService.getInstance().isAuthorizedForStore(rule.getStore());
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return Collections.emptyList();
	}

	@Override
	public Locale getDefaultLocale() {
		return null;
	}

	@Override
	protected String getSaveOnCloseMessage() {
		return
			NLS.bind(PromotionsMessages.get().PromotionEditor_OnSavePrompt,
			getEditorName());
	}
		
	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		statePolicy.init(getModel());
		super.applyStatePolicy(statePolicy);
	}

	@Override
	public String getTargetIdentifier() {
		return "shoppingCartPromotionEditor"; //$NON-NLS-1$
	}
	
	@Override
	protected String getEditorName() {
		String name = ""; //$NON-NLS-1$
		if (rule != null) {
			name = rule.getName();
		}
		return name;
	}

	@Override
	protected String getEditorToolTip() {
		return getEditorName();
	}

	@Override
	public void dispose() {
		this.pagesList.clear();
		this.pagesList = null;
		super.dispose();
	}

	private boolean checkSellingContext() {
		Rule model = this.getModel();
		SellingContext sellingContext = model.getSellingContext();
		if (sellingContext == null) {
			return true;
		}
		ConditionalExpression timeCE = sellingContext.getCondition(TagDictionary.DICTIONARY_TIME_GUID);
		ConditionalExpression shopperCE = sellingContext.getCondition(TagDictionary.DICTIONARY_PROMOTIONS_SHOPPER_GUID);

		if (shopperCE == null && timeCE == null) {
			model.setSellingContext(null);
			this.sellingContextForDelete = sellingContext;
		}

		if (!(isConditionalExpressionValid(shopperCE)
				&& isConditionalExpressionValid(timeCE))) {
			showErrorDialog(TargetedSellingMessages.get().TotalLengthOfConditionsReached);
			return false;
		}
		return true;
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

	private void showErrorDialog(final String message) {
		MessageBox messageBox = new MessageBox(this.getSite().getShell(), SWT.OK);
		messageBox.setMessage(message);
		messageBox.open();
	}

	@Override
	protected void setInput(final IEditorInput input) {
		super.setInput(input);
		GuidEditorInput guidEditorInput = (GuidEditorInput) input;
		rule = getRule(guidEditorInput.getGuid());
		setPartName(rule.getName());

		couponModel = new CouponConfigPageModel(retriveCouponConfig(rule.getCode()), new CouponCollectionModel());
	}
	
	/**
	 * Gets the coupon config model.
	 * 
	 * @return the coupon config model.
	 */
	public CouponConfigPageModel getCouponConfigPageModel() {
		return couponModel;
	}

	@Override
	public void setFocus() {
		// overriden to avoid triggering setSelection on radio buttons
	}
}
