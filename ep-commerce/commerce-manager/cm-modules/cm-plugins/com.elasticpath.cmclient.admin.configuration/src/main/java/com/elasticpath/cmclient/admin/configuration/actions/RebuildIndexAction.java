/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.configuration.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.admin.configuration.AdminConfigurationImageRegistry;
import com.elasticpath.cmclient.admin.configuration.AdminConfigurationMessages;
import com.elasticpath.cmclient.admin.configuration.models.IndexBuildStatusSelector;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;

/**
 * Rebuild Index Action.
 */
public class RebuildIndexAction extends Action {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(RebuildIndexAction.class);

	private final IndexBuildStatusSelector indexBuildStatusSelector;

	private final IndexNotificationService indexNotificationService;

	private final Shell shell;

	/**
	 * The constructor.
	 *
	 * @param shell the shell
	 * @param selector the IndexBuildStatusSelector instance
	 */
	public RebuildIndexAction(final Shell shell, final IndexBuildStatusSelector selector) {
		super(AdminConfigurationMessages.get().RebuildIndex, AdminConfigurationImageRegistry.IMAGE_SEARCH_INDEX_REBUILD);
		this.shell = shell;
		this.setToolTipText(AdminConfigurationMessages.get().RebuildIndex);
		this.indexBuildStatusSelector = selector;
		this.indexNotificationService = ServiceLocator.getService(ContextIdNames.INDEX_NOTIFICATION_SERVICE);
	}

	@Override
	public void run() {
		LOG.debug("Rebuild Index Action called."); //$NON-NLS-1$

		final IndexType indexType = indexBuildStatusSelector.getSelectedIndexBuildStatus().getIndexType();

		if (MessageDialog.openConfirm(shell, AdminConfigurationMessages.get().RebuildConfirmTitle,
			NLS.bind(AdminConfigurationMessages.get().RebuildConfirmMessage,
			indexType.getIndexName()))) {
			LOG.info("Rebuilding Index with Type : " + indexType); //$NON-NLS-1$

			final IndexNotification indexNotification = ServiceLocator.getService(ContextIdNames.INDEX_NOTIFICATION);
			indexNotification.setIndexType(indexType);
			indexNotification.setUpdateType(UpdateType.REBUILD);
			indexNotificationService.add(indexNotification);

			indexBuildStatusSelector.refresh();
			setEnabled(false);
		}
	}
}
