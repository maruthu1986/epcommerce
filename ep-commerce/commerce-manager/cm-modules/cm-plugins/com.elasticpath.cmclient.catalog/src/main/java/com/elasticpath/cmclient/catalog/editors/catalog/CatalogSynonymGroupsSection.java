/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.FormPage;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPermissions;
import com.elasticpath.cmclient.catalog.dialogs.catalog.AddEditSynonymGroupDialog;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.editors.AbstractEpTableSection;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.search.Synonym;
import com.elasticpath.domain.search.SynonymGroup;
import com.elasticpath.service.search.SynonymGroupService;

/**
 * Implementation of the <code>CatalogEditor</code> synonyms page providing search synonyms for a catalog.
 */
public class CatalogSynonymGroupsSection extends AbstractEpTableSection<SynonymGroup> {

	private static final int MAX_SYNONYM_STRING_LENGTH_IN_REMOVE_DIALOG = 200;

	private static final int CONCEPT_TERM_COLUMN_INDEX = 0;

	private static final int SYNONYMS_COLUMN_INDEX = 1;
	private static final String CATALOG_SYNONYM_GROUP_TABLE = "Catalog Synonym Group Table"; //$NON-NLS-1$

	private final SynonymGroupService synonymGroupService;

	private Map<Locale, List<SynonymGroup>> catalogSynonymGroups;

	/**
	 * Default constructor.
	 *
	 * @param formPage the form page
	 * @param editor the editor
	 */
	public CatalogSynonymGroupsSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, CATALOG_SYNONYM_GROUP_TABLE);
		synonymGroupService = ServiceLocator.getService(
				ContextIdNames.SYNONYM_GROUP_SERVICE);
	}

	@Override
	protected String getSectionType() {
		return CatalogMessages.get().CatalogSynonymGroupsSection_ButtonText;
	}

	@Override
	protected void initializeTable(final IEpTableViewer table) {
		final String[] columnNames = { CatalogMessages.get().CatalogSynonymGroupsSection_TableConceptTerm,
				CatalogMessages.get().CatalogSynonymGroupsSection_TableSynonyms, };
		final int[] columnWidths = { 100, 250 };

		for (int i = 0; i < columnNames.length; ++i) {
			table.addTableColumn(columnNames[i], columnWidths[i]);
		}

		getViewer().setContentProvider(new TableContentProvider());
		getViewer().setLabelProvider(new TableLabelProvider());

		final Collection<SynonymGroup> readOnlySynonymGroups = synonymGroupService.findAllSynonymGroupForCatalog(getModel().getUidPk());
		catalogSynonymGroups = new HashMap<>();
		for (final SynonymGroup synonymGroup : readOnlySynonymGroups) {
			List<SynonymGroup> synonymGroupList = catalogSynonymGroups.computeIfAbsent(synonymGroup.getLocale(), key -> new ArrayList<>());
			synonymGroupList.add(synonymGroup);
		}

		// real input is retrieved via content provider
		refreshViewerInput();
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// nothing to bind
	}

	@Override
	protected EpState getEditorState() {
		EpState epState;
		if (AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.CATALOG_MANAGE)
				&& AuthorizationService.getInstance().isAuthorizedForCatalog(getModel())) {
			epState = EpState.EDITABLE;
		} else {
			epState = EpState.READ_ONLY;
		}
		return epState;
	}

	@Override
	public void commit(final boolean onSave) {
		if (onSave) {
			for (final SynonymGroup synonymGroup : getAddedItems()) {
				synonymGroupService.saveOrUpdate(synonymGroup);
			}
			getAddedItems().clear();

			for (final SynonymGroup synonymGroup : getModifiedItems()) {
				synonymGroupService.saveOrUpdate(synonymGroup);
			}
			getModifiedItems().clear();

			for (final SynonymGroup synonymGroup : getRemovedItems()) {
				synonymGroupService.remove(synonymGroup);
			}
			getRemovedItems().clear();

			super.commit(onSave);
		}

	}

	@Override
	public Catalog getModel() {
		return (Catalog) super.getModel();
	}

	@Override
	protected String getRemoveDialogTitle() {
		return CatalogMessages.get().CatalogSynonymGroupsSection_RemoveDialog_title;
	}

	@Override
	protected String getRemoveDialogDescription(final SynonymGroup item) {
		return
			NLS.bind(CatalogMessages.get().CatalogSynonymGroupsSection_RemoveDialog_description,
			getItemName(item));
	}

	@Override
	protected SynonymGroup addItemAction() {
		final Locale selectedLocale = ((AbstractCmClientEditorPage) getPage()).getSelectedLocale();
		final AddEditSynonymGroupDialog dialog = new AddEditSynonymGroupDialog(getSection().getShell(), getModel(),
				selectedLocale);
		if (dialog.open() == Window.OK) {
			return dialog.getModel();
		}
		return null;
	}

	@Override
	protected boolean editItemAction(final SynonymGroup object) {
		return new AddEditSynonymGroupDialog(getSection().getShell(), object).open() == Window.OK;
	}

	@Override
	protected boolean removeItemAction(final SynonymGroup object) {
		// nothing to do for removal
		return true;
	}

	@Override
	protected String getItemName(final SynonymGroup synonymGroup) {
		boolean moreTerms = false;
		final StringBuilder stringBuilder = new StringBuilder();
		for (final Synonym synonym : synonymGroup.getSynonyms()) {
			stringBuilder.append(synonym.getSynonym()).append(", "); //$NON-NLS-1$
			if (synonym.getSynonym().length() + synonym.getSynonym().length() > MAX_SYNONYM_STRING_LENGTH_IN_REMOVE_DIALOG) {
				moreTerms = true;
				break;
			}
		}

		if (moreTerms) {
			stringBuilder.delete(MAX_SYNONYM_STRING_LENGTH_IN_REMOVE_DIALOG, stringBuilder.length());
			stringBuilder.append("..."); //$NON-NLS-1$
		} else {
			// remove the last comma
			stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
		}

		return String.format("%1$s: %2$s", synonymGroup.getConceptTerm(), stringBuilder.toString()); //$NON-NLS-1$
	}

	@Override
	protected void addAddedItem(final SynonymGroup item) {
		super.addAddedItem(item);
		final Locale selectedLocale = ((AbstractCmClientEditorPage) getPage()).getSelectedLocale();
		List<SynonymGroup> synonymGroupList = catalogSynonymGroups.computeIfAbsent(selectedLocale, key -> new ArrayList<>());
		synonymGroupList.add(item);
		markDirty();
	}

	@Override
	protected void addModifiedItem(final SynonymGroup item) {
		super.addModifiedItem(item);
		markDirty();
	}

	@Override
	protected void addRemovedItem(final SynonymGroup item) {
		super.addRemovedItem(item);
		final Locale selectedLocale = ((AbstractCmClientEditorPage) getPage()).getSelectedLocale();
		catalogSynonymGroups.get(selectedLocale).remove(item);
		markDirty();
	}

	/**
	 * Content provider for the table.
	 */
	private class TableContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(final Object inputElement) {
			final Locale selectedLocale = ((AbstractCmClientEditorPage) getPage()).getSelectedLocale();
			final List<SynonymGroup> synonymGroupList = catalogSynonymGroups.get(selectedLocale);
			if (synonymGroupList == null) {
				return new Object[0];
			}
			return synonymGroupList.toArray();
		}

		@Override
		public void dispose() {
			// not used
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// not used
		}
	}

	/**
	 * Label provider for the table.
	 */
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final SynonymGroup synonymGroup = (SynonymGroup) element;

			switch (columnIndex) {
				case CONCEPT_TERM_COLUMN_INDEX:
					return synonymGroup.getConceptTerm();
				case SYNONYMS_COLUMN_INDEX:
					final StringBuilder stringBuilder = new StringBuilder();
					for (final Synonym synonym : synonymGroup.getSynonyms()) {
						stringBuilder.append(synonym.getSynonym()).append(", "); //$NON-NLS-1$
					}
					if (!synonymGroup.getSynonyms().isEmpty()) {
						stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
					}
					return stringBuilder.toString();
				default:
					return ""; //$NON-NLS-1$
			}
		}
	}

	@Override
	public void refresh() {
		// do nothing
	}
}
