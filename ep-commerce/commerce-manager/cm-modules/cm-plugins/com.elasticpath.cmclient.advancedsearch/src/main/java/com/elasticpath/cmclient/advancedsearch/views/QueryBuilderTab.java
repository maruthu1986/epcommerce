/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.views;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import com.elasticpath.cmclient.advancedsearch.AdvancedSearchImageRegistry;
import com.elasticpath.cmclient.advancedsearch.AdvancedSearchMessages;
import com.elasticpath.cmclient.advancedsearch.AdvancedSearchPermissions;
import com.elasticpath.cmclient.advancedsearch.dialogs.SaveQueryDialog;
import com.elasticpath.cmclient.advancedsearch.helpers.QueryBuilder;
import com.elasticpath.cmclient.advancedsearch.service.EPQLSearchService;
import com.elasticpath.cmclient.advancedsearch.service.impl.EPQLSearchServiceImpl;
import com.elasticpath.cmclient.advancedsearch.service.impl.ValidationStatus;
import com.elasticpath.cmclient.core.CmClientResources;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.core.util.ServiceUtil;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.advancedsearch.AdvancedQueryType;
import com.elasticpath.domain.advancedsearch.AdvancedSearchQuery;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.persistence.dao.AdvancedSearchQueryDao;

/**
 * Provides methods for building EqQL search queries.
 */
public class QueryBuilderTab implements SelectionListener, ModifyListener {

	private static final int CONTENT_COMPOSITE_COLUMNS = 3;

	private static final String EMPTY_STRING = "";

	private static final int BUTTONS_NUMBER = 3;

	private final AbstractAdvancedSearchView advancedSearchView;

	private Label queryIdLabel;

	private Label queryNameLabel;

	private Text queryDescriptionText;

	private Label queryVisibilityLabel;

	private Label queryOwnerLabel;

	private Button runQueryButton;

	private Button saveQueryButton;

	private Button saveAsQueryButton;

	private Button validateQueryButton;

	private Text syntxErrorMessage;

	private Label syntxErrorImage;

	private Text queryText;

	private AdvancedSearchQuery searchQuery;

	private final EPQLSearchService searchService;

	private static final int WANTED_QUERY_TEXT_HEIGHT = 150;

	private static final int DESCRIPTION_TEXT_HEIGHT = 50;

	private static final int ERROR_MESSAGE_TEXT_HEIGHT = 50;

	private IEpLayoutComposite detailsComposite;

	private final AdvancedSearchQueryDao searchQueryDao;

	private final DataBindingContext bindingContext;

	private static final String HELP_QUERY_BUILDER_URL = "help/html/AppendixD.html";

	/**
	 * Constructor.
	 *
	 * @param tabFolder parent's advanced search view tab folder
	 * @param tabIndex index of this tab into tab folder
	 * @param advancedSearchView parent advanced search view
	 */
	public QueryBuilderTab(final IEpTabFolder tabFolder, final int tabIndex, final AbstractAdvancedSearchView advancedSearchView) {
		this.advancedSearchView = advancedSearchView;
		searchService = new EPQLSearchServiceImpl();
		final Image buildQueryImage = AdvancedSearchImageRegistry.getImage(AdvancedSearchImageRegistry.QUERY_BUILDER);
		final IEpLayoutComposite queryBuilderTab = tabFolder.addTabItem(AdvancedSearchMessages.get().QueryBuilder, buildQueryImage,
				tabIndex, 1, false, true);
		searchQueryDao = ServiceLocator.getService(ContextIdNames.ADVANCED_SEARCH_QUERY_DAO);
		bindingContext = new DataBindingContext();
		createControls(queryBuilderTab);
	}

	private void createControls(final IEpLayoutComposite parentComposite) {
		detailsComposite = createQueryDescribingControls(parentComposite);

		createContentComposite(parentComposite);

		createValidationComposite(parentComposite);

		parentComposite.addHorizontalSeparator(parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		createButtonsComposite(parentComposite);
	}

	/**
	 * Create controls describing saved EpQL query.
	 */
	private IEpLayoutComposite createQueryDescribingControls(final IEpLayoutComposite parentComposite) {
		IEpLayoutComposite detailsComposite = parentComposite.addGridLayoutComposite(2, false,
				parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		final IEpLayoutData boldLabelsLayoutData = detailsComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.BEGINNING, false, false);

		detailsComposite.addLabelBold(AdvancedSearchMessages.get().QueryID, boldLabelsLayoutData);
		queryIdLabel = detailsComposite.addLabel(EMPTY_STRING, null);

		detailsComposite.addLabelBold(AdvancedSearchMessages.get().Name, boldLabelsLayoutData);
		queryNameLabel = detailsComposite.addLabel(EMPTY_STRING, null);

		detailsComposite.addLabelBold(AdvancedSearchMessages.get().Description, boldLabelsLayoutData);
		queryDescriptionText = detailsComposite.addTextArea(true, false, EpState.READ_ONLY, null);
		final GridData descriptionGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.FILL_HORIZONTAL);
		descriptionGridData.heightHint = DESCRIPTION_TEXT_HEIGHT;
		queryDescriptionText.setText(EMPTY_STRING);
		queryDescriptionText.setLayoutData(descriptionGridData);

		detailsComposite.addLabelBold(AdvancedSearchMessages.get().Visibility, boldLabelsLayoutData);
		queryVisibilityLabel = detailsComposite.addLabel(EMPTY_STRING, null);

		detailsComposite.addLabelBold(AdvancedSearchMessages.get().Owner, boldLabelsLayoutData);
		queryOwnerLabel = detailsComposite.addLabel(EMPTY_STRING, null);

		return detailsComposite;
	}

	private void createContentComposite(final IEpLayoutComposite parentComposite) {
		IEpLayoutComposite contentComposite = parentComposite.addGridLayoutComposite(1, false,
				parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1));

		IEpLayoutComposite queryTypeComposite = contentComposite.addGridLayoutComposite(CONTENT_COMPOSITE_COLUMNS, false,
				parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1));

		queryTypeComposite.addLabelBoldRequired(AdvancedSearchMessages.get().QueryType, EpState.EDITABLE,
				queryTypeComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER, false, false));

		final String message = AdvancedSearchMessages.get().getMessage(AdvancedQueryType.PRODUCT.getPropertyKey());
		queryTypeComposite.addLabel(message, queryTypeComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER, false, false));

		queryTypeComposite.addHyperLinkText(AdvancedSearchMessages.get().Help, EpState.EDITABLE,
				queryTypeComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER, false, false)).addHyperlinkListener(
				new HyperlinkAdapter() {
					@Override
					public void linkActivated(final HyperlinkEvent hyperlinkEvent) {
						ServiceUtil.getUrlLauncherService().openURL(HELP_QUERY_BUILDER_URL);
					}
				});

		//next line
		contentComposite.addLabelBoldRequired(AdvancedSearchMessages.get().Query, EpState.EDITABLE,
				contentComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER, false, false));

		final GridData queryTextGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		queryTextGridData.heightHint = WANTED_QUERY_TEXT_HEIGHT;
		queryTextGridData.horizontalSpan = CONTENT_COMPOSITE_COLUMNS;
		queryText = contentComposite.addTextArea(true, false, EpState.EDITABLE, null);
		queryText.setLayoutData(queryTextGridData);
		queryText.addModifyListener(this);
	}

	private void createValidationComposite(final IEpLayoutComposite parentComposite) {
		IEpLayoutComposite validationComposite = parentComposite.addGridLayoutComposite(2, false,
				parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1));

		final Image syntxErrorIcon = AdvancedSearchImageRegistry.getImage(AdvancedSearchImageRegistry.IMAGE_QUERY_ERROR_SMALL);

		syntxErrorImage = validationComposite.addImage(syntxErrorIcon,
				validationComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, false, false));
		syntxErrorImage.setVisible(false);

		final GridData errorMessageGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		errorMessageGridData.heightHint = ERROR_MESSAGE_TEXT_HEIGHT;

		syntxErrorMessage = validationComposite.addTextArea(true, false, EpState.READ_ONLY, null);
		syntxErrorMessage.setForeground(CmClientResources.getColor(CmClientResources.COLOR_RED));
		syntxErrorMessage.setLayoutData(errorMessageGridData);
		syntxErrorMessage.setVisible(false);

		final Image refreshQueryImage = AdvancedSearchImageRegistry.getImage(AdvancedSearchImageRegistry.IMAGE_QUERY_VALIDATE);
		validateQueryButton = validationComposite.addPushButton(AdvancedSearchMessages.get().QueryBuilderTab_ValidateQuery, refreshQueryImage,
				EpState.EDITABLE, validationComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false, 2, 1));
		validateQueryButton.addSelectionListener(this);
		validateQueryButton.setEnabled(false);
	}

	private void createButtonsComposite(final IEpLayoutComposite parentComposite) {
		IEpLayoutComposite buttonsComposite = parentComposite.addGridLayoutComposite(BUTTONS_NUMBER, false, parentComposite.createLayoutData(
				IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING));

		saveQueryButton = buttonsComposite.addPushButton(AdvancedSearchMessages.get().Save, CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_SAVE),
				EpState.EDITABLE, buttonsComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING));
		saveQueryButton.setEnabled(false);
		saveQueryButton.addSelectionListener(this);

		saveAsQueryButton = buttonsComposite.addPushButton(AdvancedSearchMessages.get().SaveAs, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_SAVE_AS), EpState.EDITABLE, buttonsComposite.createLayoutData(IEpLayoutData.BEGINNING,
				IEpLayoutData.BEGINNING));
		saveAsQueryButton.setEnabled(false);
		saveAsQueryButton.addSelectionListener(this);

		runQueryButton = buttonsComposite.addPushButton(AdvancedSearchMessages.get().RunQuery,
				AdvancedSearchImageRegistry.getImage(AdvancedSearchImageRegistry.IMAGE_QUERY_RUN), EpState.EDITABLE,
				buttonsComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING));
		runQueryButton.setEnabled(false);
		runQueryButton.addSelectionListener(this);
	}

	/**
	 * Prepares query builder tab for open action.
	 *
	 * @param searchQuery the search query to open
	 */
	public void prepareForOpenAction(final AdvancedSearchQuery searchQuery) {
		updateQueryTab(searchQuery, true, false);
		validateQueryAction();
	}

	/**
	 * Prepares query builder tab for edit action.
	 *
	 * @param searchQuery the search query to edit
	 */
	public void prepareForEditAction(final AdvancedSearchQuery searchQuery) {
		updateQueryTab(searchQuery, true, true);
		validateQueryAction();
	}

	/**
	 * Prepares query builder tab for creation.
	 *
	 * @param searchQuery the new search query
	 */
	public void prepareForCreateAction(final AdvancedSearchQuery searchQuery) {
		updateQueryTab(searchQuery, false, false);
		queryText.setText(EMPTY_STRING);
		syntxErrorImage.setVisible(false);
		syntxErrorMessage.setVisible(false);
	}

	private void updateQueryTab(final AdvancedSearchQuery searchQuery, final boolean isDetailsCompositeVisible, final boolean isSaveButtonVisible) {
		this.searchQuery = searchQuery;
		detailsComposite.getSwtComposite().setVisible(isDetailsCompositeVisible);
		saveQueryButton.setVisible(isSaveButtonVisible);

		bindAndPopulateControls();
	}

	/**
	 * Perform the bindings between the controls and the domain model.
	 */
	private void bindControls() {
		searchQuery.setQueryType(AdvancedQueryType.PRODUCT);
		setButtonsStatus(false);
	}

	private void populateControls() {
		if (searchQuery.isPersisted()) {
			queryIdLabel.setText(String.valueOf(searchQuery.getQueryId()));
			queryNameLabel.setText(searchQuery.getName());
			String description = searchQuery.getDescription();
			if (description != null) {
				queryDescriptionText.setText(description);
			}
			queryVisibilityLabel.setText(AdvancedSearchMessages.get().getMessage(searchQuery.getQueryVisibility().getPropertyKey()));
			final CmUser owner = searchQuery.getOwner();
			queryOwnerLabel.setText(
				NLS.bind(AdvancedSearchMessages.get().UserName_Format,
				new Object[]{owner.getFirstName(), owner.getLastName()}));
			queryText.setText(QueryBuilder.unbuildQuery(searchQuery.getQueryContent()).getQueryPart());
		}
	}

	private void bindAndPopulateControls() {
		bindingContext.dispose();
		populateControls();
		bindControls();
		bindingContext.updateModels();
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// do nothing
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() == runQueryButton) {
			runQueryAction();
		} else if (event.getSource() == saveQueryButton) {
			saveQueryAction();
		} else if (event.getSource() == saveAsQueryButton) {
			saveAsQueryAction();
		} else if (event.getSource() == validateQueryButton) {
			validateQueryAction();
		}
	}

	private void validateQueryAction() {
		ValidationStatus validationStatus = searchService.validate(getQuery());
		syntxErrorMessage.setText(validationStatus.getStatusMessage());
		setButtonsStatus(validationStatus.isValid());
		if (validationStatus.isValid()) {
			syntxErrorImage.setImage(AdvancedSearchImageRegistry.getImage(AdvancedSearchImageRegistry.IMAGE_CHECKMARK));
			syntxErrorMessage.setForeground(CmClientResources.getColor(CmClientResources.COLOR_GREEN));
			searchQuery.setQueryContent(getQuery());
		} else {
			syntxErrorImage.setImage(AdvancedSearchImageRegistry.getImage(AdvancedSearchImageRegistry.IMAGE_QUERY_ERROR_SMALL));
			syntxErrorMessage.setForeground(CmClientResources.getColor(CmClientResources.COLOR_RED));
		}
		syntxErrorImage.setVisible(true);
		syntxErrorMessage.setVisible(true);
	}

	private void saveQueryAction() {
		if (saveAction(searchQuery, false)) {
			populateControls();
			validateQueryAction();
		}
	}

	private void saveAsQueryAction() {
		AdvancedSearchQuery advancedSearchQuery = ServiceLocator.getService(ContextIdNames.ADVANCED_SEARCH_QUERY);
		advancedSearchQuery.populateFrom(searchQuery);
		advancedSearchQuery.setName(EMPTY_STRING);
		advancedSearchQuery.setOwner(LoginManager.getCmUser());
		advancedSearchQuery.setDescription(EMPTY_STRING);
		saveAction(advancedSearchQuery, true);
	}

	private boolean saveAction(final AdvancedSearchQuery advancedSearchQuery, final boolean isSaveAs) {
		boolean result = SaveQueryDialog.openSaveDialog(advancedSearchView.getSite().getShell(), advancedSearchQuery, isSaveAs);
		if (result) {
			searchQueryDao.saveOrUpdate(advancedSearchQuery);
			advancedSearchView.refreshSavedQueriesTab();
		}
		return result;
	}

	private void runQueryAction() {
		advancedSearchView.executeSearch(searchQuery);
	}

	private void setButtonsStatus(final boolean isEnabled) {
		runQueryButton.setEnabled(isEnabled);
		saveQueryButton.setEnabled(isEnabled);
		saveAsQueryButton.setEnabled(isEnabled && isAuthorized());
	}

	private String getQuery() {
		return QueryBuilder.buildQuery(AdvancedQueryType.PRODUCT, queryText.getText());
	}

	@Override
	public void modifyText(final ModifyEvent event) {
		validateQueryButton.setEnabled(queryText.getText().trim().length() != 0);

		setButtonsStatus(false);
		syntxErrorImage.setVisible(false);
		syntxErrorMessage.setVisible(false);
	}
	
	/**
	 * Gets search query in use.
	 * 
	 * @return the searchQuery
	 */
	public AdvancedSearchQuery getSearchQuery() {
		return searchQuery;
	}
	
	private boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(AdvancedSearchPermissions.CREATE_QUERIES)
				|| AuthorizationService.getInstance().isAuthorizedWithPermission(AdvancedSearchPermissions.MANAGE_QUERIES);
	}
}
