/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.catalog.views;

import java.util.Locale;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;

/**
 * Provides labels for the CatalogBrowseView TreeViewer.
 */
public class CatalogBrowseViewLabelProvider extends LabelProvider {

	private final Locale locale;
	
	/**
	 * Constructs a new CatalogBrowseViewLabelProvider.
	 * @param locale the locale for which the labels should be presented.
	 */
	public CatalogBrowseViewLabelProvider(final Locale locale) {
		this.locale = locale;
	}
	
	@Override
	public String getText(final Object obj) {
		if (obj instanceof Catalog) {
			return ((Catalog) obj).getName();
		} else if (obj instanceof Category) {
			final Category category = (Category) obj;
			return category.getDisplayName(locale);
		}
		return CatalogMessages.get().CatalogBrowseView_LabelProvider_DefaultText;
	}

	@Override
	public Image getImage(final Object obj) {
		if (obj instanceof Catalog) {
			if (((Catalog) obj).isMaster()) {
				return CatalogImageRegistry.getImage(CatalogImageRegistry.CATALOG_MASTER_TREE);
			}
			return CatalogImageRegistry.getImage(CatalogImageRegistry.CATALOG_VIRTUAL_TREE);
		} else if (obj instanceof Category) {
			if (((Category) obj).isLinked() && ((Category) obj).isIncluded()) {
				return CatalogImageRegistry.getImage(CatalogImageRegistry.CATEGORY_LINKED_TREE);
			} else if (((Category) obj).isLinked() && !((Category) obj).isIncluded()) {
				return CatalogImageRegistry.getImage(CatalogImageRegistry.CATEGORY_LINKED_EXCLUDED_TREE);
			}
			return CatalogImageRegistry.getImage(CatalogImageRegistry.CATEGORY_TREE);
		}
		return null;
	}
}
