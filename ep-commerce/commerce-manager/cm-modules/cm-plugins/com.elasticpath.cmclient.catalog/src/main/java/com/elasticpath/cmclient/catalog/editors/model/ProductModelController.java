/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cmclient.catalog.CanDeleteObjectResult;
import com.elasticpath.cmclient.catalog.CanDeleteObjectResultImpl;
import com.elasticpath.cmclient.catalog.exception.RequiredAttributesChangedForProductTypeException;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.cmclient.core.dto.catalog.ProductSkuModel;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.service.BaseAmountEventService;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.service.ProductModelService;
import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.pricing.service.PriceListHelperService;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.persistence.dao.ProductTypeDao;
import com.elasticpath.service.catalog.ProductBundleService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.changeset.ChangeSetMemberAction;

/**
 * Represents controller for product editor model.
 */
public class ProductModelController {

	/** Reason for {@link CanDeleteObjectResult} when product is part of bundle and cannot be deleted. */
	public static final int CANNOT_DELETE_PRODUCT_PART_OF_BUNDLE = ProductService.CANNOT_DELETE_PART_OF_BUNDLE;

	/** Reason for {@link CanDeleteObjectResult} when product's SKU is in shipment. */
	public static final int CANNOT_DELETE_PRODUCT_SKU_IS_IN_USE = ProductService.CANNOT_DELETE_SKU_IS_IN_USE;


	/** PRODUCT. */
	public static final String PRODUCT_TYPE = "PRODUCT"; //$NON-NLS-1$

	/** SKU. */
	public static final String PRODUCT_SKU_TYPE = "SKU"; //$NON-NLS-1$

	private final ProductService productService = getBean(ContextIdNames.PRODUCT_SERVICE);
	private final ProductBundleService productBundleService = getBean(ContextIdNames.PRODUCT_BUNDLE_SERVICE);

	private final ProductSkuService productSkuService = getBean(ContextIdNames.PRODUCT_SKU_SERVICE);

	private final PriceListHelperService priceListHelperService = getBean(ContextIdNames.PRICE_LIST_HELPER_SERVICE);

	private final PriceListService priceListService = getBean(ContextIdNames.PRICE_LIST_CLIENT_SERVICE);

	private final ProductModelService productModelService = getBean("productModelService"); //$NON-NLS-1$

	private final ChangeSetHelper changeSetHelper = getBean(ChangeSetHelper.BEAN_ID);

	private final ProductTypeDao productTypeDao = getBean("productTypeDao"); //$NON-NLS-1$

	/**
	 * Builds product wizard model.
	 *
	 * @param product
	 *            the blank product
	 * @return product wizard model
	 */
	public ProductModel buildProductWizardModel(final Product product) {
		return productModelService.buildProductWizardModel(product);
	}

	/**
	 * Builds product editor model.
	 *
	 * @param productGuid
	 *            product GUID
	 * @return product editor model
	 */
	public ProductModel buildProductEditorModel(final String productGuid) {
		ProductModel productModel = productModelService.buildProductEditorModel(productGuid);
		if (productModel == null) {
			throw new IllegalArgumentException(

					NLS.bind(CoreMessages.get().Given_Object_Not_Exist,
					new String[]{"Product", productGuid})); //$NON-NLS-1$
		}
		return productModel;
	}


	/**
	 * Builds thin product editor models containing only high level information.
	 * 
	 * @param products the List of {@link Product} instances
	 * @return an Array of <code>ProductModel</code> objects; each encapsulating a product.
	 */
	public ProductModel[] buildLiteProductModels(final List<Product> products) {
		return productModelService.buildLiteProductModels(products);
	}
	
	/**
	 * Builds product sku editor model.
	 * 
	 * @param productSku
	 *            product sku
	 * @return product sku editor model
	 */
	public ProductSkuModel buildProductSkuEditorModel(final ProductSku productSku) {
		return productModelService.buildProductSkuEditorModel(productSku);
	}

	/**
	 * Builds productSku editor model.
	 * 
	 * @param productSkuGuid
	 *            product sku GUID
	 * @return product sku editor model
	 */
	public ProductSkuModel buildProductSkuEditorModel(
			final String productSkuGuid) {
		ProductSkuModel skuModel = productModelService.buildProductSkuEditorModel(productSkuGuid);
		if (skuModel == null) {
			throw new IllegalArgumentException("Product sku with given code: " + productSkuGuid + " does not exist"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return skuModel;
	}

	/**
	 * Builds thin productSku editor models containing only high level information.
	 * 
	 * @param products the List of {@link ProductSku} instances
	 * @return an Array of <code>ProductSkuModel</code> objects; each encapsulating a product.
	 */
	public ProductSkuModel[] buildLiteProductSkuModels(final List<ProductSku> products) {
		return productModelService.buildLiteProductSkuModels(products);
	}

	/**
	 * Save or update product model.
	 * 
	 * @param productModel
	 *            product model
	 * @return saved/updated product model
	 */
	public ProductModel saveOrUpdateProductEditorModel(final ProductModel productModel) {
		// check to see if the attributes on the productType have changed
		checkIfAttributesChangedFromProductType(productModel.getProduct());
				
		//CyclicBundleException must be caught and dealt with
		productService.saveOrUpdate(productModel.getProduct());
		priceListHelperService.processBaseAmountChangeSets(productModel.getBaseAmountChangeSets());
		
		for (ChangeSetObjects<BaseAmountDTO> baseAmountChangeSet : productModel.getBaseAmountChangeSets()) {
			changeSetHelper.addObjectsToChangeSet(baseAmountChangeSet);
			fireBaseAmountChangedEvent(baseAmountChangeSet);
		}
		
		if (!productModel.getRemovedSkus().isEmpty()) {
			priceListHelperService.removePricesForProductSkus(productModel.getRemovedSkus());
		}
		return productModel;
	}
	/**
	 * Check for a product that his type didn't change.
	 * @param product - the product to check
	 * @throws EpServiceException in case productType changed
	 */
	private void checkIfAttributesChangedFromProductType(final Product product) throws EpServiceException {
		// load the productType from the database and compare the attributes
		final ProductType reloadedType = productTypeDao.findByGuid(product.getProductType().getGuid());

		final Set<AttributeGroupAttribute> attributesOfReloadedType = reloadedType.getProductAttributeGroupAttributes();
		final Set<AttributeGroupAttribute> attributesOfType = product.getProductType().getProductAttributeGroupAttributes();
		for (AttributeGroupAttribute attrGroup : attributesOfReloadedType) {
			if (attrGroup.getAttribute().isRequired() && !attributesOfType.contains(attrGroup)) {
				product.setProductType(reloadedType);
				throw new RequiredAttributesChangedForProductTypeException("Missing attribute: " + attrGroup.getAttribute().getName()); //$NON-NLS-1$
			}

		}
	}



	private void fireBaseAmountChangedEvent(final ChangeSetObjects<BaseAmountDTO> baseAmountChangeSet) {
		fireBaseAmountEvent(baseAmountChangeSet.getAdditionList(), EventType.ADD);
		fireBaseAmountEvent(baseAmountChangeSet.getUpdateList(), EventType.CHANGE);
		fireBaseAmountEvent(baseAmountChangeSet.getRemovalList(), EventType.REMOVE);
	}

	private void fireBaseAmountEvent(final List<BaseAmountDTO> baseAmounts, final EventType eventType) {
		for (BaseAmountDTO baseAmountDTO : baseAmounts) {
			ItemChangeEvent<BaseAmountDTO> event = new ItemChangeEvent<>(this, baseAmountDTO, eventType);
			BaseAmountEventService.getInstance().fireBaseAmountChangedEvent(event);
		}
	}

	/**
	 * Save or update product wizard model.
	 * 
	 * @param productModel product wizard model
	 * @return updated product
	 * @throws com.elasticpath.domain.catalog.CyclicBundleException if the product model contains 
	 * 		   a bundle that references itself through its constituent tree
	 */
	public Product saveOrUpdateProductWizardModel(final ProductModel productModel) {
		Product product;
		// check to see if the attributes on the productType have changed
		checkIfAttributesChangedFromProductType(productModel.getProduct());
		//CyclicBundleException must be caught in the wizard and dealt with
		product = productService.saveOrUpdate(productModel.getProduct());
		List<ChangeSetObjects<BaseAmountDTO>> changesets = productModel.getBaseAmountChangeSets();
		setGuidOnBaseAmountsInChangeSets(product.getGuid(), changesets);

		for (ChangeSetObjects<BaseAmountDTO> baseAmountChangeSet : productModel.getBaseAmountChangeSets()) {
			filterNonPersistentBaseAmounts(baseAmountChangeSet.getRemovalList());
			changeSetHelper.addObjectsToChangeSet(baseAmountChangeSet);
		}
		
		priceListHelperService.processBaseAmountChangeSets(changesets);		
		return product;
	}

	// Prevent non-persistent base amount from being added to the changeset
	private void filterNonPersistentBaseAmounts(final List<BaseAmountDTO> removalList) {		
		CollectionUtils.filter(removalList, this::isRemovedDtoPersistent);
	}

	private boolean isRemovedDtoPersistent(final Object obj) {
		BaseAmountDTO dto = (BaseAmountDTO) obj;				
		return priceListService.getBaseAmount(dto.getGuid()) != null;
	}			

	/**
	 * New base amounts don't have product guid set, do this after the product is saved. 
	 */
	private void setGuidOnBaseAmountsInChangeSets(final String guid, final List<ChangeSetObjects<BaseAmountDTO>> changesets) {
		for (ChangeSetObjects<BaseAmountDTO> changeset : changesets) {
			setGuidOnDtos(guid, changeset.getAdditionList());
			setGuidOnDtos(guid, changeset.getUpdateList());
			setGuidOnDtos(guid, changeset.getRemovalList());
		}
	}

	private void setGuidOnDtos(final String guid, final List<BaseAmountDTO> dtos) {
		for (BaseAmountDTO dto : dtos) {
			dto.setObjectGuid(guid);
		}
	}

	/**
	 * Save or update product SKU model.
	 * 
	 * @param productSkuModel
	 *            product SKU model
	 * @return saved/updated product SKU model
	 */
	public ProductSkuModel saveOrUpdate(final ProductSkuModel productSkuModel) {
		ProductSku productSku = productSkuService.saveOrUpdate(productSkuModel.getProductSku());
		priceListHelperService.processBaseAmountChangeSets(productSkuModel.getBaseAmountChangeSets());
		for (ChangeSetObjects<BaseAmountDTO> baseAmountChangeSet : productSkuModel.getBaseAmountChangeSets()) {
			changeSetHelper.addObjectsToChangeSet(baseAmountChangeSet);
		}
		return buildProductSkuEditorModel(productSku);
	}

	/**
	 * Checks if <code>ProductModel</code> can be deleted or not.
	 * 
	 * @param productModel
	 *            product model to check
	 * @return true if <code>ProductModel</code> can be deleted
	 */
	public CanDeleteObjectResult canDelete(final ProductModel productModel) {
		final Product productToBeDeleted = productModel.getProduct();
		if (!productService.canDelete(productToBeDeleted)) {
			final Collection<ProductBundle> bundlesContainingProduct = 
				productBundleService.findProductBundlesContaining(productToBeDeleted);
			if (CollectionUtils.isNotEmpty(bundlesContainingProduct)) {
				return new CanDeleteObjectResultImpl(CANNOT_DELETE_PRODUCT_PART_OF_BUNDLE, 
						getBundleCodesAsString(bundlesContainingProduct));
			}
			
			for (ProductSku productSku : productToBeDeleted.getProductSkus().values()) {
				//TODO: New method to check whether any of a product's skus are in use could be added to
				//ProductService for better performance.
				if (!productSkuService.canDelete(productSku)) {
					return new CanDeleteObjectResultImpl(CANNOT_DELETE_PRODUCT_SKU_IS_IN_USE);
				}
			}
		}
		return new CanDeleteObjectResultImpl();
	}

	private String getBundleCodesAsString(final Collection<ProductBundle> bundles) {
		final StringBuilder messageBuilder = new StringBuilder();
		for (ProductBundle bundle : bundles) {
			messageBuilder.append(bundle.getCode());
			messageBuilder.append('\n');
		}
		return messageBuilder.toString();
	}
	
	/**
	 * Deletes domain instances encapsulated in <code>ProductModel</code>. It's
	 * required to retrieve all price list editor models because initial model
	 * is lightweight
	 * 
	 * @param productModel
	 *            product model to delete
	 */
	public void delete(final ProductModel productModel) {
		Set<ProductSku> skusToDelete = new HashSet<>(productModel.getProduct().getProductSkus().values());
		
		Product product = productModel.getProduct();
		
		// also add to change set as a delete action
		changeSetHelper.addObjectToChangeSet(product, ChangeSetMemberAction.DELETE);

		// Single-skued products are managed solely by Product for changesets
		// Therefore, we only delete SKUs for multi-skued products
		if (product.hasMultipleSkus()) {
			for (ProductSku productSku : skusToDelete) {
				changeSetHelper.addObjectToChangeSet(productSku, ChangeSetMemberAction.DELETE);
				ItemChangeEvent<ProductSku> event = new ItemChangeEvent<>(this, productSku, EventType.REMOVE);
				CatalogEventService.getInstance().notifyProductSkuChanged(event);
			}
		}

		productService.removeProductTree(product.getUidPk());

		ItemChangeEvent<Product> productChangedEvent = new ItemChangeEvent<>(this, product, EventType.REMOVE);
		CatalogEventService.getInstance().notifyProductChanged(productChangedEvent);
	}

	/**
	 * @return <code>ProductLoadTuner</code> customized to load a
	 *         <code>Product</code> displayable in product editor
	 */
	ProductLoadTuner createProductLoadTuner() {
		final ProductLoadTuner productLoadTuner = getBean(ContextIdNames.PRODUCT_LOAD_TUNER);

		productLoadTuner.setLoadingProductType(true);
		productLoadTuner.setLoadingAttributeValue(true);
		productLoadTuner.setLoadingSkus(true);
		productLoadTuner.setLoadingCategories(true);

		return productLoadTuner;
	}

	/**
	 * Convenience method for getting a bean instance from bean factory.
	 * 
	 * @param <T>
	 *            the type of bean to return
	 * @param beanName
	 *            the name of the bean to get and instance of.
	 * @return an instance of the requested bean.
	 */
	<T> T getBean(final String beanName) {
		return ServiceLocator.getService(beanName);
	}
}
