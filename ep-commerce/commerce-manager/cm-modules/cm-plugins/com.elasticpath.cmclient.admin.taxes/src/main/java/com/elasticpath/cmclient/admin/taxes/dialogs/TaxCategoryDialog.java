/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.taxes.dialogs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.admin.taxes.TaxesImageRegistry;
import com.elasticpath.cmclient.admin.taxes.TaxesMessages;
import com.elasticpath.cmclient.admin.taxes.TaxesPlugin;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.EpLocalizedPropertyController;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCategoryTypeEnum;
import com.elasticpath.domain.tax.TaxJurisdiction;

/**
 * Dialog for creating and editing tax category.
 */
public class TaxCategoryDialog extends AbstractEpDialog {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(TaxCategoryDialog.class);

	private static final int TAX_NAME_TEXT_LIMIT = 255;

	private static final int TAX_DISPLAY_NAME_TEXT_LIMIT = 255;

	/** Title of dialog. */
	private final String title;

	private final Image image;

	/** The data binding context. */
	private final DataBindingContext dataBindingContext;

	private Text taxNameText;

	private Text taxDisplayNameText;

	private CCombo taxDisplayNameCombo;

	private CCombo addressFieldCombo;

	private EpLocalizedPropertyController taxDisplayNameController;

	/** Current tax category. */
	private final TaxCategory taxCategory;

	/** Copy of original tax category properties map to allow resetting on cancel. */
	private final Map<String, LocalizedPropertyValue> previousPropertiesMap;

	/** Holder tax jurisdiction. */
	private final TaxJurisdiction taxJurisdiction;

	private final boolean isEditTaxCategory;

	/**
	 * Constructs the dialog with fields populated.
	 *
	 * @param parentShell the parent Shell
	 * @param taxCategory the attribute to edit
	 * @param title the title of the dialog
	 * @param image the image of the dialog
	 * @param taxJurisdiction holder tax jurisdiction
	 * @param isEditTaxCategory is this create or edit dialog.
	 */
	public TaxCategoryDialog(final Shell parentShell, final TaxCategory taxCategory, final TaxJurisdiction taxJurisdiction, final String title,
			final Image image, final boolean isEditTaxCategory) {
		super(parentShell, 2, false);
		dataBindingContext = new DataBindingContext();
		this.taxCategory = taxCategory;
		previousPropertiesMap = new HashMap<>(taxCategory.getLocalizedProperties().getLocalizedPropertiesMap());
		this.taxJurisdiction = taxJurisdiction;
		this.title = title;
		this.image = image;
		this.isEditTaxCategory = isEditTaxCategory;
	}

	/**
	 * Convenience method to open a create dialog.
	 *
	 * @param parentShell the parent Shell
	 * @param taxCategory the tax category to edit
	 * @param taxJurisdiction the holder tax jurisdiction for this category
	 * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
	 */
	public static boolean openCreateDialog(final Shell parentShell, final TaxCategory taxCategory, final TaxJurisdiction taxJurisdiction) {
		final TaxCategoryDialog dialog = new TaxCategoryDialog(parentShell, taxCategory, taxJurisdiction, TaxesMessages.get().CreateTaxCategory,
				TaxesImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD), false);
		return (dialog.open() == 0);
	}

	/**
	 * Convenience method to open an edit dialog.
	 *
	 * @param parentShell the parent Shell
	 * @param taxCategory the tax category to edit
	 * @param taxJurisdiction the holder tax jurisdiction for this category
	 * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
	 */
	public static boolean openEditDialog(final Shell parentShell, final TaxCategory taxCategory, final TaxJurisdiction taxJurisdiction) {
		final TaxCategoryDialog dialog = new TaxCategoryDialog(parentShell, taxCategory, taxJurisdiction, TaxesMessages.get().EditTaxCategory,
				TaxesImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT), true);
		return (dialog.open() == 0);
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite mainComposite) {
		final IEpLayoutData labelData = mainComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData horizontalFill = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);
		final IEpLayoutData horizontalFillWithoutJoin = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutComposite fieldLabelComposite = mainComposite.addGridLayoutComposite(3, false, horizontalFill);

		fieldLabelComposite.addLabelBoldRequired(TaxesMessages.get().TaxCategory, EpState.EDITABLE, labelData);
		taxNameText = fieldLabelComposite.addTextField(EpState.EDITABLE, horizontalFill);
		taxNameText.setTextLimit(TAX_NAME_TEXT_LIMIT);
		if (isEditTaxCategory) {
			taxNameText.setEnabled(false);
		}

		fieldLabelComposite.addLabelBold(TaxesMessages.get().TaxDisplayName, labelData);
		taxDisplayNameCombo = fieldLabelComposite.addComboBox(EpState.EDITABLE, horizontalFillWithoutJoin);
		taxDisplayNameText = fieldLabelComposite.addTextField(EpState.EDITABLE, horizontalFillWithoutJoin);
		taxDisplayNameText.setTextLimit(TAX_DISPLAY_NAME_TEXT_LIMIT);
		taxDisplayNameController = EpLocalizedPropertyController.createEpLocalizedPropertyController(taxDisplayNameText, taxDisplayNameCombo,
				TaxCategory.LOCALIZED_PROPERTY_DISPLAY_NAME, false, dataBindingContext);

		fieldLabelComposite.addLabelBoldRequired(TaxesMessages.get().TaxAddressField, EpState.EDITABLE, labelData);
		if (isEditTaxCategory) {
			addressFieldCombo = fieldLabelComposite.addComboBox(EpState.READ_ONLY, horizontalFill);
		} else {
			addressFieldCombo = fieldLabelComposite.addComboBox(EpState.EDITABLE, horizontalFill);
		}

	}

	@Override
	protected String getPluginId() {
		return TaxesPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return taxCategory;
	}

	@Override
	protected void populateControls() {

		for (final TaxCategoryTypeEnum typeEnum : TaxCategoryTypeEnum.values()) {
			final String key = CoreMessages.get().getMessage(typeEnum.getName());
			addressFieldCombo.setData(key, typeEnum);
			addressFieldCombo.add(key);
		}

		if (isEditTaxCategory) {
			taxNameText.setText(taxCategory.getName());
			addressFieldCombo.setText(CoreMessages.get().getMessage(taxCategory.getFieldMatchType().getName()));
		} else {
			addressFieldCombo.select(0);
		}

		reloadTaxDisplayName();
	}

	@Override
	protected void bindControls() {
		final boolean hideDecorationOnFirstValidation = true;
		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();
		binder.bind(dataBindingContext, taxNameText, taxCategory, "name", //$NON-NLS-1$ 
				EpValidatorFactory.REQUIRED, null, hideDecorationOnFirstValidation);

		final ObservableUpdateValueStrategy addressFieldUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				TaxCategoryTypeEnum addressField = (TaxCategoryTypeEnum) addressFieldCombo.getData(addressFieldCombo.getText());
				if (LOG.isDebugEnabled()) {
					LOG.debug("address field: " + addressField.toString()); //$NON-NLS-1$
				}
				taxCategory.setFieldMatchType(addressField);
				return Status.OK_STATUS;
			}
		};
		binder.bind(dataBindingContext, addressFieldCombo, null, null, addressFieldUpdateStrategy, hideDecorationOnFirstValidation);
		taxDisplayNameController.bind();
		EpDialogSupport.create(this, dataBindingContext);
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

	private void reloadTaxDisplayName() {
		
		final Locale defaultLocale = CorePlugin.getDefault().getDefaultLocale();

		final LocalizedProperties localizedProperties = taxCategory.getLocalizedProperties();

		taxDisplayNameController.populate(Arrays.asList(Locale.getAvailableLocales()), defaultLocale, localizedProperties);
	}

	@Override
	protected void okPressed() {
		//if (!isEditTaxCategory && taxJurisdiction.getTaxCategory(taxCategory.getName()) != null) {
		if (!isEditTaxCategory && taxCategoryNameExist(taxCategory.getName())) {
			MessageDialog.openInformation(getShell(), TaxesMessages.get().AlreadyExistTaxCategoryMsgBoxTitle,
				NLS.bind(TaxesMessages.get().AlreadyExistTaxCategoryMsgBoxText,
				taxCategory.getName()));
			return;
		}
		dataBindingContext.updateModels();
		super.okPressed();
	}
	
	@Override
	protected void cancelPressed() {
		super.cancelPressed();
		LocalizedProperties localizedProperties = taxCategory.getLocalizedProperties();
		localizedProperties.setLocalizedPropertiesMap(previousPropertiesMap, ContextIdNames.TAX_CATEGORY_LOCALIZED_PROPERTY_VALUE);
		taxCategory.setLocalizedProperties(localizedProperties);
	}
	
	private boolean taxCategoryNameExist(final String taxCategoryName) {
		for (final TaxCategory taxCategory : taxJurisdiction.getTaxCategorySet()) {
			if (taxCategory.getName().equals(taxCategoryName)) {
				return true;
			}
		}
		return false;
	}
}
