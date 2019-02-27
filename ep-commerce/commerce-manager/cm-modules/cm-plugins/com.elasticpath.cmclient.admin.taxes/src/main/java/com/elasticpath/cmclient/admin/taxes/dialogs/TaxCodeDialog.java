/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.taxes.dialogs;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.admin.taxes.TaxesImageRegistry;
import com.elasticpath.cmclient.admin.taxes.TaxesMessages;
import com.elasticpath.cmclient.admin.taxes.TaxesPlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.tax.TaxCodeService;

/**
 * Dialog for creating and editing tax code.
 */
public class TaxCodeDialog extends AbstractEpDialog {

	private static final int TAX_CODE_TEXT_LIMIT = 255;

	/** Title of dialog. */
	private final String title;

	/** Current tax code entity. */
	private final TaxCode taxCode;

	/** The data binding context. */
	private final DataBindingContext dataBindingContext;

	/** The code field. */
	private Text taxCodeField;

	private final Image image;

	private final TaxCodeService taxCodeService;

	/**
	 * Constructs the dialog with fields populated.
	 *
	 * @param parentShell the parent Shell
	 * @param taxCode the attribute to edit
	 * @param title the title of the dialog
	 * @param image the image of the dialog
	 */
	public TaxCodeDialog(final Shell parentShell, final TaxCode taxCode, final String title, final Image image) {
		super(parentShell, 2, false);
		dataBindingContext = new DataBindingContext();
		this.taxCode = taxCode;
		this.title = title;
		this.image = image;
		taxCodeService = ServiceLocator.getService(ContextIdNames.TAX_CODE_SERVICE);

	}

	/**
	 * Convenience method to open a create dialog.
	 *
	 * @param parentShell the parent Shell
	 * @param taxCode the tax code to edit
	 * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
	 */
	public static boolean openCreateDialog(final Shell parentShell, final TaxCode taxCode) {
		final TaxCodeDialog dialog = new TaxCodeDialog(parentShell, taxCode, TaxesMessages.get().CreateTaxCode, TaxesImageRegistry
				.getImage(TaxesImageRegistry.IMAGE_TAX_CODE_CREATE));
		return (dialog.open() == 0);
	}

	/**
	 * Convenience method to open an edit dialog.
	 *
	 * @param parentShell the parent Shell
	 * @param taxCode the tax code to edit
	 * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
	 */
	public static boolean openEditDialog(final Shell parentShell, final TaxCode taxCode) {
		final TaxCodeDialog dialog = new TaxCodeDialog(parentShell, taxCode, TaxesMessages.get().EditTaxCode, TaxesImageRegistry
				.getImage(TaxesImageRegistry.IMAGE_TAX_CODE_EDIT));
		return (dialog.open() == 0);
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		dialogComposite.addLabelBoldRequired(TaxesMessages.get().TaxCode, EpControlFactory.EpState.EDITABLE, labelData);
		taxCodeField = dialogComposite.addTextField(EpControlFactory.EpState.EDITABLE, fieldData);
		taxCodeField.setTextLimit(TAX_CODE_TEXT_LIMIT);
	}

	@Override
	protected String getPluginId() {
		return TaxesPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return taxCode;
	}

	@Override
	protected void populateControls() {
		if (isEditTaxCode()) {
			taxCodeField.setText(taxCode.getCode());
		}
	}

	@Override
	protected void bindControls() {
		EpControlBindingProvider.getInstance().bind(dataBindingContext, taxCodeField, taxCode, "code", //$NON-NLS-1$
				EpValidatorFactory.STRING_255_REQUIRED, null, true);
		EpDialogSupport.create(this, dataBindingContext);

	}

	private boolean isEditTaxCode() {
		return taxCode.isPersisted();
	}

	@Override
	protected void okPressed() {
		if (!isTaxCodeExists()) {
			super.okPressed();
		}
	}

	@Override
	protected String getTitle() {
		return title;
	}

	@Override
	protected String getWindowTitle() {
		return title;
	}

	@Override
	protected String getInitialMessage() {
		return ""; //$NON-NLS-1$
	}

	@Override
	protected Image getWindowImage() {
		return image;
	}

	private boolean isTaxCodeExists() {
		final boolean taxCodeExist = taxCodeService.taxCodeExists(taxCode);
		if (taxCodeExist) {
			setErrorMessage(
				NLS.bind(TaxesMessages.get().TaxCodeExists,
				taxCode.getCode()));
		}
		return taxCodeExist;
	}
}
