/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.sellingchannel.director.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.commons.exception.InvalidBundleTreeStructureException;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.sellingchannel.ShoppingItemFactory;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Maps data between a {@code ShoppingItemDto} and a {@code ShoppingItem} domain objects.
 */
@SuppressWarnings("PMD.GodClass")
public class ShoppingItemAssemblerImpl implements ShoppingItemAssembler {

	private ProductSkuLookup productSkuLookup;

	private ShoppingItemFactory shoppingItemFactory;

	private ShoppingItemDtoFactory shoppingItemDtoFactory;
	private ProductLookup productLookup;

	@Override
	public ShoppingItem createShoppingItem(final ShoppingItemDto shoppingItemDto) {
		ProductSku rootSku = getProductSku(shoppingItemDto.getSkuCode());
		// special behaviour for the root node
		Product product = rootSku.getProduct();

		ShoppingItem root;
		if (product instanceof ProductBundle) {
			root = getShoppingItemFactory().createShoppingItem(rootSku, null, shoppingItemDto.getQuantity(), 0,
					shoppingItemDto.getItemFields());
			ProductBundle bundle = (ProductBundle) product;
			createShoppingItemTree(bundle, shoppingItemDto, root, shoppingItemDto.getQuantity());
		} else {
			root = getShoppingItemFactory().createShoppingItem(rootSku, null, Math.max(product.getMinOrderQty(), shoppingItemDto.getQuantity()), 0,
					shoppingItemDto.getItemFields());
		}
		return root;
	}

	/**
	 * Recursive algorithm to create a tree of {@code ShoppingItem}s.
	 *
	 * @param bundle The bundle at the root of the tree.
	 * @param parentShoppingItemDto The DTO representing the parent.
	 * @param parent The ShoppingItem which is the parent of the children created in this invocation.
	 * @param parentShipQuantity The number of items the parent node is going to ship.
	 */
	protected void createShoppingItemTree(final ProductBundle bundle, final ShoppingItemDto parentShoppingItemDto, final ShoppingItem parent,
			final int parentShipQuantity) {
		int constituentIndex = 0;
		for (BundleConstituent bundleItem : bundle.getConstituents()) {
			ConstituentItem constituentProduct = bundleItem.getConstituent();
			// If we're not at the root of the DTO tree then we need to get the DTO from the parent's constituents, at the
			// same index of the bundle's constituent list that we're processing
			ShoppingItemDto thisShoppingItemDto = getShoppingItemDtoMatchingBundleConstituentAtIndex(parentShoppingItemDto, constituentIndex);
			if (thisShoppingItemDto != null && thisShoppingItemDto.isSelected()) {
				ProductSku sku = retrieveSkuForShoppingItem(bundleItem, thisShoppingItemDto);
				ShoppingItem shoppingItem = getShoppingItemFactory().createShoppingItem(sku, null, bundleItem.getQuantity() * parentShipQuantity,
						constituentIndex, Collections.emptyMap());
				shoppingItem.setBundleConstituent(true);
				parent.addChildItem(shoppingItem);
				if (constituentProduct.isBundle()) {
					createShoppingItemTree((ProductBundle) constituentProduct.getProduct(), thisShoppingItemDto, shoppingItem, parentShipQuantity
							* bundleItem.getQuantity());
				}
			}
			constituentIndex++;
		}
	}

	private ShoppingItemDto getShoppingItemDtoMatchingBundleConstituentAtIndex(final ShoppingItemDto parentShoppingItemDto,
			final int constituentIndex) {
		ShoppingItemDto dto = null;
		if (parentShoppingItemDto != null && constituentIndex < parentShoppingItemDto.getConstituents().size()) {
			dto = parentShoppingItemDto.getConstituents().get(constituentIndex);
		}
		return dto;
	}

	/**
	 * Gets the sku with the given code from the given product. If no skucode is given then the product's default sku will be returned. Also, if the
	 * given product doesn't have its skus loaded, then they will be loaded.
	 *
	 * @param product the product
	 * @param skuCode the sku code
	 * @return the requested product sku, or null if a sku with the given code doesn't exist on the given product
	 */
	protected ProductSku getSkuFromProduct(final Product product, final String skuCode) {

		Product productWithSkus = product;
		if (CollectionUtils.isEmpty(product.getProductSkus())) {
			productWithSkus = getProductWithSkus(product.getCode());
		}
		ProductSku productSku;
		if (StringUtils.isBlank(skuCode)) { // you weren't given the sku code
			productSku = productWithSkus.getDefaultSku();
		} else {
			// you were given the sku code for the multisku product, so get the sku specified
			productSku = productWithSkus.getSkuByCode(skuCode);
		}
		return productSku;
	}

	/**
	 * Gets the given product with its list of skus populated.
	 *
	 * @param productCode the product code
	 * @return the product with its skus
	 */
	Product getProductWithSkus(final String productCode) {
		return getProductLookup().findByGuid(productCode);
	}

	/**
	 * Retrieves the sku that should go on the ShoppingItem, given a domain bundle and its corresponding DTO. If the DTO is null then the call is
	 * assumed to be for an Accessory, which is typically passed in as a selection without any sku code being known (product only), in which case the
	 * product's default sku is used. Otherwise, the skuCode specified on the incoming DTO is used to retrieve the sku from the {@code
	 * ProductSkuService}.
	 *
	 * @param bundleItem the bundle item from which to build a ShoppingItem
	 * @param thisShoppingItemDto the DTO coming from the user (from which to build a ShoppingItem)
	 * @return the {@code ProductSku}
	 */
	ProductSku retrieveSkuForShoppingItem(final BundleConstituent bundleItem, final ShoppingItemDto thisShoppingItemDto) {
		ConstituentItem constituent = bundleItem.getConstituent();

		ProductSku sku = null;

		// Because accessory items do not have child dtos we need to handle a null dto.
		String skuCode = "";
		if (thisShoppingItemDto != null) {
			skuCode = thisShoppingItemDto.getSkuCode();
		}

		if (constituent.isProductSku()) {
			sku = constituent.getProductSku();
		} else if (constituent.isProduct()) {
			sku = getSkuFromProduct(constituent.getProduct(), skuCode);
		}

		if (sku == null) {
			throw new EpSystemException("Sku for skuCode [" + skuCode + "] for constituent [" + bundleItem.getGuid() + "] could not be found");
		}

		if (constituent.isProduct() && !constituent.getProduct().equals(sku.getProduct())) {
			throw new EpSystemException("The skuCode [" + sku.getSkuCode() + "] for constituent [" + bundleItem.getGuid()
					+ "] is not for a product which is a constituent of this bundle.");
		}
		if (constituent.isProductSku() && !StringUtils.isEmpty(skuCode) && !skuCode.equals(constituent.getCode())) {
			throw new EpSystemException("The bundle constituent [uid: " + bundleItem.getUidPk() + "] specifies a single Sku ["
					+ constituent.getCode() + " ] which is different than the sku selected [" + skuCode + "].");
		}

		return sku;
	}

	/**
	 * Retrieve the ProductSku with the given guid.
	 *
	 * @param currentSkuGuid the guid
	 * @return the corresponding ProductSku
	 * @throw EpServiceException if the productSku is not found
	 */
	ProductSku getProductSku(final String currentSkuGuid) {
		final ProductSku sku = productSkuLookup.findBySkuCode(currentSkuGuid);
		if (sku == null) { // not found.
			throw new EpServiceException("ProductSku with the specified sku code [" + currentSkuGuid + "] does not exist");
		}
		return sku;
	}

	/**
	 * Creates a {@code ShoppingItemDto} from a {@code ShoppingItem}. This implementation calls {@link #createShoppingItemDto(Product, int)} to
	 * create the default DTO from the product at the ShoppingItem's root level, and then calls
	 * {@link #configureShoppingItemDtoFromShoppingItem(ShoppingItemDto, ShoppingItem)} to configure the default DTO with data from the ShoppingItem.
	 *
	 * @param shoppingItem the shoppingItem that should be reflected by the DTO
	 * @return the generated and configured shoppingItemDto
	 */
	@Override
	public ShoppingItemDto assembleShoppingItemDtoFrom(final ShoppingItem shoppingItem) {
		final ProductSku productSku = getProductSkuLookup().findByGuid(shoppingItem.getSkuGuid());
		return assembleShoppingItemDtoFrom(shoppingItem, productSku);
	}

	@Override
	public ShoppingItemDto assembleShoppingItemDtoFrom(final ShoppingItem shoppingItem, final ProductSku productSku) {
		// Create a ShoppingItemDto from the Product represented by the ShoppingItem's root product
		Product product = productSku.getProduct();
		ShoppingItemDto rootDto = createShoppingItemDto(product, shoppingItem.getQuantity());

		// Run through the Dto and set selected items according to the items in the ShoppingItem.
		return configureShoppingItemDtoFromShoppingItem(rootDto, shoppingItem);
	}

	/**
	 * Creates a dto from a product and quantity using the ShoppingItemDtoFactory.
	 *
	 * @param product the product sku code
	 * @param quantity the quantity
	 * @return the created dto
	 */
	ShoppingItemDto createShoppingItemDto(final Product product, final int quantity) {
		return getShoppingItemDtoFactory().createDto(product, quantity);
	}

	/**
	 * Configures a ShoppingItemDto to reflect data in a ShoppingItem by ensuring that the items in the ShoppingItem are "selected" in the DTO, and
	 * the root DTO has the prices and totals that are present on the ShoppingItem. If the DTO corresponds to a sku from a Multi-sku product, the sku
	 * specified in the ShoppingItem will override the DTO's referenced sku.
	 *
	 * @param rootDto the root-level dto
	 * @param rootItem the root-level ShoppingItem
	 * @return the configured DTO
	 */
	protected ShoppingItemDto configureShoppingItemDtoFromShoppingItem(final ShoppingItemDto rootDto, final ShoppingItem rootItem) {
		final ProductSku rootSku = getProductSkuLookup().findByGuid(rootItem.getSkuGuid());

		// We know that the root DTO's product and the root ShoppingItem's product are going to be the same, but we must match the skus.
		rootDto.setSelected(true);
		rootDto.setSkuCode(rootSku.getSkuCode());
		rootDto.setShoppingItemUidPk(rootItem.getUidPk());
		rootDto.setItemFields(rootItem.getFields());
		rootDto.setQuantity(rootItem.getQuantity());
		rootDto.setGuid(rootItem.getGuid());

		for (ShoppingItem childItem : rootItem.getChildren()) {
			ShoppingItemDto foundDto = retrieveAndConfigureChildDtoForShoppingItem(childItem, rootDto.getConstituents());

			if (foundDto != null) {
				if (childItem.isBundle(getProductSkuLookup())) {
					configureShoppingItemDtoFromShoppingItem(foundDto, childItem);
				} else {
					foundDto.setShoppingItemUidPk(childItem.getUidPk());
					foundDto.setGuid(childItem.getGuid());
					foundDto.setItemFields(childItem.getFields());
					foundDto.setQuantity(childItem.getQuantity());
					foundDto.setSelected(true);
				}
			}
		}

		return rootDto;
	}

	/**
	 * Looks in the child Shopping Item DTO's of a bundle, and retrieves the DTO that corresponds to the given child shopping item. It sets the
	 * SKU code of the childShoppingItem to the returning DTO, and makes the DTO selected.
	 * @param childShoppingItem the shoppingItem to search for. It represents the selected item in a bundle.
	 * @param childDtoConstituents the constituents to search through. It contains all the constituents of the bundle.
	 * @return the modified DTO
	 * @throws com.elasticpath.commons.exception.InvalidBundleTreeStructureException if the bundle structure is not consistent with the
	 * shopping item.
	 */
	protected ShoppingItemDto retrieveAndConfigureChildDtoForShoppingItem(final ShoppingItem childShoppingItem,
			final List<ShoppingItemDto> childDtoConstituents) throws InvalidBundleTreeStructureException {
		final int childIndex = childShoppingItem.getOrdering();

		if (childDtoConstituents.size() <= childIndex) {
			throw new InvalidBundleTreeStructureException("The number of items in the bundle is less than the index of the selected constituent.");
		}

		final ShoppingItemDto childDto = childDtoConstituents.get(childIndex);
		final ProductSku childSku = getProductSkuLookup().findByGuid(childShoppingItem.getSkuGuid());
		if (!childDtoMatchesChildShoppingItem(childSku, childDto)) {
			throw new InvalidBundleTreeStructureException("The DTO from the selected bundle constituent does not match the shopping item.");
		}

		childDto.setSkuCode(childSku.getSkuCode());
		return childDto;
	}

	private boolean childDtoMatchesChildShoppingItem(final ProductSku childSku, final ShoppingItemDto dto) {
		return belongToTheSameProduct(childSku, dto)
				&& (!dto.isProductSkuConstituent() || dto.getSkuCode().equals(childSku.getSkuCode()));
	}


	private boolean belongToTheSameProduct(final ProductSku childSku, final ShoppingItemDto dto) {
		return childSku.getProduct().getSkuByCode(dto.getSkuCode()) != null;
	}

	/**
	 * @return the ProductSkuLookup
	 */
	protected ProductSkuLookup getProductSkuLookup() {
		return this.productSkuLookup;
	}

	/**
	 * Setter for shopping item factory.
	 *
	 * @param shoppingItemFactory The factory to set.
	 */
	public void setShoppingItemFactory(final ShoppingItemFactory shoppingItemFactory) {
		this.shoppingItemFactory = shoppingItemFactory;
	}


	/**
	 * @return the shoppingItemFactory
	 */
	public ShoppingItemFactory getShoppingItemFactory() {
		return shoppingItemFactory;
	}

	/**
	 * @param shoppingItemDtoFactory the shoppingItemDtoFactory to set
	 */
	public void setShoppingItemDtoFactory(final ShoppingItemDtoFactory shoppingItemDtoFactory) {
		this.shoppingItemDtoFactory = shoppingItemDtoFactory;
	}

	/**
	 * @return the shoppingItemDtoFactory
	 */
	public ShoppingItemDtoFactory getShoppingItemDtoFactory() {
		return shoppingItemDtoFactory;
	}

	/**
	 * Sets the ProductSkuLookup.
	 *
	 * @param productSkuLookup The ProductSkuLookup.
	 */
	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

	protected ProductLookup getProductLookup() {
		return productLookup;
	}

	public void setProductLookup(final ProductLookup productLookup) {
		this.productLookup = productLookup;
	}
}
