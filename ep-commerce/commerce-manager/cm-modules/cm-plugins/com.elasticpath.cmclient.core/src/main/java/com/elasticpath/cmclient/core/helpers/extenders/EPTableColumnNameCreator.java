/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.helpers.extenders;

import java.util.List;

import com.elasticpath.commons.util.Pair;

/**
 * Extension to EPTableColumnCreator to use column names rather than indexes to get the column text.
 */
public interface EPTableColumnNameCreator extends EPTableColumnCreator {
    
    /**
     * Gets the list of column names and localized labels.  Extensions can use the names to identify
     * which column is being accessed later on when getting its value from the model object.
     *
     * @return list of column names and localized labels
     */
    List<Pair> visitColumnNameLabels();
    
    /**
     * Gets the value of a column for a given column name.
     *
     * @param element the model object
     * @param columnName the name of the column
     * @return the column's value
     */
    String visitColumnText(Object element, String columnName);

}
