/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.customer;

import java.util.Collection;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPermissions;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.cmclient.fulfillment.event.FulfillmentEventService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.store.StoreService;

/**
 * Customer details editor.
 */
public class CustomerDetailsEditor extends AbstractCmClientFormEditor {

	private static final int TOTAL_WORK_UNITS = 3;

	/**
	 * Constant used for matching the right event.
	 */
	static final int UPDATE_TOOLBAR = 301;

	/**
	 * ID of the editor. It is the same as the class name.
	 */
	public static final String ID_EDITOR = CustomerDetailsEditor.class.getName();

	private static final Logger LOG = Logger.getLogger(CustomerDetailsEditor.class);

	private CustomerService customerService;

	private StoreService storeService;

	private Customer customer;

	private Store store;

	/**
	 * Creates a multi-page editor.
	 */
	public CustomerDetailsEditor() {
		super();
	}

	@Override
	protected void addPages() {
		try {
			this.addPage(new CustomerDetailsProfilePage(this));
			this.addPage(new CustomerDetailsAdressesPage(this));
			this.addPage(new CustomerDetailsOrdersPage(this));
			// Only show the customer segments tab if the user has permission.
			if (AuthorizationService.getInstance().isAuthorizedWithPermission(FulfillmentPermissions.ASSIGN_CUSTOMER_SEGMENTS)) {
				this.addPage(new CustomerDetailsCustomerSegmentsPage(this));
			}

			if (AuthorizationService.getInstance().isAuthorizedWithPermission(FulfillmentPermissions.DATA_POLICIES_MANAGE)) {
				this.addPage(new CustomerDetailsDataPoliciesPage(this));
			}

			addExtensionPages(getClass().getSimpleName(), FulfillmentPlugin.PLUGIN_ID);
		} catch (final PartInitException e) {
			LOG.error("Can not create pages for the Customer editor", e); //$NON-NLS-1$
		}
	}

	@Override
	public Customer getModel() {
		return this.customer;
	}

	@Override
	public void initEditor(final IEditorSite site, final IEditorInput input) throws PartInitException {
		this.customer = input.getAdapter(Customer.class);
		this.customerService = ServiceLocator.getService(ContextIdNames.CUSTOMER_SERVICE);
		storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);
		store = storeService.findStoreWithCode(customer.getStoreCode());
	}

	@Override
	protected void saveModel(final IProgressMonitor monitor) {
		monitor.beginTask(FulfillmentMessages.get().CustomerDetails_SaveTaskName, TOTAL_WORK_UNITS);
		try {
			final long startTime = System.currentTimeMillis();
			if (LOG.isDebugEnabled()) {
				LOG.debug("Customer start saving..."); //$NON-NLS-1$
			}
			monitor.worked(1);
			this.customerService.update(customer);
			customer = this.customerService.get(customer.getUidPk());

			if (LOG.isDebugEnabled()) {
				LOG.debug("Customer saved for " + (System.currentTimeMillis() - startTime) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			monitor.worked(1);
			FulfillmentEventService.getInstance().fireCustomerChangeEvent(new ItemChangeEvent<>(this, customer));
			fireUpdateActions();
			refreshEditorPages();
		} finally {
			monitor.done();
		}
	}

	/**
	 * Triggers a toolbar update.
	 */
	public void fireUpdateActions() {
		firePropertyChange(UPDATE_TOOLBAR);
	}

	@Override
	public void reloadModel() {
		customer = customerService.get(customer.getUidPk());
		store = storeService.findStoreWithCode(customer.getStoreCode());
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return store.getSupportedLocales();
	}

	@Override
	public Locale getDefaultLocale() {
		return store.getDefaultLocale();
	}

	@Override
	protected String getSaveOnCloseMessage() {
		return
			NLS.bind(FulfillmentMessages.get().CustomerDetailsEditor_OnSavePrompt,
			getEditorName());
	}

	@Override
	protected String getEditorName() {
		return getEditorInput().getName();
	}

}
