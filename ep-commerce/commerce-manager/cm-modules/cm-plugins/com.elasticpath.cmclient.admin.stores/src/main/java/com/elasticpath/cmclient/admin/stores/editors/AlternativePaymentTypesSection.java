/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.stores.editors;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.custom.CCombo;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.plugin.payment.PaymentGatewayType;

/**
 * Represents UI alternative payment types section.
 */
public class AlternativePaymentTypesSection extends AbstractPaymentSection {

	private CCombo paypalExpressCombo;

	/**
	 * Constructs alternative payment types section.
	 * 
	 * @param editor the editor
	 * @param editable whether the section should be editable
	 */
	public AlternativePaymentTypesSection(final AbstractCmClientFormEditor editor, final boolean editable) {
		super(editor, editable);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final boolean hideDecorationOnFirstValidation = true;
		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		final ObservableUpdateValueStrategy paypalUpdateStrategy = createUpdateValueGatewayStrategy(paypalExpressCombo);
		binder.bind(bindingContext, paypalExpressCombo, null, null, paypalUpdateStrategy, hideDecorationOnFirstValidation);
	}

	@Override
	protected void createSectionControls(final IEpLayoutComposite controlPane) {
		IEpLayoutData labelData = controlPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		IEpLayoutData fieldData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, true);

		controlPane.addLabelBoldRequired(AdminStoresMessages.get().PaypalExpress, EpState.EDITABLE, labelData);
		paypalExpressCombo = controlPane.addComboBox(EpState.EDITABLE, fieldData);
	}

	@Override
	protected void populateControls() {
		populatePaymentGatewayCombo(paypalExpressCombo);
	}

	@Override
	protected String getSectionLocalizedMessage() {
		return AdminStoresMessages.get().AlternativeTypes_Section;
	}

	@Override
	Collection<PaymentGatewayType> getApplicablePaymentGatewayTypes() {
		return Arrays.asList(PaymentGatewayType.PAYPAL_EXPRESS, PaymentGatewayType.HOSTED_PAGE);
	}
}
