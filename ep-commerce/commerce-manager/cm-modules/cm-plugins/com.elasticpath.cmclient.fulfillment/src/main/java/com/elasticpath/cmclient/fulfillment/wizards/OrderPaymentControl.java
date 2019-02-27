/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.domain.impl.OrderPaymentPresenterFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.PaymentsComparatorFactory;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Provides selection of payment source - original or input new one.
 */
public class OrderPaymentControl {

	/**
	 * An enumeration describing all the possible modes for that control.
	 */
	public enum OrderPaymentControlMode {

		/**
		 * Enables all the components of the control.
		 */
		ALL_ENABLED,
		/**
		 * Disables all the components of the control.
		 */
		ALL_DISABLED,
		/**
		 * Enables only the control for using the original account.
		 */
		ENABLE_ORIGINAL_ACCOUNT_ONLY,
		/**
		 * Enables only the control for using a new account.
		 */
		ENABLE_NEW_ACCOUNT_ONLY
	}

	private static final int MARGIN = 15;

	private final OrderPayment blankPayment;

	private final DataBindingContext bindingContext;

	private final IEpLayoutComposite refundOptionsPageComposite;

	private Button originalPaymentRadioButton;

	private CCombo originalPaymentSourceCombo;

	private Label originalPaymentSourceComboLabel;

	private final List<OrderPayment> originalPayments = new ArrayList<>();

	private OrderPayment selectedOriginalPayment;

	private EpValueBinding originalPaymentComboBinding;

	private OrderPaymentControlMode mode;

	/**
	 * The constructor.
	 *
	 * @param order order.
	 * @param pageComposite composite on which the control will be created.
	 * @param layoutData layout used to place the control.
	 * @param bindingContext binding context.
	 */
	public OrderPaymentControl(final Order order, final IEpLayoutComposite pageComposite, final IEpLayoutData layoutData,
			final DataBindingContext bindingContext) {
		blankPayment = ServiceLocator.getService(ContextIdNames.ORDER_PAYMENT);
		blankPayment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		blankPayment.setCurrencyCode(order.getCurrency().getCurrencyCode());

		this.bindingContext = bindingContext;
		refundOptionsPageComposite = pageComposite.addTableWrapLayoutComposite(1, false, layoutData);


		final List<OrderPayment> allUniqueOrderPayments = PaymentsComparatorFactory.getListOfUniquePayments(OrderPayment.CAPTURE_TRANSACTION, order
				.getOrderPayments(), PaymentType.PAYPAL_EXPRESS, PaymentType.GIFT_CERTIFICATE);

		originalPayments.addAll(allUniqueOrderPayments);
		createEpPageContent();
	}

	/**
	 * @return if original payment is selected.
	 */
	public boolean useOriginalPayment() {
		return originalPaymentRadioButton.getSelection();
	}

	/**
	 * @return selected payment (original or newly inputed).
	 */
	public OrderPayment getPayment() {
		if (useOriginalPayment()) {
			return selectedOriginalPayment;
		}
		return blankPayment;
	}

	private void createEpPageContent() {
		originalPaymentRadioButton = refundOptionsPageComposite.addRadioButton(FulfillmentMessages.get().RefundWizard_OriginalPaymentSource,
				EpState.EDITABLE, refundOptionsPageComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING));

		final IEpLayoutComposite selectPaymentSourceComposite = refundOptionsPageComposite.addTableWrapLayoutComposite(2, false,
				refundOptionsPageComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false));

		final TableWrapLayout originalPaymentLayout = (TableWrapLayout) selectPaymentSourceComposite.getSwtComposite().getLayout();
		originalPaymentLayout.leftMargin = MARGIN;

		originalPaymentSourceComboLabel = selectPaymentSourceComposite.addLabelBoldRequired(
				"Payment Source", EpState.EDITABLE, selectPaymentSourceComposite.createLayoutData()); //$NON-NLS-1$

		originalPaymentSourceCombo = selectPaymentSourceComposite.addComboBox(EpState.EDITABLE, selectPaymentSourceComposite.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false));

		originalPaymentSourceCombo.add("Select a payment source..."); //$NON-NLS-1$
		OrderPaymentPresenterFactory presenterFactory = new OrderPaymentPresenterFactory();
		for (final OrderPayment payment : originalPayments) {
			originalPaymentSourceCombo.add(presenterFactory.getOrderPaymentPresenter(payment).getDisplayPaymentDetails());
		}
		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();
		originalPaymentComboBinding = binder.bind(bindingContext, originalPaymentSourceCombo,
				EpValidatorFactory.REQUIRED_COMBO_FIRST_ELEMENT_NOT_VALID, null, new OriginalPaymentComboStrategy(), true);
		originalPaymentSourceCombo.select(0);

		boolean hasOriginal = !originalPayments.isEmpty();
		setChildEnabled(true);

		originalPaymentRadioButton.setSelection(hasOriginal);
		originalPaymentSourceCombo.setEnabled(hasOriginal);

		refreshBindings();
	}

	/**
	 * Update strategy for original payments combobox.
	 */
	class OriginalPaymentComboStrategy extends ObservableUpdateValueStrategy {

		@Override
		protected IStatus doSet(final IObservableValue observableValue, final Object value) {
			if (!originalPaymentSourceCombo.getEnabled()) {
				return Status.OK_STATUS;
			}
			int selectedIndex = (Integer) value;

			if (selectedIndex == 0) {
				selectedOriginalPayment = null;
			} else {
				selectedOriginalPayment = originalPayments.get(selectedIndex - 1);
			}

			if (selectedOriginalPayment == null && originalPaymentSourceCombo.isEnabled()) {
				return ValidationStatus.error(""); //$NON-NLS-1$
			}
			return Status.OK_STATUS;
		}

	}

	private void refreshBindings() {
		final IObservableList bindings = bindingContext.getBindings();
		if (originalPaymentSourceCombo.isEnabled() && !bindings.contains(originalPaymentComboBinding.getBinding())) {
			bindingContext.addBinding(originalPaymentComboBinding.getBinding());
		} else if (!originalPaymentSourceCombo.isEnabled() && bindings.contains(originalPaymentComboBinding.getBinding())) {
			bindingContext.removeBinding(originalPaymentComboBinding.getBinding());
		}
	}

	/**
	 * Enables composite and controls if the argument is <code>true</code>, and disables it otherwise.
	 *
	 * @param enabled if control should be enabled.
	 */
	private void setEnabled(final boolean enabled) {
		setChildEnabled(enabled);
		bindingContext.updateTargets();
	}

	/**
	 * Changes the mode of this control.
	 *
	 * @param mode the new mode which is one of the {@link OrderPaymentControlMode}
	 */
	public void changeMode(final OrderPaymentControlMode mode) {
		this.mode = mode;
		updateState();
	}

	/**
	 * Updates the UI state of that control depending on the selected mode.
	 */
	protected void updateState() {
		switch (this.mode) {
			case ALL_DISABLED:
				setEnabled(false);
				break;
			case ALL_ENABLED:
				setEnabled(true);
				break;
			case ENABLE_ORIGINAL_ACCOUNT_ONLY:
				enableOnlyOriginalPaymentSourceOption();
				break;
			default:
				setEnabled(true);
				break;
		}
		refreshBindings();
	}

	/**
	 * Sets only the original payment source option enabled/disabled.
	 */
	private void enableOnlyOriginalPaymentSourceOption() {
		enableAndSelectOriginalPaymentSourceOption();
	}

	private void enableAndSelectOriginalPaymentSourceOption() {
		originalPaymentRadioButton.setSelection(true);
		originalPaymentRadioButton.setEnabled(true);
		originalPaymentSourceCombo.setEnabled(true);
		originalPaymentSourceComboLabel.setEnabled(true);
	}

	private void setChildEnabled(final boolean enabled) {

		refundOptionsPageComposite.getSwtComposite().setEnabled(enabled);
		originalPaymentRadioButton.setEnabled(enabled && !originalPayments.isEmpty());
		originalPaymentSourceCombo.setEnabled(enabled);
		originalPaymentSourceComboLabel.setEnabled(enabled);
	}

	/**
	 * Set original payment radio button caption.
	 *
	 * @param label radio button caption.
	 */
	public void setOriginalPaymentSourceRadioButtonLabel(final String label) {
		originalPaymentRadioButton.setText(label);
	}
}
