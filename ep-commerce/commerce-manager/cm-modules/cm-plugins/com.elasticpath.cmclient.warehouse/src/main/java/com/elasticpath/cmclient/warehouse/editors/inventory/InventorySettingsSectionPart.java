/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse.editors.inventory;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.warehouse.WarehouseMessages;
import com.elasticpath.cmclient.warehouse.WarehousePlugin;
import com.elasticpath.inventory.InventoryDto;

/**
 * UI representation of the inventory settings section.
 */
public class InventorySettingsSectionPart extends AbstractCmClientEditorPageSectionPart {

	private final AbstractCmClientFormEditor editor;

	private final InventoryModel inventoryModel;

	private Spinner reserveQuantitySpinner;

	private Spinner reOrderMinimumSpinner;

	private Spinner reOrderQuantitySpinner;

	private IEpDateTimePicker expectedReStockDate;

	private final EpState editMode;
	/**
	 * Constructor.
	 * 
	 * @param formPage the FormPage
	 * @param editor the AbstractCmClientFormEditor
	 * @param editMode if this part is read only
	 */
	public InventorySettingsSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor, final EpState editMode) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR);
		inventoryModel = (InventoryModel) editor.getModel();
		this.editor = editor;
		this.editMode = editMode;
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final InventoryDto inventoryDto = inventoryModel.getInventory();

		IValidator validateReserveQuantity = value -> {
			Integer newReservedQuantity = (Integer) value;

			// Don't perform validation unless the user has edited the reservedQuantity field.
			boolean hasReservedQuantityChanged = newReservedQuantity != inventoryDto.getReservedQuantity();

			// Quantity on hand can become negative under load (where small
			// amounts of over-selling can occur).  In this case, we do not
			// want to freeze the dialog, as this blocks the store manager
			// from recovering (by adding stock or reducing the reserved quantity).
			boolean isStockAvailable = inventoryDto.getAvailableQuantityInStock() > 0;

			if (hasReservedQuantityChanged
				&& isStockAvailable
				&& newReservedQuantity > inventoryDto.getQuantityOnHand()) {
				return new Status(IStatus.ERROR, WarehousePlugin.PLUGIN_ID, IStatus.ERROR,
						WarehouseMessages.get().InventoryError_ReservedQuantityGreaterOnHand, null);
			} else {
			return Status.OK_STATUS;
		}
		};

		final boolean hideDecorationOnFirstValidation = true;
		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();
		binder.bind(bindingContext, reserveQuantitySpinner, inventoryDto,
				"reservedQuantity", validateReserveQuantity, null, hideDecorationOnFirstValidation); //$NON-NLS-1$
		binder.bind(bindingContext, reOrderMinimumSpinner, inventoryDto, "reorderMinimum"); //$NON-NLS-1$
		binder.bind(bindingContext, reOrderQuantitySpinner, inventoryDto, "reorderQuantity"); //$NON-NLS-1$

		final ObservableUpdateValueStrategy expectedReStockDateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				if (inventoryDto.getRestockDate() != expectedReStockDate.getDate()) {
					inventoryDto.setRestockDate(expectedReStockDate.getDate());
					editor.controlModified();
				}
				return Status.OK_STATUS;
			}
		};

		binder.bind(bindingContext, expectedReStockDate.getSwtText(), EpValidatorFactory.DATE, null, expectedReStockDateStrategy,
				hideDecorationOnFirstValidation);

		bindingContext.updateModels();
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		final IEpLayoutComposite composite = CompositeFactory.createTableWrapLayoutComposite(client, 2, true);
		composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
		final IEpLayoutData labelData = composite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = composite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL);
		SelectionListener selectionListener = new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				// Do nothing

			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				editor.controlModified();
			}

		};

		composite.addLabelBold(WarehouseMessages.get().Inventory_ReservedQuantity, labelData);
		reserveQuantitySpinner = composite.addSpinnerField(editMode, fieldData);
		reserveQuantitySpinner.setMinimum(0);
		reserveQuantitySpinner.setMaximum(Integer.MAX_VALUE);
		reserveQuantitySpinner.addSelectionListener(selectionListener);

		composite.addLabelBold(WarehouseMessages.get().Inventory_ReOrderMinimum, labelData);
		reOrderMinimumSpinner = composite.addSpinnerField(editMode, fieldData);
		reOrderMinimumSpinner.setMinimum(0);
		reOrderMinimumSpinner.setMaximum(Integer.MAX_VALUE);
		reOrderMinimumSpinner.addSelectionListener(selectionListener);

		composite.addLabelBold(WarehouseMessages.get().Inventory_ReOrderQuantity, labelData);
		reOrderQuantitySpinner = composite.addSpinnerField(editMode, fieldData);
		reOrderQuantitySpinner.setMinimum(0);
		reOrderQuantitySpinner.setMaximum(Integer.MAX_VALUE);
		reOrderQuantitySpinner.addSelectionListener(selectionListener);

		composite.addLabelBold(WarehouseMessages.get().Inventory_ExpectedReStockDate, labelData);
		expectedReStockDate = composite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE, editMode, fieldData);
	}

	@Override
	protected void populateControls() {
		InventoryDto inventoryDto = inventoryModel.getInventory();
		reserveQuantitySpinner.setSelection(inventoryDto.getReservedQuantity());
		reOrderMinimumSpinner.setSelection(inventoryDto.getReorderMinimum());
		reOrderQuantitySpinner.setSelection(inventoryDto.getReorderQuantity());
		expectedReStockDate.setDate(inventoryDto.getRestockDate());
	}

	@Override
	protected String getSectionTitle() {
		return WarehouseMessages.get().Inventory_SettingsSectionPart;
	}
	
	
}
