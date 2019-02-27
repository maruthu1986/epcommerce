/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.editors.actions.OpenNoteContributionAction;
import com.elasticpath.cmclient.fulfillment.editors.actions.ViewAllNoteAction;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderEvent;

/**
 * Represents the UI for viewing the notes.
 */
public class OrderNoteNotesSectionPart extends AbstractCmClientEditorPageSectionPart implements SelectionListener, IPropertyListener {

	private static final int ORG_WIDTH = 135;
	private static final int DATE_WIDTH = 135;
	private static final int DESCR_WIDTH = 420;
	private static final int TABLE_HEIGHT_HINT = 263;
	private static final int TABLE_WIDTH_HINT = 1200;
	private static final int FILTER_GROUP_COLUMNS = 5;
	private static final int LABEL_IDENT = 20;

	private static final String ORDER_NOTES_TABLE = "Order Notes Table";

	private Button openNote;

	private Button viewAll;

	private final Set<OrderEvent> orderEvents;

	private OrderEvent orderEvent;

	private EpState authorization;

	private TableViewer tableViewer;

	private final ControlModificationListener controlModificationListener;

	private OpenNoteContributionAction openNoteAction;
	
	private ViewAllNoteAction viewAllNoteAction;
	
	private CCombo noteTypeComboBox;

	private CCombo origComboBox;

	private OrderEventFilter filter;
	
	private final OrderEditor editor;
	
	/**
	 * Constructor.
	 * 
	 * @param formPage the FormPage
	 * @param editor the AbstractCmClientFormEditor
	 */
	public OrderNoteNotesSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR);
		this.orderEvents = ((Order) editor.getModel()).getOrderEvents();
		this.controlModificationListener = editor;
		this.editor = (OrderEditor) editor;
		editor.addPropertyListener(this);
		if (((OrderEditor) getEditor()).isAuthorizedAndNotLocked()) {
			authorization = EpState.EDITABLE;
		} else {
			authorization = EpState.READ_ONLY;
		}
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// nothing

	}

	@Override
	protected void createControls(final Composite parentComposite, final FormToolkit toolkit) {
		final IEpLayoutComposite mainPane = CompositeFactory.createTableWrapLayoutComposite(parentComposite, 1, false);
		mainPane.setControlModificationListener(this.controlModificationListener);
		final TableWrapData mainPaneWrapData = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
		mainPaneWrapData.grabHorizontal = true;
		mainPane.setLayoutData(mainPaneWrapData);

		//Filters Composite
		createFiltersComposite(mainPane);

		//CTable Composite
		createTableComposite(mainPane);
	}

	private void createFiltersComposite(final IEpLayoutComposite mainPane) {
		IEpLayoutComposite filterGroupComposite = mainPane.addGridLayoutComposite(FILTER_GROUP_COLUMNS, false,
				mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL));

		Label notesLabel = filterGroupComposite.addLabelBold(FulfillmentMessages.get().Event_Filters,
				filterGroupComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL));
		FontData fontData = notesLabel.getFont().getFontData()[0];
		Font font = new Font(Display.getCurrent(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
		notesLabel.setFont(font);

		GridData notesLabelGridData = new GridData();
		notesLabelGridData.verticalAlignment = SWT.CENTER;
		notesLabel.setLayoutData(notesLabelGridData);

		Label noteTypeLabel = filterGroupComposite.addLabelBold(FulfillmentMessages.get().OrderNoteFilter_Label_Note_Type,
				filterGroupComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL));
		GridData noteTypeLabelGridData = new GridData();
		noteTypeLabelGridData.horizontalIndent = LABEL_IDENT;
		noteTypeLabel.setLayoutData(noteTypeLabelGridData);

		this.noteTypeComboBox = filterGroupComposite.addComboBox(EpControlFactory.EpState.EDITABLE,
				filterGroupComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL));
		this.noteTypeComboBox.addSelectionListener(this);

		Label originateLabel = filterGroupComposite.addLabelBold(FulfillmentMessages.get().OrderNote_Label_Originate,
				filterGroupComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL));
		GridData originateLabelGridData = new GridData();
		originateLabelGridData.horizontalIndent = LABEL_IDENT;
		originateLabel.setLayoutData(originateLabelGridData);

		this.origComboBox = filterGroupComposite.addComboBox(EpControlFactory.EpState.EDITABLE,
				filterGroupComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL));
		this.origComboBox.addSelectionListener(this);
	}

	private void createTableComposite(final IEpLayoutComposite mainPane) {
		IEpLayoutComposite tableComposite = mainPane.addGridLayoutComposite(2, false,
				mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL));
		final IEpTableViewer epTableViewer = tableComposite.addTableViewer(false, authorization,
				mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false), ORDER_NOTES_TABLE);

		GridData tableGridData = (GridData) epTableViewer.getSwtTable().getLayoutData();
		tableGridData.heightHint = TABLE_HEIGHT_HINT;
		tableGridData.widthHint = TABLE_WIDTH_HINT;
		epTableViewer.addTableColumn(FulfillmentMessages.get().OrderNote_Label_Originate, ORG_WIDTH);
		epTableViewer.addTableColumn(FulfillmentMessages.get().OrderSection_Date, DATE_WIDTH);
		epTableViewer.addTableColumn(FulfillmentMessages.get().OrderNoteNotes_Description, DESCR_WIDTH);

		epTableViewer.setLabelProvider(new OrderNoteLabelProvider());
		epTableViewer.setContentProvider(new OrderNoteContentProvider());
		epTableViewer.setInput(this.orderEvents);

		epTableViewer.getSwtTableViewer().addSelectionChangedListener(event -> {
			if (event.getSelection() instanceof IStructuredSelection) {
				if (event.getSelection().isEmpty()) {
					openNote.setEnabled(false);
					return;
				}
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				OrderNoteNotesSectionPart.this.orderEvent = (OrderEvent) selection.getFirstElement();
				openNote.setEnabled(true);

				OrderNoteNotesSectionPart.this.openNoteAction = new OpenNoteContributionAction(
						OrderNoteNotesSectionPart.this.orderEvent, OrderNoteNotesSectionPart.this);
			}
		});
		epTableViewer.getSwtTableViewer().setSorter(new ViewerSorter() {
			@Override
			public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
				return ((OrderEvent) obj2).getCreatedDate().compareTo(((OrderEvent) obj1).getCreatedDate());
			}
		});
		epTableViewer.getSwtTableViewer().addDoubleClickListener(event -> {
			if (OrderNoteNotesSectionPart.this.authorization == EpState.EDITABLE) {
				OrderNoteNotesSectionPart.this.orderEvent =
						(OrderEvent) ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (OrderEventCmHelper.isCmUserGeneratedEvent(OrderNoteNotesSectionPart.this.orderEvent)) {
					OrderNoteNotesSectionPart.this.openNoteAction.run();
				}
			}
		});
		this.tableViewer = epTableViewer.getSwtTableViewer();
		this.filter = new OrderEventFilter();
		this.tableViewer.addFilter(filter);

		//Buttons composite
		final IEpLayoutComposite buttonsPane = tableComposite.addTableWrapLayoutComposite(1, true,
				tableComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL));
		this.openNote = buttonsPane.addPushButton(FulfillmentMessages.get().OrderNoteNotes_Button, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_EDIT_NOTE), authorization, mainPane.createLayoutData());
		this.openNote.setEnabled(false);

		this.viewAll = buttonsPane.addPushButton(FulfillmentMessages.get().OrderNoteNotes_ViewAllButton, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_VIEW_ALL_NOTES), EpState.EDITABLE, mainPane.createLayoutData());
		this.viewAllNoteAction = new ViewAllNoteAction(this.orderEvents);
	}

	@Override
	protected void populateControls() {
		this.openNote.addSelectionListener(this);
		this.viewAll.addSelectionListener(this);

		this.noteTypeComboBox.setItems(new String[] { FulfillmentMessages.get().Event_Type_All,
				FulfillmentMessages.get().Event_Type_CSR, FulfillmentMessages.get().Event_Type_System });
		noteTypeComboBox.select(0);
		noteTypeFilterChanged(0);
	}

	/**
	 * Note used.
	 * 
	 * @param event the selectionEvent
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// nothing
	}

	/**
	 * Invoked when Open Note is clicked.
	 * 
	 * @param event the selectionEvent
	 */
	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() == this.openNote) {
			this.openNoteAction.setViewOnly(authorization == EpState.READ_ONLY 
					|| OrderEventCmHelper.isSystemGeneratedEvent(orderEvent));
			this.openNoteAction.run();
		}
		if (event.getSource() == this.viewAll) {
			this.viewAllNoteAction.run();
		}

		if (event.getSource() == noteTypeComboBox) {
			noteTypeFilterChanged(noteTypeComboBox.getSelectionIndex());
		}
		if (event.getSource() == origComboBox) {
			noteOriginatorFilterChanged(origComboBox.getSelectionIndex());
		}
	}

	private void noteTypeFilterChanged(final int selection) {
		this.filter.setEventType(selection);
		refreshOriginatorComboBox();
	}

	private String[] populateOriginator() {
		final List<String> originator = new LinkedList<>();
		originator.add(FulfillmentMessages.get().Event_Originator_All);
		for (final OrderEvent orderEvent : orderEvents) {
			String createdBy = OrderEventCmHelper.getCreatedBy(orderEvent);
			if (filter.matchEventType(orderEvent) && !originator.contains(createdBy)) {
				originator.add(createdBy);
			}
		}
		return originator.toArray(new String[originator.size()]);
	}

	private void refreshOriginatorComboBox() {
		origComboBox.setItems(populateOriginator());
		origComboBox.select(0);
		noteOriginatorFilterChanged(0);
	}

	private void noteOriginatorFilterChanged(final int selection) {
		this.filter.setEventOriginator(origComboBox.getItem(selection));
		refreshNotes();
	}

	@Override
	protected String getSectionDescription() {
		return FulfillmentMessages.EMPTY_STRING;
	}

	@Override
	protected String getSectionTitle() {
		return FulfillmentMessages.EMPTY_STRING;
	}

	/**
	 * This label provider returns the text that should appear in each column for a given <code>OrderNote</code> object. This
	 * also determines the icon that should appear in the first column.
	 */
	class OrderNoteLabelProvider extends LabelProvider implements ITableLabelProvider {
		
		/**
		 * Gets the image for each column.
		 * 
		 * @param element the image
		 * @param columnIndex the index for the column
		 * @return Image the image
		 */
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		/**
		 * Gets the text for each column.
		 * 
		 * @param element the text
		 * @param columnIndex the index for the column
		 * @return String the text
		 */
		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final OrderEvent orderEvent = (OrderEvent) element;
			String columnToolTip = FulfillmentMessages.get().OrderNoteIcon_System;
			if (OrderEventCmHelper.isCmUserGeneratedEvent(orderEvent)) {
				columnToolTip = FulfillmentMessages.get().OrderNoteIcon_User;
			} 		
			switch (columnIndex) {
			case 0:
				if (OrderEventCmHelper.isCmUserGeneratedEvent(orderEvent)) {
					return OrderEventCmHelper.getCreatedBy(orderEvent);
				}
				return FulfillmentMessages.get().System;
			case 1:
				return DateTimeUtilFactory.getDateUtil().formatAsDateTime(orderEvent.getCreatedDate());
			case 2:
				return orderEvent.getNote();
			default:
				return columnToolTip;
			}
		}
	}

	/**
	 * The content provider class is responsible for providing objects to the view. It can wrap existing objects in adapters or
	 * simply return objects as-is. These objects may be sensitive to the current input of the view, or ignore it and always show
	 * the same content.
	 */
	class OrderNoteContentProvider implements IStructuredContentProvider {

		/**
		 * Gets the order notes from the set of order notes for each row.
		 * 
		 * @param inputElement the input order note element
		 * @return Object[] the returned input
		 */
		@Override
		@SuppressWarnings("synthetic-access")
		public Object[] getElements(final Object inputElement) {
			return ((Set<OrderEvent>) inputElement).toArray();
		}

		/**
		 * dispose the provider.
		 */
		@Override
		public void dispose() {
			// does nothing
		}

		/**
		 * Notify the provider the input has changed.
		 * 
		 * @param viewer the epTableViewer
		 * @param oldInput the previous input
		 * @param newInput the current selected input
		 */
		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// does nothing
		}
	}

	/**
	 * Refreshes the order notes.
	 */
	public void refreshNotes() {
		tableViewer.refresh();
	}

	/**
	 * Gets the table's modification listener.
	 * 
	 * @return ControlModificationListener the modification listener attached to table viewer
	 */
	public ControlModificationListener getControlModificationListener() {
		return controlModificationListener;
	}

	@Override
	public void sectionDisposed() {
		editor.removePropertyListener(this);
	}
	
	/**
	 * Invoked when property of this section is changed.
	 *
	 * @param source the event source
	 * @param propId the specific id
	 */
	@Override
	public void propertyChanged(final Object source, final int propId) {
		if (propId == OrderEditor.PROP_ADD_NOTE) {
			refreshNotes();
		}
	}

}
