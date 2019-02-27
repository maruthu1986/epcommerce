/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.controlcontribution;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * Contributes tooltip to the table.
 */
public abstract class AbstractTableTooltip {

	/**
	 * Adds tooltips for the table.
	 * 
	 * @param table - table for which tooltips will be set
	 * @param wizardContainer - table container
	 * @param horizontalshift - horizontal shift of the tooltip
	 * @param verticalshift - vertical shift of the tooltip
	 */
	public void addTableTooltip(final Table table, final IWizardContainer wizardContainer, final int horizontalshift, final int verticalshift) {

		// Disable native tips
		table.setToolTipText(StringUtils.EMPTY);

		//TODO-RAP-M1 Removed mouse hover events
		// See https://eclipse.org/rap/developers-guide/devguide.php?topic=key-and-mouse-events.html
	}

	/**
	 * Returns Text for label.
	 * 
	 * @param item - table item
	 * @return String - text for label.
	 */
	protected abstract String getLabelText(TableItem item);
}