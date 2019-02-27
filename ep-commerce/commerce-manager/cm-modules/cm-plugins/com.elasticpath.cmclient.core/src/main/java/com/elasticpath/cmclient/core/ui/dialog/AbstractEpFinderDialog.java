/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui.dialog;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.common.IPaginationControl;
import com.elasticpath.cmclient.core.common.PaginationCompositeControl;
import com.elasticpath.cmclient.core.common.PaginationContributionControl;
import com.elasticpath.cmclient.core.event.NavigationEvent;
import com.elasticpath.cmclient.core.event.NavigationEvent.NavigationType;
import com.elasticpath.cmclient.core.event.NavigationEventListener;
import com.elasticpath.cmclient.core.event.NavigationEventService;
import com.elasticpath.cmclient.core.helpers.PaginationChangeListener;
import com.elasticpath.cmclient.core.helpers.PaginationSupport;
import com.elasticpath.cmclient.core.pagination.PaginationInfo;
import com.elasticpath.cmclient.core.service.CoreEventService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.domain.catalog.Product;

/**
 * This abstract class for EP Finder dialog pages. Other finder dialog can extend this class to implement dialogs. <br>
 * It has implementation for page navigation and table viewer.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.GodClass" })
public abstract class AbstractEpFinderDialog extends AbstractEpDialog implements SelectionListener, ISelectionChangedListener,
		NavigationEventListener, FocusListener, PaginationChangeListener, PaginationSupport {

	/**
	 * The logger.
	 */
	protected static final Logger LOG = Logger.getLogger(AbstractEpFinderDialog.class);

	private IEpTableViewer resultTableViewer;

	private IEpLayoutComposite resultPaneComposite;

	private int resultsCount;

	private int resultsStartIndex;

	private IPaginationControl paginationControl;

	private Object selectedObject;

	private NavigationEventService navigationService;

	private ExpandableComposite errorComposite;

	private Label errorLabel;

	private AggregateValidationStatus aggregateStatus;

	private IStatus currentStatus;

	private static final int MARGIN_BOTTOM = 5;

	private boolean noDataAvailable;

	private static final int TABLE_HEIGHT_MINIMUM = 225;

	private String errorMessage = CoreMessages.get().Dialog_NoneCatalogAssignedToCurrentUser;

	/**
	 * Is no data available.
	 *
	 * @return true if no data is available
	 */
	public boolean isNoDataAvailable() {
		return noDataAvailable;
	}

	/**
	 * Sets the value to no data is available.
	 *
	 * @param noDataAvailable the value of no data available
	 */
	public void setNoDataAvailable(final boolean noDataAvailable) {
		this.noDataAvailable = noDataAvailable;
	}

	@Override
	public int open() {
		if (isNoDataAvailable()) {
			MessageDialog.openError(null, CoreMessages.get().ApplicationWorkbenchAdvisor_Error_Title,
				NLS.bind(errorMessage,
				null));
			return Window.CANCEL;
		}
		return super.open();
	}

	@Override
	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @param parentShell the parent shell of this dialog
	 */
	public AbstractEpFinderDialog(final Shell parentShell) {
		this(parentShell, 2);
	}

	/**
	 * @param parentShell the parent shell of this dialog
	 * @param columnCount The number of columns in the main grid layout
	 */
	public AbstractEpFinderDialog(final Shell parentShell, final int columnCount) {
		super(parentShell, columnCount, false);
		CoreEventService.getInstance().addPaginationListener(this);
	}

	/**
	 * @return the navigation service object.
	 */
	protected NavigationEventService getNavigationService() {
		if (navigationService == null) {
			navigationService = new NavigationEventService();
		}
		return navigationService;
	}

	/**
	 * @param parent the tab's composite passed.
	 */
	protected void createErrorMessageControl(final IEpLayoutComposite parent) {
		GridLayout gridLayout = (GridLayout) parent.getSwtComposite().getLayout();
		gridLayout.marginHeight = 1;
		gridLayout.marginWidth = 1;
		gridLayout.verticalSpacing = 0;

		final IEpLayoutComposite epErrorComposite = parent.addExpandableComposite(2,
				false,
				null,
				parent.createLayoutData(IEpLayoutData.CENTER, IEpLayoutData.FILL));
		errorComposite = (ExpandableComposite) epErrorComposite.getSwtComposite().getParent();
		epErrorComposite.addImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_WARNING_SMALL),
				epErrorComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL));
		errorLabel = epErrorComposite.addLabel("", epErrorComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER)); //$NON-NLS-1$

		gridLayout = (GridLayout) epErrorComposite.getSwtComposite().getLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginBottom = MARGIN_BOTTOM;
		gridLayout.verticalSpacing = 0;

		hideErrorMessage();
	}

	/**
	 * @param event the SelectionChangedEvent object
	 */
	public void selectionChanged(final SelectionChangedEvent event) {
		final Object selectedItem = ((IStructuredSelection) event.getSelection()).getFirstElement();
		if (selectedItem instanceof Product) {
			setMessage(getInitialMessage());
			setErrorMessage(null);
			getResultPaneComposite().getSwtComposite().layout();
		}
	}

	/**
	 * Set the number of results that the view could show if there were no paging.
	 *
	 * @param resultsCount the resultsCount
	 */
	protected void setResultsCount(final int resultsCount) {
		this.resultsCount = resultsCount;
	}

	/**
	 * Updates the status of the navigation components, enabling and disabling navigation buttons and triggering the results label text update.
	 */
	protected void updateNavigationComponents() {
		if (paginationControl != null) {
			paginationControl.updateNavigationComponents();
			getResultPaneComposite().getSwtComposite().layout();
		}
	}

	/**
	 * Go to the first page.
	 */
	protected void navigateFirst() {
		resultsStartIndex = 0;
		doSearch();
	}

	/**
	 * Go to the last page.
	 */
	protected void navigateLast() {
		resultsStartIndex = resultsCount / getResultsPaging() * getResultsPaging();
		if (resultsStartIndex == resultsCount && resultsStartIndex - getResultsPaging() > 0) {
			resultsStartIndex -= getResultsPaging();
		}
		doSearch();
	}

	/**
	 * Go to the next page.
	 */
	protected void navigateNext() {
		resultsStartIndex += getResultsPaging();
		doSearch();
	}

	/**
	 * Go to the previous page.
	 */
	protected void navigatePrevious() {
		resultsStartIndex = Math.max(0, resultsStartIndex - getResultsPaging());
		doSearch();
	}

	/**
	 * Go to the specific page.
	 *
	 * @param pageNumber the page number
	 */
	protected void navigateTo(final int pageNumber) {
		resultsStartIndex = (pageNumber - 1) * getResultsPaging();
		doSearch();
	}

	/**
	 * Get the selectedObject.
	 *
	 * @return the selectedObject.
	 */
	public Object getSelectedObject() {
		return selectedObject;
	}

	/**
	 * Set the selectedObject.
	 *
	 * @param selection the selected object of the table.
	 */
	public void setSelectedObject(final Object selection) {
		selectedObject = selection;
	}

	/**
	 * Getter of the resultsCount.
	 *
	 * @return the number of returned research results entries.
	 */
	public int getResultsCount() {
		return resultsCount;
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
	 * Setter of the startIndex.
	 *
	 * @param startIndex the start index of the search request.
	 */
	protected void setResultsStartIndex(final int startIndex) {
		resultsStartIndex = startIndex;
	}

	/**
	 * Setter of the resultsStartIndex.
	 *
	 * @return the start index of the search request.
	 */
	public int getResultsStartIndex() {
		return resultsStartIndex;
	}

	@Override
	public void fireNavigationEvent(final NavigationType navigationType, final Object[] args) {
		getNavigationService().fireNavigationEvent(new NavigationEvent(getResultTableViewer(), navigationType, args));
	}

	/**
	 * Creates the view toolbar navigation components.
	 *
	 * @param resultPaneComposite the result pane composite
	 */
	protected void createPaginationCompositeControl(final IEpLayoutComposite resultPaneComposite) {
		paginationControl = new PaginationCompositeControl(resultPaneComposite, this, PaginationCompositeControl.PaginationControlAlignment.CENTER);
		paginationControl.createViewPartControl();
		updateNavigationComponents();
	}

	/**
	 * Creates the view toolbar navigation components.
	 *
	 * @param resultPaneComposite the result pane composite
	 * @param alignment           the pagination control alignment
	 */
	protected void createPaginationCompositeControl(final IEpLayoutComposite resultPaneComposite,
													final PaginationCompositeControl.PaginationControlAlignment alignment) {
		paginationControl = new PaginationCompositeControl(resultPaneComposite, this, alignment);
		paginationControl.createViewPartControl();
		updateNavigationComponents();
	}

	/**
	 * Creates the view toolbar navigation components.
	 *
	 * @param toolBarManager the tool bar manager
	 */
	protected void createPaginationContributionControl(final IToolBarManager toolBarManager) {
		paginationControl = new PaginationContributionControl(toolBarManager, this);
		paginationControl.createViewPartControl();
		updateNavigationComponents();
	}

	/**
	 * Clears results count and results start index information and updates navigation componets.
	 */
	protected void clearNavigation() {
		resultsCount = 0;
		resultsStartIndex = 0;
		updateNavigationComponents();
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
	}

	@Override
	protected String getPluginId() {
		return CorePlugin.PLUGIN_ID;
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpButtonsForButtonsBar(ButtonsBarType.OK, parent);
		getOkButton().setEnabled(false);
	}

	/**
	 * Shows a message above the table within an expandable composite. The message needs to be hidden manually.
	 *
	 * @param message the message to be shown
	 */
	public void showMessage(final String message) {
		errorLabel.setText(message);
		errorComposite.setExpanded(true);
		getResultPaneComposite().getSwtComposite().layout();
	}

	/**
	 * Hides the message displayed above the table.
	 */
	public void hideErrorMessage() {
		if (!errorComposite.isDisposed() && !getResultPaneComposite().getSwtComposite().isDisposed()) {
			errorComposite.setExpanded(false);
			getResultPaneComposite().getSwtComposite().layout();
		}
	}

	/**
	 * Process the error message composite's visibility.
	 *
	 * @param message the message to be displayed.
	 */
	public void handleErrorMessage(final String message) {
		if (getResultsCount() == 0) {
			showMessage(message);
		} else {
			hideErrorMessage();
		}
	}

	/**
	 * Do nothing while focus lost.
	 *
	 * @param event the focus event.
	 */
	@Override
	public void focusLost(final FocusEvent event) {
		//do nothing
	}

	/**
	 * Disable the OK button while the user switch the focus to text input field.
	 *
	 * @param event the focus event.
	 */
	public void focusGained(final FocusEvent event) {
		getOkButton().setEnabled(false);
	}

	@Override
	public void paginationChange(final int newValue) {
		doSearch();
	}

	@Override
	public boolean close() {
		// need to remove the pagination listener on window close
		// which also includes window closure outside of a dialog button pressed.
		CoreEventService.getInstance().removePaginationListener(this);
		return super.close();
	}

	/**
	 * Handle validation status change but updating complete status on dialog according to validation result status.
	 *
	 * @param control the control to be binded to the binding context.
	 */
	protected void handleStatusChanged(final Control control) {
		if ((currentStatus != null) && (currentStatus.getSeverity() == IStatus.ERROR)) {
			control.setEnabled(false);
		} else {
			control.setEnabled(true);
		}
	}

	/**
	 * Initialize the aggregate validation status and add a listener to the status so that it can update the dialog complete state appropriately upon
	 * validation result change.
	 *
	 * @param bindingContext the DataBindingContext the control to be binded to.
	 * @param control the control to be binded.
	 */
	protected void createBinding(final DataBindingContext bindingContext, final Control control) {
		aggregateStatus = new AggregateValidationStatus(bindingContext.getBindings(), AggregateValidationStatus.MAX_SEVERITY);
		aggregateStatus.addValueChangeListener(new IValueChangeListener() {
			public void handleValueChange(final ValueChangeEvent event) {
				currentStatus = (IStatus) event.diff.getNewValue();
				handleStatusChanged(control);
			}
		});
		currentStatus = (IStatus) aggregateStatus.getValue();
		handleStatusChanged(control);
	}

	/**
	 * Disable all navigation action buttons.
	 */
	protected void disableButtons() {
		getOkButton().setEnabled(false);
	}

	/**
	 * Returns the result pane composite.
	 *
	 * @return the result pane composite
	 * */
	protected IEpLayoutComposite getResultPaneComposite() {
		return  resultPaneComposite;
	}

	/**
	 *	The search result table viewer.
	 *	You can subclass this method to return instead IEpTreeViewer
	 *
	 *	@return the table viewer
	 */
	protected IEpTableViewer getResultTableViewer() {
		return resultTableViewer;
	}

	@Override
	protected Image getWindowImage() {
		return null;
	}

	/**
	 * Creates the result panel. You can override this method to get a custom composite.
	 * <p>
	 * It usually contains the pagination control, error control, results table viewer and in some cases the price table viewer.
	 * <p>
	 * This panel will be updated when results change.
	 *
	 * @param mainComposite the main composite
	 * @return the result composite
	 */
	protected IEpLayoutComposite createResultPaneComposite(final IEpLayoutComposite mainComposite) {
		final IEpLayoutData resultPaneData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		resultPaneComposite = mainComposite.addGridLayoutComposite(1, false, resultPaneData);
		return resultPaneComposite;
	}

	/**
	 * Sets the result pane.
	 *
	 * @param resultPaneComposite the result pane
	 */
	protected void setResultPaneComposite(final IEpLayoutComposite resultPaneComposite) {
		this.resultPaneComposite = resultPaneComposite;
	}

	/**
	 * Creates the result table view component.
	 *
	 * @param resultPaneComposite the result pane composite
	 * @param layoutData          the grid layout data
	 * @param tableName           the table name
	 */
	protected void createTableViewControl(final IEpLayoutComposite resultPaneComposite, final IEpLayoutData layoutData,
													final String tableName) {
		resultTableViewer = resultPaneComposite.addTableViewer(false, EpControlFactory.EpState.READ_ONLY, layoutData, tableName);

		GridData resultTableViewerGridData = (GridData) resultTableViewer.getSwtTable().getLayoutData();
		resultTableViewerGridData.horizontalAlignment = SWT.FILL;
		resultTableViewerGridData.verticalAlignment = SWT.FILL;
		resultTableViewerGridData.grabExcessHorizontalSpace = true;
		resultTableViewerGridData.grabExcessVerticalSpace = false;
		resultTableViewerGridData.horizontalSpan = 1;
		resultTableViewerGridData.verticalSpan = 1;
		resultTableViewerGridData.heightHint = getResultTableHeight();
	}

	/**
	 * Returns the desired table height.
	 *
	 * @return the desired table height
	 */
	protected int getResultTableHeight() {
		return TABLE_HEIGHT_MINIMUM;
	}


	/**
	 * To initiate the index searching for the target.
	 */
	protected abstract void doSearch();

	/**
	 * Return the message to be displayed in front of the results sets while no results found for the search.
	 *
	 * @return the message string to be displayed while no matched results found.
	 */
	protected abstract String getMsgForNoResultFound();

	/**
	 * Gets the model object.
	 *
	 * @return model object
	 */
	public abstract Object getModel();

	/**
	 * Clear all search fields.
	 */
	protected abstract void clearFields();
}
