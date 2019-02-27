/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.wizards.product.create;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.editors.model.ProductModelController;
import com.elasticpath.cmclient.catalog.exception.RequiredAttributesChangedForProductTypeException;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPermissions;
import com.elasticpath.common.dto.pricing.PriceListAssignmentsDTO;
import com.elasticpath.common.pricing.service.PriceListAssignmentHelperService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.AttributeValueIsRequiredException;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CyclicBundleException;
import com.elasticpath.domain.catalog.InvalidAssignedBundleWithRecurringChargeItemsException;
import com.elasticpath.domain.catalog.InvalidBundleConstituentPricingMechanism;
import com.elasticpath.domain.catalog.InvalidBundleSelectionRuleException;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.ObjectWithLocaleDependantFields;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.exceptions.DuplicateBaseAmountException;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.changeset.ChangeSetMemberAction;

/**
 * The Abstraction of CreateProductWizard.
 */
public abstract class AbstractCreateProductWizard extends AbstractEpWizard<ProductModel> {

	private static final String NEW_LINE = "\n"; //$NON-NLS-1$

	private final PriceListAssignmentHelperService plaHelperService = getBean(ContextIdNames.PRICE_LIST_ASSIGNMENT_HELPER_SERVICE);
	private final CategoryService categoryService = getBean(ContextIdNames.CATEGORY_SERVICE);
	private final ChangeSetHelper changeSetHelper = getBean(ChangeSetHelper.BEAN_ID);

	private final ProductModelController wizardController = new ProductModelController();
	
	private ProductModel wizardModel;
	private final boolean hasPricingPage;
	private List<PriceListAssignmentsDTO> plas;

	/**
	 * @param windowTitle the title of the wizard.
	 * @param selectedCategoryUid the selected category UID
	 * @param newProduct the new product instance.
	 */
	protected AbstractCreateProductWizard(final String windowTitle, final long selectedCategoryUid, final Product newProduct) {
		super(windowTitle, CatalogMessages.EMPTY_STRING, CatalogImageRegistry.getImage(CatalogImageRegistry.PRODUCT_CREATE));
		this.setNeedsProgressMonitor(true);

		// load the category so that it has all the attributes loaded
		populateWizardModel(newProduct, getCategoryLookup().findByUid(selectedCategoryUid));
		plas = getAssignmentsForCatalog();
		hasPricingPage = hasPricingPermissions() && !plas.isEmpty();
	}
	
	private void populateWizardModel(final Product product, final Category selectedCategory) {
		product.setCode(CatalogMessages.EMPTY_STRING);
		product.addCategory(selectedCategory);

		initProductLocaleDependentFields(product, selectedCategory.getCatalog().getSupportedLocales());
		addLinkedCategories(product, selectedCategory);
		
		wizardModel = wizardController.buildProductWizardModel(product);
	}

	/*
	 * Add the product to all categories that link to the selected one
	 */
	private void addLinkedCategories(final Product product, final Category selectedCategory) {
		for (Category currCategory : categoryService.findLinkedCategories(selectedCategory.getUidPk())) {
			product.addCategory(currCategory);
		}
	}

	/**
	 * Creates new locale dependent fields in order to avoid creating default ones
	 * with display name = product code.
	 */
	private void initProductLocaleDependentFields(final ObjectWithLocaleDependantFields ldfObject, final Collection<Locale> locales) {
		for (Locale locale : locales) {
			LocaleDependantFields ldf = ldfObject.getLocaleDependantFieldsWithoutFallBack(locale);
			ldfObject.addOrUpdateLocaleDependantFields(ldf);
		}
	}

	@Override
	public void addPages() {

		addPage(new ProductDetailsWizardPage1(
				ProductDetailsWizardPage1.PRODUCT_DETAILS_WIZARD_PAGE1,

				NLS.bind(CatalogMessages.get().ProductCreateWizard_PageTitle,
				new Object[]{getCreationType(), 1, getTotalPages()}),
				CatalogMessages.get().ProductCreateWizard_ProductDetailsDescription));

		final int page2 = 2;
		addPage(new AttributeValuesWizardPage4(
				AttributeValuesWizardPage4.ATTRIBUTE_VALUES_WIZARD_PAGE4,

				NLS.bind(CatalogMessages.get().ProductCreateWizard_PageTitle,
				new Object[]{getCreationType(), page2, getTotalPages()}),
				CatalogMessages.get().ProductCreateWizard_AttributeValuesDescription));

		final int page3 = 3;
		addPage(new MultiSkuWizardPage5(
				MultiSkuWizardPage5.MULTI_SKU_WIZARD_PAGE5,

				NLS.bind(CatalogMessages.get().ProductCreateWizard_PageTitle,
				new Object[]{getCreationType(), page3, getTotalPages()}),
				CatalogMessages.get().ProductCreateWizard_MultiSkuDescription));


		addPage(new SingleSkuWizardPage5(
				SingleSkuWizardPage5.SINGLE_SKU_WIZARD_PAGE5,

				NLS.bind(CatalogMessages.get().ProductCreateWizard_PageTitle,
				new Object[]{getCreationType(), page3, getTotalPages()}),
				CatalogMessages.get().ProductCreateWizard_CreateSingleSkuDescription,
				true,
				true));

	}

	@Override
	public boolean performFinish() {
		try {
			Product product = wizardController.saveOrUpdateProductWizardModel(wizardModel);
			changeSetHelper.addObjectToChangeSet(product, ChangeSetMemberAction.ADD);
			fireProductChangedEvent(product);
		} catch (CyclicBundleException cbe) {
			Product bundle = cbe.getBundle();
			Product constituent = cbe.getConstituent();
			String errorMessage;
			if (bundle.equals(constituent)) {
				errorMessage =
					NLS.bind(CatalogMessages.get().ProductSaveBundleCyclicDependencyErrorMsg2,
					cbe.getBundle().getCode(), cbe.getConstituent().getCode());
			} else {
				errorMessage =
					NLS.bind(CatalogMessages.get().ProductSaveBundleCyclicDependencyErrorMsg1,
					cbe.getBundle().getCode(), cbe.getConstituent().getCode());
			}
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
				CatalogMessages.get().ProductSaveBundleCyclicDependencyErrorTitle, errorMessage);
			return false;
		} catch (DuplicateBaseAmountException dbae) {
			BaseAmount baseAmount = dbae.getBaseAmount();
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
				CatalogMessages.get().ProductSaveDuplicateBaseAmountErrorTitle,

					NLS.bind(CatalogMessages.get().ProductSaveDuplicateBaseAmountErrorMsg,
					new Object[]{baseAmount.getObjectType(), baseAmount.getObjectGuid(), baseAmount.getQuantity().toString()}));
			return false;
		} catch (InvalidBundleSelectionRuleException cbe) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
				CatalogMessages.get().ProductSaveInvalidSelectionRuleTitle,

					NLS.bind(CatalogMessages.get().ProductSaveInvalidSelectionRuleErrorMsg,
					cbe.getMessage()));
			return false;
		} catch (InvalidAssignedBundleWithRecurringChargeItemsException iab) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					CatalogMessages.get().ProductSaveRecurringChargeOnAssignedBundleErrorTitle, iab.getMessage());
			return false;
		} catch (InvalidBundleConstituentPricingMechanism ibpm) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					CatalogMessages.get().ProductBundleInvalidPricingDialogTitle, ibpm.getMessage());
			return false;
		} catch (RequiredAttributesChangedForProductTypeException rac) {
			// refresh the attribute editor to pick up the changes of the productType

			AttributeValuesWizardPage4 attrPage = (AttributeValuesWizardPage4) this.getPage(AttributeValuesWizardPage4.ATTRIBUTE_VALUES_WIZARD_PAGE4);
			attrPage.refreshData();
			getWizardDialog().showPage(attrPage);

			MessageDialog.openError(
							Display.getCurrent().getActiveShell(),
							CatalogMessages.get().RequiredAttributesChangedForProduct,
							CatalogMessages.get().RequiredAttributesChangedForProductMessage);
			return false;
		} catch (AttributeValueIsRequiredException avire) {
			AttributeValuesWizardPage4 attrPage = (AttributeValuesWizardPage4) this.getPage(AttributeValuesWizardPage4.ATTRIBUTE_VALUES_WIZARD_PAGE4);
			getWizardDialog().showPage(attrPage);
			MessageDialog.openError(
							Display.getCurrent().getActiveShell(),
							CatalogMessages.get().ProductSaveMissingValueForRequiredAttribute,

					NLS.bind(CatalogMessages.get().ProductSaveMissingValueForRequiredAttributeMessage,
					avire.getAttributesAsString(NEW_LINE)));
			return false;
		}
		return true;
	}

	/**
	 * Notify the CatalogEventService of the new product.
	 *
	 * @param product the new product
	 */
	private void fireProductChangedEvent(final Product product) {
		final ItemChangeEvent<Product> event = new ItemChangeEvent<>(this, product, ItemChangeEvent.EventType.ADD);
		CatalogEventService.getInstance().notifyProductChanged(event);
	}
	
	@Override
	public ProductModel getModel() {
		return wizardModel;
	}
	
	/**
	 * Convenience method for getting a bean instance from  bean factory.
	 * @param <T> the type of bean to return
	 * @param beanName the name of the bean to get and instance of.
	 * @return an instance of the requested bean.
	 */
	protected static final <T> T getBean(final String beanName) {
		return ServiceLocator.getService(beanName);
	}

	/**
	 * Opens the Product creation wizard dialog.
	 * 
	 * @param shell the shell
	 * @param wizard the {@link AbstractCreateProductWizard} child to open.
	 * @return result Window.OK or CANCEL
	 */
	public static int openWizard(final Shell shell, final AbstractCreateProductWizard wizard) {
		WizardDialog dialog = new EpWizardDialog(shell, wizard);
		dialog.addPageChangingListener(wizard);
		return dialog.open();
	}
	
	/**
	 * @return total number of pages in this wizard
	 */
	protected abstract int getTotalPages();
	
	/**
	 * @return the creation type of this wizard, e.g. Product.
	 */
	protected abstract String getCreationType();

	/**
	 * @return list of price list assignments for the catalog that the product that is being created belongs to
	 */
	protected final List<PriceListAssignmentsDTO> getAssignmentsForCatalog() {
		if (plas == null) {
			CmUser currentUser = LoginManager.getCmUser();
			plas = plaHelperService.getPriceListAssignmentsDTO(wizardModel.getProduct().getCatalogs(), currentUser);
		}
		return plas;
	}

	/**
	 * @return true, if the user has permissions to manage product pricing
	 */
	protected final boolean hasPricingPermissions() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(
				PriceListManagerPermissions.PRICE_MANAGEMENT_MANAGE_PRODUCT_PRICING);
	}

	/**
	 * @return true if the wizard has pricing page
	 */
	protected boolean isHasPricingPage() {
		return hasPricingPage;
	}


	protected CategoryLookup getCategoryLookup() {
		return getBean(ContextIdNames.CATEGORY_LOOKUP);
	}
}
