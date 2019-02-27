/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.attribute;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.helpers.IValueRetriever;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * * The dialog box for editing integer attributes.
 */
public class IntegerDialog extends AbstractEpDialog implements IValueRetriever {
	/**
	 * the spinner for integer value input.
	 */
	private Spinner spinnerField;

	private static final int MAX_INTEGER_ALLOWED = 1000000000;

	/**
	 * the integer value of the handled attribute.
	 */
	private int value;

	/**
	 * The constructor of the integer dialog window.
	 * 
	 * @param parentShell the parent shell object of the dialog window
	 * @param value the attribute integer value passed in
	 */
	public IntegerDialog(final Shell parentShell, final Object value) {
		super(parentShell, 2, false);
		if (value == null) {
			this.value = 0;
		} else {
			this.value = (Integer) value;
		}
	}

	@Override
	protected void createEpButtonsForButtonsBar(final ButtonsBarType buttonsBarType, final Composite parent) {
		createEpOkButton(parent, CoreMessages.get().AbstractEpDialog_ButtonOK, null);
		createEpCancelButton(parent);
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
//		dialogComposite.addLabelBold(CatalogMessages.get().AttributeIntegerDialog_Value, dialogComposite.createLayoutData(IEpLayoutData.FILL,
//				IEpLayoutData.CENTER));
		this.spinnerField = dialogComposite.addSpinnerField(EpState.EDITABLE, dialogComposite.createLayoutData(IEpLayoutData.FILL,
				IEpLayoutData.CENTER, true, true));
		this.spinnerField.setMaximum(MAX_INTEGER_ALLOWED);
	}

	@Override
	protected void okPressed() {

		this.value = spinnerField.getSelection();
		super.okPressed();

	}

	@Override
	protected String getInitialMessage() {
		return null;
//		return CatalogMessages.get().AttributeIntegerDialog_SetIntegerValue_Msg;
	}

	@Override
	protected String getTitle() {
		return CatalogMessages.get().AttributeIntegerDialog_Title;
	}

	@Override
	protected String getWindowTitle() {
		return CatalogMessages.get().AttributeIntegerDialog_WindowTitle;
	}

	@Override
	public Integer getValue() {
		return this.value;
	}

	@Override
	protected void bindControls() {
		// not used
	}

	@Override
	protected Image getWindowImage() {
		return null;
	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return value;
	}

	@Override
	protected void populateControls() {
		this.spinnerField.setSelection(value);
	}
}
