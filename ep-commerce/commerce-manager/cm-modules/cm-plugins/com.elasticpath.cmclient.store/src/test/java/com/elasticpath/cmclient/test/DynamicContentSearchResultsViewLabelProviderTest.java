/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.views.DynamicContentSearchResultsViewLabelProvider;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.contentspace.impl.DynamicContentImpl;


/**
 * Test the behaviour of DynamicContentSearchResultsViewLabelProvider.
 */
public class DynamicContentSearchResultsViewLabelProviderTest {

	private static final int NAME_COLUMN = DynamicContentSearchResultsViewLabelProvider.COLUMN_NAME;
	private static final int DESCRIPTION_COLUMN = DynamicContentSearchResultsViewLabelProvider.COLUMN_DESCRIPTION;
	private static final int INVALID_COLUMN = 10;
	
	private final DynamicContentSearchResultsViewLabelProvider provider = new DynamicContentSearchResultsViewLabelProvider();
	
	/**
	 * Test get column text name.
	 */
	@Test
	public void testGetColumnTextName() {
		DynamicContent dynamicContent = new DynamicContentImpl();
		dynamicContent.setName("Name");
		assertEquals("Column #1 should be the name", "Name", provider.getColumnText(dynamicContent, NAME_COLUMN));
	}
	
	/**
	 * Test get column text description.
	 */
	@Test
	public void testGetColumnTextDescription() {
		DynamicContent dynamicContent = new DynamicContentImpl();
		dynamicContent.setDescription("description");
		assertEquals("Column #2 should be the decription", "description", provider.getColumnText(dynamicContent, DESCRIPTION_COLUMN));
	}
	
	/**
	 * Test get column text with invalid index.
	 */
	@Test
	public void testGetColumnTextWithInvalidIndex() {
		DynamicContent dynamicContent = new DynamicContentImpl();
		assertEquals("There is no column 10, result should be blank", "", provider.getColumnText(dynamicContent, INVALID_COLUMN));
	}
	
	/**
	 * Test get column text with null description.
	 */
	@Test
	public void testGetColumnTextWithNullDescription() {
		DynamicContent dynamicContent = new DynamicContentImpl();
		assertEquals("Column #2 should be empty when there is no decription", "", provider.getColumnText(dynamicContent, DESCRIPTION_COLUMN));
	}
	
	/**
	 * Test get column text removes tabs and new lines.
	 */
	@Test
	public void testGetColumnTextRemovesTabsAndNewLines() {
		DynamicContent dynamicContent = new DynamicContentImpl();
		dynamicContent.setDescription("This description\thas some\ttabs.\r\nIt also has a new line");
		String columnText = provider.getColumnText(dynamicContent, DESCRIPTION_COLUMN);
		assertEquals("The tabs and new lines should be replaced with spaces", "This description has some tabs. It also has a new line", columnText);
	}
}
