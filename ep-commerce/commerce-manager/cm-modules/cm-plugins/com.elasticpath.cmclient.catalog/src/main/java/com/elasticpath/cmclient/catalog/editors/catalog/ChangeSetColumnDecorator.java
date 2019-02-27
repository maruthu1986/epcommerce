/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.cmclient.catalog.editors.catalog;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.ui.framework.IEpTreeViewer;

/**
 * Common features in setting up columns with change sets.
 */
public class ChangeSetColumnDecorator {

	private static final int CHANGE_SET_LOCK_COLUMN_WIDTH = 25;

	private static final int CHANGE_SET_ACTION_COLUMN_WIDTH = 25;

	private int changeSetLockColumnWidth = CHANGE_SET_LOCK_COLUMN_WIDTH;

	private int changeSetActionColumnWidth = CHANGE_SET_ACTION_COLUMN_WIDTH;

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	/**
	 * The table section's availability for change set decoration.
	 *
	 * @return true if a change set decoration can be performed, false otherwise.
	 */
	public boolean isDecoratable() {
		return changeSetHelper.isChangeSetsEnabled();
	}

	/**
	 * Add a change set lock column to the given table viewer.
	 *
	 * @param table the table to add the change set lock column to
	 */
	public void addLockColumn(final IEpTableViewer table) {
		table.addTableColumn(StringUtils.EMPTY, getChangeSetLockColumnWidth());
	}

	/**
	 * Add a change set action column to the given tree viewer.
	 *
	 * @param tree the tree to add the change set action column to
	 */
	public void addActionColumn(final IEpTreeViewer tree) {
		tree.addColumn(StringUtils.EMPTY, getChangeSetOperationColumnWidth());
	}

	/**
	 * Add a change set lock column to the given tree viewer.
	 *
	 * @param tree the tree to add the change set lock column to
	 */
	public void addLockColumn(final IEpTreeViewer tree) {
		tree.addColumn(StringUtils.EMPTY, getChangeSetLockColumnWidth());
	}

	/**
	 * Add a change set action column to the given table viewer.
	 *
	 * @param table the table to add the change set action column to
	 */
	public void addActionColumn(final IEpTableViewer table) {
		table.addTableColumn(StringUtils.EMPTY, getChangeSetOperationColumnWidth());
	}

	/**
	 * Get the change set lock column width.
	 *
	 * @return the change set lock column width
	 */
	public int getChangeSetLockColumnWidth() {
		return changeSetLockColumnWidth;
	}

	/**
	 * Set the change set lock column width.
	 *
	 * @param changeSetLockColumnWidth the change set lock column width
	 */
	public void setChangeSetLockColumnWidth(final int changeSetLockColumnWidth) {
		this.changeSetLockColumnWidth = changeSetLockColumnWidth;
	}

	/**
	 * Get the change set action column width.
	 *
	 * @return the change set action column width.
	 */
	public int getChangeSetOperationColumnWidth() {
		return changeSetActionColumnWidth;
	}

	/**
	 * Set the change set action column width.
	 *
	 * @param changeSetOperationColumnWidth the change set action column width
	 */
	public void setChangeSetActionColumnWidth(final int changeSetOperationColumnWidth) {
		changeSetActionColumnWidth = changeSetOperationColumnWidth;
	}

}
