/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.misc;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Currency;

/**
 * Comparator for ordering Currency objects by currency code.
 */
public interface CurrencyCodeComparator extends Comparator<Currency>, Serializable {

}
