/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.conditionbuilder.valueeditor;

import java.util.List;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

import com.elasticpath.tags.domain.SelectableValue;

/**
 * Implementation of {@link SelectableValueResolver}.
 */
public class SelectableValuesProvider implements SelectableValueResolver {

	private final BidiMap valueNameMap;
	private final String[] names;
	private final Object[] values;

	/**
	 * Values provider constructor.
	 *
	 * @param valuesList list of {@link SelectableValue}
	 */
	public SelectableValuesProvider(final List<SelectableValue<Object>> valuesList) {
		super();
		this.valueNameMap = new DualHashBidiMap();
		this.names = new String[valuesList.size()];
		this.values = new Object[valuesList.size()];
		for (int i = 0; i < names.length; i++) {
			SelectableValue<Object> selectableValue = valuesList.get(i);
			names[i] = selectableValue.getName();
			values[i] = selectableValue.getValue();
			valueNameMap.put(selectableValue.getValue(), selectableValue.getName());
		}
	}

	/**
	 * Values provider constructor.
	 * Sets all of the the fields to be EMPTY.
	 */
	public SelectableValuesProvider() {
		super();
		this.valueNameMap = new DualHashBidiMap();
		this.names = new String[0];
		this.values = new Object[0];
	}


	@Override
	public Object getValueBySelectionIndex(final int selectionIndex) {
		return values[selectionIndex];
	}

	@Override
	public String getNameByValue(final Object value) {
		return (String) valueNameMap.get(value);
	}

	/**
	 * Get the object representation for in persistent storage.
	 *
	 * @param value string, that represent object on UI.
	 * @return the key, that will be used in tag framework expression
	 */
	public Object getValueByName(final String value) {
		return valueNameMap.getKey(value);
	}

	/**
	 * Get the list of String values.
	 *
	 * @return list of String values
	 */

	public String[] getNames() {
		return names; //NOPMD
	}

	/**
	 * Returns true if provider has no values.
	 *
	 * @return is empty
	 */
	public boolean isEmpty() {
		return valueNameMap.isEmpty();
	}
}
