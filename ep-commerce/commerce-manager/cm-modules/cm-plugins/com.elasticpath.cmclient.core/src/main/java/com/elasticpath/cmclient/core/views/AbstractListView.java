/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.views;


import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.common.PaginationContributionControl;
import com.elasticpath.cmclient.core.event.NavigationEvent;
import com.elasticpath.cmclient.core.event.NavigationEvent.NavigationType;
import com.elasticpath.cmclient.core.event.NavigationEventListener;
import com.elasticpath.cmclient.core.event.NavigationEventService;
import com.elasticpath.cmclient.core.helpers.PaginationChangeListener;
import com.elasticpath.cmclient.core.helpers.PaginationSupport;
import com.elasticpath.cmclient.core.helpers.extenders.ExtensibleTableLabelProvider;
import com.elasticpath.cmclient.core.helpers.extenders.PluginHelper;
import com.elasticpath.cmclient.core.service.CoreEventService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.ui.framework.impl.EpTableViewer;

/**
 * Provides the abstract functionality for a SearchResults view, including navigation buttons and navigation labels in the view's toolbar.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.GodClass" })
public abstract class AbstractListView extends AbstractCmClientView
	implements NavigationEventListener, PaginationChangeListener, PaginationSupport  {
	/**
	 *
	 */
	private static final int MARGIN_BOTTOM = 5;

	private static final Logger LOG = Logger.getLogger(AbstractListView.class);

	private String tableName;

	private NavigationEventService navigationService;

	/**
	 * The view's TableViewer.
	 */
	private TableViewer viewer;

	private int resultsCount;

	private int resultsPaging = getPagination();
	private int resultsStartIndex;

	private final boolean enableNavigation;

	private ExpandableComposite errorComposite;

	private Label errorLabel;

	private IEpLayoutComposite layoutComposite;

	private boolean checkable;

	private ToolBarManager toolbarManager;

	private PaginationContributionControl paginationControl;

	/**
	 * Constructs new list view.
	 *
	 * @param enableNavigation true if navigation buttons should be visible
	 * @param tableName name of the table
	 */
	public AbstractListView(final boolean enableNavigation, final String tableName) {
		super();
		this.tableName = tableName;
		this.enableNavigation = enableNavigation;
		CoreEventService.getInstance().addPaginationListener(this);
	}

	/**
	 * Constructs new list view.
	 *  @param enableNavigation true if navigation buttons should be visible
	 * @param checkable if this view should contain checkboxes.
	 * @param tableName name of the table
	 */
	public AbstractListView(final boolean enableNavigation, final boolean checkable, final String tableName) {
		this(enableNavigation, tableName);
		this.checkable = checkable;
	}

	@Override
	public void createViewPartControl(final Composite parent) {
		layoutComposite = CompositeFactory.createGridLayoutComposite(parent, 1, true);
		// implement set methods in IEpLayoutComposite
		GridLayout gridLayout = (GridLayout) layoutComposite.getSwtComposite().getLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;

		// Toolbar section
		setToolbarManager(createOrRetrieveToolbarManager());

		IEpLayoutData toolbarLayoutData = layoutComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, false, false);
		ToolBar toolbar = getToolbarManager().createControl(layoutComposite.getSwtComposite());
		toolbar.setLayoutData(toolbarLayoutData.getSwtLayoutData());
		layoutComposite.getSwtComposite().setBackground(toolbar.getBackground());

		// Error message section
		IEpLayoutData errorLayoutData = layoutComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
		((GridData) errorLayoutData.getSwtLayoutData()).grabExcessHorizontalSpace = true;

		IEpLayoutComposite epErrorComposite = layoutComposite.addExpandableComposite(2, true, null, errorLayoutData);
		errorComposite = (ExpandableComposite) epErrorComposite.getSwtComposite().getParent();
		gridLayout = (GridLayout) epErrorComposite.getSwtComposite().getLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginBottom = MARGIN_BOTTOM;
		gridLayout.verticalSpacing = 0;

		// error image
		IEpLayoutData imageLayoutData = epErrorComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		((GridData) imageLayoutData.getSwtLayoutData()).grabExcessHorizontalSpace = true;
		epErrorComposite.addImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_WARNING_SMALL), imageLayoutData);

		// error label
		IEpLayoutData labelLayoutData = epErrorComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER);
		((GridData) labelLayoutData.getSwtLayoutData()).grabExcessHorizontalSpace = true;
		errorLabel = epErrorComposite.addLabel(" ", labelLayoutData); //$NON-NLS-1$

		hideErrorMessage();

		final IEpLayoutData tableLayoutData = layoutComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		IEpTableViewer epTableViewer;

		if (checkable) {
			epTableViewer = new EpTableViewer(layoutComposite.addCheckboxTableViewer(EpState.READ_ONLY, tableLayoutData, true, tableName), false);
		} else {
			epTableViewer = layoutComposite.addTableViewer(false, EpState.READ_ONLY, tableLayoutData, tableName);
		}
		viewer = epTableViewer.getSwtTableViewer();
		viewer.setComparator(new ViewerComparator() {

			@Override
			public void sort(final Viewer viewer, final Object[] elements) {
				// Do nothing, sorting will be done before elements are set on table
			}

		});


		initializeTable(epTableViewer);
		addExtendedTableColumns(epTableViewer);

		epTableViewer.setContentProvider(getViewContentProvider());
		epTableViewer.setLabelProvider(new ExtensibleTableLabelProvider(getViewLabelProvider(), getClass().getSimpleName(), getPluginId()));
		refreshViewerInput();

		initializeViewToolbar();

		if (enableNavigation) {
			if (getPluginContributionSeparatorGroup() != null) {
				getToolbarManager().add(new Separator(getPluginContributionSeparatorGroup()));
			}
			initializeToolbarNavigationComponents(getToolbarManager());
			getNavigationService().registerNavigationEventListener(this);
		}

		initializeViewToolbarPost();

		// new
		getToolbarManager().update(true);
		layoutComposite.getSwtComposite().layout();
		updateNavigationComponents();
	}

	/**
	 * Extension for post initializing view tool bar, ie. after navigation bar.
	 */
	protected void initializeViewToolbarPost() {
		// do nothing for extension
		// put control creation code here
	}

	/**
	 * Creates or retrieves the toolbar manager.
	 *
	 * @return the toolbar manager
	 * */
	protected ToolBarManager createOrRetrieveToolbarManager() {
		return new ToolBarManager(SWT.FLAT | SWT.RIGHT | SWT.HORIZONTAL);
	}

	/**
	 * Some plugins will add menu contributions through the plugin.xml. This separator
	 * puts them between the hard coded contributions and the pagination control.
	 *
	 * @return the name of the separator group
	 * */
	protected String getPluginContributionSeparatorGroup() {
		return null;
	}

	/**
	 * Convenience method to refresh the viewer with the latest view input.
	 */
	public void refreshViewerInput() {
		if (viewer.getContentProvider() != null) {
			viewer.setInput(getViewInput());
		}
	}

	/**
	 * Get this view's underlying {@link TableViewer}.
	 *
	 * @return this view's underlying TableViewer
	 */
	public TableViewer getViewer() {
		return viewer;
	}

	/**
	 * Adds a double click listener to the Viewer, which invokes the run method of the given action.
	 *
	 * @param doubleClickAction the action whose run method should be invoked upon double-click
	 */
	public void addDoubleClickAction(final Action doubleClickAction) {
		this.viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent event) {
				if (doubleClickAction.isEnabled()) {
					doubleClickAction.run();
				}
			}
		});
	}

	/**
	 * Finds and adds extension columns.
	 * @param epTableViewer The table Viewer to extend.
	 */
	protected void addExtendedTableColumns(final IEpTableViewer epTableViewer) {
		PluginHelper.findTables(getClass().getSimpleName(), getPluginId())
				.forEach(table -> table.visitColumnNames()
						.forEach(col -> epTableViewer.addTableColumn(col, -1)));

	}
	/**
	 * Gets the table's corresponding plugin, to identify where to find the extensions.
	 * @return The plugin ID of the table.
	 */
	protected abstract String getPluginId();

	/**
	 * Initialize the view's toolbar with any actions or buttons that are specific to the subclass. Called automatically by the abstract constructor
	 * because navigation components need to be inserted afterwards.
	 */
	protected abstract void initializeViewToolbar();

	/**
	 * Get the array of objects that will be used as input to the view's TableViewer.
	 *
	 * @return array of objects that will be used as input to the view's TableViewer
	 */
	protected abstract Object[] getViewInput();

	/**
	 * Initializes the TableViewer's internal Table. Sets the column headers, etc.
	 *
	 * @param epTableViewer the viewer's Table object
	 */
	protected abstract void initializeTable(final IEpTableViewer epTableViewer);

	/**
	 * Get this view's TableViewer's label provider. Subclasses must provide their own <code>LabelProvider</code>.
	 *
	 * @return this viewer's LabelProvider
	 */
	protected abstract ITableLabelProvider getViewLabelProvider();

	/**
	 * Get this view's ContentProvider. If subclasses don't specify their own ContentProvider this will return a default one.
	 *
	 * @return the view's ContentProvider.
	 */
	protected IStructuredContentProvider getViewContentProvider() {
		return new ViewContentProvider();
	}

	/**
	 * Get the number of results that the view could show if there were no paging.
	 *
	 * @return the resultsCount
	 */
	public int getResultsCount() {
		return resultsCount;
	}

	/**
	 * Set the number of results that the view could show if there were no paging.
	 *
	 * @param resultsCount the resultsCount
	 */
	public void setResultsCount(final int resultsCount) {
		this.resultsCount = resultsCount;
	}

	/**
	 * Get the index within the search results count at which this view will start displaying results (the index of the result at the top of this
	 * page of results).
	 *
	 * @return the results start index
	 */
	public int getResultsStartIndex() {
		return resultsStartIndex;
	}

	/**
	 * Set the index within the search results count at which this view will start displaying results (the index of the result at the top of this
	 * page of results).
	 *
	 * @param resultsStartIndex the startIndex
	 */
	public void setResultsStartIndex(final int resultsStartIndex) {
		this.resultsStartIndex = resultsStartIndex;
	}

	/**
	 * Get the number of results to show on a page.
	 *
	 * @return the number of results to show on a page
	 */
	public int getResultsPaging() {
		return resultsPaging;
	}

	@Override
	public void fireNavigationEvent(final NavigationType navigationType, final Object[] args) {
		getNavigationService().fireNavigationEvent(new NavigationEvent(viewer, navigationType, args));
	}

	/**
	 * Creates the view toolbar navigation components.
	 *
	 * @param toolBarManager toolbar manager
	 */
	protected void initializeToolbarNavigationComponents(final IToolBarManager toolBarManager) {
		paginationControl = new PaginationContributionControl(toolBarManager, this);
		paginationControl.createViewPartControl();
	}

	@Override
	public void dispose() {
		getNavigationService().unregisterNavigationEventListener(this);
		CoreEventService.getInstance().removePaginationListener(this);
		super.dispose();
	}

	/** Sets the focus. */
	@Override
	public void setFocus() {
		// Auto-generated method stub
	}

	@Override
	protected Object getModel() {
		// Auto-generated method stub
		return null;
	}

	/**
	 * Provides content to the Search Results View. If a NavigationEvent occurs, adjusts the table model accordingly and updates the results label.
	 * If a SearchResultEvent occurs, replaces the view's contents with the event's list of results.
	 */
	protected class ViewContentProvider implements IStructuredContentProvider {

		/**
		 * Constructor. Registers this content provider with listener services.
		 */
		public ViewContentProvider() {
			// none
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			LOG.debug("The view's input has been changed"); //$NON-NLS-1$
		}

		@Override
		public void dispose() {
			// nothing
		}

		@Override
		public Object[] getElements(final Object inputElement) {
			if (inputElement instanceof Object[]) {
				LOG.debug("TableViewer input set to array of Objects"); //$NON-NLS-1$
				return (Object[]) inputElement;
			}
			return new Object[0];
		}
	}





	/**
	 * @return
	 */
	private NavigationEventService getNavigationService() {
		if (navigationService == null) {
			navigationService = new NavigationEventService();
		}
		return navigationService;
	}

	/**
	 * Called by the <code>NavigationEventService</code> when <code>NavigationEvent</code>s occur.
	 *
	 * @param event the NavigationEvent
	 */
	public void navigationChanged(final NavigationEvent event) {
		LOG.debug("Navigation event"); //$NON-NLS-1$
		final Enum<NavigationEvent.NavigationType> type = event.getType();
		if (type.equals(NavigationType.FIRST)) {
			navigateFirst();
		} else if (type.equals(NavigationType.PREVIOUS)) {
			navigatePrevious();
		} else if (type.equals(NavigationType.NEXT)) {
			navigateNext();
		} else if (type.equals(NavigationType.LAST)) {
			navigateLast();
		} else if (type.equals(NavigationType.TO)) {
			int toPage = ((Integer) event.getArgs()[0]).intValue();
			navigateTo(toPage);
		}

		updateNavigationComponents();
	}

	/**
	 * Go to the first page.
	 */
	protected void navigateFirst() {
		if (resultsStartIndex == 0) {
			return;
		}
		resultsStartIndex = 0;
		refreshViewerInput();
	}

	/**
	 * Go to the previous page.
	 */
	protected void navigatePrevious() {
		resultsStartIndex = Math.max(0, resultsStartIndex - resultsPaging);
		refreshViewerInput();
	}

	/**
	 * Go to the next page.
	 */
	protected void navigateNext() {
		resultsStartIndex += resultsPaging;
		refreshViewerInput();
	}

	/**
	 * Go to the last page.
	 */
	protected void navigateLast() {
		resultsStartIndex = resultsCount / resultsPaging * resultsPaging;
		if (resultsStartIndex == resultsCount && resultsStartIndex - resultsPaging > 0) {
			resultsStartIndex -= resultsPaging;
		}
		refreshViewerInput();
	}

	/**
	 * Go to the specific page passed in.
	 *
	 * @param pageNumber the page number
	 */
	protected void navigateTo(final int pageNumber) {
		resultsStartIndex = getStartIndexByPageNumber(pageNumber, getResultsPaging());
		refreshViewerInput();
	}

	/**
	 * get the page start index by page number.
	 *
	 * @param pageNumber the page number
	 * @param itemsPerPage item number per page
	 * @return the start index
	 */
	protected int getStartIndexByPageNumber(final int pageNumber, final int itemsPerPage) {
		return (pageNumber - 1) * itemsPerPage;
	}

	/**
	 * Get the list of objects for current page.
	 *
	 * @param objects the list of results
	 * @param startIndex the current page starting index
	 * @param paging the amount to page
	 * @return the current page's results
	 */
	protected Object[] getPageResults(final Object[] objects, final int startIndex, final int paging) {
		if ((objects == null) || (objects.length == 0)) {
			return new Object[0];
		}

		int length = paging;
		if (length > objects.length - startIndex) {
			length = objects.length - startIndex;
		}
		final Object[] ret = new Object[length];

		for (int nIndex = 0; nIndex < length; nIndex++) {
			if (nIndex + startIndex >= objects.length) {
				break;
			}
			ret[nIndex] = objects[startIndex + nIndex];
		}
		return ret;
	}

	/**
	 * Shows a message above the table within an expandable composite. The message needs to be hidden manually.
	 *
	 * @param message the message to be shown
	 */
	public void showMessage(final String message) {
		errorLabel.setText(message);
		errorComposite.setExpanded(true);
		layoutComposite.getSwtComposite().layout();
	}

	/**
	 * Hides the message displayed above the table.
	 */
	public void hideErrorMessage() {
		if (!errorComposite.isDisposed() && !layoutComposite.getSwtComposite().isDisposed()) {
			errorComposite.setExpanded(false);
			layoutComposite.getSwtComposite().layout();
		}
	}

	@Override
	public void paginationChange(final int newValue) {
		resultsPaging = newValue;
		refreshViewerInput();

		updateNavigationComponents();
	}

	/**
	 * update naviation components.
	 */
	public void updateNavigationComponents() {
		if (paginationControl != null) {
			paginationControl.updateNavigationComponents();
		}
		if (layoutComposite != null) {
			layoutComposite.getSwtComposite().layout();
		}
	}

	/**
	 * Set Toolbar Manager.
	 * @param toolbarManager the toolbarManager to set
	 */
	protected void setToolbarManager(final ToolBarManager toolbarManager) {
		this.toolbarManager = toolbarManager;
	}

	/**
	 * Get Toolbar Manager.
	 * @return the toolbarManager
	 */
	protected ToolBarManager getToolbarManager() {
		return toolbarManager;
	}
}
