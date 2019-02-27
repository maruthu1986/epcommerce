/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.testutil;

import com.elasticpath.cmclient.core.helpers.TestIdMapManager;
import com.elasticpath.cmclient.core.helpers.TestIdUtil;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.core.util.ServiceUtil;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.ClientFileLoader;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.internal.lifecycle.UITestUtil;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.editor.IFormPage;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Adds ids to frontend widgets that can be used by client-side javascript and test automation.
 * <WidgetsSupported>
 * MainMenu; 	(it's components)
 * CTabItem
 * CoolBar;
 * Table;
 * ToolBar;
 * Tree;
 * Text, Label, Button, Combo, Hyperlink; 	(created through <class>EpControlFactory<class/>)
 * </WidgetsSupported>
 */

public class EPWidgetIdUtil implements TestIdUtil {
    private static final String EMPTY_STRING = ""; //$NON-NLS-1$
    private static final String SPACE = " "; //$NON-NLS-1$
    private static final String QUOTE = "'"; //$NON-NLS-1$
    private static final String FILE_PATH = "javascript/ep-test-support.js"; //$NON-NLS-1$
    private static int uniqueId;
    private static boolean initialized;

    /**
     * Constructor.
     */
    public EPWidgetIdUtil() {
        //no-op constructor.
    }

    /**
     * Sets consequent ids to the widgets every time this function is called.
     *
     * @param widget widget
     */
    @Override
    @Deprecated //TODO remove this function
    public void setUniqueId(final Widget widget) {
        setId(widget, Integer.toString(uniqueId));
        uniqueId++;
    }

    /**
     * Loads Javascript file to RWT resource manager.
     *
     * @throws IOException javascript file wasn't found
     */
    @Override
    public void initialize() throws IOException {
        TestIdMapManager.initializeEncodingMarkers();
        ResourceManager resourceManager = RWT.getResourceManager();
        if (!resourceManager.isRegistered(EPWidgetIdUtil.FILE_PATH)) {

            try (InputStream inputStream = EPWidgetIdUtil.class.getClassLoader().getResourceAsStream(EPWidgetIdUtil.FILE_PATH)) {
                resourceManager.register(EPWidgetIdUtil.FILE_PATH, inputStream);
            }
        }

        ClientFileLoader rwtService = ServiceUtil.getRWTService(ClientFileLoader.class);
        rwtService.requireJs(resourceManager.getLocation(EPWidgetIdUtil.FILE_PATH));
        this.setAppearance();
        initialized = true;
    }

    /**
     * Calls the EPTest javascript object to override SetAppearance calls.
     */
    @Override
    public void setAppearance() {
        String callClientJavaScriptFunction = "EPTest.overrideSetAppearances()";
        JavaScriptExecutor executor = ServiceUtil.getRWTService(JavaScriptExecutor.class);
        executor.execute(callClientJavaScriptFunction);
    }


    /**
     * Sets the testId onto the widget's id attribute.
     *  @param widget the Widget.
     * @param widgetId the testId
     */
    @Override
    public void setId(final Widget widget, final String widgetId) {
        if (widget != null && notDisposed(widget) && UITestUtil.isEnabled()) {
            String wId = WidgetUtil.getId(widget);
            String testIdJavaScriptSafe = (widgetId == null) ? EMPTY_STRING : clean(widgetId);
            String widgetType = widget.getClass().getSimpleName();

            mapWidgetIdToTestId(wId, testIdJavaScriptSafe, widgetType);
        }
    }

    @Override
    public void setAutomationId(final Widget widget, final String automationId) {
        String widgetId = WidgetUtil.getId(widget);

        String callClientJavaScriptFunction = "EPTest.setAutomationIdFromJava("
            + QUOTE + widgetId + QUOTE + ","
            + QUOTE + automationId + QUOTE + ");";
        JavaScriptExecutor executor = ServiceUtil.getRWTService(JavaScriptExecutor.class);
        executor.execute(callClientJavaScriptFunction);
    }

    private void mapWidgetIdToTestId(final String widgetId, final String testId, final String widgetType) {
        String callClientJavaScriptFunction = "EPTest.mapWidgetIdToTestId("
                + QUOTE + widgetId + QUOTE + ","
                + QUOTE + testId + QUOTE + ","
                + QUOTE + widgetType + QUOTE + ");";
        JavaScriptExecutor executor = ServiceUtil.getRWTService(JavaScriptExecutor.class);
        executor.execute(callClientJavaScriptFunction);
    }

    /**
     * Cleans the values, makes them easier to access via frontend code and ensures JavaScriptExecutor will not fail.
     *
     * @param value the value to clean up
     * @return acceptable value for JS
     */
    private String clean(final String value) {
        String processedValue = value.replaceAll("\n", SPACE);
        return processedValue.replaceAll("&", EMPTY_STRING);
    }

    private boolean notDisposed(final Widget widget) {
        return !widget.isDisposed();
    }

    /**
     * Attaches test ids to each TableItem.
     *
     * @param table table with items
     */
    @Override
    public void setTestIdsToTableItems(final Table table) {
        int testIdNum = 0;
        for (TableItem tableItem : table.getItems()) {
            this.setId(tableItem, tableItem.getText() + testIdNum);
            testIdNum++;
        }
    }

    /**
     * Attaches test ids to each CTabItem.
     *
     * @param tabFolder tabFolder containing all of the CTabItems
     */
    @Override
    public void setTestIdsToTabFolderItems(final IEpTabFolder tabFolder) {
        //Set test ids to the tabs
        for (CTabItem item : tabFolder.getSwtTabFolder().getItems()) {
            this.setId(item, item.getText());
        }
    }

    /**
     * Add test ids to the tabs inside of tab folder.
     *
     * @param tabFolder tab folder
     * @param page      form page
     */
    @Override
    public void addIdToMultiPageEditorTabFolder(final Composite tabFolder, final IFormPage page) {
        if (tabFolder instanceof CTabFolder) {
            CTabFolder folder = (CTabFolder) tabFolder;
            this.setId(folder, page.getId() + "_" + page.getIndex());
            CTabItem[] tabItems = folder.getItems();
            for (CTabItem tabItem : tabItems) {
                this.setId(tabItem, tabItem.getText());
            }
        }
    }

    /**
     * Calls the setPostLoginWindowId() method on the client side to set an ID to the post login window.
     */
    @Override
    public void setPostLoginWindowId() {
        if (UITestUtil.isEnabled()) {
            String callClientJavaScriptFunction = "EPTest.setPostLoginWindowId()";
            JavaScriptExecutor executor = ServiceUtil.getRWTService(JavaScriptExecutor.class);
            executor.execute(callClientJavaScriptFunction);
        }
    }

    /**
     * send test-id maps to the client.
     * @param minifiedMap shortId to qualifiedFieldName map
     */
    @Override
    public void sendTestIdMapsToClient(final Map<String, String> minifiedMap) {
        if (initialized) {
            JSONObject minifiedJsonMap = new JSONObject(minifiedMap);
            String minifiedJsonString = minifiedJsonMap.toString();

            String callClientJavaScriptFunction = "EPTest.storeMinifiedMap(" + minifiedJsonString + ")";
            JavaScriptExecutor executor = ServiceUtil.getRWTService(JavaScriptExecutor.class);
            executor.execute(callClientJavaScriptFunction);
        }
    }
}
