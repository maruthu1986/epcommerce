/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.wizards.category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.CategoryTypeService;

/**
 * The create category details wizard page.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class CreateCategoryWizardDetailsPage extends AbstractEPWizardPage<Category> {

	private static final int NUM_COLUMNS = 3;

	private Text categoryCodeText;

	private Text categoryNameText;

	private CCombo categoryTypeCombo;

	private Text catalogText;

	private Text parentCategoryText;

	private IEpDateTimePicker enableDateTimePicker;

	private IEpDateTimePicker disableDateTimePicker;

	private Button visibleInStoreCheckbox;

	private List<CategoryType> availableCategoryTypes;

	private List<Locale> localeList;

	/**
	 * Constructor.
	 *
	 * @param pageName the page name
	 */
	protected CreateCategoryWizardDetailsPage(final String pageName) {
		super(NUM_COLUMNS, false, pageName, new DataBindingContext());
		this.setDescription(CatalogMessages.get().CreateCategoryWizardDetailsPage_Description);
		this.setTitle(CatalogMessages.get().CreateCategoryWizardDetailsPage_Title);

	}

	@Override
	public void createEpPageContent(final IEpLayoutComposite mainPane) {
		final IEpLayoutData labelData = mainPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.BEGINNING);
		//final IEpLayoutData fieldData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);
		final IEpLayoutData fieldData2 = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);

		mainPane.addLabelBoldRequired(CatalogMessages.get().CreateCategoryWizardDetailsPage_Label_CategoryCode, EpState.EDITABLE, labelData);
		this.categoryCodeText = mainPane.addTextField(EpState.EDITABLE, fieldData2);

		mainPane.addLabelBoldRequired(CatalogMessages.get().CreateCategoryWizardDetailsPage_Label_CategoryName, EpState.EDITABLE, labelData);
		this.categoryNameText = mainPane.addTextField(EpState.EDITABLE, fieldData2);

		mainPane.addLabelBoldRequired(CatalogMessages.get().CreateCategoryWizardDetailsPage_Label_CategoryType, EpState.EDITABLE, labelData);
		this.categoryTypeCombo = mainPane.addComboBox(EpState.EDITABLE, fieldData2);

		mainPane.addLabelBold(CatalogMessages.get().CreateCategoryWizardDetailsPage_Label_Catalog, labelData);
		this.catalogText = mainPane.addTextField(EpState.READ_ONLY, fieldData2);

		mainPane.addLabelBold(CatalogMessages.get().CreateCategoryWizardDetailsPage_Label_ParentCategory, labelData);
		this.parentCategoryText = mainPane.addTextField(EpState.READ_ONLY, fieldData2);

		mainPane.addLabelBoldRequired(CatalogMessages.get().CreateCategoryWizardDetailsPage_Label_EnableDate, EpState.EDITABLE, labelData);
		this.enableDateTimePicker = mainPane.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, EpState.EDITABLE, fieldData2);

		mainPane.addLabelBold(CatalogMessages.get().CreateCategoryWizardDetailsPage_Label_DisableDate, labelData);
		this.disableDateTimePicker = mainPane.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, EpState.EDITABLE, fieldData2);

		mainPane.addLabelBold(CatalogMessages.get().CreateCategoryWizardDetailsPage_Label_VisibleInStore, labelData);
		this.visibleInStoreCheckbox = mainPane.addCheckBoxButton("", EpState.EDITABLE, fieldData2); //$NON-NLS-1$

		/* MUST be called */
		this.setControl(mainPane.getSwtComposite());
	}

	@Override
	public void populateControls() {
		final CategoryTypeService categoryTypeService = ServiceLocator.getService(
				ContextIdNames.CATEGORY_TYPE_SERVICE);

		final List<CategoryType> catTypesInCatalog;
		if (getModel().isVirtual()) {
			// Virtual catalogs do not have category types of their own; so list category types of all catalogs
			catTypesInCatalog = categoryTypeService.list();
		} else {
			// Non-virtual catalogs do have their own category types; only find category types for the current catalog
			catTypesInCatalog = categoryTypeService.findAllCategoryTypeFromCatalog(this.getModel().getCatalog().getUidPk());
		}

		this.availableCategoryTypes = new ArrayList<>(catTypesInCatalog.size());
		for (final CategoryType currCatType : catTypesInCatalog) {
			this.availableCategoryTypes.add(currCatType);
		}

		// populate the category type combo box
		this.categoryTypeCombo.add(CatalogMessages.get().CreateCategoryWizardDetailsPage_CategoryType_Select);
		for (final CategoryType currCategoryType : this.availableCategoryTypes) {
			this.categoryTypeCombo.add(currCategoryType.getName());
		}

		// set the catalog text
		this.catalogText.setText(this.getModel().getCatalog().getName());

		// set the parent category text
		this.parentCategoryText.setText(getParentCategoryText());

		// locale dependent display name initialization
		localeList = new ArrayList<>();
		final Collection<Locale> localeSet = getModel().getCatalog().getSupportedLocales();

		// virtual catalogs have only default locale
		if (getModel().isVirtual()) {
			Locale defaultLocale = getModel().getCatalog().getDefaultLocale();
			localeList.add(defaultLocale);
		} else {
			for (final Locale currLocale : localeSet) {
				localeList.add(currLocale);
			}
		}
	}

	private String getParentCategoryText() {
		final String parentCategory;
		final Category parent = getCategoryLookup().findParent(this.getModel());
		if (parent == null) {
			parentCategory = CatalogMessages.get().CreateCategoryWizardDetailsPage_NotAvailable;
		} else {
			parentCategory = parent.getDisplayName(CorePlugin.getDefault().getDefaultLocale());
		}
		return parentCategory;
	}

	@Override
	public boolean beforeNext(final PageChangingEvent event) {
		Locale categoryDefaultLocale = getModel().getCatalog().getDefaultLocale();

		LocaleDependantFields ldf = getModel().getLocaleDependantFieldsWithoutFallBack(categoryDefaultLocale);
		if (ldf == null || ldf.getDisplayName() == null) {
			setErrorMessage(
				NLS.bind(CatalogMessages.get().CreateCategoryWizardDetailsPage_DisplayNameRequired,
				categoryDefaultLocale.getDisplayName()));
			return false;
		}

		final CategoryService categoryService = ServiceLocator.getService(
				ContextIdNames.CATEGORY_SERVICE);

		if (categoryService.isCodeInUse(this.getModel().getCode())) {
			setErrorMessage(
				NLS.bind(CatalogMessages.get().CreateCategoryWizard_Error_DuplicateCode,
				categoryDefaultLocale.getDisplayName()));
			return false;
		}

		setErrorMessage(null);
		return true;
	}

	@Override
	public void bindControls() {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
		final boolean hideDecorationOnFirstValidation = true;

		// bind category code
		bindingProvider.bind(getDataBindingContext(), this.categoryCodeText, this.getModel(), "code",  //$NON-NLS-1$
				EpValidatorFactory.CATEGORY_CODE, null, hideDecorationOnFirstValidation);

		// bind category name
		bindingProvider.bind(getDataBindingContext(), this.categoryNameText, EpValidatorFactory.STRING_255_REQUIRED, null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
						final String displayName = (String) newValue;
						final Locale locale = getModel().getCatalog().getDefaultLocale();
						getModel().setDisplayName(displayName, locale);
						return Status.OK_STATUS;
					}
				}, hideDecorationOnFirstValidation);

		// bind category type
		bindingProvider.bind(getDataBindingContext(), categoryTypeCombo, EpValidatorFactory.REQUIRED_COMBO_FIRST_ELEMENT_NOT_VALID, null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
						final int selectedIndex = categoryTypeCombo.getSelectionIndex() - 1;
						getModel().setCategoryType(availableCategoryTypes.get(selectedIndex));
						return Status.OK_STATUS;
					}
				}, hideDecorationOnFirstValidation);

		// bind enable date
		enableDateTimePicker.bind(getDataBindingContext(), EpValidatorFactory.DATE_TIME_REQUIRED, getModel(), "startDate"); //$NON-NLS-1$

		// from-to date interbinding for from before to date validation
		final ModifyListener updateModels = (ModifyListener) event -> {
			getDataBindingContext().updateModels(); // re-validate bound events
		};
		enableDateTimePicker.getSwtText().addModifyListener(updateModels);
		disableDateTimePicker.getSwtText().addModifyListener(updateModels);

		// ensures that disable date > enable date
		IValidator disableDateValidator = new CompoundValidator(new IValidator[]{EpValidatorFactory.DATE_TIME,
				EpValidatorFactory.createDisableDateValidator(enableDateTimePicker, disableDateTimePicker)});

		// bind disable date
		disableDateTimePicker.bind(getDataBindingContext(), disableDateValidator, getModel(), "endDate"); //$NON-NLS-1$

		// bind store visible
		bindingProvider.bind(getDataBindingContext(), this.visibleInStoreCheckbox, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				getModel().setHidden(!visibleInStoreCheckbox.getSelection());
				return Status.OK_STATUS;
			}
		}, hideDecorationOnFirstValidation);

		EpWizardPageSupport.create(CreateCategoryWizardDetailsPage.this, getDataBindingContext());
	}

	protected CategoryLookup getCategoryLookup() {
		return ServiceLocator.getService(ContextIdNames.CATEGORY_LOOKUP);
	}
}