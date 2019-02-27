/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.cache.impl;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elasticpath.cache.Cache;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;

public class OpenJPAEntityCacheDecoratorTest {
	@Mock private Cache<String, SamplePersistable> baseCache;
	@Mock private PersistenceEngine persistenceEngine;
	private OpenJPAEntityCacheDecorator<String, SamplePersistable> decorator;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		decorator = new OpenJPAEntityCacheDecorator<>(baseCache, persistenceEngine);
	}

	@Test
	public void testPutDetachesObjectsBeforePuttingThemInCache() throws Exception {
		// Given
		final SamplePersistable attached = new SamplePersistable(1L);
		final SamplePersistable detached = new SamplePersistable(1L);
		when(persistenceEngine.detach(attached)).thenReturn(detached);

		// When
		decorator.put("sample", attached);

		// Then
		InOrder inOrder = inOrder(persistenceEngine, baseCache);
		inOrder.verify(persistenceEngine).detach(attached);
		inOrder.verify(baseCache).put("sample", detached);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testUnwrapReturnsTheDecoratorIfAsked() throws Exception {
		OpenJPAEntityCacheDecorator<String, SamplePersistable> unwrapped = decorator.unwrap(
				OpenJPAEntityCacheDecorator.class);
		assertSame("Unwrap should return the decorator if asked for the decorator class", unwrapped, decorator);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testUnwrapDelegatesToTheDecoratedCacheIfAskedForAClassOtherThanItself() throws Exception {
		// When
		when(baseCache.unwrap(Cache.class)).thenReturn(baseCache);

		// Given
		Cache<String, SamplePersistable> unwrapped = decorator.unwrap(Cache.class);

		// Then
		assertSame("Unwrap should have been delegate to the base cache", baseCache, unwrapped);
	}

	private static class SamplePersistable implements Persistable {
		private static final long serialVersionUID = 1L;

		private long uidpk;

		SamplePersistable(final long uidpk) {
			this.uidpk = uidpk;
		}

		@Override
		public long getUidPk() {
			return uidpk;
		}

		@Override
		public void setUidPk(final long uidPk) {
			this.uidpk = uidPk;
		}

		@Override
		public boolean isPersisted() {
			return uidpk != 0;
		}
	}
}
