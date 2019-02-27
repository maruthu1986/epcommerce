/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.conversion.EpStringToBigDecimalConverter;
import com.elasticpath.cmclient.core.ui.framework.AbstractCmClientFormSectionPart;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;

/**
 * Page for payment source information.
 */
public class RefundCardInfoPage extends AbstractEPWizardPage<Order> {

	private static final int TEXT_AREA_HEIGHT = 50;

	/**
	 * Validating the string is not blank.
	 */
	private class NonBlankValidator implements IValidator {
		@Override
		public IStatus validate(final Object toValidate) {
			if (StringUtils.isBlank((String) toValidate)) {
				return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR, CoreMessages.get().EpValidatorFactory_BigDecimal, null);
			}

			return Status.OK_STATUS;
		}

	}
	
	private Text amountText;
	
	private Text textArea;

	private BigDecimal amount;
	
	private String refundNote;

	private OrderPaymentControl paymentControl;

	/**
	 * The constructor.
	 * 
	 * @param pageName the page name.
	 */
	protected RefundCardInfoPage(final String pageName) {
		super(1, true, pageName, new DataBindingContext());
		setMessage(FulfillmentMessages.get().RefundWizard_CardInfoPage_Message);
	}

	@Override
	protected void bindControls() {
		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();
		binder.bind(getDataBindingContext(),
					amountText,
					this,
					"amount", //$NON-NLS-1$
					new CompoundValidator(EpValidatorFactory.NON_NEGATIVE_NON_ZERO_BIG_DECIMAL, new NonBlankValidator()), 
					new EpStringToBigDecimalConverter(), true); 
		binder.bind(getDataBindingContext(), textArea, this, "refundNote", EpValidatorFactory.STRING_255_REQUIRED, null, true); //$NON-NLS-1$

		EpWizardPageSupport.create(this, getDataBindingContext());
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {
		setControl(pageComposite.getSwtComposite());

		final int columnNumber = 3;
		final IEpLayoutComposite refundAmountComposite = pageComposite.addGridLayoutComposite(columnNumber, false, pageComposite
				.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, true));

		IEpLayoutData labelData = refundAmountComposite
				.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER);
		refundAmountComposite.addLabelBoldRequired(FulfillmentMessages.get().RefundWizard_RefundAmount, EpState.EDITABLE, labelData);
		refundAmountComposite.addLabel(getModel().getCurrency().getCurrencyCode(), labelData);
		amountText = refundAmountComposite.addTextField(EpState.EDITABLE, refundAmountComposite.createLayoutData(IEpLayoutData.FILL,
				IEpLayoutData.CENTER, true, true));
		
		refundAmountComposite.addLabelBoldRequired(FulfillmentMessages.get().RefundWizard_RefundNote, EpState.EDITABLE, refundAmountComposite
				.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER, false, true, 2, 1));
		this.textArea = refundAmountComposite.addTextArea(true, false, EpState.EDITABLE, refundAmountComposite.createLayoutData(IEpLayoutData.FILL,
				IEpLayoutData.CENTER, true, true));

		((GridData) this.textArea.getLayoutData()).heightHint = TEXT_AREA_HEIGHT;

		final IManagedForm managedForm = EpControlFactory.getInstance().createManagedForm(pageComposite.getSwtComposite());
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;
		final int margin = 10;
		layout.leftMargin = margin;
		layout.rightMargin = margin;

		final ScrolledForm scrolledForm = managedForm.getForm();
		scrolledForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		scrolledForm.getBody().setLayout(layout);

		managedForm.addPart(new RefundOptionsSection(scrolledForm.getBody(), managedForm.getToolkit(), getDataBindingContext()));

		final IEpLayoutComposite noteComposite = pageComposite.addTableWrapLayoutComposite(2, false, pageComposite.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));

		noteComposite.addLabelBoldRequired(FulfillmentMessages.get().RefundWizard_Note, EpState.READ_ONLY, noteComposite.createLayoutData(
				IEpLayoutData.BEGINNING, IEpLayoutData.CENTER));

		noteComposite.addLabel(FulfillmentMessages.get().RefundWizard_Note_Text,
				noteComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));

	}

	@Override
	protected void populateControls() {
		amountText.setText(BigDecimal.ZERO.toString());
	}

	@Override
	public RefundWizard getWizard() {
		return (RefundWizard) super.getWizard();
	}

	/**
	 * Getter for refund amount. It should be public for binding.
	 * 
	 * @return amount to refund.
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * Setter for refund amount. It should be public for binding.
	 * 
	 * @param amount value to set.
	 */
	public void setAmount(final BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * Getter for the refund note.
	 * 
	 * @return refund note.
	 */
	public String getRefundNote() {
		return refundNote;
	}
	
	/**
	 * Setter for the refund note.
	 * 
	 * @param refundNote refund note.
	 */
	public void setRefundNote(final String refundNote) {
		this.refundNote = refundNote;
	}
	
	@Override
	public boolean beforeNext(final PageChangingEvent event) {
		OrderPayment payment = paymentControl.getPayment();
		payment.setAmount(getAmount());
		return getWizard().process(payment, getRefundNote());
	}

	/**
	 * This section contains paymentControl.
	 */
	class RefundOptionsSection extends AbstractCmClientFormSectionPart {

		/**
		 * The constructor.
		 * 
		 * @param parent parent composite.
		 * @param toolkit will be passed to super.
		 * @param dataBindingContext data binding context.
		 */
		RefundOptionsSection(final Composite parent, final FormToolkit toolkit, final DataBindingContext dataBindingContext) {
			super(parent, toolkit, dataBindingContext, ExpandableComposite.TITLE_BAR);
		}

		@Override
		protected void bindControls(final DataBindingContext bindingContext) {
			// nothing
		}

		@Override
		protected void createControls(final Composite client, final FormToolkit toolkit) {
			IEpLayoutComposite sectionComposite = CompositeFactory.createTableWrapLayoutComposite(client, 1, false);
			paymentControl = new OrderPaymentControl(getModel(), sectionComposite, sectionComposite.createLayoutData(
					IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING), getDataBindingContext());

			paymentControl.setOriginalPaymentSourceRadioButtonLabel(FulfillmentMessages.get().RefundWizard_OriginalPaymentSource);

		}

		@Override
		protected void populateControls() {
			// nothing
		}

		@Override
		protected String getSectionTitle() {
			return FulfillmentMessages.get().RefundOptionsSection_Title;
		}

	}

}
