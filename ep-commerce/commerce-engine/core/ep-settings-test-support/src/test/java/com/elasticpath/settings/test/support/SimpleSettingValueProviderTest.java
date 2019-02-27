/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.test.support;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * SimpleSettingValueProviderTest.
 */
public class SimpleSettingValueProviderTest {

	@Test
	public void verifySameInstanceIsReturnedFromGet() throws Exception {
		final Object input = new Object();

		final Object output = new SimpleSettingValueProvider<>(input).get();

		assertSame(input, output);
	}

	@Test
	public void verifySameMapInstanceIsReturnedFromGetWithContext() throws Exception {
		final String key1 = "key1";
		final String key2 = "key2";

		final Object input1 = new Object();
		final Object input2 = new Object();

		final Map<String, Object> instanceMap = new HashMap<>();
		instanceMap.put(key1, input1);
		instanceMap.put(key2, input2);

		final SimpleSettingValueProvider<Object> settingValueProvider = new SimpleSettingValueProvider<>(instanceMap);

		assertSame(input1, settingValueProvider.get(key1));
		assertSame(input2, settingValueProvider.get(key2));
	}

	@Test
	public void verifyNullReturnedWhenContextNotInConstructorParam() throws Exception {
		final String key = "key";
		final Object input = new Object();

		final SimpleSettingValueProvider<Object> settingValueProvider = new SimpleSettingValueProvider<>(Collections.singletonMap(key, input));

		assertNull(settingValueProvider.get("key2"));
	}

	@Test
	public void verifySameParamInstanceReturnedFromGetWithContext() throws Exception {
		final String key = "key";
		final Object input = new Object();

		final SimpleSettingValueProvider<Object> settingValueProvider = new SimpleSettingValueProvider<>(key, input);

		assertSame(input, settingValueProvider.get(key));
	}

	@Test
	public void verifyNullReturnedFromGetWithoutContextWhenConstructedWithParam() throws Exception {
		final SimpleSettingValueProvider<Object> settingValueProvider = new SimpleSettingValueProvider<>("key", new Object());

		assertNull(settingValueProvider.get());
	}

}