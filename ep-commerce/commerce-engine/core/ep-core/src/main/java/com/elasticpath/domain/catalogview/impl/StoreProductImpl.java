/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.domain.catalogview.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.common.dto.SkuInventoryDetails;
import com.elasticpath.domain.catalog.Availability;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductAssociationType;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.service.catalogview.impl.AbstractWrappedProductImpl;
import com.elasticpath.service.catalogview.impl.InventoryMessage;

/**
 * The default implementation of StoreProduct, designed as a
 * read-mostly wrapper of a product domain object.
 */
@SuppressWarnings("PMD.GodClass")
public class StoreProductImpl extends AbstractWrappedProductImpl implements StoreProduct {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private boolean productAvailable;
	private boolean productDisplayable;

	private final Map<String, Boolean> skuAvailableMap = new HashMap<>();
	private final Map<String, Boolean> skuDisplayableMap = new HashMap<>();
	private Set<ProductAssociation> productAssociations = new HashSet<>();

	private ProductSku defaultSku;

	private final Map<String, SkuInventoryDetails> inventoryDetailsMap = new HashMap<>();

	private Availability productAvailability;
	private final Map<String, Availability> skuAvailability = new HashMap<>();

	/**
	 * Constructor.
	 *
	 * @param wrappedProduct the product to be wrapped
	 */
	public StoreProductImpl(final Product wrappedProduct) {
		super(wrappedProduct);
	}

	@Override
	public boolean isProductAvailable() {
		return productAvailable;
	}

	@Override
	public boolean isProductDisplayable() {
		return productDisplayable;
	}

	public void setProductAvailable(final boolean productAvailable) {
		this.productAvailable = productAvailable;
	}

	public void setProductDisplayable(final boolean productDisplayable) {
		this.productDisplayable = productDisplayable;
	}

	/**
	 * Retrieves the inventory message for the specified SKU.
	 *
	 * @param productSkuUidPk the uidPk of the product SKU
	 * @return {@link InventoryMessage}
	 */
	@Override
	public InventoryMessage getMessageCode(final long productSkuUidPk) {

		ProductSku productSku = findProductSku(productSkuUidPk);
		return getMessageCode(productSku);
	}

	private ProductSku findProductSku(final long productSkuUidPk) {
		for (ProductSku productSku : getProductSkus().values()) {
			if (productSku.getUidPk() == productSkuUidPk) {
				return productSku;
			}
		}
		return null;
	}

	@Override
	public InventoryMessage getMessageCode(final ProductSku productSku) {
		SkuInventoryDetails inventoryDetails = inventoryDetailsMap.get(productSku.getSkuCode());
		if (inventoryDetails == null) {
			return null;
		}
		return inventoryDetails.getMessageCode();
	}

	@Override
	public SkuInventoryDetails getInventoryDetails(final String skuCode) {
		return inventoryDetailsMap.get(skuCode);
	}

	/**
	 * Gets the availability status using the skuCode.
	 *
	 * @param skuCode the SKU code
	 * @return true if available (within date range and has available quantities)
	 */
	@Override
	public boolean isSkuAvailable(final String skuCode) {
		return skuAvailableMap.getOrDefault(skuCode, Boolean.FALSE);
	}

	/**
	 * Sets the availability for the SKU with given skuCode.
	 *
	 * @param skuCode the SKU code
	 * @param skuAvailable true if SKU is available
	 */
	public void setSkuAvailable(final String skuCode, final boolean skuAvailable) {
		skuAvailableMap.put(skuCode, skuAvailable);
	}

	/**
	 * Sets the displayability for the SKU with the given skuCode.
	 *
	 * @param skuCode the SKU code
	 * @param skuDisplayable true if SKU is displayable
	 */
	public void setSkuDisplayable(final String skuCode, final boolean skuDisplayable) {
		skuDisplayableMap.put(skuCode, skuDisplayable);
	}


	/**
	 * @return an unmodifiable Set of <code>ProductAssociation</code>s for this StoreProduct.
	 */
	@Override
	public Set<ProductAssociation> getProductAssociations() {
		return Collections.unmodifiableSet(this.productAssociations);
	}

	/**
	 * Get a sorted list of <code>ProductAssociation</code>s for merchandising this product.
	 *
	 * @return an unmodifiable sorted list of <code>ProductAssociation</code>s
	 */
	@Override
	public List<ProductAssociation> getSortedProductAssociations() {
		if (this.getProductAssociations() == null || this.getProductAssociations().isEmpty()) {
			return Collections.emptyList();
		}
		List<ProductAssociation> sortedProductAssociationList = new ArrayList<>(this.getProductAssociations());
		Collections.sort(sortedProductAssociationList);
		return Collections.unmodifiableList(sortedProductAssociationList);
	}

	/**
	 * Returns a set of <code>ProductAssociation</code>s with the given association type.
	 *
	 * @param associationType the type of the association. The association type is a constant value
	 * defined on the <code>ProductAssociation</code> interface.
	 * @param includeAll Set to true to return associations that are not in their valid date range.
	 * @param filterTargetProducts Excludes any provided target products from list of returned associations.
	 * @return a set of all defined associations of the specified type
	 */
	private Set<ProductAssociation> getAssociationsByType(final ProductAssociationType associationType, final boolean includeAll,
			final Set<Product> filterTargetProducts) {
		Set<ProductAssociation> matchingAssociations = new LinkedHashSet<>();
		for (ProductAssociation currAssociation : this.getSortedProductAssociations()) {
			boolean excludeTarget = isExcludeTargetProduct(filterTargetProducts, currAssociation.getTargetProduct());
			if (currAssociation.getAssociationType().equals(associationType) && (includeAll || currAssociation.isValid()) && !excludeTarget) {
				matchingAssociations.add(currAssociation);
			}
		}
		return matchingAssociations;
	}

	private boolean isExcludeTargetProduct(final Set<Product> filterTargetProducts, final Product targetProduct) {
		if (filterTargetProducts != null) {
			for (Product currTarget : filterTargetProducts) {
				if (targetProduct.getUidPk() == currTarget.getUidPk()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns a set of <code>ProductAssociation</code>s with the given
	 * association type. Only returns associations where the date range is
	 * valid for the current date.
	 *
	 * @param associationType the type of the association. The association
	 * 	type is a constant value defined on the <code>ProductAssociation</code>
	 * 	interface.
	 * @return a set of all defined associations of the specified type
	 */
	@Override
	public Set<ProductAssociation> getAssociationsByType(final ProductAssociationType associationType) {
		return getAssociationsByType(associationType, false, null);
	}

	/**
	 * Returns a set of <code>ProductAssociation</code>s with the given association type.
	 *
	 * @param associationType the type of the association. The association type
	 *  is a constant value defined on the <code>ProductAssociation</code>
	 *  interface.
	 * @param includeAll Set to true to return associations that are not in their valid date range
	 * @return a set of all defined associations of the specified type
	 */
	@Override
	public Set<ProductAssociation> getAssociationsByType(final ProductAssociationType associationType, final boolean includeAll) {
		return getAssociationsByType(associationType, includeAll, null);
	}

	/**
	 * Returns a set of <code>ProductAssociation</code>s with the given association type.
	 *
	 * @param associationType the type of the association. The association type
	 *  is a constant value defined on the <code>ProductAssociation</code>
	 *  interface.
	 * @param filterTargetProducts Excludes any provided target products from list of returned associations.
	 * @return a set of all defined associations of the specified type
	 */
	@Override
	public Set<ProductAssociation> getAssociationsByType(final ProductAssociationType associationType, final Set<Product> filterTargetProducts) {
		return getAssociationsByType(associationType, false, filterTargetProducts);
	}

	/**
	 * Return the product association with the specified UID.
	 *
	 * @param associationUid the UID of the <code>ProductAssociation</code> to be returned.
	 * @return the corresponding <code>ProductAssociation</code> or null if no matching association is found.
	 */
	@Override
	public ProductAssociation getAssociationById(final long associationUid) {
		ProductAssociation productAssociation = null;
		for (ProductAssociation currAssociation : this.getProductAssociations()) {
			if (currAssociation.getUidPk() == associationUid) {
				productAssociation = currAssociation;
				break;
			}
		}
		return productAssociation;
	}

	/**
	 * Sets the StoreProduct's set of associations.
	 * @param productAssociations the product associations to set.
	 */
	@Override
	public void setProductAssociations(final Set<ProductAssociation> productAssociations) {
		this.productAssociations = productAssociations;
	}

	/**
	 * Overrides the functionality of getDefaultSku() on the Product class.
	 * Used to get the first available product SKU.
	 *
	 * @return the first available product SKU
	 */
	@Override
	public ProductSku getDefaultSku() {
		if (defaultSku == null || !isSkuAvailable(defaultSku.getSkuCode())) {
			Collection<ProductSku> productSkus = getProductSkus().values();
			for (ProductSku sku : productSkus) {
				if (isSkuAvailable(sku.getSkuCode())) {
					defaultSku = sku;
					return defaultSku;
				}
			}
			defaultSku = super.getDefaultSku();
		}
		return defaultSku;
	}


	@Override
	public LocaleDependantFields getLocaleDependantFields(final Locale locale) {
		LocaleDependantFields ldf = super.getLocaleDependantFields(locale);
		//RUMBA-78 fix: CM - SEO : If title is blank in SEO tab, product page in SF is not displaying product name as title
		if (ldf.getTitle() == null || "".equals(ldf.getTitle())) {
			ldf.setTitle(ldf.getDisplayName());
		}
		return ldf;
	}

	/**
	 * Adds {@code inventoryDetails} for {@code skuCode} so that it is available for {@code getInventoryDetails}.
	 * @param skuCode The sku code to add against.
	 * @param inventoryDetails The details to add.
	 */
	public void addInventoryDetails(final String skuCode, final SkuInventoryDetails inventoryDetails) {
		inventoryDetailsMap.put(skuCode, inventoryDetails);
	}

	@Override
	public Availability getProductAvailability() {
		return productAvailability;
	}

	@Override
	public boolean isSkuDisplayable(final String skuCode) {
		return skuDisplayableMap.getOrDefault(skuCode, Boolean.FALSE);
	}

	@Override
	public Availability getSkuAvailability(final String skuCode) {
		return skuAvailability.getOrDefault(skuCode, Availability.NOT_AVAILABLE);
	}

	public void setProductAvailability(final Availability productAvailability) {
		this.productAvailability = productAvailability;
	}

	/**
	 * Set the SKU availability.
	 *
	 * @param skuCode the SKU code
	 * @param skuAvailability the {@link Availability} of the SKU
	 */
	public void setSkuAvailability(final String skuCode, final Availability skuAvailability) {
		this.skuAvailability.put(skuCode, skuAvailability);
	}

	@Override
	public boolean equals(final Object obj) {
		return (this == obj)
			|| ((obj instanceof Product)
				&& getCode().equals(((Product) obj).getCode()));
	}

	@Override
	public int hashCode() {
		return getCode().hashCode();
	}

	@Override
	public String toString() {
		Locale defaultLocale = getCatalogs().iterator().next().getDefaultLocale();
		String displayName = getDisplayName(defaultLocale);
		return String.format("StoreProduct: code [%s], guid [%s], name [%s], brand [%s]",
				getCode(), getGuid(), displayName, getBrand());
	}
}
