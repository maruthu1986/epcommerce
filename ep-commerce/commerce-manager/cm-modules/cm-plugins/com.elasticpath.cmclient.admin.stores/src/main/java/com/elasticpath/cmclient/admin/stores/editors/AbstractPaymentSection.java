/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.plugin.payment.PaymentGatewayType;
import com.elasticpath.service.payment.PaymentGatewayService;

/**
 * Abstract class for payment section.
 */
public abstract class AbstractPaymentSection {

	private final AbstractCmClientFormEditor editor;

	private final PaymentGatewayService paymentGatewayService;

	private final List<PaymentGateway> paymentGatewaysList;

	private IEpLayoutComposite controlPane;

	private final boolean editable;
	
	/**
	 * Constructs payment section.
	 * 
	 * @param editor the editor
	 * @param editable whether the section should be editable
	 */
	public AbstractPaymentSection(final AbstractCmClientFormEditor editor, final boolean editable) {
		this.editor = editor;
		this.editable = editable;
		paymentGatewayService = ServiceLocator.getService(ContextIdNames.PAYMENT_GATEWAY_SERVICE);
		paymentGatewaysList = paymentGatewayService.findAllPaymentGateways();
	}

	/**
	 * Initialized the credit card section.
	 * 
	 * @param client the client area
	 * @param toolkit the form toolkit
	 * @param bindingContext the data binding context
	 */
	public void initialize(final Composite client, final FormToolkit toolkit, final DataBindingContext bindingContext) {
		createControls(client, toolkit);
		bindControls(bindingContext);
		populateControls();
		controlPane.setControlModificationListener(editor);
	}

	/**
	 * Creates controls.
	 * 
	 * @param client the client area
	 * @param toolkit the form toolkit
	 */
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		final Section section = toolkit.createSection(client, ExpandableComposite.TITLE_BAR);
		section.setText(getSectionLocalizedMessage());
		section.setEnabled(isEditable());

		controlPane = CompositeFactory.createTableWrapLayoutComposite(section, 2, false);
		final TableWrapData data = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
		data.grabHorizontal = true;
		controlPane.setLayoutData(data);

		createSectionControls(controlPane);

		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL));
		section.setClient(controlPane.getSwtComposite());
	}

	/** @return true if the section should be editable */
	boolean isEditable() {
		return editable;
	}
	
	/**
	 * Creates controls for the section.
	 * 
	 * @param client the client composite
	 */
	protected abstract void createSectionControls(final IEpLayoutComposite client);

	/**
	 * Binds the controls for the contents.
	 * 
	 * @param bindingContext the binding context
	 */
	protected abstract void bindControls(final DataBindingContext bindingContext);

	/**
	 * Populates controls for the contents.
	 */
	protected abstract void populateControls();

	/**
	 * Gets section title.
	 * 
	 * @return the section title
	 */
	protected abstract String getSectionLocalizedMessage();
	
	/**
	 * Initialize payment method combobox.
	 *
	 * @param combo the combo
	 * @param paymentGateways the payment gateways
	 * @param selectedGateway the selected gateway
	 */
	protected void initializePaymentMethodCombobox(final CCombo combo,
			final List<PaymentGateway> paymentGateways,
			final PaymentGateway selectedGateway) {
		// if no gateways were available set text to "Not Available" and disable the combo
		if (paymentGateways.isEmpty()) {
			combo.setEnabled(false);
			combo.setText(AdminStoresMessages.get().NotSupported); // Not Available
		} else {
			// add every payment gateway to the combo and it's data map
			for (PaymentGateway paymentGateway : paymentGateways) {
				combo.setData(paymentGateway.getName(), paymentGateway);
				combo.add(paymentGateway.getName());
			}
			combo.add(AdminStoresMessages.get().NotInUse); // Not Enabled
			
			if (selectedGateway == null) {
				// if no gateway selected, select Not Enabled
				int index = combo.indexOf(AdminStoresMessages.get().NotInUse); // Not Enabled
				combo.select(index);
				
			} else {
				combo.setText(selectedGateway.getName());
			}
		}
	}

	/**
	 * Gets the selected gateway.
	 *
	 * @return the selected gateway
	 */
	protected PaymentGateway getSelectedGateway() {
		for (PaymentGatewayType paymentGatewayType : getApplicablePaymentGatewayTypes()) {
			PaymentGateway selectedPaymentGateway = getStoreEditorModel().getPaymentGateway(paymentGatewayType);
			if (selectedPaymentGateway != null) {
				return selectedPaymentGateway;
			}
		}
		
		return null;
	}	

	/**
	 * Created update value strategy for given combo and for given payment type.
	 * 
	 * @param combo the combo
	 * @return new update value strategy for given payment type
	 */
	protected ObservableUpdateValueStrategy createUpdateValueGatewayStrategy(final CCombo combo) {
		return new PaymentUpdateValueStrategy(combo);
	}

	/**
	 * Gets payment gateways by given payment type.
	 * 
	 * @return obtained list of gateways
	 */
	protected List<PaymentGateway> getAndFilterPaymentGateways() {
		final List<PaymentGateway> paymentsList = new ArrayList<>(paymentGatewaysList);
		CollectionUtils.filter(paymentsList, gateway -> {
			PaymentGateway paymentGateway = (PaymentGateway) gateway;
			for (PaymentGatewayType paymentGatewayType : getApplicablePaymentGatewayTypes()) {
				if (paymentGatewayType.equals(paymentGateway.getPaymentGatewayType())) {
					return true;
				}
			}

			return false;
		});

		return paymentsList;
	}

	/**
	 * Gets store for editing.
	 * 
	 * @return the store
	 */
	protected StoreEditorModel getStoreEditorModel() {
		return ((StoreEditorModel) editor.getModel());
	}

	/**
	 * Gets payment gateway service.
	 * 
	 * @return the payment gateway service
	 */
	protected PaymentGatewayService getPaymentGatewayService() {
		return paymentGatewayService;
	}

	/**
	 * Gets form editor.
	 * 
	 * @return form editor
	 */
	protected AbstractCmClientFormEditor getEditor() {
		return editor;
	}

	/**
	 * Payment update value strategy.
	 */
	class PaymentUpdateValueStrategy extends ObservableUpdateValueStrategy {

		private final CCombo combo;

		/**
		 * Constructs payment update value strategy with given arguments.
		 * 
		 * @param combo the combo for binding
		 */
		PaymentUpdateValueStrategy(final CCombo combo) {
			this.combo = combo;
		}

		@Override
		protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
			final PaymentGateway paymentGateway = (PaymentGateway) combo.getData(combo.getText());

			// Compare only if user has selected a value.
			if (paymentGateway != null && paymentGateway != getSelectedGateway()) {
				getStoreEditorModel().putPaymentGateway(getApplicablePaymentGatewayTypes(), paymentGateway);
			}

			additionalUpdateAction(paymentGateway);
			return Status.OK_STATUS;
		}

		/**
		 * This method makes additional update action, do nothing by default.
		 * 
		 * @param paymentGateway the payment gateway that was processed
		 */
		protected void additionalUpdateAction(final PaymentGateway paymentGateway) {
			// do nothing
		}
	}

	/**
	 * Gets the applicable payment gateway types.
	 *
	 * @return the applicable payment gateway types
	 */
	abstract Collection<PaymentGatewayType> getApplicablePaymentGatewayTypes();

	/**
	 * Populate payment gateway combo.
	 *
	 * @param paymentGatewayCombo the payment gateway combo
	 */
	protected void populatePaymentGatewayCombo(final CCombo paymentGatewayCombo) {
		// get the gateways available for the given payment type combo
		final List<PaymentGateway> paymentGateways = getAndFilterPaymentGateways();
		
		// get the currently selected gateway
		final PaymentGateway selectedGateway = getSelectedGateway();
		
		initializePaymentMethodCombobox(paymentGatewayCombo, paymentGateways, selectedGateway);
	}
}
