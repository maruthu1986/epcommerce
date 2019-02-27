/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.attribute;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.helpers.IValueRetriever;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * Create input dialog window for short text attributes.
 */
public class ShortTextDialog extends AbstractEpDialog implements
		IValueRetriever {

	private final boolean editMode;

	private String value;

	private Text valueText;
	
	private boolean validationError;

	/**
	 * @param parentShell
	 *            the parent shell object to create this dialog.
	 * @param value
	 *            the string object passed to create this dialog.
	 * @param editMode true for edit mode, false for add short text values.            
	 */
	public ShortTextDialog(final Shell parentShell, final Object value, final boolean editMode) {
		super(parentShell, 2, false);
		this.value = (String) value;
		this.editMode = editMode;
	}

	/**
	 * 
	 * @param stringValue the string user input in the short text area to be validated
	 */
	public void validateAction(final String stringValue) {
		final int maxShortTextLength = 255;
		this.setErrorMessage(null);
		if (stringValue.length() > maxShortTextLength) {
			this.setErrorMessage(CatalogMessages.get().ShortTextDialog_ErrorMsg);
			validationError = true;
			return;
		} 
		if (stringValue.trim().length() == 0) {
			this.setErrorMessage(CatalogMessages.get().ShortTextDialog_RequiredMsg);
			validationError = true;
			return;
		}
		validationError = false;
	}

	@Override
	protected void bindControls() {
		// final Converter converter = StringToNumberConverter.toFloat(true);
		DataBindingContext bindingContext = new DataBindingContext();
		EpControlBindingProvider.getInstance().bind(bindingContext,
				this.valueText, null, null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(
							final IObservableValue observableValue,
							final Object value) {
						validateAction((String) value);
						ShortTextDialog.this.value = (String) value;
						return Status.OK_STATUS;
					}
				}, true);
		EpDialogSupport.create(this, bindingContext);

	}

	@Override
	protected void createEpButtonsForButtonsBar(
			final ButtonsBarType buttonsBarType, final Composite parent) {
		createEpOkButton(parent, CoreMessages.get().AbstractEpDialog_ButtonOK, null);
		createEpCancelButton(parent);
	}

	@Override
	protected void createEpDialogContent(
			final IEpLayoutComposite dialogComposite) {
		final int textAreaWidth = 350;
		final int textAreaHeight = 20;

		this.valueText = dialogComposite.addTextArea(true, false,
				EpState.EDITABLE, dialogComposite.createLayoutData(
						IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));
		((GridData) valueText.getLayoutData()).widthHint = textAreaWidth;
		((GridData) valueText.getLayoutData()).heightHint = textAreaHeight;

	}

	@Override
	protected String getInitialMessage() {
		return null;
//		return CatalogMessages.get().AttributeShortTextDialog_SetShortTextValue_Msg;
	}

	@Override
	protected String getTitle() {
		return getWindowTitle();
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	protected Image getWindowImage() {
		return null;
	}

	@Override
	protected String getWindowTitle() {
		if (editMode) {
			return CatalogMessages.get().AttributeShortTextDialog_WindowTitle;
		}
		return CatalogMessages.get().ShortTextDialog_Add_Title;
	}

	@Override
	protected void populateControls() {
		if (value == null) {
			return;
		}
		valueText.setText(value);
	}

	@Override
	public boolean isComplete() {
		return super.isComplete() && !validationError;
	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return getValue();
	}
}
