/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse.editors.inventory;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.warehouse.WarehouseMessages;
import com.elasticpath.cmclient.warehouse.WarehousePlugin;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.InventoryAudit;
import com.elasticpath.domain.catalog.InventoryEventType;
import com.elasticpath.domain.store.AdjustmentQuantityOnHandReason;
import com.elasticpath.inventory.InventoryDto;

/**
 * UI representation of the inventory adjust quantity on hand section.
 */
public class AdjustQuantityOnHandSectionPart extends AbstractCmClientEditorPageSectionPart {

	private static final int DESCRIPTION_AREA_HEIGHT = 100;

	private final AbstractCmClientFormEditor editor;

	private final InventoryAudit inventoryAudit;

	private final InventoryModel inventoryModel;

	private CCombo adjustmentCombo;

	private Spinner quantitySpinner;

	private CCombo reasonCombo;

	private Text commentText;

	private EpState epStateAdjustment;

	private Label quantityLabel;

	private Label reasonLabel;

	private Label commentLabel;

	private final EpState editMode;

	private IEpLayoutComposite adjustmentComposite;

	/**
	 * Constructor.
	 *
	 * @param formPage the FormPage
	 * @param editor the AbstractCmClientFormEditor
	 * @param editMode if this part is read only
	 */
	public AdjustQuantityOnHandSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor, final EpState editMode) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR);
		inventoryModel = (InventoryModel) editor.getModel();
		inventoryAudit = ServiceLocator.getService(ContextIdNames.INVENTORY_AUDIT);
		inventoryAudit.setEventType(InventoryEventType.STOCK_ADJUSTMENT);
		inventoryAudit.setEventOriginator(InventoryAudit.EVENT_ORIGINATOR_CMUSER + LoginManager.getCmUserGuid());
		inventoryAudit.setLogDate(new Date());
		inventoryModel.setInventoryAudit(null);
		this.editor = editor;
		this.editMode = editMode;
	}

	@Override
	@SuppressWarnings("PMD.PrematureDeclaration")
	protected void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();
		final InventoryDto inventoryDto = inventoryModel.getInventory();

		final IValidator validateReserveQuantity = new CompoundValidator(new IValidator[] {
				EpValidatorFactory.POSITIVE_INTEGER,
				value -> {
					final Integer reserveValue = (Integer) value;
					if (WarehouseMessages.get().Inventory_AdjustmentRemoveStock.equals(adjustmentCombo.getText())
							&& reserveValue > (inventoryDto.getQuantityOnHand() - inventoryDto.getReservedQuantity())) {
						return new Status(IStatus.ERROR, WarehousePlugin.PLUGIN_ID, IStatus.ERROR,
								WarehouseMessages.get().InventoryError_ReservedQuantityGreaterOnHand, null);

					}

					return Status.OK_STATUS;
				}});

		final ObservableUpdateValueStrategy quantityStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				if (WarehouseMessages.get().Inventory_AdjustmentAddStock.equals(adjustmentCombo.getText())) {
					inventoryAudit.setQuantity(quantitySpinner.getSelection());
				} else if (WarehouseMessages.get().Inventory_AdjustmentRemoveStock.equals(adjustmentCombo.getText())) {
					inventoryAudit.setQuantity(-quantitySpinner.getSelection());
				}

				return Status.OK_STATUS;
			}
		};
		final boolean hideDecorationOnFirstValidation = true;
		binder.bind(bindingContext, quantitySpinner, validateReserveQuantity, null, quantityStrategy, hideDecorationOnFirstValidation);

		binder.bind(bindingContext, commentText, inventoryAudit, "comment"); //$NON-NLS-1$

		final ObservableUpdateValueStrategy reasonStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				inventoryAudit.setReason((String) reasonCombo.getData(reasonCombo.getText()));
				return Status.OK_STATUS;
			}
		};

		binder.bind(bindingContext, reasonCombo, null, null, reasonStrategy, hideDecorationOnFirstValidation);

		bindingContext.updateModels();
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		adjustmentComposite = CompositeFactory.createTableWrapLayoutComposite(client, 2, false);
		adjustmentComposite.setLayoutData(getLayoutData());
		final IEpLayoutData labelData = adjustmentComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = adjustmentComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL);

		adjustmentComposite.addLabelBold(WarehouseMessages.get().Inventory_Adjustment, labelData);
		adjustmentCombo = adjustmentComposite.addComboBox(editMode, fieldData);

		adjustmentCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				// do nothing
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (WarehouseMessages.get().Inventory_AdjustmentSelectAction.equals(adjustmentCombo.getText())) {
					epStateAdjustment = EpState.DISABLED;
					inventoryModel.setInventoryAudit(null);
				} else {
					epStateAdjustment = editMode;
					inventoryModel.setInventoryAudit(inventoryAudit);

					AdjustQuantityOnHandSectionPart.this.getBindingContext().updateModels();
				}
				updateState(epStateAdjustment);
				editor.controlModified();
			}

		});

		epStateAdjustment = EpState.DISABLED;
		quantityLabel = adjustmentComposite.addLabelBoldRequired(WarehouseMessages.get().Inventory_Quantity, epStateAdjustment, labelData);
		quantitySpinner = adjustmentComposite.addSpinnerField(epStateAdjustment, fieldData);
		quantitySpinner.setMinimum(1);
		quantitySpinner.setMaximum(Integer.MAX_VALUE);

		reasonLabel = adjustmentComposite.addLabelBoldRequired(WarehouseMessages.get().Inventory_Reason, epStateAdjustment, labelData);
		reasonCombo = adjustmentComposite.addComboBox(epStateAdjustment, fieldData);

		commentLabel = adjustmentComposite.addLabelBold(WarehouseMessages.get().Inventory_Comment, labelData);
		adjustmentComposite.addEmptyComponent(fieldData);
		commentText = adjustmentComposite.addTextArea(true, false, epStateAdjustment,
				adjustmentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 2, 1));

		((TableWrapData) commentText.getLayoutData()).heightHint = DESCRIPTION_AREA_HEIGHT;

		updateState(epStateAdjustment);

	}

	private void updateState(final EpState epState) {
		EpControlFactory.changeEpState(quantitySpinner, epState);
		EpControlFactory.changeEpState(reasonCombo, epState);
		EpControlFactory.changeEpState(commentText, epState);
		EpControlFactory.changeEpState(quantityLabel, epState);
		EpControlFactory.changeEpState(reasonLabel, epState);
		EpControlFactory.changeEpState(commentLabel, epState);
		adjustmentComposite.getSwtComposite().layout();
	}

	@Override
	protected void populateControls() {
		adjustmentCombo.add(WarehouseMessages.get().Inventory_AdjustmentSelectAction);
		adjustmentCombo.add(WarehouseMessages.get().Inventory_AdjustmentAddStock);
		adjustmentCombo.add(WarehouseMessages.get().Inventory_AdjustmentRemoveStock);
		adjustmentCombo.select(0);

		final AdjustmentQuantityOnHandReason adjustmentReason = ServiceLocator.getService(
				ContextIdNames.ADJUSTMENT_QUANTITY_ON_HAND_REASON);
		final Map<String, String> reasonMap = adjustmentReason.getReasonMap();
		for (final Entry<String, String> entry : reasonMap.entrySet()) {
			reasonCombo.add(entry.getValue());
			reasonCombo.setData(entry.getValue(), entry.getKey());
		}
		reasonCombo.select(0);
		quantitySpinner.setSelection(1);
	}

	@Override
	protected String getSectionTitle() {
		return WarehouseMessages.get().Inventory_AdjustQuantityOnHandSectionPart;
	}

}
