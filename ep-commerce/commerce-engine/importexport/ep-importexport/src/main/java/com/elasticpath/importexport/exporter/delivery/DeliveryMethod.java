/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.exporter.delivery;

import java.io.OutputStream;

/**
 * Deliver streams to destination: file system, FTP server etc.
 */
public interface DeliveryMethod {

	/**
	 * Starts next file's delivering process.
	 *
	 * @param fileName the name of file to be delivered
	 * @return outputStream of destination to deliver into
	 */
	OutputStream deliver(String fileName);

	/**
	 * Initialize target full path.
	 *
	 * @param target destination address: URL, file name, etc.
	 */
	void initialize(String target);
}
