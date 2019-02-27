/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.views.DynamicContentSearchResultsView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.contentspace.DynamicContentService;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;

/**
 * Delete dynamic content implementation.
 */
public class DeleteDynamicContentAction extends AbstractBaseDynamicContentAction {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(DeleteDynamicContentAction.class);

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);
	private final DynamicContentService dynamicContentService = ServiceLocator.getService(ContextIdNames.DYNAMIC_CONTENT_SERVICE);
	private final DynamicContentDeliveryService dcaService = ServiceLocator.getService(ContextIdNames.DYNAMIC_CONTENT_DELIVERY_SERVICE);

	/** DynamicContentSearchResultsView list view. */
	private final DynamicContentSearchResultsView listView;

	/**
	 * The constructor.
	 *
	 * @param listView the results from search list view.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public DeleteDynamicContentAction(final DynamicContentSearchResultsView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	@Override
	public void run() {
		LOG.debug("DeleteDynamicContent Action called."); //$NON-NLS-1$

		final DynamicContent selectedDynamicContent = listView.getSelectedItem();



		Shell parent = listView.getSite().getShell();
		boolean fireRefreshView = false;

		final DynamicContent dynamicContentToDelete = dynamicContentService.findByGuid(selectedDynamicContent.getGuid());

		if (dynamicContentToDelete == null) {
			MessageDialog.openInformation(parent, TargetedSellingMessages.get().NoLongerExistDynamicContentMsgBoxTitle,
				NLS.bind(TargetedSellingMessages.get().NoLongerExistDynamicContentMsgBoxText,
				selectedDynamicContent.getName()));
			fireRefreshView = true;
		} else {
			if (dcaService.isDynamicContentAssigned(dynamicContentToDelete)) {
				MessageDialog.openInformation(parent, TargetedSellingMessages.get().UsedDynamicContentDialogTitle,
					NLS.bind(TargetedSellingMessages.get().UsedDynamicContentDialogText,
					dynamicContentToDelete.getName()));

				return;
			}

			boolean confirmed = MessageDialog.openConfirm(listView.getSite().getShell(),
					TargetedSellingMessages.get().ConfirmDeleteDynamicContentMsgBoxTitle,

					NLS.bind(TargetedSellingMessages.get().ConfirmDeleteDynamicContentMsgBoxText,
					dynamicContentToDelete.getName()));
			if (confirmed) {
				changeSetHelper.addObjectToChangeSet(dynamicContentToDelete, ChangeSetMemberAction.DELETE);
				dynamicContentService.remove(dynamicContentToDelete);

				fireRefreshView = true;
			}
		}

		if (fireRefreshView) {
			fireEvent(EventType.DELETE, dynamicContentToDelete);
		}
	}

	@Override
	protected Object getDependentObject() {
		if (listView == null) {
			return null;
		}
		return listView.getSelectedItem();
	}

	@Override
	public String getTargetIdentifier() {
		return "deleteDynamicContentAction"; //$NON-NLS-1$
	}
}

