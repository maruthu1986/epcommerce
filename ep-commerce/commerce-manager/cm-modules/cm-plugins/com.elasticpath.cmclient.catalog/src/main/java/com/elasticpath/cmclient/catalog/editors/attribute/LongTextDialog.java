/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.attribute;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.helpers.IValueRetriever;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * * The dialog box for editing long text attributes.
 */
public class LongTextDialog extends AbstractEpDialog implements IValueRetriever {
	/**
	 * the text area for decimal value input.
	 */
	private Text textArea;

	/**
	 * the string value of the handled attribute.
	 */
	private String value;

	/**
	 * The constructor of the long text dialog window.
	 * 
	 * @param parentShell the parent shell object of the dialog window
	 * @param value the attribute long text string value passed in
	 */
	public LongTextDialog(final Shell parentShell, final Object value) {
		super(parentShell, 1, false);
		if (value == null) {
			this.value = ""; //$NON-NLS-1$
		} else {
			this.value = value.toString();
		}
	}

	@Override
	protected void createEpButtonsForButtonsBar(final ButtonsBarType buttonsBarType, final Composite parent) {
		createEpOkButton(parent, CoreMessages.get().AbstractEpDialog_ButtonOK, null);
		createEpCancelButton(parent);
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final int textAreaWidth = 350;
		final int textAreaHeight = 250;
		
		this.textArea = dialogComposite.addTextArea(true, false, EpState.EDITABLE, dialogComposite
				.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));

		((GridData) textArea.getLayoutData()).widthHint = textAreaWidth;
		((GridData) textArea.getLayoutData()).heightHint = textAreaHeight;
		this.textArea.setText(this.value);
	}

	@Override
	protected void okPressed() {

		this.value = this.textArea.getText().trim();
		super.okPressed();

	}

	@Override
	protected String getInitialMessage() {
		return CatalogMessages.get().AttributeLongTextDialog_SetLongTextValue_Msg;
	}

	@Override
	protected String getTitle() {
		return CatalogMessages.get().AttributeLongTextDialog_Title;
	}

	@Override
	protected String getWindowTitle() {
		return CatalogMessages.get().AttributeLongTextDialog_WindowTitle;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	protected void bindControls() {
//		do nothing here
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
		textArea.setText(value);
	}

}
