/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.dialogs.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.catalog.editors.catalog.SkuOptionNameVerifierImpl;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.EpLocalizedPropertyController;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * A dialog for adding a new option value.
 */
public class ProductSkuOptionValueDialog extends AbstractEpDialog {

	private static final int COLUMNS = 4;

	private SkuOptionValue skuOptionValue;

	private Text skuOptionNameText;

	private Text skuOptionDisplayNameText;

	private final boolean editMode;

	private final SkuOption skuOption;

	private final DataBindingContext bindingContext = new DataBindingContext();

	private EpLocalizedPropertyController nameController;

	private final SkuOptionNameVerifierImpl skuOptionNameVerifierImpl = new SkuOptionNameVerifierImpl();

	/**
	 * Constructs a new dialog for creating or editing a ProductSkuOptionValue.
	 *
	 * @param parentShell the parent shell
	 * @param skuOption the sku option
	 * @param skuOptionValue the sku option value for edit or null if a new one has to be created
	 */
	public ProductSkuOptionValueDialog(
			final Shell parentShell, final SkuOption skuOption, final SkuOptionValue skuOptionValue) {
		super(parentShell, COLUMNS, false);
		this.skuOptionValue = skuOptionValue;
		editMode = skuOptionValue != null;
		this.skuOption = skuOption;
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpButtonsForButtonsBar(ButtonsBarType.SAVE, parent);
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false);
		final IEpLayoutData fieldData3 = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 3, 1);
		final IEpLayoutData fieldData2 = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);

		EpState nameState = EpState.EDITABLE;
		if (editMode) {
			nameState = EpState.READ_ONLY;
		}

		dialogComposite.addLabelBoldRequired(CatalogMessages.get().ProductSkuOptionValueDialog_Name, nameState, labelData);
		skuOptionNameText = dialogComposite.addTextField(nameState, fieldData3);

		dialogComposite.addLabelBoldRequired(CatalogMessages.get().ProductSkuOptionValueDialog_DisplayName, EpState.EDITABLE, labelData);
		CCombo languageCombo = dialogComposite.addComboBox(EpState.EDITABLE, null);
		skuOptionDisplayNameText = dialogComposite.addTextField(EpState.EDITABLE, fieldData2);

		nameController = EpLocalizedPropertyController.createEpLocalizedPropertyController(skuOptionDisplayNameText, languageCombo,
				"skuOptionValueDisplayName", true, bindingContext, EpValidatorFactory.MAX_LENGTH_255); //$NON-NLS-1$
	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return this.skuOptionValue;
	}

	@Override
	protected void populateControls() {
		final List<Locale> allLocales = new ArrayList<>(skuOption.getCatalog().getSupportedLocales());
		final Locale defaultLocale = skuOption.getCatalog().getDefaultLocale();
		nameController.populate(allLocales, defaultLocale, getSkuOptionValue().getLocalizedProperties());

		if (editMode) {
			skuOptionNameText.setText(getSkuOptionValue().getOptionValueKey());
			skuOptionDisplayNameText.setText(getSkuOptionValue().getOptionValueKey());
		}
	}

	@Override
	protected String getInitialMessage() {
		if (!editMode) {
			return CatalogMessages.get().ProductSkuOptionValueDialog_InitialMessage_Add;
		}
		return CatalogMessages.get().ProductSkuOptionValueDialog_InitialMessage_Edit;
	}

	@Override
	protected String getTitle() {
		if (!editMode) {
			return CatalogMessages.get().ProductSkuOptionValueDialog_Title_Add;
		}
		return CatalogMessages.get().ProductSkuOptionValueDialog_Title_Edit;
	}

	@Override
	protected String getWindowTitle() {
		if (!editMode) {
			return CatalogMessages.get().ProductSkuOptionValueDialog_WindowTitle_Add;
		}
		return CatalogMessages.get().ProductSkuOptionValueDialog_WindowTitle_Edit;
	}

	/**
	 * Gets the newly created or edited instance of the {@link SkuOptionValue}.
	 *
	 * @return {@link SkuOptionValue}
	 */
	public SkuOptionValue getSkuOptionValue() {
		if (skuOptionValue == null) {
			skuOptionValue = ServiceLocator.getService(ContextIdNames.SKU_OPTION_VALUE);
		}
		return skuOptionValue;
	}

	@Override
	protected void bindControls() {
		final EpControlBindingProvider provider = EpControlBindingProvider.getInstance();

		final IValidator skuOptionKeyValidator = new CompoundValidator(
				new IValidator[] { EpValidatorFactory.SKU_OPTION_CODE,
						new SkuOptionValueKeyUniqueValidator() });

		provider.bind(bindingContext, skuOptionNameText, getSkuOptionValue(), "optionValueKey", skuOptionKeyValidator, null, true); //$NON-NLS-1$

		nameController.bind();

		EpDialogSupport.create(this, bindingContext);
	}

	/**
	 * A common validator for regular expression string.
	 */
	private class SkuOptionValueKeyUniqueValidator implements IValidator {
		@Override
		public IStatus validate(final Object value) {
			final String optionValueKey = (String) value;
			if (!skuOptionNameVerifierImpl.verifySkuOptionValueKey(optionValueKey)) {
				return new Status(IStatus.ERROR, CatalogPlugin.PLUGIN_ID, IStatus.ERROR,

						NLS.bind(CatalogMessages.get().ProductSkuOptionValueDialog_OptionValueKey_Exist,
						optionValueKey),
						null);
			}
			return Status.OK_STATUS;
		}
	}

	@Override
	protected Image getWindowImage() {
		return null;
	}
}