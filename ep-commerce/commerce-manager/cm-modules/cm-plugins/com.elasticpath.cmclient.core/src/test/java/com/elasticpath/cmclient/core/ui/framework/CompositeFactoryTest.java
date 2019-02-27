/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework;

import junit.framework.TestCase;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * Tests for the CompositeFactory class.
 * 
 * DISABLED: because the test fails on headless Linux platforms 
 * which is the case when performing the automatic build.
 */
public class CompositeFactoryTest extends TestCase {

	private static final String THE_PARENT_IS_NOT_THE_RIGHT_ONE = "The parent is not the right one"; //$NON-NLS-1$

	/**
	 * 
	 */
	private static final int COLUMN_COUNT_CHILD = 2;

	/**
	 * 
	 */
	private static final int COLUMNS_COUNT = 3;

	private transient Composite parentSwtComposite;

//	private Shell shell;
//
//	/**
//	 * Creates new Shell and parent composite out of it.
//	 * 
//	 * @throws Exception on error
//	 */
//	@Override
//	protected void setUp() throws Exception {
//		super.setUp();
//		this.shell = new Shell((Display) null);
//		this.parentSwtComposite = new Composite(this.shell, SWT.NONE);
//	}
//
//	/**
//	 * Disposes of the Display instance.
//	 * 
//	 * @throws Exception on error
//	 */
//	@Override
//	protected void tearDown() throws Exception {
//		this.parentSwtComposite.dispose();
//		this.shell.dispose();
//	}

	/**
	 * A fake test method.
	 */
	public void testDisabled() {
		// a fake test method
	}
	
	/**
	 * Tests that the CompositeFactory creates an EP layout composite that has the appropriate 
	 * Eclipse layout composite created and set.
	 * 
	 */
	public void disabledCreateGridLayoutComposite() {
		assertNull("The parent composite layout data is not null", this.parentSwtComposite.getLayoutData()); //$NON-NLS-1$
		final IEpLayoutComposite epComposite = CompositeFactory.createGridLayoutComposite(this.parentSwtComposite, COLUMNS_COUNT, true);
		final IEpLayoutComposite childTWrapComposite = epComposite.addTableWrapLayoutComposite(COLUMN_COUNT_CHILD, true, null);

		// check the created SWT Composite
		final Composite createdSwtComposite = epComposite.getSwtComposite();
		assertNotNull("The underlying composite is null", createdSwtComposite); //$NON-NLS-1$

		// checks the layout set to the created composites
		// ---the main composite
		assertTrue("The created layout is not grid", createdSwtComposite.getLayout() instanceof GridLayout); //$NON-NLS-1$
		final GridLayout gridLayout = (GridLayout) createdSwtComposite.getLayout();
		assertEquals("The column count is not ok", gridLayout.numColumns, COLUMNS_COUNT); //$NON-NLS-1$
		assertTrue("Equal width flag has not been set", gridLayout.makeColumnsEqualWidth); //$NON-NLS-1$
		// ---the child composite
		final Layout layout = childTWrapComposite.getSwtComposite().getLayout();
		assertTrue("The layout should be instance of TableWrapLayout", layout instanceof TableWrapLayout); //$NON-NLS-1$
		final TableWrapLayout tableWrapLayout = (TableWrapLayout) layout;
		assertEquals("Column count does not match", tableWrapLayout.numColumns, COLUMN_COUNT_CHILD); //$NON-NLS-1$
		assertTrue("The flag is not set properly", tableWrapLayout.makeColumnsEqualWidth); //$NON-NLS-1$

		// checks the hierarchy of the composites
		assertEquals(THE_PARENT_IS_NOT_THE_RIGHT_ONE, createdSwtComposite.getParent(), this.parentSwtComposite); 
		assertEquals(THE_PARENT_IS_NOT_THE_RIGHT_ONE, childTWrapComposite.getSwtComposite().getParent(), createdSwtComposite); 
	}

	/**
	 * Tests CompositeFactory for properly creating a table wrap layout composite.
	 */
	public void disabledCreateTableWrapLayoutComposite() {
		assertNull("", this.parentSwtComposite.getLayoutData()); //$NON-NLS-1$
		final IEpLayoutComposite epTWrapComposite = CompositeFactory.createTableWrapLayoutComposite(this.parentSwtComposite, 3, true);
		assertNull("", epTWrapComposite.getSwtComposite().getLayoutData()); //$NON-NLS-1$
		final IEpLayoutComposite childGridComposite = epTWrapComposite.addGridLayoutComposite(2, true, null);

		// check the created SWT Composite
		final Composite tableWrapSwtComposite = epTWrapComposite.getSwtComposite();
		assertNotNull("", tableWrapSwtComposite); //$NON-NLS-1$

		// checks the layout set to the created composites
		// ---the main composite
		assertTrue("", tableWrapSwtComposite.getLayout() instanceof TableWrapLayout); //$NON-NLS-1$
		final TableWrapLayout tWrapLayout = (TableWrapLayout) tableWrapSwtComposite.getLayout();
		assertEquals("", tWrapLayout.numColumns, COLUMNS_COUNT); //$NON-NLS-1$
		assertTrue("", tWrapLayout.makeColumnsEqualWidth); //$NON-NLS-1$
		// ---the child composite
		final Layout layout = childGridComposite.getSwtComposite().getLayout();
		assertTrue("The layout should be instance of GridLayout", layout instanceof GridLayout); //$NON-NLS-1$
		final GridLayout gridLayout = (GridLayout) layout;
		assertEquals("Column count does not match", gridLayout.numColumns, COLUMN_COUNT_CHILD); //$NON-NLS-1$
		assertTrue("The flag is not set properly", gridLayout.makeColumnsEqualWidth); //$NON-NLS-1$
		// ----check layout data
		Object layoutData = childGridComposite.getSwtComposite().getLayoutData();
		assertNotNull("the layout data is null", layoutData); //$NON-NLS-1$
		// should be table wrap data
		assertTrue("The layout data is not properly set", layoutData instanceof TableWrapData); //$NON-NLS-1$

		// checks the hierarchy of the composites
		assertEquals(THE_PARENT_IS_NOT_THE_RIGHT_ONE, tableWrapSwtComposite.getParent(), this.parentSwtComposite); 
		assertEquals(THE_PARENT_IS_NOT_THE_RIGHT_ONE, childGridComposite.getSwtComposite().getParent(), tableWrapSwtComposite); 
	}

}
