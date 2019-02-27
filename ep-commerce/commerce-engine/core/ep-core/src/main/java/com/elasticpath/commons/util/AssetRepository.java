/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
/**
 * 
 */
package com.elasticpath.commons.util;

/**
 * Provides paths to various asset storage locations as well the url path for retrieving a resource.
 */
public interface AssetRepository {

	/**
	 * Gets the absolute path to the catalog assets folder on the file system.
	 *
	 * @return the file system path to catalog assets
	 */
	String getCatalogAssetPath();
	
	/**
	 * @return the file system path to import files.
	 */
	String getImportAssetPath();
	
	/**
	 * @return the name of the top-level directory within which commerce manager assets are stored
	 */
	String getCmAssetsSubfolder();
	
	/**
	 * @return the name of the top-level directory within which themed resources are stored
	 */
	String getThemesSubfolder();
	
	/**
	 * @return the file system path to store assets.
	 */
	String getThemeAssetsPath();
	
	/**
	 * @return the system path to content wrappers repository.
	 */
	String getContentWrappersPath();

}
