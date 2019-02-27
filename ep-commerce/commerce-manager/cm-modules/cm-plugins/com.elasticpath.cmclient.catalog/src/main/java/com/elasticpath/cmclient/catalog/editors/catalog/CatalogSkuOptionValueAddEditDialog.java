/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.ui.framework.EpLocalizedPropertyController;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareDialog;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * The dialog UI class for Adding/Editing a SKU Option Value.
 */
public class CatalogSkuOptionValueAddEditDialog extends AbstractPolicyAwareDialog {

	private static final int DIALOG_NUMBER_OF_COLUMN = 1;

	private static final int MAIN_COMPOSITE_NUMBER_OF_COLUMN = 4;

	private final boolean editMode;

	private CCombo languageCombo;

	private Text skuOptionDisplayNameText;

	private Text optionValueKey;

	private SkuOptionValue skuOptionValue;

	private final DataBindingContext bindingContext;

	private final SkuOptionNameVerifier verifier;
	
	private final Collection<Locale> supportedLocales;

	private final Locale defaultLocale;
	
	private EpLocalizedPropertyController nameController;
	
	private final Map<Locale, String> localizedDisplayNames = new HashMap<>();

	private final PolicyActionContainer skuOptionValuePolicyActionContainer
	= addPolicyActionContainer("CatalogSkuOptionValueOkButtonContainer");

	
	/**
	 * @param parentShell the parent shell object of the dialog window.
	 * @param object the SKU option value
	 * @param supportedLocales the locales supported by the parent object
	 * @param defaultLocale the default locale of the catalog
	 * @param verifier the verifier for sku option value id 
	 */
	public CatalogSkuOptionValueAddEditDialog(final Shell parentShell, final SkuOptionValue object, 
			final Collection<Locale> supportedLocales, final Locale defaultLocale, 
			final SkuOptionNameVerifier verifier) {
		super(parentShell, DIALOG_NUMBER_OF_COLUMN, false);
		this.skuOptionValue = object;
		if (object == null) {
			editMode = false;
		} else {
			editMode = true;
			for (Locale locale : supportedLocales) {
				localizedDisplayNames.put(locale, skuOptionValue.getDisplayName(locale, true));
			}
		}
		this.bindingContext = new DataBindingContext();
		this.verifier = verifier;
		this.supportedLocales = supportedLocales;
		this.defaultLocale = defaultLocale;
	}

	/**
	 * A common validator for regular expression string.
	 */
	private class SkuOptionValueKeyUniqueValidator implements IValidator {
		@Override
		public IStatus validate(final Object value) {
			if (!editMode && !verifier.verifySkuOptionValueKey((String) value)) {
				return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID,
						IStatus.ERROR,
						CatalogMessages.get().SKUOptionAddDialog_DuplicateKeyMsg,
						null);
			}
			return Status.OK_STATUS;
		}
	}

	@Override
	protected void bindControls() {
		final IValidator skuOptionKeyValidator = new CompoundValidator(
				new IValidator[] { EpValidatorFactory.SKU_OPTION_CODE,
						new SkuOptionValueKeyUniqueValidator() });

		EpControlBindingProvider.getInstance().bind(bindingContext,
				this.optionValueKey, getSkuOptionValue(), "optionValueKey",
				skuOptionKeyValidator, null, true);

		nameController.bind();

		EpDialogSupport.create(this, this.bindingContext);
	}

	@Override
	protected void createDialogContent(final IPolicyTargetLayoutComposite dialogComposite) {
		final IEpLayoutData labelLayoutData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false);
		final IEpLayoutData textFieldLayoutData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);
		final IEpLayoutData keyLayoutData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false, 3, 1);

		PolicyActionContainer codeFieldContainer = addPolicyActionContainer("catalogSkuOptionValueCodeField");

		IPolicyTargetLayoutComposite mainComposite = dialogComposite.addGridLayoutComposite(MAIN_COMPOSITE_NUMBER_OF_COLUMN, false,
				dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true), codeFieldContainer);

		if (editMode) {
			// force state policy to check if sku option value is in current change set
			codeFieldContainer.setPolicyDependent(this.getSkuOptionValue().getSkuOption());
			skuOptionValuePolicyActionContainer.setPolicyDependent(this.getSkuOptionValue().getSkuOption());
			
			
		} else {
			// force state policy to *not* check if sku option value in current change set
			codeFieldContainer.setPolicyDependent(null);
			skuOptionValuePolicyActionContainer.setPolicyDependent(null);
		}

		mainComposite.addLabelBoldRequired(CatalogMessages.get().SKUOptionAddDialog_Code, labelLayoutData, codeFieldContainer);
		optionValueKey = mainComposite.addTextField(keyLayoutData, codeFieldContainer);

		mainComposite.addLabelBoldRequired(CatalogMessages.get().SKUOptionAddDialog_DisplayName,
				labelLayoutData, skuOptionValuePolicyActionContainer);
		skuOptionDisplayNameText = mainComposite.addTextField(textFieldLayoutData, skuOptionValuePolicyActionContainer);
		languageCombo = mainComposite.addComboBox(null, skuOptionValuePolicyActionContainer);

		nameController = EpLocalizedPropertyController.createEpLocalizedPropertyController(this.skuOptionDisplayNameText, languageCombo,
				"skuOptionValueDisplayName", true, bindingContext, EpValidatorFactory.MAX_LENGTH_255); 
	}

	@Override
	protected String getInitialMessage() {
		return getTitle();
	}

	/**
	 * @return the current SkuOptionValue objected edited or added.
	 */
	public SkuOptionValue getSkuOptionValue() {
		if (skuOptionValue == null) {
			this.skuOptionValue = ServiceLocator.getService(ContextIdNames.SKU_OPTION_VALUE);
		}
		return skuOptionValue;
	}

	@Override
	protected String getTitle() {
		if (editMode) {
			return CatalogMessages.get().SKUOptionAddDialog_EditSkuOptionValue;
		}
		return CatalogMessages.get().SKUOptionAddDialog_AddSkuOptionValue;
	}

	@Override
	protected Image getWindowImage() {
		// no image for the dialog now.
		return null;
	}

	@Override
	protected String getWindowTitle() {
		return getTitle();
	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return skuOptionValue;
	}

	@Override
	public void populateControls() {
		setButtonLabel();
		getOkButton().setImage(null);
		getOkButton().setAlignment(SWT.CENTER);
		getOkButton().redraw();
		
		final List<Locale> localesList = new ArrayList<>(supportedLocales);
		nameController.populate(localesList, defaultLocale, getSkuOptionValue().getLocalizedProperties());
		
		if (!editMode) {
			return;
		}
		
		if (getSkuOptionValue() != null) {
			optionValueKey.setText(getSkuOptionValue().getOptionValueKey());
		}
	}

	private void setButtonLabel() {
		if (editMode) {
			getOkButton().setText(
					CoreMessages.get().AbstractEpDialog_ButtonOK);
		} else {
			getOkButton().setText(
					CatalogMessages.get().ProductMerchandisingAssociationDialog_Add);
		}
	}

	@Override
	protected Object getDependentObject() {
		return getSkuOptionValue();
	}

	@Override
	public String getTargetIdentifier() {
		return "catalogSkuOptionValueAddEditDialog";
	}

	@Override
	protected void refreshLayout() {
		// TODO  possibly reapply state policy?
		
	}

	@Override
	protected PolicyActionContainer getOkButtonPolicyActionContainer() {
		return skuOptionValuePolicyActionContainer;
	}

	@Override
	public DataBindingContext getDataBindingContext() {
		return bindingContext;
	}

	
}
