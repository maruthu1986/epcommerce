/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.importexport.common.comparators;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * A comparator that compares two lists.
 * @param <T> the type of elements in the list.
 *
 * Examples:
 * [1,2,5,1] > [1,2,5] (because lists are identical, except list one has one more element)
 * [1,2,5] < [1,1000] (because 2 < 1000 in the second element)
 * [1,2,5] > [1,1,1000] (because 2 > 1 in the second element)
 * [1,2,5] < [1,1000,1] (because 1000 > 2 in the second element)
 */
public class ListComparator<T> implements Serializable, Comparator<List<T>> {
	/**
	 * Serial version UID.
	 */
	public static final long serialVersionUID = 1L;

	private final Comparator<T> elementComparator;

	/**
	 * Constructor that takes a comparator for the list elements.
	 * @param elementComparator the list element comparator
	 */
	public ListComparator(final Comparator<T> elementComparator) {
		this.elementComparator = elementComparator;
	}

	@Override
	public int compare(final List<T> list1, final List<T> list2) {
		for (int index = 0; index < Math.min(list1.size(), list2.size()); index++) {
			T object1 = list1.get(index);
			T object2 = list2.get(index);
			int comparison = elementComparator.compare(object1, object2);
			if (comparison != 0) {
				return comparison;
			}
		}
		return list1.size() - list2.size();
	}
}
