/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.cmclient.store.promotions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.promotions.CouponCollectionModel;
import com.elasticpath.cmclient.core.promotions.ValidationState;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpFinderDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.store.promotions.event.CouponEventListener;
import com.elasticpath.cmclient.store.promotions.event.PromotionsEventService;
import com.elasticpath.common.dto.CouponModelDto;
import com.elasticpath.common.dto.CouponUsageModelDto;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.CouponUsageType;

/**
 * This class provides the presentation of coupon dialog window.
 */
@SuppressWarnings({"PMD.GodClass"})
public class CouponEditorDialog extends AbstractEpFinderDialog {

	private static final int DIALOG_COLUMN_COUNT = 3;
	private static final String COUPON_TABLE = "Coupon"; //$NON-NLS-1$
	private static final int EMPTY_COMPOSITES = 4;

	private final CouponCollectionModel model;

	/**
	 * This label provider returns the text displayed in each column for a given <code>CouponUsage</code> object. This also
	 * determines the icon to be displayed for each corresponding product at the first column.
	 */
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		private static final int COLUMN_INDEX_COUPON_CODE = 0;
		private static final int COLUMN_INDEX_EMAIL = 1;

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			// no column image is required for the table.
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final CouponModelDto couponUsageModel = (CouponModelDto) element;

			switch (columnIndex) {
				case COLUMN_INDEX_COUPON_CODE:
					return couponUsageModel.getCouponCode();
				case COLUMN_INDEX_EMAIL:
					return ((CouponUsageModelDto) couponUsageModel).getEmailAddress();
				default:
					return CoreMessages.get().NotAvailable;
			}
		}
	}

	/**
	 * The content provider class is responsible for providing objects to the view. It wraps existing objects in adapters or
	 * simply return objects as-is. These objects may be sensitive to the current input of the view, or ignore it and always show
	 * the same content.
	 */
	class ViewContentProvider implements IStructuredContentProvider, CouponEventListener {
		private int tableSortDirection = SWT.UP;
		private Comparator<CouponModelDto> currentComparator;

		/**
		 * Listens for clicks on the column header and translates them into requests for sorting.
		 */
		class SortListener implements Listener {
			private final Table table;

			private final Map<TableColumn, Comparator<CouponModelDto>> columnComparatorMap =
					new HashMap<>();

			/**
			 * Normal constructor.
			 *
			 * @param table The table we work on.
			 */
			SortListener(final Table table) {
				this.table = table;
				currentComparator = new CouponUsageModelDto.CodeComparator();
			}

			/**
			 * Called when a column header is clicked. Performs the sort.
			 *
			 * @param event The event object.
			 */
			@Override
			public void handleEvent(final Event event) {
				final TableColumn eventTableColumn = (TableColumn) event.widget;

				sortBy(eventTableColumn);
			}

			/**
			 * Sorts the model by the selected column. Note that for the sort to work
			 * {@code linkColumnToComparator} must have been called for {@code tableColumn}.
			 *
			 * @param tableColumn the column to sort by.
			 */
			void sortBy(final TableColumn tableColumn) {
				final Comparator<CouponModelDto> comparator = columnComparatorMap.get(tableColumn);

				if (comparator != null) {
					table.setRedraw(false);
					if (tableColumn.equals(table.getSortColumn())) {
						final int currentSortDirection = table.getSortDirection();
						if (currentSortDirection == SWT.UP) {
							tableSortDirection = SWT.DOWN;
							table.setSortDirection(SWT.DOWN);
						} else {
							tableSortDirection = SWT.UP;
							table.setSortDirection(SWT.UP);
						}
					} else {
						tableSortDirection = SWT.UP;
						table.setSortDirection(SWT.UP);
					}
					table.setSortColumn(tableColumn);
					currentComparator = comparator;

					model.sort(tableSortDirection, currentComparator);

					refreshTableViewerInput();
					table.setRedraw(true);
				}
			}

			/**
			 * Links a table column with a comparator so that an event on the table column
			 * will use the comparator.
			 *
			 * @param swtTableColumn The column.
			 * @param comparator     The comparator.
			 */
			public void linkColumnToComparator(final TableColumn swtTableColumn,
											   final Comparator<CouponModelDto> comparator) {
				columnComparatorMap.put(swtTableColumn, comparator);
			}

			/**
			 * Get the table column associated with the given comparator.
			 *
			 * @param comparator the comparator to find the column for
			 * @return the table column
			 */
			public TableColumn getColumnForComparator(final Comparator<CouponModelDto> comparator) {
				for (Map.Entry<TableColumn, Comparator<CouponModelDto>> entry : columnComparatorMap.entrySet()) {
					if (comparator.getClass().equals(entry.getValue().getClass())) {
						return entry.getKey();
					}
				}
				return null;
			}

		}

		/**
		 * The default constructor.
		 */
		ViewContentProvider() {
			PromotionsEventService.getInstance().registerCouponEventListener(this);
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// Do nothing
		}

		@Override
		public void dispose() {
			PromotionsEventService.getInstance().unregisterCouponListener(this);
		}

		/**
		 * Called when the coupon list is returned from the database or when the paging changes.
		 *
		 * @param event The event object.
		 */
		@Override
		public void searchResultsUpdate(final SearchResultEvent<CouponUsage> event) {
			//No action yet.
		}

		/**
		 * Returns the specified page of data.
		 *
		 * @return The items on this page.
		 */
		private Object[] getTableViewerInputPaged() {
			List<CouponModelDto> couponUsageList = new ArrayList<>(getResultsPaging());

			int itemCount = 0;

			for (CouponModelDto couponUsageModel : model.getObjects()) {
				if (itemCount >= getResultsStartIndex() && itemCount < getResultsStartIndex() + getResultsPaging()) {  // make pagination work
					couponUsageList.add(couponUsageModel);
				}
				itemCount++;
			}
			return couponUsageList.toArray();
		}

		@Override
		public Object[] getElements(final Object inputElement) {
			if (inputElement instanceof Object[]) {
				LOG.debug("TableViewer input set to array of Objects"); //$NON-NLS-1$
				return (Object[]) inputElement;
			}
			return new Object[0];
		}

		/**
		 * Called after a change has been made to the model.
		 */
		void refreshTableViewerInput() {
			getResultTableViewer().setInput(getTableViewerInputPaged());
			setResultsCount(model.getObjects().size());
			updateNavigationComponents();
		}
	}

	private Button addButton;

	private Button deleteButton;

	private final boolean enableNavigation;

	private ViewContentProvider.SortListener columnSortListener;

	private ViewContentProvider contentProvider;

	private final CouponUsageType couponUsageType;

	private IEpTableColumn codeColumn;

	/**
	 * @param parentShell                the parent shell of this dialog
	 * @param couponUsageCollectionModel The model which represents coupons - output from this dialog box.
	 * @param couponUsageType            coupon usage type
	 */
	public CouponEditorDialog(final Shell parentShell, final CouponCollectionModel couponUsageCollectionModel,
							  final CouponUsageType couponUsageType) {
		super(parentShell, DIALOG_COLUMN_COUNT);
		enableNavigation = true;
		this.model = couponUsageCollectionModel;
		this.couponUsageType = couponUsageType;
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final int couponCodeColumn = 100;
		final int emailColumnWidth = 300;
		final int tableHeight = 350;

		// Product table tab
		final IEpLayoutData mainCompositeData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		final IEpLayoutComposite mainComposite = dialogComposite.addGridLayoutComposite(2, false, mainCompositeData);

		IEpLayoutComposite resultPanelComposite  = createResultPaneComposite(mainComposite);
		if (enableNavigation) {
			createPaginationCompositeControl(resultPanelComposite);
			getNavigationService().registerNavigationEventListener(this);
		}
		createErrorMessageControl(resultPanelComposite);

		final IEpLayoutData resultPanelLayoutData = resultPanelComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		createTableViewControl(resultPanelComposite, resultPanelLayoutData, COUPON_TABLE);

		// Setup sort columns
		contentProvider = new ViewContentProvider();
		columnSortListener = contentProvider.new SortListener(getResultTableViewer().getSwtTable());

		codeColumn = getResultTableViewer().addTableColumn(PromotionsMessages.get().CouponEditorDialog_Code, couponCodeColumn);
		final TableColumn codeSwtColumn = codeColumn.getSwtTableColumn();
		columnSortListener.linkColumnToComparator(codeSwtColumn, new CouponUsageModelDto.CodeComparator());
		codeSwtColumn.addListener(SWT.Selection, columnSortListener);

		if (isEmailNeeded()) {
			IEpTableColumn emailColumn = getResultTableViewer().addTableColumn(PromotionsMessages.get().Coupon_Email, emailColumnWidth);
			final TableColumn emailSwtColumn = emailColumn.getSwtTableColumn();
			columnSortListener.linkColumnToComparator(emailSwtColumn, new CouponUsageModelDto.EmailComparator());
			emailSwtColumn.addListener(SWT.Selection, columnSortListener);
		}

		((GridData) getResultTableViewer().getSwtTable().getLayoutData()).heightHint = tableHeight;

		final IEpLayoutData rightFieldData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutComposite rightPaneComposite = mainComposite.addGridLayoutComposite(1, false, rightFieldData);

		final IEpLayoutComposite rightButtonsComposite = rightPaneComposite.addGridLayoutComposite(1, true, dialogComposite.createLayoutData(
				IEpLayoutData.END, IEpLayoutData.FILL));

		//Adding space to align the buttons
		for (int i = 0; i < EMPTY_COMPOSITES; i++) {
			rightButtonsComposite.addGridLayoutComposite(1, false, rightFieldData);
		}

		addButton = rightButtonsComposite.addPushButton(PromotionsMessages.get().CouponEditorDialog_Add, EpState.EDITABLE, null);
		addButton.addSelectionListener(getAddAction());

		deleteButton = rightButtonsComposite.addPushButton(PromotionsMessages.get().CouponEditorDialog_Delete, EpState.EDITABLE, null);
		deleteButton.addSelectionListener(getDeleteAction());
		deleteButton.setEnabled(false);
	}

	@Override
	protected IEpLayoutComposite createResultPaneComposite(final IEpLayoutComposite mainComposite) {
		IEpLayoutData resultPanelLayoutData  = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 1, 1);
		IEpLayoutComposite resultPaneComposite = mainComposite.addGridLayoutComposite(1, false, resultPanelLayoutData);
		setResultPaneComposite(resultPaneComposite);
		return resultPaneComposite;
	}

	private boolean isEmailNeeded() {
		return CouponUsageType.LIMIT_PER_SPECIFIED_USER.equals(this.couponUsageType);
	}

	private SelectionAdapter getAddAction() {
		return new SelectionAdapter() {
			private boolean firstTime = true;

			@Override
			public void widgetSelected(final SelectionEvent event) {
				CouponModelDto couponUsageModel = createCouponModelDto();

				CouponEditingPopupDialog dialog = new CouponEditingPopupDialog(Arrays.asList(couponUsageModel), model);
				if (dialog.open() == Window.OK) {
					model.add(couponUsageModel);
					contentProvider.refreshTableViewerInput();
					if (firstTime) {
						// After the add the sort arrow went away so this makes sure it remains
						getResultTableViewer().getSwtTable().setSortDirection(SWT.UP);
						firstTime = false;
					}
				}
			}

			private CouponModelDto createCouponModelDto() {
				if (isEmailNeeded()) {
					return new CouponUsageModelDto();
				}

				return new CouponModelDto();
			}
		};
	}

	private SelectionAdapter getDeleteAction() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				IStructuredSelection selection = (IStructuredSelection) getResultTableViewer().getSwtTableViewer().getSelection();
				CouponModelDto couponUsageModel = (CouponModelDto) selection.getFirstElement();
				int status = new CouponRemoveDialog(null, couponUsageModel).open();
				if (status == Window.OK) {
					model.delete(couponUsageModel);
					contentProvider.refreshTableViewerInput();
				}
			}
		};
	}

	@Override
	public void populateControls() {

		getResultTableViewer().getSwtTableViewer().addSelectionChangedListener(this);
		getResultTableViewer().setContentProvider(contentProvider);
		getResultTableViewer().setLabelProvider(new ViewLabelProvider());

		contentProvider.refreshTableViewerInput();

	}

	@Override
	protected String getInitialMessage() {
		return PromotionsMessages.get().CouponEditorDialog_InitialMessage;
	}

	@Override
	protected String getTitle() {
		return PromotionsMessages.get().CouponEditorDialog_Title;
	}

	@Override
	protected String getWindowTitle() {
		return PromotionsMessages.get().CouponEditorDialog_WindowTitle;
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		// final Object selectedItem = ((IStructuredSelection) event.getSelection()).getFirstElement();
		// So the delete button only gets enabled when something is selected.
		if (event.getSelection().isEmpty()) {
			deleteButton.setEnabled(false);
		} else {
			deleteButton.setEnabled(true);
		}

	}

	@Override
	protected Image getWindowImage() {
		return null;
	}

	@Override
	public Object getModel() {
		return null;
	}

	@Override
	protected void bindControls() {
		// Enable the OK button
		setComplete(true);

		// Default sort on open.
		if (model.getComparator() == null) {
			getResultTableViewer().getSwtTable().setSortDirection(SWT.UP);
			columnSortListener.sortBy(codeColumn.getSwtTableColumn());
		} else {
			getResultTableViewer().getSwtTable().setSortDirection(model.getSortDirection());
			columnSortListener.sortBy(columnSortListener.getColumnForComparator(model.getComparator()));
		}
	}

	@Override
	protected void doSearch() {
		contentProvider.refreshTableViewerInput();
	}

	@Override
	protected String getMsgForNoResultFound() {
		return PromotionsMessages.get().CouponEditorDialog_NoResultsFound;
	}

	@Override
	protected void clearFields() {
		// Nothing required
	}

	@Override
	protected void okPressed() {
		ValidationState validationResult = model.getCouponValidator().isBatchValid(getCouponCodesToAdd());
		if (!validationResult.isValid()) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), CoreMessages.get().ApplicationWorkbenchAdvisor_Error_Title,

					NLS.bind(PromotionsMessages.get().CouponCodesAlreadyExist,
					validationResult.getDuplicates()));
			return;
		}
		super.okPressed();
	}

	private Collection<String> getCouponCodesToAdd() {
		Collection<CouponModelDto> couponUsagesToAdd = model.getObjectsToAdd();
		Collection<String> couponCodesToAdd = new ArrayList<>();
		for (CouponModelDto couponUsageModel : couponUsagesToAdd) {
			couponCodesToAdd.add(couponUsageModel.getCouponCode());
		}
		return couponCodesToAdd;
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent arg0) {
		// Nothing required
	}

	@Override
	public void widgetSelected(final SelectionEvent arg0) {
		// Nothing required
	}
}