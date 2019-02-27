/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ObjectGuidReceiver;
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
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.service.catalog.SkuOptionService;

/**
 * The dialog UI class for Adding/Editing SKU option.
 */
@SuppressWarnings({ "PMD.LooseCoupling" })
public class CatalogSkuOptionAddEditDialog extends AbstractPolicyAwareDialog implements ObjectGuidReceiver {

	private static final int DIALOG_NUMBER_OF_COLUMN = 1;

	private static final int MAIN_COMPOSITE_NUMBER_OF_COLUMN = 3;

	private CCombo languageCombo;

	private boolean editMode;
	
	private Catalog catalog;

	private SkuOption skuOption;

	private Text skuOptionDisplayNameText;

	private Text skuOptionCodeText;

	private Locale defaultLocale;
	
	private final DataBindingContext bindingContext;

	private final SkuOptionNameVerifier verifier;

	private Collection<Locale> supportedLocales;

	private EpLocalizedPropertyController displayNameController;

	private PolicyActionContainer addEditSkuOptionDialogContainer;

	private final SkuOptionService skuOptionService = ServiceLocator.getService(ContextIdNames.SKU_OPTION_SERVICE);

	private boolean openFromChangesetView;
	
	/**
	 * Default constructor.
	 * Used to open a SkuOption from the ChangeSet view. Check the setObjectGuid() for the 
	 * rest of the initialization. 
	 */
	public CatalogSkuOptionAddEditDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), DIALOG_NUMBER_OF_COLUMN, false);
		bindingContext = new DataBindingContext();
		this.verifier = new SkuOptionNameVerifierImpl();
	}

	
	/**
	 * @param parentShell the parent shell object of the dialog window.
	 * @param skuOption the SkuOption object; set to null if adding a SkuOption
	 * @param verifier the verifier for sku option value id
	 * @param supportedLocales the locales supported by the parent object
	 * @param defaultLocale the default locale
	 * @param catalog the catalog of the SkuOption
	 */
	public CatalogSkuOptionAddEditDialog(final Shell parentShell, final SkuOption skuOption, final SkuOptionNameVerifier verifier,
			final Collection<Locale> supportedLocales, final Locale defaultLocale, final Catalog catalog) {
		super(parentShell, DIALOG_NUMBER_OF_COLUMN, false);

		this.editMode = skuOption != null;
		this.skuOption = skuOption;
		this.bindingContext = new DataBindingContext();
		this.verifier = verifier;
		this.supportedLocales = supportedLocales;
		this.catalog = catalog;
		this.defaultLocale = defaultLocale;
	}

	@Override
	protected void bindControls() {
		final IValidator skuOptionKeyValidator = new CompoundValidator(new IValidator[] {
				EpValidatorFactory.SKU_OPTION_CODE, new SkuOptionKeyUniqueValidator() });
		
		EpControlBindingProvider.getInstance().bind(bindingContext,
				this.skuOptionCodeText, this.getSkuOption(), "optionKey",
				skuOptionKeyValidator, null, true);

		// binding display name field.
		displayNameController.bind();
		
		EpDialogSupport.create(this, this.bindingContext);		
	}

	@Override
	protected void createDialogContent(final IPolicyTargetLayoutComposite dialogComposite) {
		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false);
		final IEpLayoutData fieldData2 = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutData fieldData3 = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false, 2, 1);

		addEditSkuOptionDialogContainer = addPolicyActionContainer("addEditSkuOptionDialog");

		IPolicyTargetLayoutComposite mainComposite = dialogComposite.addGridLayoutComposite(MAIN_COMPOSITE_NUMBER_OF_COLUMN, false,
				dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true), addEditSkuOptionDialogContainer);

		PolicyActionContainer skuCodeEditSkuOptionDialogContainer = addPolicyActionContainer("skuCodeEditSkuOptionDialogContainer");

		if (editMode) {
			// force state policy to check if sku option is in current change set
			addEditSkuOptionDialogContainer.setPolicyDependent(getSkuOption());
			skuCodeEditSkuOptionDialogContainer.setPolicyDependent(getSkuOption());
		} else {
			// force state policy to *not* check if sku option in current change set
			addEditSkuOptionDialogContainer.setPolicyDependent(null);
			skuCodeEditSkuOptionDialogContainer.setPolicyDependent(null);
		}

		mainComposite.addLabelBoldRequired(CatalogMessages.get().SKUOptionAddDialog_Code, labelData, addEditSkuOptionDialogContainer);
		skuOptionCodeText = mainComposite.addTextField(fieldData3, skuCodeEditSkuOptionDialogContainer);

		mainComposite.addLabelBoldRequired(CatalogMessages.get().SKUOptionAddDialog_DisplayName, labelData, addEditSkuOptionDialogContainer);
		languageCombo = mainComposite.addComboBox(fieldData2, addEditSkuOptionDialogContainer);
		skuOptionDisplayNameText = mainComposite.addTextField(fieldData2, addEditSkuOptionDialogContainer);
		
		displayNameController = EpLocalizedPropertyController.createEpLocalizedPropertyController(
				skuOptionDisplayNameText, 
				// the string comes from the SkuOptionImpl class
				languageCombo, "skuOptionDisplayName", true, bindingContext, EpValidatorFactory.MAX_LENGTH_255);
	}

	@Override
	protected String getInitialMessage() {
		return getTitle();
	}

	/**
	 * @return the current SkuOption objected edited or added.
	 */
	public SkuOption getSkuOption() {
		if (skuOption == null) {
			this.skuOption = ServiceLocator.getService(ContextIdNames.SKU_OPTION);
			this.skuOption.setCatalog(this.catalog);
		}
		return skuOption;
	}

	@Override
	protected String getTitle() {
		if (editMode) {
			return CatalogMessages.get().SKUOptionAddDialog_EditSkuOption;
		}
		return CatalogMessages.get().SKUOptionAddDialog_AddSkuOption;
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
		return skuOption;
	}

	@Override
	public void populateControls() {
		displayNameController.populate(new ArrayList<>(supportedLocales), defaultLocale, getSkuOption().getLocalizedProperties());
		
		if (editMode) {
			populateSkuOption();
		}

		setButtonLabel();
		getOkButton().setImage(null);
		getOkButton().setAlignment(SWT.CENTER);
		getOkButton().redraw();
	}

	/**
	 * refresh the content of display name text box.
	 */
	private String getSkuOptionDisplayName() {
		String displayName = getSkuOption().getDisplayName(defaultLocale, false);
		if (displayName == null) {
			return CatalogMessages.EMPTY_STRING;
		}
		return displayName;
	}

	private void populateSkuOption() {
		if (getSkuOption().getOptionKey() != null) {
			skuOptionCodeText.setText(getSkuOption().getOptionKey());
		}
		skuOptionDisplayNameText.setText(getSkuOptionDisplayName());
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

	/**
	 * A common validator for regular expression string.
	 */
	private class SkuOptionKeyUniqueValidator implements IValidator {
		@Override
		public IStatus validate(final Object value) {
			if (!editMode && !verifier.verifySkuOptionKey((String) value)) {
				return new Status(
						IStatus.ERROR,
						CorePlugin.PLUGIN_ID,
						IStatus.ERROR,
						CatalogMessages.get().SKUOptionAddDialog_DuplicateKeyMsg,
						null);
			}
			return Status.OK_STATUS;
		}
	}

	@Override
	public String getTargetIdentifier() {
		return "addEditSkuOptionDialog";
	}

	/**
	 * Set the object guid when the user opens this dialog from the change set viewer.
	 * 
	 * @param objectGuid the object guid
	 */
	@Override
	public void setObjectGuid(final String objectGuid) {
		skuOption = skuOptionService.findByKey(objectGuid);
		this.supportedLocales = skuOption.getCatalog().getSupportedLocales();
		this.catalog = skuOption.getCatalog();
		this.defaultLocale = skuOption.getCatalog().getDefaultLocale();
		editMode = true;
		openFromChangesetView = true;
	}

	@Override
	protected Object getDependentObject() {
		return this.getSkuOption();
	}

	@Override
	protected void refreshLayout() {
		// Do nothing
	}

	@Override
	protected PolicyActionContainer getOkButtonPolicyActionContainer() {
		return addEditSkuOptionDialogContainer;
	}

	@Override
	protected void okPressed() {
		if (openFromChangesetView) {
			skuOptionService.saveOrUpdate(skuOption);
		}
		performSaveOperation();
		super.okPressed();
	}

	/**
	 * Currently does nothing, but should be overridden if the dialog should actually save the catalog Sku Option.
	 *
	 */
	protected void performSaveOperation() {
		// do nothing
	}
	
	@Override
	protected DataBindingContext getDataBindingContext() {
		return bindingContext;
	}

	protected SkuOptionService getSkuOptionService() {
		return skuOptionService;
	}
}


