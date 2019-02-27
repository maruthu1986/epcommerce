/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.promotions.CouponCollectionModel;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsageType;

/**
 * An abstract factory to construct coupon config widgets.
 */
@SuppressWarnings({"PMD.TooManyFields", "PMD.TooManyMethods", "PMD.GodClass"})
public abstract class AbstractCouponConfigWidgetFactory {
	/**
	 * Strategy to update private coupon setting.
	 */
	private class PrivateCouponsSettingUpdateStrategy implements CouponConfigPageModelUpdateStrategy {
		@Override
		public void updateModel(final Control control) {
			if (control.equals(privateCouponsNoLimitRadioButton)) {
				model.setUnlimited();
			}

			if (control.equals(privateCouponsMaxUseRadioButton) || control.equals(privateCouponsMaxUseText)) {
				int maxUses;

				try {
					maxUses = Integer.valueOf(privateCouponsMaxUseText.getText());
				} catch (Exception exception) {
					maxUses = 0;
				}

				model.setUsageLimit(maxUses);
			}


		}
	}

	/**
	 * Strategy to update private coupon expire days setting.
	 */
	private class ExpireDaysSettingUpdateStrategy implements CouponConfigPageModelUpdateStrategy {
		@Override
		public void updateModel(final Control control) {
			if ((control.equals(expireDaysSettingCheckbox) && expireDaysSettingCheckbox.getSelection())
					|| control.equals(expireDaysSettingText)) {
				boolean isLimitedDuration;
				int durationDays;

				try {
					durationDays = Integer.valueOf(expireDaysSettingText.getText());
					isLimitedDuration = true;
				} catch (Exception exception) {
					durationDays = 0;
					isLimitedDuration = false;
				}
				model.setLimitedDuration(isLimitedDuration);
				model.setDurationDays(durationDays);
			} else if ((control.equals(expireDaysSettingCheckbox) && !expireDaysSettingCheckbox.getSelection())) {
				model.setLimitedDuration(false);
				model.setDurationDays(0);
			}
		}
	}

	/**
	 * Strategy to update public coupon setting.
	 */
	private class PublicCouponsSettingUpdateValueStrategy implements CouponConfigPageModelUpdateStrategy {
		@Override
		public void updateModel(final Control control) {
			if (control.equals(publicCouponsNoLimitRadioButton)) {
				model.setUsageType(CouponUsageType.LIMIT_PER_COUPON);
				model.setUnlimited();
			}

			if (control.equals(publicCouponsMaxUseRadioButton) || control.equals(publicCouponsMaxUseText)) {
				model.setUsageType(CouponUsageType.LIMIT_PER_COUPON);

				int maxUses;
				try {
					maxUses = Integer.valueOf(publicCouponsMaxUseText.getText());
				} catch (Exception exception) {
					maxUses = 0;
				}

				model.setUsageLimit(maxUses);
			}

			if (publicCouponsMaxUseEachShopperRadioButton.getSelection() || control.equals(publicCouponsMaxUseEachShopperText)) {
				model.setUsageType(CouponUsageType.LIMIT_PER_ANY_USER);

				int maxUses;
				try {
					maxUses = Integer.valueOf(publicCouponsMaxUseEachShopperText.getText());
				} catch (Exception exception) {
					maxUses = 0;
				}

				model.setUsageLimit(maxUses);
			}

		}
	}

	/**
	 * Strategy to update limit Multi-Use Coupon per Order setting.
	 */
	private class LimitMultiUsePerOrderSettingUpdateStrategy implements CouponConfigPageModelUpdateStrategy {
		@Override
		public void updateModel(final Control control) {
			if (control.equals(limitMultiUsePerOrderSettingCheckbox) && limitMultiUsePerOrderSettingCheckbox.getSelection()) {
				model.setMultiUsePerOrder(true);
			} else {
				model.setMultiUsePerOrder(false);
			}
		}
	}

	private static final int COLUMN_FOUR = 4;

	private static final int COLUMN_THREE = 3;

	private static final String DEFAULT_EXPIRE_DAYS = "60"; //$NON-NLS-1$

	private static final String DEFAULT_MAX_USES_TIMES = "1"; //$NON-NLS-1$

	private static final int DEFAULT_TEXT_FIELD_WIDTH = 40;

	private VerifyListener integerVerifyListener;

	private CouponConfig model;

	private Button expireDaysSettingCheckbox;

	private Label expireDaysSettingLabel;

	private Text expireDaysSettingText;

	private Label privateCouponsMaxUseLabel;

	private Button privateCouponsMaxUseRadioButton;

	private Text privateCouponsMaxUseText;

	private Button privateCouponsNoLimitRadioButton;

	private MultiUsePerOrderCouponConfigPageControlsController privateCouponsSettingController;

	private CouponConfigPageControlsController expireDaysSettingController;

	private ExpireDaysSettingUpdateStrategy expireDaysSettingUpdateStrategy;

	private PrivateCouponsSettingUpdateStrategy privateCouponsSettingUpdateStrategy;

	private Label publicCouponsMaxUseTextLabel;

	private MultiUsePerOrderCouponConfigPageControlsController publicCouponsSettingController;

	private Label publicCouponsMaxUseEachShopperLabel;

	private Button publicCouponsMaxUseEachShopperRadioButton;

	private Text publicCouponsMaxUseEachShopperText;

	private Button publicCouponsMaxUseRadioButton;

	private Text publicCouponsMaxUseText;

	private Button publicCouponsNoLimitRadioButton;

	private PublicCouponsSettingUpdateValueStrategy publicCouponsSettingUpdateValueStrategy;

	private CouponCollectionModel couponUsageCollectionModel;

	private Button limitMultiUsePerOrderSettingCheckbox;

	private LimitMultiUsePerOrderSettingUpdateStrategy limitMultiUsePerOrderSettingUpdateStrategy;

	private MultiUsePerOrderCouponConfigPageControlsController limitMultiUsePerOrderSettingController;

	/**
	 * The constructor.
	 *
	 * @param couponConifgPageModel the coupon config model.
	 */
	public AbstractCouponConfigWidgetFactory(final CouponConfigPageModel couponConifgPageModel) {
		this.model = couponConifgPageModel.getCouponConfig();
		this.couponUsageCollectionModel = couponConifgPageModel.getCouponUsageCollectionModel();
	}

	/**
	 * set the coupon config page model.
	 *
	 * @param couponConifgPageModel the coupon config page model
	 */
	public void setCouponConfigPageModel(final CouponConfigPageModel couponConifgPageModel) {
		this.model = couponConifgPageModel.getCouponConfig();
		this.couponUsageCollectionModel = couponConifgPageModel.getCouponUsageCollectionModel();
	}

	private void addEmptyPadding(final IPolicyTargetLayoutComposite parentComposite, final PolicyActionContainer container) {
		IEpLayoutData emptyData = parentComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false, COLUMN_FOUR, 1);
		parentComposite.addEmptyComponent(emptyData, container);
	}

	/**
	 * Create private coupons controller.
	 *
	 * @param parentComposite the parent.
	 * @param container       the container.
	 * @return the {@link MultiUsePerOrderCouponConfigPageControlsController}.
	 */
	public MultiUsePerOrderCouponConfigPageControlsController createPrivateCouponsSetting(final IPolicyTargetLayoutComposite parentComposite,
																						  final PolicyActionContainer container) {
		createPrivateCouponsSettingWidgets(parentComposite, container);

		return populatePrivateCouponSettingControls();
	}

	private void createPrivateCouponsSettingWidgets(final IPolicyTargetLayoutComposite parentComposite,
													final PolicyActionContainer container) {
		final IEpLayoutData privateCouponsSettingData = parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);

		// private coupons no limit
		addEmptyPadding(parentComposite, container);

		IPolicyTargetLayoutComposite privateCouponsNoLimitComposite = parentComposite.addGridLayoutComposite(1, false, privateCouponsSettingData,
				container);
		privateCouponsNoLimitRadioButton = privateCouponsNoLimitComposite.addRadioButton(PromotionsMessages.
				get().CouponConfigPagePrivateCouponsNoLimit, privateCouponsSettingData, container);
		privateCouponsNoLimitRadioButton.addSelectionListener(getPrivateCouponsSettingSelectionListener());

		// private coupons max use
		addEmptyPadding(parentComposite, container);

		IPolicyTargetLayoutComposite privateCouponsMaxUseComposite = parentComposite.addGridLayoutComposite(COLUMN_THREE, false,
				privateCouponsSettingData, container);
		IEpLayoutData privateCouponsMaxUseData = privateCouponsMaxUseComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER,
				false, true);

		String[] splitTextOfCouponsLimit = splitTextByParameters(PromotionsMessages.get().CouponConfigPagePrivateCouponsLimited);
		privateCouponsMaxUseRadioButton = privateCouponsMaxUseComposite.addRadioButton(splitTextOfCouponsLimit[0], privateCouponsMaxUseData,
				container);
		privateCouponsMaxUseRadioButton.addSelectionListener(getPrivateCouponsSettingSelectionListener());

		privateCouponsMaxUseText = privateCouponsMaxUseComposite.addTextField(privateCouponsMaxUseData, container);
		privateCouponsMaxUseText.setLayoutData(createTextLayoutData());

		privateCouponsMaxUseLabel = privateCouponsMaxUseComposite.addLabel(splitTextOfCouponsLimit[1], privateCouponsMaxUseData, container);

	}

	/**
	 * Creates the expire days setting.
	 *
	 * @param parentComposite the parent composite.
	 * @param container       the policy container.
	 * @return the {@link CouponConfigPageControlsController}.
	 */
	public CouponConfigPageControlsController createExpireDaysSetting(final IPolicyTargetLayoutComposite parentComposite,
																	  final PolicyActionContainer container) {
		createExpireDaysSettingWidgets(parentComposite, container);

		return populateExpireDaysSettingControls();
	}

	private void createExpireDaysSettingWidgets(final IPolicyTargetLayoutComposite parentComposite, final PolicyActionContainer container) {
		final IEpLayoutData privateCouponsSettingData = parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);

		IPolicyTargetLayoutComposite privateCouponsExpireDaysComposite = parentComposite.addGridLayoutComposite(COLUMN_FOUR, false,
				privateCouponsSettingData, container);
		IEpLayoutData privateCouponExpireDaysData = privateCouponsExpireDaysComposite.createLayoutData(IEpLayoutData.BEGINNING,
				IEpLayoutData.CENTER, false, true);

		String[] splitTexOfExpireDays = splitTextByParameters(PromotionsMessages.get().CouponConfigPagePrivateCouponsExpireDaysTagged);
		expireDaysSettingCheckbox = privateCouponsExpireDaysComposite.addCheckBoxButton(splitTexOfExpireDays[0], privateCouponExpireDaysData,
				container);
		expireDaysSettingText = privateCouponsExpireDaysComposite.addTextField(privateCouponExpireDaysData, container);
		expireDaysSettingText.setLayoutData(createTextLayoutData());

		expireDaysSettingLabel = privateCouponsExpireDaysComposite.addLabel(splitTexOfExpireDays[1], privateCouponExpireDaysData, container);
	}

	private SelectionListener getExpireDaysSettingSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				Button source = (Button) event.getSource();

				expireDaysSettingController.select(source, source.getSelection());

				refreshExpireDaysSettingFinishState();

				widgetChanged(source);
			}
		};
	}

	private CouponConfigPageControlsController populateExpireDaysSettingControls() {
		expireDaysSettingUpdateStrategy = new ExpireDaysSettingUpdateStrategy();

		expireDaysSettingController = new CouponConfigPageControlsController(expireDaysSettingUpdateStrategy);
		expireDaysSettingController.addDependentControl(expireDaysSettingCheckbox, expireDaysSettingText);
		expireDaysSettingController.addDependentControl(expireDaysSettingText, null);
		expireDaysSettingController.addDependentControl(expireDaysSettingLabel, null);

		expireDaysSettingText.setText(DEFAULT_EXPIRE_DAYS);


		if (model.isLimitedDuration()) {
			expireDaysSettingText.setText(String.valueOf(model.getDurationDays()));
			expireDaysSettingController.select(expireDaysSettingCheckbox, true);
		} else {
			expireDaysSettingController.select(expireDaysSettingCheckbox, false);
		}

		expireDaysSettingCheckbox.addSelectionListener(getExpireDaysSettingSelectionListener());
		expireDaysSettingText.addVerifyListener(getIntegerVerifyListener());
		expireDaysSettingText.addModifyListener(getExpireDaysSettingModifyListener());

		return expireDaysSettingController;
	}

	private ModifyListener getExpireDaysSettingModifyListener() {
		return (ModifyListener) event -> {
			Control control = (Control) event.widget;
			expireDaysSettingUpdateStrategy.updateModel(control);

			refreshExpireDaysSettingFinishState();

			widgetChanged(event.widget);
		};
	}

	/**
	 * Creates a controller for limiting MultiUse coupons to 1 use per order.
	 *
	 * @param parentComposite the parent composite.
	 * @param container       the container.
	 * @return the {@link MultiUsePerOrderCouponConfigPageControlsController}.
	 */
	public MultiUsePerOrderCouponConfigPageControlsController createLimitMultiUsePerOrderSetting(
			final IPolicyTargetLayoutComposite parentComposite, final PolicyActionContainer container) {
		final IEpLayoutData limitMultiUseCouponsData = parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);

		IPolicyTargetLayoutComposite limitMultiUseCouponComposite = parentComposite.addGridLayoutComposite(COLUMN_FOUR, false,
				limitMultiUseCouponsData, container);

		limitMultiUsePerOrderSettingCheckbox = limitMultiUseCouponComposite.addCheckBoxButton(PromotionsMessages.
				get().CouponConfigPageLimitToSingleOrder, limitMultiUseCouponsData, container);
		return populateLimitMultiUsePerOrderSettingControls();
	}

	private MultiUsePerOrderCouponConfigPageControlsController populateLimitMultiUsePerOrderSettingControls() {
		limitMultiUsePerOrderSettingUpdateStrategy = new LimitMultiUsePerOrderSettingUpdateStrategy();

		limitMultiUsePerOrderSettingController = new MultiUsePerOrderCouponConfigPageControlsController(
				limitMultiUsePerOrderSettingUpdateStrategy);
		limitMultiUsePerOrderSettingController.addDependentControl(limitMultiUsePerOrderSettingCheckbox, null);

		if (model.isMultiUsePerOrder()) {
			limitMultiUsePerOrderSettingController.select(limitMultiUsePerOrderSettingCheckbox, true);
		} else {
			limitMultiUsePerOrderSettingController.select(limitMultiUsePerOrderSettingCheckbox, false);
		}

		limitMultiUsePerOrderSettingCheckbox.addSelectionListener(getLimitMultiUsePerOrderSettingSelectionListener());

		return limitMultiUsePerOrderSettingController;
	}

	private SelectionListener getLimitMultiUsePerOrderSettingSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				Button source = (Button) event.getSource();

				limitMultiUsePerOrderSettingController.select(source, source.getSelection());

				widgetChanged(source);
			}
		};
	}

	/**
	 * Creates a coupon management button.
	 *
	 * @param parentComposite the parent composite.
	 * @param container       the container.
	 * @return the {@link Button}.
	 */
	public Button createManageCouponsButton(final IPolicyTargetLayoutComposite parentComposite, final PolicyActionContainer container) {
		final IEpLayoutData manageCouponsData = parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
		IPolicyTargetLayoutComposite manageCouponComposite = parentComposite.addGridLayoutComposite(1, false, manageCouponsData, container);
		Button manageCouponsButton = manageCouponComposite.addPushButton(
				PromotionsMessages.get().CouponConfigPageManageCouponCodes,
				PromotionsImageRegistry
						.getImage(PromotionsImageRegistry.COUPON_CODES_MANAGE),
				manageCouponComposite.createLayoutData(IEpLayoutData.BEGINNING,
						IEpLayoutData.FILL), container);
		manageCouponsButton.addSelectionListener(getManageCouponsAction());

		return manageCouponsButton;
	}

	/**
	 * Creates a controller for public settings.
	 *
	 * @param parentComposite the parent composite.
	 * @param container       the container.
	 * @return the {@link MultiUsePerOrderCouponConfigPageControlsController}.
	 */
	public MultiUsePerOrderCouponConfigPageControlsController createPublicCouponsSetting(final IPolicyTargetLayoutComposite parentComposite,
																						 final PolicyActionContainer container) {
		createPublicCouponsSettingWidgets(parentComposite, container);

		return populatePublicCouponSettingControls();
	}

	/**
	 * LimitMultiUsePerOrder checkbox needs to be linked into private and public controls.
	 * This does the linkage which must be called after the controllers are created..
	 */
	public void linkLimitMultiUsePerOrderToPrivatePublicCouponSettingControls() {
		linkLimitMultiUsePerOrderToPrivateCouponSettingControls();
		linkLimitMultiUsePerOrderToPublicCouponSettingControls();
	}

	/**
	 * LimitMultiUsePerOrder checkbox needs to be linked into private and public controls.
	 * This does the linkage which must be called after the controllers are created..
	 */
	public void linkLimitMultiUsePerOrderToPrivateCouponSettingControls() {
		privateCouponsSettingController.addDependentControl(privateCouponsMaxUseRadioButton, limitMultiUsePerOrderSettingCheckbox);
		privateCouponsSettingController.addDependentControl(limitMultiUsePerOrderSettingCheckbox, null);
		privateCouponsSettingController.setAffectedControl(limitMultiUsePerOrderSettingCheckbox);
	}

	/**
	 * LimitMultiUsePerOrder checkbox needs to be linked into private and public controls.
	 * This does the linkage which must be called after the controllers are created..
	 */
	public void linkLimitMultiUsePerOrderToPublicCouponSettingControls() {
		publicCouponsSettingController.addDependentControl(publicCouponsMaxUseEachShopperRadioButton, limitMultiUsePerOrderSettingCheckbox);
		publicCouponsSettingController.addDependentControl(limitMultiUsePerOrderSettingCheckbox, null);
		publicCouponsSettingController.setAffectedControl(limitMultiUsePerOrderSettingCheckbox);
	}

	private void createPublicCouponsSettingWidgets(final IPolicyTargetLayoutComposite parentComposite, final PolicyActionContainer container) {
		final IEpLayoutData publicCouponsSettingData = parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 1, 1);

		// public coupons no limit
		addEmptyPadding(parentComposite, container);

		IPolicyTargetLayoutComposite publicCouponsNoLimitComposite = parentComposite.addGridLayoutComposite(1, false, publicCouponsSettingData,
				container);
		publicCouponsNoLimitRadioButton = publicCouponsNoLimitComposite.addRadioButton(PromotionsMessages.get().CouponConfigPagePublicCouponsNoLimit,
				publicCouponsSettingData, container);
		publicCouponsNoLimitRadioButton.addSelectionListener(getPublicCouponsSettingSelectionListener());

		// public coupons max use
		addEmptyPadding(parentComposite, container);

		IPolicyTargetLayoutComposite publicCouponsMaxUseComposite = parentComposite.addGridLayoutComposite(COLUMN_THREE, false,
				publicCouponsSettingData, container);
		IEpLayoutData publicCouponsMaxUseData = publicCouponsMaxUseComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER, false,
				true);

		String[] splitLimitedTextByParameters = splitTextByParameters(PromotionsMessages.get().CouponConfigPagePublicCouponsLimited);
		publicCouponsMaxUseRadioButton = publicCouponsMaxUseComposite.addRadioButton(splitLimitedTextByParameters[0], publicCouponsMaxUseData,
				container);
		publicCouponsMaxUseRadioButton.addSelectionListener(getPublicCouponsSettingSelectionListener());

		publicCouponsMaxUseText = publicCouponsMaxUseComposite.addTextField(publicCouponsMaxUseData, container);
		publicCouponsMaxUseText.setLayoutData(createTextLayoutData());

		publicCouponsMaxUseTextLabel = publicCouponsMaxUseComposite.addLabel(splitLimitedTextByParameters[1], publicCouponsMaxUseData, container);

		// public coupons with type of limitPerAnyUser
		addEmptyPadding(parentComposite, container);

		IPolicyTargetLayoutComposite publicCouponsMaxUseEachShopperComposite = parentComposite.addGridLayoutComposite(COLUMN_THREE, false,
				publicCouponsSettingData, container);
		IEpLayoutData publicCouponsMaxUseEachShopperData = publicCouponsMaxUseEachShopperComposite.createLayoutData(IEpLayoutData.BEGINNING,
				IEpLayoutData.CENTER, false, true);

		String[] splitLimitedEachShopperTextByParameters = splitTextByParameters(PromotionsMessages.
				get().CouponConfigPagePublicCouponsLimitedEachShopper);
		publicCouponsMaxUseEachShopperRadioButton = publicCouponsMaxUseEachShopperComposite.addRadioButton(
				splitLimitedEachShopperTextByParameters[0], publicCouponsMaxUseEachShopperData, container);
		publicCouponsMaxUseEachShopperRadioButton.addSelectionListener(getPublicCouponsSettingSelectionListener());

		publicCouponsMaxUseEachShopperText = publicCouponsMaxUseEachShopperComposite.addTextField(publicCouponsMaxUseEachShopperData, container);
		publicCouponsMaxUseEachShopperText.setLayoutData(createTextLayoutData());

		publicCouponsMaxUseEachShopperLabel = publicCouponsMaxUseEachShopperComposite.addLabel(splitLimitedEachShopperTextByParameters[1],
				publicCouponsMaxUseEachShopperData, container);
	}

	private SelectionAdapter getManageCouponsAction() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				CouponCollectionModel newCouponUsageCollectionModel = new CouponCollectionModel(model.getUsageType());
				if (couponUsageCollectionModel != null && isSameUsageType()) {
					newCouponUsageCollectionModel.copyFrom(couponUsageCollectionModel);
				}

				CouponWizardValidatorImpl validator = new CouponWizardValidatorImpl();
				validator.setModel(newCouponUsageCollectionModel);
				newCouponUsageCollectionModel.setCouponValidator(validator);

				newCouponUsageCollectionModel.setCouponConfig(model);
				CouponEditorDialog dialog = new CouponEditorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						newCouponUsageCollectionModel, model.getUsageType());
				if (dialog.open() == Window.OK) {
					couponUsageCollectionModel = newCouponUsageCollectionModel;
				}
			}

			/**
			 * Checks that whether the usage type is changed according to selection. limit per any user and limit per coupon should be regarded as
			 * the same type under public coupon.
			 *
			 * @return true if they are the same type.
			 */
			private boolean isSameUsageType() {
				CouponUsageType oldUsageType = model.getUsageType();
				CouponUsageType newUsageType = couponUsageCollectionModel.getUsageType();

				if (CouponUsageType.LIMIT_PER_SPECIFIED_USER.equals(oldUsageType) && CouponUsageType.LIMIT_PER_SPECIFIED_USER.equals(newUsageType)) {
					return true;
				}

				List<CouponUsageType> publicUsageTypes = Arrays.asList(CouponUsageType.LIMIT_PER_ANY_USER, CouponUsageType.LIMIT_PER_COUPON);
				return publicUsageTypes.contains(oldUsageType) && publicUsageTypes.contains(newUsageType);
			}
		};
	}

	/**
	 * Gets the coupon usage collection model.
	 *
	 * @return {@link CouponCollectionModel}.
	 */
	public CouponCollectionModel getCouponUsageCollectionModel() {
		return couponUsageCollectionModel;
	}

	/**
	 * Resets the coupon usage collection model.
	 */
	public void resetCouponUsageCollectionModel() {
		couponUsageCollectionModel = new CouponCollectionModel(model.getUsageType());
	}

	private GridData createTextLayoutData() {
		final GridData gridData = new GridData();
		gridData.widthHint = DEFAULT_TEXT_FIELD_WIDTH;
		return gridData;
	}

	private VerifyListener getIntegerVerifyListener() {
		if (integerVerifyListener == null) {
			integerVerifyListener = (VerifyListener) event -> {
				event.doit = event.text.matches("\\d*"); //$NON-NLS-1$
			};
		}

		return integerVerifyListener;
	}

	private ModifyListener getPrivateCouponsSettingModifyListener() {
		return (ModifyListener) event -> {
			Control control = (Control) event.widget;
			privateCouponsSettingUpdateStrategy.updateModel(control);

			refreshPrivateCouponsSettingFinishState();

			widgetChanged(event.widget);
		};
	}

	private SelectionListener getPrivateCouponsSettingSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				Button source = (Button) event.getSource();

				boolean state = true;
				if ((source.getStyle() & SWT.CHECK) != 0) {
					state = source.getSelection();
				}

				privateCouponsSettingController.select(source, state);

				if (limitMultiUsePerOrderSettingController != null) {
					limitMultiUsePerOrderSettingController.select(limitMultiUsePerOrderSettingController.getSelection(), false);
				}

				refreshPrivateCouponsSettingFinishState();

				widgetChanged(source);
			}
		};
	}

	private ModifyListener getPublicCouponsSettingModifyListener() {
		return (ModifyListener) event -> {
			Control control = (Control) event.widget;
			publicCouponsSettingUpdateValueStrategy.updateModel(control);

			refreshPublicCouponsSettingFinishState();

			widgetChanged(event.widget);
		};
	}


	private SelectionListener getPublicCouponsSettingSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				Button source = (Button) event.getSource();
				publicCouponsSettingController.select(source, true);

				if (limitMultiUsePerOrderSettingController != null) {
					limitMultiUsePerOrderSettingController.select(limitMultiUsePerOrderSettingController.getSelection(), false);
				}

				refreshPublicCouponsSettingFinishState();

				widgetChanged(source);
			}
		};
	}

	private MultiUsePerOrderCouponConfigPageControlsController populatePrivateCouponSettingControls() {
		privateCouponsSettingUpdateStrategy = new PrivateCouponsSettingUpdateStrategy();

		privateCouponsSettingController = new MultiUsePerOrderCouponConfigPageControlsController(privateCouponsSettingUpdateStrategy);
		privateCouponsSettingController.addDependentControl(privateCouponsNoLimitRadioButton, null);
		privateCouponsSettingController.addDependentControl(privateCouponsMaxUseRadioButton,
				privateCouponsMaxUseText);
		privateCouponsSettingController.addDependentControl(privateCouponsMaxUseText, null);
		privateCouponsSettingController.addDependentControl(privateCouponsMaxUseLabel, null);

		// sets the initial state
		privateCouponsMaxUseText.setText(DEFAULT_MAX_USES_TIMES);

		if (model.isUnlimited()) {
			privateCouponsSettingController.select(privateCouponsNoLimitRadioButton, true);
		} else {
			privateCouponsMaxUseText.setText(String.valueOf(model.getUsageLimit()));
			privateCouponsSettingController.select(privateCouponsMaxUseRadioButton, true);
		}

		privateCouponsMaxUseText.addVerifyListener(getIntegerVerifyListener());
		privateCouponsMaxUseText.addModifyListener(getPrivateCouponsSettingModifyListener());

		return privateCouponsSettingController;
	}

	private MultiUsePerOrderCouponConfigPageControlsController populatePublicCouponSettingControls() {
		publicCouponsSettingUpdateValueStrategy = new PublicCouponsSettingUpdateValueStrategy();

		publicCouponsSettingController = new MultiUsePerOrderCouponConfigPageControlsController(publicCouponsSettingUpdateValueStrategy);
		publicCouponsSettingController.addDependentControl(publicCouponsNoLimitRadioButton, null);
		publicCouponsSettingController.addDependentControl(publicCouponsMaxUseRadioButton, publicCouponsMaxUseText);
		publicCouponsSettingController.addDependentControl(publicCouponsMaxUseEachShopperRadioButton,
				publicCouponsMaxUseEachShopperText);
		publicCouponsSettingController.addDependentControl(publicCouponsMaxUseEachShopperText, null);
		publicCouponsSettingController.addDependentControl(publicCouponsMaxUseText, null);
		publicCouponsSettingController.addDependentControl(publicCouponsMaxUseTextLabel, null);
		publicCouponsSettingController.addDependentControl(publicCouponsMaxUseEachShopperLabel, null);

		// sets the initial state
		publicCouponsMaxUseText.setText(DEFAULT_MAX_USES_TIMES);
		publicCouponsMaxUseEachShopperText.setText(DEFAULT_MAX_USES_TIMES);

		if (model.isUnlimited()) {
			publicCouponsSettingController.select(publicCouponsNoLimitRadioButton, true);
		} else if (CouponUsageType.LIMIT_PER_ANY_USER.equals(model.getUsageType())) {
			publicCouponsMaxUseEachShopperText.setText(String.valueOf(model.getUsageLimit()));
			publicCouponsSettingController.select(publicCouponsMaxUseEachShopperRadioButton, true);
		} else {
			publicCouponsMaxUseText.setText(String.valueOf(model.getUsageLimit()));
			publicCouponsSettingController.select(publicCouponsMaxUseRadioButton, true);
		}

		publicCouponsMaxUseText.addVerifyListener(getIntegerVerifyListener());
		publicCouponsMaxUseText.addModifyListener(getPublicCouponsSettingModifyListener());
		publicCouponsMaxUseEachShopperText.addVerifyListener(getIntegerVerifyListener());
		publicCouponsMaxUseEachShopperText.addModifyListener(getPublicCouponsSettingModifyListener());

		return publicCouponsSettingController;
	}

	/**
	 * Refreshes the private coupons setting finish state.
	 */
	public void refreshPrivateCouponsSettingFinishState() {
		if (textIsEnabledButNoContent(privateCouponsMaxUseText)) {
			setFinishStatus(false, PromotionsMessages.get().CouponConfigPageNoMaxUseError);
			return;
		}

		setFinishStatus(true, null);
	}

	/**
	 * Refreshes the expire days setting finish state.
	 */
	public void refreshExpireDaysSettingFinishState() {
		if (textIsEnabledButNoContent(expireDaysSettingText)) {
			setFinishStatus(false, PromotionsMessages.get().CouponConfigPageNoExpireDaysError);
			return;
		}

		setFinishStatus(true, null);
	}

	/**
	 * Refreshes the public coupons setting finish state.
	 */
	public void refreshPublicCouponsSettingFinishState() {
		if (textIsEnabledButNoContent(publicCouponsMaxUseText)) {
			setFinishStatus(false, PromotionsMessages.get().CouponConfigPageNoMaxUseError);
			return;
		}

		if (textIsEnabledButNoContent(publicCouponsMaxUseEachShopperText)) {
			setFinishStatus(false, PromotionsMessages.get().CouponConfigPageNoMaxUseEachShopperError);
			return;
		}

		setFinishStatus(true, null);
	}

	/**
	 * Sets the finish status. Children should override this to provide specific implementation.
	 *
	 * @param status         the status.
	 * @param errorMessages the error messages.
	 */
	protected abstract void setFinishStatus(final boolean status, String errorMessages);

	private String[] splitTextByParameters(final String text) {
		return text.split("\\[\\d*\\]"); //$NON-NLS-1$
	}

	private boolean textIsEnabledButNoContent(final Text text) {
		return text.isEnabled() && text.getText().length() == 0;
	}

	/**
	 * A certain widget is changed (selection change or modification change). Children should override this to provide specific implementation.
	 *
	 * @param widget the changed widget.
	 */
	public abstract void widgetChanged(final Widget widget);
}
