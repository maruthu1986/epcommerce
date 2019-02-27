/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.users.views;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.admin.users.AdminUsersMessages;
import com.elasticpath.cmclient.admin.users.helpers.UserSearchRequestJob;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.EpSortingCompositeControl;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.views.AbstractCmClientView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.domain.cmuser.UserStatus;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.cmuser.UserRoleService;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.service.search.query.UserSearchCriteria;
import com.elasticpath.service.store.StoreService;

/**
 * The view that allows a user to select a specific view from different admin plugins.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass" })
public class UserSearchView extends AbstractCmClientView {

	private static final Logger LOG = Logger.getLogger(UserSearchView.class);
	
	/** The View's ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.admin.users.views.UserSearchView"; //$NON-NLS-1$

	private static final int NORMAL_TEXT_LENGTH = 255;

	private static final int ANY_FILTER_INDEX = 0;

	private static final int COMBO_HORIZONTAL_SPACING = 8;

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private Text userNameText;

	private Text userLastNameText;

	private Text userFirstNameText;

	private Text userEmailText;

	private CCombo statusCombo;

	private CCombo userRolesCombo;

	private CCombo catalogCombo;

	private CCombo storeCombo;

	private Button searchButton;

	private Button clearButton;
	
	private EpSortingCompositeControl sortingControl;

	private UserSearchCriteria userSearchCriteria;
	
	private final UserSearchRequestJob searchJob = new UserSearchRequestJob();

	@Override
	protected void createViewPartControl(final Composite parentComposite) {
		IEpLayoutComposite parentEpComposite = CompositeFactory.createGridLayoutComposite(parentComposite, 1, false);

		final IEpLayoutComposite fixedComposite = parentEpComposite.addScrolledGridLayoutComposite(1, true);
		this.createTermsGroup(fixedComposite);
		this.createFiltersGroup(fixedComposite);
		this.createSortingGroup(fixedComposite);

		this.createButtonsPane(parentEpComposite);

		this.populateControls();
		this.bindControls();
	}

	private void populateControls() {
		populateUserStatusCombo();		
		populateUserRoleCombo();		
		populateCatalogCombo();		
		populateStoreCombo();
		populateSortingControl();
	}

	private void createTermsGroup(final IEpLayoutComposite parentComposite) {
		final IEpLayoutData data = parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutComposite searchTermsGroup = parentComposite.addGroup(AdminUsersMessages.get().SearchView_SearchTermsGroup, 1, false, data);
		
		final SelectionListener textListener = new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				processSearch();
			}
		};
		
		this.userNameText = createTextField(AdminUsersMessages.get().SearchView_Search_Label_UserName, searchTermsGroup, data, textListener);
		this.userLastNameText = createTextField(AdminUsersMessages.get().SearchView_Search_Label_UserLastName, searchTermsGroup, data, textListener);
		this.userFirstNameText = createTextField(AdminUsersMessages.get().SearchView_Search_Label_UserFirstName,
				searchTermsGroup, data, textListener);
		this.userEmailText = createTextField(AdminUsersMessages.get().SearchView_Search_Label_UserEmail, searchTermsGroup, data, textListener);
	}


	private void createFiltersGroup(final IEpLayoutComposite parentComposite) {
		final IEpLayoutData data = parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutComposite groupComposite = parentComposite.addGroup(AdminUsersMessages.get().SearchView_FiltersGroup, 1, false, data);

		this.statusCombo = createComboField(AdminUsersMessages.get().SearchView_Filter_Label_Status, groupComposite, data);
		this.userRolesCombo = createComboField(AdminUsersMessages.get().SearchView_Filter_Label_User_Roles, groupComposite, data);
		this.catalogCombo = createComboField(AdminUsersMessages.get().SearchView_Filter_Label_AssignedCatalog, groupComposite, data);
		this.storeCombo = createComboField(AdminUsersMessages.get().SearchView_Filter_Label_AssignedStore, groupComposite, data);
	}

	private void createSortingGroup(final IEpLayoutComposite parentComposite) {
		this.sortingControl = new EpSortingCompositeControl(parentComposite, getModel());
	}

	private void createButtonsPane(final IEpLayoutComposite parentComposite) {
		final IEpLayoutData wrapCompositeData = parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutComposite wrapComposite = parentComposite.addTableWrapLayoutComposite(1, false, wrapCompositeData);

		// alter the wrapper composite by removing the margins and vertical spacing between the two components
		final TableWrapLayout tableWrapLayout = (TableWrapLayout) wrapComposite.getSwtComposite().getLayout();
		tableWrapLayout.verticalSpacing = 0;
		tableWrapLayout.bottomMargin = 0;
		tableWrapLayout.leftMargin = 0;
		tableWrapLayout.rightMargin = 0;

		wrapComposite.addHorizontalSeparator(wrapComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false));

		final IEpLayoutData buttonsCompositeData = parentComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, true, false);
		// wrapper composite for the buttons in order to make them be right aligned. Without that
		final IEpLayoutComposite buttonsWrapComposite = wrapComposite.addGridLayoutComposite(1, false, buttonsCompositeData);
		// buttons composite holding the buttons and setting them to the right
		final IEpLayoutComposite buttonsComposite = buttonsWrapComposite.addGridLayoutComposite(2, true, buttonsCompositeData);

		// search button
		Image searchImage = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_SEARCH_ACTIVE);
		this.searchButton = buttonsComposite.addPushButton(AdminUsersMessages.get().SearchView_SearchButton, searchImage, EpState.EDITABLE, null);
		this.searchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				processSearch();
			}
		});

		// clear button
		this.clearButton = buttonsComposite.addPushButton(AdminUsersMessages.get().SearchView_ClearButton, EpState.EDITABLE, null);
		this.clearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				clearFiledsToDefault();
			}
		});
	}	

	private void populateUserStatusCombo() {
		this.statusCombo.add(AdminUsersMessages.get().SearchView_Filter_Item_Any, ANY_FILTER_INDEX);
		this.statusCombo.add(AdminUsersMessages.get().SearchView_Filter_Item_Active);
		this.statusCombo.add(AdminUsersMessages.get().SearchView_Filter_Item_Locked);
		this.statusCombo.add(AdminUsersMessages.get().SearchView_Filter_Item_Disabled);
		this.statusCombo.select(ANY_FILTER_INDEX);
		
		statusCombo.setData(AdminUsersMessages.get().SearchView_Filter_Item_Active, UserStatus.ENABLED);
		statusCombo.setData(AdminUsersMessages.get().SearchView_Filter_Item_Locked, UserStatus.LOCKED);
		statusCombo.setData(AdminUsersMessages.get().SearchView_Filter_Item_Disabled, UserStatus.DISABLED);
	}

	private void populateUserRoleCombo() {
		final UserRoleService userRoleService = (UserRoleService) getBean(ContextIdNames.USER_ROLE_SERVICE);
		
		this.userRolesCombo.add(AdminUsersMessages.get().SearchView_Filter_Item_Any, ANY_FILTER_INDEX);
		this.userRolesCombo.select(ANY_FILTER_INDEX);
		for (final UserRole userRole : userRoleService.list()) {			
			String userRoleName = getUserRoleName(userRole);
			this.userRolesCombo.add(userRoleName);
			this.userRolesCombo.setData(userRoleName, userRole);
		}
	}

	private void populateCatalogCombo() {
		final CatalogService catalogService = (CatalogService) getBean(ContextIdNames.CATALOG_SERVICE);

		this.catalogCombo.add(AdminUsersMessages.get().SearchView_Filter_Item_Any, ANY_FILTER_INDEX);
		this.catalogCombo.select(ANY_FILTER_INDEX);
		for (final Catalog catalog : catalogService.findAllCatalogs()) {
			this.catalogCombo.add(catalog.getName());
			this.catalogCombo.setData(catalog.getName(), catalog);
		}
	}

	private void populateStoreCombo() {
		final StoreService storeService = (StoreService) getBean(ContextIdNames.STORE_SERVICE);

		this.storeCombo.add(AdminUsersMessages.get().SearchView_Filter_Item_Any, ANY_FILTER_INDEX);
		this.storeCombo.select(ANY_FILTER_INDEX);
		for (final Store store : storeService.findAllStores()) {
			this.storeCombo.add(store.getName());
			this.storeCombo.setData(store.getName(), store);
		}
	}
	
	private void populateSortingControl() {
		this.sortingControl.addSortTypeItem(AdminUsersMessages.get().SearchView_Sorting_User_Name, StandardSortBy.NAME, true);
		this.sortingControl.addSortTypeItem(AdminUsersMessages.get().SearchView_Sorting_First_Name, StandardSortBy.FIRST_NAME);
		this.sortingControl.addSortTypeItem(AdminUsersMessages.get().SearchView_Sorting_Last_Name, StandardSortBy.LAST_NAME);
		this.sortingControl.addSortTypeItem(AdminUsersMessages.get().SearchView_Sorting_Email, StandardSortBy.EMAIL);
		this.sortingControl.addSortTypeItem(AdminUsersMessages.get().SearchView_Sorting_Status, StandardSortBy.STATUS);
	}
	
	private void bindControls() {
		bind(userNameText, getModel(), "userName", null, null); //$NON-NLS-1$
		bind(userLastNameText, getModel(), "lastName", null, null); //$NON-NLS-1$
		bind(userFirstNameText, getModel(), "firstName", null, null); //$NON-NLS-1$
		bind(userEmailText, getModel(), "email", null, null); //$NON-NLS-1$
	
		bindCombo(catalogCombo, new BindComboHelper<Catalog>() {
			public void handleSelectionAny() {
				getModel().setCatalogCode(null);				
			}			
			public void handleSelection(final Catalog catalog) {
				getModel().setCatalogCode(catalog.getCode());
			}
		});
		
		bindCombo(storeCombo, new BindComboHelper<Store>() {
			public void handleSelectionAny() {
				getModel().setStoreCode(null);
			}			
			public void handleSelection(final Store store) {
				getModel().setStoreCode(store.getCode());
			}
		});
		
		bindCombo(statusCombo, new BindComboHelper<UserStatus>() {
			public void handleSelectionAny() {
				getModel().setUserStatus(null);
			}
			public void handleSelection(final UserStatus userStatus) {
				getModel().setUserStatus(userStatus);
			}
		});
		
		bindCombo(userRolesCombo, new BindComboHelper<UserRole>() {
			public void handleSelectionAny() {
				getModel().setUserRoleName(null);
			}
			public void handleSelection(final UserRole userRole) {
				getModel().setUserRoleName(userRole.getName());
			}
		});
	}
	
	private void clearFiledsToDefault() {
		this.userNameText.setText(EMPTY_STRING);
		this.userLastNameText.setText(EMPTY_STRING);
		this.userFirstNameText.setText(EMPTY_STRING);
		this.userEmailText.setText(EMPTY_STRING);
		
		this.statusCombo.select(ANY_FILTER_INDEX);
		this.userRolesCombo.select(ANY_FILTER_INDEX);
		this.catalogCombo.select(ANY_FILTER_INDEX);
		this.storeCombo.select(ANY_FILTER_INDEX);
		this.sortingControl.clear();
		
		this.getModel().clear();
	}

	private void processSearch() {
		this.showView();
		this.checkBlank();
		sortingControl.updateSearchCriteriaValues();
		UserSearchCriteria searchCriteria = duplicateModel();
		this.searchJob.setSearchCriteria(updateLocale(searchCriteria));
		this.searchJob.executeSearch(null);
	}

	private UserSearchCriteria updateLocale(final UserSearchCriteria searchCriteria) {
		searchCriteria.setLocale(CorePlugin.getDefault().getDefaultLocale());
		return searchCriteria;
	}

	private UserSearchCriteria duplicateModel() {
		try {
			return (UserSearchCriteria) getModel().clone();
		} catch (CloneNotSupportedException e) {
			return getModel();
		}
	}
	
	private void showView() {
		final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		final UserListView openedUserListView = (UserListView) workbenchPage.findView(UserListView.VIEW_ID);
		if (openedUserListView == null) {
			try {
				getSite().getWorkbenchWindow().getActivePage().showView(UserListView.VIEW_ID);
			} catch (final PartInitException e) {
				// Log the error and throw an unchecked exception
				LOG.error(e.getStackTrace());
				throw new EpUiException("Fail to reopen users list view.", e); //$NON-NLS-1$
			}
		} else {
			workbenchPage.activate(openedUserListView);
		}
	}

	private void checkBlank() {
		if (StringUtils.isBlank(userNameText.getText())
				&& StringUtils.isBlank(userLastNameText.getText())
				&& StringUtils.isBlank(userFirstNameText.getText())
				&& StringUtils.isBlank(userEmailText.getText())
				&& statusCombo.getSelectionIndex() == ANY_FILTER_INDEX
				&& userRolesCombo.getSelectionIndex() == ANY_FILTER_INDEX
				&& catalogCombo.getSelectionIndex() == ANY_FILTER_INDEX
				&& storeCombo.getSelectionIndex() == ANY_FILTER_INDEX) {
			this.getModel().setMatchAll(true);
		} else {
			this.getModel().setMatchAll(false);
		}
	}
	
	@Override
	public void setFocus() {
		if (this.userNameText != null) {
			this.userNameText.setFocus();
		}
	}

	@Override
	protected UserSearchCriteria getModel() {
		if (this.userSearchCriteria == null) {
			this.userSearchCriteria = (UserSearchCriteria) getBean(ContextIdNames.USER_SEARCH_CRITERIA);
		}
		return this.userSearchCriteria;		
	}
	
	/**
	 * Helper to bind filer combo.

	 * @param <T> the combo type
	 */
	private interface BindComboHelper<T> {
		void handleSelectionAny();
		void handleSelection(T dataType);
	}

	private <T> void bindCombo(final CCombo combo, final BindComboHelper<T> bindHelper) {
		this.bind(combo, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				if (combo.getSelectionIndex() == ANY_FILTER_INDEX) {
					bindHelper.handleSelectionAny();
				} else {
					bindHelper.handleSelection((T) combo.getData(combo.getText()));
				}
				return Status.OK_STATUS;
			}			
		});
	}
	
	private static Object getBean(final String beanName) {
		return ServiceLocator.getService(beanName);
	}
	
	private static String getUserRoleName(final UserRole role) {
		String result = role.getName();
		if (result.equals(UserRole.CMUSER)) {
			return AdminUsersMessages.get().CMUser;
		} else if (result.equals(UserRole.SUPERUSER)) {
			return AdminUsersMessages.get().SuperUser;
		} else if (result.equals(UserRole.WSUSER)) {
			return AdminUsersMessages.get().WSUser;
		}
		return result;
	}

	private static CCombo createComboField(final String label, final IEpLayoutComposite layoutComposite, final IEpLayoutData layoutData) {
		layoutComposite.addLabelBold(label, null);
		CCombo ccombo = layoutComposite.addComboBox(EpState.EDITABLE, layoutData);
		ccombo.setEnabled(true);
		GridLayout comboLayout = new GridLayout();
		comboLayout.horizontalSpacing = COMBO_HORIZONTAL_SPACING;
		ccombo.setLayout(comboLayout);
		return ccombo;
	}

	private static Text createTextField(final String label,
			final IEpLayoutComposite layoutComposite,
			final IEpLayoutData layoutData,
			final SelectionListener selectionListener) {
		layoutComposite.addLabelBold(label, null);
		Text text = layoutComposite.addTextField(EpState.EDITABLE, layoutData);
		text.setTextLimit(NORMAL_TEXT_LENGTH);
		text.addSelectionListener(selectionListener);
		return text;
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
