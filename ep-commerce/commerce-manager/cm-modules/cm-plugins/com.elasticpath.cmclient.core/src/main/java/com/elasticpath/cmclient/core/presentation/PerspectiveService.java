/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.presentation;

import java.util.List;

import org.eclipse.swt.widgets.ToolItem;

/**
 * This service is responsible to return desired Perspective Id when WorkbenchWindow is created.
 * The logic on which Preferred Perspective Id may vary.
 * One example is to return id of the first Perspective that appears in the Perspective ToolBar.
 * Also it holds the list of actual Widgets that represent Perspectives.
 */
public interface PerspectiveService {

	/**
	 * Subclasses may change which perspective should be opened when the user firstly logs in.
	 *
	 * @return preferred perspective id
	 */
	String getPreferredPerspectiveId();

	/**
	 * Getter that will return an ordered list of ToolItems that represent Perspective Toolbar.
	 *
	 * @return list of toolItems
	 */
	List<ToolItem> getPerspectiveToolItems();
}

