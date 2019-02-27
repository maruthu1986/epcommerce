/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.commons.util.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.service.environment.EnvironmentInfoService;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * Tests for AssetRepositoryImpl.
 */
public class AssetRepositoryImplTest {

	private static final String ASSET_ROOT_DIR = "foo";
	private static final String FILE_PATH = "bar";

	private AssetRepositoryImpl repository;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock
	private EnvironmentInfoService environmentInfoService;

	@Mock
	private SettingValueProvider<String> assetsPathProvider;

	@Mock
	private SettingValueProvider<String> contentWrappersSubfolderProvider;

	@Mock
	private SettingValueProvider<String> importAssetSubfolderProvider;

	@Mock
	private SettingValueProvider<String> themeAssetsSubfolderProvider;

	private boolean absolutePathToggle = true;

	/**
	 * Runs before every test case.
	 */
	@Before
	public void setUp() {
		repository = createAssetRepositoryImplWithAbsolutePathToggle();
		repository.setEnvironmentInfoService(environmentInfoService);
		repository.setAssetsPathProvider(assetsPathProvider);
		repository.setContentWrappersSubfolderProvider(contentWrappersSubfolderProvider);
		repository.setImportAssetSubfolderProvider(importAssetSubfolderProvider);
		repository.setThemeAssetsSubfolderProvider(themeAssetsSubfolderProvider);
	}

	/**
	 * Created to understand the behavior of the getCatalogAssetPath method.
	 */
	@Test
	public void testGetCatalogAssetPathWindowsAbsolutePath() {
		givenProviderHasPath(assetsPathProvider, "c:\\assets");

		absolutePathToggle = true;

		assertEquals("c:\\assets", repository.getCatalogAssetPath());
	}

	/**
	 * Test to understand the behaviour of the getCatalogAssetPath method.
	 */
	@Test
	public void testGetCatalogAssetPathWindowsRelativePath() {
		context.checking(new Expectations() { {
			oneOf(environmentInfoService).getApplicationRootPath(); will(returnValue("c:\\servletbasedir"));
		} });

		givenProviderHasPath(assetsPathProvider, "assets");

		absolutePathToggle = false;

		assertEquals("c:\\servletbasedir\\assets", FilenameUtils.separatorsToWindows(repository.getCatalogAssetPath()));
	}

	/**
	 * Test to understand the behavior of the getCatalogAssetPath method.
	 */
	@Test
	public void testGetCatalogAssetPathUnixAbsolutePath() {
		givenProviderHasPath(assetsPathProvider, "/var/ep/assets");

		absolutePathToggle = true;

		assertEquals("/var/ep/assets", FilenameUtils.separatorsToUnix(repository.getCatalogAssetPath()));
	}

	/**
	 * Test to understand the behavior of the getCatalogAssetPath method.
	 *
	 * We should be normalizing and returning the canonical absolute path, i.e. without the '../..' bits.
	 */
	@Test
	public void testGetCatalogAssetPathUnixRelativePath() {
		context.checking(new Expectations() { {
			oneOf(environmentInfoService).getApplicationRootPath(); will(returnValue("/var/deploy/ep/sf"));
		} });

		givenProviderHasPath(assetsPathProvider, "../../assets");

		absolutePathToggle = false;

		assertEquals("Should normalize relative path", "/var/deploy/assets", FilenameUtils.separatorsToUnix(repository
				.getCatalogAssetPath()));
	}

	@Test
	public void verifyGetImportAssetsPathConcatenatesAssetsDirAndSubfolder() throws Exception {
		final String expectedPath = getPath(ASSET_ROOT_DIR, FILE_PATH);

		givenProviderHasPath(assetsPathProvider, getPath(ASSET_ROOT_DIR));
		givenProviderHasPath(importAssetSubfolderProvider, FILE_PATH);

		assertEquals(expectedPath, repository.getImportAssetPath());
	}

	@Test
	public void verifyGetThemeAssetsPathConcatenatesAssetsDirAndSubfolder() throws Exception {
		final String expectedPath = getPath(ASSET_ROOT_DIR, FILE_PATH);

		givenProviderHasPath(assetsPathProvider, getPath(ASSET_ROOT_DIR));
		givenProviderHasPath(themeAssetsSubfolderProvider, FILE_PATH);

		assertEquals(expectedPath, repository.getThemeAssetsPath());
	}

	@Test
	public void verifyGetContentWrappersPathConcatenatesAssetsDirAndSubfolder() throws Exception {
		final String expectedPath = getPath(ASSET_ROOT_DIR, FILE_PATH);

		givenProviderHasPath(assetsPathProvider, getPath(ASSET_ROOT_DIR));
		givenProviderHasPath(contentWrappersSubfolderProvider, FILE_PATH);

		assertEquals(expectedPath, repository.getContentWrappersPath());
	}

	/**
	 * Creates a new AssetRepositoryImpl instance that will return the {@code absolutePathToggle} variable from the
	 * {@link AssetRepositoryImpl#isAbsolute(String)}
	 * method.
	 *
	 * @return a new AssetRepositoryImpl instance
	 */
	private AssetRepositoryImpl createAssetRepositoryImplWithAbsolutePathToggle() {
		return new AssetRepositoryImpl() {
			@Override
			boolean isAbsolute(final String path) {
				return absolutePathToggle;
			}
		};
	}

	private void givenProviderHasPath(final SettingValueProvider<String> provider, final String path) {
		context.checking(new Expectations() {
			{
				allowing(provider).get();
				will(returnValue(path));
			}
		});
	}

	private String getPath(final String... pathElements) {
		final StringBuilder path = new StringBuilder();

		for (final String pathElement : pathElements) {
			path.append(File.separator)
					.append(pathElement);
		}

		return path.toString();
	}

}
