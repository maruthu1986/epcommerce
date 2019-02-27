/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.test.integration.caching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Tests initialization of Ehcache {@link net.sf.ehcache.CacheManager} using external
 * ehcache.xml file as it would work in non-OSGi environment (e.g. storefront, search & cm servers).
 */
public class EhCacheDefaultConfigurationFactoryBeanTest {

	private ApplicationContext appContext;

	@Before
	public void beforeTest() throws Exception{

		final String userHome = this.getClass().getClassLoader().getResource(".").getPath().replace('\\','/');
		System.setProperty("user.home", userHome);

		createEpPropertiesFile(userHome);
	}

	@After
	public void afterTest(){

		new File(System.getProperty("user.home") + "/ep/ep.properties").delete();

		closeSpringContext();
	}

	//non-OSGI environment
	@Test
	public void shouldLoadExternalEhcacheXMLWhenRunningInNonOSGiEnvironment() throws Exception{

		final Cache cache = getCache("/caching/external-ehcache-configuration-non-osgi-context.xml");

		assertExternalEhcache(cache);
	}

	//OSGi environment
	@Test
	public void shouldLoadExternalEhcacheXMLWhenRunningInOSGiEnvironment() {

		final Cache cache = getCache("/caching/external-ehcache-configuration-osgi-context.xml");

		assertExternalEhcache(cache);
	}

	private Cache getCache(final String springContextXmlPath) {
		createSpringContext(springContextXmlPath);
		Cache cache = getDefaultEhcache();

		/* previous tests may leave Ehcache manager alive despite @DirtiesContext
		* which should ensure that Spring context is closed - the workaround here
		* closes current context and creates a new one if Ehcache manager has more than
		* one cache instance - must be only 1
		* */

		if (cache.getCacheManager().getCacheNames().length > 1) {
			cache = reOpenSpringContextWithCache(springContextXmlPath);
		}

		return cache;
	}

	private Cache reOpenSpringContextWithCache(final String springContextXmlPath) {
		closeSpringContext();
		createSpringContext(springContextXmlPath);

		return getDefaultEhcache();
	}

	private Cache getDefaultEhcache() {
		return (Cache)appContext.getBean("defaultEhcacheConfiguration");
	}

	private void createSpringContext(final String springContextXmlPath) {
		appContext =  new ClassPathXmlApplicationContext(springContextXmlPath);
	}

	private void closeSpringContext() {
		((ConfigurableApplicationContext)appContext).close();
	}

	private void createEpPropertiesFile(final String userHome) throws Exception {
		final Properties epProperties = new Properties();
		epProperties.setProperty("ep.external.ehcache.xml.path", "file://" + userHome + "caching/test-ehcache.xml");

		OutputStream os = null;

		try {
			new File(userHome, "ep").mkdir();
			os = new FileOutputStream(userHome + "/ep/ep.properties");

			epProperties.store(os, "UTF-8");

		}finally {
			if (os != null){
				os.close();
			}
		}
	}

	private void assertExternalEhcache(final Ehcache externalEhcache){

		final long expectedTimeToLive = 123L;
		final long expectedTimeToIdle = 456L;
		final long expectedMaxElementsInMemory = 789L;

		assertNotNull("Cache instance cannot be null", externalEhcache);

		final CacheConfiguration cacheConfiguration = externalEhcache.getCacheConfiguration();

		assertEquals("Actual time-to-live value does not match expected one", expectedTimeToLive, cacheConfiguration.getTimeToLiveSeconds());
		assertEquals("Actual time-to-idle value does not match expected one", expectedTimeToIdle, cacheConfiguration.getTimeToIdleSeconds());
		assertEquals("Actual max elements in memory value does not match expected one", expectedMaxElementsInMemory,
																						cacheConfiguration.getMaxEntriesLocalHeap());
	}
}
