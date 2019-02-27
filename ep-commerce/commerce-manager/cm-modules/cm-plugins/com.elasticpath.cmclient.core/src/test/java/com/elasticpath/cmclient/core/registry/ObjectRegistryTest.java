/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.core.registry;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.eclipse.rap.rwt.testfixture.TestContext;

import java.io.Serializable;

/**
 * A test for {@link ObjectRegistry}.
 */
public class ObjectRegistryTest {

	private static final String TEST_KEY = "test_key"; //$NON-NLS-1$
	private ObjectRegistry objectRegistry;

	@Rule
	public TestContext context = new TestContext();

	/**
	 *
	 * @throws java.lang.Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		objectRegistry = ObjectRegistry.getInstance();
		objectRegistry.addObjectListener(new ObjectRegistryListener() {

			public void objectAdded(final String key, final Object object) {
				((TestObject) object).setAdded(true);
			}

			public void objectRemoved(final String key, final Object object) {
				((TestObject) object).setRemoved(true);
			}

			public void objectUpdated(final String key, final Object oldValue, final Object newValue) {
				((TestObject) newValue).setChanged(true);
				((TestObject) oldValue).setChanged(true);
			}
		});
	}

	/**
	 *  Tears down the test by removing any added objects.
	 *
	 * @throws Exception on error.
	 */
	@After
	public void tearDown() throws Exception {
		objectRegistry.removeObject(TEST_KEY);
	}
	
	/**
	 * Tests that putting and getting an object from the registry works as expected.
	 */
	@Test
	public void testPutAndGetObject() {
		
		final TestObject object = new TestObject();
		objectRegistry.putObject(TEST_KEY, object);
		assertSame(object, objectRegistry.getObject(TEST_KEY));
		assertTrue(object.isAdded());
	}

	/**
	 * Tests that removing an object will call the listener and the object is removed from the map.
	 */
	@Test
	public void testRemoveObject() {
		// add the object
		final TestObject object = new TestObject();
		objectRegistry.putObject(TEST_KEY, object);
		
		// remove it
		objectRegistry.removeObject(TEST_KEY);
		// assert results
		assertNull(objectRegistry.getObject(TEST_KEY));
		assertTrue(object.isRemoved());
		
	}

	/**
	 * Tests that on update both the new and the old objects are the same and the listener gets invoked.
	 */
	@Test
	public void testUpdateObjectInRegistry() {
		final TestObject object = new TestObject();
		objectRegistry.putObject(TEST_KEY, object);
		assertSame(object, objectRegistry.getObject(TEST_KEY));
		assertTrue(object.isAdded());
		
		final TestObject newObject = new TestObject();
		objectRegistry.putObject(TEST_KEY, newObject);
		assertSame(newObject, objectRegistry.getObject(TEST_KEY));
		assertTrue(object.isChanged());
		assertTrue(newObject.isChanged());
	}

	/**
	 * A test object.
	 */
	public static class TestObject implements Serializable {
		private static final long serialVersionUID = 1L;

		private boolean added;
		private boolean removed;
		private boolean changed;
		/**
		 *
		 * @return the added true if added
		 */
		public boolean isAdded() {
			return added;
		}
		/**
		 *
		 * @param added the added to set
		 */
		public void setAdded(final boolean added) {
			this.added = added;
		}
		/**
		 *
		 * @return the removed true if removed
		 */
		public boolean isRemoved() {
			return removed;
		}
		/**
		 *
		 * @param removed the removed to set
		 */
		public void setRemoved(final boolean removed) {
			this.removed = removed;
		}
		/**
		 *
		 * @return the changed true if changed
		 */
		public boolean isChanged() {
			return changed;
		}
		/**
		 *
		 * @param changed the changed to set
		 */
		public void setChanged(final boolean changed) {
			this.changed = changed;
		}
	}

}
