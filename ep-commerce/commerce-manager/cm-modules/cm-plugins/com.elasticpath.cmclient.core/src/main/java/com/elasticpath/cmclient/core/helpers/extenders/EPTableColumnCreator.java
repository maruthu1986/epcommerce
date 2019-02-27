/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers.extenders;

import java.util.List;

/**
 * EP Table Column creator.
 */
public interface EPTableColumnCreator {

    /**
     * Gets the list of column names.
     *
     * @return the column names.
     */
    List<String> visitColumnNames();

    /**
     * Gets column data.
     *
     * @param element the element.
     * @param columnIndex the index.
     * @return column data.
     */
    String visitColumn(Object element, int columnIndex);
}
