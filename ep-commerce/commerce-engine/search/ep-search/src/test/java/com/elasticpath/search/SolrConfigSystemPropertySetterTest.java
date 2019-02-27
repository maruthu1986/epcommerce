/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static com.elasticpath.service.search.solr.SolrIndexConstants.CATEGORY_INDEX_DIR_PROPERTY;
import static com.elasticpath.service.search.solr.SolrIndexConstants.PROMOTION_INDEX_DIR_PROPERTY;
import static com.elasticpath.service.search.solr.SolrIndexConstants.SKU_INDEX_DIR_PROPERTY;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Test class for {@link SolrConfigSystemPropertySetter}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SolrConfigSystemPropertySetterTest {

	private static final String CONFIGURATION_ROOT_PATH = "." + File.separator + "foo";

	private SolrConfigSystemPropertySetter systemPropertySetter;

	private final Map<IndexType, String> indexTypeSystemPropertyKeyMap = Maps.newHashMap();

	@Before
	public void setUp() {
		systemPropertySetter = new SolrConfigSystemPropertySetter();
		systemPropertySetter.setConfigurationRootPath(CONFIGURATION_ROOT_PATH);
		systemPropertySetter.setIndexTypeSystemPropertyKeyMap(indexTypeSystemPropertyKeyMap);
	}

	@Before
	@After
	public void clearSystemProperties() {
		System.clearProperty(SolrIndexConstants.SOLR_HOME_PROPERTY);

		for (final String systemProperty : indexTypeSystemPropertyKeyMap.values()) {
			System.clearProperty(systemProperty);
		}
	}

	@Test
	public void verifySolrHomeNotSetWhenAlreadyProvided() throws Exception {
		final String existingValue = "./foo";

		System.setProperty(SolrIndexConstants.SOLR_HOME_PROPERTY, existingValue);

		systemPropertySetter.setSolrConfigProperties();

		assertThat(System.getProperty(SolrIndexConstants.SOLR_HOME_PROPERTY))
				.as("Values set by the JVM should take precedence.")
				.isEqualTo(existingValue);
	}

	@Test
	public void verifySolrHomeSetWhenAbsent() throws Exception {
		final String expectedSolrHomeDirectory = solrHomeDirectoryWithBase(CONFIGURATION_ROOT_PATH);

		systemPropertySetter.setSolrConfigProperties();

		assertThat(System.getProperty(SolrIndexConstants.SOLR_HOME_PROPERTY))
				.isEqualTo(expectedSolrHomeDirectory);
	}

	@Test
	public void verifyIndexDirectoriesSetWhenAbsent() throws Exception {
		final String categoryIndexDir = "catDir";
		final String promoIndexDir = "indexDir";

		indexTypeSystemPropertyKeyMap.putAll(ImmutableMap.of(
				IndexType.CATEGORY, CATEGORY_INDEX_DIR_PROPERTY,
				IndexType.PROMOTION, PROMOTION_INDEX_DIR_PROPERTY
		));

		systemPropertySetter.setIndexDataSubdirectories(ImmutableMap.<IndexType, String>builder()
				.put(IndexType.CATEGORY, categoryIndexDir)
				.put(IndexType.PROMOTION, promoIndexDir)
				.build());

		systemPropertySetter.setSolrConfigProperties();

		final SoftAssertions softly = new SoftAssertions();

		softly.assertThat(System.getProperty(CATEGORY_INDEX_DIR_PROPERTY))
				.isEqualTo(indexDir(CONFIGURATION_ROOT_PATH, categoryIndexDir));

		softly.assertThat(System.getProperty(PROMOTION_INDEX_DIR_PROPERTY))
				.isEqualTo(indexDir(CONFIGURATION_ROOT_PATH, promoIndexDir));

		softly.assertAll();
	}

	@Test
	public void verifyIndexDirectoriesNotSetWhenAlreadyProvided() throws Exception {
		final String categoryIndexDirOriginal = "catDirOriginal";
		final String promoIndexDirOriginal = "indexDirOriginal";
		final String skuIndexDirOriginal = "skuDirOriginal";

		indexTypeSystemPropertyKeyMap.putAll(ImmutableMap.of(
				IndexType.CATEGORY, CATEGORY_INDEX_DIR_PROPERTY,
				IndexType.PROMOTION, PROMOTION_INDEX_DIR_PROPERTY,
				IndexType.SKU, SKU_INDEX_DIR_PROPERTY
		));

		systemPropertySetter.setIndexDataSubdirectories(ImmutableMap.<IndexType, String>builder()
				.put(IndexType.CATEGORY, "/catDirConfigured")
				.put(IndexType.PROMOTION, "/indexDirConfigured")
				.put(IndexType.SKU, "/skuDirConfigured")
				.build());

		System.setProperty(CATEGORY_INDEX_DIR_PROPERTY, categoryIndexDirOriginal);
		System.setProperty(PROMOTION_INDEX_DIR_PROPERTY, promoIndexDirOriginal);
		System.setProperty(SKU_INDEX_DIR_PROPERTY, skuIndexDirOriginal);

		systemPropertySetter.setSolrConfigProperties();

		final SoftAssertions softly = new SoftAssertions();

		softly.assertThat(System.getProperty(CATEGORY_INDEX_DIR_PROPERTY))
				.isEqualTo(categoryIndexDirOriginal);

		softly.assertThat(System.getProperty(PROMOTION_INDEX_DIR_PROPERTY))
				.isEqualTo(promoIndexDirOriginal);

		softly.assertThat(System.getProperty(SKU_INDEX_DIR_PROPERTY))
				.isEqualTo(skuIndexDirOriginal);

		softly.assertAll();
	}

	@Test
	public void verifyExceptionThrownWhenKeyMismatchBetweenMaps() throws Exception {
		indexTypeSystemPropertyKeyMap.putAll(ImmutableMap.of(
				IndexType.CATEGORY, CATEGORY_INDEX_DIR_PROPERTY
		));

		systemPropertySetter.setIndexDataSubdirectories(ImmutableMap.<IndexType, String>builder()
				.put(IndexType.CATEGORY, "/catDirConfigured")
				.put(IndexType.PROMOTION, "/indexDirConfigured")
				.build());

		assertThatThrownBy(() -> systemPropertySetter.setSolrConfigProperties())
				.isInstanceOf(IllegalStateException.class);
	}

	private String solrHomeDirectoryWithBase(final String solrHomeBaseDirectory) {
		return Paths.get(solrHomeBaseDirectory, SolrIndexConstants.SOLR_HOME_DIR).toString();
	}

	private String indexDir(final String solrHomeBaseDirectory, final String indexSubDir) {
		return Paths.get(solrHomeDirectoryWithBase(solrHomeBaseDirectory), indexSubDir).toString();
	}

}