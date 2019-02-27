/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * The table label provider adapter is a concrete base implementation to use in table label provider decorations.
 */
public class TableLabelProviderAdapter extends LabelProvider implements ExtensibleTableLabelProvider {

	/**
	 * A list of the column names in an array. <br>
	 * The index position in the list corresponds to the column ordering. <br>
	 */
	private final List<String> columnIndexRegistry = new ArrayList<>();

	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		return StringUtils.EMPTY;
	}

	@Override
	public void addColumnIndexRegistry(final String columnIndexRegistryName) {
		columnIndexRegistry.add(columnIndexRegistryName);
	}

	@Override
	public void addAllColumnIndexRegistry(final List<String> columnIndexRegistryNames) {
		columnIndexRegistry.addAll(columnIndexRegistryNames);
	}

	@Override
	public void removeColumnIndexRegistry(final String columnIndexRegistryName) {
		columnIndexRegistry.remove(columnIndexRegistryName);
	}

	@Override
	public void removeAllColumnIndexRegistry(final List<String> columnIndexRegistryNames) {
		columnIndexRegistry.removeAll(columnIndexRegistryNames);
	}

	@Override
	public String getColumnIndexRegistryName(final int columnIndex) {
		return columnIndexRegistry.get(columnIndex);
	}

	@Override
	public void forceAddToStartColumnIndexRegistry(final String columnIndexRegistryName) {
		columnIndexRegistry.add(0, columnIndexRegistryName);
	}

}
