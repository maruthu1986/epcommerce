/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.wizard.tabletooltip;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.TableItem;

import com.elasticpath.cmclient.core.controlcontribution.AbstractTableTooltip;
import com.elasticpath.domain.contentspace.ContentSpace;

/**
 * Adds tooltip for {@link ContentSpace} table. 
 */
public class ContentSpaceTableTooltip extends AbstractTableTooltip {

	@Override
	protected String getLabelText(final TableItem item) {
		if (null == item || null == item.getData()) {
			return StringUtils.EMPTY;
		}
		return ((ContentSpace) item.getData()).getDescription();
	}

}
