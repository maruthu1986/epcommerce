/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 *
 */
package com.elasticpath.cmclient.core.editors;

import junit.framework.TestCase;

/**
 * Test class for the TableItem class.
 */
public class TableItemTest extends TestCase {

	private static final String MODIFIED_NAME = "Modified Name"; //$NON-NLS-1$

	/**
	 * Runs all tests.
	 * @param args not used
	 */
	public static void main(final String[] args) {
		junit.textui.TestRunner.run(TableItemTest.class);
	}

	/**
	 * Tests that adding an item to the ModifiedItems collection doesn't
	 * do so if the item is already in the AddedItems collection, even if
	 * the item has been modified since adding to the AddedItems collection
	 * in such a way that its hashcode or equals method has since changed.
	 */
	public void testAddModifiedItemFailsIfInAdded() {
		final TableItems<MutableItemForTest> tableItems =
			new TableItems<MutableItemForTest>();
		final MutableItemForTest item = createMutableItemForTest();
		//Initial test - simple case where we don't change it.
		tableItems.addAddedItem(item);
		tableItems.addModifiedItem(item);
		assertEquals("Item should not be added to the modifiedItems collection if it's already " //$NON-NLS-1$
				+ "in the addedItems collection", 0, tableItems.getModifiedItems().size()); //$NON-NLS-1$

		//Now modify the hashcode and check again
		item.setName(MODIFIED_NAME);
		tableItems.addModifiedItem(item);
		assertEquals("Item should not be added to the modifiedItems collection if it's already " //$NON-NLS-1$
				+ "in the addedItems collection, even if the hashcode has changed",  //$NON-NLS-1$
				0, tableItems.getModifiedItems().size());
	}

	private MutableItemForTest createMutableItemForTest() {
		final MutableItemForTest item = new MutableItemForTest();
		item.setName("OriginalName"); //$NON-NLS-1$
		return item;
	}

	/**
	 * Tests that adding an item to the RemovedItem collection will remove
	 * the item from both the AddedItem and the ModifiedItem collections.
	 */
	public void testAddRemovedItemRemovesFromAdded() {
		final TableItems<MutableItemForTest> tableItems =
			new TableItems<MutableItemForTest>();
		final MutableItemForTest item = createMutableItemForTest();
		//Initial test - simple case where we don't change it
		tableItems.addAddedItem(item);
		tableItems.addRemovedItem(item);
		assertEquals("Item should be removed from the AddedItems collection", //$NON-NLS-1$
				0, tableItems.getAddedItems().size());
		assertEquals("Item should be added to the RemovedItems collection", //$NON-NLS-1$
				1, tableItems.getRemovedItems().size());
	}

	/**
	 * Tests that adding an item to the RemovedItem collection will remove
	 * the item from both the AddedItem and the ModifiedItem collections, even if
	 * the item has been modified since adding to the either of the other collections
	 * in such a way that its hashcode or equals method has since changed.
	 */
	public void testAddRemovedItemRemovesFromAddedIfChanged() {
		final TableItems<MutableItemForTest> tableItems =
			new TableItems<MutableItemForTest>();
		final MutableItemForTest item = createMutableItemForTest();

		tableItems.addAddedItem(item);
		//Now modify the hashcode
		item.setName(MODIFIED_NAME);
		tableItems.addRemovedItem(item);
		assertEquals("Item should be removed from the AddedItems collection even if the hashcode has changed",  //$NON-NLS-1$
				0, tableItems.getAddedItems().size());
	}

	/**
	 * Tests that adding an item to the RemovedItem collection will remove
	 * the item from both the AddedItem and the ModifiedItem collections, even if
	 * the item has been modified since adding to the either of the other collections
	 * in such a way that its hashcode or equals method has since changed.
	 */
	public void testAddRemovedItemRemovesFromModified() {
		final TableItems<MutableItemForTest> tableItems =
			new TableItems<MutableItemForTest>();
		final MutableItemForTest item = createMutableItemForTest();

		tableItems.addModifiedItem(item);
		//Simple case
		tableItems.addRemovedItem(item);
		assertEquals("Item should be removed from the ModifiedItems collection", //$NON-NLS-1$
				0, tableItems.getModifiedItems().size());
	}

	/**
	 * Tests that adding an item to the RemovedItem collection will remove
	 * the item from both the AddedItem and the ModifiedItem collections, even if
	 * the item has been modified since adding to the either of the other collections
	 * in such a way that its hashcode or equals method has since changed.
	 */
	public void testAddRemovedItemRemovesFromModifiedIfChanged() {
		final TableItems<MutableItemForTest> tableItems =
			new TableItems<MutableItemForTest>();
		final MutableItemForTest item = createMutableItemForTest();

		tableItems.addModifiedItem(item);
		//Now modify the hashcode
		item.setName(MODIFIED_NAME);
		tableItems.addRemovedItem(item);
		assertEquals("Item should be removed from the ModifiedItems collection even if the hashcode has changed",  //$NON-NLS-1$
				0, tableItems.getModifiedItems().size());
	}

	/**
	 * A Test item that will allow us to manipulate the hashcode
	 * and will always return false for the equals method.
	 */
	private class MutableItemForTest {

		private String name;

		/**
		 * Set the name.
		 * @param name the new name
		 */
		public void setName(final String name) {
			this.name = name;
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		@Override
		public boolean equals(final Object obj) {
			return false;
		}
	}
}
