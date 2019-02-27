/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.editors.order.actions.ContributedAction;
import com.elasticpath.cmclient.fulfillment.editors.order.dialog.OrderItemFieldValueCompositeFactory;
import com.elasticpath.cmclient.fulfillment.editors.order.dialog.OrderItemFieldValueCompositeFactory.FieldValueAdapter;
import com.elasticpath.domain.order.Order;

/**
 * Details sub-section that displays the order data attached to an order.
 */
public class OrderDetailsOrderDataSectionPart extends AbstractCmClientEditorPageSectionPart {
	
	/** ContributedAction extension point ID for order data dialogs. **/
	public static final String ORDER_CONTRIBUTED_ACTIONS_EXT_POINT = 
		"com.elasticpath.cmclient.fulfillment.order.contributedactions"; //$NON-NLS-1$
	
	private final OrderItemFieldValueCompositeFactory compositeFactory;

	/**
	 * Constructor.
	 * 
	 * @param formPage the formpage
	 * @param editor the CmClientFormEditor that contains the form
	 */
	public OrderDetailsOrderDataSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
	
		Order order = (Order) editor.getModel();
		FieldValueAdapter fieldValueProvider = new OrderItemFieldValueCompositeFactory.OrderDataFieldValueAdapter(order);
		List<ContributedAction> contributedActions = 
			OrderItemFieldValueCompositeFactory.getContributedActions(ORDER_CONTRIBUTED_ACTIONS_EXT_POINT, order, null);
		
		final boolean canEdit = true;
		compositeFactory = new OrderItemFieldValueCompositeFactory(fieldValueProvider, contributedActions, canEdit);
	}
	
	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		compositeFactory.createComposite(client);
	}

	@Override
	protected void populateControls() {
		// Do Nothing
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		compositeFactory.bindControls(bindingContext);
		compositeFactory.addControlModificationListener(getEditor());
		compositeFactory.addControlModificationListener(compositeFactory::saveChanges);
	}

	@Override
	protected String getSectionTitle() {
		return FulfillmentMessages.get().OrderDataSection_Title;
	}
}
