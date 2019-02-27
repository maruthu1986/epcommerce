/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.cmclient.fulfillment.dialogs;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPermissions;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;

/**
 * A dialog for re-sending an RMA email to the customer.
 */
public class ResendRMAEmailDialog extends AbstractEpDialog {
	
	private Text recipientText;
	
	private String recipientEmail;

	/**
	 * Constructs a new instance.
	 * 
	 * @param parentShell the parent shell 
	 * @param recipientEmail the RMA email address
	 */
	public ResendRMAEmailDialog(final Shell parentShell, final String recipientEmail) {
		super(parentShell, 2, false);
		this.recipientEmail = recipientEmail;
	}

	@Override
	protected void bindControls() {
		final DataBindingContext context = new DataBindingContext();
		final ObservableUpdateValueStrategy contextUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				String email = recipientText.getText();
				IStatus status = EpValidatorFactory.EMAIL_REQUIRED.validate(email);
				getOkButton().setEnabled(status.isOK());	
				recipientEmail = email;
				return status;
				
			}
		};
	
		EpControlBindingProvider.getInstance().bind(context, recipientText, null, 
				null, contextUpdateStrategy, true);

	}
	
	

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final EpState epState = getRecipientTextState();
		
		dialogComposite.addLabelBoldRequired(FulfillmentMessages.get().ResendRMAEmailDialog_Recipient, epState, null);
		recipientText = dialogComposite.addTextField(epState, null);
		
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		recipientText.setLayoutData(layoutData);
	}

	private EpState getRecipientTextState() {
		EpState epState = EpState.DISABLED;	
		if (isAuthorized()) {
			epState = EpState.EDITABLE;
		}
		return epState;
	}

	private boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(FulfillmentPermissions.CREATE_EDIT_RETURNS);
	}

	
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpOkButton(parent, FulfillmentMessages.get().ResendRMAEmailDialog_Send, null);
		createEpCancelButton(parent);
	}
	
	@Override
	protected String getInitialMessage() {
		return FulfillmentMessages.get().ResendRMAEmailDialog_InitialMessage;
	}

	@Override
	protected String getTitle() {
		return FulfillmentMessages.get().ResendRMAEmailDialog_Title;
	}

	@Override
	protected Image getWindowImage() {
		return FulfillmentImageRegistry.getImage(FulfillmentImageRegistry.IMAGE_RESEND_RMA_EMAIL);
	}

	@Override
	protected String getWindowTitle() {
		return FulfillmentMessages.get().ResendRMAEmailDialog_WindowTitle;
	}

	@Override
	protected String getPluginId() {
		return FulfillmentPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return getRecipientEmail();
	}

	@Override
	protected void populateControls() {
		recipientText.setText(recipientEmail);
	}

	/**
	 * @return the recipientEmail
	 */
	public String getRecipientEmail() {
		return recipientEmail;
	}

	/**
	 * @param recipientEmail the recipientEmail to set
	 */
	public void setRecipientEmail(final String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}

	


	
	
}