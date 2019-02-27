/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.commons.util.impl;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.service.environment.EnvironmentInfoService;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * Provides paths to various Asset storage locations as well the url path for retrieving a resource.
 * This implementation ensures that the paths are valid for the platform
 * upon which the JVM is running.
 */
public class AssetRepositoryImpl implements AssetRepository {

	private EnvironmentInfoService environmentInfoService;

	private SettingValueProvider<String> assetsPathProvider;
	private SettingValueProvider<String> cmAssetsSubfolderProvider;
	private SettingValueProvider<String> contentWrappersSubfolderProvider;
	private SettingValueProvider<String> importAssetSubfolderProvider;
	private SettingValueProvider<String> themeAssetsSubfolderProvider;

	@Override
	public String getCatalogAssetPath() {
		final String definedPath = getAssetsPathProvider().get();

		if (!isAbsolute(definedPath)) {
			return getAbsoluteCatalogAssetPathFromRelativePath(definedPath);
		}
		return definedPath;
	}

	/**
	 * Takes in a relative path to the catalog assets directory and converts it
	 * to an absolute path, assuming that the given path is relative to the
	 * web application's root. Calls {{@link #getApplicationRootPath()}.
	 * 
	 * @param relativePath the relative path to the catalog assets directory
	 * @return the absolute path to the catalog assets directory
	 */
	String getAbsoluteCatalogAssetPathFromRelativePath(final String relativePath) {
		String absolutePath = FilenameUtils.concat(getApplicationRootPath(), relativePath);
		if (absolutePath == null) {
			absolutePath = relativePath;
		}
		return absolutePath;
	}
	
	/**
	 * Get the application's absolute root path.
	 * This implementation gets the path to the WEB-INF directory from the ElasticPath
	 * singleton, and strips off the trailing 'WEB-INF' portion to retrieve the root
	 * path to the web application.
	 * @return the root path to the application.
	 */
	String getApplicationRootPath() {
		return getEnvironmentInfoService().getApplicationRootPath();
	}

	@Override
	public String getImportAssetPath() {
		return FilenameUtils.concat(getCatalogAssetPath(), getImportAssetSubfolderProvider().get());
	}

	@Override
	public String getThemeAssetsPath() {
		return FilenameUtils.concat(getCatalogAssetPath(), getThemesSubfolder());
	}
	
	@Override
	public String getThemesSubfolder() {
		return getThemeAssetsSubfolderProvider().get();
	}

	@Override
	public String getCmAssetsSubfolder() {
		return getCmAssetsSubfolderProvider().get();
	}

	@Override
	public String getContentWrappersPath() {
		final String contentWrapperSubDirectory = getContentWrappersSubfolderProvider().get();
		
		return FilenameUtils.concat(getCatalogAssetPath(), contentWrapperSubDirectory);
	}
	
	/**
	 * Test hook to allow testing of getCatalogAssetPath().
	 * http://commons.apache.org/io/api-release/org/apache/commons/io/FilenameUtils.html
	 * @param path the path to check.
	 * @return true if the path represents an absolute path location, false otherwise.
	 */
	boolean isAbsolute(final String path) {
		return new File(path).isAbsolute();
	}

	protected EnvironmentInfoService getEnvironmentInfoService() {
		return environmentInfoService;
	}

	public void setEnvironmentInfoService(final EnvironmentInfoService environmentInfoService) {
		this.environmentInfoService = environmentInfoService;
	}

	protected SettingValueProvider<String> getAssetsPathProvider() {
		return assetsPathProvider;
	}

	public void setAssetsPathProvider(final SettingValueProvider<String> assetsPathProvider) {
		this.assetsPathProvider = assetsPathProvider;
	}

	protected SettingValueProvider<String> getImportAssetSubfolderProvider() {
		return importAssetSubfolderProvider;
	}

	public void setImportAssetSubfolderProvider(final SettingValueProvider<String> importAssetSubfolderProvider) {
		this.importAssetSubfolderProvider = importAssetSubfolderProvider;
	}

	protected SettingValueProvider<String> getCmAssetsSubfolderProvider() {
		return cmAssetsSubfolderProvider;
	}

	public void setCmAssetsSubfolderProvider(final SettingValueProvider<String> cmAssetsSubfolderProvider) {
		this.cmAssetsSubfolderProvider = cmAssetsSubfolderProvider;
	}

	protected SettingValueProvider<String> getThemeAssetsSubfolderProvider() {
		return themeAssetsSubfolderProvider;
	}

	public void setThemeAssetsSubfolderProvider(final SettingValueProvider<String> themeAssetsSubfolderProvider) {
		this.themeAssetsSubfolderProvider = themeAssetsSubfolderProvider;
	}

	protected SettingValueProvider<String> getContentWrappersSubfolderProvider() {
		return contentWrappersSubfolderProvider;
	}

	public void setContentWrappersSubfolderProvider(final SettingValueProvider<String> contentWrappersSubfolderProvider) {
		this.contentWrappersSubfolderProvider = contentWrappersSubfolderProvider;
	}

}
