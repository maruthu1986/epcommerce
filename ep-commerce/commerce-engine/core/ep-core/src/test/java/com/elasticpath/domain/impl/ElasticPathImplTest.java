/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.impl;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;

/**
 * Test cases for <code>ElasticPathImpl</code>.
 */
public class ElasticPathImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ElasticPathImpl elasticPathImpl;
	


	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of any error
	 */
	@Before
	public void setUp() throws Exception {
		elasticPathImpl = new ElasticPathImpl();
	}

	/**
	 * Test the getBean calls are simply delegated to the underlying bean 
	 * factory.
	 */
	@Test
	public void testGetBean() {
		final String name = "testBean";
		final BeanFactory mockBeanFactory = context.mock(BeanFactory.class);
		context.checking(new Expectations() {
			{
				oneOf(mockBeanFactory).getBean(name);
			}
		});
		elasticPathImpl.setBeanFactory(mockBeanFactory);
		
		// Run the test.
		elasticPathImpl.getBean(name);
	}

	
	/**
	 * Create a map from a vararg list of strings.
	 * @param keyThenValue key then value, key then value.
	 * @return a map with the keys tied to their value as appropriate.
	 */
	Map<String, String> createProps(final String... keyThenValue) {
		Map<String, String> testProperties = new HashMap<>();
		for (int x = 0; x < keyThenValue.length; x = x + 2) {
			testProperties.put(keyThenValue[x], keyThenValue[x + 1]);	
		}
		return testProperties;
	}

}
