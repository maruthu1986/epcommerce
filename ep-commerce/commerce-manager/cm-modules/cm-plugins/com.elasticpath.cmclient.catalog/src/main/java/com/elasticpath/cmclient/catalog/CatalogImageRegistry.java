/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.AbstractImageRegistry;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * Provides an image registry for any plugins that require the CMClientCore. Caches the images so that they don't have to be loaded more than once.
 */
public final class CatalogImageRegistry extends AbstractImageRegistry {

	private static final String PLUGIN_ID = CatalogPlugin.PLUGIN_ID;

	/** Category Icon. * */
	public static final ImageDescriptor CATEGORY = getImageDescriptor(PLUGIN_ID, "category_default_22.png"); //$NON-NLS-1$

	/** Category Icon for tree. * */
	public static final ImageDescriptor CATEGORY_TREE = getImageDescriptor(PLUGIN_ID, "category_default_22x16.png"); //$NON-NLS-1$

	/** Category Create Icon. * */
	public static final ImageDescriptor CATEGORY_CREATE = getImageDescriptor(PLUGIN_ID, "category-create_default_22.png"); //$NON-NLS-1$

	/** Category Exclude Icon. * */
	public static final ImageDescriptor CATEGORY_EXCLUDE = getImageDescriptor(PLUGIN_ID, "category-exclude_default_22.png"); //$NON-NLS-1$

	/** Category Include Icon. * */
	public static final ImageDescriptor CATEGORY_INCLUDE = getImageDescriptor(PLUGIN_ID, "category-include_default_22.png"); //$NON-NLS-1$

	/** Category Linked Icon. * */
	public static final ImageDescriptor CATEGORY_LINKED = getImageDescriptor(PLUGIN_ID, "category-linked_default_22.png"); //$NON-NLS-1$

	/** Category Linked Icon for tree. * */
	public static final ImageDescriptor CATEGORY_LINKED_TREE = getImageDescriptor(PLUGIN_ID, "category-linked_default_22x16.png"); //$NON-NLS-1$

	/** Category Linked Excluded Icon. * */
	public static final ImageDescriptor CATEGORY_LINKED_EXCLUDED =
			getImageDescriptor(PLUGIN_ID, "category-linked-excluded_default_22.png"); //$NON-NLS-1$

	/** Category Linked Excluded Icon for tree. * */
	public static final ImageDescriptor CATEGORY_LINKED_EXCLUDED_TREE =
			getImageDescriptor(PLUGIN_ID, "category-linked-excluded_default_22x16.png"); //$NON-NLS-1$

	/** Category Linked Add Icon. * */
	public static final ImageDescriptor CATEGORY_LINKED_ADD = getImageDescriptor(PLUGIN_ID, "category-linked-add_default_22.png"); //$NON-NLS-1$
	
	/** Category Linked Remove Icon. * */
	public static final ImageDescriptor CATEGORY_LINKED_REMOVE = getImageDescriptor(PLUGIN_ID, "category-linked-remove_default_22.png"); //$NON-NLS-1$
	
	/** Master Catalog Icon. */
	public static final ImageDescriptor CATALOG_MASTER = getImageDescriptor(PLUGIN_ID, "catalog_default_22.png"); //$NON-NLS-1$

	/** Master Catalog Icon for tree. */
	public static final ImageDescriptor CATALOG_MASTER_TREE = getImageDescriptor(PLUGIN_ID, "catalog_default_22x16.png"); //$NON-NLS-1$

	/** Master Catalog Create Icon. */
	public static final ImageDescriptor CATALOG_MASTER_CREATE = getImageDescriptor(PLUGIN_ID, "catalog-create_default_22.png"); //$NON-NLS-1$
	
	/** Virtual Catalog Icon. */
	public static final ImageDescriptor CATALOG_VIRTUAL = getImageDescriptor(PLUGIN_ID, "catalog-virtual_default_22.png"); //$NON-NLS-1$

	/** Virtual Catalog Icon for tree. */
	public static final ImageDescriptor CATALOG_VIRTUAL_TREE = getImageDescriptor(PLUGIN_ID, "catalog-virtual_default_22x16.png"); //$NON-NLS-1$

	/** Virtual Catalog Create Icon. */
	public static final ImageDescriptor CATALOG_VIRTUAL_CREATE = getImageDescriptor(PLUGIN_ID, "catalog-virtual-create_default_22.png"); //$NON-NLS-1$
	
	/** Virtual Catalog Edit Icon. */
	public static final ImageDescriptor CATALOG_VIRTUAL_EDIT = getImageDescriptor(PLUGIN_ID, "catalog-virtual-edit_default_22.png"); //$NON-NLS-1$
	
	/** Product Icon. * */
	public static final ImageDescriptor PRODUCT = getImageDescriptor(PLUGIN_ID, "product_default_22.png"); //$NON-NLS-1$

	/** Product Icon. * */
	public static final ImageDescriptor PRODUCT_SMALL = getImageDescriptor(PLUGIN_ID, "product_default_16.png"); //$NON-NLS-1$

	/** Product create Icon. * */
	public static final ImageDescriptor PRODUCT_CREATE = getImageDescriptor(PLUGIN_ID, "product-create_default_22.png"); //$NON-NLS-1$

	/** Product delete Icon. * */
	public static final ImageDescriptor PRODUCT_DELETE = getImageDescriptor(PLUGIN_ID, "product-delete_default_22.png"); //$NON-NLS-1$

	/** Product include Icon. * */
	public static final ImageDescriptor PRODUCT_INCLUDE = getImageDescriptor(PLUGIN_ID, "product-include_default_22.png"); //$NON-NLS-1$
	
	/** Product exclude Icon. * */
	public static final ImageDescriptor PRODUCT_EXCLUDE = getImageDescriptor(PLUGIN_ID, "product-exclude_default_22.png"); //$NON-NLS-1$
	
	/** Product edit Icon. * */
	public static final ImageDescriptor PRODUCT_EDIT = getImageDescriptor(PLUGIN_ID, "product-edit_default_22.png"); //$NON-NLS-1$
	
	/** Product private Icon. * */
	public static final ImageDescriptor PRODUCT_PRIVATE_SMALL = getImageDescriptor(PLUGIN_ID, "product-private_default_16.png"); //$NON-NLS-1$;
	
	/** Product sku Icon. * */
	public static final ImageDescriptor PRODUCT_SKU = getImageDescriptor(PLUGIN_ID, "sku_default_22.png"); //$NON-NLS-1$

	/** Product sku small Icon. * */
	public static final ImageDescriptor PRODUCT_SKU_SMALL = getImageDescriptor(PLUGIN_ID, "sku_default_16.png"); //$NON-NLS-1$

	/** Add icon. */
	public static final ImageDescriptor ADD = getImageDescriptor(PLUGIN_ID, "add_default_22.png"); //$NON-NLS-1$
	
	/** Edit icon. */
	public static final ImageDescriptor EDIT = getImageDescriptor(PLUGIN_ID, "edit_default_22.png"); //$NON-NLS-1$
	
	/** Remove icon. */
	public static final ImageDescriptor REMOVE = getImageDescriptor(PLUGIN_ID, "delete_default_22.png"); //$NON-NLS-1$
	
	/** Move up icon. */
	public static final ImageDescriptor MOVE_UP = getImageDescriptor(PLUGIN_ID, "move-up_default_22.png"); //$NON-NLS-1$
	
	/** Move down icon. */
	public static final ImageDescriptor MOVE_DOWN = getImageDescriptor(PLUGIN_ID, "move-down_default_22.png"); //$NON-NLS-1$

	/** Not available Image. */
	public static final ImageDescriptor IMAGE_NOT_AVAILABLE = getImageDescriptor(PLUGIN_ID, "image-not-available.jpg"); //$NON-NLS-1$
	
	/** Clear attribute value Image. */
	public static final ImageDescriptor CLEAR = getImageDescriptor(PLUGIN_ID, "clear_default_22.png"); //$NON-NLS-1$
	
	/** Search Image. */
	public static final ImageDescriptor SEARCH = getImageDescriptor(PLUGIN_ID, "search_default_22.png"); //$NON-NLS-1$
	
	/** Edit attributes edit. */
	public static final ImageDescriptor ATTRIBUTE_EDIT = getImageDescriptor(PLUGIN_ID, "attribute-edit_default_22.png"); //$NON-NLS-1$

	/** Edit cell. */
	public static final ImageDescriptor EDIT_CELL_SMALL = getImageDescriptor(PLUGIN_ID, "edit-cell_default_16.png"); //$NON-NLS-1$

	/** product bundle. */
	public static final ImageDescriptor PRODUCT_BUNDLE = getImageDescriptor(PLUGIN_ID, "bundle_default_22.png"); //$NON-NLS-1$

	/** product bundle small image. */
	public static final ImageDescriptor PRODUCT_BUNDLE_SMALL = getImageDescriptor(PLUGIN_ID, "bundle_default_16.png"); //$NON-NLS-1$

	/** product bundle when add. */
	public static final ImageDescriptor PRODUCT_BUNDLE_CREATE = getImageDescriptor(PLUGIN_ID, "bundle-add_default_22.png"); //$NON-NLS-1$
	
	/** bullet image for select all. */
	public static final ImageDescriptor BUNDLE_ITEM_SELECTED = getImageDescriptor(PLUGIN_ID, "tick_default_22.png"); //$NON-NLS-1$
	
	/** checkbox on for multiple selection. */
	public static final ImageDescriptor BUNDLE_ITEM_CHECKBOX_ON = getImageDescriptor(PLUGIN_ID, "checkbox-on_default_22.png"); //$NON-NLS-1$
	
	/** checkbox off for multiple selection. */
	public static final ImageDescriptor BUNDLE_ITEM_CHECKBOX_OFF = getImageDescriptor(PLUGIN_ID, "checkbox-off_default_22.png"); //$NON-NLS-1$
	
	/** radio button on for single selection. */
	public static final ImageDescriptor BUNDLE_ITEM_RADIO_ON = getImageDescriptor(PLUGIN_ID, "radio-on_default_22.png"); //$NON-NLS-1$
	
	/** radio button off for single selection. */
	public static final ImageDescriptor BUNDLE_ITEM_RADIO_OFF = getImageDescriptor(PLUGIN_ID, "radio-off_default_22.png"); //$NON-NLS-1$


	/**
	 * Gets the proper image for {@link Product}.
	 *
	 * @param product {@link Product}. Can't be null.
	 *
	 * @return {@link Image}.
	 */
	public static Image getSmallImageForProduct(final Product product) {
		if (product instanceof ProductBundle) {
			return getImage(PRODUCT_BUNDLE_SMALL);
		}
		return getImage(PRODUCT_SMALL);
	}

	/**
	 * Gets the proper image for product editor tab.
	 *
	 * @param product {@link Product}. Can't be null.
	 * @return {@link Image}.
	 */
	public static Image getProductEditorTabImage(final Product product) {

		ImageDescriptor imageDescriptor;
		if (product instanceof ProductBundle) {
			imageDescriptor = PRODUCT_BUNDLE;
		} else {
			imageDescriptor = PRODUCT;
		}

		Image tabImage = getImage(imageDescriptor);

		if (tabImage.getDevice().isDisposed()) {
			tabImage = reCreateImage(imageDescriptor, PLUGIN_ID);
		}

		return tabImage;
	}

	/**
	 * Gets the proper small image for {@link ProductSku}.
	 *
	 * @param productSku {@link ProductSku}. Can't be null.
	 *
	 * @return {@link Image}.
	 */
	public static Image getSmallImageForProductSku(final ProductSku productSku) {
		if (productSku.getProduct().hasMultipleSkus()) {
			return getImage(PRODUCT_SKU_SMALL);
		}

		if (productSku.getProduct() instanceof ProductBundle) {
			return getImage(PRODUCT_BUNDLE_SMALL);
		}

		return getImage(PRODUCT_SMALL);
	}

	private CatalogImageRegistry() {
		// utility class
	}

	/**
	 * Returns and instance of <code>Image</code> of an <code>ImageDescriptor</code>.
	 *
	 * @param imageDescriptor the image descriptor
	 * @return instance of an <code>Image</code>
	 */
	public static Image getImage(final ImageDescriptor imageDescriptor) {
		return getImage(imageDescriptor, PLUGIN_ID);
	}

	/**
	 * Disposes all the images in the <code>HashMap</code>. Should be called by the Plugin's stop method.
	 */
	public static void disposeAllImages() {
		disposeAllImages(PLUGIN_ID);
	}
}
