/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.pricelistassignments.wizard.tabletooltip;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.TableItem;

import com.elasticpath.cmclient.core.controlcontribution.AbstractTableTooltip;
import com.elasticpath.domain.pricing.PriceListDescriptor;

/**
 * Adds tooltip for {@link PriceListDescriptor} table. 
 */
public class PriceListDescriptorTableTooltip extends AbstractTableTooltip {

	@Override
	protected String getLabelText(final TableItem item) {
		if (null == item || null == item.getData()) {
			return StringUtils.EMPTY;
		}
		return ((PriceListDescriptor) item.getData()).getDescription();
	}

}
