/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.importer.unpackager.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.PackageType;
import com.elasticpath.importexport.importer.retrieval.RetrievalMethod;
import com.elasticpath.importexport.importer.unpackager.Unpackager;

/**
 * Tests UnpackagerFactoryImpl. 
 */
public class UnpackagerFactoryImplTest {

	private final UnpackagerFactoryImpl unpackagerFactory = new UnpackagerFactoryImpl();
	
	/**
	 * DummyUnpackager.
	 */
	private static class DummyUnpackager implements Unpackager {

		@Override
		public void initialize(final RetrievalMethod retrievalMethod) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public InputStream nextEntry() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	/**
	 * DummyRetievalMethod.
	 */
	private static class DummyRetievalMethod implements RetrievalMethod {
		@Override
		public File retrieve() {
			return null;
		}
	}
	
	/**
	 * Tests CreateUnpackager.
	 * 
	 * @throws ConfigurationException if factory can't create
	 */
	@Test
	public void testCreateUnpackager() throws ConfigurationException {
		final Map<PackageType, Unpackager> unpackagerMap = new HashMap<>();
		unpackagerMap.put(PackageType.NONE, new DummyUnpackager());
		unpackagerFactory.setPackagerMap(unpackagerMap);
		assertEquals(unpackagerMap, unpackagerFactory.getPackagerMap());

		final DummyRetievalMethod dummyRetievalMethod = new DummyRetievalMethod();
		
		Unpackager unpackager = unpackagerFactory.createUnpackager(PackageType.NONE, dummyRetievalMethod);
		assertNotNull(unpackager);
		assertTrue(unpackager instanceof DummyUnpackager);
			
		try {
			unpackagerFactory.createUnpackager(PackageType.ZIP, dummyRetievalMethod);
			fail("ConfigurationException must be thrown");
		} catch (ConfigurationException e) {
			assertNotNull(e);
		}
	}
}
