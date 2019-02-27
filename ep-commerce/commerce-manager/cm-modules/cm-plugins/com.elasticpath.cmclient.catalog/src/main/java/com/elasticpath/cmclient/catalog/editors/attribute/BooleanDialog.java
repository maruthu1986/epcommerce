/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.attribute;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.helpers.IValueRetriever;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * The dialog box for editing boolean attributes.
 */
public class BooleanDialog extends AbstractEpDialog implements IValueRetriever {

	/**
	 * the boolean value of the handled attribute.
	 */
	private Boolean value;

	/**
	 * The Combo box for boolean value editing, toggling Yes/No.
	 */
	private CCombo valueCombo;

	/**
	 * The constructor of the boolean dialog window.
	 * 
	 * @param parentShell the parent shell object of the dialog window
	 * @param value the attribute boolean value passed in
	 */
	public BooleanDialog(final Shell parentShell, final Object value) {
		super(parentShell, 2, false);
		this.value = (Boolean) value;
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
//		dialogComposite.addLabelBold(CatalogMessages.get().AttributeBooleanDialog_Value, dialogComposite.createLayoutData(IEpLayoutData.FILL,
//				IEpLayoutData.CENTER));
		this.valueCombo = dialogComposite.addComboBox(EpState.EDITABLE, dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER,
				true, true));
	}

	@Override
	protected void createEpButtonsForButtonsBar(final ButtonsBarType buttonsBarType, final Composite parent) {
		createEpOkButton(parent, CoreMessages.get().AbstractEpDialog_ButtonOK, null);
		createEpCancelButton(parent);
	}
	
	
	@Override
	protected String getInitialMessage() {
		return null;
//		return CatalogMessages.get().AttributeBooleanDialog_SetBooleanValue_Msg;
	}

	@Override
	protected String getTitle() {
		return CatalogMessages.get().AttributeBooleanDialog_Title;
	}

	@Override
	protected String getWindowTitle() {
		return CatalogMessages.get().AttributeBooleanDialog_WindowTitle;
	}

	@Override
	public Boolean getValue() {
		return this.value;
	}

	@Override
	protected void bindControls() {
		// do nothing here.
	}

	@Override
	protected Image getWindowImage() {
		// do nothing here
		return null;
	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return getValue();
	}

	@Override
	protected void populateControls() {
		this.valueCombo
				.setItems(CatalogMessages.get().ProductEditorAttributeSection_Yes, CatalogMessages.get().ProductEditorAttributeSection_No);
		if (this.value == null) {
			valueCombo.setText(CatalogMessages.get().ProductEditorAttributeSection_Yes);
			return;
		}
		if (this.value) {
			valueCombo.setText(CatalogMessages.get().ProductEditorAttributeSection_Yes);
		} else {
			valueCombo.setText(CatalogMessages.get().ProductEditorAttributeSection_No);
		}
	}

	@Override
	protected void okPressed() {
		if (this.valueCombo.getSelectionIndex() == 0) {
			// Yes
			this.value = Boolean.TRUE;
		} else {
			// No
			this.value = Boolean.FALSE;
		}
		
		super.okPressed();
	}
}
