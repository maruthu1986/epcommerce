/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse.editors.orderreturn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.warehouse.WarehouseImageRegistry;
import com.elasticpath.cmclient.warehouse.WarehouseMessages;
import com.elasticpath.cmclient.warehouse.WarehousePermissions;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnReceivedState;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderReturnStatus;

/**
 * UI representation of the order return details section.
 */
@SuppressWarnings({"PMD.GodClass"})
public class OrderReturnDetailsSectionPart extends AbstractCmClientEditorPageSectionPart {
	private static final int REC_STATE_COLUMN_WIDTH = 120;

	private static final int REC_QTY_COLUMN_WIDTH = 60;

	private static final int EXP_QTY_COLUMN_WIDTH = 60;

	private static final int PRODUCT_NAME_COLUMN_WIDTH = 120;

	private static final int SKU_CODE_COLUMN_WIDTH = 75;

	private static final int EMPTY_COLUMN_WIDTH = 21;

	private static final int COLUMN_INDEX_EMPTY = 0;

	private static final int COLUMN_INDEX_SKU_CODE = 1;

	private static final int COLUMN_INDEX_PRODUCT_NAME = 2;

	private static final int COLUMN_INDEX_EXP_QTY = 3;

	private static final int COLUMN_INDEX_REC_QTY = 4;

	private static final int COLUMN_INDEX_REC_STATE = 5;

	private static final int TABLE_HEIGHT = 100;

	private static final String ORDER_RETURN_DETAILS_TABLE = "Order Return Details Table"; //$NON-NLS-1$

	private final OrderReturn orderReturn;

	private final ControlModificationListener controlModificationListener;

	private IEpTableViewer skuTableViewer;

	private IEpTableColumn recQtyColumn;

	private IEpTableColumn recStateColumn;

	private final List<String> statesList;

	private final EpState editMode;

	/**
	 * The constructor.
	 *
	 * @param formPage the form page.
	 * @param editor the editor.
	 * @param editMode if this part is read only
	 */
	public OrderReturnDetailsSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor, final EpState editMode) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);

		this.orderReturn = (OrderReturn) editor.getModel();
		this.controlModificationListener = editor;

		this.statesList = new ArrayList<>();

		if (editMode == EpState.EDITABLE && orderReturn.getReturnStatus() == OrderReturnStatus.AWAITING_STOCK_RETURN
				&& AuthorizationService.getInstance().isAuthorizedWithPermission(WarehousePermissions.WAREHOUSE_ORDER_RETURN_EDIT)) {
			this.editMode = EpState.EDITABLE;
		} else {
			this.editMode = EpState.READ_ONLY;
		}

	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		IEpLayoutComposite mainPane = CompositeFactory.createTableWrapLayoutComposite(client, 2, false);
		mainPane.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));

		IEpLayoutData layoutData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		skuTableViewer = mainPane.addTableViewer(false, editMode, layoutData, ORDER_RETURN_DETAILS_TABLE);
		TableWrapData skuTableLayoutData = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB);
		skuTableLayoutData.heightHint = TABLE_HEIGHT;
		skuTableViewer.getSwtTable().setLayoutData(skuTableLayoutData);

		createSkuTableContent();

		if (editMode == EpState.EDITABLE) {
			mainPane.setControlModificationListener(controlModificationListener);
		}
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// Nothing to bind
	}

	@Override
	protected void populateControls() {
		skuTableViewer.setInput(orderReturn.getOrderReturnSkus());

		getSection().setText(getSectionTitle());

		Table table = skuTableViewer.getSwtTable();

		recQtyColumn.setEditingSupport(new RecQtyEditingSupport(skuTableViewer.getSwtTableViewer(), new TextCellEditor(table)));

		OrderReturnReceivedState orderReturnReceivedState = ServiceLocator.getService(
				ContextIdNames.ORDER_RETURN_RECEIVED_STATE);
		Set<String> orderReturnReceivedStateKeys = orderReturnReceivedState.getStateMap().keySet();
		String[] states = new String[orderReturnReceivedStateKeys.size()];
		int index = 0;
		for (String key : orderReturnReceivedStateKeys) {
			String value = orderReturnReceivedState.getStateMap().get(key);
			states[index] = value;

			statesList.add(key);

			index++;
		}

		recStateColumn.setEditingSupport(new RecStateEditingSupport(skuTableViewer.getSwtTableViewer(), new ComboBoxCellEditor(table, states,
				SWT.DROP_DOWN)));
	}

	@Override
	protected String getSectionDescription() {
		return getSectionTitle();
	}

	@Override
	protected String getSectionTitle() {
		return
			NLS.bind(WarehouseMessages.get().OrderReturnDetailsSection_Title,
			orderReturn.getUidPk(), WarehouseMessages.EMPTY_STRING);
	}

	private void createSkuTableContent() {
		int[] columnWidths = new int[] { EMPTY_COLUMN_WIDTH, SKU_CODE_COLUMN_WIDTH, PRODUCT_NAME_COLUMN_WIDTH, EXP_QTY_COLUMN_WIDTH,
				REC_QTY_COLUMN_WIDTH, REC_STATE_COLUMN_WIDTH };

		String[] columnNames = new String[] { WarehouseMessages.EMPTY_STRING, WarehouseMessages.get().OrderReturnDetailsSection_SKUCodeColumn,
				WarehouseMessages.get().OrderReturnDetailsSection_ProductNameColumn, WarehouseMessages.get().OrderReturnDetailsSection_ExpQtyColumn,
				WarehouseMessages.get().OrderReturnDetailsSection_RecQtyColumn, WarehouseMessages.get().OrderReturnDetailsSection_RecStateColumn };

		for (int i = 0; i < columnWidths.length; i++) {
			IEpTableColumn column = skuTableViewer.addTableColumn(columnNames[i], columnWidths[i], IEpTableColumn.TYPE_NONE);
			switch (i) {
			case COLUMN_INDEX_EMPTY:
				column.getSwtTableColumn().setToolTipText(WarehouseMessages.get().OrderReturnDetailsSection_RMATableTooltip);
				break;
			case COLUMN_INDEX_REC_QTY:
				recQtyColumn = column;
				break;
			case COLUMN_INDEX_REC_STATE:
				recStateColumn = column;
				break;
			default:
				break;
			}
		}

		skuTableViewer.setLabelProvider(new SkuTableLabelProvider());
		skuTableViewer.setContentProvider(new ArrayContentProvider());

	}

	/**
	 * Abstract class for editing support.
	 */
	private abstract class AbstractEditingSupport extends EditingSupport {
		private final CellEditor cellEditor;

		AbstractEditingSupport(final ColumnViewer columnViewer, final CellEditor cellEditor) {
			super(columnViewer);

			this.cellEditor = cellEditor;
		}

		@Override
		protected boolean canEdit(final Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(final Object element) {
			return cellEditor;
		}

		@Override
		protected Object getValue(final Object element) {
			return doGetValue((OrderReturnSku) element);
		}

		@Override
		protected void setValue(final Object element, final Object value) {
			doSetValue((OrderReturnSku) element, value.toString());

			getViewer().update(element, null);

		}

		protected abstract Object doGetValue(OrderReturnSku orderReturnSku);

		protected abstract void doSetValue(OrderReturnSku orderReturnSku, String value);
	}

	/**
	 * Editing support for RecQty column.
	 */
	private class RecQtyEditingSupport extends AbstractEditingSupport {
		RecQtyEditingSupport(final ColumnViewer columnViewer, final CellEditor cellEditor) {
			super(columnViewer, cellEditor);
		}

		@Override
		protected void doSetValue(final OrderReturnSku orderReturnSku, final String value) {
			if (validateQuantity(value)) {
				int recQty = Integer.valueOf(value);

				if (updateReturnQuantity(orderReturnSku, recQty)) {
					((OrderReturnPage) getManagedForm().getContainer()).updateStatus();
					return;
				}
			}
			displayErrorMessageBox();
		}

		private boolean updateReturnQuantity(final OrderReturnSku orderReturnSku, final int recQty) {
			if (recQty <= orderReturnSku.getQuantity()) {
				orderReturnSku.setReceivedQuantity(recQty);
				orderReturn.updateOrderReturnStatus();
				return true;
			}
			return false;
		}

		private boolean validateQuantity(final String value) {
			return EpValidatorFactory.REQUIRED.validate(value) == ValidationStatus.ok()
					&& EpValidatorFactory.NON_NEGATIVE_INTEGER.validate(value) == Status.OK_STATUS;
		}

		private void displayErrorMessageBox() {
			MessageBox messageBox = new MessageBox(getManagedForm().getForm().getShell(), SWT.ICON_ERROR);
			messageBox.setText(WarehouseMessages.get().OrderReturn_RecQtyError_Title);
			messageBox.setMessage(WarehouseMessages.get().OrderReturn_RecQtyError_Msg);
			messageBox.open();
		}

		@Override
		protected Object doGetValue(final OrderReturnSku orderReturnSku) {
			return String.valueOf(orderReturnSku.getReceivedQuantity());
		}

	}

	/**
	 * Editing support for RecState column.
	 */
	private class RecStateEditingSupport extends AbstractEditingSupport {
		RecStateEditingSupport(final ColumnViewer columnViewer, final CellEditor cellEditor) {
			super(columnViewer, cellEditor);
		}

		@Override
		protected Object doGetValue(final OrderReturnSku orderReturnSku) {
			if (orderReturnSku.getReceivedState() == null) {
				return 0;
			}

			return statesList.indexOf(orderReturnSku.getReceivedState());
		}

		@Override
		protected void doSetValue(final OrderReturnSku orderReturnSku, final String value) {
			orderReturnSku.setReceivedState(statesList.get(Integer.valueOf(value)));
		}

	}

	/**
	 * Label provider for table of returned SKU's.
	 */
	class SkuTableLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			Image image = null;
			switch (columnIndex) {
			case COLUMN_INDEX_EMPTY:
				image = WarehouseImageRegistry.getImage(WarehouseImageRegistry.ICON_SKUTABLE_ITEM);
				break;
			case COLUMN_INDEX_REC_QTY:
				image = WarehouseImageRegistry.getImage(WarehouseImageRegistry.ICON_SKUTABLE_EDIT_CELL);
				break;
			case COLUMN_INDEX_REC_STATE:
				image = WarehouseImageRegistry.getImage(WarehouseImageRegistry.ICON_SKUTABLE_EDIT_CELL);
				break;
			default:
				break;
			}

			return image;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			OrderReturnSku orderReturnSku = (OrderReturnSku) element;

			String text = WarehouseMessages.EMPTY_STRING;
			switch (columnIndex) {
			case COLUMN_INDEX_SKU_CODE:
				text = orderReturnSku.getOrderSku().getSkuCode();
				break;
			case COLUMN_INDEX_PRODUCT_NAME:
				text = orderReturnSku.getOrderSku().getDisplayName();
				break;
			case COLUMN_INDEX_EXP_QTY:
				text = String.valueOf(orderReturnSku.getQuantity());
				break;
			case COLUMN_INDEX_REC_QTY:
				text = String.valueOf(orderReturnSku.getReceivedQuantity());
				break;
			case COLUMN_INDEX_REC_STATE:
				if (orderReturnSku.getReceivedState() == null) {
					if (editMode == EpState.EDITABLE) {
						text = WarehouseMessages.get().OrderReturn_SelectRecState;
					}
				} else {
					OrderReturnReceivedState orderReturnReceivedState = ServiceLocator.getService(
							ContextIdNames.ORDER_RETURN_RECEIVED_STATE);
					text = orderReturnReceivedState.getStateMap().get(orderReturnSku.getReceivedState());
				}

				break;
			case COLUMN_INDEX_EMPTY:
			default:
				break;
			}

			return text;
		}
	}

}
