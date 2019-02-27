/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers.extenders;

import java.util.Optional;

import com.google.common.base.Strings;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 *Extensible Table Label Provider. Wraps the TableLabelProvider and delegates to extensions to get extension text.
 */
public class ExtensibleTableLabelProvider implements ITableLabelProvider {

	private final ITableLabelProvider viewLabelProvider;
	private final String tableClassName;
	private final String pluginId;

	/**
	 * Constructor.
	 * @param viewLabelProvider The tableLabelProvide to wrap.
	 * @param tableClassName The name of the Table's class. Used to look up table extensions.
	 * @param pluginId The name of the Table's plugin. Used to look up table extensions.
	 */
	public ExtensibleTableLabelProvider(final ITableLabelProvider viewLabelProvider, final String tableClassName, final String pluginId) {
		this.viewLabelProvider = viewLabelProvider;
		this.tableClassName = tableClassName;
		this.pluginId = pluginId;
	}

	@Override
	public Image getColumnImage(final Object object, final int index) {
		return viewLabelProvider.getColumnImage(object, index);
	}

	@Override
	public String getColumnText(final Object object, final int index) {

		String columnText = viewLabelProvider.getColumnText(object, index);
		if (Strings.isNullOrEmpty(columnText)) {
			Optional<EPTableColumnCreator> extendedTable = PluginHelper.findTables(tableClassName, pluginId).stream().findFirst();
			if (extendedTable.isPresent()) {
				columnText = extendedTable.get().visitColumn(object, index);
			}
		}

		return columnText;
	}

	@Override
	public void addListener(final ILabelProviderListener iLabelProviderListener) {
			viewLabelProvider.addListener(iLabelProviderListener);
	}

	@Override
	public void dispose() {
		viewLabelProvider.dispose();
	}

	@Override
	public boolean isLabelProperty(final Object object, final String property) {
		return viewLabelProvider.isLabelProperty(object, property);
	}

	@Override
	public void removeListener(final ILabelProviderListener iLabelProviderListener) {
		viewLabelProvider.removeListener(iLabelProviderListener);
	}
}
