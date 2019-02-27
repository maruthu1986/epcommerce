/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.wizards.category;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.ObjectWithLocaleDependantFields;

/**
 * The wizard for creating categories.
 */
public class CreateCategoryWizard extends AbstractEpWizard<Category> {

	private static final String DETAILS_PAGE_NAME = "CreateCategoryWizardDetailsPage"; //$NON-NLS-1$

	private static final String ATTRIBUTES_PAGE_NAME = "CreateCategoryWizardAttributesPage"; //$NON-NLS-1$

	private final Category category;

	/**
	 * Constructor.
	 * 
	 * @param category <code>Category</code> object.
	 */
	protected CreateCategoryWizard(final Category category) {
		super(CatalogMessages.get().CreateCategoryWizard_Title, CatalogMessages.EMPTY_STRING,
				CatalogImageRegistry.getImage(CatalogImageRegistry.CATEGORY_CREATE));
		this.category = category;
		initProductLocaleDependentFields(category, category.getCatalog().getSupportedLocales());
	}
	
	/**
	 * Creates new locale dependent fields in order to avoid creating default ones 
	 * with display name = product code.
	 */
	private void initProductLocaleDependentFields(final ObjectWithLocaleDependantFields ldfObject, 
			final Collection<Locale> locales) {
		for (Locale locale : locales) {
			LocaleDependantFields ldf = ldfObject.getLocaleDependantFieldsWithoutFallBack(locale);
			ldfObject.addOrUpdateLocaleDependantFields(ldf);
		}
	}

	/**
	 * Opens the Product creation wizard dialog.
	 * 
	 * @param shell the shell
	 * @param category the parent category
	 * @return result Window.OK or CANCEL
	 */
	public static int showWizard(final Shell shell, final Category category) {
		CreateCategoryWizard wizard = new CreateCategoryWizard(category);
		
		WizardDialog dialog = new EpWizardDialog(shell, wizard);
		dialog.addPageChangingListener(wizard);
		return dialog.open();
	}

	@Override
	public boolean performFinish() {
//		final CategoryService categoryService = (CategoryService) ServiceLocator.getService(
//				ContextIdNames.CATEGORY_SERVICE);
//		
//		// Ensure the category code is unique
//		if (categoryService.isCodeInUse(this.getModel().getCode())) {
//			final CreateCategoryWizardDetailsPage detailsPage = ((CreateCategoryWizardDetailsPage) this.getPage(DETAILS_PAGE_NAME));
//			detailsPage.setErrorMessage(CatalogMessages.get().CreateCategoryWizard_Error_DuplicateCode);
//			this.getContainer().showPage(detailsPage);			
//			return false;
//		}
		
		// Ensure that all Required Attributes are specified
		final Map<String, AttributeValue> allAttributes = this.getModel().getAttributeValueMap();
		for (final AttributeValue currAttributeValue : allAttributes.values()) {
			if (currAttributeValue.getAttribute().isRequired() && (!currAttributeValue.isDefined())) {
				final CreateCategoryWizardAttributesPage attributesPage = ((CreateCategoryWizardAttributesPage) this.getPage(ATTRIBUTES_PAGE_NAME));
				attributesPage.setErrorMessage(CatalogMessages.get().CreateCategoryWizard_Error_RequiredAttributes);
				this.getContainer().showPage(attributesPage);			
				return false;
			}
		}
		
		// TODO: Ensure the localized category name is unique

		return true;
	}

	@Override
	public Category getModel() {
		return this.category;
	}

	@Override
	public void addPages() {
		addPage(new CreateCategoryWizardDetailsPage(DETAILS_PAGE_NAME));
		addPage(new CreateCategoryWizardAttributesPage(ATTRIBUTES_PAGE_NAME));
	}
}