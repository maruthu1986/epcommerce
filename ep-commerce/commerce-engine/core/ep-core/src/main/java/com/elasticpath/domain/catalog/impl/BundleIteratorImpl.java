/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.catalog.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ProductBundle;

/**
 * An iterator that iterates through bundle elements. It skips the bundle itself.
 */
public class BundleIteratorImpl implements Iterable<BundleConstituent> {
	private final BundleIteratorInternal bundleIterator = new BundleIteratorInternal();

	private final ProductBundle bundle;

	private List<BundleConstituent> items;

	/**
	 * Constructs the class with given bundle.
	 *
	 * @param bundle a bundle to be traversed
	 */
	public BundleIteratorImpl(final ProductBundle bundle) {
		this.bundle = bundle;
		this.items = new ArrayList<>();
	}

	/**
	 * @return the {@link Iterator}
	 */
	@Override
	public Iterator<BundleConstituent> iterator() {
		this.items = new ArrayList<>();
		populateItems(bundle);
		return bundleIterator;
	}

	/**
	 * Populate the items for iterator. All items are {@link BundleConstituent}.
	 *
	 * @param bundle the bundle
	 */
	void populateItems(final ProductBundle bundle) {
		for (BundleConstituent bundleConstituent : bundle.getConstituents()) {
			this.items.add(bundleConstituent);

			if (bundleConstituent.getConstituent().isBundle()) {
				populateItems((ProductBundle) bundleConstituent.getConstituent().getProduct());
			}
		}
	}

	/**
	 * An internal {@link Iterator} implementation for {@link BundleConstituent}s.
	 */
	class BundleIteratorInternal implements Iterator<BundleConstituent> {
		private int index;

		@Override
		public boolean hasNext() {
			return index < items.size();
		}

		@Override
		public BundleConstituent next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			return items.get(index++);
		}

		@Override
		public void remove() {
			// do nothing
		}

	}

}
