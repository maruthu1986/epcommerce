/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.core.dialog.value.dialog;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.helpers.IValueRetriever;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * Create input dialog window for short text values.
 */
public class ShortTextDialog extends AbstractValueDialog<String> implements IValueRetriever {

	private static final int MAX_TEXT_LENGTH = 255;

	private Text valueText;

	private static final int TEXT_AREA_WIDTH = 350;

	private static final int TEXT_AREA_HEIGHT = 60;

	/**
	 * @param parentShell the parent shell object to create this dialog.
	 * @param value the string value passed to create this dialog.
	 * @param editMode true for edit mode, false for add short text values.
	 * @param valueRequired true if value is required, false is value is not required
	 */
	public ShortTextDialog(final Shell parentShell, final String value, final boolean editMode, final boolean valueRequired) {
		super(parentShell, value, editMode, valueRequired);
	}

	/**
	 * @param parentShell the parent shell object to create this dialog.
	 * @param value the string value passed to create this dialog.
	 * @param editMode true for edit mode, false for add short text values.
	 * @param valueRequired true if value is required, false is value is not required
	 * @param label the label for this value
	 * @param isLabelBold true if label needs to be bold, false for normal font labels
	 */
	public ShortTextDialog(final Shell parentShell, final String value, final boolean editMode,
			final boolean valueRequired, final String label, final boolean isLabelBold) {
		super(parentShell, value, editMode, valueRequired, label, isLabelBold);
	}

	/**
	 * @param stringValue the string user input in the short text area to be validated
	 */
	public void validateAction(final String stringValue) {
		this.setErrorMessage(null);
		if (StringUtils.isEmpty(stringValue) && isValueRequired()) {
			this.setErrorMessage(CoreMessages.get().EpValidatorFactory_ValueRequired);
			getOkButton().setEnabled(false);
			return;
		}
		if (stringValue.trim().length() > getMaxTextLength()) {
			this.setErrorMessage(
				NLS.bind(CoreMessages.get().EpValidatorFactory_MaxCharLength,
				getMaxTextLength()));
			getOkButton().setEnabled(false);
			return;
		}
		getOkButton().setEnabled(true);
	}

	@Override
	protected void bindControls() {
		// final Converter converter = StringToNumberConverter.toFloat(true);
		DataBindingContext bindingContext = new DataBindingContext();
		EpControlBindingProvider.getInstance().bind(bindingContext, getValueText(), null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				validateAction((String) value);
				setValue((String) value);
				return Status.OK_STATUS;
			}
		}, true);
		EpDialogSupport.create(this, bindingContext);

	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		super.createEpDialogContent(dialogComposite); // default label from adapter
		this.valueText = dialogComposite.addTextArea(hasVerticalScroll(), 
				false, EpState.EDITABLE, dialogComposite.createLayoutData(IEpLayoutData.FILL,
				IEpLayoutData.CENTER, true, false));
		((GridData) valueText.getLayoutData()).widthHint = getTextAreaWidth();
		((GridData) valueText.getLayoutData()).heightHint = getTextAreaHeight();
	}

	/**
	 * Defines if text area has vertical scroll.
	 * 
	 * @return true if text area has vertical scroll
	 */
	protected boolean hasVerticalScroll() {
		return true;
	}

	@Override
	protected void setValue(final String value) {
		if (StringUtils.isEmpty(value)) {
			super.setValue(StringUtils.EMPTY);
		} else {
			super.setValue(value);
		}
	}
	
	@Override
	public String getValue() {
		if (StringUtils.isEmpty(super.getValue())) {
			return StringUtils.EMPTY;
		}
		return super.getValue();
	}
	
	@Override
	protected String getEditTitle() {
		return CoreMessages.get().ShortTextDialog_EditTitle;
	}

	@Override
	protected String getAddTitle() {
		return CoreMessages.get().ShortTextDialog_AddTitle;
	}

	@Override
	protected String getEditWindowTitle() {
		return CoreMessages.get().ShortTextDialog_EditWindowTitle;
	}

	@Override
	protected String getAddWindowTitle() {
		return CoreMessages.get().ShortTextDialog_AddWindowTitle;
	}

	@Override
	protected void populateControls() {
		valueText.setText(getValue());
	}
	
	/**
	 * Returns max length of the text in the dialog box.
	 *  
	 * @return max length of the text in the dialog box
	 */
	protected int getMaxTextLength() {
		return MAX_TEXT_LENGTH;
	}

	/**
	 * Returns dialog edit area width.
	 * @return dialog edit area width
	 */
	protected int getTextAreaWidth() {
		return TEXT_AREA_WIDTH;
	}

	/**
	 * Returns dialog edit area height.
	 * @return dialog edit area height
	 */
	protected int getTextAreaHeight() {
		return TEXT_AREA_HEIGHT;
	}

	/**
	 * Returns control of dialog edit area.
	 * @return control of dialog edit area
	 */
	protected Text getValueText() {
		return valueText;
	}
}
