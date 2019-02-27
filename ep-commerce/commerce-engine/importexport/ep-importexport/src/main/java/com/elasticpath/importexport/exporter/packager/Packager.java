/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.exporter.packager;

import java.io.InputStream;

import com.elasticpath.importexport.exporter.delivery.DeliveryMethod;

/**
 * Packaging is the step of export process occurs after transformations and before delivering.
 */
public interface Packager {

	/**
	 * Pack new entry.
	 *
	 * @param entry input stream containing entry to be added
	 * @param fileName name for new entry
	 */
	void addEntry(InputStream entry, String fileName);

	/**
	 * Notify that all entries are packed.
	 */
	void finish();

	/**
	 * Initialize packager with delivery method and the package file name.
	 *
	 * @param deliveryMethod delivery method to be used
	 * @param packageName produced package's file name
	 */
	void initialize(DeliveryMethod deliveryMethod, String packageName);
}
