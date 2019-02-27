/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.customer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPermissions;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.settings.SettingsService;

/**
 * Represents the UI of the customer details profile page.
 */
public class CustomerDetailsProfilePage extends AbstractCmClientEditorPage implements IPropertyListener {

	/**
	 * This page ID.
	 */
	public static final String PAGE_ID = "CustomerDetailsProfilerPage"; //$NON-NLS-1$

	private Action changePasswordAction;

	/**
	 * Constructor.
	 * 
	 * @param editor <code>FormEditor</code>
	 */
	public CustomerDetailsProfilePage(final AbstractCmClientFormEditor editor) {
		super(editor, CustomerDetailsProfilePage.PAGE_ID, FulfillmentMessages.get().ProfilePage_Title);
		addPropertyListener(this);
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		// Create the sections
		managedForm.addPart(new CustomerDetailsProfileBasicSection(this, editor));
		managedForm.addPart(new CustomerDetailsProfileRegistrationSection(this, editor));
		managedForm.addPart(new CustomerDetailsProfileAttributesSection(this, editor));
		addExtensionEditorSections(editor, managedForm, FulfillmentPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	@Override
	protected int getFormColumnsCount() {
		return 2;
	}

	@Override
	protected String getFormTitle() {
		return FulfillmentMessages.get().ProfilePage_Form_Title;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		changePasswordAction = new Action(FulfillmentMessages.get().CustomerDetails_ChangePassword) {
			@Override
			public void run() {
				updateCustomerPassword();
			}
		};

		updateActionsState();

		changePasswordAction.setImageDescriptor(FulfillmentImageRegistry.ICON_CUSTOMER_EDIT_PASSWORD);

		final ActionContributionItem changePasswordContributionItem = new ActionContributionItem(changePasswordAction);
		changePasswordContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		toolBarManager.add(changePasswordContributionItem);
		toolBarManager.update(true);
	}

	private void updateActionsState() {
		final Customer customer = (Customer) ((AbstractCmClientFormEditor) getEditor()).getModel();

		final AuthorizationService authorizationService = AuthorizationService.getInstance();

		final boolean authorized =
				authorizationService.isAuthorizedWithPermission(FulfillmentPermissions.CUSTOMER_EDIT)
						&& authorizationService.isAuthorizedForStore(customer.getStoreCode());

		final boolean isRegistered = customer.isRegistered();
		changePasswordAction.setEnabled(authorized && isRegistered);
	}

	private void updateCustomerPassword() {
		final boolean resetPassword = MessageDialog.openConfirm(getSite().getShell(), 
				FulfillmentMessages.get().CustomerDetailsPage_ResetPassDialogTitle,
				FulfillmentMessages.get().CustomerDetailsPage_ResetPassDialogQuestion);
		if (resetPassword) {
			final Customer customer = (Customer) ((AbstractCmClientFormEditor) getEditor()).getModel();
			final CustomerService customerService =
					ServiceLocator.getService(ContextIdNames.CUSTOMER_SERVICE);
			if (getUserIdMode() == WebConstants.GENERATE_UNIQUE_PERMANENT_USER_ID_MODE) {
				customerService.resetPassword(customer.getEmail(), customer.getStoreCode());	//Weblogic style login using email
			} else {
				customerService.resetPassword(customer.getUserId(), customer.getStoreCode());
			}
			MessageDialog.openInformation(getSite().getShell(), 
					FulfillmentMessages.get().CustomerDetailsPage_ResetPassInfoTitle,
					FulfillmentMessages.get().CustomerDetailsPage_ResetPassInfoMessage);
		}
	}

	private int getUserIdMode() {
		return Integer.parseInt(((SettingsService) ServiceLocator.getService(ContextIdNames.SETTINGS_SERVICE))
						.getSettingValue("COMMERCE/SYSTEM/userIdMode").getValue());  //$NON-NLS-1$
	}

	@Override
	public void propertyChanged(final Object source, final int propId) {
		if (propId == CustomerDetailsEditor.UPDATE_TOOLBAR) {
			updateActionsState();
			getManagedForm().getForm().getToolBarManager().update(true);
		}
	}

}
