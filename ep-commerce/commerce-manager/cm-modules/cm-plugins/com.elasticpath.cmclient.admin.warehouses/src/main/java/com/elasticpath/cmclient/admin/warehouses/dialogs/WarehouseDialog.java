/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.warehouses.dialogs;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.admin.warehouses.AdminWarehousesImageRegistry;
import com.elasticpath.cmclient.admin.warehouses.AdminWarehousesMessages;
import com.elasticpath.cmclient.admin.warehouses.AdminWarehousesPlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.EpCountrySelectorControl;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.WarehouseAddress;
import com.elasticpath.service.store.WarehouseService;

/**
 * Edit create dialog class.
 */
public final class WarehouseDialog extends AbstractEpDialog implements ModifyListener {

	private static final int WAREHOUSE_NAME_TEXT_LIMIT = 255;

	private static final int ADDRESS_TEXT_LIMIT = 200;

	private static final int CITY_TEXT_LIMIT = 200;

	private static final int POSTALCODE_TEXT_LIMIT = 50;

	/** The maximum pick delay (mins). */
	private static final int MAX_DELAY = 10_000;

	/** The minimum pick delay (mins). */
	private static final int MIN_DELAY = 0;

	private static final int WAREHOUSE_CODE_TEXT_LIMIT = 64;

	/** Title of dialog. */
	private final String title;

	/** Title image of dialog. */
	private final Image titleImage;

	/** The data binding context. */
	private final DataBindingContext dataBindingCtx;

	/** Current warehouse entity. */
	private final Warehouse warehouse;

	/** instance of <code>EpCountrySelectorControl</code> which manages logic of State and Country combo. */
	private final EpCountrySelectorControl stateCountryManager;

	/** The Warehouse name text field. */
	private Text warehouseNameText;

	/** The Warehouse pick delay spinner field. */
	private Spinner warehousePickDelaySpinner;

	/** The Warehouse street address line 1 text field. */
	private Text streetAddressText1;

	/** The Warehouse street address line 2 text field. */
	private Text streetAddressText2;

	/** The Warehouse street address line 2 text field. */
	private Text cityText;

	/** The Warehouse Zip/postal code text field. */
	private Text zipPostalCodeText;

	private Text codeText;

	/**
	 * True if the current CM user is authorized to edit this warehouse.
	 */
	private boolean authorized;

	/**
	 * Constructor.
	 *
	 * @param parentShell parent shell.
	 * @param warehouse the warehouse entity to edit.
	 * @param title title of dialog
	 * @param image title image
	 */
	public WarehouseDialog(final Shell parentShell, final Warehouse warehouse, final String title, final Image image) {
		super(parentShell, 2, false);
		this.title = title;
		titleImage = image;
		this.warehouse = warehouse;
		dataBindingCtx = new DataBindingContext();
		stateCountryManager = new EpCountrySelectorControl();

		setAuthorized();
	}

	/**
	 * Determines and sets the CM user authorization to edit this warehouse.
	 */
	protected void setAuthorized() {
		authorized = !isEditWarehouse() || AuthorizationService.getInstance().isAuthorizedForWarehouse(warehouse);
	}

	/**
	 * Convenience method to open a simple create dialog.
	 *
	 * @param parentShell the parent Shell
	 * @param warehouse the warehouse to edit
	 * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
	 */
	public static boolean openCreateDialog(final Shell parentShell, final Warehouse warehouse) {
		final WarehouseDialog dialog = new WarehouseDialog(parentShell, warehouse, AdminWarehousesMessages.get().CreateWarehouse,
				AdminWarehousesImageRegistry.getImage(AdminWarehousesImageRegistry.IMAGE_WAREHOUSE_CREATE));
		return dialog.open() == 0;
	}

	/**
	 * Convenience method to open a simple edit dialog.
	 *
	 * @param parentShell the parent Shell
	 * @param warehouse the warehouse to edit
	 * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
	 */
	public static boolean openEditDialog(final Shell parentShell, final Warehouse warehouse) {
		final WarehouseDialog dialog = new WarehouseDialog(parentShell, warehouse, AdminWarehousesMessages.get().EditWarehouse,
				AdminWarehousesImageRegistry.getImage(AdminWarehousesImageRegistry.IMAGE_WAREHOUSE_EDIT));
		return dialog.open() == 0;
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		EpState epState = EpState.EDITABLE;
		EpState epStateWarehouseCode = EpState.EDITABLE;
		if (isEditWarehouse()) {
			// Edit Warehouse dialog, not Create Warehouse dialog
			epStateWarehouseCode = EpState.READ_ONLY;

			if (authorized) {
				epState = EpState.EDITABLE;
			} else {
				epState = EpState.READ_ONLY;
				setErrorMessage(
					NLS.bind(AdminWarehousesMessages.get().WarehouseNoPermission,
					new String[]{warehouse.getName()}));
			}
		}

		dialogComposite.addLabelBoldRequired(AdminWarehousesMessages.get().WarehouseCode, epStateWarehouseCode, labelData);
		codeText = dialogComposite.addTextField(epStateWarehouseCode, fieldData);
		codeText.setTextLimit(WAREHOUSE_CODE_TEXT_LIMIT);
		codeText.addModifyListener(this);

		dialogComposite.addLabelBoldRequired(AdminWarehousesMessages.get().WarehouseName, epState, labelData);
		warehouseNameText = dialogComposite.addTextField(epState, fieldData);
		warehouseNameText.setTextLimit(WAREHOUSE_NAME_TEXT_LIMIT);

		dialogComposite.addLabelBoldRequired(AdminWarehousesMessages.get().PickDelay, epState, labelData);
		warehousePickDelaySpinner = dialogComposite.addSpinnerField(epState, fieldData);
		warehousePickDelaySpinner.setMinimum(MIN_DELAY);
		warehousePickDelaySpinner.setMaximum(MAX_DELAY);
		dialogComposite.addLabelBoldRequired(AdminWarehousesMessages.get().AddressLine1, epState, labelData);
		streetAddressText1 = dialogComposite.addTextField(epState, fieldData);
		streetAddressText1.setTextLimit(ADDRESS_TEXT_LIMIT);

		dialogComposite.addLabelBold(AdminWarehousesMessages.get().AddressLine2, labelData);
		streetAddressText2 = dialogComposite.addTextField(epState, null);
		streetAddressText2.setTextLimit(ADDRESS_TEXT_LIMIT);

		dialogComposite.addLabelBoldRequired(AdminWarehousesMessages.get().City, epState, labelData);
		cityText = dialogComposite.addTextField(epState, fieldData);
		cityText.setTextLimit(CITY_TEXT_LIMIT);

		dialogComposite.addLabelBoldRequired(AdminWarehousesMessages.get().StateProvinceRegion, epState, labelData);
		stateCountryManager.setStateCombo(dialogComposite.addComboBox(epState, fieldData));

		dialogComposite.addLabelBoldRequired(AdminWarehousesMessages.get().ZipPostalCode, epState, labelData);
		zipPostalCodeText = dialogComposite.addTextField(epState, fieldData);
		zipPostalCodeText.setTextLimit(POSTALCODE_TEXT_LIMIT);

		dialogComposite.addLabelBoldRequired(AdminWarehousesMessages.get().Country, epState, labelData);
		stateCountryManager.setCountryCombo(dialogComposite.addComboBox(epState, fieldData));

		stateCountryManager.initStateCountryCombo(epState);
	}

	@Override
	public boolean isComplete() {
		boolean complete = false;

		// if not authorized, form will never be complete
		if (authorized) {
			complete = super.isComplete();
		}

		return complete;
	}

	@Override
	protected String getPluginId() {
		return AdminWarehousesPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return warehouse;
	}

	@Override
	protected void okPressed() {
		dataBindingCtx.updateModels();

		// we can only change code if we creating a warehouse
		if (!isEditWarehouse()) {
			final WarehouseService warehouseService = (WarehouseService) ServiceLocator.getService(
					ContextIdNames.WAREHOUSE_SERVICE);
			if (warehouseService.findByCode(warehouse.getCode()) != null) {
				setErrorMessage(AdminWarehousesMessages.get().WarehouseCodeExists);
				return;
			}
		}

		super.okPressed();
	}

	@Override
	protected String getTitle() {
		return title;
	}

	@Override
	protected String getInitialMessage() {
		return AdminWarehousesMessages.get().DialogInitialMessage;
	}

	@Override
	protected String getWindowTitle() {
		return getTitle();
	}

	@Override
	protected Image getWindowImage() {
		return titleImage;
	}

	/**
	 * @return the DataBindingContext.
	 */
	public DataBindingContext getBindingContext() {
		return dataBindingCtx;
	}

	/**
	 * @return the warehouse
	 */
	public Warehouse getWarehouse() {
		return warehouse;
	}

	@Override
	protected void bindControls() {
		final Warehouse warehouse = getWarehouse();
		final boolean hideDecorationOnFirstValidation = true;

		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();
		binder.bind(getBindingContext(), codeText, warehouse, "code", //$NON-NLS-1$
				EpValidatorFactory.WAREHOUSE_CODE, null, hideDecorationOnFirstValidation);

		binder.bind(getBindingContext(), warehouseNameText, warehouse, "name", //$NON-NLS-1$
				EpValidatorFactory.STRING_255_REQUIRED, null, hideDecorationOnFirstValidation);

		binder.bind(getBindingContext(), warehousePickDelaySpinner, warehouse, "pickDelay", //$NON-NLS-1$
				null, null, hideDecorationOnFirstValidation);

		binder.bind(getBindingContext(), streetAddressText1, warehouse.getAddress(), "street1", //$NON-NLS-1$
				EpValidatorFactory.STRING_255_REQUIRED, null, hideDecorationOnFirstValidation);

		binder.bind(getBindingContext(), streetAddressText2, warehouse.getAddress(), "street2", //$NON-NLS-1$
				null, null, hideDecorationOnFirstValidation);

		binder.bind(getBindingContext(), cityText, warehouse.getAddress(), "city", //$NON-NLS-1$
				EpValidatorFactory.STRING_255_REQUIRED, null, hideDecorationOnFirstValidation);

		final ObservableUpdateValueStrategy stateUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				String selectedState = stateCountryManager.getStateComboItem();
				warehouse.getAddress().setSubCountry(selectedState);
				return Status.OK_STATUS;
			}
		};

		binder.bind(getBindingContext(), stateCountryManager.getStateCombo(), null, null, stateUpdateStrategy, hideDecorationOnFirstValidation);

		binder.bind(getBindingContext(), zipPostalCodeText, warehouse.getAddress(), "zipOrPostalCode", //$NON-NLS-1$
				EpValidatorFactory.STRING_255_REQUIRED, null, hideDecorationOnFirstValidation);

		final ObservableUpdateValueStrategy countryUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				String selectedCountry = stateCountryManager.getCountryComboItem();
				warehouse.getAddress().setCountry(selectedCountry);
				return Status.OK_STATUS;
			}
		};

		binder.bind(getBindingContext(), stateCountryManager.getCountryCombo(), null, null, countryUpdateStrategy, hideDecorationOnFirstValidation);

		EpDialogSupport.create(this, getBindingContext());
	}

	@Override
	protected void populateControls() {
		final WarehouseAddress address = warehouse.getAddress();

		stateCountryManager.populateStateCountryCombo();
		stateCountryManager.selectCountryCombo(address.getCountry());

		if (isEditWarehouse()) {
			codeText.setText(warehouse.getCode());
			warehouseNameText.setText(warehouse.getName());
			warehousePickDelaySpinner.setSelection(warehouse.getPickDelay());
			streetAddressText1.setText(address.getStreet1());
			if (address.getStreet2() != null) {
				streetAddressText2.setText(address.getStreet2());
			}
			cityText.setText(address.getCity());
			stateCountryManager.selectStateCombo(address.getSubCountry());
			zipPostalCodeText.setText(address.getZipOrPostalCode());
		}

	}

	/**
	 * @return true if it is edit dialog
	 */
	private boolean isEditWarehouse() {
		return warehouse.isPersisted();
	}

	/** {@inheritDoc} */
	public void modifyText(final ModifyEvent event) {
		// clear error on text modify
		setErrorMessage(null);

		if (!authorized) {
			setErrorMessage(
				NLS.bind(AdminWarehousesMessages.get().WarehouseNoPermission,
				new String[]{warehouse.getName()}));
		}
	}
}
