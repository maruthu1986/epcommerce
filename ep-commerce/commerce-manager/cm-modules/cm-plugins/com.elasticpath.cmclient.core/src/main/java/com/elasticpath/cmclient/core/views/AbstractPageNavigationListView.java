/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 *
 */
package com.elasticpath.cmclient.core.views;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.common.PaginationContributionControl;
import com.elasticpath.cmclient.core.event.NavigationEvent.NavigationType;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.PaginationChangeListener;
import com.elasticpath.cmclient.core.helpers.PaginationSupport;
import com.elasticpath.cmclient.core.helpers.extenders.EPTableColumnCreator;
import com.elasticpath.cmclient.core.helpers.extenders.ExtensibleTableLabelProvider;
import com.elasticpath.cmclient.core.helpers.extenders.PluginHelper;
import com.elasticpath.cmclient.core.pagination.PaginationInfo;
import com.elasticpath.cmclient.core.service.CoreEventService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.ui.framework.impl.EpTableViewer;

/**
 * AbstractPageNavigationListView
 * abstract class for paged views.
 *
 * @param <T>
 */
@SuppressWarnings({"PMD.GodClass"})
public abstract class AbstractPageNavigationListView<T> extends AbstractCmClientView implements PaginationChangeListener, PaginationSupport {

	private static final Logger LOG = Logger.getLogger(AbstractPageNavigationListView.class);

	/**
	 *
	 */
	private static final int MARGIN_BOTTOM = 5;

	/**
	 * Event constant for model changes.
	 */
	protected static final int PROPERTY_MODEL = 9900;
	/**
	 * Event constant for navigation changes.
	 */
	protected static final int PROPERTY_NAVIGATION_KEY = 9901;
	private final String tableName;

	/**
	 * The view's TableViewer.
	 */
	private TableViewer viewer;

	private IEpLayoutComposite layoutComposite;

	private ExpandableComposite errorComposite;

	private Label errorLabel;

	private final boolean checkable;

	private PaginationContributionControl paginationControl;

	/**
	 * View data holder.
	 */
	private PageNavigator pageNavigator;


	/**
	 * Constructor.
	 *
	 * @param checkable will be view use a checkable editor
	 * @param tableName name of the table
	 */
	public AbstractPageNavigationListView(final boolean checkable, final String tableName) {
		super();
		this.checkable = checkable;
		this.tableName = tableName;
		CoreEventService.getInstance().addPaginationListener(this);
	}

	@Override
	public void createViewPartControl(final Composite parent) {

		// base layout
		layoutComposite = CompositeFactory.createGridLayoutComposite(parent, 1, true);

		GridLayout gridLayout = (GridLayout) layoutComposite.getSwtComposite().getLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;

		// Toolbar section
		final ToolBarManager toolbarManager = new ToolBarManager(SWT.FLAT | SWT.RIGHT | SWT.HORIZONTAL);

		IEpLayoutData toolbarLayoutData = layoutComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, true, false);
		ToolBar toolbar = toolbarManager.createControl(layoutComposite.getSwtComposite());
		toolbar.setLayoutData(toolbarLayoutData.getSwtLayoutData());
		layoutComposite.getSwtComposite().setBackground(toolbar.getBackground());

		initializeToolbar(toolbarManager);

		// Error message section
		IEpLayoutData errorLayoutData = layoutComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
		((GridData) errorLayoutData.getSwtLayoutData()).grabExcessHorizontalSpace = true;

		IEpLayoutComposite epErrorComposite = layoutComposite.addExpandableComposite(2, true, null, errorLayoutData);
		errorComposite = (ExpandableComposite) epErrorComposite.getSwtComposite().getParent();
//		errorComposite.setBackground(CmClientResources.getColor(CmClientResources.COLOR_RED));
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

		this.addPropertyListener(new IPropertyListener() {
			public void propertyChanged(final Object object, final int flag) {
				if (flag == PROPERTY_MODEL) {
					PageNavigator pageNavigator = AbstractPageNavigationListView.this.pageNavigator;
					// show message for empty result
					if (pageNavigator == null || pageNavigator.getTotalItemsFound() == 0) {
						AbstractPageNavigationListView.this.showErrorMessage(CoreMessages.get().NoSearchResultsError);
					} else {
						AbstractPageNavigationListView.this.hideErrorMessage();
					}
				}
			}
		});

		hideErrorMessage();

		final IEpLayoutData tableLayoutData = layoutComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		IEpTableViewer epTableViewer;

		if (checkable) {
			epTableViewer = new EpTableViewer(layoutComposite.addCheckboxTableViewer(EpState.READ_ONLY, tableLayoutData, true, tableName), false);
		} else {
			epTableViewer = layoutComposite.addTableViewer(false, EpState.READ_ONLY, tableLayoutData, tableName);
		}
		viewer = epTableViewer.getSwtTableViewer();

		initializeTable(epTableViewer);
		addExtendedTableColumns(epTableViewer);

		epTableViewer.setContentProvider(getViewContentProvider());
		epTableViewer.setLabelProvider(new ExtensibleTableLabelProvider(getViewLabelProvider(), this.getClass().getSimpleName(), getPluginId()));
		if (pageNavigator != null) {
			viewer.setInput(this.pageNavigator.getPageItems());
		}

		this.getSite().setSelectionProvider(this.viewer);

		this.addPropertyListener((object, flag) -> {
			if (flag == PROPERTY_MODEL) {
				AbstractPageNavigationListView.this.viewer.setInput(AbstractPageNavigationListView.this.pageNavigator.getPageItems());
			}
		});
		//TODO Table Items are not present at this time so there will be no ids set
		//EPTestUtilFactory.getInstance().getTestIdUtil().setTestIdsToTableItems(viewer.getTable());
	}


	/**
	 * Shows a message above the table within an expandable composite. The message needs to be hidden manually.
	 *
	 * @param message the message to be shown
	 */
	public void showErrorMessage(final String message) {
		errorLabel.setText(message);
		errorComposite.setExpanded(true);
		layoutComposite.getSwtComposite().layout();
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
	 * Hides the message displayed above the table.
	 */
	public void hideErrorMessage() {
		if (!errorComposite.isDisposed() && !layoutComposite.getSwtComposite().isDisposed()) {
			errorComposite.setExpanded(false);
			layoutComposite.getSwtComposite().layout();
		}
	}

	/**
	 * Get this view's ContentProvider. If subclasses don't specify their own ContentProvider this will return a default one.
	 *
	 * @return the view's ContentProvider.
	 */
	protected IStructuredContentProvider getViewContentProvider() {
		return new IStructuredContentProvider() {
			@Override
			public Object[] getElements(final Object inputElement) {
				if (inputElement instanceof Object[]) {
					LOG.debug("TableViewer input set to array of Objects"); //$NON-NLS-1$
					return (Object[]) inputElement;
				}
				return new Object[0];
			}

			@Override
			public void dispose() { //NOPMD
			}

			@Override
			public void inputChanged(final Viewer arg0, final Object arg1, final Object arg2) { //NPMD
			}
		};
	}

	/**
	 * Creates the view toolbar navigation components.
	 *
	 * @param manager toolbar manager
	 */
	protected void initializeToolbar(final IToolBarManager manager) {
		paginationControl = new PaginationContributionControl(manager, this);
		paginationControl.createViewPartControl();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				manager.update(true);
				layoutComposite.getSwtComposite().layout();
			}
		});
	}

	/**
	 * Get the number of results to show on a page.
	 *
	 * @return the number of results to show on a page
	 */
	public int getResultsPaging() {
		return PaginationInfo.getInstance().getPagination();
	}

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

	@Override
	public void setFocus() { //NOPMD

	}

	@Override
	protected Object getModel() {
		if (this.pageNavigator == null) {
			return new Object[0];
		}
		return this.pageNavigator.getPageItems().clone();
	}

	/**
	 * PageNavigator
	 * a page navigator helper.
	 */
	protected final class PageNavigator {
		private boolean firstActionEnabled;
		private boolean previousActionEnabled;
		private boolean nextActionEnabled;
		private boolean lastActionEnabled;
		//private final int pageSize;

		private final List<T> itemList;
		private int startIndex;

		/**
		 * Constructor.
		 *
		 * @param itemList items list
		 */
		public PageNavigator(final List<T> itemList) {
			super();
			this.itemList = itemList;
			this.updateFlags();
		}

		/**
		 * Constructor.
		 *
		 * @param itemList   items list
		 * @param startIndex start index
		 */
		public PageNavigator(final List<T> itemList, final int startIndex) {
			super();
			this.startIndex = startIndex;
			this.itemList = itemList;
			if (this.startIndex >= this.itemList.size() && (this.itemList.size() - 1 >= 0)) {
				this.startIndex = this.itemList.size() - 1;
			}
			this.updateFlags();
		}

		/**
		 * Get the current page size.
		 *
		 * @return the pageSize
		 */
		public int getPageSize() {
			return PaginationInfo.getInstance().getPagination();
		}

		/**
		 * Get the index of the first item on the page.
		 *
		 * @return start index
		 */
		public int getStartIndex() {
			return this.startIndex;
		}

		/**
		 * Set start index for the page.
		 *
		 * @param startIndex start index
		 */
		public void setStartIndex(final int startIndex) {
			this.startIndex = startIndex;
			this.updateFlags();
		}

		/**
		 * Get total item found.
		 *
		 * @return items found quantity
		 */
		public int getTotalItemsFound() {
			if (this.itemList == null) {
				return 0;
			}
			return this.itemList.size();
		}

		private void updateFlags() {

			if (this.getTotalItemsFound() <= 0) {
				firstActionEnabled = false;
				previousActionEnabled = false;
				nextActionEnabled = false;
				lastActionEnabled = false;
			} else {
				if (this.getStartIndex() <= 0) {
					firstActionEnabled = false;
					previousActionEnabled = false;
				} else {
					firstActionEnabled = true;
					previousActionEnabled = true;
				}
				if (this.getTotalItemsFound() > this.getStartIndex() + getPageSize()) {
					nextActionEnabled = true;
					lastActionEnabled = true;
				} else {
					nextActionEnabled = false;
					lastActionEnabled = false;
				}
			}
		}

		/**
		 * Get the items for the page.
		 *
		 * @return items array
		 */
		public Object[] getPageItems() {
			List<?> result = this.itemList.subList(this.getStartIndex(), Math.min(this.getStartIndex() + getPageSize(), this.getTotalItemsFound()));
			if (result == null) {
				return new Object[0];
			}
			return result.toArray();
		}

		/**
		 * Get the status for the navigation type.
		 *
		 * @param navigationType navigation type of control
		 * @return is enabled the ui control
		 */
		public boolean getStatusForNovigationType(final NavigationType navigationType) {
			updateFlags();
			boolean result = false;
			switch (navigationType) {
				case FIRST:
					result = this.firstActionEnabled;
					break;
				case PREVIOUS:
					result = this.previousActionEnabled;
					break;
				case NEXT:
					result = this.nextActionEnabled;
					break;
				case LAST:
					result = this.lastActionEnabled;
					break;
				default:
			}
			return result;
		}

		/**
		 * Do action for navigation event.
		 *
		 * @param navigationType navigation type
		 * @param args           the arguments
		 */
		public void goNavigationType(final NavigationType navigationType, final Object[] args) {
			switch (navigationType) {
				case FIRST:
					goFirst();
					break;
				case PREVIOUS:
					goPrevious();
					break;
				case NEXT:
					goNext();
					break;
				case LAST:
					goLast();
					break;
				case TO:
					int toPage = (Integer) args[0];
					gotoSpecificPage(toPage);
					break;
				default:
			}
			this.updateFlags();
		}

		/**
		 * Go first page.
		 */
		public void goFirst() {
			this.startIndex = 0;
		}

		/**
		 * Go previous page.
		 */
		public void goPrevious() {
			if (startIndex > getPageSize()) {
				this.startIndex = startIndex - getPageSize();
			} else {
				this.startIndex = 0;
			}
		}

		/**
		 * Go next page.
		 */
		public void goNext() {
			if (startIndex + getPageSize() >= getTotalItemsFound()) {
				this.goLast();
			} else {
				this.startIndex = startIndex + getPageSize();
			}
		}

		/**
		 * Go last page.
		 */
		public void goLast() {
			int pageSize = getPageSize();
			this.startIndex = getTotalItemsFound() / pageSize * pageSize;
			if (this.startIndex == getTotalItemsFound()) {
				this.startIndex -= pageSize;
			}
			if (this.startIndex < 0) {
				this.startIndex = 0;
			}
		}

		/**
		 * Go to specific page.
		 *
		 * @param page the page to go
		 */
		public void gotoSpecificPage(final int page) {
			this.startIndex = (page - 1) * getPageSize();
		}

		/**
		 * @return the itemList
		 */
		public List<T> getItemList() {
			return itemList;
		}
	}

	/**
	 * Set search result.
	 *
	 * @param searchResultEvent the search event.
	 */
	public void setSearchResultEvent(final SearchResultEvent<T> searchResultEvent) {
		if (null == this.pageNavigator || searchResultEvent.isStartFromFirstPage()) {
			this.pageNavigator = new PageNavigator(searchResultEvent.getItems());
		} else {
			this.pageNavigator = new PageNavigator(searchResultEvent.getItems(), this.pageNavigator.getStartIndex());
		}
		this.firePropertyChange(PROPERTY_MODEL);
		if (this.paginationControl != null) {
			paginationControl.updateNavigationComponents();
		}
	}

	/**
	 * @return the viewer
	 */
	protected TableViewer getViewer() {
		return viewer;
	}

	/**
	 * @return the pageNavigator
	 */
	protected PageNavigator getPageNavigator() {
		return pageNavigator;
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
	 * Handles a fired pagination change with the new value.
	 *
	 * @param newValue the new pagination setting
	 */
	public void paginationChange(final int newValue) {
		if (pageNavigator != null) {
			pageNavigator.goFirst();
			viewer.setInput(this.pageNavigator.getPageItems());
			firePropertyChange(PROPERTY_MODEL);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		CoreEventService.getInstance().removePaginationListener(this);
	}

	@Override
	public int getResultsCount() {
		if (pageNavigator == null) {
			return 0;
		}
		return pageNavigator.getTotalItemsFound();
	}

	@Override
	public int getResultsStartIndex() {
		if (pageNavigator == null) {
			return 0;
		}
		return pageNavigator.getStartIndex();
	}

	@Override
	public void fireNavigationEvent(final NavigationType navigationType, final Object[] args) {
		pageNavigator.goNavigationType(navigationType, args);
		this.firePropertyChange(PROPERTY_MODEL);
		paginationControl.updateNavigationComponents();
	}

	/**
	 * @return the paginationControl
	 */
	protected PaginationContributionControl getPaginationControl() {
		return paginationControl;
	}

	/**
	 * set the paginationControl.
	 *
	 * @param paginationControl the pagination control
	 */
	protected void setPaginationControl(final PaginationContributionControl paginationControl) {
		this.paginationControl = paginationControl;
	}

	/**
	 * Find table extenders.
	 *
	 * @param tableName the table name.
	 * @param pluginId the plugin id.
	 * @return list of table extenders.
	 */
	protected List<EPTableColumnCreator> findTableExtenders(final String tableName, final String pluginId) {
		return PluginHelper.findTables(tableName, pluginId);
	}

}
