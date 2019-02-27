/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.cmclient.admin.customers.views;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.admin.customers.AdminCustomersMessages;
import com.elasticpath.cmclient.admin.customers.AdminCustomersPlugin;
import com.elasticpath.cmclient.admin.customers.event.CustomerSegmentEventService;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.domain.customer.CustomerRole;
import com.elasticpath.service.customer.CustomerGroupService;

/**
 * Customer segment editor.
 */
public class CustomerSegmentEditor extends AbstractCmClientFormEditor {

	/**
	 * Constant used for matching the right event.
	 */
	protected static final int UPDATE_TOOLBAR = 301;

	/**
	 * ID of the editor. It is the same as the class name.
	 */
	public static final String ID_EDITOR = CustomerSegmentEditor.class.getName();

	private static final Logger LOG = Logger.getLogger(CustomerSegmentEditor.class);

	private static final int TOTAL_WORK_UNITS = 2;

	private CustomerGroupService customerGroupService;

	private CustomerGroup customerGroup;

	/**
	 * Creates a multi-page editor.
	 */
	public CustomerSegmentEditor() {
		super();
	}

	@Override
	protected void addPages() {
		try {
			addPage(new CustomerSegmentSummaryPage(this));
			addExtensionPages(getClass().getSimpleName(), AdminCustomersPlugin.PLUGIN_ID);
		} catch (final PartInitException e) {
			LOG.error("Can not create pages for the Customer Segment editor", e); //$NON-NLS-1$
		}
	}

	@Override
	public CustomerGroup getModel() {
		return customerGroup;
	}

	@Override
	public void initEditor(final IEditorSite site, final IEditorInput input) throws PartInitException {
		customerGroupService = ServiceLocator.getService(ContextIdNames.CUSTOMER_GROUP_SERVICE);

		final Long uid = input.getAdapter(Long.class);
		if (uid > 0) {
			// existing group
			customerGroup = customerGroupService.load(uid);
		} else {
			// new group
			customerGroup = ServiceLocator.getService(ContextIdNames.CUSTOMER_GROUP);
			customerGroup.setCustomerRoles(defineCustomerRoles());
		}
	}

	private Set<CustomerRole> defineCustomerRoles() {
		final CustomerRole defaultCustomerRole = ServiceLocator.getService(ContextIdNames.CUSTOMER_ROLE);
		defaultCustomerRole.setAuthority(WebConstants.ROLE_CUSTOMER);
		return new HashSet<>(Arrays.asList(defaultCustomerRole));
	}

	@Override
	protected void saveModel(final IProgressMonitor monitor) {
		final boolean editing = customerGroup.isPersisted();
		final boolean creating = !editing;

		if (creating && customerGroupService.groupExists(customerGroup.getName())) {
			MessageDialog.openWarning(getSite().getShell(),
				AdminCustomersMessages.get().CustomerSegmentExists,

					NLS.bind(AdminCustomersMessages.get().CustomerSegmentExistsWithName,
					customerGroup.getName()));
			return;
		}

		if (editing && !customerGroupService.groupExists(customerGroup.getName())) {
			MessageDialog.openWarning(getSite().getShell(),
					AdminCustomersMessages.get().CustomerSegmentNoLongerExists,

					NLS.bind(AdminCustomersMessages.get().CustomerSegmentNoLongerExists,
					customerGroup.getName()));
			return;
		}

		monitor.beginTask(AdminCustomersMessages.get().CustomerSegmentEditor_SaveTaskName, TOTAL_WORK_UNITS);
		try {
			final long startTime = System.currentTimeMillis();
			if (LOG.isDebugEnabled()) {
				LOG.debug("CustomerSegment start saving..."); //$NON-NLS-1$
			}
			monitor.worked(1);

			if (editing) {
				customerGroup = customerGroupService.update(customerGroup);
			} else {
				customerGroup = customerGroupService.add(customerGroup);
			}

			if (LOG.isDebugEnabled()) {
				LOG.debug("CustomerSegment saved for " + (System.currentTimeMillis() - startTime) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			fireUpdateActions(creating);
			monitor.worked(1);
		} finally {
			monitor.done();
		}
	}

	private void fireUpdateActions(final boolean isNew) {
		refreshAllDataBindings();

		CustomerSegmentEventService.getInstance().fireCustomerSegmentChangeEvent(
				new ItemChangeEvent<>(this, customerGroup, eventType(isNew)));

		CustomerSegmentEditorInput input = (CustomerSegmentEditorInput) getEditorInput();
		input.setUid(customerGroup.getUidPk());
		input.setName(customerGroup.getName());
		input.setToolTipText(AdminCustomersMessages.get().CreateCustomerSegment);

		firePropertyChange(UPDATE_TOOLBAR);
	}

	private EventType eventType(final boolean isNew) {
		if (isNew) {
			return EventType.ADD;
		}
		return EventType.CHANGE;
	}

	@Override
	public void reloadModel() {
		customerGroup = customerGroupService.load(customerGroup.getUidPk());
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return Collections.emptyList();
	}

	@Override
	public Locale getDefaultLocale() {
		return null;
	}

	@Override
	protected String getSaveOnCloseMessage() {
		return
			NLS.bind(AdminCustomersMessages.get().CustomerSegmentEditor_OnSavePrompt,
			getEditorName());
	}

	@Override
	protected String getEditorName() {
		return getEditorInput().getName();
	}

}
