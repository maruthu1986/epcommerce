/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.editors;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.store.CreditCardType;
import com.elasticpath.plugin.payment.PaymentGatewayType;

/**
 * Represents UI paymentGateway section.
 */
public class PaymentGatewaySection extends AbstractPaymentSection {

	private CCombo paymentGatewayCombo;

	private Map<String, CreditCardType> supportedCardTypesMap;

	private Label uninstalledPaymentGatewayPluginWarning;
	
	/**
	 * Constructs payment gateway section.
	 * 
	 * @param editor the editor
	 * @param editable whether the section should be editable
	 */
	public PaymentGatewaySection(final AbstractCmClientFormEditor editor, final boolean editable) {
		super(editor, editable);
		initializeCardTypesMap(getPaymentGatewayService().getSupportedCreditCardTypes());
	}

	private void initializeCardTypesMap(final Set<String> supportedCreditCardTypes) {
		supportedCardTypesMap = new HashMap<>(supportedCreditCardTypes.size());
		for (String cardType : supportedCreditCardTypes) {
			final CreditCardType creditCardType = ServiceLocator.getService(ContextIdNames.CREDIT_CARD_TYPE);
			creditCardType.setCreditCardType(cardType);
			supportedCardTypesMap.put(cardType, creditCardType);
		}
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final boolean hideDecorationOnFirstValidation = true;
		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		final ObservableUpdateValueStrategy creditCardUpdateStrategy = new PaymentUpdateValueStrategy(paymentGatewayCombo) {
			@Override
			protected void additionalUpdateAction(final PaymentGateway paymentGateway) {
				final Collection<CreditCardType> cardTypesCollection = supportedCardTypesMap.values();
				final Set<CreditCardType> selectedElements = new HashSet<>();
				selectedElements.addAll(cardTypesCollection);
				getStoreEditorModel().setCreditCardTypes(selectedElements);
			}
		};
		binder.bind(bindingContext, paymentGatewayCombo, null, null, creditCardUpdateStrategy, hideDecorationOnFirstValidation);
	}

	@Override
	protected void createSectionControls(final IEpLayoutComposite controlPane) {
		controlPane.addLabelBoldRequired(AdminStoresMessages.get().PrimaryPaymentGateway, EpState.EDITABLE,
				controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER));
		
		paymentGatewayCombo = controlPane.addComboBox(EpState.EDITABLE,
				controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));

		uninstalledPaymentGatewayPluginWarning = controlPane.addLabel(AdminStoresMessages.get().UninstalledCreditCardPaymentGatewayPluginWarning,
				controlPane.createLayoutData(IEpLayoutData.CENTER, IEpLayoutData.FILL, true, true, 2, 1));
		
		if (!uninstalledPaymentGatewayPluginWarning.isDisposed()) {
			uninstalledPaymentGatewayPluginWarning.setForeground(controlPane.getSwtComposite().getDisplay().getSystemColor(SWT.COLOR_RED));
		}
		
		paymentGatewayCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				configureControlsBasedOnGatewayComboState();
				}
		});
	}

	@Override
	Collection<PaymentGatewayType> getApplicablePaymentGatewayTypes() {
		return Collections.singletonList(PaymentGatewayType.CREDITCARD);
	}

	@Override
	protected void populateControls() {
		final StoreEditorModel storeEditorModel = getStoreEditorModel();
		populatePaymentGatewayCombo(paymentGatewayCombo);

		if (storeEditorModel.isPersistent()) {
			final Set<CreditCardType> creditCardsList = storeEditorModel.getCreditCardTypes();

			if (creditCardsList.isEmpty()) {
				paymentGatewayCombo.notifyListeners(SWT.Selection, null);
			}
		} else {
			paymentGatewayCombo.notifyListeners(SWT.Selection, null);
		}
	
		configureControlsBasedOnGatewayComboState();
	}

	@Override
	protected String getSectionLocalizedMessage() {
		return AdminStoresMessages.get().PrimarySection;
	}

	private void configureControlsBasedOnGatewayComboState() {
		final PaymentGateway paymentGateway = (PaymentGateway) paymentGatewayCombo.getData(paymentGatewayCombo.getText());
		makeWarningVisibleOnUninstalledPaymentGatewayPlugin(isSelectedGatewayPluginInstalled(paymentGateway));
	}

	private boolean isSelectedGatewayPluginInstalled(final PaymentGateway paymentGateway) {
		return paymentGateway != null && paymentGateway.isPaymentGatewayPluginInstalled();
	}

	private void makeWarningVisibleOnUninstalledPaymentGatewayPlugin(final boolean selectedGatewayPluginInstalled) {
		if ((AdminStoresMessages.get().NotInUse.equals(paymentGatewayCombo.getText()))) {
			uninstalledPaymentGatewayPluginWarning.setVisible(false);
		} else { 
			uninstalledPaymentGatewayPluginWarning.setVisible(!selectedGatewayPluginInstalled);
		}
	}
}
