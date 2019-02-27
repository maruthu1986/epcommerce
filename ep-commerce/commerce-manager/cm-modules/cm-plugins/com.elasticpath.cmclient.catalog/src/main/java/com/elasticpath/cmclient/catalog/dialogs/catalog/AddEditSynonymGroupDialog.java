/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.dialogs.catalog;

import java.util.Locale;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.search.Synonym;
import com.elasticpath.domain.search.SynonymGroup;
import com.elasticpath.service.search.SynonymGroupService;

/**
 * Dialog box to add/edit {@link SynonymGroup}s.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class AddEditSynonymGroupDialog extends AbstractEpDialog implements ModifyListener {

	private static final int SYNONYMS_HEIGHT = 150;

	private final DataBindingContext bindingContext = new DataBindingContext();

	private String originalConceptTerm;

	private final boolean editing;

	private final SynonymGroup model;

	private Text conceptTerm;

	private Text synonyms;

	/**
	 * Constructor for adding/creating a synonym group.
	 *
	 * @param parentShell the parent shell
	 * @param catalog     the catalog to create the synonym group for
	 * @param locale      the locale to create the synonym group for
	 */
	public AddEditSynonymGroupDialog(final Shell parentShell, final Catalog catalog, final Locale locale) {
		super(parentShell, 2, false);
		editing = false;

		model = ServiceLocator.getService(ContextIdNames.SYNONYM_GROUP);
		Assert.isNotNull(catalog);
		Assert.isNotNull(locale);
		model.setCatalog(catalog);
		model.setLocale(locale);
	}

	/**
	 * Constructor for editing a synonym group.
	 *
	 * @param parentShell  the parent shell
	 * @param synonymGroup the synonym group to edit
	 */
	public AddEditSynonymGroupDialog(final Shell parentShell, final SynonymGroup synonymGroup) {
		super(parentShell, 2, false);
		editing = true;

		Assert.isNotNull(synonymGroup);
		model = synonymGroup;
		originalConceptTerm = model.getConceptTerm();
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite parent) {
		final IEpLayoutData labelData = parent.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER);
		final IEpLayoutData groupData = parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 2, 1);
		final IEpLayoutData textAreaData = parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);

		parent.addLabelBoldRequired(CatalogMessages.get().AddEditSynonymGroupDialog_ConceptTerm, EpState.EDITABLE, labelData);
		conceptTerm = parent.addTextField(EpState.EDITABLE, fieldData);
		conceptTerm.setTextLimit(GlobalConstants.SHORT_TEXT_MAX_LENGTH);
		conceptTerm.addModifyListener(this);

		IEpLayoutComposite synonymsGroup = parent.addGroup(CatalogMessages.get().AddEditSynonymGroupDialog_SynonymsGroupLabel, 1,
				true, groupData);
		synonyms = synonymsGroup.addTextArea(true, true, EpState.EDITABLE, textAreaData);
		((GridData) synonyms.getLayoutData()).heightHint = SYNONYMS_HEIGHT;
		synonyms.addModifyListener(this);
	}

	@Override
	protected void bindControls() {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		bindingProvider.bind(bindingContext, conceptTerm, EpValidatorFactory.STRING_255_REQUIRED, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				getModel().setConceptTerm(conceptTerm.getText());
				return Status.OK_STATUS;
			}
		}, true);
		// this seems to only set the dialog complete 90% of the time
//		bindingProvider.bind(bindingContext, conceptTerm, getModel(),
//				"conceptTerm", EpValidatorFactory.STRING_255_REQUIRED, null, true); //$NON-NLS-1$

		// don't bind the text area, parse on OK button pressed

		EpDialogSupport.create(this, bindingContext);
	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	protected void populateControls() {
		if (getModel() == null) {
			return;
		}

		if (getModel().getConceptTerm() != null) {
			conceptTerm.setText(getModel().getConceptTerm());
		}

		final StringBuilder stringBuilder = new StringBuilder();
		for (Synonym synonym : getModel().getSynonyms()) {
			if (synonym.getSynonym() != null) {
				stringBuilder.append(synonym.getSynonym()).append('\n');
			}
		}
		if (getModel().getSynonyms().size() > 1) {
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		}
		synonyms.setText(stringBuilder.toString());
	}

	@Override
	protected String getInitialMessage() {
		return ""; //$NON-NLS-1$
	}

	@Override
	protected String getTitle() {
		if (editing) {
			return CatalogMessages.get().AddEditSynonymGroupDialog_Title_Edit;
		}
		return CatalogMessages.get().AddEditSynonymGroupDialog_Title_Add;
	}

	@Override
	protected Image getWindowImage() {
		return null;
	}

	@Override
	protected String getWindowTitle() {
		if (editing) {
			return CatalogMessages.get().AddEditSynonymGroupDialog_WindowTitle_Edit;
		}
		return CatalogMessages.get().AddEditSynonymGroupDialog_WindowTitle_Add;
	}

	/**
	 * Returns the {@link SynonymGroup} this dialog is editing/creating.
	 *
	 * @return the {@link SynonymGroup} this dialog is editing/creating
	 */
	public SynonymGroup getModel() {
		return model;
	}

	@Override
	protected void okPressed() {
		final String[] splitSynonyms = synonyms.getText().split("\n|,"); //$NON-NLS-1$
		if (splitSynonyms == null || splitSynonyms.length <= 0 || splitSynonyms[0].length() == 0) {
			setErrorMessage(CatalogMessages.get().AddEditSynonymGroupDialog_Error_AtLeastOneSynonym);
			return;
		}

		boolean allSynonymsSameAsConceptTerm = true;
		for (int i = 0, line = i + 1; i < splitSynonyms.length; ++i) {
			splitSynonyms[i] = splitSynonyms[i].trim();
			allSynonymsSameAsConceptTerm &= splitSynonyms[i].equals(getModel().getConceptTerm());
			if (splitSynonyms[i].length() >= GlobalConstants.SHORT_TEXT_MAX_LENGTH) {
				setErrorMessage(
					NLS.bind(CatalogMessages.get().AddEditSynonymGroupDialog_Error_LineLength,
					line, GlobalConstants.SHORT_TEXT_MAX_LENGTH));
				return;
			}
			++line;
		}

		if (allSynonymsSameAsConceptTerm) {
			setErrorMessage(CatalogMessages.get().AddEditSynonymGroupDialog_Error_SameAsConceptTerm);
			return;
		}

		// check for concept term uniqueness
		if (!editing || !originalConceptTerm.equals(getModel().getConceptTerm())) {
			final SynonymGroupService sgService = ServiceLocator.getService(
					ContextIdNames.SYNONYM_GROUP_SERVICE);
			if (sgService.conceptTermExists(getModel().getConceptTerm(), getModel().getCatalog(), getModel().getLocale())) {
				setErrorMessage(CatalogMessages.get().AddEditSynonymGroupDialog_Error_ConceptTermExists);
				return;
			}
		}

		getModel().setSynonyms(splitSynonyms);
		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		// set concept term back to original
		getModel().setConceptTerm(originalConceptTerm);
		super.cancelPressed();
	}

	@Override
	public void modifyText(final ModifyEvent event) {
		// clear error when anything is modified
		setErrorMessage(null);
	}
}
