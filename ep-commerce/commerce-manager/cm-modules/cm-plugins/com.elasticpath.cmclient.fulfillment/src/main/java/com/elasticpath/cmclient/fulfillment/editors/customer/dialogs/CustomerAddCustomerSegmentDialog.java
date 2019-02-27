/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.cmclient.fulfillment.editors.customer.dialogs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.service.customer.CustomerGroupService;
import com.elasticpath.service.customer.CustomerService;

/**
 * Dialog for adding a customer segment.
 */
public class CustomerAddCustomerSegmentDialog extends AbstractEpDialog {

	private static final int LAYOUT_COLUMNS = 5;
	
	private static final int DROPDOWN_WIDTH = 80;

	private final transient Customer customer;

	private transient CustomerGroup customerGroup;

	private transient CustomerService customerService;

	private transient CCombo customerSegmentCombo;

	private final transient DataBindingContext bindingContext;

	private final transient EpState editMode;

	private final transient List< CustomerGroup > customerSegmentList = new ArrayList<>();

	@Override
	protected Image getWindowImage() {
		return FulfillmentImageRegistry.CUSTOMER_SEGMENT_ICON.createImage();
	}

	/**
	 * Constructs the dialog.
	 *
	 * @param parentShell the parent Shell
	 * @param viewer the viewer containing the customer's cards
	 */
	public CustomerAddCustomerSegmentDialog(final Shell parentShell, final TableViewer viewer) {
		super(parentShell, LAYOUT_COLUMNS, false);
		customer = (Customer) viewer.getInput();
		editMode = populateCustomerSegmentList();
		bindingContext = new DataBindingContext();
	}
	
	private EpState populateCustomerSegmentList() {
		final CustomerGroupService customerGroupService =
				ServiceLocator.getService(ContextIdNames.CUSTOMER_GROUP_SERVICE);
		
		for (CustomerGroup customerGroup : customerGroupService.list()) {
			if (!customer.getCustomerGroups().contains(customerGroup)) {
				customerSegmentList.add(customerGroup);
			}
		}
		
		if (customerSegmentList.isEmpty()) {			
			return EpState.DISABLED;
		} else {
			customerSegmentList.sort(Comparator.comparing(CustomerGroup::getName));
			return EpState.EDITABLE;			
		}
	}
	
	/**
	 * Creates the button bar.
	 *
	 * @param parent utility button pane
	 */
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpButtonsForButtonsBar(ButtonsBarType.SAVE, parent);
	}

	/**
	 * Creates the dialog content.
	 *
	 * @param dialogComposite parent EP layout composite
	 */
	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final IEpLayoutData fieldDataLongCol = dialogComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, true, false, 4, 1);
		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false, 1, 1);
		dialogComposite.addLabelBoldRequired(FulfillmentMessages.get().CustomerSegmentsPage_GroupName, editMode, labelData);
		customerSegmentCombo = dialogComposite.addComboBox(editMode, fieldDataLongCol);
	}

	@Override
	protected String getPluginId() {
		return FulfillmentPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return customer;
	}

	@Override
	protected void populateControls() {
		if (!customerSegmentList.isEmpty()) {
			for (final CustomerGroup customerGroup : customerSegmentList) {
				customerSegmentCombo.add(customerGroup.getName() + buildDescription(customerGroup.getDescription()));
			}		
			customerSegmentCombo.select(0);
			setupCustomerGroup();
		}
	}
	
	private String buildDescription(final String value) {
		if (value == null) {
			return ""; //$NON-NLS-1$ 
		}
		return " - " + value.substring(0, Math.min(value.length(), DROPDOWN_WIDTH)); //$NON-NLS-1$
	}	

	/**
	 * Bind the fields to the model after controls are created.
	 */
	@Override
	protected void bindControls() {
		final boolean hideDecorationOnFirstValidation = true;

		final ObservableUpdateValueStrategy customerGroupUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				setupCustomerGroup();
				return Status.OK_STATUS;
			}
		};
			
		EpControlBindingProvider.getInstance().bind(bindingContext, customerSegmentCombo, null, null,
				customerGroupUpdateStrategy, hideDecorationOnFirstValidation);

		EpDialogSupport.create(this, bindingContext);
	}
	
	private void setupCustomerGroup() {
		customerGroup = customerSegmentList.get(customerSegmentCombo.getSelectionIndex());
	}

	/**
	 * Invoked on button pressed event. Enabled if validation passes.
	 */
	@Override
	protected void okPressed() {
		// FIXME: should disable save button instead of this check
		if (!customerSegmentList.isEmpty()) {
			if (customerService == null) {
				customerService = ServiceLocator.getService(ContextIdNames.CUSTOMER_SERVICE);
			}
			customer.addCustomerGroup(customerGroup);
			super.okPressed();
		}
	}

	/**
	 * Returns the title of this dialog.
	 *
	 * @return String
	 */
	@Override
	protected String getTitle() {
		return FulfillmentMessages.get().CustomerSegmentsPageDialog_AddTitle;
	}

	/**
	 * Returns the window title of this dialog.
	 *
	 * @return String
	 */
	@Override
	protected String getWindowTitle() {
		return FulfillmentMessages.get().CustomerSegmentsPageDialog_AddWindowTitle;
	}

	/**
	 * Returns the initial description message.
	 *
	 * @return String
	 */
	@Override
	protected String getInitialMessage() {
		return null;
	}
}
