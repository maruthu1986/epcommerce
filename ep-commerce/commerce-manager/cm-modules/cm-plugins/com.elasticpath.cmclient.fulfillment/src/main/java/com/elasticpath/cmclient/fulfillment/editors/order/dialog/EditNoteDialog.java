/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.order.dialog;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.cmclient.fulfillment.editors.order.OrderEventCmHelper;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderEvent;

/**
 * Represents the UI for edit note dialog.
 */
public class EditNoteDialog extends AbstractEpDialog {

	private final OrderEvent orderEvent;
	
	private final OrderEvent orderNoteProxy;

	private Text textArea;

	private Text textField;

	private final DataBindingContext bindingContext;

	private final boolean viewOnly;

	/**
	 * Constructor.
	 *
	 * @param parentShell the Shell
	 * @param orderEvent the OrderNote
	 * @param viewOnly the view only state
	 */
	public EditNoteDialog(final Shell parentShell, final OrderEvent orderEvent, final boolean viewOnly) {
		super(parentShell, 2, false);
		this.orderEvent = orderEvent;
		this.orderNoteProxy = ServiceLocator.getService(ContextIdNames.ORDER_EVENT);
		orderNoteProxy.setNote(orderEvent.getNote());

		this.bindingContext = new DataBindingContext();
		this.viewOnly = viewOnly;
	}

	@Override
	// ---- DOCbindControls
	protected void bindControls() {
		final boolean hideDecorationOnFirstValidation = true;
		EpControlBindingProvider.getInstance().bind(this.bindingContext, this.textArea, this.orderNoteProxy, "note", //$NON-NLS-1$
				EpValidatorFactory.STRING_255_REQUIRED, null, hideDecorationOnFirstValidation);
		EpDialogSupport.create(this, this.bindingContext);
	}
	// ---- DOCbindControls

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {

		createEpOkButton(parent, "OK", null); //$NON-NLS-1$
		createEpCancelButton(parent);

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

		if (viewOnly) {
			dialogComposite.addLabelBoldRequired(FulfillmentMessages.get().OrderNoteNotes_DialogLabelNote, epStateRead, labelData);
			this.textArea = dialogComposite.addTextArea(false, false, epStateRead, fieldDataText);
		} else {
			dialogComposite.addLabelBoldRequired(FulfillmentMessages.get().OrderNoteNotes_DialogLabelNote, epStateEdit, labelData);
			this.textArea = dialogComposite.addTextArea(true, false, epStateEdit, fieldDataText);
		}
	}

	@Override
	protected String getPluginId() {
		return FulfillmentPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return orderEvent;
	}

	@Override
	protected void populateControls() {
		this.textArea.setFocus();
		this.textField.setText(OrderEventCmHelper.getCreatedBy(orderEvent));
		this.textArea.setText(this.orderEvent.getNote());
	}

	@Override
	protected void okPressed() {
		orderEvent.setNote(orderNoteProxy.getNote());
		super.okPressed();
	}

	@Override
	protected String getInitialMessage() {
		return null;
	}

	@Override
	protected String getTitle() {
		return FulfillmentMessages.get().OrderNoteNotes_DialogTitleOpen;
	}

	@Override
	protected String getWindowTitle() {
		return FulfillmentMessages.get().OrderNoteNotes_DialogTitleOpen;
	}

	@Override
	protected Image getWindowImage() {
		return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_OPEN);
	}

}
