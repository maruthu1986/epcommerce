/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.taxes.dialogs;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.admin.taxes.TaxesImageRegistry;
import com.elasticpath.cmclient.admin.taxes.TaxesMessages;
import com.elasticpath.cmclient.admin.taxes.TaxesPlugin;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.EpPropertyTableControl;
import com.elasticpath.cmclient.core.ui.framework.EpPropertyTableValueModifiedListener;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.domain.tax.TaxValue;
import com.elasticpath.service.tax.TaxCodeService;

/**
 * Dialog for creating and editing tax rates for different tax codes.
 */
@SuppressWarnings({"PMD.PrematureDeclaration"})
public class TaxValueDialog extends AbstractEpDialog {

	/**
	 * Multiplier for transforming a percentage tax rate to a decimal.
	 */
	public static final int MULTIPLIER_TAX_VALUE = 100;

	/**
	 * Multiplier for transforming a percentage tax rate to a decimal.
	 */
	public static final int SCALE_TAX_VALUE = 10;

	/**
	 * The logger.
	 */
	private static final Logger LOG = Logger.getLogger(TaxValueDialog.class);

	/**
	 * This dialog's title. Depends from whether this is create or edit dialog
	 */
	private final String title;

	/**
	 * This dialog's image. Depends from whether this is create or edit dialog
	 */
	private final Image image;

	private final String regionTitle;

	private final DataBindingContext dataBindingContext;

	private Text regionNameText;

	private EpPropertyTableControl taxValuesPropertyTable;

	private final TaxRegion taxRegion;

	private final TaxCategory taxCategory;

	private final boolean isEditDialog;

	/**
	 * The constructor.
	 *
	 * @param parentShell  the parent Shell
	 * @param taxCategory  holder tax category
	 * @param taxRegion    the tax region
	 * @param image        the image for this dialog
	 * @param title        the title for this dialog
	 * @param regionTitle  the regionTitle label
	 * @param isEditDialog is this is edit or create dialog
	 */
	public TaxValueDialog(final Shell parentShell, final TaxCategory taxCategory, final TaxRegion taxRegion, final String title, final Image image,
		final String regionTitle, final boolean isEditDialog) {
		super(parentShell, 1, false);
		this.title = title;
		this.image = image;
		this.taxRegion = taxRegion;
		this.taxCategory = taxCategory;
		this.regionTitle = CoreMessages.get().getMessage(regionTitle);
		dataBindingContext = new DataBindingContext();
		this.isEditDialog = isEditDialog;
	}

	/**
	 * Convenience method to open a create dialog.
	 *
	 * @param parentShell the parent Shell
	 * @param taxCategory the holder tax category
	 * @param taxRegion   the tax region to create
	 * @param regionTitle the regionTitle label
	 * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
	 */
	public static boolean openCreateDialog(final Shell parentShell, final TaxCategory taxCategory, final TaxRegion taxRegion,
		final String regionTitle) {
		final TaxValueDialog dialog = new TaxValueDialog(parentShell, taxCategory, taxRegion, TaxesMessages.get().TaxValueAddDialogTitle,
			TaxesImageRegistry.getImage(TaxesImageRegistry.IMAGE_TAX_VALUE_CREATE), regionTitle, false);
		return (dialog.open() == 0);
	}

	/**
	 * Convenience method to open an edit dialog.
	 *
	 * @param parentShell the parent Shell
	 * @param taxCategory the holder tax category
	 * @param taxRegion   the tax region to edit
	 * @param regionTitle the regionTitle label
	 * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
	 */
	public static boolean openEditDialog(final Shell parentShell, final TaxCategory taxCategory, final TaxRegion taxRegion,
		final String regionTitle) {
		final TaxValueDialog dialog = new TaxValueDialog(parentShell, taxCategory, taxRegion, TaxesMessages.get().TaxValueEditDialogTitle,
			TaxesImageRegistry.getImage(TaxesImageRegistry.IMAGE_TAX_VALUE_EDIT), regionTitle, true);
		return (dialog.open() == 0);
	}

	@Override
	protected String getInitialMessage() {
		return TaxesMessages.get().TaxValueDialogInstructions;
	}

	@Override
	protected String getTitle() {
		return title;
	}

	@Override
	protected Image getWindowImage() {
		return image;
	}

	@Override
	protected String getWindowTitle() {
		return getTitle();
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {

		LOG.debug("TaxValueDialog.createEpDialogContent"); //$NON-NLS-1$
		final IEpLayoutComposite composite = dialogComposite.addGridLayoutComposite(2, false, dialogComposite.createLayoutData(IEpLayoutData.FILL,
			IEpLayoutData.FILL));

		final IEpLayoutData fieldData = composite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		final IEpLayoutData labelData = composite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL);

		composite.addLabelBoldRequired(regionTitle, EpState.EDITABLE, labelData);

		if (isEditDialog) {
			regionNameText = composite.addTextField(EpState.READ_ONLY, fieldData);
		} else {
			regionNameText = composite.addTextField(EpState.EDITABLE, fieldData);
		}

		final IEpLayoutComposite groupComposite = dialogComposite.addGroup(TaxesMessages.get().TaxValuesLabel,
				1, false, dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL));

		taxValuesPropertyTable = EpPropertyTableControl.createPropertyModifierControl(groupComposite, TaxesMessages.get().TaxCode,
			TaxesMessages.get().TaxValueLabel, null, new TaxValuePropertyTableValueModifiedListener());
	}

	@Override
	protected void bindControls() {
		final boolean hideDecorationOnFirstValidation = true;
		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		binder.bind(dataBindingContext, regionNameText, taxRegion, "regionName", //$NON-NLS-1$
			EpValidatorFactory.STRING_255_REQUIRED, null, hideDecorationOnFirstValidation);

		EpDialogSupport.create(this, dataBindingContext);
	}

	@Override
	protected String getPluginId() {
		return TaxesPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return taxRegion;
	}

	@Override
	protected void populateControls() {
		final TaxCodeService taxCodeService = ServiceLocator.getService(ContextIdNames.TAX_CODE_SERVICE);
		final Properties properties = new Properties();

		if (isEditDialog) {
			regionNameText.setText(taxRegion.getRegionName());
		}

		for (final TaxCode taxCode : taxCodeService.list()) {
			final BigDecimal decimalTaxValue = taxRegion.getTaxRate(taxCode.getCode());
			properties.put(taxCode.getCode(), convertPercentageToDecimalString(decimalTaxValue));
		}
		taxValuesPropertyTable.setProperties(properties);
	}

	/**
	 * Converts a decimal percentage (e.g. 7.5) into a String representation of the
	 * percentage rate (e.g. 0.075).
	 *
	 * @param percentage the percentage value (e.g. 7.5%)
	 * @return the decimal percentage divided by 100, as a String
	 */
	String convertPercentageToDecimalString(final BigDecimal percentage) {
		if (percentage == null) {
			return StringUtils.EMPTY;
		}
		return percentage.divide(new BigDecimal(MULTIPLIER_TAX_VALUE), SCALE_TAX_VALUE, BigDecimal.ROUND_HALF_UP).toPlainString();
	}

	@Override
	protected void okPressed() {
		if (!isEditDialog && taxCategory.getTaxRegion(taxRegion.getRegionName()) != null) {
			MessageDialog.openInformation(getShell(), TaxesMessages.get().AlreadyExistTaxRegionMsgBoxTitle,
				NLS.bind(TaxesMessages.get().AlreadyExistTaxRegionMsgBoxText,
				taxCategory.getName()));
			return;
		}
		saveTaxValues();
		super.okPressed();
	}

	/**
	 * Runs through the table of tax codes => tax rates to create a map of them to set on this instance's{@link TaxRegion}.
	 * Expects the table values to be decimal strings (e.g. 0.07 to represent a 7.0% tax), but the <code>TaxRegion</code> expects
	 * the rates to be specified as percentages, so this method converts them before saving.
	 */
	private void saveTaxValues() {
		final Properties properties = taxValuesPropertyTable.getProperties();
		final Map<String, TaxValue> taxValuesMap = new HashMap<>();
		TaxCodeService taxCodeService = ServiceLocator.getService(ContextIdNames.TAX_CODE_SERVICE);
		for (final Entry<Object, Object> entry : properties.entrySet()) {
			if (!"".equals(entry.getValue().toString())) { //$NON-NLS-1$
				final TaxValue taxValue = ServiceLocator.getService(ContextIdNames.TAX_VALUE);
				//TODO: how to retrieve tax code some other way.
				taxValue.setTaxCode(taxCodeService.findByCode(entry.getKey().toString()));

				taxValue.setTaxValue(convertDecimalStringToPercentage(entry.getValue().toString()));
				taxValuesMap.put(taxValue.getTaxCode().getCode(), taxValue);
			}
		}
		taxRegion.setTaxValuesMap(taxValuesMap);
	}

	/**
	 * Converts a String representation of the percentage rate (e.g. 0.07) into a
	 * decimal percentage (e.g. 7.0).
	 *
	 * @param decimalValue the decimal value as a String (e.g. 0.07)
	 * @return the decimal value multiplied by 100 (e.g. 7.0)
	 */
	BigDecimal convertDecimalStringToPercentage(final String decimalValue) {
		return BigDecimal.valueOf(MULTIPLIER_TAX_VALUE).multiply(new BigDecimal(decimalValue));
	}

	/**
	 * TaxValue PropertyTableValueModifiedListener.
	 */
	private static class TaxValuePropertyTableValueModifiedListener implements EpPropertyTableValueModifiedListener {

		private static final int MIN_TAX_VALUE = 0;

		private static final int MAX_TAX_VALUE = 1;

		/**
		 * Fired when specific Entry's tax rate value is being modified by user.
		 * Checks that the new entry is between 0 and 1 before it is assigned.
		 *
		 * @param entry            TaxCode -> TaxRate entry which is being modified.
		 * @param newPropertyValue new string value that will be assigned to the tax rate.
		 * @return true if the newPropertyValue is within bounds, false otherwise. If false, the value will not be modified.
		 */
		@Override
		public boolean onModification(final Entry<String, String> entry, final String newPropertyValue) {
			boolean error = false;
			if ("".equals(newPropertyValue)) { //$NON-NLS-1$				
				return true;
			}
			try {
				Double value = Double.valueOf(newPropertyValue);
				if (value < MIN_TAX_VALUE || value > MAX_TAX_VALUE) {
					error = true;
				}
			} catch (final NumberFormatException ex) {
				error = true;
			}
			return !error;
		}

		@Override
		public void onPrepareForModification(final Entry<String, String> entry) {
			// do nothing
		}

		/**
		 * No modification to the tax rates are required post modification,
		 * so this implementation does nothing.
		 */
		@Override
		public void onPostModification(final Entry<String, String> entry) {
			//Do nothing
		}
	}
}
