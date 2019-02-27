/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.changeset.ChangeSetImageRegistry;
import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.ChangeSetPermissions;
import com.elasticpath.cmclient.changeset.ChangeSetPlugin;
import com.elasticpath.cmclient.changeset.event.ChangeSetEventListener;
import com.elasticpath.cmclient.changeset.event.ChangeSetEventService;
import com.elasticpath.cmclient.changeset.helpers.impl.ChangeSetSearchRequestJob;
import com.elasticpath.cmclient.changeset.perspective.ChangeSetPerspectiveFactory;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.service.changeset.ChangeSetSearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * The change sets main search tab.
 */
public class ChangeSetSearchTab implements SelectionListener {
	
	private static final int ALL_STATES_ID = 0;

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private static final int TEXT_LIMIT = 100;

	private static final Logger LOG = Logger
			.getLogger(ChangeSetSearchTab.class);

	private final SearchView searchView;

	private CCombo changeSetStateCombo;

	private Text changeSetNameText;

	private Text assignedUserText;

	private Button searchButton;

	private Button clearButton;

	private final Map<Integer, String> changeSetStateCodes = new HashMap<>();

	private final ChangeSetEventListener changeSetEventListener = event -> openViewAndShowChangeSets();

	private final ChangeSetSearchRequestJob searchJob = new ChangeSetSearchRequestJob();
	
	/**
	 * Constructs a new change set search tab.
	 * 
	 * @param tabFolder
	 *            the tab folder to use
	 * @param tabIndex
	 *            the tab index to use
	 * @param searchView
	 *            the search view
	 */
	public ChangeSetSearchTab(final IEpTabFolder tabFolder, final int tabIndex,
			final SearchView searchView) {
		// Create the promotions search tab container
		final Image promotionImage = ChangeSetImageRegistry
				.getImage(ChangeSetImageRegistry.CHANGESET);
		final IEpLayoutComposite changeSetTab = tabFolder.addTabItem(
				ChangeSetMessages.get().ChangeSetSearchView_ChangeSetTab,
				promotionImage, tabIndex, 1, false);
		this.searchView = searchView;

		createSearchTabFiltersGroup(changeSetTab);

		populateControls();
		
		ChangeSetEventService.getInstance().registerChangeSetEventListener(changeSetEventListener);
		tabFolder.getSwtTabFolder().addDisposeListener((DisposeListener) event ->
			ChangeSetEventService.getInstance().unregisterChangeSetEventListener(changeSetEventListener));
	}

	private void populateControls() {

		// Retrieve change set states
		changeSetStateCombo.removeAll();

		populateChangeSetStateCodesCombo();
	}

	private void populateChangeSetStateCodesCombo() {
		List<ChangeSetStateCode> stateCodes = ChangeSetStateCode.getEnumList();

		changeSetStateCombo.add(ChangeSetMessages.get().ALL_STATES, ALL_STATES_ID);

		for (int codeIndex = 0; codeIndex < stateCodes.size(); codeIndex++) {

			ChangeSetStateCode changeSetStateCode = stateCodes.get(codeIndex);

			int stateId = codeIndex + 1;

			changeSetStateCodes.put(stateId, changeSetStateCode.getName());
			changeSetStateCombo.add(ChangeSetMessages.get().get()
					.getMessage(changeSetStateCode), stateId);
		}

		changeSetStateCombo.select(ALL_STATES_ID);
	}

	/**
	 * Creates the filters group.
	 */
	private void createSearchTabFiltersGroup(
			final IEpLayoutComposite parentComposite) {

		// Create the filters container
		final IEpLayoutComposite filtersGroup = parentComposite.addGroup(
				ChangeSetMessages.get().ChangeSetSearchView_FiltersGroup, 1, false,
				null);
		final IEpLayoutData comboLayoutData = filtersGroup.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		// Add the change set state filter item
		filtersGroup.addLabelBold(ChangeSetMessages.get().ChangeSetState_ComboLabel,
				null);
		this.changeSetStateCombo = filtersGroup.addComboBox(EpState.EDITABLE,
				comboLayoutData);
		this.changeSetStateCombo.setEnabled(true);

		// Change set name
		// Create the search terms container
		final IEpLayoutData textfieldLayoutData = parentComposite
				.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true,
						false);
		final IEpLayoutComposite searchTermsGroup = parentComposite.addGroup(
				ChangeSetMessages.get().ChangeSetSearchView_SearchTermsGroup, 1,
				false, textfieldLayoutData);

		// Add the promotion name search term
		searchTermsGroup.addLabelBold(
				ChangeSetMessages.get().ChangeSetEditor_ChangeSet_Name, null);
		changeSetNameText = searchTermsGroup.addTextField(EpState.EDITABLE,
				textfieldLayoutData);
		changeSetNameText.setTextLimit(TEXT_LIMIT);
		this.changeSetNameText.addSelectionListener(this);

		// Assigned user
		searchTermsGroup.addLabelBold(
				ChangeSetMessages.get().ChangeSetSearchView_ChangeSet_User, null);
		assignedUserText = searchTermsGroup.addTextField(EpState.EDITABLE,
				textfieldLayoutData);
		assignedUserText.setTextLimit(TEXT_LIMIT);
		this.changeSetNameText.addSelectionListener(this);

		// Create the buttons group container
		final IEpLayoutData buttonLayoutData = parentComposite
				.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true,
						false);
		final IEpLayoutComposite buttonsGroup = parentComposite
				.addGridLayoutComposite(1, false, buttonLayoutData);

		buttonsGroup.addHorizontalSeparator(buttonsGroup.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		// Create the buttons container
		final IEpLayoutData buttonsCompositeData = parentComposite
				.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false,
						false);
		final IEpLayoutComposite buttonsComposite = buttonsGroup
				.addGridLayoutComposite(2, true, buttonsCompositeData);

		// Add the buttons to the buttons container
		searchButton = buttonsComposite.addPushButton(
				ChangeSetMessages.get().ChangeSetSearchView_SearchButton,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_SEARCH_ACTIVE),
				EpState.EDITABLE, null);

		clearButton = buttonsComposite.addPushButton(
				ChangeSetMessages.get().ChangeSetSearchView_ClearButton,
				EpState.EDITABLE, null);

		// Setup this class as the listener for the buttons
		searchButton.addSelectionListener(this);

		clearButton.addSelectionListener(this);
	}

	/**
	 * Opena view nad show the change sets post search.
	 */
	private void openViewAndShowChangeSets() {

		try {
			String perspectiveId = 
				ChangeSetPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective().getId();
			if (ChangeSetPerspectiveFactory.PERSPECTIVE_ID.equals(perspectiveId)) {
				// show search results only for ChangeSet perspective
				searchView.getSite().getPage().showView(ChangeSetsView.ID_CHANGESETS_VIEW);
			}
			// Do the search
			final ChangeSetSearchCriteria searchCriteria = getChangeSetSearchCriteria();

			this.searchJob.setSearchCriteria(searchCriteria);
			this.searchJob.executeSearch(null);

		} catch (final PartInitException partInitException) {
			LOG.error(partInitException.getMessage(), partInitException);
		}
	}

	private ChangeSetSearchCriteria getChangeSetSearchCriteria() {

		ChangeSetSearchCriteria searchCriteria = new ChangeSetSearchCriteria();
		if (StringUtils.isNotBlank(assignedUserText.getText())) {
			searchCriteria.setAssignedUserName(assignedUserText.getText());
		}
		if (StringUtils.isNotBlank(changeSetNameText.getText())) {
			searchCriteria.setChangeSetName(changeSetNameText.getText());
		}

		int selectionIndex = changeSetStateCombo.getSelectionIndex();

		if (selectionIndex != ALL_STATES_ID) {
			String changeSetCodeText = changeSetStateCodes.get(selectionIndex);
			searchCriteria.setChangeSetStateCode(ChangeSetStateCode
					.getState(changeSetCodeText));
		}

		if (userCannotManageChangeSets()) {
			searchCriteria.setUserGuid(LoginManager.getCmUserGuid());
		}
		
		searchCriteria.setSortingType(StandardSortBy.NAME);
		searchCriteria.setSortingOrder(SortOrder.ASCENDING);

		return searchCriteria;
	}

	private boolean userCannotManageChangeSets() {
		return !AuthorizationService.getInstance().isAuthorizedWithPermission(
				ChangeSetPermissions.CHANGE_SET_PERMISSIONS_MANAGE);
	}

	/**
	 * Set focus.
	 */
	public void setFocus() {
		if (changeSetStateCombo != null) {
			changeSetStateCombo.setFocus();
		}
	}

	/**
	 * Called when the default action is selected for the widget. For text
	 * fields hitting ENTER calls this method.
	 * 
	 * @param event
	 *            - selection event
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		if (event.getSource() instanceof Text) {
			openViewAndShowChangeSets();
		}
	}

	/**
	 * Called when a widget is selected.
	 * 
	 * @param event
	 *            - selection event
	 */
	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() == searchButton) {
			openViewAndShowChangeSets();
		} else if (event.getSource() == clearButton) {
			resetSearchFields();
		}
	}

	/**
	 * Reset search fields.
	 */
	private void resetSearchFields() {
		changeSetNameText.setText(EMPTY_STRING);
		assignedUserText.setText(EMPTY_STRING);
		changeSetStateCombo.select(ALL_STATES_ID);
	}

}
