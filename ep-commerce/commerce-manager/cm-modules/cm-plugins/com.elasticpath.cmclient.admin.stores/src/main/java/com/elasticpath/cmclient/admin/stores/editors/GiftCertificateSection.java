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
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.plugin.payment.PaymentGatewayType;

/**
 * Represent UI gift certificate section.
 */
public class GiftCertificateSection extends AbstractPaymentSection {

	private CCombo giftCertificatesCombo;

	/**
	 * Constructs gift certificate section.
	 * 
	 * @param editor the editor
	 * @param editable whether the section should be editable
	 */
	public GiftCertificateSection(final AbstractCmClientFormEditor editor, final boolean editable) {
		super(editor, editable);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();
		final boolean hideDecorationOnFirstValidation = true;

		binder.bind(bindingContext, giftCertificatesCombo, null, null,
				createUpdateValueGatewayStrategy(giftCertificatesCombo),
				hideDecorationOnFirstValidation);
	}

	@Override
	protected void createSectionControls(final IEpLayoutComposite controlPane) {
		controlPane.addLabelBoldRequired(AdminStoresMessages.get().GiftCertificates, EpState.EDITABLE,
				controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER));
		
		giftCertificatesCombo = controlPane.addComboBox(EpState.EDITABLE, 
				controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false));
	}

	@Override
	protected void populateControls() {
		populatePaymentGatewayCombo(giftCertificatesCombo);
	}

	@Override
	protected String getSectionLocalizedMessage() {
		return AdminStoresMessages.get().GiftCertificates_Section;
	}

	@Override
	Collection<PaymentGatewayType> getApplicablePaymentGatewayTypes() {
		return Arrays.asList(PaymentGatewayType.GIFT_CERTIFICATE);
	}
}
