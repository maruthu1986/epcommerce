/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.test.support.jndi;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.naming.NamingException;

import org.springframework.mock.jndi.SimpleNamingContextBuilder;

/**
 * <p>
 * Manages a JNDI context for use within test classes. This allows multiple JNDI dependencies to share a common context.
 * </p>
 * <p>
 * This class is intended for use within a {@link org.springframework.test.context.TestExecutionListener TestExecutionListener}.
 * </p>
 * <p>
 * Keep in mind that the scope of a {@link org.springframework.test.context.TestExecutionListener TestExecutionListener} is limited to the execution
 * of a single class's tests. This class is not appropriate for binding JNDI resources that require longer lifecycles, eg. for the lifetime of the
 * JVM.
 * </p>
 * <p>
 * Care should be taken not to combine usage of this class and other JNDI-creating classes such as {@link SimpleNamingContextBuilder}. If this class
 * is used to create and manage JNDI contexts, it should be used exclusively. The behaviour should be considered undefined if external classes create
 * or replace JNDI contexts during each {@code JndiContextManager} instance's lifetime.
 * </p>
 * <p>
 * Note that this class is <strong>not</strong> thread-safe. If parallel test execution is required, please consider refactoring this class and
 * implement a locking mechanism around the creation of the JNDI context.
 * </p>
 * 
 * @see org.springframework.test.context.TestExecutionListener
 */
public class JndiContextManager {

	private SimpleNamingContextBuilder contextBuilder;

	private static final Map<String, Object> BOUND_OBJECTS = new HashMap<>();

	/**
	 * <p>
	 * Creates and returns a new {@code JndiContextManager} instance.
	 * </p>
	 * <p>
	 * This instance will be populated with the current JNDI context if one does exists, or otherwise creates a new one.
	 * </p>
	 * 
	 * @return a new instance of {@code JndiContextManager}.
	 */
	public static JndiContextManager createJndiContextManager() {
		SimpleNamingContextBuilder contextBuilder = SimpleNamingContextBuilder.getCurrentContextBuilder();

		if (contextBuilder == null) {
			contextBuilder = createEmptyActivatedSimpleNamingContextBuilder();
		}

		return new JndiContextManager(contextBuilder);
	}

	/**
	 * Instances should be created via {@link #createJndiContextManager()}.
	 * 
	 * @param contextBuilder the wrapped {@link SimpleNamingContextBuilder}
	 */
	JndiContextManager(final SimpleNamingContextBuilder contextBuilder) {
		this.contextBuilder = contextBuilder;
	}

	/**
	 * <p>
	 * Binds a resource at a given JNDI name.
	 * </p>
	 * <p>
	 * Note that behaviour is undefined if an existing resource is currently bound at {@code jndiName}; it is recommended first to call
	 * {@link #unbind(String)} if the intent is to replace an existing resource.
	 * </p>
	 * 
	 * @param jndiName the JNDI name
	 * @param resource the Object to be bound
	 */
	public void bind(final String jndiName, final Object resource) {
		synchronized (BOUND_OBJECTS) {
			// for future enhancement:
			// 1. inspect BOUND_OBJECTS.get(jndiName) for existing resource at requested JNDI reference
			// 2. if non-null, test for equality with resource
			// 3. if equal, no-op (return)
			// 4. if not equal, call unbind(jndiName)

			BOUND_OBJECTS.put(jndiName, resource);
			getContextBuilder().bind(jndiName, resource);
		}
	}

	/**
	 * <p>
	 * Unbinds a resource at a given JNDI name.
	 * </p>
	 * <p>
	 * This is useful when an existing JNDI location should be updated with a new resource; merely calling {@link #bind(String, Object)} to overwrite
	 * the existing instance does not function as expected, particularly when binding an OpenJPA datasource resource. It appears that the previous
	 * context is not cleared up fully, despite the Spring application context being reset. If that defect can be resolved, this method could likely
	 * be deleted.
	 * </p>
	 * 
	 * @param jndiName the JNDI name
	 */
	public void unbind(final String jndiName) {
		synchronized (BOUND_OBJECTS) {
			final Object removed = BOUND_OBJECTS.remove(jndiName);
			if (removed != null) {
				getContextBuilder().deactivate();
				setContextBuilder(createNewContextBuilder());
				for (final Entry<String, Object> bindingEntry : BOUND_OBJECTS.entrySet()) {
					getContextBuilder().bind(bindingEntry.getKey(), bindingEntry.getValue());
				}
			}
		}
	}

	/**
	 * Unbinds all resources currently bound to the JNDI context.
	 */
	public void unbindAll() {
		synchronized (BOUND_OBJECTS) {
			BOUND_OBJECTS.clear();
			setContextBuilder(createNewContextBuilder());
		}
	}

	/**
	 * Returns a new, empty {@link SimpleNamingContextBuilder}.
	 *
	 * @return a new, empty {@link SimpleNamingContextBuilder}
	 */
	SimpleNamingContextBuilder createNewContextBuilder() {
		return createEmptyActivatedSimpleNamingContextBuilder();
	}

	private static SimpleNamingContextBuilder createEmptyActivatedSimpleNamingContextBuilder() {
		try {
			return SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		} catch (final NamingException e) {
			throw new IllegalStateException("Cannot create JndiContextManager", e);
		}
	}

	protected SimpleNamingContextBuilder getContextBuilder() {
		return contextBuilder;
	}

	protected void setContextBuilder(final SimpleNamingContextBuilder contextBuilder) {
		this.contextBuilder = contextBuilder;
	}

}
