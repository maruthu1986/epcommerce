/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.actions;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.conditionbuilder.wizard.conditions.handlers.ConditionHandler;
import com.elasticpath.cmclient.conditionbuilder.wizard.model.impl.StoresConditionModelAdapterImpl;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.delivery.views.DynamicContentDeliverySearchResultsView;
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.model.DynamicContentDeliveryModelAdapter;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.LogicalOperatorType;
import com.elasticpath.tags.domain.TagDictionary;

/**
 * Delete shipping service level implementation.
 */
public class DeleteDynamicContentDeliveryAction extends BaseDynamicContentDeliveryAction {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(DeleteDynamicContentDeliveryAction.class);

	/** DynamicContentSearchResultsView list view. */
	private final DynamicContentDeliverySearchResultsView listView;
	private final DynamicContentDeliveryService dynamicContentDeliveryService = ServiceLocator.getService(
		ContextIdNames.DYNAMIC_CONTENT_DELIVERY_SERVICE);
	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	/**
	 * The constructor.
	 *
	 * @param listView the results from search list view.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public DeleteDynamicContentDeliveryAction(final DynamicContentDeliverySearchResultsView listView, final String text,
			final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	@Override
	public void run() {
		LOG.debug("DeleteDynamicContentAssignment Action called."); //$NON-NLS-1$

		boolean fireRefreshView = false;

		final Shell parent = listView.getSite().getShell();
		final DynamicContentDeliveryModelAdapter selectedDynamicAssignmentContentWrapper = listView.getSelectedItem();

		DynamicContentDelivery dynamicContentDeliveryToDelete =
			dynamicContentDeliveryService.findByGuid(selectedDynamicAssignmentContentWrapper.getDynamicContentDelivery().getGuid());

		if (dynamicContentDeliveryToDelete == null) {
			MessageDialog.openInformation(parent, TargetedSellingMessages.get().NoLongerExistDynamicContentMsgBoxTitle,
				NLS.bind(TargetedSellingMessages.get().NoLongerExistDynamicContentMsgBoxText,
				selectedDynamicAssignmentContentWrapper
				.getDynamicContentDelivery().getName()));
			// listView.removeSelected();
			fireRefreshView = true;
		} else {
			DynamicContentDeliveryModelAdapter dcaWrapperToDelete = new DynamicContentDeliveryModelAdapter(dynamicContentDeliveryToDelete);

			final String storeNames = getStoreNames(dcaWrapperToDelete);
			final String contentspaceNames = getContentSpaces(dynamicContentDeliveryToDelete);

			if (MessageDialog.openConfirm(parent, TargetedSellingMessages.get().ConfirmDeleteDynamicContentDeliveryMsgBoxTitle,
				NLS.bind(TargetedSellingMessages.get().ConfirmDeleteDynamicContentDeliveryMsgBoxText,
				new Object[]{dynamicContentDeliveryToDelete.getName(),
				storeNames, contentspaceNames}))) {

				changeSetHelper.addObjectToChangeSet(dynamicContentDeliveryToDelete, ChangeSetMemberAction.DELETE);

				performDelete(dynamicContentDeliveryService, dynamicContentDeliveryToDelete);

				fireRefreshView = true;
			}
		}
		if (fireRefreshView) {
			fireEvent(EventType.DELETE, dynamicContentDeliveryToDelete);
		}
	}

	/**
	 * Perform delete.
	 * @param dynamicContentDeliveryService instance of DynamicContentDeliveryService
	 * @param dynamicContentDeliveryToDelete instance of DynamicContentAssignment
	 */
	private void performDelete(
			final DynamicContentDeliveryService dynamicContentDeliveryService,
			final DynamicContentDelivery dynamicContentDeliveryToDelete) {
		dynamicContentDeliveryService.remove(
				dynamicContentDeliveryService.findByName(dynamicContentDeliveryToDelete.getName())
				);
		deleteSellingContextManually(dynamicContentDeliveryToDelete);
	}

	/**
	 * Get the comma separated list of content spaces, that use given dynamic content delivery.
	 * @param dynamicContentAssignmentToDelete dynamic content delivery.
	 * @return comma separated list of content spaces, that use given dynamic content delivery.
	 */
	private String getContentSpaces(final DynamicContentDelivery dynamicContentAssignmentToDelete) {
		final StringBuilder contentspaceNames = new StringBuilder();
		Set<ContentSpace> contentspaces = dynamicContentAssignmentToDelete.getContentspaces();
		if (contentspaces != null && !contentspaces.isEmpty()) {
			for (ContentSpace contentspace : contentspaces) {
				contentspaceNames.append(contentspace.getTargetId());
				contentspaceNames.append(", "); //$NON-NLS-1$
			}
			contentspaceNames.delete(contentspaceNames.length() - 2, contentspaceNames.length() - 1); // last comma
		}
		return contentspaceNames.toString();
	}

	/**
	 * Get the comma separated list of stores, that use given dynamic content delivery.
	 * @param dcaWrapperToDelete dynamic content delivery.
	 * @return comma separated list of stores, that use given dynamic content delivery.
	 */
	private String getStoreNames(final DynamicContentDeliveryModelAdapter dcaWrapperToDelete) {

		List<Store> stores;

		ConditionalExpression expression = dcaWrapperToDelete.getSellingContext().getCondition(TagDictionary.DICTIONARY_STORES_GUID);
		if (expression == null) {
			// get list of all stores
			stores = new StoresConditionModelAdapterImpl(new LogicalOperator(LogicalOperatorType.AND)).getAllStores();
		} else {
			// get list of assigned stores
			ConditionHandler handler = new ConditionHandler();
			LogicalOperator logicalOperator = handler.convertConditionExpressionStringToLogicalOperator(expression);
			stores = new StoresConditionModelAdapterImpl(logicalOperator).getStores();
		}

		final StringBuilder storesNames = new StringBuilder();
		if (stores != null && !stores.isEmpty()) {
			for (Store store : stores) {
				storesNames.append(store.getName());
				storesNames.append(", "); //$NON-NLS-1$
			}
			storesNames.delete(storesNames.length() - 2, storesNames.length() - 1); // last comma
		}
		return storesNames.toString();
	}

	/**
	 * must delete selling context manually because currently selling context is only
	 * editable through delivery wizard only.
	 *
	 * @param dynamicContentAssignmentToDelete the guid of the context to delete
	 */
	private void deleteSellingContextManually(final DynamicContentDelivery dynamicContentAssignmentToDelete) {
		SellingContextHelper.deleteSellingContextManually(dynamicContentAssignmentToDelete.getSellingContextGuid());
	}

	@Override
	public String getTargetIdentifier() {
		return "deleteDcdAction"; //$NON-NLS-1$
	}

	@Override
	protected Object getDependentObject() {
		if (listView == null) {
			return null;
		}
		if (listView.getSelectedItem() != null) {
			return dynamicContentDeliveryService.findByGuid(listView.getSelectedItem().getGuid());
		}
		return null;
	}

}
