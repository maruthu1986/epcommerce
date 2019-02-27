/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.attribute;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;
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
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;

/**
 * * The dialog box for editing decimal attributes.
 */
public class DecimalDialog extends AbstractEpDialog implements IValueRetriever {
	/**
	 * A logger for this class.
	 */
	protected static final Logger LOG = Logger.getLogger(DecimalDialog.class);

	/**
	 * the text area for decimal value input.
	 */
	private Text textField;

	/**
	 * the decimal value of the handled attribute.
	 */
	private BigDecimal value;

	/**
	 * The constructor of the boolean dialog window.
	 * 
	 * @param parentShell
	 *            the parent shell object of the dialog window
	 * @param value
	 *            the attribute decimal value passed in
	 */
	public DecimalDialog(final Shell parentShell, final Object value) {
		super(parentShell, 2, false);
			this.value = (BigDecimal) value;
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
		this.textField = dialogComposite.addTextField(EpState.EDITABLE,
				dialogComposite.createLayoutData(IEpLayoutData.FILL,
						IEpLayoutData.CENTER, true, true));
	}

	@Override
	protected String getInitialMessage() {
		return null;
//		return CatalogMessages.get().AttributeDecimalDialog_SetDecimalValue_Msg;
	}

	@Override
	protected String getTitle() {
		return CatalogMessages.get().AttributeDecimalDialog_Title;
	}

	@Override
	protected String getWindowTitle() {
		return CatalogMessages.get().AttributeDecimalDialog_WindowTitle;
	}

	@Override
	public BigDecimal getValue() {
		return this.value;
	}

	@Override
	protected void bindControls() {
		DataBindingContext bindingContext = new DataBindingContext();
		EpControlBindingProvider.getInstance().bind(bindingContext,
				this.textField, EpValidatorFactory.BIG_DECIMAL_REQUIRED, null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(
							final IObservableValue observableValue,
							final Object value) {

						try {
							DecimalDialog.this.value = new BigDecimal(
									((String) value).trim());
						} catch (NumberFormatException e) {
							if (LOG.isDebugEnabled()) {
								LOG.debug("The input at decimal field is not a decimal number"); //$NON-NLS-1$
							}
						}
						return Status.OK_STATUS;
					}
				}, true);

		EpDialogSupport.create(this, bindingContext);
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
		if (value != null) {
			this.textField.setText(this.value.toPlainString());			
		}

	}
}
