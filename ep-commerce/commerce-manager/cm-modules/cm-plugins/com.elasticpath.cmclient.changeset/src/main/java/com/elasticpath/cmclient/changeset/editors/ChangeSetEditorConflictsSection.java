/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.changeset.editors;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.ChangeSetPlugin;
import com.elasticpath.cmclient.changeset.helpers.ComponentHelper;
import com.elasticpath.cmclient.core.pagination.PaginationInfo;
 import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CmClientResources;
import com.elasticpath.cmclient.core.common.IPaginationControl;
import com.elasticpath.cmclient.core.common.PaginationContributionControl;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.cmclient.core.event.NavigationEvent;
import com.elasticpath.cmclient.core.event.NavigationEvent.NavigationType;
import com.elasticpath.cmclient.core.event.NavigationEventListener;
import com.elasticpath.cmclient.core.event.NavigationEventService;
import com.elasticpath.cmclient.core.helpers.PaginationSupport;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.ui.framework.impl.TableColumnSorterControl;
import com.elasticpath.cmclient.core.ui.framework.impl.TableColumnSorterSupporterListener;
import com.elasticpath.common.dto.ChangeSetDependencyDto;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.PaginationConfig;
import com.elasticpath.commons.pagination.SortingDirection;
import com.elasticpath.commons.pagination.SortingField;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.service.changeset.ChangeSetMemberSortingField;
import com.elasticpath.service.changeset.ChangeSetService;

/**
 * The conflicts section UI.
 */
@SuppressWarnings({ "PMD.ExcessiveImports",  "PMD.TooManyMethods", "PMD.GodClass" })
public class ChangeSetEditorConflictsSection extends AbstractCmClientEditorPageSectionPart 
	implements PaginationSupport, NavigationEventListener, TableColumnSorterSupporterListener {
	
	private static final int DEPENDENCY_NAME_COLUMN_INDEX = 2;
	private static final int DEPENDENCY_CHANGESET_COLUMN_INDEX = 4;
	private static final String CONFLICTS_TABLE = "Conflicts Table"; //$NON-NLS-1$
	private static final int TABLE_HEIGHT = 265;

	private static final transient Logger LOG = Logger.getLogger(ChangeSetEditorConflictsSection.class);
	
	private final ComponentHelper componentHelper = new ComponentHelper();

	private IEpTableViewer conflictsTableViewer;

	private IPaginationControl paginationControl;

	private ChangeSetDependencyPaginator dependencyPaginator;

	private IEpLayoutComposite paginationComposite;

	private NavigationEventService navigationService;
	
	private final List<Control> dependencyNameHyperLinks = new ArrayList<>();
	
	private final List<Control> changeSetHyperLinks = new ArrayList<>();
	
	private final SortingField[] sortingFields = new SortingField[] {
			ChangeSetDependencySortingField.SOURCE_OBJECT_NAME,
			ChangeSetDependencySortingField.SOURCE_OBJECT_TYPE,
			ChangeSetDependencySortingField.DEPENDENCY_OBJECT_NAME,
			ChangeSetDependencySortingField.DEPENDENCY_OBJECT_TYPE,
			ChangeSetDependencySortingField.CHANGE_SET_NAME
	};
	
	private TableColumnSorterControl tableColumnSorterControl;

	/**
	 * Constructs a new section.
	 * 
	 * @param formPage the form page
	 * @param editor the editor
	 */
	public ChangeSetEditorConflictsSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {		
		super(formPage, editor, ExpandableComposite.NO_TITLE);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// nothing to bind
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		IEpLayoutComposite mainComposite = CompositeFactory.createGridLayoutComposite(client, 1, false);
		mainComposite.getSwtComposite().setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
		
		IEpLayoutData paginationLayoutData = mainComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		paginationComposite = mainComposite.addGridLayoutComposite(1, false, paginationLayoutData);
		createPagination(paginationComposite.getSwtComposite());
		
		IEpLayoutData tableData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 1, 2);
		conflictsTableViewer = mainComposite.addTableViewer(false, EpState.EDITABLE, tableData, CONFLICTS_TABLE);
		((GridData) conflictsTableViewer.getSwtTable().getLayoutData()).heightHint = TABLE_HEIGHT;

		tableColumnSorterControl = new TableColumnSorterControl(conflictsTableViewer.getSwtTable(), this);
		
		final int[] widths = new int[] { 120, 120, 120, 120, 120 };
		final String[] columnNames = new String[] {
				ChangeSetMessages.get().ChangeSetEditor_ConflictsTable_SourceName,
				ChangeSetMessages.get().ChangeSetEditor_ConflictsTable_SourceType,
				ChangeSetMessages.get().ChangeSetEditor_ConflictsTable_DependencyName,
				ChangeSetMessages.get().ChangeSetEditor_ConflictsTable_DependencyType,
				ChangeSetMessages.get().ChangeSetEditor_ConflictsTable_DependencyChangeSet
		};
		
		
		for (int columnIndex = 0; columnIndex < widths.length; columnIndex++) {
			IEpTableColumn tableColumn = conflictsTableViewer.addTableColumn(columnNames[columnIndex], widths[columnIndex]);
			tableColumnSorterControl.registerColumnListener(tableColumn.getSwtTableColumn(), sortingFields[columnIndex]);
		}
		
		conflictsTableViewer.setContentProvider(new ConflictsChangeSetProvider());
		conflictsTableViewer.setLabelProvider(new ConflictsChangeSetLabelProvider());
	}

	private int getColumnIndexBySortingField(final SortingField sortingField) {
		return ArrayUtils.indexOf(sortingFields, sortingField);
	}

	private void createPagination(final Composite parentComposite) {
		ToolBar toolBar = new ToolBar(parentComposite, SWT.FLAT | SWT.RIGHT);
		toolBar.setBackground(parentComposite.getBackground());
		final ToolBarManager toolBarManager = new ToolBarManager(toolBar);

		paginationControl = new PaginationContributionControl(toolBarManager, this);
		paginationControl.createViewPartControl();
		
		getNavigationService().registerNavigationEventListener(this);
	}
	
	private void updateNavigationComponents() {
		if (paginationControl != null) {
			paginationControl.updateNavigationComponents();
			paginationComposite.getSwtComposite().layout();
		}
	}

	/**
	 *
	 * @param beanName the bean name
	 * @param <T> the bean type
	 * @return the bean instance
	 */
	<T> T getBean(final String beanName) {
		return ServiceLocator.getService(beanName);
	}
	
	@Override
	protected void populateControls() {		
		ChangeSet changeSet = (ChangeSet) getModel();
		dependencyPaginator = new ChangeSetDependencyPaginator(getChangeSetDependencies(changeSet));
		PaginationConfig paginationConfig = getBean(ContextIdNames.PAGINATION_CONFIG);
		paginationConfig.setObjectId(((ChangeSet) getModel()).getGuid());
		paginationConfig.setPageSize(PaginationInfo.getInstance().getPagination());
		paginationConfig.setSortingFields(
				new DirectedSortingField(ChangeSetDependencySortingField.SOURCE_OBJECT_NAME, SortingDirection.ASCENDING));
		dependencyPaginator.init(paginationConfig);
		
		populateTable();
		
		updateNavigationComponents();
		
		SortingDirection sortingDirection = dependencyPaginator.getSortingFields()[0].getSortingDirection();
		SortingField sortingField = dependencyPaginator.getSortingFields()[0].getSortingField();

		TableColumn sortedTableColumn = this.conflictsTableViewer.getSwtTable().getColumn(getColumnIndexBySortingField(sortingField));
		this.tableColumnSorterControl.updateTableSortDirection(sortedTableColumn, sortingDirection);
	}

	private void populateTable() {
		List<ChangeSetDependencyDto> dependencyDtosForOnePage = dependencyPaginator.getCurrentPage().getItems();
		
		clearTable();
		conflictsTableViewer.setInput(dependencyDtosForOnePage);
		populateHyperLinkInTable(dependencyDtosForOnePage);
	}

	/**
	 * Clear both controls in table and the normal table items.
	 */
	private void clearTable() {
		disposeHyperLinks(dependencyNameHyperLinks);
		disposeHyperLinks(changeSetHyperLinks);
		
		conflictsTableViewer.getSwtTable().removeAll();
		conflictsTableViewer.getSwtTable().layout();
	}

	/**
	 * Dispose the the controls in the list and remove all the reference from the list.
	 * 
	 * @param controlList the controlList the control list
	 */
	private void disposeHyperLinks(final List<Control> controlList) {
		for (Control control : controlList) {
			control.dispose();
		}
		controlList.clear();
	}

	private void populateHyperLinkInTable(final List<ChangeSetDependencyDto> dependencyDtosToDisplay) {
		Table table = conflictsTableViewer.getSwtTable();
		TableItem[] items = table.getItems();
		for (int i = 0; i < dependencyDtosToDisplay.size(); i++) {
			final ChangeSetDependencyDto dependencyDto = dependencyDtosToDisplay.get(i);
			if (isHyperLinkForDependencyName(dependencyDto)) {
				Hyperlink dependencyNameHyperLink = createHyperLinkForDependencyName(table, dependencyDto);
				populateHyperLinkInTableItem(table, items[i], dependencyNameHyperLink, DEPENDENCY_NAME_COLUMN_INDEX);
			}

			Hyperlink changeSetHyperLink = createHyperLinkForChangeSet(table, dependencyDto);
			populateHyperLinkInTableItem(table, items[i], changeSetHyperLink, DEPENDENCY_CHANGESET_COLUMN_INDEX);
		}
	}

	private boolean isHyperLinkForDependencyName(final ChangeSetDependencyDto changeSetDependencyDto) {
		//permission check code could be put here to determine if a hyperlink should be created
		return !isMemberDeleted(changeSetDependencyDto);
	}

	private Hyperlink createHyperLinkForChangeSet(final Table table, final ChangeSetDependencyDto dependencyDto) {
		Hyperlink changeSetHyperLink = createHyperLink(table, dependencyDto.getDependencyChangeSetName());
		//save the reference for disposing when clearing the table for re-populate
		changeSetHyperLinks.add(changeSetHyperLink);
		changeSetHyperLink.addHyperlinkListener(new IHyperlinkListener() {
			public void linkActivated(final HyperlinkEvent arg0) {
				openChangeSetEditor(dependencyDto.getDependencyChangeSetGuid());
			}
			public void linkEntered(final HyperlinkEvent arg0) {
				// nothing to do
			}
			public void linkExited(final HyperlinkEvent arg0) {
				// nothing to do
			}
		});
		return changeSetHyperLink;
	}

	private BusinessObjectDescriptor getBusinessObjectDescriptor(
			final String objectType, final String objectIdentifier) {
		BusinessObjectDescriptor objDescriptor = ServiceLocator.getService(ContextIdNames.BUSINESS_OBJECT_DESCRIPTOR);
		objDescriptor.setObjectIdentifier(objectIdentifier);
		objDescriptor.setObjectType(objectType);
		return objDescriptor;
	}
	
	private Hyperlink createHyperLinkForDependencyName(final Table table,
			final ChangeSetDependencyDto dependencyDto) {
		Hyperlink dependencyNameHyperLink = createHyperLink(table, dependencyDto.getDependencyObjectName());
		//save the reference for disposing when clearing the table for re-populate
		dependencyNameHyperLinks.add(dependencyNameHyperLink);
		dependencyNameHyperLink.addHyperlinkListener(new IHyperlinkListener() {
			public void linkActivated(final HyperlinkEvent arg0) {
				BusinessObjectDescriptor dependencyBusinessObjectDescriptor = getBusinessObjectDescriptor(
						dependencyDto.getDependencyObjectType(), dependencyDto.getDependencyObjectIdentifier());
				openComponentForSelectedObject(dependencyDto.getDependencyChangeSetGuid(), 
						dependencyBusinessObjectDescriptor);
			}
			public void linkEntered(final HyperlinkEvent arg0) {
				// nothing to do
			}
			public void linkExited(final HyperlinkEvent arg0) {
				// nothing to do
			}
		});
		return dependencyNameHyperLink;
	}

	private Hyperlink createHyperLink(final Composite parentComposite, final String hyperLinkText) {
		Hyperlink dependencyNameHyperLink = new Hyperlink(parentComposite, SWT.NONE);
		dependencyNameHyperLink.setText(hyperLinkText);
		dependencyNameHyperLink.setUnderlined(true);
		dependencyNameHyperLink.setForeground(CmClientResources.getColor(CmClientResources.COLOR_BLUE));
		dependencyNameHyperLink.setBackground(parentComposite.getBackground());
		return dependencyNameHyperLink;
	}

	private void populateHyperLinkInTableItem(final Table table,
			final TableItem item, final Control control, final int columnIndex) {
		TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
		editor.setEditor(control, item, columnIndex);
		
	}

	private List<ChangeSetDependencyDto> getChangeSetDependencies(final ChangeSet changeSet) {
		return getChangeSetService().getChangeSetDependencies(changeSet, getSortedField());
	}

	private ChangeSetService getChangeSetService() {
		return ServiceLocator.getService(ContextIdNames.CHANGESET_SERVICE);
	}

	private DirectedSortingField getSortedField() {
		return new DirectedSortingField(ChangeSetMemberSortingField.OBJECT_ID, SortingDirection.ASCENDING);
	}
	
	private void openComponentForSelectedObject(final String changeSetGuid, final BusinessObjectDescriptor businessObjectDescriptor) {
		getComponentHelper().openComponent(changeSetGuid, businessObjectDescriptor);
	}
	
	private ComponentHelper getComponentHelper() {
		return componentHelper;
	}
	
	private void openChangeSetEditor(final String changeSetGuid) {
		String editorId = ChangeSetEditor.ID_EDITOR;
		try {
			Class< ? > clazz = ChangeSet.class;
			IEditorInput input = new GuidEditorInput(changeSetGuid, clazz);
			IWorkbenchPage workbenchPage = ChangeSetPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
			workbenchPage.openEditor(input, editorId);
		} catch (PartInitException exc) {
			LOG.error("Could not open editor for change set: " + changeSetGuid, exc); //$NON-NLS-1$
		}
	}
	
	private boolean isMemberDeleted(final ChangeSetDependencyDto changeSetDependencyDto) {
		return changeSetDependencyDto.isDependencyObjectDeleted();
	}
	
	public int getResultsCount() {
		return (int) this.dependencyPaginator.getTotalItems();
	}

	public int getResultsStartIndex() {
		return this.dependencyPaginator.getCurrentPage().getPageStartIndex() - 1;
	}

	public int getResultsPaging() {
		return PaginationInfo.getInstance().getPagination();
	}

	/**
	 * Fire navigation event.
	 *
	 * @param navigationType the type
	 * @param args the arguments
	 */
	public void fireNavigationEvent(final NavigationType navigationType, final Object[] args) {
		getNavigationService().fireNavigationEvent(new NavigationEvent(conflictsTableViewer, navigationType, args));
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
	 * Called by the <code>NavigationEventService</code> when <code>NavigationEvent</code>s occur.
	 * 
	 * @param event the NavigationEvent
	 */
	public void navigationChanged(final NavigationEvent event) {
		LOG.debug("Navigation event"); //$NON-NLS-1$
		final Enum<NavigationEvent.NavigationType> type = event.getType();
		if (type.equals(NavigationType.FIRST)) {
			dependencyPaginator.first();
		} else if (type.equals(NavigationType.PREVIOUS)) {
			dependencyPaginator.previous();
		} else if (type.equals(NavigationType.NEXT)) {
			dependencyPaginator.next();
		} else if (type.equals(NavigationType.LAST)) {
			dependencyPaginator.last();
		} else if (type.equals(NavigationType.TO)) {
			int toPage = (Integer) event.getArgs()[0];
			dependencyPaginator.getPage(toPage);
		}
		//Once the page navigation happens, populate the table again and update the navigation components.
		populateTable();
		updateNavigationComponents();
	}

	/**
	 * Select column header.
	 *
	 * @param directedSortingField the field
	 */
	public void columnHeaderSelected(final DirectedSortingField directedSortingField) {
		dependencyPaginator.setSortingField(directedSortingField.getSortingField());
		dependencyPaginator.first();
		updateNavigationComponents();
		populateTable();
	}
	
	public DirectedSortingField getCurrentSortingField() {
		return dependencyPaginator.getSortingFields()[0];
	}

}
