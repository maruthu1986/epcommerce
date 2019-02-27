/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.commons.util.csv;

import java.io.File;

/**
 * Util class for handling uploaded CSV files.
 */
public final class CSVFileUtil {

	private CSVFileUtil() {
		//hidden
	}

	/**
	 * Build correct path to the uploaded CSV file, using asset folder path (specified in db) and
	 * actual file name. If file is not found, CSV file parent folder's name is used.
	 *
	 * @param importAssetPath the path to asset folder where file will be uploaded
	 * @param csvFileName the actual CSV file name (may or may not include full path)
	 *
	 * @return correct path to the uploaded CSV file
	 */
	public static String getRemoteCsvFileName(final String importAssetPath, final String csvFileName) {

		File csvFile = new File(csvFileName.replace('\\', '/'));

		StringBuilder builder = new StringBuilder(importAssetPath)
			.append(File.separator);

		if (!new File(builder.toString(), csvFile.getName()).exists()) {
			//file doesn't exist - try to use its parent
			File parent = csvFile.getParentFile();

			if (parent != null) {
				builder.append(parent.getName())
					.append(File.separator);
			}
		}

		builder.append(csvFile.getName());

		return builder.toString();
	}
}
