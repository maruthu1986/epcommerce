/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.catalog;

import java.util.Collection;
import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.catalog.editors.ViewSynchronizer;
import com.elasticpath.cmclient.catalog.editors.model.CatalogModel;
import com.elasticpath.cmclient.catalog.editors.model.CatalogModelImpl;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.event.ChangeSetMemberSelectionProvider;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.TableSelectionProvider;
import com.elasticpath.cmclient.policy.ui.EditorTableSelectionProvider;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.search.solr.SolrIndexConstants;

//import org.eclipse.emf.common.util.WrappedException;


/**
 * Implements a multi-page editor for displaying and editing catalogs.
 */
public class CatalogEditor extends AbstractCmClientFormEditor implements ChangeSetMemberSelectionProvider,
		EditorTableSelectionProvider {
	/**
	 * Editor ID.
	 */
	public static final String PART_ID = CatalogEditor.class.getName();

	private static final boolean ENABLE_SYNONYM_GROUPS = System.getProperty(SolrIndexConstants.SOLR_ENABLE_SYNONYM_GROUPS) != null;

	private CatalogModel catalogModel;

	private CatalogService catalogService;

	private final TableSelectionProvider tableSelectionProvider = new TableSelectionProvider();


	@Override
	public void initEditor(final IEditorSite site, final IEditorInput input) {
		catalogService = ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);

		final Catalog catalog = input.getAdapter(Catalog.class);
		catalogModel = new CatalogModelImpl(catalog);

		// add the category type table listener to the selection service as a selection provider
		// a selected category type can then be added to a change set, see AddToChangeSetActionDelegate
		site.setSelectionProvider(tableSelectionProvider);
	}

	@Override
	public CatalogModel getModel() {
		return catalogModel;
	}

	@Override
	@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
	protected void addPages() {
		try {
			addPage(new CatalogSummaryPage(this));
			addPage(new CatalogAttributesPage(this));
			addPage(new CatalogCartItemModifierGroupsPage(this));
			addPage(new CatalogCategoryTypesPage(this));
			addPage(new CatalogProductTypesPage(this));
			addPage(new CatalogSkuOptionsPage(this));
			addPage(new CatalogBrandsPage(this));
			// Synonym Groups are deprecated as of 6.2.2
			// set this system property if you have a customization still requiring synonym groups
			if (ENABLE_SYNONYM_GROUPS) {
				addPage(new CatalogSynonymGroupsPage(this));
			}
			addExtensionPages(getClass().getSimpleName(), CatalogPlugin.PLUGIN_ID);
		} catch (final PartInitException e) {

			// TODO: Find out what should be done in this case
			// Can't throw the PartInitException because it is checked
			// and the super-implementation doesn't check for it.
			// throwing an unchecked generic exception for now (bad)
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void saveModel(final IProgressMonitor monitor) {
		monitor.beginTask(CatalogMessages.get().CatalogEditor_Save_StatusBarMsg, 2);
		try {
			if (!checkCatalog()) {
				MessageDialog.openWarning(getSite().getShell(), CatalogMessages.get().CatalogEditor_CanNotSaveCatalog,
						CatalogMessages.get().CreateCatalogDialog_CatalogNameExists_ErrorMessage);
				monitor.setCanceled(true);
				return;
			}

			getModel().setCatalog(catalogService.saveOrUpdate(getModel().getCatalog()));
			refreshEditorTitle();
			refreshEditorPages();
			final ItemChangeEvent<Catalog> event = new ItemChangeEvent<>(this, getModel().getCatalog());
			monitor.worked(1);

			CatalogEventService.getInstance().notifyCatalogChanged(event);
		} finally {
			monitor.done();
		}
	}

	private void refreshEditorTitle() {
		setInput(new CatalogEditorInput(getModel().getCatalog()));
	}

	private boolean checkCatalog() {
		final Catalog catalogByName = catalogService.findByName(getModel().getCatalog().getName());
		return catalogByName == null || catalogByName.getUidPk() == getModel().getCatalog().getUidPk();
	}

	@Override
	public void reloadModel() {
		getModel().setCatalog(catalogService.getCatalog(getModel().getCatalog().getUidPk()));
		getModel().clearAllChangeSets();
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return getModel().getCatalog().getSupportedLocales();
	}

	@Override
	public Locale getDefaultLocale() {
		return getModel().getCatalog().getDefaultLocale();
	}

	@Override
	protected String getSaveOnCloseMessage() {
		return
			NLS.bind(CatalogMessages.get().CatalogEditor_OnSavePrompt,
			getEditorName());
	}

	@Override
	protected String getEditorName() {
		return getEditorInput().getName();
	}

	@Override
	public Object resolveObjectMember(final Object changeSetObjectSelection) {
		return changeSetObjectSelection;
	}

	@Override
	public TableSelectionProvider getEditorTableSelectionProvider() {
		return tableSelectionProvider;
	}


	@Override
	public Object getAdapter(final Class clazz) {
		if (clazz == ViewSynchronizer.class) {
			return new MyViewSynchronizer();
		}

		return super.getAdapter(clazz);
	}

	/**
	 * @return requiresSaving
	 */
	public boolean requiresSaving() {
		return isDirty() && isSaveOnCloseNeeded();

	}

	/**
	 * Implementation of {@link com.elasticpath.cmclient.catalog.editors.ViewSynchronizer}.
	 */
	protected class MyViewSynchronizer implements ViewSynchronizer {

		/**
		 * Save or reload the editor as requested by the user.
		 *
		 * @return true if editor does not require saving or if save or reload chosen, false if canceled by user.
		 */
		@Override
		public boolean saveOrReload() {
			if (requiresSaving()) {
				int choice = promptToSaveOnClose();
				if (choice == ISaveablePart2.YES) {
					save();
					return true;
				} else if (choice == ISaveablePart2.NO) {
					reload();
					return true;
				} else if (choice == ISaveablePart2.CANCEL) {
					return false;
				}
			}
			return true;
		}

		private void save() {
			doSave(new IProgressMonitor() {

				@Override
				public void worked(final int arg0) {
					// empty method
				}

				@Override
				public void subTask(final String arg0) {
					// empty method
				}

				@Override
				public void setTaskName(final String arg0) {
					// empty method
				}

				@Override
				public void setCanceled(final boolean arg0) {
					// empty method
				}

				@Override
				public boolean isCanceled() {
					return false;
				}

				@Override
				public void internalWorked(final double arg0) {
					// empty method
				}

				@Override
				public void done() {
					// empty method
				}

				@Override
				public void beginTask(final String arg0, final int arg1) {
					// empty method
				}
			});
		}
	}
}
