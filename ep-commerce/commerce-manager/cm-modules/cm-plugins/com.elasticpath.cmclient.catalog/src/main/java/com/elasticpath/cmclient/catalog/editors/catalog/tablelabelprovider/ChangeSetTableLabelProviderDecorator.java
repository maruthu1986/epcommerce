/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider;

import java.util.Arrays;

import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.TableItems;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;

/**
 * Common features in setting up table label providers using change sets.
 *
 * @param <T> the {@link TableItems} type
 */
public class ChangeSetTableLabelProviderDecorator<T> extends AbstractTableLabelProviderDecorator {
	/** Change set lock index. */
	protected static final String CHANGE_SET_LOCK_INDEX = "CHANGE_SET_LOCK_INDEX"; //$NON-NLS-1$

	/** Change set action index. */
	protected static final String CHANGE_SET_ACTION_INDEX = "CHANGE_SET_ACTION_INDEX"; //$NON-NLS-1$

	private final String[] changeSetIndexes = { CHANGE_SET_LOCK_INDEX, CHANGE_SET_ACTION_INDEX };

	private final TableItems<T> tableItems;
	
	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	/**
	 * Constructor.
	 *
	 * @param decoratedChangeSetTableLabelProvider the table label provider to be decorated
	 * @param tableItems the change set objects
	 */
	public ChangeSetTableLabelProviderDecorator(final ExtensibleTableLabelProvider decoratedChangeSetTableLabelProvider,
			final TableItems<T> tableItems) {
		super(decoratedChangeSetTableLabelProvider);
		this.tableItems = tableItems;

		// add change set indexes to the beginning of the column list
		getDecoratedTableLabelProvider().addAllColumnIndexRegistry(Arrays.asList(changeSetIndexes));
	}

	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		Image image = getDecoratedTableLabelProvider().getColumnImage(element, columnIndex);
		final String columnName = getColumnIndexRegistryName(columnIndex);
		if (CHANGE_SET_LOCK_INDEX.equals(columnName)) {
			final ChangeSetObjectStatus changeSetObjectStatus = changeSetHelper.getChangeSetObjectStatus(element);
			if (changeSetObjectStatus.isLocked()) {
				image = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_LOCKED_SMALL);
			}
		} else if (CHANGE_SET_ACTION_INDEX.equals(columnName)) {
			if (tableItems.getAddedItems().contains(element)) {
				image = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_ADDED_SMALL);
			} else if (tableItems.getRemovedItems().contains(element)) {
				image = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_DELETED_SMALL);
			} else if (tableItems.getModifiedItems().contains(element)) {
				image = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_CHANGED_SMALL);
			}
		}
		return image;
	}

	@Override
	public void dispose() {
		getDecoratedTableLabelProvider().removeAllColumnIndexRegistry(Arrays.asList(changeSetIndexes));
		getDecoratedTableLabelProvider().dispose();
	}

}
