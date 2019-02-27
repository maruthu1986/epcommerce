/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.views;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;

import com.elasticpath.cmclient.advancedsearch.AdvancedSearchImageRegistry;
import com.elasticpath.cmclient.advancedsearch.AdvancedSearchMessages;
import com.elasticpath.cmclient.advancedsearch.actions.TabQueryAction;
import com.elasticpath.cmclient.advancedsearch.helpers.AdvancedSearchQuerySelector;
import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.advancedsearch.AdvancedQueryType;
import com.elasticpath.domain.advancedsearch.AdvancedSearchQuery;
import com.elasticpath.domain.advancedsearch.QueryVisibility;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.persistence.dao.AdvancedSearchQueryDao;

/**
 * Provides methods for managing EqQL search queries.
 */
public class SavedQueriesTab implements AdvancedSearchQuerySelector {

	private static final int NUMBER_OF_ROWS = 20;

	private static final int QUERY_NAME_COLUMN = 0;

	private static final int QUERY_OWNER_COLUMN = 1;

	private static final int COLUMN_NAME_WIDTH = 180;

	private static final int COLUMN_OWNER_WIDTH = 100;

	private static final String SAVED_QUERIES_TABLE = "Saved Queries Table"; //$NON-NLS-1$

	private final AbstractAdvancedSearchView advancedSearchView;

	private final AdvancedSearchQueryDao searchQueryDao;

	private final ToolBarManager toolBarManager;

	private final Action createQueryAction;

	private final Action openQueryAction;

	private final Action editQueryAction;

	private final Action deleteQueryAction;

	private final Action runQueryAction;

	private IEpTableViewer epTableViewer;

	private Button runQueryButton;

	/**
	 * Constructor.
	 *
	 * @param tabFolder
	 *            parent's advanced search view tab folder
	 * @param tabIndex
	 *            index of this tab into tab folder
	 * @param advancedSearchView
	 *            parent advanced search view
	 */
	public SavedQueriesTab(final IEpTabFolder tabFolder, final int tabIndex,
			final AbstractAdvancedSearchView advancedSearchView) {
		this.searchQueryDao = ServiceLocator.getService(ContextIdNames.ADVANCED_SEARCH_QUERY_DAO);
		this.advancedSearchView = advancedSearchView;
		final IEpLayoutComposite savedQueriesTab = tabFolder.addTabItem(AdvancedSearchMessages.get().SavedQueries,
				AdvancedSearchImageRegistry.getImage(AdvancedSearchImageRegistry.QUERY), tabIndex, 1, false);

		// creating Actions
		createQueryAction = new TabQueryAction.CreateQueryAction(advancedSearchView);
		openQueryAction = new TabQueryAction.OpenQueryAction(advancedSearchView, this);
		editQueryAction = new TabQueryAction.EditQueryAction(advancedSearchView, this);
		runQueryAction = new TabQueryAction.RunQueryAction(advancedSearchView);
		deleteQueryAction = new TabQueryAction.DeleteQueryAction(advancedSearchView, this);

		// create tool bar
		toolBarManager = new ToolBarManager(SWT.WRAP | SWT.FLAT);

		createControls(savedQueriesTab);
		populateControls();
	}

	private void createControls(final IEpLayoutComposite tabComposite) {

		toolBarManager.add(runQueryAction);
		toolBarManager.add(openQueryAction);
		toolBarManager.add(new Separator());
		toolBarManager.add(editQueryAction);
		toolBarManager.add(createQueryAction);
		toolBarManager.add(deleteQueryAction);

		// this must be done after all actions added to toolBarManager
		tabComposite.getFormToolkit().adapt(toolBarManager.createControl(tabComposite.getSwtComposite()));

		epTableViewer = tabComposite.addTableViewer(false, EpState.EDITABLE, null, SAVED_QUERIES_TABLE);
		epTableViewer.addTableColumn(AdvancedSearchMessages.get().QueryName, COLUMN_NAME_WIDTH, IEpTableColumn.TYPE_NONE);
		epTableViewer.addTableColumn(AdvancedSearchMessages.get().QueryOwner, COLUMN_OWNER_WIDTH, IEpTableColumn.TYPE_NONE);
		epTableViewer.setLabelProvider(new QueryViewLabelProvider());
		epTableViewer.setContentProvider(new ArrayContentProvider());
		epTableViewer.getSwtTableViewer().addDoubleClickListener(
				event -> runQueryAction.run());

		epTableViewer.getSwtTableViewer().setComparator(new ViewerComparator() {
			@Override
			public int compare(final Viewer viewer, final Object element1,
					final Object element2) {
				return ((AdvancedSearchQuery) element1).getName().compareTo(
						((AdvancedSearchQuery) element2).getName());
			}

		});
		setCheatGirdDataForToTable(epTableViewer.getSwtTable());

		runQueryButton = tabComposite.addPushButton(AdvancedSearchMessages.get().RunQuery,
				AdvancedSearchImageRegistry.getImage(AdvancedSearchImageRegistry.IMAGE_QUERY_RUN), EpState.EDITABLE, null);

		runQueryButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				AdvancedSearchQuery currentSelected = getCurrentSelected();
				if (currentSelected != null) {
					advancedSearchView.executeSearchForSelectedElement();
				}
			}

		});

		epTableViewer.getSwtTableViewer().addSelectionChangedListener(
				event -> buttonStateChange());

		final MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.add(runQueryAction);
		menuMgr.add(openQueryAction);
		menuMgr.add(new Separator());
		menuMgr.add(editQueryAction);
		menuMgr.add(deleteQueryAction);

		final Menu menu = menuMgr.createContextMenu(epTableViewer
				.getSwtTableViewer().getControl());
		epTableViewer.getSwtTableViewer().getControl().setMenu(menu);
	}

	private void setCheatGirdDataForToTable(final Table swtTable) {
		final GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.heightHint = NUMBER_OF_ROWS * (swtTable.getItemHeight() + swtTable.getGridLineWidth() * 2) + swtTable.getHeaderHeight();
		swtTable.setLayoutData(data);
	}

	/**
	 * Get the selected element.
	 *
	 * @return AdvancedSearchQuery the element in view row.
	 */
	@Override
	public final AdvancedSearchQuery getCurrentSelected() {
		final IStructuredSelection selection = (IStructuredSelection) epTableViewer.getSwtTableViewer().getSelection();
		return (AdvancedSearchQuery) selection.getFirstElement();
	}

	/**
	 * Provides labels for the QueryView TableViewer.
	 */
	protected class QueryViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			if (columnIndex == QUERY_NAME_COLUMN) {
				final AdvancedSearchQuery query = (AdvancedSearchQuery) element;
				switch (query.getQueryType()) {
				case PRODUCT:
					return getProductQueryImage(query.getQueryVisibility());
				case CATEGORY:
					return CatalogImageRegistry.getImage(CatalogImageRegistry.CATEGORY);
				default:
					return null;
				}
			}
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final AdvancedSearchQuery query = (AdvancedSearchQuery) element;
			switch (columnIndex) {
			case QUERY_NAME_COLUMN:
				return query.getName();
			case QUERY_OWNER_COLUMN:
				return
					NLS.bind(AdvancedSearchMessages.get().UserName_Format,
					new Object[] { query.getOwner().getFirstName(), query.getOwner().getLastName() });
				default:
				return ""; //$NON-NLS-1$
			}			
		}

		private Image getProductQueryImage(final QueryVisibility queryVisibility) {
			if (queryVisibility == QueryVisibility.PRIVATE) { 
				return CatalogImageRegistry.getImage(CatalogImageRegistry.PRODUCT_PRIVATE_SMALL);
			}
			
			return CatalogImageRegistry.getImage(CatalogImageRegistry.PRODUCT_SMALL);
		}
	}

	/**
	 * Refreshes saved queries tab.
	 */
	public void refreshTab() {
		populateControls();
	}

	/**
	 * Populates the Query Table View.
	 */
	private void populateControls() {
		final CmUser owner = LoginManager.getCmUser();
		final List<AdvancedQueryType> queryTypes = Arrays.asList(advancedSearchView.getQueryTypes());
		List<AdvancedSearchQuery> queryList;
		
		if (owner.isSuperUser()) {
			queryList = searchQueryDao.findAllQueriesWithTypes(queryTypes, true);
		} else {
			queryList = searchQueryDao.findAllVisibleQueriesWithTypes(owner, queryTypes, true);
		}

		Object[] result = queryList.toArray(new Object[queryList.size()]);
		epTableViewer.setInput(result);

		buttonStateChange();
	}

	private void buttonStateChange() {
		boolean enabled = getCurrentSelected() != null;

		openQueryAction.setEnabled(enabled);
		editQueryAction.setEnabled(enabled);
		deleteQueryAction.setEnabled(enabled);
		runQueryAction.setEnabled(enabled);
		runQueryButton.setEnabled(enabled);
	}
}
