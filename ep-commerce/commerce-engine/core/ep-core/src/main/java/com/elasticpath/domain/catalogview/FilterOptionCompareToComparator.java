/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.catalogview;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A comparator for range {@link FilterOption}s.
 * 
 * @param <T> the type of filter
 */
public interface FilterOptionCompareToComparator<T extends Filter<T>> extends Comparator<FilterOption<T>>, Serializable {

}