/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.commons.util;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.exception.EpSystemException;

@RunWith(MockitoJUnitRunner.class)
public class EhcacheConfigurationLoaderTest {

	private EhcacheConfigurationLoader fixture;
	private File expectedResource;

	@After
	public void cleanUp() {
		if (expectedResource != null && expectedResource.exists()) {
			expectedResource.delete();
		}
	}

	@Test
	public void shouldReturnNullWhenResourceIsNull() {
		fixture = new EhcacheConfigurationLoader();
		fixture.setPathname(null);

		assertThat(fixture.getResource())
				.as("Returned resource must be null")
				.isNull();
	}

	@Test
	public void shouldReturnNullWhenResourceIsStringNull() {
		fixture = new EhcacheConfigurationLoader();
		fixture.setPathname("null");

		assertThat(fixture.getResource())
				.as("Returned resource must be null")
				.isNull();
	}

	@Test
	public void shouldThrowExceptionWhenResourceDoesNotExist() {
		fixture = new EhcacheConfigurationLoader();
		fixture.setPathname("file:///somewhere/ep.properties");

		assertThatThrownBy(() -> fixture.getResource())
				.isInstanceOf(EpSystemException.class);
	}

	@Test
	public void shouldReturnUrlResourceWhenResourceExists() throws Exception {
		expectedResource = File.createTempFile("tmp", "properties");

		fixture = new EhcacheConfigurationLoader();
		fixture.setPathname("file://" + expectedResource.getAbsolutePath());

		assertThat(fixture.getResource().getFile())
				.as("Returned resource must be the same as original")
				.isEqualTo(expectedResource);
	}

	@Test
	public void shouldReturnResourceWhenResourceExists() throws Exception {
		expectedResource = File.createTempFile("tmp", "properties");

		fixture = new EhcacheConfigurationLoader();
		fixture.setPathname(expectedResource.getAbsolutePath());

		assertThat(fixture.getResource().getFile())
				.as("Returned resource must be the same as original")
				.isEqualTo(expectedResource);

	}

	@Test
	public void shouldReturnUserHomeResourceWhenResourceExists() throws Exception {
		String pathname = System.getProperty("user.home") + "/ep/conf/junit-test.temp";
		expectedResource = new File(pathname);
		expectedResource.getParentFile().mkdirs();
		expectedResource.createNewFile();

		fixture = new EhcacheConfigurationLoader();
		fixture.setPathname("${user.home}/ep/conf/junit-test.temp");

		assertThat(fixture.getResource().getFile())
				.as("Returned resource must be the same as original")
				.isEqualTo(expectedResource);
	}

}
