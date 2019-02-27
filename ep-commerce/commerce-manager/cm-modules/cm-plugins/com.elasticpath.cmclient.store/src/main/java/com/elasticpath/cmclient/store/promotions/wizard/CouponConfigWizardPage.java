/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.wizard;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.promotions.CouponCollectionModel;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareWizardPage;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.cmclient.store.promotions.CouponConfigPageControlsController;
import com.elasticpath.cmclient.store.promotions.CouponConfigPageModel;
import com.elasticpath.cmclient.store.promotions.CouponConfigPageModelUpdateStrategy;
import com.elasticpath.cmclient.store.promotions.MultiUsePerOrderCouponConfigPageControlsController;
import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.DiscountType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;

/**
 * The coupon configuration page on the promotion creation wizard.
 */
@SuppressWarnings({"PMD.TooManyFields", "PMD.ConstructorCallsOverridableMethod"})
public class CouponConfigWizardPage extends AbstractPolicyAwareWizardPage<CouponConfigPageModel> implements SelectionListener {
	/**
	 * .
	 */
	public static final String COUPON_WIZARD_CONFIG_PAGE = "couponConfigPage"; //$NON-NLS-1$

	/**
	 * Strategy to update coupon usage type.
	 */
	private class CouponUsageTypeUpdateValueStrategy implements CouponConfigPageModelUpdateStrategy {
		@Override
		public void updateModel(final Control control) {
			setDefaultCouponConfig();

			CouponUsageType type = null;

			if (control.equals(publicCouponsRadioButton)) {
				type = CouponUsageType.LIMIT_PER_COUPON;
			} else if (control.equals(privateCouponsRadioButton)) {
				type = CouponUsageType.LIMIT_PER_SPECIFIED_USER;
			}

			couponConfig.setUsageType(type);
		}
	}

	private void setDefaultCouponConfig() {
		couponConfig.setLimitedDuration(false);
		couponConfig.setDurationDays(DEFAULT_DURATION_DAYS);
		couponConfig.setUsageType(null);
		couponConfig.setUnlimited();
		couponConfig.setMultiUsePerOrder(false);
	}

	private static final int COLUMN_FIVE = 5;

	private static final int COLUMN_FOUR = 4;

	private static final int COLUMN_THREE = 3;

	private static final int DEFAULT_DURATION_DAYS = 60;

	private final CouponConfig couponConfig;

	private final MultiUsePerOrderCouponConfigPageControlsController couponUsageTypeController;

	private Button noCouponsRadioButton;

	private Button privateCouponsRadioButton;

	private Button publicCouponsRadioButton;

	private final CouponConfigWizardPageWidgetFactory widgetFactory;

	private MultiUsePerOrderCouponConfigPageControlsController publicCouponsSettingController;

	private MultiUsePerOrderCouponConfigPageControlsController privateCouponsSettingController;

	private final PolicyActionContainer container;

	private Button manageCouponsButton;

	private CouponConfigPageControlsController expireDaysSettingController;

	private MultiUsePerOrderCouponConfigPageControlsController limitMultiUsePerOrderSettingController;

	/**
	 * Constructor.
	 *
	 * @param pageName the page name.
	 * @param title    the title.
	 */
	protected CouponConfigWizardPage(final String pageName, final String title) {
		super(1, false, pageName, title, PromotionsMessages.get().CreatePromotionWizardCouponsPage_Description, new DataBindingContext());

		this.couponConfig = ServiceLocator.getService(ContextIdNames.COUPON_CONFIG);
		CouponConfigPageModel emptyModel = new CouponConfigPageModel(couponConfig, new CouponCollectionModel());

		this.couponUsageTypeController = new MultiUsePerOrderCouponConfigPageControlsController(new CouponUsageTypeUpdateValueStrategy());

		this.widgetFactory = new CouponConfigWizardPageWidgetFactory(this, emptyModel);

		this.container = addPolicyActionContainer(COUPON_WIZARD_CONFIG_PAGE);

		setDefaultCouponConfig();

	}

	@Override
	public boolean beforePrev(final PageChangingEvent event) {
		CouponConfigPageModel emptyModel = new CouponConfigPageModel(couponConfig, new CouponCollectionModel());

		this.widgetFactory.setCouponConfigPageModel(emptyModel);

		return true;
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite parentComposite) {
		final TableWrapData data = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
		data.grabHorizontal = true;
		parentComposite.setLayoutData(data);

		IPolicyTargetLayoutComposite couponConfigGroup = PolicyTargetCompositeFactory.wrapLayoutComposite(parentComposite).addGridLayoutComposite(1,
				false, parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true), container);

		createNoCouponsGroup(couponConfigGroup);

		final IEpLayoutData labelData = couponConfigGroup.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, false, true, 1, COLUMN_FOUR);
		couponConfigGroup.addEmptyComponent(labelData, container);

		createPublicCouponsGroup(couponConfigGroup);
		createPrivateCouponsGroup(couponConfigGroup);

		createManageCouponsButton(couponConfigGroup);
		createMultiUseCouponLimitCheckbox(couponConfigGroup);

		widgetFactory.linkLimitMultiUsePerOrderToPrivatePublicCouponSettingControls();

		/* MUST be called */
		this.setControl(parentComposite.getSwtComposite());
	}

	private void createManageCouponsButton(final IPolicyTargetLayoutComposite couponConfigGroup) {
		final IEpLayoutData labelData = couponConfigGroup.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER);
		IPolicyTargetLayoutComposite couponButtonComposite = couponConfigGroup.addGridLayoutComposite(COLUMN_FOUR, false, labelData, container);
		IEpLayoutData emptyData = couponButtonComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false, COLUMN_THREE, 1);
		couponButtonComposite.addEmptyComponent(emptyData, container);

		manageCouponsButton = widgetFactory.createManageCouponsButton(couponButtonComposite, container);
	}

	private void createMultiUseCouponLimitCheckbox(final IPolicyTargetLayoutComposite couponConfigGroup) {
		limitMultiUsePerOrderSettingController = widgetFactory.createLimitMultiUsePerOrderSetting(couponConfigGroup, container);
	}

	private void createNoCouponsGroup(final IPolicyTargetLayoutComposite couponConfigGroup) {
		final IEpLayoutData labelData = couponConfigGroup.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER);
		noCouponsRadioButton = couponConfigGroup.addRadioButton(PromotionsMessages.get().CouponConfigPageNotActivateByCoupons, labelData, container);
		noCouponsRadioButton.addSelectionListener(this);
	}

	private void createPrivateCouponsGroup(final IPolicyTargetLayoutComposite couponConfigGroup) {
		final IEpLayoutData labelData = couponConfigGroup.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER);
		privateCouponsRadioButton = couponConfigGroup.addRadioButton(PromotionsMessages.get().CouponConfigPageActivatedByPrivateCoupons, labelData,
				container);
		privateCouponsRadioButton.addSelectionListener(this);

		IEpLayoutData privateCouponsSettingCompositeData = couponConfigGroup.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, false, true);
		IPolicyTargetLayoutComposite privateCouponsSettingComposite = couponConfigGroup.addGridLayoutComposite(COLUMN_FIVE, false,
				privateCouponsSettingCompositeData, container);

		privateCouponsSettingController = widgetFactory.createPrivateCouponsSetting(privateCouponsSettingComposite, container);

		// expire days
		IEpLayoutData emptyData = privateCouponsSettingComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false, COLUMN_FOUR,
				1);
		privateCouponsSettingComposite.addEmptyComponent(emptyData, container);
		expireDaysSettingController = widgetFactory.createExpireDaysSetting(privateCouponsSettingComposite, container);
	}

	private void createPublicCouponsGroup(final IPolicyTargetLayoutComposite couponConfigGroup) {
		final IEpLayoutData labelData = couponConfigGroup.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER);
		publicCouponsRadioButton = couponConfigGroup.addRadioButton(PromotionsMessages.get().CouponConfigPageActivatedByPublicCoupons, labelData,
				container);
		publicCouponsRadioButton.addSelectionListener(this);

		IEpLayoutData publicCouponsSettingCompositeData = couponConfigGroup.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, false, true);
		IPolicyTargetLayoutComposite publicCouponsSettingComposite = couponConfigGroup.addGridLayoutComposite(COLUMN_FIVE, false,
				publicCouponsSettingCompositeData, container);

		publicCouponsSettingController = widgetFactory.createPublicCouponsSetting(publicCouponsSettingComposite, container);
	}

	@Override
	public CouponConfigPageModel getModel() {
		return new CouponConfigPageModel(couponConfig, widgetFactory.getCouponUsageCollectionModel());
	}

	@Override
	public void setVisible(final boolean show) {
		if (show) {
			updateRuleModel();
			// ensure changes refresh.
			Button selection = couponUsageTypeController.getSelection();

			// setting the correct states of the radio buttons and check button. The sequence matters since we need
			// to enable/disable the parent radio buttons (coupon usage type) and then set the state of its children.
			if (selection.equals(publicCouponsRadioButton)) {
				publicCouponsSettingController.select(publicCouponsSettingController.getSelection(), true);
			}

			if (selection.equals(privateCouponsRadioButton)) {
				privateCouponsSettingController.select(privateCouponsSettingController.getSelection(), true);
			}

		}

		super.setVisible(show);
	}

	private void updateRuleModel() {
		Rule ruleModel = ((NewShoppingCartPromotionWizard) getWizard()).getModel();
		if (ruleModel != null) {
			boolean isCartItemAction = false;
			// rule model might not be set at construction so we assume false
			for (RuleAction action : ruleModel.getActions()) {
				if (DiscountType.CART_ITEM_DISCOUNT.equals(action.getDiscountType())) {
					isCartItemAction = true;
				}
			}

			publicCouponsSettingController.setHasRuleACartItemAction(isCartItemAction);

			privateCouponsSettingController.setHasRuleACartItemAction(isCartItemAction);

			couponUsageTypeController.setHasRuleACartItemAction(isCartItemAction);

			limitMultiUsePerOrderSettingController.select(limitMultiUsePerOrderSettingController.getSelection(), isCartItemAction
					&& limitMultiUsePerOrderSettingController.getSelection().getSelection());
		}
	}

	@Override
	protected void populateControls() {
		couponUsageTypeController.addDependentControl(noCouponsRadioButton, null);

		for (Control control : publicCouponsSettingController.getParentControls()) {
			couponUsageTypeController.addDependentControl(publicCouponsRadioButton, control);
		}

		for (Control control : privateCouponsSettingController.getParentControls()) {
			couponUsageTypeController.addDependentControl(privateCouponsRadioButton, control);
		}

		for (Control control : expireDaysSettingController.getParentControls()) {
			couponUsageTypeController.addDependentControl(privateCouponsRadioButton, control);
		}

		couponUsageTypeController.addDependentControl(publicCouponsRadioButton, manageCouponsButton);
		couponUsageTypeController.addDependentControl(privateCouponsRadioButton, manageCouponsButton);

		// set checkbox affected by external flag
		for (Control control : limitMultiUsePerOrderSettingController.getParentControls()) {
			couponUsageTypeController.setAffectedControl(control);
		}

		setDefaultCouponConfig();

		// setting initial state
		couponUsageTypeController.select(noCouponsRadioButton, true);
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		widgetSelected(event);
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		Button source = (Button) event.getSource();

		if (!source.getSelection()) {
			return;
		}

		// pops up unsaved coupon warning dialog.
		CouponCollectionModel couponUsageCollectionModel = widgetFactory.getCouponUsageCollectionModel();
		if (!couponUsageCollectionModel.getObjectsToAdd().isEmpty() || !couponUsageCollectionModel.getObjectsToDelete().isEmpty()) {

			boolean confirm = MessageDialog.openConfirm(getShell(), PromotionsMessages.get().CouponConfigPageUnsavedCouponsTitle,
					PromotionsMessages.get().CouponConfigPageUnsavedCoupons);
			if (!confirm) {
				couponUsageTypeController.select(couponUsageTypeController.getSelection(), true);
				return;
			}
		}

		// release the page first
		setPageComplete(true);

		if (source.equals(noCouponsRadioButton)) {
			couponUsageTypeController.select(source, true);
		} else if (source.equals(publicCouponsRadioButton)) {
			couponUsageTypeController.select(source, true);
			publicCouponsSettingController.select(publicCouponsSettingController.getSelection(), publicCouponsSettingController.getSelection()
					.getSelection());
			widgetFactory.refreshPublicCouponsSettingFinishState();
		} else if (source.equals(privateCouponsRadioButton)) {
			couponUsageTypeController.select(source, true);
			privateCouponsSettingController.select(privateCouponsSettingController.getSelection(), privateCouponsSettingController.getSelection()
					.getSelection());
			widgetFactory.refreshPrivateCouponsSettingFinishState();
			expireDaysSettingController
					.select(expireDaysSettingController.getSelection(), expireDaysSettingController.getSelection().getSelection());
			widgetFactory.refreshExpireDaysSettingFinishState();
		}

		limitMultiUsePerOrderSettingController.select(limitMultiUsePerOrderSettingController.getSelection(), false);

		widgetFactory.resetCouponUsageCollectionModel();

	}

	@Override
	protected void bindControls() {
		// do nothing
	}

	@Override
	protected void createPageContents(final IPolicyTargetLayoutComposite policyComposite) {
		// do nothing
	}

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		super.applyStatePolicy(statePolicy);
		couponUsageTypeController.select(noCouponsRadioButton, true);
	}

	@Override
	protected boolean isCreateCompositeBlock() {
		return false;
	}

}
