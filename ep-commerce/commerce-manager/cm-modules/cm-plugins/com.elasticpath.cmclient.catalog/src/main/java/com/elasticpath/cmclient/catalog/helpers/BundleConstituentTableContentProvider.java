/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.helpers;

import org.eclipse.jface.viewers.ArrayContentProvider;

import com.elasticpath.domain.catalog.ProductBundle;

/**
 * Provides BundleConstituents for table viewer.
 */
public class BundleConstituentTableContentProvider extends ArrayContentProvider {
	@Override
	public Object[] getElements(final Object inputElement) {
		if (inputElement instanceof ProductBundle) {
			return ((ProductBundle) inputElement).getConstituents().toArray();
		}
		return new Object[0];
	}
}