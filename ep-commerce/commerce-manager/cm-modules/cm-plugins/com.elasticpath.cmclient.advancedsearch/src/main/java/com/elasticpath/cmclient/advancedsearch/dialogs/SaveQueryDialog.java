/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.dialogs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.advancedsearch.AdvancedSearchMessages;
import com.elasticpath.cmclient.advancedsearch.AdvancedSearchPlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.advancedsearch.AdvancedSearchQuery;
import com.elasticpath.domain.advancedsearch.QueryVisibility;
import com.elasticpath.persistence.dao.AdvancedSearchQueryDao;

/**
 * Save dialog for advanced search query.
 */
public class SaveQueryDialog extends AbstractEpDialog {

	private static final int DESCRIPTION_HEIGHT = 80;

	private static final int NAME_TEXT_LIMIT = 255;

	private final String windowTitle;

	private final String title;

	private final AdvancedSearchQuery query;

	private final DataBindingContext dataBindingCtx;

	private final Map<QueryVisibility, Button> visibilityButtons;

	private Text queryNameText;

	private Text queryDescriptionText;
	
	private final boolean isSaveAs;

	private final AdvancedSearchQueryDao searchQueryDao =
			ServiceLocator.getService(ContextIdNames.ADVANCED_SEARCH_QUERY_DAO);
	
	/**
	 * Constructor.
	 * 
	 * @param parentShell parent shell.
	 * @param query the query entity to save or edit.
	 * @param windowTitle window title of dialog
	 * @param title title of dialog
	 * @param isSaveAs true if the dialog is to "Save As" and not just "Save"
	 */
	public SaveQueryDialog(final Shell parentShell, final AdvancedSearchQuery query, final String windowTitle, final String title,
						   final boolean isSaveAs) {
		super(parentShell, 2, false);
		this.windowTitle = windowTitle;
		this.title = title;
		this.query = query;
		this.isSaveAs = isSaveAs;
		visibilityButtons = new HashMap<>();
		dataBindingCtx = new DataBindingContext();
	}

	/**
	 * Convenience method to open a save as query dialog.
	 * 
	 * @param parentShell the parent Shell
	 * @param query the query to save
	 * @param isSaveAs true if it should be save as dialog and false if it should be save dialog
	 * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
	 */
	public static boolean openSaveDialog(final Shell parentShell, final AdvancedSearchQuery query, final boolean isSaveAs) {
		SaveQueryDialog dialog;

		if (isSaveAs) {
			dialog = new SaveQueryDialog(parentShell, query, AdvancedSearchMessages.get().SaveAs, AdvancedSearchMessages.get().SaveQueryAs, isSaveAs);
		} else {
			dialog = new SaveQueryDialog(parentShell, query, AdvancedSearchMessages.get().Save, AdvancedSearchMessages.get().SaveQuery, isSaveAs);
		}

		return dialog.open() == 0;
	}

	@Override
	protected void bindControls() {
		final boolean hideDecorationOnFirstValidation = true;

		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();
		binder.bind(dataBindingCtx, queryNameText, query,
				"name", //$NON-NLS-1$
				new CompoundValidator(new IValidator[] { EpValidatorFactory.REQUIRED, EpValidatorFactory.NO_LEADING_TRAILING_SPACES }), null,
				hideDecorationOnFirstValidation);

		binder.bind(dataBindingCtx, queryDescriptionText, query, "description"); //$NON-NLS-1$

		EpDialogSupport.create(this, dataBindingCtx);
	}

	@Override
	protected void okPressed() {
		for (Entry<QueryVisibility, Button> entry : visibilityButtons.entrySet()) {
			if (entry.getValue().getSelection()) {
				query.setQueryVisibility(entry.getKey());
				break;
			}
		}
		
		// duplicate query names should be limited
		if (isSaveAs && queryNameExists(queryNameText.getText())) {
			setErrorMessage(AdvancedSearchMessages.get().Name_Exists);
			
			// interrupt the ok button press event
			return;
		}
		
		super.okPressed();
	}

	/**
	 * Checks if the given query name is already used. 
	 * 
	 * @param queryName The new query name.
	 * @return True if the given query name exists, false otherwise.
	 */
	protected boolean queryNameExists(final String queryName) {
		boolean withDetails = false;
		List<AdvancedSearchQuery> existingQueries = searchQueryDao.findByName(queryName, withDetails);
		return existingQueries != null && !existingQueries.isEmpty();
	}
	
	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		dialogComposite.addLabelBoldRequired(AdvancedSearchMessages.get().Name, EpState.EDITABLE, labelData);
		queryNameText = dialogComposite.addTextField(EpState.EDITABLE, fieldData);
		queryNameText.setTextLimit(NAME_TEXT_LIMIT);

		dialogComposite.addLabelBold(AdvancedSearchMessages.get().Description, labelData);
		queryDescriptionText = dialogComposite.addTextArea(EpState.EDITABLE, fieldData);
		final GridData queryTextGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.FILL_HORIZONTAL);
		queryTextGridData.heightHint = DESCRIPTION_HEIGHT;
		queryDescriptionText.setLayoutData(queryTextGridData);

		dialogComposite.addLabelBoldRequired(AdvancedSearchMessages.get().Visibility, EpState.EDITABLE, labelData);

		IEpLayoutComposite visibilityComposite = dialogComposite.addGridLayoutComposite(1, false, fieldData);
		visibilityButtons.put(QueryVisibility.PRIVATE, visibilityComposite.addRadioButton(AdvancedSearchMessages.get().PrivateForCurrentUser, null,
				EpState.EDITABLE, null));
		visibilityButtons.put(QueryVisibility.PUBLIC, visibilityComposite.addRadioButton(AdvancedSearchMessages.get().PublicForAllUsers, null,
				EpState.EDITABLE, null));
	}

	/**
	 * Gets the query.
	 * 
	 * @return the warehouse
	 */
	public AdvancedSearchQuery getQuery() {
		return query;
	}

	@Override
	protected String getInitialMessage() {
		return null;
	}

	@Override
	protected String getTitle() {
		return title;
	}

	@Override
	protected Image getWindowImage() {
		return null;
	}

	@Override
	protected String getWindowTitle() {
		return windowTitle;
	}

	@Override
	protected String getPluginId() {
		return AdvancedSearchPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return null;
	}

	@Override
	protected void populateControls() {
		if (query.isPersisted()) {
			queryNameText.setText(query.getName());
			String description = query.getDescription();
			if (description != null) {
				queryDescriptionText.setText(description);
			}
			visibilityButtons.get(query.getQueryVisibility()).setSelection(true);
		} else {
			visibilityButtons.get(QueryVisibility.PRIVATE).setSelection(true);
		}
	}

}
