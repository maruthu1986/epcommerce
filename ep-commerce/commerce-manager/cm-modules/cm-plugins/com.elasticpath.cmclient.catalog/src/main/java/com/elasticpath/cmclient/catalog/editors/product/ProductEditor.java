/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.product;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.springframework.validation.FieldError;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.catalog.editors.model.ProductModelController;
import com.elasticpath.cmclient.catalog.editors.price.PriceAdjustmentPage;
import com.elasticpath.cmclient.catalog.editors.sku.ProductSkuDetailsPage;
import com.elasticpath.cmclient.catalog.exception.RequiredAttributesChangedForProductTypeException;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.dto.catalog.PriceListSectionModel;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.cmclient.core.event.ChangeSetMemberSelectionProvider;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.cmclient.core.helpers.BaseAmountChangedEventListener;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.service.BaseAmountEventService;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.TableSelectionProvider;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareFormEditor;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.domain.catalog.AttributeValueIsRequiredException;
import com.elasticpath.domain.catalog.CyclicBundleException;
import com.elasticpath.domain.catalog.InvalidBundleSelectionRuleException;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.exceptions.BaseAmountInvalidException;
import com.elasticpath.domain.pricing.exceptions.DuplicateBaseAmountException;
import com.elasticpath.service.changeset.ChangeSetMemberAction;

/**
 * Implements a multi-page editor for displaying and editing products.
 */
@SuppressWarnings({ "PMD.GodClass", "PMD.PrematureDeclaration" })
public class ProductEditor extends AbstractPolicyAwareFormEditor implements ChangeSetMemberSelectionProvider, BaseAmountChangedEventListener {
	/**
	 * Editor ID.
	 */
	public static final String PART_ID = ProductEditor.class.getName();
	/** The logger. */
	private static final Logger LOG = Logger.getLogger(ProductEditor.class);
	private static final String NEW_LINE = "\n"; //$NON-NLS-1$

	/**
	 * Save property action ID.
	 */
	public static final int PROP_SAVE_ACTION = 171;
	
	private ProductModel productEditorModel;
	
	private PolicyActionContainer editorContainer;
	
	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);
	
	private final ProductModelController productModelController;
	
	private final TableSelectionProvider baseAmountTableSelectionProvider;

	private ProductPricePage productPricePage;
	
	 
	/**
	 * Constructor.
	 */
	public ProductEditor() {
		super();		
		productModelController = new ProductModelController();
		baseAmountTableSelectionProvider = new TableSelectionProvider();
	}

	@Override
	public void initEditor(final IEditorSite site, final IEditorInput input) throws PartInitException {
		this.productEditorModel = retrieveProductEditorModel(getProductGuid());

		setEditorTitleImage(CatalogImageRegistry.getProductEditorTabImage(productEditorModel.getProduct()));

		// add the base amount table listener to the selection service as a selection provider
		// a selected base amount can then be added to a change set, see AddToChangeSetActionDelegate
		site.setSelectionProvider(baseAmountTableSelectionProvider);
		
		BaseAmountEventService.getInstance().addBaseAmountChangedEventListener(this);
	}
	
	@Override
	public void dispose() {
		BaseAmountEventService.getInstance().removeBaseAmountChangedEventListener(this);
		super.dispose();
	}

	/**
	 * Retrieves the product GUID from the editor input.
	 */
	private String getProductGuid() {
		return getEditorInput().getAdapter(String.class);
	}

	@Override
	public ProductModel getModel() {
		return productEditorModel;
	}
	
	/**
	 * Returns the product from the product editor model.
	 * 
	 * @return the product of the model
	 */
	private Product getProductFromModel() {
		return productEditorModel.getProduct();
	}
	
	@Override
	public Object getDependentObject() {
		return getProductFromModel();
	}

	@Override
	protected void addPages() {
		editorContainer = addPolicyActionContainer("editor"); //$NON-NLS-1$
		try {
//			setTitleImage();
			
			addPage(new ProductSummaryPage(this), editorContainer);
			
			if (isProductBundleModel()) {
				addPage(new BundleItemsPage(this), editorContainer);
			}
			
			if (!isCalculatedBundleModel()) {
				// Price list page does not inherit editor container since it uses its own policy.
				productPricePage = new ProductPricePage("productPrice", this, baseAmountTableSelectionProvider); //$NON-NLS-1$
				addPage(productPricePage, editorContainer); 
			}
			
			if (isProductBundleModel()) {
				addPage(new PriceAdjustmentPage(this), editorContainer);
			}
			
			addPage(new ProductAttributePage(this), editorContainer);
			
			if (hasMultipleSkus()) {
				addPage(new ProductMultiSkuPage(this), editorContainer);
			} else {
				addPage(new ProductSkuDetailsPage(this, !isProductBundleModel(), !isProductBundleModel()), editorContainer);
			}			
			
			addPage(new ProductEditorCategoryAssignmentPage(this), editorContainer);
			addPage(new ProductMerchandisingAssociationsPage(this), editorContainer);
			getCustomData().put("editorContainer", editorContainer);
			addExtensionPages(getClass().getSimpleName(), CatalogPlugin.PLUGIN_ID);
		} catch (final PartInitException e) {
			throw new EpUiException(e);
		}
	}

	private boolean hasMultipleSkus() {
		return getProductFromModel().hasMultipleSkus();
	}

	private boolean isProductBundleModel() {
		return getProductFromModel() instanceof ProductBundle;
	}
	
	private boolean isCalculatedBundleModel() {
		if (!isProductBundleModel()) {
			return false;
		}
		
		ProductBundle bundle = (ProductBundle) getProductFromModel();
		return bundle.isCalculated();
	}

	@Override
	protected void saveModel(final IProgressMonitor monitor) {
		monitor.beginTask(CatalogMessages.get().ProductEditor_Save_StatusBarMsg, 2);
		try {
			final Product product = productEditorModel.getProduct();
			// keep a list of the newly added SKUs, if any
			Set<ProductSku> addedSkus = getNewProductSkus(product);
			removeProductSkusFromChangeSet();

			// the newly updated object should be returned by JPA's save/update
			try {
				productModelController.saveOrUpdateProductEditorModel(productEditorModel);
				if (productPricePage != null) {
					productPricePage.saveModel();
				}
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
				monitor.setCanceled(true);
				return;
			} catch (final BaseAmountInvalidException exception) {
				String message = getMessage(exception);
				MessageDialog
						.openError(
								Display.getCurrent().getActiveShell(),
								CatalogMessages.get().ProductSavePriceError_Title,
								message);
				monitor.setCanceled(true);
				return;
			} catch (final DuplicateBaseAmountException dbae) {
				BaseAmount baseAmount = dbae.getBaseAmount();
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
					CatalogMessages.get().ProductSaveDuplicateBaseAmountErrorTitle,

						NLS.bind(CatalogMessages.get().ProductSaveDuplicateBaseAmountErrorMsg,
						new Object[]{baseAmount.getObjectType(), baseAmount.getObjectGuid(), baseAmount.getQuantity().toString()}));
				monitor.setCanceled(true);
				return;
			} catch (InvalidBundleSelectionRuleException cbe) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
					CatalogMessages.get().ProductSaveInvalidSelectionRuleTitle,

						NLS.bind(CatalogMessages.get().ProductSaveInvalidSelectionRuleErrorMsg,
						cbe.getMessage()));
				monitor.setCanceled(true);
				return;
			} catch (RequiredAttributesChangedForProductTypeException rac) {
				// refresh the attribute editor to pick up the changes of the productType
				refreshPage(ProductAttributePage.PAGE_ID);
				LOG.debug(rac.getMessage());
				MessageDialog.openError(
								Display.getCurrent().getActiveShell(),
								CatalogMessages.get().RequiredAttributesChangedForProduct,
								CatalogMessages.get().RequiredAttributesChangedForProductMessage);
				monitor.setCanceled(true);
				return;
			} catch (AttributeValueIsRequiredException avire) {
				MessageDialog.openError(
								Display.getCurrent().getActiveShell(),
								CatalogMessages.get().ProductSaveMissingValueForRequiredAttribute,

						NLS.bind(CatalogMessages.get().ProductSaveMissingValueForRequiredAttributeMessage,
						avire.getAttributesAsString(NEW_LINE)));
				monitor.setCanceled(true);
				return;
			}

			addProductSkusToChangeSet(addedSkus);

			//have to retrieve the new product again as JPA doesn't give the new added values: 
			//e.g. In the new added productAssociation object, 
			//productAssociation.targetProduct.localeDependantFieldsMap will be null when we refresh the pages.
			//TODO: Remove this call and wrap the previous in the appropriate load tuner if possible [MSC-7034]
			this.productEditorModel = retrieveProductEditorModel(getProductGuid());
			firePropertyChange(PROP_SAVE_ACTION);
			refreshEditorPages();

			monitor.worked(1);
			fireProductChangedEvent();
			monitor.worked(1);
		} finally {
			monitor.done();
		}
	}

	// TODO remove duplication. The same in com.elasticpath.cmclient.pricelistmanager.editorsPriceListEditor
	private String getMessage(final BaseAmountInvalidException exception) {

		StringBuilder result = new StringBuilder();

		FieldError fieldError;
		String message;
		for (Object error : exception.getErrors().getAllErrors()) {
			fieldError = (FieldError) error;
			message = CatalogMessages.get().getMessage(fieldError.getCode().replace(".", "_"));  //$NON-NLS-1$//$NON-NLS-2$
			result.append(message);
			result.append('\n');
		}
		return result.toString();
	}

	private void addProductSkusToChangeSet(final Set<ProductSku> newProductSkus) {
		for (ProductSku newProductSku : newProductSkus) {
			changeSetHelper.addObjectToChangeSet(newProductSku, ChangeSetMemberAction.ADD);
		}
	}

	private void removeProductSkusFromChangeSet() {
		for (ProductSku removedSku : getRemovedSkus()) {
			changeSetHelper.addObjectToChangeSet(removedSku, ChangeSetMemberAction.DELETE);
			ItemChangeEvent<ProductSku> event = new ItemChangeEvent<>(this, removedSku, EventType.REMOVE);
			CatalogEventService.getInstance().notifyProductSkuChanged(event);
		}

		// Removed skus list will be cleared while re-retrieving product model
	}

	/**
	 * Given the product about to be saved, determine the newly added SKUs.
	 *
	 * @param product The product about to be saved to the DB.
	 * @return A set of GUIDs (SKu codes) of the newly added SKUs.
	 */
	protected Set<ProductSku> getNewProductSkus(final Product product) {
		Set<ProductSku> newProductSkus = new HashSet<>();

		// if change sets are not enabled, then no point in getting sku codes (guids)
		if (changeSetHelper.isChangeSetsEnabled()) {
			for (ProductSku sku : product.getProductSkus().values()) {
				if (!sku.isPersisted()) {
					newProductSkus.add(sku);
				}
			}
		}

		return newProductSkus;
	}

	/**
	 *
	 */
	private void fireProductChangedEvent() {
		final ItemChangeEvent<Product> event = new ItemChangeEvent<>(this, getProductFromModel());
		CatalogEventService.getInstance().notifyProductChanged(event);
	}

	@Override
	public void reloadModel() {
		this.productEditorModel = retrieveProductEditorModel(getProductGuid());
		fireProductChangedEvent();
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return getProductFromModel().getProductType().getCatalog().getSupportedLocales();
	}

	@Override
	public Locale getDefaultLocale() {
		return getProductFromModel().getProductType().getCatalog().getDefaultLocale();
	}

	@Override
	protected String getSaveOnCloseMessage() {
		return
			NLS.bind(CatalogMessages.get().ProductEditor_OnSavePrompt,
			getEditorName());
	}

	@Override
	public String getEditorName() {
		return getProductFromModel().getCode();
	}

	@Override
	public String getEditorToolTip() {
		final Locale locale = CorePlugin.getDefault().getDefaultLocale();
		final ProductSku sku = getProductFromModel().getDefaultSku();
		String tooltipSegment;
		if (sku == null) {
			tooltipSegment = getProductFromModel().getCode();
		} else {
			tooltipSegment = sku.getSkuCode();
		}

		if (hasMultipleSkus()) {
			return
				NLS.bind(CatalogMessages.get().ProductEditor_MultiSku_Tooltip,
				new Object[]{tooltipSegment,
						getProductFromModel().getDisplayName(locale) });
		}
		return
			NLS.bind(CatalogMessages.get().ProductEditor_SingleSku_Tooltip,
			new Object[]{tooltipSegment,
				getProductFromModel().getDisplayName(locale) });
	}

	/**
	 * Retrieves the product instance from the data source along with price list data and populated product editor model.
	 * 
	 * @param productGuid the product GUID
	 * @return the product model 
	 */
	private ProductModel retrieveProductEditorModel(final String productGuid) {		
		return productModelController.buildProductEditorModel(productGuid);
	}
    
	/**
	 * Access the set of removed SKUs.
	 * 
	 * @return Set of ProductSku instances removed from the product.
	 */
	public final Set<ProductSku> getRemovedSkus() {
		return getModel().getRemovedSkus();
	}

	@Override
	public String getTargetIdentifier() {
		return "productEditor"; //$NON-NLS-1$
	}

	@Override
	public Object resolveObjectMember(final Object changeSetObjectSelection) {
		return changeSetObjectSelection;
	}

	@Override
	public void baseAmountChanged(final ItemChangeEvent<BaseAmountDTO> event) {
		if (isEventFromItSelf(event)) {
			return;
		}
		
		Object source = event.getItem();
		if (event.getEventType().equals(EventType.ADD) || productPriceContainsBaseAmount((BaseAmountDTO) source)) {
			reloadModel();
			refreshEditorPages();
		}
	}
	
	private boolean isEventFromItSelf(final ItemChangeEvent<BaseAmountDTO> event) {
		if (event.getSource() instanceof ProductModelController) {
			ProductModelController from = (ProductModelController) event.getSource();
			if (from == this.productModelController) {
				return true;
			}
		}
		return false;
	}

	private boolean productPriceContainsBaseAmount(final BaseAmountDTO baseAmountDTO) {
		for (List<PriceListSectionModel> priceListSectionModels : productEditorModel.getPriceListSectionModels().values()) {
			for (PriceListSectionModel priceListSectionModel : priceListSectionModels) {
				Collection<BaseAmountDTO> baseAmounts = productEditorModel.getPriceEditorModel(
						priceListSectionModel.getPriceListDescriptorDTO().getGuid()).getBaseAmounts();
				for (BaseAmountDTO dto : baseAmounts) {
					if (dto.getGuid().equals(baseAmountDTO.getGuid())) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
