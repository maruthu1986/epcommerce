/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.order.dialog;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.cmclient.fulfillment.editors.order.OrderEventCmHelper;
import com.elasticpath.domain.order.Order;

/**
 * Add note Dialog.
 */
public class AddNoteDialog extends AbstractEpDialog {

	private final Order order;

	private Text textArea;

	private Text textField;

	private final DataBindingContext bindingContext;

	/**
	 * Constructor.
	 *
	 * @param parentShell the Shell
	 * @param order the order
	 */
	public AddNoteDialog(final Shell parentShell, final Order order) {
		super(parentShell, 2, false);
		this.order = order;
		this.bindingContext = new DataBindingContext();


	}

	@Override
	protected void bindControls() {

		final boolean hideDecorationOnFirstValidation = true;

		final ObservableUpdateValueStrategy emptyUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				return Status.OK_STATUS;
			}
		};
		EpControlBindingProvider.getInstance().bind(this.bindingContext,
				this.textArea, EpValidatorFactory.STRING_255_REQUIRED, null, emptyUpdateStrategy,
				hideDecorationOnFirstValidation);
		EpDialogSupport.create(this, this.bindingContext);
	}

	@Override
	protected String getPluginId() {
		return FulfillmentPlugin.PLUGIN_ID;
	}

	@Override
	protected void populateControls() {
		this.textArea.setFocus();
		this.textField.setText(OrderEventCmHelper.getCreatedBy(order.getModifiedBy()));

	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {

		createEpOkButton(parent, "OK", null); //$NON-NLS-1$
		createEpCancelButton(parent);

	}

	@Override
	public Object getModel() {
		return this.order;
	}
	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final EpState epStateRead = EpState.READ_ONLY;
		final EpState epStateEdit = EpState.EDITABLE;

		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);
		final IEpLayoutData fieldDataText = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		dialogComposite.addLabelBoldRequired(FulfillmentMessages.get().OrderNoteNotes_DialogLabelCreatedBy, epStateRead, labelData);
		this.textField = dialogComposite.addTextField(epStateRead, fieldData);

		dialogComposite.addLabelBoldRequired(FulfillmentMessages.get().OrderNoteNotes_DialogLabelNote, epStateEdit, labelData);

		this.textArea = dialogComposite.addTextArea(true, false, epStateEdit, fieldDataText);

	}

	@Override
	protected void okPressed() {
		if (this.textArea.getText() != null) {
			OrderEventCmHelper.getOrderEventHelper().logOrderNote(order, this.textArea.getText());
		}

		super.okPressed();

	}

	@Override
	protected String getInitialMessage() {
		return null;
	}

	@Override
	protected String getTitle() {
		return FulfillmentMessages.get().OrderNotePage_DialogTitle;
	}

	@Override
	protected String getWindowTitle() {
		return FulfillmentMessages.get().OrderNotePage_DialogTitle;
	}

	@Override
	protected Image getWindowImage() {
		return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD_NOTE);
	}

}
