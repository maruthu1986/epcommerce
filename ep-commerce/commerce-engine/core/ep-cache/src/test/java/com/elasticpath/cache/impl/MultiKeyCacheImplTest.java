/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cache.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elasticpath.cache.Cache;
import com.elasticpath.cache.CacheLoader;
import com.elasticpath.persistence.api.Persistable;

public class MultiKeyCacheImplTest {
	private static final String KEY_UID_PK = "uidPk";
	private static final String KEY_GUID = "guid";

	@Mock private Cache<MultiKeyCacheImpl.CacheKey, Foo> cache;
	@Mock private CacheLoader<Long, Foo> fallbackLoader;

	private MultiKeyCacheImpl<Foo> multiKeyCache;
	private final Foo foo1 = new Foo(1L, "one", "foo1"),
			foo2 = new Foo(2L, "two", "foo2");

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		List<String> keyProperties = Arrays.asList(KEY_UID_PK, KEY_GUID);

		multiKeyCache = new MultiKeyCacheImpl<>();
		multiKeyCache.setCache(cache);
		multiKeyCache.setKeyProperties(keyProperties);
	}

	@Test
	public void ensurePutDetachesObjectAndAddsOneEntryPerKeyIntoCache() {
		//  Given

		// When
		multiKeyCache.put(foo1);

		// Then
		verify(cache).put(new MultiKeyCacheImpl.CacheKey(KEY_UID_PK, 1L), foo1);
		verify(cache).put(new MultiKeyCacheImpl.CacheKey(KEY_GUID, "one"), foo1);
	}

	@Test
	public void ensurePutIsTolerantOfNullKeyValues() {
		// Given
		foo1.setGuid(null);

		// When
		multiKeyCache.put(foo1);

		// Then
		verify(cache).put(new MultiKeyCacheImpl.CacheKey(KEY_UID_PK, 1L), foo1);
		verifyNoMoreInteractions(cache);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ensurePutPukesOnNullInput() {
		multiKeyCache.put(null);
	}

	@Test
	public void ensureGetRetrievesValuesByKey() {
		// Given
		when(cache.get(new MultiKeyCacheImpl.CacheKey(KEY_UID_PK, 2L))).thenReturn(foo2);

		// When
		Foo cached = multiKeyCache.get(KEY_UID_PK, 2L);

		// Then
		assertSame("Cache should retrieve entity", foo2, cached);
	}

	@Test
	public void ensureGetReturnsNullIfValueNotFound() {
		// Expectations
		when(cache.get(new MultiKeyCacheImpl.CacheKey(KEY_UID_PK, 2L))).thenReturn(null);

		// When
		Foo cached = multiKeyCache.get(KEY_UID_PK, 2L);

		// Then
		assertNull("Cache should not retrieve entity", cached);
	}

	@Test
	public void ensureGetWithFallbackRetrievesPreviouslyCachedValues() {
		// Given
		when(cache.get(new MultiKeyCacheImpl.CacheKey(KEY_UID_PK, 2L))).thenReturn(foo2);

		// When
		Foo cached = multiKeyCache.get(KEY_UID_PK, 2L, fallbackLoader);

		// Then
		assertSame("Cache should retrieve entity", foo2, cached);
	}

	@Test
	public void ensureGetWithFallbackDelegatesToFallbackLoaderOnCacheMiss() {
		// Given
		final MultiKeyCacheImpl.CacheKey cacheKey = new MultiKeyCacheImpl.CacheKey(KEY_UID_PK, 2L);
		when(cache.get(cacheKey)).thenReturn(null);
		when(fallbackLoader.load(2L)).thenReturn(foo2);

		// When
		Foo cached = multiKeyCache.get(KEY_UID_PK, 2L, fallbackLoader);

		// Then
		assertSame("Entity should have been retrieved from the fallback loader", foo2, cached);
		// Verify that the item retrieved from the fallback loader was added to cache
		verify(cache).put(cacheKey, foo2);
	}

	@Test
	public void ensureGetWithFallbackReturnsNullOnCacheMissAndFallbackMiss() {
		// Given
		final MultiKeyCacheImpl.CacheKey cacheKey = new MultiKeyCacheImpl.CacheKey(KEY_UID_PK, 2L);
		when(cache.get(cacheKey)).thenReturn(null);
		when(fallbackLoader.load(2L)).thenReturn(null);

		// When
		Foo cached = multiKeyCache.get(KEY_UID_PK, 2L, fallbackLoader);

		// Then
		assertNull("Entity should not have been loaded", cached);
		// Verify that the item retrieved from the fallback loader was added to cache
		verify(cache, never()).put(cacheKey, foo2);
	}

	@Test
	public void ensureGetAllReturnsCachedValues() {
		// Given
		when(cache.get(new MultiKeyCacheImpl.CacheKey(KEY_UID_PK, 1L))).thenReturn(foo1);
		when(cache.get(new MultiKeyCacheImpl.CacheKey(KEY_UID_PK, 2L))).thenReturn(foo2);

		// When
		Map<Long, Foo> found = multiKeyCache.getAll(KEY_UID_PK, Arrays.asList(1L, 2L, 0L));

		assertEquals("Cached values should have been returned",
				ImmutableMap.of(
						1L, foo1,
						2L, foo2),
				found);
	}

	@Test
	public void ensureGetAllReturnsEmptyMapIfNoHitsWereFound() {
		// Given

		// When
		Map<Long, Foo> found = multiKeyCache.getAll(KEY_UID_PK, Arrays.asList(1L, 2L, 0L));

		assertEquals("Empty map should have been returned", Collections.emptyMap(), found);
	}

	@Test
	public void ensureGetAllWithFallbackReturnsCachedProductsOnCacheHit() {
		// Given
		when(cache.get(new MultiKeyCacheImpl.CacheKey(KEY_UID_PK, 1L))).thenReturn(foo1);
		when(cache.get(new MultiKeyCacheImpl.CacheKey(KEY_UID_PK, 2L))).thenReturn(foo2);

		// When
		Map<Long, Foo> found = multiKeyCache.getAll(KEY_UID_PK, Arrays.asList(1L, 2L, 0L), fallbackLoader);

		// Then
		assertEquals("Cached values should have been returned",
				ImmutableMap.of(
						1L, foo1,
						2L, foo2),
				found);
		verify(cache, never()).put(any(MultiKeyCacheImpl.CacheKey.class), any(Foo.class));
	}

	@Test
	public void ensureGetAllWithFallbackDelegatesToFallbackLoaderOnCacheMiss() {
		// Given
		when(fallbackLoader.loadAll(Arrays.asList(1L, 2L))).thenReturn(
				ImmutableMap.of(
						1L, foo1,
						2L, foo2
				));

		// When
		Map<Long, Foo> found = multiKeyCache.getAll(KEY_UID_PK, Arrays.asList(1L, 2L), fallbackLoader);

		// Then
		assertEquals("Products should have been loaded from the fallback loader",
				ImmutableMap.of(
						1L, foo1,
						2L, foo2
				), found);
		verify(cache).put(new MultiKeyCacheImpl.CacheKey(KEY_UID_PK, 1L), foo1);
		verify(cache).put(new MultiKeyCacheImpl.CacheKey(KEY_UID_PK, 2L), foo2);
	}

	@Test
	public void ensureGetAllWithFallbackCanMixCachedAndUncachedResults() {
		// Given
		when(cache.get(new MultiKeyCacheImpl.CacheKey(KEY_UID_PK, 1L))).thenReturn(foo1);
		when(fallbackLoader.loadAll(Arrays.asList(2L))).thenReturn(
				ImmutableMap.of(2L, foo2));

		// When
		Map<Long, Foo> found = multiKeyCache.getAll(KEY_UID_PK, Arrays.asList(1L, 2L), fallbackLoader);

		// Then
		assertEquals("Products should have been loaded from both cache and the fallback loader",
				ImmutableMap.of(
						1L, foo1,
						2L, foo2
				), found);
		verify(cache, never()).put(new MultiKeyCacheImpl.CacheKey(KEY_UID_PK, 1L), foo1);
		verify(cache).put(new MultiKeyCacheImpl.CacheKey(KEY_UID_PK, 2L), foo2);
	}

	@SuppressWarnings("PMD.ShortClassName")
	public static class Foo implements Persistable {
		private static final long serialVersionUID = 1L;

		long uidPk;
		String guid;
		String payload;

		public Foo(final long uidPk, final String guid, final String payload) {
			this.uidPk = uidPk;
			this.guid = guid;
			this.payload = payload;
		}

		@Override
		public long getUidPk() {
			return uidPk;
		}

		@Override
		public void setUidPk(final long uidPk) {
			this.uidPk = uidPk;
		}

		@Override
		public boolean isPersisted() {
			return getUidPk() != 0;
		}

		public String getGuid() {
			return guid;
		}

		public void setGuid(final String guid) {
			this.guid = guid;
		}

		public String getPayload() {
			return payload;
		}

		public void setPayload(final String payload) {
			this.payload = payload;
		}

		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof Foo)) {
				return false;
			}

			Foo rhs = (Foo) obj;
			return Objects.equals(uidPk, rhs.uidPk)
				&& Objects.equals(guid, rhs.guid)
				&& Objects.equals(payload, rhs.payload);
		}

		@Override
		public int hashCode() {
			return Objects.hash(uidPk, guid, payload);
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append(KEY_UID_PK, uidPk)
					.append(KEY_GUID, guid)
					.toString();
		}
	}
}
