/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.fulfillment.editors.order.dialog;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.store.StoreService;

/**
 * Dialog class that represent a store selection dialog.
 */
public class StoreSelectionDialog extends AbstractEpDialog {

	private CCombo storeComboBox;

	private List<Store> storeList;

	private Store selectedStore;

	private final DataBindingContext bindingContext;

	/**
	 * Constructor.
	 * 
	 * @param parentShell the parent shell
	 */
	public StoreSelectionDialog(final Shell parentShell) {
		super(parentShell, 2, false);
		this.bindingContext = new DataBindingContext();
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		this.createEpButtonsForButtonsBar(ButtonsBarType.OK, parent);
	}

	@Override
	protected void bindControls() {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
		final boolean hideDecorationOnFirstValidation = true;
		bindingProvider.bind(this.bindingContext, storeComboBox, EpValidatorFactory.REQUIRED_COMBO_FIRST_ELEMENT_NOT_VALID, null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object value) {
						int selectedIndex = (Integer) value;
						if (selectedIndex == 0) {
							return Status.CANCEL_STATUS;
						}
						return Status.OK_STATUS;
					}

				}, hideDecorationOnFirstValidation);

	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);

		dialogComposite.addLabelBold(FulfillmentMessages.get().OrderCreate_DialogStoreLabel, labelData);

		storeComboBox = dialogComposite.addComboBox(EpState.EDITABLE, fieldData);
		storeComboBox.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				// nothing

			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				int selectionIndex = storeComboBox.getSelectionIndex();
				if (selectionIndex == 0) {
					return;
				}
				selectedStore = storeList.get(selectionIndex - 1);

			}

		});

	}

	@Override
	protected String getPluginId() {
		return FulfillmentPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return Arrays.asList(storeList, selectedStore);
	}

	@Override
	protected void populateControls() {
		storeComboBox.setItems(getStoreAsArray());
		storeComboBox.add(FulfillmentMessages.get().OrderCreate_DialogComboSelectAStore, 0);
		storeComboBox.select(0);
	}

	/**
	 * Gets the store name as string array.
	 * 
	 * @return String[] contains all the stores' names
	 */
	private String[] getStoreAsArray() {
		final StoreService storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);

		storeList = storeService.findAllCompleteStores();
		AuthorizationService.getInstance().filterAuthorizedStores(storeList);
		if (storeList == null || storeList.isEmpty()) {
			return new String[0];
		}

		String[] storeArray = new String[storeList.size()];

		for (int i = 0; i < storeArray.length; i++) {
			final Store store = storeList.get(i);
			storeArray[i] = store.getName();
		}
		return storeArray;
	}

	@Override
	protected String getInitialMessage() {
		return FulfillmentMessages.get().OrderCreate_DialogDescription;
	}

	@Override
	protected String getTitle() {
		return FulfillmentMessages.get().OrderCreate_DialogTitle;
	}

	@Override
	protected String getWindowTitle() {
		return FulfillmentMessages.get().OrderCreate_DialogTitle;
	}

	@Override
	protected Image getWindowImage() {
		return FulfillmentImageRegistry.getImage(FulfillmentImageRegistry.IMAGE_CREATE_ORDER);
	}

	/**
	 * Gets the selected store.
	 * 
	 * @return store the selected store
	 */
	public Store getSelectedStore() {
		return selectedStore;
	}

}
