/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.commons.beanframework;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;
import org.junit.After;
import org.junit.Test;


/**
 * Tests {@link EhCacheDefaultConfigurationFactoryBean}.
 */
public class EhCacheDefaultConfigurationFactoryBeanTest {

	private static final String CACHE_NAME = "cache name";

	private static final String BEAN_NAME = "bean name";

	private EhCacheDefaultConfigurationFactoryBean factory;

	/**
	 * Tests the cache name for the case only the bean name is set.
	 */
	@Test
	public void testNoName() {
		factory = new EhCacheDefaultConfigurationFactoryBean() {
			@Override
			protected Ehcache createCache() {
				return null;
			}
		};

		factory.setBeanName(BEAN_NAME);

		factory.afterPropertiesSet();

		assertThat(factory.getName()).isEqualTo(BEAN_NAME);
	}

	/**
	 * Tests the cache name for the case both the name and the bean name are set.
	 */
	@Test
	public void testNameAndBeanName() {
		factory = new EhCacheDefaultConfigurationFactoryBean() {
			@Override
			protected Ehcache createCache() {
				return null;
			}
		};
		factory.setBeanName(BEAN_NAME);
		factory.setName(CACHE_NAME);

		factory.afterPropertiesSet();
		assertThat(factory.getName()).isEqualTo(CACHE_NAME);
	}

	/**
	 * Verifies that a custom cache manager will be used if it is set.
	 */
	@Test
	public void testCacheManager() {
		final CacheManager cacheManager = mock(CacheManager.class);

		factory = new EhCacheDefaultConfigurationFactoryBean() {
			@Override
			protected Ehcache createCache() {
				return null;
			}
		};
		factory.setCacheManager(cacheManager);

		factory.afterPropertiesSet();

		assertThat(factory.getCacheManager()).isEqualTo(cacheManager);
	}

	/**
	 * Verifies that the default cache manager will be used if none is specified.
	 */
	@Test
	public void testNoCacheManager() {
		factory = new EhCacheDefaultConfigurationFactoryBean() {
			@Override
			protected Ehcache createCache() {
				return null;
			}
		};

		factory.afterPropertiesSet();

		assertThat(factory.getCacheManager()).isEqualTo(CacheManager.getInstance());
	}

	/**
	 * Tests creating a cache without mocking anything. Overrides two of the three configurations. The Ehcache library is packed with static and
	 * final methods, and there is no way for JMock to mock those methods.
	 */
	@Test
	public void testCreateCache() {
		factory = new EhCacheDefaultConfigurationFactoryBean();

		final long timeToIdle = 1001L;
		final long timeToLive = 2002L;
		final long maxEntriesLocalHeap = 3003L;

		factory.setTimeToIdle(timeToIdle);
		factory.setTimeToLive(timeToLive);
		factory.setMaxEntriesLocalHeap(maxEntriesLocalHeap);
		factory.setBeanName(BEAN_NAME);

		Ehcache cache = doGetCache();

		CacheConfiguration configuration = cache.getCacheConfiguration();

		assertThat(configuration.getTimeToIdleSeconds()).isEqualTo(timeToIdle);
		assertThat(configuration.getMaxEntriesLocalHeap()).isEqualTo(maxEntriesLocalHeap);

		assertThat(configuration.getTimeToLiveSeconds()).isEqualTo(timeToLive);

		assertThat(configuration.getPersistenceConfiguration().getStrategy())
				.isEqualTo(PersistenceConfiguration.Strategy.NONE);

		assertThat(cache.getName()).isEqualTo(BEAN_NAME);

		assertThat(CacheManager.getInstance().cacheExists(BEAN_NAME)).isTrue();
	}

	/**
	 * When cache instances are loaded from an external ehcache.xml file, they are all initialized (Status.STATUS_ALIVE) and
	 * createCache and createCacheInstance methods must return initialized instance.
	 */
	@Test
	public void testReturnExistingInitializedCache() {

		final long timeToIdle = 100L;
		final long timeToLive = 100L;
		final int maxEntriesLocalHeap = 1;

		//Given
		factory = new EhCacheDefaultConfigurationFactoryBean();
		factory.setName(CACHE_NAME);
		factory.setCacheManager(factory.getDefaultCacheManager());

		final Cache expectedCache = new Cache(CACHE_NAME, maxEntriesLocalHeap, false, false, timeToLive, timeToIdle);
		expectedCache.setCacheManager(factory.getCacheManager());
		expectedCache.initialise();

		factory.getCacheManager().addDecoratedCache(expectedCache);

		//When
		final Ehcache actualCache = doGetCache();

		assertThat(actualCache).isEqualTo(expectedCache);

		final CacheConfiguration configuration = actualCache.getCacheConfiguration();

		assertThat(configuration.getTimeToIdleSeconds()).isEqualTo(timeToIdle);
		assertThat(configuration.getMaxEntriesLocalHeap()).isEqualTo(maxEntriesLocalHeap);

		assertThat(configuration.getTimeToLiveSeconds()).isEqualTo(timeToLive);

		assertThat(actualCache.getName()).isEqualTo(CACHE_NAME);

		assertThat(factory.getCacheManager().cacheExists(CACHE_NAME)).isTrue();

	}

		/**
		 * Tests creating a cache without mocking anything. Doesn't override anything from the configuration.
		 * The Ehcache library is packed with static and final methods, and there is no way for JMock to mock those methods.
		 */
	@Test
	public void testDefaultConfiguration() {
		factory = new EhCacheDefaultConfigurationFactoryBean();
		factory.setBeanName(BEAN_NAME);

		Ehcache cache = doGetCache();

		CacheConfiguration defaultConfiguration = CacheManager.getInstance().getConfiguration().getDefaultCacheConfiguration();
		CacheConfiguration cacheConfiguration = cache.getCacheConfiguration();

		assertThat(defaultConfiguration).isNotEqualTo(cacheConfiguration);
		assertThat(defaultConfiguration.getTimeToIdleSeconds()).isEqualTo(cacheConfiguration.getTimeToIdleSeconds());
		assertThat(defaultConfiguration.getMaxEntriesLocalHeap()).isEqualTo(cacheConfiguration.getMaxEntriesLocalHeap());

		assertThat(defaultConfiguration.getTimeToLiveSeconds()).isEqualTo(cacheConfiguration.getTimeToLiveSeconds());

	}

	/**
	 * Test that the created cache is removed when the destruction callback is called.
	 *
	 * @throws Exception to accommodate the method signature of {@link org.springframework.beans.factory.DisposableBean#destroy()}
	 */
	@Test
	public void testCacheIsRemovedOnDestroy() throws Exception {
		final CacheManager cacheManager = mock(CacheManager.class);
		EhCacheDefaultConfigurationFactoryBean factoryBean = createFactoryBeanWithCacheManager(BEAN_NAME, cacheManager);
		when(cacheManager.getStatus()).thenReturn(Status.STATUS_ALIVE);
		factoryBean.destroy();
		verify(cacheManager, times(1)).removeCache(BEAN_NAME);
	}

	/**
	 * Test that any exceptions thrown by the {@link CacheManager} during removal of the cache are not
	 * swallowed by the factory.
	 *
	 * @throws Exception to accommodate the method signature of {@link org.springframework.beans.factory.DisposableBean#destroy()}
	 */
	@Test(expected = IllegalStateException.class)
	public void testExceptionPropagatedOnDestroy() throws Exception {
		final CacheManager cacheManager = mock(CacheManager.class);
		EhCacheDefaultConfigurationFactoryBean factoryBean = createFactoryBeanWithCacheManager(BEAN_NAME, cacheManager);

		doThrow(new IllegalStateException()).when(cacheManager).removeCache(BEAN_NAME);
		when(cacheManager.getStatus()).thenReturn(Status.STATUS_ALIVE);

		factoryBean.destroy();
	}

	@Test
	public void testCacheNotRemovedWhenCacheManagerIsNotAliveOnDestroy() throws Exception {
		final CacheManager cacheManager = mock(CacheManager.class);
		EhCacheDefaultConfigurationFactoryBean factoryBean = createFactoryBeanWithCacheManager(BEAN_NAME, cacheManager);
		when(cacheManager.getStatus()).thenReturn(Status.STATUS_SHUTDOWN);
		factoryBean.destroy();
		verify(cacheManager, never()).removeCache(BEAN_NAME);
	}

	/**
	 * Tests whether proper persistence strategy is set.
	 */
	@Test
	public void testCreateCacheWithProperPersistenceStrategy() {
		factory = new EhCacheDefaultConfigurationFactoryBean();
		factory.setBeanName(BEAN_NAME);
		factory.setPersistenceStrategy(Strategy.LOCALTEMPSWAP.toString());

		Ehcache cache = doGetCache();

		CacheConfiguration configuration = cache.getCacheConfiguration();

		assertThat(configuration.getPersistenceConfiguration().getStrategy())
				.isEqualTo(Strategy.LOCALTEMPSWAP);

	}

	/**
	 * Tests whether default persistence strategy is set when an invalid value is being passed in for persistence strategy.
	 */
	@Test
	public void testCreateCacheForInvalidPersistenceStrategy() {
		factory = new EhCacheDefaultConfigurationFactoryBean();
		factory.setBeanName(BEAN_NAME);
		factory.setPersistenceStrategy("invalid");

		Ehcache cache = doGetCache();

		CacheConfiguration configuration = cache.getCacheConfiguration();

		assertThat(configuration.getPersistenceConfiguration().getStrategy()).
				isEqualTo(Strategy.NONE);

	}

	private EhCacheDefaultConfigurationFactoryBean createFactoryBeanWithCacheManager(final String beanName, final CacheManager cacheManager) {
		EhCacheDefaultConfigurationFactoryBean factoryBean = new EhCacheDefaultConfigurationFactoryBean();
		factoryBean.setName(beanName);
		factoryBean.setCacheManager(cacheManager);
		return factoryBean;
	}

	/**
	 * Runs after each test.
	 */
	@After
	public void tearDown() {
		CacheManager.getInstance().shutdown();
	}

	private Ehcache doGetCache() {
		factory.afterPropertiesSet();
		return factory.getObject();
	}
}
