/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.dialog;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;

/**
 * * The dialog box for editing decimal values (BigDecimal).
 */
public class DecimalDialog extends AbstractValueDialog<BigDecimal> implements
		IValueRetriever {
	/**
	 * A logger for this class.
	 */
	protected static final Logger LOG = Logger.getLogger(DecimalDialog.class);

	/**
	 * the text area for decimal value input.
	 */
	private Text textField;

	/**
	 * The constructor of the boolean dialog window.
	 * 
	 * @param parentShell
	 *            the parent shell object of the dialog window
	 * @param value
	 *            the decimal value passed in
	 * @param editMode
	 *            true for edit mode, false for add short text values.
	 * @param valueRequired
	 *            true if value is required, false is value is not required
	 */
	public DecimalDialog(final Shell parentShell, final BigDecimal value,
			final boolean editMode, final boolean valueRequired) {
		super(parentShell, value, editMode, valueRequired);
	}

	/**
	 * The constructor of the boolean dialog window.
	 * 
	 * @param parentShell
	 *            the parent shell object of the dialog window
	 * @param value
	 *            the decimal value passed in
	 * @param editMode
	 *            true for edit mode, false for add short text values.
	 * @param valueRequired
	 *            true if value is required, false is value is not required
	 * @param label
	 *            the label for this value
	 * @param isLabelBold
	 *            true if label needs to be bold, false for normal font labels
	 */
	public DecimalDialog(final Shell parentShell, final BigDecimal value,
			final boolean editMode, final boolean valueRequired,
			final String label, final boolean isLabelBold) {
		super(parentShell, value, editMode, valueRequired, label, isLabelBold);
	}

	@Override
	protected void createEpDialogContent(
			final IEpLayoutComposite dialogComposite) {
		super.createEpDialogContent(dialogComposite); // default label from
														// adapter
		this.textField = dialogComposite.addTextField(EpState.EDITABLE,
				dialogComposite.createLayoutData(IEpLayoutData.FILL,
						IEpLayoutData.CENTER, true, true));
	}

	@Override
	protected String getEditTitle() {
		return CoreMessages.get().DecimalDialog_EditTitle;
	}

	@Override
	protected String getAddTitle() {
		return CoreMessages.get().DecimalDialog_AddTitle;
	}

	@Override
	protected String getEditWindowTitle() {
		return CoreMessages.get().DecimalDialog_EditWindowTitle;
	}

	@Override
	protected String getAddWindowTitle() {
		return CoreMessages.get().DecimalDialog_AddWindowTitle;
	}

	@Override
	protected void bindControls() {
		DataBindingContext bindingContext = new DataBindingContext();
		IValidator validator = EpValidatorFactory.BIG_DECIMAL;
		if (isValueRequired()) {
			validator = EpValidatorFactory.BIG_DECIMAL_REQUIRED;
		}
		EpControlBindingProvider.getInstance().bind(bindingContext,
				this.textField, validator, null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(
							final IObservableValue observableValue,
							final Object value) {
						try {
							String valuestr = (String) value;
							if (StringUtils.isNotEmpty(valuestr)) {
								DecimalDialog.this.setValue(new BigDecimal(
										(valuestr).trim()));
							}
							getOkButton().setEnabled(true);
						} catch (NumberFormatException e) {
							getOkButton().setEnabled(false);
							if (LOG.isDebugEnabled()) {
								LOG
										.debug("The input at decimal field is not a decimal number"); //$NON-NLS-1$
							}
						}
						return Status.OK_STATUS;
					}
				}, true);

		EpDialogSupport.create(this, bindingContext);
	}

	@Override
	protected void populateControls() {
		if (this.getValue() != null) {
			this.textField.setText(this.getValue().toPlainString());
		}

	}

	@Override
	protected void okPressed() {
		setValue(new BigDecimal(textField.getText()));
		super.okPressed();
	}
}
