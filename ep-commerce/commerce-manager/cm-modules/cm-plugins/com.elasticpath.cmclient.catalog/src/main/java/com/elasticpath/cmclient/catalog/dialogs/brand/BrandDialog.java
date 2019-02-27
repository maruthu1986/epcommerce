/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.dialogs.brand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.catalog.editors.model.CatalogModel;
import com.elasticpath.cmclient.catalog.editors.model.CatalogModelImpl;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ObjectGuidReceiver;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.ui.framework.EpLocalizedPropertyController;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareDialog;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.service.catalog.BrandService;

/**
 * The dialog UI class for Adding/Editing SKU option.
 */
public class BrandDialog extends AbstractPolicyAwareDialog implements ObjectGuidReceiver {

	private static final int DIALOG_NUMBER_OF_COLUMN = 1;

	private static final int MAIN_COMPOSITE_NUMBER_OF_COLUMN = 4;

	private final DataBindingContext dataBindingContext;

	private Text brandCodeText;

	private Text brandNameText;

	private Brand brand;

	private boolean editMode;

	private CCombo languageCombo;

	private EpLocalizedPropertyController nameController;

	private Locale selectedLocale;

	private CatalogModel catalogModel;

	private String originalCode;

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	/**
	 * Policy container for the dialog controls.
	 */
	private PolicyActionContainer addEditBrandDialogContainer;

	private final BrandService brandService = ServiceLocator.getService(ContextIdNames.BRAND_SERVICE);

	/**
	 * Constructs the Brand dialog.
	 */
	public BrandDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), DIALOG_NUMBER_OF_COLUMN, false);
		dataBindingContext = new DataBindingContext();
	}

	/**
	 * Constructor used for the Brand add and edit dialog.
	 *
	 * @param selectedLocale the locale to be selected
	 * @param brand the brand to edit
	 * @param catalogModel the catalog model
	 */
	public BrandDialog(final Locale selectedLocale, final Brand brand, final CatalogModel catalogModel) {
		this();
		this.catalogModel = catalogModel;
		editMode = brand != null;

		this.selectedLocale = selectedLocale;

		if (editMode) {
			initializeDialog(brand);
		} else {
			this.brand = createBrand();
			this.brand.setCatalog(catalogModel.getCatalog());
			originalCode = null;
		}
	}

	private void initializeDialog(final Brand brand) {
		this.brand = brand;

		// remember the original code for validation purposes
		originalCode = brand.getCode();
	}

	/**
	 * @return the current Brand objected edited or added.
	 */
	public Brand getBrand() {
		if (brand == null) {
			brand = createBrand();
		}
		return brand;
	}

	private Brand createBrand() {
		final Brand newBrand = ServiceLocator.getService(ContextIdNames.BRAND);
		newBrand.setLocalizedPropertiesMap(new HashMap<>());
		return newBrand;
	}

	@Override
	protected void bindControls() {
		nameController.bind();

		// update strategy for the code control
		final ObservableUpdateValueStrategy codeUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				final String code = brandCodeText.getText();
				getBrand().setCode(code);
				return Status.OK_STATUS;
			}
		};

		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();
		binder.bind(dataBindingContext, brandCodeText, EpValidatorFactory.BRAND_CODE, null, codeUpdateStrategy, true);

		EpDialogSupport.create(this, dataBindingContext);
	}

	@Override
	protected String getTitle() {
		if (editMode) {
			return CatalogMessages.get().BrandAddEditDialog_EditBrand;
		}
		return CatalogMessages.get().BrandAddEditDialog_AddBrand;
	}

	@Override
	protected String getInitialMessage() {
		return getTitle();
	}

	@Override
	protected Image getWindowImage() {
		// no image required for the window.
		return null;
	}

	@Override
	protected String getWindowTitle() {
		return getTitle();
	}

	@Override
	protected void okPressed() {
		setErrorMessage(null);
		final boolean valid = validate();
		if (!valid) {
			return;
		}

		if (changeSetHelper.isChangeSetsEnabled() && changeSetHelper.getActiveChangeSet() != null) {
			if (editMode) {
				// case 1: edit dialog changing the name or image
				catalogModel.getBrandTableItems().addModifiedItem(getBrand());
				dataBindingContext.updateModels();

			} else {
				// case 2: new dialog and then implement a check if the code already exists
				// if so then create an error telling the user to use a different code.
				catalogModel.getBrandTableItems().addAddedItem(getBrand());
				dataBindingContext.updateModels();
			}
		}
		performSaveOperation();
		super.okPressed();
	}

	/**
	 * Currently does nothing, but should be overridden if the dialog should actually save the brand.
	 */
	protected void performSaveOperation() {
		// do nothing
	}

	/**
	 * Validate the brand fields.
	 *
	 * @return <code>true</code> if the brand fields are valid, false otherwise.
	 */
	private boolean validate() {
		final String brandCode = brandCodeText.getText();

		final Brand brandByCode = brandService.findByCode(brandCode);

		if (brandByCode != null) {
			if (editMode && brandByCode.getCode().equals(originalCode)) {
				return true;
			}
			setErrorMessage(CatalogMessages.get().CatalogBrandsSection_ErrorDialog_AddInUse_desc);
			return false;
		}

		//validates the model (newly added items)
		if (catalogModel != null) {
			final Set<Brand> addedItems = catalogModel.getBrandTableItems().getAddedItems();
			for (final Brand addedItem : addedItems) {
				if (addedItem.getCode().equals(brandCode)) {
					setErrorMessage(CatalogMessages.get().CatalogBrandsSection_ErrorDialog_AddInUse_desc);
					return false;
				}
			}
		}

		return true;
	}

	@Override
	protected void createDialogContent(final IPolicyTargetLayoutComposite dialogComposite) {
		final PolicyActionContainer codeFieldContainer = addPolicyActionContainer("catalogBrandCodeField");

		IPolicyTargetLayoutComposite mainComposite = dialogComposite.addGridLayoutComposite(MAIN_COMPOSITE_NUMBER_OF_COLUMN, false,
				dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true), codeFieldContainer);

		addEditBrandDialogContainer = addPolicyActionContainer("addEditBrandDialog");
		if (editMode) {
			// force state policy to check if brand is in current change set
			addEditBrandDialogContainer.setPolicyDependent(brand);
			codeFieldContainer.setPolicyDependent(brand);
		} else {
			// force state policy to *not* check if brand in current change set
			addEditBrandDialogContainer.setPolicyDependent(null);
			codeFieldContainer.setPolicyDependent(null);
		}

		final IEpLayoutData labelData = mainComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false);
		final IEpLayoutData fieldData2 = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);
		final IEpLayoutData fieldData3 = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false, 3, 1);

		mainComposite.addLabelBoldRequired(CatalogMessages.get().BrandAddEditDialog_BrandCode, labelData, codeFieldContainer);
		brandCodeText = mainComposite.addTextField(fieldData3, codeFieldContainer);

		mainComposite.addLabelBoldRequired(CatalogMessages.get().BrandAddEditDialog_BrandName, labelData, addEditBrandDialogContainer);
		languageCombo = mainComposite.addComboBox(null, addEditBrandDialogContainer);
		brandNameText = mainComposite.addTextField(fieldData2, addEditBrandDialogContainer);

		nameController = EpLocalizedPropertyController.createEpLocalizedPropertyController(brandNameText, languageCombo,
				Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, true, dataBindingContext, EpValidatorFactory.MAX_LENGTH_255);
	}

	@Override
	protected Object getDependentObject() {
		return brand;
	}

	@Override
	protected void refreshLayout() {
		// Do nothing
	}

	@Override
	protected PolicyActionContainer getOkButtonPolicyActionContainer() {
		return addEditBrandDialogContainer;
	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return brand;
	}

	@Override
	protected void populateControls() {
		final ArrayList<Locale> localesList = new ArrayList<>(brand.getCatalog().getSupportedLocales());

		Locale locale;
		if (selectedLocale == null) {
			locale = brand.getCatalog().getDefaultLocale();
		} else {
			locale = selectedLocale;
		}

		nameController.populate(localesList, locale, getBrand().getLocalizedProperties());

		setButtonLabel();
		getOkButton().setImage(null);
		getOkButton().setAlignment(SWT.CENTER);
		getOkButton().redraw();

		if (!editMode) {
			return;
		}

		brandCodeText.setText(brand.getCode());

		languageCombo.select(localesList.indexOf(selectedLocale));
	}

	private void setButtonLabel() {
		if (editMode) {
			getOkButton().setText(CoreMessages.get().AbstractEpDialog_ButtonOK);
		} else {
			getOkButton().setText(CatalogMessages.get().ProductMerchandisingAssociationDialog_Add);
		}
	}

	@Override
	public String getTargetIdentifier() {
		return "addEditBrandDialog";
	}

	@Override
	public void setObjectGuid(final String objectGuid) {
		if (objectGuid == null) {
			brand = createBrand();
			editMode = false;
		} else {
			initializeDialog(getBrandService().findByCode(objectGuid));

			selectedLocale = brand.getCatalog().getDefaultLocale();

			editMode = true;

			// On opening dialog in the change set object editor,
			// we need to populate the model used in the dialog
			if (catalogModel == null) {
				catalogModel = new CatalogModelImpl(brand.getCatalog());
			}
		}
	}

	/**
	 * Get the category type service.
	 *
	 * @return the instance of the category type service
	 */
	protected BrandService getBrandService() {
		return brandService;
	}

	@Override
	public DataBindingContext getDataBindingContext() {
		return dataBindingContext;
	}

}
