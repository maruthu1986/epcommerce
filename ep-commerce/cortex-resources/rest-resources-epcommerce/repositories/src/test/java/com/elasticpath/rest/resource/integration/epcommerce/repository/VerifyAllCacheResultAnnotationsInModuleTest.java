/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.elasticpath.rest.cache.impl.CacheAnnotationVerifier;


/**
 * Tests all {@link com.elasticpath.rest.cache.CacheResult} annotations in this module.
 */
public class VerifyAllCacheResultAnnotationsInModuleTest {

	private static final String BASE_DATA_DIR = "target/classes";
	private final CacheAnnotationVerifier annotationVerifier = new CacheAnnotationVerifier();

	@Test
	public void verifyCacheResultsAnnotationsContainNoConflicts() throws IOException, ClassNotFoundException {
		String[] directoryNames = {BASE_DATA_DIR};

		List<List<String>> conflictingMethods = annotationVerifier.findConflictingCacheAnnotationsInDirectories(directoryNames);

		assertThat(conflictingMethods).hasSize(0);
	}

}