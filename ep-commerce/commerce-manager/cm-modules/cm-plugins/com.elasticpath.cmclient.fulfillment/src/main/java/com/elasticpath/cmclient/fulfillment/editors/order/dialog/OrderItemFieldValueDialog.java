/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.order.dialog;

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.cmclient.fulfillment.editors.order.actions.ContributedAction;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;

/**
 *
 * Simple field value edit dialog.
 *
 */
@SuppressWarnings({"PMD.RedundantFieldInitializer"})
public class OrderItemFieldValueDialog  extends AbstractEpDialog {
	/** ContributedAction extension point ID for order sku data dialogs. **/
	public static final String ORDERSKU_CONTRIBUTED_ACTIONS_EXT_POINT =
		"com.elasticpath.cmclient.fulfillment.ordersku.contributedactions"; //$NON-NLS-1$


	private boolean changed = false;

	private final OrderItemFieldValueCompositeFactory compositeFactory;

	private final String title;

	/**
	 * Factory method for create a Dialog-based editor for OrderSku field values.
	 *
	 * @param parentShell the parent shell
	 * @param orderSku order sku item
	 * @param order order
	 * @param isEditable true if item values can be edited
	 *
	 * @return the dialog
	 */
	public static OrderItemFieldValueDialog createOrderItemDataDialog(
			final Shell parentShell,
			final OrderSku orderSku,
			final Order order,
			final boolean isEditable) {

		String title =
			NLS.bind(FulfillmentMessages.get().EditItemDetails_OrderSkuTitle,
			new Object[]{orderSku.getDisplayName(), orderSku.getSkuCode()});
		
		return new OrderItemFieldValueDialog(
				parentShell, title, 
				new OrderItemFieldValueCompositeFactory.OrderSkuFieldValueAdapter(orderSku), 
				OrderItemFieldValueCompositeFactory.getContributedActions(ORDERSKU_CONTRIBUTED_ACTIONS_EXT_POINT, order, orderSku), 
				isEditable);
	}
	
	/**
	 * Constructor. 
	 * @param parentShell the parent shell
	 * @param title the text to display above the table component
	 * @param fieldValueProvider the strategy that gives access to the Item data for the Order or OrderSku
	 * @param contributedActions the pluggable actions contributed to this dialog
	 * @param editable is item values can be edited
	 */
	protected OrderItemFieldValueDialog(final Shell parentShell,
			final String title,
			final OrderItemFieldValueCompositeFactory.FieldValueAdapter fieldValueProvider,
			final List<ContributedAction> contributedActions,
			final boolean editable
			) {
		super(parentShell, 1, false);
		
		this.title = title;
		compositeFactory = new OrderItemFieldValueCompositeFactory(fieldValueProvider, contributedActions, editable);
	}
	
	@Override
	protected void bindControls() {
		compositeFactory.bindControls(new DataBindingContext());
	}
	
	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		compositeFactory.createComposite(dialogComposite.getSwtComposite());
	}

	
	@Override
	protected String getInitialMessage() {
		return null;
	}

	@Override
	protected String getTitle() {
		return title;
	}

	@Override
	protected Image getWindowImage() {
		return null;
	}

	@Override
	protected String getWindowTitle() {
		return FulfillmentMessages.get().EditItemDetails_WindowTitle;
	}

	@Override
	protected String getPluginId() {
		return FulfillmentPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return compositeFactory;
	}

	@Override
	protected void populateControls() {
		// nothing to do
	}
	
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpOkButton(parent, "OK", null); //$NON-NLS-1$
		createEpCancelButton(parent);
	}

	@Override
	protected void okPressed() {
		changed = compositeFactory.saveChanges() || changed;
		super.okPressed();
	}

	/** 
	 * @return true if field values are changed
	 */
	public boolean isChanged() {
		return changed;
	}
}
