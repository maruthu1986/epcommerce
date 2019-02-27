/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.AbstractCmClientFormSectionPart;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.domain.impl.OrderPaymentPresenterFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.PaymentsComparatorFactory;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.money.Money;
import com.elasticpath.plugin.payment.PaymentGatewayType;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * First page of reauthorization wizard. User can select payment sources for the shipments.
 */
public class ReAuthInitPage extends AbstractEPWizardPage<Order> {

	private final List<OrderPayment> orderPayments = new ArrayList<>();

	private final List<CCombo> paymentCombos = new ArrayList<>();

	private PaymentSourceComboListener paymentSourceComboListener;

	private final Order order;

	/**
	 * The constructor.
	 *
	 * @param pageName name of this page. Will be passed to super.
	 * @param dataBindingContext binding context.
	 * @param order the order
	 */
	protected ReAuthInitPage(final String pageName, final DataBindingContext dataBindingContext, final Order order) {
		super(1, true, pageName, dataBindingContext);
		setMessage(FulfillmentMessages.get().CaptureWizard_CardInfoPage_Message);

		this.order = order;
	}

	@Override
	protected void bindControls() {
		// nothing
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {

		orderPayments.addAll(getReusablePayments());

		setControl(pageComposite.getSwtComposite());

		final IManagedForm managedForm = EpControlFactory.getInstance().createManagedForm(pageComposite.getSwtComposite());
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;
		final int margin = 10;
		layout.leftMargin = margin;
		layout.rightMargin = margin;

		final ScrolledForm scrolledForm = managedForm.getForm();
		scrolledForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		scrolledForm.getBody().setLayout(layout);

		paymentSourceComboListener = new PaymentSourceComboListener();
		for (final ReAuthorizationItem reAuthorizationItem : getWizard().getReAuthorizationList()) {
			managedForm.addPart(new ShipmentSection(scrolledForm.getBody(), managedForm.getToolkit(), getDataBindingContext(), reAuthorizationItem));
		}

		final IEpLayoutComposite notePage = pageComposite.addGridLayoutComposite(2, false, pageComposite.createLayoutData());
		notePage.addLabelBoldRequired(FulfillmentMessages.get().RefundWizard_Note, EpState.EDITABLE, notePage.createLayoutData());
		notePage.addLabel(FulfillmentMessages.get().CaptureWizard_Note_Text, notePage.createLayoutData());
	}

	/**
	 * Filters out all payments that are not applicable to be used for re-authorization.
	 * By default, it returns the list of payments with payment token as a method.
	 *
	 * Extension classes may override this method and provide a different collection of
	 * payments and methods.
	 *
	 * @return a collection of the filtered order payments
	 */
	protected Collection<OrderPayment> getReusablePayments() {
		return PaymentsComparatorFactory.getListOfUniquePayments(null, this.order.getOrderPayments(), new PaymentType[]{});
	}

	@SuppressWarnings("PMD.CompareObjectsWithEquals")
	private void refreshPaymentCombos() {

		List<ReAuthorizationItem> reAuthorizationItems = getWizard().getReAuthorizationList();

		if (!reAuthorizationItems.isEmpty()) {

			OrderPaymentPresenterFactory presenterFactory = new OrderPaymentPresenterFactory();

			for (int shipmentIndex = 0; shipmentIndex < reAuthorizationItems.size(); ++shipmentIndex) {
				final CCombo paymentCombo = paymentCombos.get(shipmentIndex);


				paymentCombo.removeAll();
				paymentCombo.add(FulfillmentMessages.get().ReAuthWizard_SelectPaymentSource);
				paymentCombo.select(0);

				final OrderPayment selectedPayment = getSelectedPayment(shipmentIndex);
				int paymentIndex = 1;

				for (final OrderPayment currOrderPayment : orderPayments) {
					paymentCombo.add(presenterFactory.getOrderPaymentPresenter(currOrderPayment).getDisplayPaymentDetails());
					if (isCurrentOrderPaymentSameAsSelected(currOrderPayment, selectedPayment)) {
						paymentCombo.select(paymentIndex);
						break;
					}
					++paymentIndex;
				}
			}
		}
	}

	private boolean isCurrentOrderPaymentSameAsSelected(final OrderPayment currOrderPayment, final OrderPayment selectedPayment) {
		return selectedPayment != null && currOrderPayment.getDisplayValue().equals(selectedPayment.getDisplayValue());
	}

	private OrderPayment getSelectedPayment(final int shipmentIndex) {
		final CCombo paymentCombo = paymentCombos.get(shipmentIndex);
		OrderPayment selectedPayment;
		int selectedPaymentIndex = paymentCombo.getSelectionIndex();
		if (selectedPaymentIndex < 0) {
			selectedPayment = getWizard().getReAuthorizationList().get(shipmentIndex).getOldPayment();
		} else {
			--selectedPaymentIndex;

			if (selectedPaymentIndex >= 0) {
				selectedPayment = orderPayments.get(selectedPaymentIndex);
			} else {
				selectedPayment = null;
			}
		}
		return selectedPayment;
	}

	@Override
	protected void populateControls() {
		refreshPaymentCombos();
	}

	@Override
	public ReAuthWizard getWizard() {
		return (ReAuthWizard) super.getWizard();
	}

	@Override
	public boolean beforeNext(final PageChangingEvent event) {
		final Map<PaymentGatewayType, PaymentGateway> gatewayMap = getPaymentGatewayMap();

		for (int shipmentIndex = 0; shipmentIndex < getWizard().getReAuthorizationList().size(); ++shipmentIndex) {
			final OrderPayment selectedOrderPayment = getSelectedPayment(shipmentIndex);

			final PaymentGateway paymentGateway = gatewayMap.get(selectedOrderPayment.getPaymentMethod().getPaymentGatewayType());

			if (paymentGateway == null) {
				continue;
			}

			final ReAuthorizationItem reAuthorizationItem = getWizard().getReAuthorizationList().get(shipmentIndex);
			reAuthorizationItem.setNewPayment(ServiceLocator.getService(ContextIdNames.ORDER_PAYMENT));

			if (paymentGateway.getPaymentGatewayType() == PaymentGatewayType.GIFT_CERTIFICATE) {
				reAuthorizationItem.getNewPayment().setGiftCertificate(selectedOrderPayment.getGiftCertificate());
			}
			reAuthorizationItem.getNewPayment().copyTransactionFollowOnInfo(selectedOrderPayment);
		}

		return getWizard().process();
	}

	@Override
	public boolean canFlipToNextPage() {
		for (final CCombo paymentCombo : paymentCombos) {
			if (paymentCombo.getSelectionIndex() == 0) {
				return false;
			}
		}
		return super.canFlipToNextPage();
	}

	protected Map<PaymentGatewayType, PaymentGateway> getPaymentGatewayMap() {
		return getModel().getStore().getPaymentGatewayMap();
	}

	/**
	 * Selection listener for payment source drop downs. Used for enabling/disabling of the Next button.
	 */
	class PaymentSourceComboListener extends SelectionAdapter {

		@Override
		public void widgetSelected(final SelectionEvent event) {
			getWizard().getContainer().updateButtons();
		}

	}

	/**
	 * This section contains information about shipment, reauth amount, provides controls for payment source selection.
	 */
	class ShipmentSection extends AbstractCmClientFormSectionPart {

		private final ReAuthorizationItem reAuthorizationItem;

		/**
		 * The constructor.
		 *
		 * @param parent parent composite.
		 * @param toolkit will be passed to super.
		 * @param dataBindingContext data binding context. Unused.
		 * @param reAuthorizationItem shipment reuthorization information.
		 */
		ShipmentSection(final Composite parent, final FormToolkit toolkit, final DataBindingContext dataBindingContext,
				final ReAuthorizationItem reAuthorizationItem) {
			super(parent, toolkit, dataBindingContext, ExpandableComposite.TITLE_BAR);
			this.reAuthorizationItem = reAuthorizationItem;
		}

		@Override
		protected void bindControls(final DataBindingContext bindingContext) {
			// nothing

		}

		@Override
		protected void createControls(final Composite client, final FormToolkit toolkit) {
			final IEpLayoutComposite page = CompositeFactory.createTableWrapLayoutComposite(client, 3, false);

			page.addLabelBoldRequired(FulfillmentMessages.get().ReAuthWizard_AuthAmount, EpState.EDITABLE, page.createLayoutData());
			page.addLabel(FulfillmentMessages.get().formatMoneyAsString(
					Money.valueOf(reAuthorizationItem.getShipment().getTotal(), getModel().getCurrency()), getModel().getLocale()),
					page.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, false, false, 2, 1));

			page.addLabelBoldRequired(FulfillmentMessages.get().ReAuthWizard_PaymentSource, EpState.EDITABLE, page.createLayoutData(IEpLayoutData.END,
					IEpLayoutData.BEGINNING));

			final CCombo paymentsCombo = page.addComboBox(EpState.EDITABLE, page.createLayoutData(IEpLayoutData.END, IEpLayoutData.BEGINNING));
			paymentsCombo.addSelectionListener(paymentSourceComboListener);
			paymentsCombo.select(-1);
			paymentCombos.add(paymentsCombo);
		}

		@Override
		protected void populateControls() {
			getSection().setText(

					NLS.bind(FulfillmentMessages.get().ReAuthWizard_ShipmentNumber,
					reAuthorizationItem.getShipment().getShipmentNumber()));
		}
	}
}