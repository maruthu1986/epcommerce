/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.helpers.extenders.PluginHelper;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import org.apache.log4j.Logger;
import org.eclipse.rap.rwt.internal.lifecycle.UITestUtil;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.editor.IFormPage;

import java.io.IOException;
import java.util.Map;

/**
 * Factory for creating EPTestUtil.
 * Falls back to No-Op util if none registered as extension.
 */
public class EPTestUtilFactory {
	private static final Logger LOG = Logger.getLogger(EPTestUtilFactory.class);
	private TestIdUtil epWidgetUtil;



	public static EPTestUtilFactory getInstance() {
		return CmSingletonUtil.getApplicationInstance(EPTestUtilFactory.class);
	}
	/**
	 * Get the epTestUtil instanace that has been registered, or a No-Op otherwise.
	 * @return the TEstIdUtil.
	 */
	public TestIdUtil getTestIdUtil() {
		if (epWidgetUtil == null) {
			epWidgetUtil = PluginHelper.findTestIdUtil();

			if (epWidgetUtil == null || !UITestUtil.isEnabled()) {
				LOG.debug("using No-OP test id util.");
				epWidgetUtil = new NoOpEpTestUtil();
			} else {
				LOG.debug("using Test plugin id util.");
			}
		}
		return epWidgetUtil;
	}

	/**
	 * No-Op implementation of the TestIdUtil.
	 */
	private static class NoOpEpTestUtil implements TestIdUtil {
		private static final Logger LOG = Logger.getLogger(NoOpEpTestUtil.class);
		private static final String NO_OP = "NO-OP";

		@Override
		public void setUniqueId(final Widget widget) {
			LOG.trace(NO_OP);
		}

		@Override
		public void initialize() throws IOException {
			LOG.trace(NO_OP);
		}

		@Override
		public void setAppearance() {
			LOG.trace(NO_OP);
		}

		@Override
		public void setId(final Widget widget, final String widgetId) {
			LOG.trace(NO_OP);
		}

		@Override
		public void setAutomationId(final Widget widget, final String automationId) {
			LOG.trace(NO_OP);
		}

		@Override
		public void setTestIdsToTableItems(final Table table) {
			LOG.trace(NO_OP);
		}

		@Override
		public void setTestIdsToTabFolderItems(final IEpTabFolder tabFolder) {
			LOG.trace(NO_OP);
		}

		@Override
		public void addIdToMultiPageEditorTabFolder(final Composite tabFolder, final IFormPage page) {
			LOG.trace(NO_OP);
		}

		@Override
		public void setPostLoginWindowId() {
			LOG.trace(NO_OP);
		}

		@Override
		public void sendTestIdMapsToClient(final Map<String, String> minifiedMap) {
			LOG.trace(NO_OP);
		}
	}
}
