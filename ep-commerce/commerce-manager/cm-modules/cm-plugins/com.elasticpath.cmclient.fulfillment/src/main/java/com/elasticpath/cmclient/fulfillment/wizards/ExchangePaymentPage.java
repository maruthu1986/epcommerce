/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.ui.framework.AbstractCmClientFormSectionPart;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.wizards.ExchangeWizard.ExchangeWizardType;
import com.elasticpath.cmclient.fulfillment.wizards.OrderPaymentControl.OrderPaymentControlMode;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.service.order.ReturnExchangeType;

/**
 * Payment exchange wizard page.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.GodClass"})
public class ExchangePaymentPage extends AbstractEPWizardPage<OrderReturn> {

	private static final Logger LOG = Logger.getLogger(ExchangePaymentPage.class);

	private IEpLayoutComposite amountComposite;

	private IEpLayoutComposite refundComposite;

	private ExchangeSummarySectionPart exchangeSummarySectionPart;

	private AdditionalAutorizationOptionsSectionPart additionalAutorizationOptionsSectionPart;

	private RefundOptionsSectionPart refundOptionsSectionPart;

	private RefundOptionsComposite refundOptionsComposite;

	private OrderPaymentControl orderPaymentControl;

	private Label physicalReturnLabel;

	private Button physicalReturnCheckbox;

	private BigDecimal exchangeTotal;

	private BigDecimal exchangeOrderTotal;

	private BigDecimal refundTotal;
	
	private boolean ccStored;


	/**
	 * The constructor.
	 * 
	 * @param pageName the page name
	 * @param message the message for this page
	 */
	protected ExchangePaymentPage(final String pageName, final String message) {
		super(1, false, pageName, new DataBindingContext());
		LOG.debug("summary constructor"); //$NON-NLS-1$				
		setMessage(message);
		EpWizardPageSupport.create(this, getDataBindingContext());
	}

	@Override
	protected void bindControls() {
		// nothing to bind here
	}

	@Override
	protected void populateControls() {
		// nothing to populate here
		if (isCompleteExchange()) {
			beforeFromPrev(null);
		}
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite parent) {
		IEpLayoutComposite composite = parent.addTableWrapLayoutComposite(1, true, parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL,
				true, true));
		TableWrapLayout tableWrapLayout = new TableWrapLayout();
		TableWrapData tableWrapData = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB);
		final IManagedForm managedForm = EpControlFactory.getInstance().createManagedForm(composite.getSwtComposite());
		managedForm.getForm().getBody().setLayout(tableWrapLayout);
		managedForm.getForm().setLayoutData(tableWrapData);

		exchangeSummarySectionPart = new ExchangeSummarySectionPart(managedForm.getForm().getBody(), managedForm.getToolkit(),
				getDataBindingContext());
		managedForm.addPart(exchangeSummarySectionPart);
		refundOptionsSectionPart = new RefundOptionsSectionPart(managedForm.getForm().getBody(), managedForm.getToolkit(), getDataBindingContext());
		managedForm.addPart(refundOptionsSectionPart);
		additionalAutorizationOptionsSectionPart = new AdditionalAutorizationOptionsSectionPart(managedForm.getForm().getBody(), managedForm
				.getToolkit(), getDataBindingContext());
		managedForm.addPart(additionalAutorizationOptionsSectionPart);

		this.setControl(parent.getSwtComposite());
		getModel().recalculateOrderReturn();
		ccStored = getModel().getOrder().getStore().isStoreFullCreditCardsEnabled();

	}

	private static void setEnabled(final Control control, final boolean enabled) {
		control.setEnabled(enabled);
		if (control instanceof Composite) {
			for (Control child : ((Composite) control).getChildren()) {
				setEnabled(child, enabled);
			}
		}
	}
	
	/**
	 * Depending on several factors, components on this page have to be enabled and disabled.
	 */
	private void toggleComponents() {
		boolean physicalReturnRequired = physicalReturnCheckbox.getSelection();
		
		if (isRefund()) {
			refundOptionsSectionPart.getSection().setExpanded(true);
			refundOptionsComposite.setEnabled(!physicalReturnRequired);
			
		} else if (isPayment()) {
			refundOptionsSectionPart.getSection().setExpanded(false);

			if (physicalReturnRequired) {
				// customer must return items first before exchange is authorized and paid for
				orderPaymentControl.changeMode(OrderPaymentControlMode.ALL_DISABLED);
			} else {
				orderPaymentControl.changeMode(OrderPaymentControlMode.ALL_ENABLED);

				// if CC's are not stored, force the request for a new payment method
				if (!ccStored) {
					orderPaymentControl.changeMode(OrderPaymentControlMode.ENABLE_NEW_ACCOUNT_ONLY);
				}
			}
			
		}
	}

	/**
	 * Exchange Summary Section.
	 */
	class ExchangeSummarySectionPart extends AbstractCmClientFormSectionPart {

		private static final int NUMBER_OF_COLUMNS = 3;

		private static final String EMPTY_TEXT = ""; //$NON-NLS-1$

		private Label exchangeTotalLabel;

		private Label exchangeOrderTotalLabel;

		private Label refundLabel;

		private Label extraChargeLabel;

		private IEpLayoutComposite controlPane;

		/**
		 * Constructor.
		 * 
		 * @param parent the parent form
		 * @param toolkit the form toolkit
		 * @param dataBindingContext the form databinding context
		 */
		ExchangeSummarySectionPart(final Composite parent, final FormToolkit toolkit, final DataBindingContext dataBindingContext) {
			super(parent, toolkit, dataBindingContext, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
			LOG.debug("SP summary constructor constructor"); //$NON-NLS-1$
		}

		@Override
		protected void createControls(final Composite parent, final FormToolkit toolkit) {
			controlPane = CompositeFactory.createTableWrapLayoutComposite(parent, NUMBER_OF_COLUMNS, false);
			final IEpLayoutData fieldData = controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, true, true);

			final IEpLayoutData horizontalFill2Cells = controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, true, true, 2, 1);
			final IEpLayoutData horizontalFill3Cells = controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, true, true, 3, 1);

			controlPane.addLabelBold(FulfillmentMessages.get().ExchangeWizard_TotalPriceOfItemsToBeExchanged_Label, fieldData);
			controlPane.addLabel(getModel().getCurrency().getCurrencyCode(), fieldData);
			exchangeTotalLabel = controlPane.addLabel(EMPTY_TEXT, fieldData);

			controlPane.addLabelBold(FulfillmentMessages.get().ExchangeWizard_TotalPriceOfItemsToBeOrdered_Label, fieldData);
			controlPane.addLabel(getModel().getCurrency().getCurrencyCode(), fieldData);
			exchangeOrderTotalLabel = controlPane.addLabel(EMPTY_TEXT, fieldData);

			refundComposite = controlPane.addTableWrapLayoutComposite(NUMBER_OF_COLUMNS, false, horizontalFill3Cells);
			refundComposite.addLabelBold(FulfillmentMessages.get().ExchangeWizard_RefundAmount_Label, fieldData);
			refundComposite.addLabel(getModel().getCurrency().getCurrencyCode(), fieldData);

			refundLabel = refundComposite.addLabel(EMPTY_TEXT, fieldData);

			amountComposite = controlPane.addTableWrapLayoutComposite(NUMBER_OF_COLUMNS, false, horizontalFill3Cells);
			amountComposite.addLabelBold(FulfillmentMessages.get().ExchangeWizard_AdditionalAuthorizationAmmount_Label, fieldData);
			amountComposite.addLabel(getModel().getCurrency().getCurrencyCode(), fieldData);
			extraChargeLabel = amountComposite.addLabel(EMPTY_TEXT, fieldData);

			physicalReturnLabel = controlPane.addLabelBold(FulfillmentMessages.
					get().ExchangeWizard_PhysicalReturnRequiredBeforeRefund_Label, fieldData);
			physicalReturnCheckbox = controlPane.addCheckBoxButton(EMPTY_TEXT, EpState.EDITABLE, horizontalFill2Cells);
			
			getSection().setExpanded(true);
		}

		@Override
		protected void populateControls() {
			physicalReturnCheckbox.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent event) {
					toggleComponents();
				}
			});
			physicalReturnCheckbox.setSelection(true);
		}
		
		@Override
		protected String getSectionTitle() {
			return FulfillmentMessages.get().ExchangeWizard_ExchangeSummary_Section;
		}

		@Override
		protected void bindControls(final DataBindingContext bindingContext) {
			// nothing to bound here.
		}

		/**
		 * Should be called in this.beforeShow() method.
		 */
		public void repopulate() {
			exchangeTotalLabel.setText(exchangeTotal.toString());
			exchangeOrderTotalLabel.setText(exchangeOrderTotal.toString());
			refundLabel.setText(BigDecimal.ZERO.toString());
			extraChargeLabel.setText(BigDecimal.ZERO.toString());
			if (isRefund()) {
				refundLabel.setText(refundTotal.toString());
			} else if (isPayment()) {
				extraChargeLabel.setText(refundTotal.abs().toString());
			} 
			controlPane.getSwtComposite().pack(true);
			refundComposite.getSwtComposite().pack(true);
			amountComposite.getSwtComposite().pack(true);
		}
	}

	/**
	 * Refund Options Section.
	 */
	class RefundOptionsSectionPart extends AbstractCmClientFormSectionPart {

		/**
		 * Constructor.
		 * 
		 * @param parent the parent form
		 * @param toolkit the form toolkit
		 * @param dataBindingContext the form databinding context
		 */
		RefundOptionsSectionPart(final Composite parent, final FormToolkit toolkit, final DataBindingContext dataBindingContext) {
			super(parent, toolkit, dataBindingContext, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
			LOG.debug("SP refund constructor"); //$NON-NLS-1$
		}

		@Override
		protected void createControls(final Composite parent, final FormToolkit toolkit) {
			final IEpLayoutComposite controlPane = CompositeFactory.createGridLayoutComposite(parent, 2, false);
			final IEpLayoutData tableLayoutData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 1, 3);
			refundOptionsComposite = RefundOptionsComposite.createRefundOptionsComposite(getModel(), controlPane,
					tableLayoutData);
		}

		@Override
		protected void populateControls() {
			refundOptionsComposite.setEnabled(false);
		}

		@Override
		protected String getSectionTitle() {
			return FulfillmentMessages.get().ExchangeWizard_RefundOptions_Section;
		}

		@Override
		protected void bindControls(final DataBindingContext bindingContext) {
			// nothing to bound here.
		}

	}

	/**
	 * Additional Authorization Section.
	 */
	class AdditionalAutorizationOptionsSectionPart extends AbstractCmClientFormSectionPart {

		/**
		 * Constructor.
		 * 
		 * @param parent the parent form
		 * @param toolkit the form toolkit
		 * @param dataBindingContext the form databinding context
		 */
		AdditionalAutorizationOptionsSectionPart(final Composite parent, final FormToolkit toolkit,
				final DataBindingContext dataBindingContext) {
			super(parent, toolkit, dataBindingContext, ExpandableComposite.TITLE_BAR);
			LOG.debug("SP additional constructor"); //$NON-NLS-1$
		}

		@Override
		protected void createControls(final Composite parent, final FormToolkit toolkit) {
			final IEpLayoutComposite controlPane = CompositeFactory.createTableWrapLayoutComposite(parent, 1, false);
			final IEpLayoutData tableLayoutData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 1, 1);
			orderPaymentControl = new OrderPaymentControl(getModel().getOrder(), controlPane, tableLayoutData,
					ExchangePaymentPage.this.getDataBindingContext());
		}

		/**
		 * Enables composite and controls if the argument is <code>true</code>, and disables it otherwise.
		 * 
		 * @param enabled if control should be enabled.
		 */
		public void setEnabled(final boolean enabled) {
			getSection().setEnabled(enabled);
			orderPaymentControl.changeMode(OrderPaymentControlMode.ALL_ENABLED);
		}

		@Override
		protected void populateControls() {
			orderPaymentControl.setOriginalPaymentSourceRadioButtonLabel(FulfillmentMessages.get().ExchangeWizard_OriginalPaymentSource);
			orderPaymentControl.changeMode(OrderPaymentControlMode.ALL_DISABLED);
		}

		@Override
		protected String getSectionTitle() {
			return FulfillmentMessages.get().ExchangeWizard_AdditionalAuthorizationOptions_Section;
		}

		@Override
		protected void bindControls(final DataBindingContext bindingContext) {
			// nothing to bound here.
		}

	}

	@Override
	public boolean beforeFromPrev(final PageChangingEvent event) {
		LOG.debug("beforeShow method called."); //$NON-NLS-1$
		exchangeTotal = getModel().getReturnTotal();
		if (isCompleteExchange()) {
			exchangeOrderTotal = getModel().getExchangeOrder().getTotal();
		} else {
			exchangeOrderTotal = getModel().getExchangePricingSnapshot().getTotal();
		}
		refundTotal = getModel().getRefundTotal();
		
		if (isRefund()) { // => refund section enabled and payment section disabled.
			setEnabled(refundComposite.getSwtComposite(), true);
			refundOptionsSectionPart.getSection().setEnabled(true);

			setEnabled(amountComposite.getSwtComposite(), false);
			additionalAutorizationOptionsSectionPart.getSection().setEnabled(!ccStored);
			//orderPaymentControl.setEnabled(!ccStored);
			toggleComponents();
			
		} else if (isPayment()) { // => refund section disabled and payment section enabled.
			setEnabled(amountComposite.getSwtComposite(), true);
			additionalAutorizationOptionsSectionPart.getSection().setEnabled(true);

			setEnabled(refundComposite.getSwtComposite(), false);
			refundOptionsSectionPart.getSection().setEnabled(false);
			refundOptionsSectionPart.getSection().setExpanded(false);
			toggleComponents();
			
		} else { // => refund section disabled and payment section disabled.
			setEnabled(amountComposite.getSwtComposite(), false);
			setEnabled(refundComposite.getSwtComposite(), false);
			refundOptionsSectionPart.getSection().setEnabled(false);

			additionalAutorizationOptionsSectionPart.getSection().setEnabled(!ccStored);
			toggleComponents();
		}
		
		exchangeSummarySectionPart.repopulate();
		refundOptionsComposite.updateRefundToOriginalPaymentSourceRadioButtonText();
		if (isCompleteExchange()) {
			physicalReturnCheckbox.setVisible(false);
			physicalReturnCheckbox.setEnabled(false);
			physicalReturnCheckbox.setSelection(false);
			physicalReturnLabel.setVisible(false);
			
			toggleComponents();
		}
		return super.beforeFromPrev(event);
	}

	@Override
	public boolean beforeNext(final PageChangingEvent event) {
		boolean terminated = false;
		if (getWizard().getExchangeWizardType() == ExchangeWizardType.COMPLETE_EXCHANGE) {
			terminated = getWizard().completeExchnage();
		} else if (getWizard().getExchangeWizardType() == ExchangeWizardType.CREATE_EXCHANGE) {
			terminated = getWizard().createExchange();
		}

		return terminated;
	}

	@Override
	public ExchangeWizard getWizard() {
		return (ExchangeWizard) super.getWizard();
	}

	/**
	 * Returns what option was selected.
	 * 
	 * @return selected option
	 */
	public ReturnExchangeType getSelectionResult() {
		final ReturnExchangeType res;
		if (isPhysicalReturn()) {
			res = ReturnExchangeType.PHYSICAL_RETURN_REQUIRED;
		} else if (!isPhysicalReturn() && isRefund()) {
			if (refundOptionsComposite.useOriginalPayment()) {
				res = ReturnExchangeType.REFUND_TO_ORIGINAL;
			} else {
				res = ReturnExchangeType.MANUAL_RETURN;
			}
		} else if (!isPhysicalReturn() && isPayment()) {
			if (orderPaymentControl.useOriginalPayment()) {
				res = ReturnExchangeType.ORIGINAL_PAYMENT;
			} else {
				res = ReturnExchangeType.NEW_PAYMENT;
			}
		} else {
			res = ReturnExchangeType.CREATE_WO_PAYMENT;
		}
		
		LOG.debug("refund control results: " + res); //$NON-NLS-1$
		return res;
	}

	private boolean isPhysicalReturn() {
		return physicalReturnCheckbox.getSelection();
	}

	private boolean isRefund() {
		return refundTotal.compareTo(BigDecimal.ZERO) > 0;
	}

	private boolean isPayment() {
		return refundTotal.compareTo(BigDecimal.ZERO) < 0;
	}

	/**
	 * @return the payment
	 */
	public OrderPayment getPayment() {
		return orderPaymentControl.getPayment();
	}

	private boolean isCompleteExchange() {
		LOG.debug(getWizard().getExchangeWizardType());
		return getWizard().getExchangeWizardType() == ExchangeWizard.ExchangeWizardType.COMPLETE_EXCHANGE;
	}
}
