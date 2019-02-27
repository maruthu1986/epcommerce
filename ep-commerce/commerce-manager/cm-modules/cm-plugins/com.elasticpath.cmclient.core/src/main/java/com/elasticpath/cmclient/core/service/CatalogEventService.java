/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.event.BrowseResultEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.AttributeListener;
import com.elasticpath.cmclient.core.helpers.BrandListener;
import com.elasticpath.cmclient.core.helpers.CartItemModifierGroupListener;
import com.elasticpath.cmclient.core.helpers.CatalogListener;
import com.elasticpath.cmclient.core.helpers.CategoryListener;
import com.elasticpath.cmclient.core.helpers.CategoryTypeListener;
import com.elasticpath.cmclient.core.helpers.ProductBrowseListener;
import com.elasticpath.cmclient.core.helpers.ProductListener;
import com.elasticpath.cmclient.core.helpers.ProductSkuListener;
import com.elasticpath.cmclient.core.helpers.ProductTypeListener;
import com.elasticpath.cmclient.core.helpers.WarehouseListener;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;

/**
 * The <code>CatalogRcpService</code> provides event notification
 * services for the RCP UI. Note that this does not handle selection
 * changed events. Those events are handled using Eclipse's
 * SelectionProvider mechanism.
 * 
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class CatalogEventService {

	private final List<ProductBrowseListener> productBrowseListeners = new ArrayList<ProductBrowseListener>();
	
	private final List<ProductListener> productListeners = new CopyOnWriteArrayList<ProductListener>();
	
	private final List<CategoryListener> categoryListeners = new ArrayList<CategoryListener>();
			
	private final List<ProductSkuListener> productSkuListeners = new CopyOnWriteArrayList<ProductSkuListener>();
	
	private final List<CatalogListener> catalogListeners = new ArrayList<CatalogListener>();
	
	private final List<CategoryTypeListener> categoryTypeListeners = new ArrayList<CategoryTypeListener>();
	
	private final List<ProductTypeListener> productTypeListeners = new ArrayList<ProductTypeListener>();
	
	private final List<BrandListener> brandListeners = new ArrayList<BrandListener>();

	private final List<CartItemModifierGroupListener> groupListeners = new ArrayList<CartItemModifierGroupListener>();
	
	private final List<AttributeListener> attributeListeners = new ArrayList<AttributeListener>();
	
	private final List<WarehouseListener> warehouseListeners = new ArrayList<WarehouseListener>();

	private CatalogEventService() {
	}
	
	/**
	 * Returns the session instance of {@link CatalogEventService}.
	 * @return CatalogEventService instance
	 */
	public static CatalogEventService getInstance() {
		return  CmSingletonUtil.getSessionInstance(CatalogEventService.class);
	}
	
	/**
	 * Adds a product listener.
	 * 
	 * @param listener the listener
	 */
	public void addProductListener(final ProductListener listener) {
		productListeners.add(listener);
	}
	
	
	/**
	 * Adds a product browse listener.
	 * 
	 * @param listener the listener
	 */
	public void addProductBrowseListener(final ProductBrowseListener listener) {
		productBrowseListeners.add(listener);
	}
	
	/**
	 * Removes a product browse listener.
	 * 
	 * @param listener the listener to remove
	 */
	public void removeProductBrowseListener(final ProductBrowseListener listener) {
		productBrowseListeners.remove(listener);
	}
	
	
	/**
	 * Adds a product SKU listener.
	 * 
	 * @param listener the listener
	 */
	public void addProductSkuListener(final ProductSkuListener listener) {
		productSkuListeners.add(listener);
	}
	
	/**
	 * Removes a product SKU listener.
	 * 
	 * @param listener the listener to remove
	 */
	public void removeProductSkuListener(final ProductSkuListener listener) {
		productSkuListeners.remove(listener);
	}
	

	/**
	 * Adds a category listener to the list of category listeners.
	 *
	 * @param listener the category listener
	 */
	public void addCategoryListener(final CategoryListener listener) {
		categoryListeners.add(listener);
	}
	
	/**
	 * Removes a category listener from the list of category listeners. Does nothing if the
	 * listener is not apart of the list of category listeners.
	 *
	 * @param listener the category listener
	 */
	public void removeCategoryListener(final CategoryListener listener) {
		categoryListeners.remove(listener);
	}
	
	/**
	 * Adds a catalog listener to the list of catalog listeners.
	 *
	 * @param listener the catalog listener
	 */
	public void addCatalogListener(final CatalogListener listener) {
		catalogListeners.add(listener);
	}
	
	/**
	 * Removes a catalog listener from the list of catalog listeners. Does nothing if the listener
	 * is not apart of the list of catalog listeners.
	 * 
	 * @param listener the catalog listener
	 */
	public void removeCatalogListener(final CatalogListener listener) {
		catalogListeners.remove(listener);
	}
	
	/**
	 * Adds a category type listener to the list of category type listeners.
	 *
	 * @param listener the category type listener
	 */
	public void addCategoryTypeListener(final CategoryTypeListener listener) {
		categoryTypeListeners.add(listener);
	}
	
	/**
	 * Removes a category type listener from the list of category type listeners. Does nothing if
	 * the listener is not apart of the list of category type listeners.
	 * 
	 * @param listener the category type listener
	 */
	public void removeCategoryTypeListener(final CategoryTypeListener listener) {
		categoryTypeListeners.remove(listener);
	}
	
	/**
	 * Adds a product type listener to the list of product type listeners.
	 *
	 * @param listener the product type listener
	 */
	public void addProductTypeListener(final ProductTypeListener listener) {
		productTypeListeners.add(listener);
	}
	
	/**
	 * Adds a warehouse listener to the list of warehouse listeners.
	 *
	 * @param listener the warehouse listener
	 */	
	public void addWarehouseListener(final WarehouseListener listener) {
		warehouseListeners.add(listener);
	}
	
	/**
	 * Removes a product type listener from the list of product type listeners. Does nothing if
	 * the listener is not apart of the list of product type listeners.
	 * 
	 * @param listener the product type listener
	 */
	public void removeProductTypeListener(final ProductTypeListener listener) {
		productTypeListeners.remove(listener);
	}
	
	/**
	 * Adds a brand listener to the list of brand listeners.
	 *
	 * @param listener the brand listener
	 */
	public void addBrandListener(final BrandListener listener) {
		brandListeners.add(listener);
	}
	
	/**
	 * Removes a brand listener from the list of brand listeners. Does nothing if
	 * the listener is not apart of the list of brand listeners.
	 * 
	 * @param listener the brand listener
	 */
	public void removeBrandListener(final BrandListener listener) {
		brandListeners.remove(listener);
	}

	/**
	 * Adds a brand listener to the list of group listeners.
	 *
	 * @param listener the group listener
	 */
	public void addGroupListener(final CartItemModifierGroupListener listener) {
		groupListeners.add(listener);
	}

	/**
	 * Removes a group listener from the list of group listeners. Does nothing if
	 * the listener is not apart of the list of group listeners.
	 *
	 * @param listener the group listener
	 */
	public void removeGroupListener(final CartItemModifierGroupListener listener) {
		groupListeners.remove(listener);
	}
	
	/**
	 * Adds a attribute listener to the list of attribute listeners.
	 *
	 * @param listener the attribute listener
	 */
	public void addAttributeListener(final AttributeListener listener) {
		attributeListeners.add(listener);
	}
	
	/**
	 * Removes a attribute listener from the list of attribute listeners. Does nothing if
	 * the listener is not apart of the list of attribute listeners.
	 * 
	 * @param listener the brand listener
	 */
	public void removeAttributeListener(final AttributeListener listener) {
		attributeListeners.remove(listener);
	}
	
	/**
	 * Removes a product listener.
	 * 
	 * @param listener the listener to remove
	 */
	public void removeProductListener(final ProductListener listener) {
		productListeners.remove(listener);
	}

	/**
	 * @param searchResultsReturnedEvent the event
	 */
	public void notifyProductSearchResultReturned(final SearchResultEvent<Product> searchResultsReturnedEvent) {
		for (final ProductListener currProductListener : this.productListeners) {
			currProductListener.productSearchResultReturned(searchResultsReturnedEvent);
		}
	}
	
	/**
	 * @param browseResultsReturnedEvent the event
	 */
	public void notifyProductBrowseResultReturned(final BrowseResultEvent<Product> browseResultsReturnedEvent) {
		for (final ProductBrowseListener currProductBrowseListener : this.productBrowseListeners) {
			currProductBrowseListener.productBrowseResultReturned(browseResultsReturnedEvent);
		}
	}
	
	/**
	 * @param searchResultsReturnedEvent the event
	 */
	public void notifyCategorySearchResultReturned(final SearchResultEvent<Category> searchResultsReturnedEvent) {
		for (final CategoryListener currCategoryListener : this.categoryListeners) {
			currCategoryListener.categorySearchResultReturned(searchResultsReturnedEvent);
		}
	}

	/**
	 * @param searchResultsReturnedEvent the event.
	 */
	public void notifyProductSkuSearchResultReturned(final SearchResultEvent<ProductSku> searchResultsReturnedEvent) {
		for (final ProductSkuListener currProductSkuListener : this.productSkuListeners) {
			currProductSkuListener.productSkuSearchResultReturned(searchResultsReturnedEvent);
		}
	}

	/**
	 * Notifies the listeners of an event.
	 * 
	 * @param productChangedEvent the event
	 */
	public void notifyProductChanged(final ItemChangeEvent<Product> productChangedEvent) {
		for (final ProductListener currProductListener : this.productListeners) {
			currProductListener.productChanged(productChangedEvent);
		}
	}
	
	/**
	 * Notifies all category listeners of a <code>CategoryChangeEvent</code>.
	 *
	 * @param categoryChangedEvent the category change event
	 */
	public void notifyCategoryChanged(final ItemChangeEvent<Category> categoryChangedEvent) {
		for (final CategoryListener currCategoryListener : categoryListeners) {
			currCategoryListener.categoryChanged(categoryChangedEvent);
		}
	}

	/**
	 * Notifies the listeners of an event.
	 * 
	 * @param event the event
	 */
	public void notifyProductSkuChanged(final ItemChangeEvent<ProductSku> event) {
		for (final ProductSkuListener currProductSkuListener : this.productSkuListeners) {
			currProductSkuListener.productSkuChanged(event);
		}
	}
	
	/**
	 * Notifies all catalog listeners of a {@link ItemChangeEvent}.
	 *
	 * @param event the event
	 */
	public void notifyCatalogChanged(final ItemChangeEvent<Catalog> event) {
		for (final CatalogListener catalogListener : catalogListeners) {
			catalogListener.catalogChanged(event);
		}
	}
	
	/**
	 * Notifies all category type listeners of category type changed.
	 *
	 * @param event the event
	 */
	public void notifyCategoryTypeChanged(final ItemChangeEvent<CategoryType> event) {
		for (CategoryTypeListener listener : categoryTypeListeners) {
			listener.categoryTypeChange(event);
		}
	}
	
	/**
	 * Notifies all product type listeners of product type changed.
	 *
	 * @param event the event
	 */
	public void notifyProductTypeChanged(final ItemChangeEvent<ProductType> event) {
		for (ProductTypeListener listener : productTypeListeners) {
			listener.productTypeChange(event);
		}
	}
	
	/**
	 * Notifies all brand listeners of brand changed.
	 *
	 * @param event the event
	 */
	public void notifyBrandChanged(final ItemChangeEvent<Brand> event) {
		for (BrandListener listener : brandListeners) {
			listener.brandChange(event);
		}
	}

	/**
	 * Notifies all brand listeners of brand changed.
	 *
	 * @param event the event
	 */
	public void notifyGroupChanged(final ItemChangeEvent<CartItemModifierGroup> event) {
		for (CartItemModifierGroupListener listener : groupListeners) {
			listener.groupChange(event);
		}
	}
	
	/**
	 * Notifies all attribute listeners of attribute changed.
	 *
	 * @param event the event
	 */
	public void notifyAttributeChanged(final ItemChangeEvent<Attribute> event) {
		for (AttributeListener listener : attributeListeners) {
			listener.attributeChange(event);
		}
	}
	
	/**
	 * Notifies all warehouse listeners of warehouse changed.
	 *
	 * @param event the event
	 */
	public void notifyWarehouseChanged(final ItemChangeEvent<Warehouse> event) {
		for (WarehouseListener listener : warehouseListeners) {
			listener.warehouseChange(event);
		}
	}
}
