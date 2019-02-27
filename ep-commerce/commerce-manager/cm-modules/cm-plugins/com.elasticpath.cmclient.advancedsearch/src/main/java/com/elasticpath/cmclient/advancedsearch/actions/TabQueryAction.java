/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.advancedsearch.AdvancedSearchImageRegistry;
import com.elasticpath.cmclient.advancedsearch.AdvancedSearchMessages;
import com.elasticpath.cmclient.advancedsearch.helpers.AdvancedSearchQuerySelector;
import com.elasticpath.cmclient.advancedsearch.views.TabsInteractionController;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.advancedsearch.AdvancedSearchQuery;
import com.elasticpath.persistence.dao.AdvancedSearchQueryDao;

/**
 * Implementation for using TabsInteractionController.
 */
public class TabQueryAction extends AbstractQueryAction {

	private final QueryBuilderAction queryBuilderAction;

	private final TabsInteractionController tabsInteractionController;

	/**
	 * Creates TabQueryAction.
	 *
	 * @param text the Action text
	 * @param imageDescriptor the Action Image Descriptor
	 * @param tabsInteractionController the instance of TabsInteractionController
	 * @param queryBuilderAction the query builder action
	 */
	protected TabQueryAction(final String text, final ImageDescriptor imageDescriptor, final TabsInteractionController tabsInteractionController,
			final QueryBuilderAction queryBuilderAction) {

		super(text, imageDescriptor);

		this.tabsInteractionController = tabsInteractionController;
		this.queryBuilderAction = queryBuilderAction;
	}

	/**
	 * Gets the instance of TabsInteractionController.
	 *
	 * @return TabsInteractionController
	 */
	public TabsInteractionController getTabsInteractionController() {
		return tabsInteractionController;
	}

	@Override
	void performWork() {
		tabsInteractionController.selectQueryBuilderTab(queryBuilderAction);
	}

	/**
	 * This class is responsible for carrying out the action of creating a query.
	 */
	public static class CreateQueryAction extends TabQueryAction {

		/**
		 * Constructs a create query action.
		 *
		 * @param tabsInteractionController instance of TabsInteractionController
		 */
		public CreateQueryAction(final TabsInteractionController tabsInteractionController) {
			super(AdvancedSearchMessages.get().CreateQuery, AdvancedSearchImageRegistry.QUERY_ADD,
					tabsInteractionController, QueryBuilderAction.CREATE);
		}

		@Override
		protected boolean isAuthorized() {
			return isCreateAuthorized() || isManageAuthorized();
		}
	}

	/**
	 * This is base for Actions which need currently selected Query.
	 */
	private static class SelectedTabQueryAction  extends TabQueryAction {
		private final AdvancedSearchQuerySelector selector;

		/**
		 * Creates SelectedTabQueryAction.
		 *
		 * @param text the Action text
		 * @param imageDescriptor the Action Image Descriptor
		 * @param tabsInteractionController the instance of TabsInteractionController
		 * @param selector the instance of AdvancedSearchQuerySelector
		 * @param queryBuilderAction the query builder action
		 */
		protected SelectedTabQueryAction(final String text, final ImageDescriptor imageDescriptor,
				final TabsInteractionController tabsInteractionController, final AdvancedSearchQuerySelector selector,
				final QueryBuilderAction queryBuilderAction) {
			super(text, imageDescriptor, tabsInteractionController, queryBuilderAction);

			this.selector = selector;
		}

		public AdvancedSearchQuery getSelectedQuery() {
			if (selector == null) {
				return null;
			}

			return selector.getCurrentSelected();
		}
	}

	/**
	 * This is base for Actions with hard authorize logic.
	 */
	private static class ManageableTabQueryAction  extends SelectedTabQueryAction {

		/**
		 * Creates ManageableTabQueryAction.
		 *
		 * @param text the Action text
		 * @param imageDescriptor the Action Image Descriptor
		 * @param tabsInteractionController the instance of TabsInteractionController
		 * @param selector the instance of AdvancedSearchQuerySelector
		 * @param queryBuilderAction the query builder action
		 */
		protected ManageableTabQueryAction(final String text, final ImageDescriptor imageDescriptor,
				final TabsInteractionController tabsInteractionController, final AdvancedSearchQuerySelector selector,
				final QueryBuilderAction queryBuilderAction) {
			super(text, imageDescriptor, tabsInteractionController, selector, queryBuilderAction);
		}

		@Override
		protected boolean isAuthorized() {
			AdvancedSearchQuery query = getSelectedQuery();

			if (query != null) {
				if (query.getOwner().equals(LoginManager.getCmUser()) && isCreateAuthorized()) {
					return true;
				}
				if (isManageAuthorized()) {
					return true;
				}
			}
			return false;
		}

	}

	/**
	 * This class is responsible for carrying out the action of opening a query.
	 * Is Always authorized.
	 */
	public static class OpenQueryAction extends SelectedTabQueryAction {

		/**
		 * Constructs a open query action.
		 *
		 * @param tabsInteractionController the instance of TabsInteractionController
		 * @param selector the instance of AdvancedSearchQuerySelector
		 */
		public OpenQueryAction(final TabsInteractionController tabsInteractionController, final AdvancedSearchQuerySelector selector) {
			super(AdvancedSearchMessages.get().OpenQuery, AdvancedSearchImageRegistry.QUERY_OPEN, tabsInteractionController, selector,
					QueryBuilderAction.OPEN);
		}
	}

	/**
	 * This class is responsible for carrying out the action of editing a query.
	 */
	public static class EditQueryAction extends ManageableTabQueryAction {

		/**
		 * Constructs a edit query action.
		 *
		 * @param tabsInteractionController the instance of TabsInteractionController
		 * @param selector the instance of AdvancedSearchQuerySelector
		 */
		public EditQueryAction(final TabsInteractionController tabsInteractionController, final AdvancedSearchQuerySelector selector) {
			super(AdvancedSearchMessages.get().EditQuery,
					AdvancedSearchImageRegistry.QUERY_EDIT,
					tabsInteractionController,
					selector,
					QueryBuilderAction.EDIT);
		}
	}

	/**
	 * This class is responsible for carrying out the action of running a query.
	 * Is Always Authorized
	 */
	public static class RunQueryAction extends TabQueryAction {

		/**
		 * Constructs a run query action.
		 *
		 * @param tabsInteractionController instance of TabsInteractionController
		 */
		public RunQueryAction(final TabsInteractionController tabsInteractionController) {
			super(AdvancedSearchMessages.get().RunQuery, AdvancedSearchImageRegistry.IMAGE_QUERY_RUN, tabsInteractionController, null);
		}

		@Override
		void performWork() {
			getTabsInteractionController().executeSearchForSelectedElement();
		}
	}

	/**
	 * This class is responsible for carrying out the action of deleting a query.
	 */
	public static class DeleteQueryAction extends ManageableTabQueryAction {

		private final AdvancedSearchQueryDao searchQueryDao;

		/**
		 * Constructs a delete query action.
		 *
		 * @param tabsInteractionController instance of TabsInteractionController
		 * @param selector the instance of AdvancedSearchQuerySelector
		 */
		public DeleteQueryAction(final TabsInteractionController tabsInteractionController, final AdvancedSearchQuerySelector selector) {
			super(AdvancedSearchMessages.get().DeleteQuery, AdvancedSearchImageRegistry.QUERY_DELETE, tabsInteractionController, selector, null);

			searchQueryDao = ServiceLocator.getService(ContextIdNames.ADVANCED_SEARCH_QUERY_DAO);
		}

		@Override
		void performWork() {
			AdvancedSearchQuery currentSelected = getSelectedQuery();
			if (currentSelected != null) {

				final boolean answerYes =
					MessageDialog.openConfirm(null, AdvancedSearchMessages.get().DeleteQuery_MsgBox_Title,

							NLS.bind(AdvancedSearchMessages.get().DeleteQuery_MsgBox_Content,
							new Object[]{currentSelected.getName(), currentSelected.getQueryId()}));
							
				if (answerYes) {
					searchQueryDao.remove(currentSelected);
					getTabsInteractionController().refreshSavedQueriesTab();
					getTabsInteractionController().checkQueryBuilderTabAfterDelete(currentSelected);
				}				
			}
		}
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}
}
