/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.CategoryListener;
import com.elasticpath.cmclient.core.helpers.CategoryTypeListener;
import com.elasticpath.cmclient.core.helpers.LocalCategoryLookup;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.CategoryTypeService;

/**
 * Implements a section of the category editor providing basic information/details about a category.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class CategoryEditorSummarySection extends AbstractPolicyAwareEditorPageSectionPart implements CategoryListener,
	CategoryTypeListener, ModifyListener {

	private IPolicyTargetLayoutComposite controlPane;

	private final FormPage formPage;
	
	private String originalCategoryCode;

	private final ControlModificationListener controlModificationListener;

	private Text categoryCode;

	private Text categoryName;

	private CCombo categoryType;
	
	private Text catalogText;

	private Text parentCategoryText;
	
	private IEpDateTimePicker enableDate;

	private IEpDateTimePicker disableDate;

	private Button storeVisible;
	
	private boolean onSaveValidation;

	private final CategoryService categoryService = (CategoryService) ServiceLocator.getService(
			ContextIdNames.CATEGORY_SERVICE);
	
	private final List<CategoryType> availableCategoryTypes;
	private CategoryLookup categoryLookup;

	/** 
	 * All open category editors are somewhat linked as categories are hierarchical and changes
	 * on one may affect others.  We refresh these editors when another is changed.  This flag
	 * tracks whether we should do the update now (not disposed) or skip the update and let RCP 
	 * recreate everything up to date.
	 */ 
	private boolean currentlyDisposed;

	/**
	 * Default constructor.
	 * 
	 * @param formPage the form page
	 * @param editor the editor
	 */
	public CategoryEditorSummarySection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.EXPANDED);
		this.formPage = formPage;
		originalCategoryCode = getModel().getCode();
		controlModificationListener = editor;
		CatalogEventService.getInstance().addCategoryListener(this);
		CatalogEventService.getInstance().addCategoryTypeListener(this);
		
		final CategoryTypeService categoryTypeService = ServiceLocator.getService(ContextIdNames.CATEGORY_TYPE_SERVICE);
		final List<CategoryType> readOnlyCategoryTypes;
		
		if (getModel().isVirtual()) {
			// Virtual catalogs do not have category types of their own; so list category types of all catalogs
			readOnlyCategoryTypes = categoryTypeService.list();
		} else {
			// Non-virtual catalogs do have their own category types; only find category types for the current catalog
			readOnlyCategoryTypes = categoryTypeService.findAllCategoryTypeFromCatalog(this.getModel().getCatalog().getUidPk());
		}

		availableCategoryTypes = new ArrayList<>(readOnlyCategoryTypes);
	}

	@Override
	protected void createControls(final Composite parent, final FormToolkit toolkit) {
		final PolicyActionContainer alwaysReadOnlyControls = addPolicyActionContainer("categorySummaryReadOnlyControls"); //$NON-NLS-1$		
		final PolicyActionContainer editableControls = addPolicyActionContainer("categorySummaryEditableControls"); //$NON-NLS-1$
		
		controlPane = PolicyTargetCompositeFactory.wrapLayoutComposite(
				CompositeFactory.createTableWrapLayoutComposite(parent, 2, false));
		
		resetDisposedStateAndListener(parent);
		
		final IEpLayoutData labelData = controlPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER);

		controlPane.addLabelBold(CatalogMessages.get().CreateCategoryWizardDetailsPage_Label_Catalog, labelData, alwaysReadOnlyControls);
		catalogText = controlPane.addTextField(fieldData, alwaysReadOnlyControls);
		
		controlPane.addLabelBold(CatalogMessages.get().CategoryEditorOverviewSection_ParentCategory, labelData, alwaysReadOnlyControls);
		parentCategoryText = controlPane.addTextField(fieldData, alwaysReadOnlyControls);
		
		controlPane.addLabelBoldRequired(CatalogMessages.get().CategoryEditorOverviewSection_CategoryCode, labelData, alwaysReadOnlyControls);
		categoryCode = controlPane.addTextField(fieldData, alwaysReadOnlyControls);
		
		final PolicyActionContainer controlPolicyGroup =
				getControlPolicy(alwaysReadOnlyControls, editableControls);

		controlPane.addLabelBoldRequired(CatalogMessages.get().CategoryEditorOverviewSection_CategoryName, labelData, controlPolicyGroup);
		categoryName = controlPane.addTextField(fieldData, controlPolicyGroup);

		controlPane.addLabelBoldRequired(CatalogMessages.get().CategoryEditorOverviewSection_CategoryType, labelData, controlPolicyGroup);
		categoryType = controlPane.addComboBox(fieldData, controlPolicyGroup);
		
		controlPane.addLabelBold(CatalogMessages.get().Item_EnableDateTime, labelData, controlPolicyGroup);
		enableDate = controlPane.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, fieldData, controlPolicyGroup);
		controlPane.addLabelBold(CatalogMessages.get().Item_DisableDateTime, labelData, controlPolicyGroup);
		disableDate = controlPane.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, fieldData, controlPolicyGroup);

		controlPane.addLabelBold(CatalogMessages.get().Item_StoreVisible, labelData, controlPolicyGroup);
		storeVisible = controlPane.addCheckBoxButton("", fieldData, controlPolicyGroup); //$NON-NLS-1$
		
		addCompositesToRefresh(controlPane.getSwtComposite());
	}

	private void resetDisposedStateAndListener(final Composite disposableParent) {
		currentlyDisposed = false;
		disposableParent.addDisposeListener(new DisposeListener() {
			
			/**
			 * When the parent composite is disposed we know the whole section is disposed
			 * and we shouldn't do certain things.  Rather we let RCP reinit everything.
			 */
			@Override
			public void widgetDisposed(final DisposeEvent event) {
				currentlyDisposed = true;
			}
		});
	}

	/**
	 * Make date fields read only if linked as the date values come from the master.
	 *
	 * @param alwaysReadOnlyControls read only policy control
	 * @param editableControls editable policy control
	 * @return read only if we're editing a virtual catalog, editable for master catalogs
	 */
	private PolicyActionContainer getControlPolicy(final PolicyActionContainer alwaysReadOnlyControls,
			final PolicyActionContainer editableControls) {
		if (getModel().isLinked()) {
			return alwaysReadOnlyControls;
		} else {
			return editableControls;
		}
	}

	@Override
	protected void populateControls() {
		categoryCode.setText(getModel().getCode());

		categoryCode.addModifyListener(this);
		//refresh dirty status after first populating text
		refresh();

		final Locale selectedLocale = ((CategorySummaryPage) formPage).getSelectedLocale();
		if (getModel().getLocaleDependantFieldsWithoutFallBack(selectedLocale).getDisplayName() != null) {
			categoryName.setText(getModel().getLocaleDependantFieldsWithoutFallBack(selectedLocale).getDisplayName());
		}

		// set the catalog text
		catalogText.setText(getModel().getCatalog().getName());

		for (CategoryType categoryType : availableCategoryTypes) {
			this.categoryType.add(categoryType.getName());
			if (isEqual(getModel().getCategoryType(), categoryType)) {
				this.categoryType.select(this.categoryType.getItemCount() - 1);
			}
		}

		Category parent = getCategoryLookup().findParent(getModel());
		if (parent != null) {
			parentCategoryText.setText(parent.getDisplayName(selectedLocale));
		}

		enableDate.setDate(getModel().getStartDate());
		disableDate.setDate(getModel().getEndDate());
		storeVisible.setSelection(!getModel().isHidden());

		// Set the modification listener *after* setting the control values
		// so that controls aren't considered to be modified when the initial value is set
		controlPane.setControlModificationListener(controlModificationListener);

		// the modification listener has to be set at last as it catches some of the change events associated
		// with the control modification listener and the binding framework
		categoryCode.addModifyListener(this);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		// category code -- create a custom update strategy to update the model based on the
		final ObservableUpdateValueStrategy categoryCodeUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				Category category = getModel();
				String newCode = categoryCode.getText();
				String oldCode = category.getCode();
				if (!newCode.equals(oldCode)) {
					category.setCode(newCode);
				}
				return Status.OK_STATUS;
			}
		};
		bindingProvider.bind(bindingContext, categoryCode, new IValidator() { 
			private final IValidator validator = new CompoundValidator(new IValidator[] { EpValidatorFactory.REQUIRED,
					EpValidatorFactory.MAX_LENGTH_64, EpValidatorFactory.NO_LEADING_TRAILING_SPACES });

			@Override
			public IStatus validate(final Object value) {
				final String stringValue = ((String) value);

				final IStatus status = validator.validate(stringValue);
				if (!status.isOK()) {
					return status;
				}

				//this flag needs to be set when moving away from page or on save
				if (onSaveValidation) {
					if (!originalCategoryCode.equals(categoryCode.getText()) && categoryService.isCodeInUse(stringValue)) {
						return new Status(IStatus.ERROR, CatalogPlugin.PLUGIN_ID, IStatus.ERROR,
								CatalogMessages.get().CategoryEditorOverviewSection_Duplicate_Code, null);
					}
					onSaveValidation = false;
				}
				return Status.OK_STATUS;
			}
		}, null, categoryCodeUpdateStrategy, false);

		// category name -- create a custom update strategy to update the model based on the
		// selected locale
		final ObservableUpdateValueStrategy categoryNameUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				final Locale selectedLocale = ((CategorySummaryPage) CategoryEditorSummarySection.this.formPage).getSelectedLocale();
				getModel().setDisplayName(((String) newValue).trim(), selectedLocale);
				return Status.OK_STATUS;
			}
		};

		// category type
		final ObservableUpdateValueStrategy categoryTypeUpdateStrat = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				final int selectedIndex = categoryType.getSelectionIndex();
				Category category = getModel();
				CategoryType type = availableCategoryTypes.get(selectedIndex);
				if (!category.getCategoryType().equals(type)) {
					getModel().setCategoryType(type);
				}
				return Status.OK_STATUS;
			}
		};
		
		if (!getModel().isLinked()) {
			// category name -- bind the text box to the control using the custom update strategy
			bindingProvider.bind(bindingContext, categoryName, EpValidatorFactory.STRING_255_REQUIRED, null, categoryNameUpdateStrategy, false);	
			bindingProvider.bind(bindingContext, categoryType, null, null, categoryTypeUpdateStrat, false);
		}
		
		bindDateAndStoreVisible(bindingContext, bindingProvider);
	}

	private void bindDateAndStoreVisible(final DataBindingContext bindingContext, final EpControlBindingProvider bindingProvider) {
		// bind the date controls if not a linked category
		if (!getModel().isLinked()) {
			enableDate.bind(bindingContext, EpValidatorFactory.DATE_TIME_REQUIRED, getModel(), "startDate"); //$NON-NLS-1$

			// from-to date interbinding for from before to date validation
			final ModifyListener updateModels = (ModifyListener) event -> {
				bindingContext.updateModels(); // re-validate bound events
			};
			enableDate.getSwtText().addModifyListener(updateModels);
			disableDate.getSwtText().addModifyListener(updateModels);

			// make sure always disable date > enable date
			IValidator disableDateValidator = new CompoundValidator(new IValidator[] { EpValidatorFactory.DATE_TIME,
					EpValidatorFactory.createDisableDateValidator(enableDate, disableDate) });

			// disable date
			disableDate.bind(bindingContext, disableDateValidator, getModel(), "endDate"); //$NON-NLS-1$

			// store visible -- checkbox is the inverse of the underlying logic
			bindingProvider.bind(bindingContext, storeVisible, null, null, new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object value) {
					Category category = getModel();
					boolean newHidden  = !storeVisible.getSelection();
					boolean oldHidden = category.isHidden();
					if (newHidden != oldHidden) {
						category.setHidden(newHidden);
					}
					return Status.OK_STATUS;
				}
			}, true);
		}
	}
	
	@Override
	public Category getModel() {
		return (Category) super.getModel();
	}
	
	@Override
	public void commit(final boolean onSave) {
		//Enable validation all the time to avoid bypass when saving on a different page.
		onSaveValidation = true;
		super.commit(onSave);
	}

	@Override
	public void categoryChanged(final ItemChangeEvent<Category> event) {
		Category changedCategory = event.getItem();
		// since editors are the only way to modify categories, we know that only
		// the parent category can change
		if (isParent(getModel(), changedCategory) && !currentlyDisposed) {
			// fix to not set dirty state when the parent category is updated
			controlPane.setControlModificationListener(null);
			final Locale selectedLocale = ((CategorySummaryPage) formPage).getSelectedLocale();
			parentCategoryText.setText(changedCategory.getLocaleDependantFieldsWithoutFallBack(selectedLocale).getDisplayName());
			controlPane.setControlModificationListener(controlModificationListener);
		}

		if (isAncestor(getModel(), changedCategory)) {
			//When the parent is updated, reload the child if its editor is opened. 
			//otherwise an openjpa optimistic locking exception will be thrown 
			//when the child category is being updated
			this.getEditor().reloadModel();
			// Any page that had a reference to the old model now need to be refreshed
			this.getEditor().reloadPage(CategoryAttributePage.PART_ID);
			this.getEditor().refreshAllDataBindings();
		}

		//When the child is updated, reload the parent if its editor is opened. 
		//otherwise an openjpa optimistic locking exception will be thrown
		//when the parent category is being updated
		if (isChild(getModel(), changedCategory)) {
			this.getEditor().reloadModel();
			// Any page that had a reference to the old model now need to be refreshed
			this.getEditor().reloadPage(CategoryAttributePage.PART_ID);
			this.getEditor().refreshAllDataBindings();
		}
	}
	
	/**
	 * determine if the changed category is the ancestor category of mine.
	 * 
	 * @param category this category
	 * @param changedCategory the changed category
	 * @return true if the changed category is the ancestor of mine
	 */
	protected boolean isAncestor(final Category category, final Category changedCategory) {
		Category parent = getCategoryLookup().findParent(category);
		return parent != null
				&& (parent.getUidPk() == changedCategory.getUidPk() || isAncestor(parent, changedCategory));

	}

	/**
	 * determine if the changed category is the parent category of mine.
	 * 
	 * @param category this category
	 * @param changedCategory the changed category 
	 * @return true if the changed category is the parent category of mine
	 */
	protected boolean isParent(final Category category, final Category changedCategory) {
		Category parent = getCategoryLookup().findParent(category);
		return parent != null
			&& parent.getUidPk() == changedCategory.getUidPk();
	}

	/**
	 * determine if the changed category is one child category of mine.
	 * 
	 * @param category this category
	 * @param changedCategory the changed category
	 * @return true if the changed category is one child category of mine
	 */
	protected boolean isChild(final Category category, final Category changedCategory) {
		return isAncestor(changedCategory, category);
	}

	@Override
	public void categorySearchResultReturned(final SearchResultEvent<Category> event) {
		// don't care about search results returned
	}

	@Override
	public void dispose() {
		CatalogEventService.getInstance().removeCategoryListener(this);
		CatalogEventService.getInstance().removeCategoryTypeListener(this);
		super.dispose();
	}

	@Override
	public void categoryTypeChange(final ItemChangeEvent<CategoryType> event) {
		int selection = 0;
		switch (event.getEventType()) {
			case ADD:
				availableCategoryTypes.add(event.getItem());
				categoryType.add(event.getItem().getName());
				categoryType.setData(String.valueOf(categoryType.getItemCount()), event.getItem().getUidPk());
				break;
			case CHANGE:
				for (CategoryType catType : availableCategoryTypes) {
					if (catType.getUidPk() == event.getItem().getUidPk()) {
						availableCategoryTypes.set(selection, event.getItem());
						categoryType.setItem(selection, event.getItem().getName());
					}
					++selection;
				}
				break;
			case REMOVE:
				for (CategoryType catType : availableCategoryTypes) {
					if (catType.getUidPk() == event.getItem().getUidPk()) {
						availableCategoryTypes.remove(selection);
						categoryType.remove(selection);
						categoryType.setData(String.valueOf(selection), null);
					}
					++selection;
				}
				break;
			default:
				// should never get here
				throw new EpUiException("Not implemented.", null); //$NON-NLS-1$
		}
	}

	@Override
	public void modifyText(final ModifyEvent event) {
		if (!isDirty()) {
			// save original code so that we don't need DB call to compare with the same code on
			// save
			originalCategoryCode = getModel().getCode();
		}
		markDirty();
		controlModificationListener.controlModified();
	}
	
	private <T extends Persistable> boolean isEqual(final T domain1, final T domain2) {
		if (domain1 == null && domain2 == null) {
			return true;
		} else if (domain1 == null ^ domain2 == null) {
			return false;
		} else if (domain1.getUidPk() == domain2.getUidPk()) {
			return true;
		}
		return false;
	}

	/**
	 * Lazy loads a category lookup.
	 * @return a category lookup
	 */
	protected CategoryLookup getCategoryLookup() {
		if (categoryLookup == null) {
			categoryLookup = new LocalCategoryLookup();
		}
		return categoryLookup;
	}
}
