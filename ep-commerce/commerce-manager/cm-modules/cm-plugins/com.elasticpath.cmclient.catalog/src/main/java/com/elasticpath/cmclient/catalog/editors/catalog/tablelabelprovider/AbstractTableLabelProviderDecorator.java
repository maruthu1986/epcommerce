/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider;

import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.CoreMessages;

/**
 * Common features in setting up table label providers using the Decorator pattern.
 */
abstract class AbstractTableLabelProviderDecorator implements ExtensibleTableLabelProvider {

	/** The TableLabelProvider being decorated. **/
	private final ExtensibleTableLabelProvider decoratedTableLabelProvider;

	/**
	 * Constructor.
	 *
	 * @param decoratedTableLabelProvider the table label provider to be decorated
	 */
	AbstractTableLabelProviderDecorator(final ExtensibleTableLabelProvider decoratedTableLabelProvider) {
		this.decoratedTableLabelProvider = decoratedTableLabelProvider;
	}

	@Override
	public void addListener(final ILabelProviderListener listener) {
		getDecoratedTableLabelProvider().addListener(listener);
	}

	@Override
	public void dispose() {
		getDecoratedTableLabelProvider().dispose();
	}

	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return getDecoratedTableLabelProvider().isLabelProperty(element, property);
	}

	@Override
	public void removeListener(final ILabelProviderListener listener) {
		getDecoratedTableLabelProvider().removeListener(listener);
	}

	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		return getDecoratedTableLabelProvider().getColumnImage(element, columnIndex);
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		return getDecoratedTableLabelProvider().getColumnText(element, columnIndex);
	}

	@Override
	public void addColumnIndexRegistry(final String columnIndexRegistryName) {
		getDecoratedTableLabelProvider().addColumnIndexRegistry(columnIndexRegistryName);
	}

	@Override
	public void addAllColumnIndexRegistry(final List<String> columnIndexRegistryNames) {
		getDecoratedTableLabelProvider().addAllColumnIndexRegistry(columnIndexRegistryNames);
	}

	@Override
	public void removeColumnIndexRegistry(final String columnIndexRegistryName) {
		getDecoratedTableLabelProvider().removeColumnIndexRegistry(columnIndexRegistryName);
	}

	@Override
	public void removeAllColumnIndexRegistry(final List<String> columnIndexRegistryNames) {
		getDecoratedTableLabelProvider().removeAllColumnIndexRegistry(columnIndexRegistryNames);
	}


	@Override
	public String getColumnIndexRegistryName(final int columnIndex) {
		return getDecoratedTableLabelProvider().getColumnIndexRegistryName(columnIndex);
	}

	@Override
	public void forceAddToStartColumnIndexRegistry(final String columnIndexRegistryName) {
		getDecoratedTableLabelProvider().forceAddToStartColumnIndexRegistry(columnIndexRegistryName);
	}

	/**
	 * Convenience method to show true or false message in table columns.
	 *
	 * @param onTrue boolean to act upon to show associated message
	 * @return the associated message based on the boolean argument
	 */
	protected String getBooleanMessage(final boolean onTrue) {
		String token;
		if (onTrue) {
			token = CoreMessages.get().YesNoForBoolean_true;
		} else {
			token = CoreMessages.get().YesNoForBoolean_false;
		}
		return token;
	}

	/**
	 * Get the underlying table label provider.
	 *
	 * @return the underlying table label provider
	 */
	protected ExtensibleTableLabelProvider getDecoratedTableLabelProvider() {
		return decoratedTableLabelProvider;
	}

}
