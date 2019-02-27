/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers;

import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.editor.IFormPage;

import java.io.IOException;
import java.util.Map;

/**
 * Interface for Test Id Util.
 */
public interface TestIdUtil {
	/**
	 * Catalog Browse Tree id.
	 */
	String CATALOG_BROWSE_TREE_ID = "Catalog Browse Tree"; //$NON-NLS-1$
	/**
	 * Catalog Browse ToolBar id.
	 */
	String BROWSE_VIEW_TOOLBAR_ID = "Catalog Browse ToolBar"; //$NON-NLS-1$
	/**
	 * To be removed. Use this value if not certain about id at this point.
	 */
	String NO_ID_TO_BE_IMPLEMENTED = "ID NOT IMPLEMENTED"; //$NON-NLS-1$

	/**
	 * Sets the unique widget Id.
	 * @param widget the widget.
	 */
	@Deprecated //TODO remove this function
	void setUniqueId(Widget widget);

	/**
	 * Initialize the test util.
	 * @throws IOException the exception.
	 */
	void initialize() throws IOException;

	/**
	 * Sets the apperance.
	 */
	void setAppearance();

	/**
	 * Sets the id.
	 * @param widget the widget to set the id on.
	 * @param widgetId the id to set.
	 */
	void setId(Widget widget, String widgetId);

	/**
	 * Set the id to this widget if it is language independent.
	 * @param widget widget
	 * @param automationId automationId
	 */
	void setAutomationId(Widget widget, String automationId);

	/**
	 * Sets Test ids to table items.
	 * @param table the table.
	 */
	void setTestIdsToTableItems(Table table);

	/**
	 * Sets Test ids to tab folder items.
	 * @param tabFolder the tab folder.
	 */
	void setTestIdsToTabFolderItems(IEpTabFolder tabFolder);

	/**
	 * Adds Test ids to multi page editor tab folders.
	 * @param tabFolder the tab folder.
	 * @param page the page.
	 */
	void addIdToMultiPageEditorTabFolder(Composite tabFolder, IFormPage page);

	/**
	 * Sets the post login window id.
	 */
	void setPostLoginWindowId();

	/**
	 * Sends the test id map to the client.
	 * @param minifiedMap the map.
	 */
	void sendTestIdMapsToClient(Map<String, String> minifiedMap);
}
