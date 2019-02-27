/**
 * Copyright (c) Elastic Path Software Inc., 2007
 *
 */
package com.elasticpath.cmclient.admin.users.views;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.admin.users.AdminUsersImageRegistry;
import com.elasticpath.cmclient.admin.users.AdminUsersMessages;
import com.elasticpath.cmclient.admin.users.AdminUsersPlugin;
import com.elasticpath.cmclient.admin.users.event.AdminUsersEventListener;
import com.elasticpath.cmclient.admin.users.event.AdminUsersEventService;
import com.elasticpath.cmclient.admin.users.helpers.UserSearchRequestJob;
import com.elasticpath.cmclient.admin.users.wizards.UserWizard;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.core.service.impl.AuthenticationServiceImpl;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractSortListView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EmailSendException;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.UserStatus;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * View to show and allow the manipulation of the available Users in CM.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class UserListView extends AbstractSortListView implements AdminUsersEventListener {
	private static final Logger LOG = Logger.getLogger(UserListView.class);

	/** The view ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.admin.users.views.UserListView"; //$NON-NLS-1$

	private static final String USER_LIST_TABLE = "User List"; //$NON-NLS-1$

	// Column indices

	private static final int INDEX_USERNAME = 0;

	private static final int INDEX_LASTNAME = 1;

	private static final int INDEX_FIRSTNAME = 2;

	private static final int INDEX_EMAIL = 3;

	private static final int INDEX_STATUS = 4;

	private final Separator userActionGroup = new Separator("userActionGroup"); //$NON-NLS-1$

	// Actions
	private final Action createUserAction = new CreateUserAction();

	private final Action editUserAction = new EditUserAction();

	private final Action deleteUserAction = new DisableUserAction();

	private final Action editUserPasswordAction = new ChangeUserPasswordAction();

	// Actions have to be wrapped in ActionContributionItems so that they can be forced to display both text and image
	private final ActionContributionItem createUserActionContributionItem = new ActionContributionItem(createUserAction);

	private final ActionContributionItem editUserActionContributionItem = new ActionContributionItem(editUserAction);

	private final ActionContributionItem deleteUserActionContributionItem = new ActionContributionItem(deleteUserAction);

	private final ActionContributionItem editUserPasswordActionContributionItem = new ActionContributionItem(editUserPasswordAction);
	
	private final Map<UserStatus, String> userStatusLocalizedMessages = new HashMap<>();

	private UserSearchRequestJob userSearchRequestJob;

	/**
	 * Constructor.
	 */
	public UserListView() {
		super(true, USER_LIST_TABLE);
		userStatusLocalizedMessages.put(UserStatus.DISABLED, AdminUsersMessages.get().Inactive);
		userStatusLocalizedMessages.put(UserStatus.ENABLED, AdminUsersMessages.get().Active);
		userStatusLocalizedMessages.put(UserStatus.LOCKED, AdminUsersMessages.get().Locked);
	}

	@Override
	protected void initializeViewToolbar() {

		getToolbarManager().add(userActionGroup);

		createUserActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		editUserActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		deleteUserActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		editUserPasswordActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		getToolbarManager().appendToGroup(userActionGroup.getGroupName(), editUserActionContributionItem);
		getToolbarManager().appendToGroup(userActionGroup.getGroupName(), createUserActionContributionItem);
		getToolbarManager().appendToGroup(userActionGroup.getGroupName(), deleteUserActionContributionItem);
		getToolbarManager().appendToGroup(userActionGroup.getGroupName(), editUserPasswordActionContributionItem);

		// Disable buttons until a row is selected.
		disableActions();
		this.getViewer().addSelectionChangedListener(event -> {
			CmUser selectedUser = getSelectedUser();

			if (selectedUser == null) {
				enableActions();
			} else {
				if (selectedUser.equals(LoginManager.getCmUser())) {
					//logged in user is selected user
					deleteUserAction.setEnabled(false);
					editUserAction.setEnabled(true);
					editUserPasswordAction.setEnabled(true);
				} else {
					//logged in user is not selected user
					enableActions();

					//grey out disable button if user is already disabled
					if (selectedUser.getUserStatus().equals(UserStatus.DISABLED)) {
						deleteUserAction.setEnabled(false);
					}
				}
			}
		});
	}
	
	private void disableActions() {
		deleteUserAction.setEnabled(false);
		editUserAction.setEnabled(false);
		editUserPasswordAction.setEnabled(false);
	}

	private void enableActions() {
		deleteUserAction.setEnabled(true);
		editUserAction.setEnabled(true);
		editUserPasswordAction.setEnabled(true);
	}

	@Override
	protected void initializeTable(final IEpTableViewer table) {
		final String[] columnNames = new String[] { AdminUsersMessages.get().UserName,
				AdminUsersMessages.get().LastName, AdminUsersMessages.get().FirstName,
				AdminUsersMessages.get().Email, AdminUsersMessages.get().Status };

		final SortBy[] sortBy = new SortBy[] {
				StandardSortBy.NAME,
				StandardSortBy.LAST_NAME,
				StandardSortBy.FIRST_NAME,
				StandardSortBy.EMAIL,
				StandardSortBy.STATUS
		};

		final int[] columnWidths = new int[] { 120, 120, 120, 200, 80 };
		
		for (int i = 0; i < columnNames.length; i++) {
			IEpTableColumn tableColumn = table.addTableColumn(columnNames[i], columnWidths[i]);
			registerTableColumn(tableColumn, sortBy[i]);
		}
		addDoubleClickAction(editUserAction);
	}
	
	@Override
	protected Object[] getViewInput() {
		return null;
	}
	
	
	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new UserListViewLabelProvider();
	}

	/**
	 * Gets the currently-selected user.
	 * 
	 * @return the currently-selected CmUser
	 */
	protected CmUser getSelectedUser() {
		ISelection selection = UserListView.this.getViewer().getSelection();
		if (selection == null) {
			return null;
		}
		return (CmUser) ((IStructuredSelection) selection).getFirstElement();
	}

	/**
	 * Label provider for the view.
	 */
	protected class UserListViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		/**
		 * Get the image to put in each column.
		 * 
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the Image to put in the column
		 */
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		/**
		 * Get the text to put in each column.
		 * 
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the String to put in the column
		 */
		public String getColumnText(final Object element, final int columnIndex) {
			if (!(element instanceof CmUser)) {
				return null;
			}
			CmUser user = (CmUser) element;

			switch (columnIndex) {

			// case UserListView.INDEX_USERIMAGE:
			// return AdminUsersMessages.EMPTY_STRING;
			case UserListView.INDEX_USERNAME:
				return user.getUserName();
			case UserListView.INDEX_FIRSTNAME:
				return user.getFirstName();
			case UserListView.INDEX_LASTNAME:
				return user.getLastName();
			case UserListView.INDEX_EMAIL:
				return user.getEmail();
			case UserListView.INDEX_STATUS:
				return userStatusLocalizedMessages.get(user.getUserStatus());
			default:
				return AdminUsersMessages.EMPTY_STRING;
			}
		}
	}

	/**
	 * Begin the process for creating a new user.
	 */
	protected class CreateUserAction extends Action {

		/**
		 * Constructor.
		 */
		public CreateUserAction() {
			super();
			this.setImageDescriptor(AdminUsersImageRegistry.IMAGE_USER_CREATE);
			this.setToolTipText(AdminUsersMessages.get().CreateUser);
			this.setText(AdminUsersMessages.get().CreateUser);
		}

		@Override
		public void run() {
			LOG.debug("CreateUser Action called."); //$NON-NLS-1$
			// Create a new user
			CmUser newUser = (CmUser) ServiceLocator.getService(ContextIdNames.CMUSER);
			// Create the wizard
			if (Window.OK == UserWizard.showWizard(UserListView.this.getSite().getShell(), newUser)) {
				CmUserService cmUserService = (CmUserService) ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);
				try {
					cmUserService.add(newUser);
				} catch (EmailSendException exception) {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(), AdminUsersMessages.get().CreateUserErrorEmailDialogTitle,
							AdminUsersMessages.get().CreateUserErrorEmailDialogDescription);
				}
				refreshViewerInput();
			}
		}
	}

	/**
	 * Begin the process for editing a user.
	 */
	private class EditUserAction extends Action {

		/**
		 * Constructor.
		 */
		EditUserAction() {
			super();
			this.setImageDescriptor(AdminUsersImageRegistry.IMAGE_USER_EDIT);
			this.setToolTipText(AdminUsersMessages.get().EditUser);
			this.setText(AdminUsersMessages.get().EditUser);
		}

		@Override
		public void run() {
			LOG.debug("EditUser Action called."); //$NON-NLS-1$
			CmUser editableUser = getPersistedCmUser((IStructuredSelection) UserListView.this.getViewer().getSelection());
			if (Window.OK == UserWizard.showWizard(UserListView.this.getSite().getShell(), editableUser)) {
				CmUserService cmUserService = (CmUserService) ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);
				cmUserService.update(editableUser);
				refreshViewerInput();
			}
		}

		private CmUser getPersistedCmUser(final IStructuredSelection selectedUser) {
			String userName = ((CmUser) selectedUser.getFirstElement()).getUserName();
			CmUserService service = (CmUserService) ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);
			return service.findByUserNameWithAccessInfo(userName);
		}
	}

	/**
	 * Begin the process for deleting a user.
	 */
	private class DisableUserAction extends Action {

		/**
		 * Constructor.
		 */
		DisableUserAction() {
			super();
			this.setImageDescriptor(AdminUsersImageRegistry.IMAGE_USER_DELETE);
			this.setToolTipText(AdminUsersMessages.get().DisableUser);
			this.setText(AdminUsersMessages.get().DisableUser);
		}

		@Override
		public void run() {
			LOG.debug("DisableUser Action called."); //$NON-NLS-1$
			CmUser cmUser = getSelectedUser();
			if (cmUser == null) {
				return;
			}
			MessageBox confirmBox = new MessageBox(UserListView.this.getSite().getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			confirmBox.setText(AdminUsersMessages.get().DisableUser);
			confirmBox.setMessage(AdminUsersMessages.get().ConfirmDisableUser + AdminUsersMessages.get().ConfirmLineSeparator + cmUser.getFirstName()
					+ AdminUsersMessages.SPACE + cmUser.getLastName());
			if (confirmBox.open() == SWT.NO) {
				return;
			}
			disableCmUser(cmUser);
			refreshViewerInput();
		}

		private void disableCmUser(final CmUser cmUser) {
			LOG.info("Disabling user: " + cmUser.getFirstName()); //$NON-NLS-1$
			CmUserService service = (CmUserService) ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);
			try {
				final CmUser userToDisable = service.get(cmUser.getUidPk());
				userToDisable.setEnabled(false);
				service.update(userToDisable);
				deleteUserAction.setEnabled(false);
				
			} catch (EpPersistenceException e) {
				LOG.error(e);
			}
		}
	}

	/**
	 * Edit user password.
	 */
	private class ChangeUserPasswordAction extends Action {

		/**
		 * Constructor.
		 */
		ChangeUserPasswordAction() {
			super();
			this.setImageDescriptor(AdminUsersImageRegistry.IMAGE_CHANGE_USER_PASSWORD);
			this.setToolTipText(AdminUsersMessages.get().ResetUserPassword);
			this.setText(AdminUsersMessages.get().ResetUserPassword);
		}

		@Override
		public void run() {
			LOG.debug("ResetUserPassword Action called."); //$NON-NLS-1$
			resetPassword((IStructuredSelection) UserListView.this.getViewer().getSelection());
		}

		private void resetPassword(final IStructuredSelection selectedUser) {
			if (MessageDialog.openConfirm(Display.getDefault().getActiveShell(), AdminUsersMessages.get().ChangePasswordDialogConfirmTitle,
					AdminUsersMessages.get().ChangePasswordDialogConfirm)) {
				String userEmail = ((CmUser) selectedUser.getFirstElement()).getEmail();
				CmUserService service = (CmUserService) ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);
				try {
					final CmUser updatedUser = service.resetUserPassword(userEmail);
					
					//make re-authentication if it is current user
					if (LoginManager.getCmUser().equals(updatedUser)) {
						AuthenticationServiceImpl.getInstance().login(updatedUser.getUserName(), updatedUser.getClearTextPassword());
					}

					refreshViewerInput();
				} catch (EmailSendException e) {
					LOG.debug("Error reseting password", e); //$NON-NLS-1$
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							CoreMessages.get().ChangePasswordDialog_Error_EmailError_DialogTitle,
							CoreMessages.get().ChangePasswordDialog_Error_EmailError_DialogDescription);
				}
			}
		}

	}

	@Override
	public void searchResultsUpdate(final SearchResultEvent<CmUser> event) {
		userSearchRequestJob = (UserSearchRequestJob) event.getSource();
		getViewer().getTable().getDisplay().syncExec(() -> {
			UserListView.this.setResultsCount(event.getTotalNumberFound());
			UserListView.this.getViewer().getTable().clearAll();

			if (event.getItems().isEmpty() && event.getStartIndex() <= 0) {
				UserListView.this.showMessage(CoreMessages.get().NoSearchResultsError);
			} else {
				UserListView.this.hideErrorMessage();
			}

			UserListView.this.getViewer().setInput(event.getItems().toArray());
			UserListView.this.setResultsStartIndex(event.getStartIndex());
			UserListView.this.updateSortingOrder(userSearchRequestJob.getSearchCriteria());
			UserListView.this.updateNavigationComponents();
		});
	}
	
	@Override
	public void dispose() {
		AdminUsersEventService.getInstance().unregisterUserEventListener(this);
		super.dispose();
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		
		AdminUsersEventService.getInstance().registerUserEventListener(this);
	}
	
	@Override
	public void refreshViewerInput() {
		if (userSearchRequestJob != null) {
			userSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		}
	}

	@Override
	protected String getPluginId() {
		return AdminUsersPlugin.PLUGIN_ID;
	}

	@Override
	public AbstractSearchRequestJob< ? extends Persistable> getSearchRequestJob() {
		return userSearchRequestJob;
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
