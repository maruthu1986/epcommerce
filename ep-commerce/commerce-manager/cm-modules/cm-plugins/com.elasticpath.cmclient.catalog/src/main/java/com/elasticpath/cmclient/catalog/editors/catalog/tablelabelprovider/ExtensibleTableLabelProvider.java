/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider;

import java.util.List;

import org.eclipse.jface.viewers.ITableLabelProvider;

/**
 * An extensible table label provider. Adds dynamic tracking of columns.
 */
public interface ExtensibleTableLabelProvider extends ITableLabelProvider {

	/**
	 * Add a column index registry name to the registry.<br>
	 * Add unique column names to determine their placement.<br>
	 *
	 * @param columnIndexRegistryName the column index registry name
	 */
	void addColumnIndexRegistry(String columnIndexRegistryName);

	/**
	 * Add a list of column index registry names to the registry.<br>
	 * Add unique column names to determine their placement.<br>
	 * The order of the list is maintained in the registry.<br>
	 *
	 * @param columnIndexRegistryNames the column index registry names list
	 */
	void addAllColumnIndexRegistry(List<String> columnIndexRegistryNames);

	/**
	 * Remove a column index registry names from the registry.<br>
	 * The order of the list is maintained in the registry after deletion.<br>
	 *
	 * @param columnIndexRegistryName the column index registry name
	 */
	void removeColumnIndexRegistry(String columnIndexRegistryName);

	/**
	 * Remove a list of column index registry names from the registry.<br>
	 *
	 * @param columnIndexRegistryNames the column index registry names list
	 */
	void removeAllColumnIndexRegistry(List<String> columnIndexRegistryNames);

	/**
	 * Get the name of the column list specified by the index.
	 *
	 * @param columnIndex the column index
	 * @return the name of the column list specified by the index
	 */
	String getColumnIndexRegistryName(int columnIndex);

	/**
	 * Force an addition to the start of the column index registry.
	 *
	 * @param columnIndexRegistryName the column index registry name to place at the start of the registry
	 */
	void forceAddToStartColumnIndexRegistry(String columnIndexRegistryName);

}
