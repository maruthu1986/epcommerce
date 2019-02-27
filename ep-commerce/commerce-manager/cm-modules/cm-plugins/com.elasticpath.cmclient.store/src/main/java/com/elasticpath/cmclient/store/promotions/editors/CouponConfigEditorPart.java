/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.editors;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.cmclient.store.promotions.CouponConfigPageControlsController;
import com.elasticpath.cmclient.store.promotions.MultiUsePerOrderCouponConfigPageControlsController;
import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.DiscountType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;

/**
 * The coupon config editor part that edit an existing coupon config.
 */
public class CouponConfigEditorPart extends AbstractPolicyAwareEditorPageSectionPart {
	private IPolicyTargetLayoutComposite mainPane;

	private final CouponConfig couponConfig;

	private final CouponConfigEditorWidgetFactory widgetFactory;

	private static final int COLUMN_FOUR = 4;

	private static final int COLUMN_FIVE = 5;

	private MultiUsePerOrderCouponConfigPageControlsController publicCouponsSettingController;

	private MultiUsePerOrderCouponConfigPageControlsController privateCouponsSettingController;

	private PolicyActionContainer container;

	private CouponConfigPageControlsController expireDaysSettingController;

	private final Rule ruleModel;

	private boolean isCartItemAction;


	/**
	 * Constructor.
	 *
	 * @param editor   the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 * @param formPage the form page.
	 */
	public CouponConfigEditorPart(final CouponConfigEditorPage formPage,
								  final ShoppingCartPromotionsEditor editor) {
		super(formPage, editor, ExpandableComposite.COMPACT);
		this.couponConfig = editor.getCouponConfigPageModel().getCouponConfig();
		this.ruleModel = editor.getModel();
		this.widgetFactory = new CouponConfigEditorWidgetFactory(this, editor.getCouponConfigPageModel());
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// do nothing
	}

	@Override
	protected void createControls(final Composite parentComposite, final FormToolkit toolkit) {
		container = addPolicyActionContainer("couponConfigPage"); //$NON-NLS-1$

		this.mainPane = PolicyTargetCompositeFactory.wrapLayoutComposite(CompositeFactory.createTableWrapLayoutComposite(parentComposite, 1, false));
		final TableWrapData data = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
		data.grabHorizontal = false;
		this.mainPane.setLayoutData(data);

		IEpLayoutData labelData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, false, true, 1, COLUMN_FOUR);

		CouponUsageType type = couponConfig.getUsageType();
		if (type == null) {
			mainPane.addLabel(PromotionsMessages.get().CouponConfigPageNotActivateByCoupons, labelData, container);
		} else if (type.equals(CouponUsageType.LIMIT_PER_COUPON) || type.equals(CouponUsageType.LIMIT_PER_ANY_USER)) {
			mainPane.addLabel(PromotionsMessages.get().CouponConfigPageActivatedByPublicCoupons, labelData, container);
			labelData = mainPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER);

			IEpLayoutData publicCouponsSettingCompositeData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, false, true);
			IPolicyTargetLayoutComposite publicCouponsSettingComposite = mainPane.addGridLayoutComposite(COLUMN_FIVE, false,
					publicCouponsSettingCompositeData, container);
			publicCouponsSettingController = widgetFactory.createPublicCouponsSetting(publicCouponsSettingComposite, container);
			widgetFactory.createLimitMultiUsePerOrderSetting(mainPane, container);
			widgetFactory.linkLimitMultiUsePerOrderToPublicCouponSettingControls();
		} else if (type.equals(CouponUsageType.LIMIT_PER_SPECIFIED_USER)) {
			mainPane.addLabel(PromotionsMessages.get().CouponConfigPageActivatedByPrivateCoupons, labelData, container);

			labelData = mainPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER);

			IEpLayoutData privateCouponsSettingCompositeData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, false, true);
			IPolicyTargetLayoutComposite privateCouponsSettingComposite = mainPane.addGridLayoutComposite(COLUMN_FIVE, false,
					privateCouponsSettingCompositeData, container);
			privateCouponsSettingController = widgetFactory.createPrivateCouponsSetting(privateCouponsSettingComposite, container);

			IEpLayoutData emptyData = privateCouponsSettingComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false,
					COLUMN_FOUR, 1);
			privateCouponsSettingComposite.addEmptyComponent(emptyData, container);

			final IEpLayoutData privateCouponsSettingData = privateCouponsSettingComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
			IPolicyTargetLayoutComposite privateCouponsExpireDaysComposite = privateCouponsSettingComposite.addGridLayoutComposite(COLUMN_FOUR,
					false,
					privateCouponsSettingData, container);
			IEpLayoutData privateCouponExpireDaysData = privateCouponsExpireDaysComposite.createLayoutData(IEpLayoutData.BEGINNING,
					IEpLayoutData.CENTER, false, true);
			String couponExpiryText;
			if (couponConfig.isLimitedDuration()) {
				couponExpiryText =

						NLS.bind(PromotionsMessages.get().CouponConfigPagePrivateCouponsExpireDays,
						couponConfig.getDurationDays());
			} else {
				couponExpiryText = PromotionsMessages.get().CouponConfigPagePrivateCouponsNoExpiry;
			}
			privateCouponsExpireDaysComposite.addLabel(couponExpiryText, privateCouponExpireDaysData, container);

			widgetFactory.createLimitMultiUsePerOrderSetting(mainPane, container);
			widgetFactory.linkLimitMultiUsePerOrderToPrivateCouponSettingControls();
		}
		enableAllWidgets();
	}

	private void enableAllWidgets() {
		StatePolicy statePolicy = new AbstractStatePolicyImpl() {
			@Override
			public EpState determineState(final PolicyActionContainer targetContainer) {
				return EpState.EDITABLE;
			}

			@Override
			public void init(final Object dependentObject) {
				// not applicable
			}
		};
		super.applyStatePolicy(statePolicy);
	}

	@Override
	protected void populateControls() {
		// The rule model needs to be updated on populate to ensure that the multi use per order controller is configured correctly.
		updateRuleModel();
		if (publicCouponsSettingController != null) {
			publicCouponsSettingController.select(publicCouponsSettingController.getSelection(), publicCouponsSettingController.getSelection()
					.getSelection());
		}

		if (privateCouponsSettingController != null) {
			privateCouponsSettingController.select(privateCouponsSettingController.getSelection(), privateCouponsSettingController.getSelection()
					.getSelection());
		}

		if (expireDaysSettingController != null) {
			expireDaysSettingController
					.select(expireDaysSettingController.getSelection(), expireDaysSettingController.getSelection().getSelection());
		}
	}

	private void updateRuleModel() {
		if (ruleModel != null) {
			updateRuleModelUpdate(ruleModel);
		}
	}

	private void updateRuleModelUpdate(final Rule ruleModel) {
		isCartItemAction = false;
		if (ruleModel != null) {
			// rule model might not be set at construction so we assume false
			for (RuleAction action : ruleModel.getActions()) {
				if (DiscountType.CART_ITEM_DISCOUNT.equals(action.getDiscountType())) {
					isCartItemAction = true;
				}
			}
		}
		if (publicCouponsSettingController != null) {
			publicCouponsSettingController.setHasRuleACartItemAction(isCartItemAction);
		}

		if (privateCouponsSettingController != null) {
			privateCouponsSettingController.setHasRuleACartItemAction(isCartItemAction);
		}
	}

	/**
	 * Notify the page of a need to refresh.
	 * Trying to make sure things are disabled as appropriate.
	 */
	public void notifyRefresh() {
		updateRuleModel();
		if (publicCouponsSettingController != null) {
			publicCouponsSettingController.select(publicCouponsSettingController.getSelection(), true);
		}
		if (privateCouponsSettingController != null) {
			privateCouponsSettingController.select(privateCouponsSettingController.getSelection(), true);
		}
	}


	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		super.applyStatePolicy(statePolicy);

		EpState statePolicyState = statePolicy.determineState(container);

		if (privateCouponsSettingController != null) {
			privateCouponsSettingController.applyStatePolicyState(statePolicyState);
		}

		if (publicCouponsSettingController != null) {
			publicCouponsSettingController.applyStatePolicyState(statePolicyState);
		}
	}
}
