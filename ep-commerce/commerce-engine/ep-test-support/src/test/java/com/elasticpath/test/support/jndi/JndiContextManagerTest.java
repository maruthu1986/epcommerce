/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.test.support.jndi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import javax.naming.NamingException;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

/**
 * Test class for {@link JndiContextManager}.
 */
public class JndiContextManagerTest {

	public static final String RESOURCE_1_NAME = "res1";
	public static final String RESOURCE_2_NAME = "res2";
	public static final String RESOURCE_3_NAME = "res3";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Before
	public void setUp() throws NamingException {
		SimpleNamingContextBuilder.emptyActivatedContextBuilder().deactivate();
	}

	@After
	public void tearDown() {
		JndiContextManager.createJndiContextManager().unbindAll();
	}

	/**
	 * <p>
	 * Test method for {@link JndiContextManager#createJndiContextManager()}.
	 * </p>
	 * <p>
	 * Verifies that a new SimpleNamingContextBuilder is created if one does not already exist.
	 * </p>
	 */
	@Test
	public void testCreateJndiContextManagerWithNoExistingContextCreatesNewContext() throws Exception {
		assertNull("Test is in invalid state; expected no JNDI context to exist", SimpleNamingContextBuilder.getCurrentContextBuilder());

		final JndiContextManager jndiContextManager = JndiContextManager.createJndiContextManager();

		assertNotNull("A new JNDI context should have been created", jndiContextManager.getContextBuilder());

	}

	/**
	 * <p>
	 * Test method for {@link JndiContextManager#createJndiContextManager()}.
	 * </p>
	 * <p>
	 * Verifies that an existing SimpleNamingContextBuilder is reused if one already exists.
	 * </p>
	 */
	@Test
	public void testCreateJndiContextManagerReusesExistingContext() throws Exception {
		final SimpleNamingContextBuilder originalContextBuilder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();

		final JndiContextManager jndiContextManager = JndiContextManager.createJndiContextManager();

		assertSame("Expected the context manager to reuse the existing JNDI context", originalContextBuilder, jndiContextManager.getContextBuilder());
	}

	/**
	 * <p>
	 * Test method for {@link JndiContextManager#bind(String, Object)}.
	 * </p>
	 * <p>
	 * Verifies that a resource can be bound to a particular JNDI name.
	 * </p>
	 */
	@Test
	public void testBind() throws Exception {
		final SimpleNamingContextBuilder contextBuilder = context.mock(SimpleNamingContextBuilder.class);
		final JndiContextManager jndiContextManager = new JndiContextManager(contextBuilder);

		final String jndiName = "ep.foo.bar";
		final Object resource = new Object();

		context.checking(new Expectations() {
			{
				oneOf(contextBuilder).bind(jndiName, resource);
			}
		});

		jndiContextManager.bind(jndiName, resource);
	}

	/**
	 * Verifies that {@link JndiContextManager#unbindAll)} creates a new, empty context.
	 */
	@Test
	public void testUnbindAllResetsContext() throws Exception {
		final SimpleNamingContextBuilder originalContextBuilder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		final SimpleNamingContextBuilder newContextBuilder = context.mock(SimpleNamingContextBuilder.class);

		final JndiContextManager jndiContextManager = new JndiContextManager(originalContextBuilder) {
			@Override
			SimpleNamingContextBuilder createNewContextBuilder() {
				return newContextBuilder;
			}
		};

		jndiContextManager.unbindAll();

		assertEquals("A new, empty JNDI context should be created", newContextBuilder, jndiContextManager.getContextBuilder());
	}

	/**
	 * Verifies that {@link JndiContextManager#unbind(String)} creates a new context and calls {@link JndiContextManager#bind(String, Object) bind}
	 * for all other currently-bound resource.
	 */
	@Test
	public void testUnbindResetsContextAndRebindsOtherResources() throws Exception {
		final SimpleNamingContextBuilder originalContextBuilder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		final SimpleNamingContextBuilder newContextBuilder = context.mock(SimpleNamingContextBuilder.class);

		final Object resource1 = new Object();
		final Object resource2 = new Object();
		final Object resource3 = new Object();

		final JndiContextManager jndiContextManager = new JndiContextManager(originalContextBuilder) {
			@Override
			SimpleNamingContextBuilder createNewContextBuilder() {
				return newContextBuilder;
			}
		};

		// Bind three resources in preparation for unbinding
		jndiContextManager.bind(RESOURCE_1_NAME, resource1);
		jndiContextManager.bind(RESOURCE_2_NAME, resource2);
		jndiContextManager.bind(RESOURCE_3_NAME, resource3);

		context.checking(new Expectations() {
			{
				oneOf(newContextBuilder).bind(RESOURCE_1_NAME, resource1);
				oneOf(newContextBuilder).bind(RESOURCE_3_NAME, resource3);
			}
		});

		jndiContextManager.unbind(RESOURCE_2_NAME);

		assertEquals("A new JNDI context should be created", newContextBuilder, jndiContextManager.getContextBuilder());
	}

	/**
	 * Verifies that {@link JndiContextManager#unbind(String)} re-binds resources created by other JndiContextManager instances.
	 */
	@Test
	public void testUnbindResetsContextAndRebindsResourcesCreatedByOtherInstances() throws Exception {
		final SimpleNamingContextBuilder originalContextBuilder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		final SimpleNamingContextBuilder newContextBuilder = context.mock(SimpleNamingContextBuilder.class);

		final Object resource1 = new Object();
		final Object resource2 = new Object();
		final Object resource3 = new Object();

		final JndiContextManager jndiContextManager = new JndiContextManager(originalContextBuilder) {
			@Override
			SimpleNamingContextBuilder createNewContextBuilder() {
				return newContextBuilder;
			}
		};

		// Bind three resources to three different instances, in preparation for unbinding
		jndiContextManager.bind(RESOURCE_1_NAME, resource1);
		new JndiContextManager(originalContextBuilder).bind(RESOURCE_2_NAME, resource2);
		new JndiContextManager(originalContextBuilder).bind(RESOURCE_3_NAME, resource3);

		context.checking(new Expectations() {
			{
				oneOf(newContextBuilder).bind(RESOURCE_2_NAME, resource2);
				oneOf(newContextBuilder).bind(RESOURCE_3_NAME, resource3);
			}
		});

		jndiContextManager.unbind(RESOURCE_1_NAME);
	}

}