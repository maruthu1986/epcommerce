/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.core.pagination.PaginationInfo;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.promotions.CouponCollectionModel;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.jobs.handlers.RunCsvImportForCouponCodesHandler;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.cmclient.store.promotions.CouponConfigPageModel;
import com.elasticpath.cmclient.store.promotions.CouponPageModel;
import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.cmclient.store.promotions.model.CouponModelDtoDatabasePaginatorLocator;
import com.elasticpath.cmclient.store.promotions.model.CouponUsageModelDtoDatabasePaginatorLocator;
import com.elasticpath.common.dto.CouponModelDto;
import com.elasticpath.common.dto.CouponUsageModelDto;
import com.elasticpath.common.dto.DatabaseMemoryMergeSearchablePaginatorLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.PaginationConfig;
import com.elasticpath.commons.pagination.Paginator;
import com.elasticpath.commons.pagination.PaginatorFactory;
import com.elasticpath.commons.pagination.SearchCriterion;
import com.elasticpath.commons.pagination.SearchablePaginatorLocatorAdapter;
import com.elasticpath.commons.pagination.SortingDirection;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.service.rules.CouponUsageModelDtoSortingField;

/**
 * The coupon config editor part that edit an existing coupon config.
 */
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyMethods", "PMD.GodClass"})
public class CouponEditorPart extends AbstractPolicyAwareEditorPageSectionPart {

	private static final int TABLE_HEIGHT_HINT = 100;

	/**
	 * .
	 */
	public static final String OBJECTS_SECTION = "objectsSection"; //$NON-NLS-1$

	/**
	 * .
	 */
	public static final String OBJECTS_DISPLAY_SECTION = "couponEditorPart"; //$NON-NLS-1$
	private static final int COUPON_CODE_COLUMN_WIDTH = 200;
	private static final int COLUMN_WIDTH = 100;
	private static final int EMAIL_COLUMN_WIDTH = 300;
	private static final transient Logger LOG = Logger.getLogger(CouponEditorPart.class);

	private static final int LAYOUT_COLUMN_COUNT = 3;
	private static final String COUPON_USAGE_TABLE = "Coupon Usage"; //$NON-NLS-1$
	private IPolicyTargetLayoutComposite mainComposite;
	private IEpTableViewer couponUsageTableViewer;

	private PolicyActionContainer container;
	private PolicyActionContainer displayContainer;

	private StatePolicy statePolicy;

	private CouponPaginationControl<?> paginationControl;

	private Button addButton;
	private Button editButton;

	private Button importButton;
	private Text searchCouponCodeField;
	private Text searchEmailAddressField;
	private Button searchButton;
	private Button clearButton;
	private final CouponConfigPageModel couponConfigPageModel;

	private CCombo statusFilterCombo;

	private final CouponPageModel model;
	private final Display display;

	/**
	 * Constructor.
	 *
	 * @param editor                the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 * @param model                 The model for this page.
	 * @param formPage              the form page
	 * @param couponConfigPageModel the coupon config model.
	 */
	public CouponEditorPart(final CouponEditorPage formPage,
							final AbstractCmClientFormEditor editor, final CouponPageModel model, final CouponConfigPageModel
									couponConfigPageModel) {
		super(formPage, editor, ExpandableComposite.NO_TITLE);
		this.couponConfigPageModel = couponConfigPageModel;
		this.model = model;
		this.display = Display.getDefault();
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// nothing to bind
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		mainComposite = PolicyTargetCompositeFactory
				.wrapLayoutComposite(CompositeFactory
						.createGridLayoutComposite(client, LAYOUT_COLUMN_COUNT,
								false));
		mainComposite.setLayoutData(mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true).getSwtLayoutData());

		container = addPolicyActionContainer(OBJECTS_SECTION);
		displayContainer = addPolicyActionContainer(OBJECTS_DISPLAY_SECTION);

		mainComposite.addEmptyComponent(null, container);

		createPaginationControl();

		mainComposite.addEmptyComponent(null, container);

		createSearchSection();

		createCentreCompositeControls();

		createRightSideButtonSection();

		refreshTableLayout();
	}

	private void refreshTableLayout() {
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		layoutData.heightHint = TABLE_HEIGHT_HINT;
		couponUsageTableViewer.getSwtTableViewer().getTable().getParent().setLayoutData(layoutData);
	}

	private void createRightSideButtonSection() {
		final IEpLayoutData buttonData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING);
		final IEpLayoutData rightCompositeLayoutData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false);
		final IPolicyTargetLayoutComposite rightComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(mainComposite
				.getLayoutComposite().addGridLayoutComposite(1, false,
						rightCompositeLayoutData));

		addButton = rightComposite.addPushButton(
				PromotionsMessages.get().CouponEditorPart_Add, CoreImageRegistry
						.getImage(CoreImageRegistry.IMAGE_ADD), buttonData,
				container);
		editButton = rightComposite.addPushButton(
				PromotionsMessages.get().CouponEditorPart_Edit, CoreImageRegistry
						.getImage(CoreImageRegistry.IMAGE_EDIT_CELL_SMALL),
				buttonData, displayContainer);

		createImportButton(displayContainer, buttonData, rightComposite);
	}

	private void createSearchSection() {
		// Search group UI presentation including 3 text fields.

		final IEpLayoutData leftPaneLayoutData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false);
		final IEpLayoutComposite leftPaneComposite = mainComposite
				.getLayoutComposite().addGridLayoutComposite(1, false,
						leftPaneLayoutData);

		final IEpLayoutData searchGroupLayoutData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutComposite searchGroupComposite = leftPaneComposite
				.addGroup(PromotionsMessages.get().CouponEditorPart_Search, 1, false,
						searchGroupLayoutData);

		final IEpLayoutData fieldData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		searchGroupComposite.addLabelBold(PromotionsMessages.get().CouponEditorPart_Label_CouponCode, null);
		searchCouponCodeField = searchGroupComposite.addTextField(EpState.EDITABLE, fieldData);

		if (isLimitPerSpecifiedUser()) {
			searchGroupComposite.addLabelBold(PromotionsMessages.get().CouponEditorPart_Label_EmailAddress, null);
			searchEmailAddressField = searchGroupComposite.addTextField(EpState.EDITABLE, fieldData);
		}

		final IEpLayoutComposite filterGroupComposite = leftPaneComposite
				.addGroup(PromotionsMessages.get().CouponEditorPart_Filter, 1, false,
						searchGroupLayoutData);
		filterGroupComposite.addLabelBold(PromotionsMessages.get().CouponEditorPart_Label_Status, null);
		statusFilterCombo = filterGroupComposite.addComboBox(EpState.EDITABLE, fieldData);
		String[] statusFilterItems = {
				PromotionsMessages.get().CouponEditorPart_Status_All,
				PromotionsMessages.get().CouponEditorPart_Status_In_Use,
				PromotionsMessages.get().CouponEditorPart_Status_Suspended};
		statusFilterCombo.setItems(statusFilterItems);

		final IEpLayoutComposite buttonsComposite = leftPaneComposite.addGridLayoutComposite(2, true, mainComposite.createLayoutData(
				IEpLayoutData.END, IEpLayoutData.FILL));
		searchButton = buttonsComposite.addPushButton(PromotionsMessages.get().CouponEditorDialog_Search, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_SEARCH), EpState.EDITABLE, null);
		searchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				doSearch();
			}
		});

		clearButton = buttonsComposite.addPushButton(PromotionsMessages.get().CouponEditorDialog_Clear, EpState.EDITABLE, null);
		clearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				doClear();
			}
		});
	}

	@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
	private void createImportButton(
			final PolicyActionContainer displayContainer,
			final IEpLayoutData buttonData,
			final IPolicyTargetLayoutComposite rightComposite) {
		importButton = rightComposite.addPushButton(PromotionsMessages.get().CouponEditorPart_Import,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_CSV_IMPORT), buttonData, displayContainer);
		importButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				CouponCollectionModel couponUsageCollectionModel = couponConfigPageModel.getCouponUsageCollectionModel();
				couponUsageCollectionModel.setCouponConfig(couponConfigPageModel.getCouponConfig());
				try {

					final RunCsvImportForCouponCodesHandler csvImportHandler =
							new RunCsvImportForCouponCodesHandler(couponUsageCollectionModel, new ImportJobDoneListener(display));
					csvImportHandler.execute(null);
				} catch (ExecutionException eep) {
					LOG.error("Error during importing coupon codes", eep); //$NON-NLS-1$
					throw new RuntimeException(eep);
				}
			}
		});
	}

	private void createPaginationControl() {

		// policy action container, which is not registered with anything else,
		// to allow the pagination controls to be enabled when a change set
		// is not selected.
		PolicyActionContainer alwaysEnabledContainer = new PolicyActionContainer("alwaysEnabledContainer"); //$NON-NLS-1$

		IEpLayoutData paginationData = mainComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		PaginatorFactory factory = getBean(ContextIdNames.PAGINATOR_FACTORY);
		PaginationConfig config = getBean(ContextIdNames.PAGINATION_CONFIG);

		final String objectId = Long.toString(this.couponConfigPageModel.getCouponConfig().getUidPk());
		config.setObjectId(objectId);
		config.setPageSize(getPageSize());
		config.setSortingFields(new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING));
		if (isLimitPerSpecifiedUser()) {
			createCouponUsagePaginationControl(alwaysEnabledContainer, mainComposite,
					paginationData, factory, config);
		} else {
			createCouponPaginationControl(alwaysEnabledContainer, mainComposite,
					paginationData, factory, config);
		}
	}

	private void doSearch() {
		List<SearchCriterion> searchCriteria = new ArrayList<>();

		String couponCodeText = searchCouponCodeField.getText();
		if (!StringUtils.isEmpty(couponCodeText)) {
			SearchCriterion codeSearchCriterion = new SearchCriterion("couponCode", couponCodeText); //$NON-NLS-1$
			searchCriteria.add(codeSearchCriterion);
		}
		if (isLimitPerSpecifiedUser()) {
			String emailAddressText = searchEmailAddressField.getText();
			if (!StringUtils.isEmpty(emailAddressText)) {
				SearchCriterion emailAddressSearchCriterion = new SearchCriterion("emailAddress", emailAddressText); //$NON-NLS-1$
				searchCriteria.add(emailAddressSearchCriterion);
			}
		}
		if (statusFilterCombo.getSelectionIndex() == 1) {
			SearchCriterion inUseCriterion = new SearchCriterion("status", "in_use");  //$NON-NLS-1$//$NON-NLS-2$
			searchCriteria.add(inUseCriterion);
		} else if (statusFilterCombo.getSelectionIndex() == 2) {
			SearchCriterion suspendedCriterion = new SearchCriterion("status", "suspended"); //$NON-NLS-1$ //$NON-NLS-2$
			searchCriteria.add(suspendedCriterion);
		}

		paginationControl.search(searchCriteria);
	}

	private void doClear() {
		List<SearchCriterion> searchCriteria = new ArrayList<>();
		paginationControl.search(searchCriteria);

		statusFilterCombo.select(0);
		searchCouponCodeField.setText(StringUtils.EMPTY);
		if (searchEmailAddressField != null) {
			searchEmailAddressField.setText(StringUtils.EMPTY);
		}

	}

	private void createCentreCompositeControls() {
		final IPolicyTargetLayoutComposite centreComposite = PolicyTargetCompositeFactory.wrapLayoutComposite((mainComposite
				.getLayoutComposite()).addGridLayoutComposite(1, false,
				mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true)));

		IEpTableColumn couponCodeColumn;
		if (isLimitPerSpecifiedUser()) {
			couponCodeColumn = createCouponUsageTable(centreComposite);
		} else {
			couponCodeColumn = createCouponTable(centreComposite);
		}

		DirectedSortingField sortField = new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING);
		paginationControl.sortBy(sortField);
		couponUsageTableViewer.getSwtTable().setSortColumn(couponCodeColumn.getSwtTableColumn());
		couponUsageTableViewer.getSwtTable().setSortDirection(SWT.UP);

		couponUsageTableViewer.getSwtTableViewer().addDoubleClickListener(
				event -> {
					if (EpState.EDITABLE == statePolicy.determineState(displayContainer)) {
						editCoupon();
					}
				});
	}

	private boolean isLimitPerSpecifiedUser() {
		return CouponUsageType.LIMIT_PER_SPECIFIED_USER.equals(couponConfigPageModel.getCouponConfig().getUsageType());
	}

	private IEpTableColumn createCouponTable(
			final IPolicyTargetLayoutComposite centreComposite) {

		IEpLayoutData tableData = centreComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 1, 2);
		couponUsageTableViewer = centreComposite.addTableViewer(true, tableData, displayContainer, COUPON_USAGE_TABLE);
		couponUsageTableViewer.getSwtTable().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(final KeyEvent event) {
				if ((event.stateMask & SWT.CTRL) != 0 && event.keyCode == 'a') {
					couponUsageTableViewer.getSwtTable().selectAll();
				}
			}
		});

		CouponEditorPartColumnClickListener columnClickListener = new CouponEditorPartColumnClickListener(
				couponUsageTableViewer.getSwtTable(), paginationControl);

		IEpTableColumn column = addColumnToTable(COUPON_CODE_COLUMN_WIDTH,
				PromotionsMessages.get().CouponEditorPart_Table_CouponCode,
				columnClickListener);
		columnClickListener.registerSortingField(column.getSwtTableColumn(), CouponUsageModelDtoSortingField.COUPON_CODE);
		IEpTableColumn couponCodeColumn = column;

		column = addColumnToTable(COLUMN_WIDTH, PromotionsMessages.get().CouponEditorPart_Table_Status, columnClickListener);
		columnClickListener.registerSortingField(column.getSwtTableColumn(), CouponUsageModelDtoSortingField.STATUS);

		couponUsageTableViewer.setContentProvider(new CouponModelDtoProvider());
		couponUsageTableViewer.setLabelProvider(new CouponModelDtoLabelProvider());
		return couponCodeColumn;
	}

	private void createCouponPaginationControl(
			final PolicyActionContainer displayContainer,
			final IPolicyTargetLayoutComposite centreComposite,
			final IEpLayoutData paginationData,
			final PaginatorFactory factory,
			final PaginationConfig config) {
		Paginator<CouponModelDto> paginator = factory.createPaginator(CouponModelDto.class, config);

		SearchablePaginatorLocatorAdapter<CouponModelDto> locatorAdapter = new SearchablePaginatorLocatorAdapter<>();
		DatabaseMemoryMergeSearchablePaginatorLocator<CouponModelDto> paginatorLocator
				= new DatabaseMemoryMergeSearchablePaginatorLocator<>();
		model.setCouponMemoryPaginatorLocator(paginatorLocator);
		CouponModelDtoDatabasePaginatorLocator databasePaginator = new CouponModelDtoDatabasePaginatorLocator();
		paginatorLocator.setDatabasePaginatorLocator(databasePaginator);
		locatorAdapter.setSearchablePaginatorLocator(paginatorLocator);
		paginator.setPaginatorLocator(locatorAdapter);

		paginationControl = new CouponPaginationControl<>(this, centreComposite, paginationData, displayContainer, paginator);
		paginationControl.createControls();
		paginationControl.setState(EpState.EDITABLE);
	}

	private IEpTableColumn createCouponUsageTable(
			final IPolicyTargetLayoutComposite centreComposite) {

		IEpLayoutData tableData = centreComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 1, 2);
		couponUsageTableViewer = centreComposite.addTableViewer(true, tableData, displayContainer, COUPON_USAGE_TABLE);

		couponUsageTableViewer.getSwtTable().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(final KeyEvent event) {
				if ((event.stateMask & SWT.CTRL) != 0 && event.keyCode == 'a') {
					couponUsageTableViewer.getSwtTable().selectAll();
				}
			}
		});

		CouponEditorPartColumnClickListener columnClickListener = new CouponEditorPartColumnClickListener(
				couponUsageTableViewer.getSwtTable(), paginationControl);


		IEpTableColumn column = addColumnToTable(COUPON_CODE_COLUMN_WIDTH,
				PromotionsMessages.get().CouponEditorPart_Table_CouponCode,
				columnClickListener);
		columnClickListener.registerSortingField(column.getSwtTableColumn(), CouponUsageModelDtoSortingField.COUPON_CODE);
		IEpTableColumn couponCodeColumn = column;

		column = addColumnToTable(EMAIL_COLUMN_WIDTH, PromotionsMessages.get().CouponEditorPart_Table_EmailAddress, columnClickListener);
		columnClickListener.registerSortingField(column.getSwtTableColumn(), CouponUsageModelDtoSortingField.EMAIL_ADDRESS);

		column = addColumnToTable(COLUMN_WIDTH, PromotionsMessages.get().CouponEditorPart_Table_Status, columnClickListener);
		columnClickListener.registerSortingField(column.getSwtTableColumn(), CouponUsageModelDtoSortingField.STATUS);

		couponUsageTableViewer.setContentProvider(new CouponUsageModelDtoProvider());
		couponUsageTableViewer.setLabelProvider(new CouponUsageModelDtoLabelProvider());
		return couponCodeColumn;
	}

	private void createCouponUsagePaginationControl(
			final PolicyActionContainer displayContainer,
			final IPolicyTargetLayoutComposite centreComposite,
			final IEpLayoutData paginationData,
			final PaginatorFactory factory,
			final PaginationConfig config) {

		Paginator<CouponUsageModelDto> paginator = factory.createPaginator(CouponUsageModelDto.class, config);

		SearchablePaginatorLocatorAdapter<CouponUsageModelDto> locatorAdapter = new SearchablePaginatorLocatorAdapter<>();
		DatabaseMemoryMergeSearchablePaginatorLocator<CouponUsageModelDto> paginatorLocator
				= new DatabaseMemoryMergeSearchablePaginatorLocator<>();
		model.setCouponUsageMemoryPaginatorLocator(paginatorLocator);
		CouponUsageModelDtoDatabasePaginatorLocator databasePaginator = new CouponUsageModelDtoDatabasePaginatorLocator();
		paginatorLocator.setDatabasePaginatorLocator(databasePaginator);
		locatorAdapter.setSearchablePaginatorLocator(paginatorLocator);
		paginator.setPaginatorLocator(locatorAdapter);
		paginationControl = new CouponPaginationControl<>(this, centreComposite, paginationData, displayContainer, paginator);
		paginationControl.createControls();
		paginationControl.setState(EpState.EDITABLE);
	}

	private IEpTableColumn addColumnToTable(final int columnWidth, final String columnName,
											final CouponEditorPartColumnClickListener columnClickListener) {
		IEpTableColumn column = couponUsageTableViewer.addTableColumn(columnName, columnWidth);
		column.getSwtTableColumn().addListener(SWT.Selection, columnClickListener);
		return column;
	}

	/**
	 * @param beanName the bean name
	 * @param <T>      the bean type
	 * @return the bean instance
	 */
	<T> T getBean(final String beanName) {
		return ServiceLocator.getService(beanName);
	}

	/**
	 * @return the current page size
	 */
	private int getPageSize() {
		return PaginationInfo.getInstance().getPagination();
	}

	@Override
	protected void populateControls() {
		paginationControl.populateControls();

		searchCouponCodeField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(final KeyEvent event) {
				if (event.keyCode == SWT.CR) {
					doSearch();
				}
				searchCouponCodeField.setFocus();
			}
		});

		addButton.addSelectionListener(createAddCouponButtonSelectionListener());
		editButton.addSelectionListener(createEditCouponButtonSelectionListener());
		statusFilterCombo.select(0);
	}

	private SelectionListener createAddCouponButtonSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				CouponUsageType usageType = couponConfigPageModel.getCouponConfig().getUsageType();
				CouponModelDto dto = new CouponModelDto();
				if (isPrivateCoupon(usageType)) {
					dto = new CouponUsageModelDto();
				}

				AddOrEditCouponDialog dialog = new AddOrEditCouponDialog(Arrays
						.asList(dto), couponConfigPageModel
						.getCouponUsageCollectionModel(), false);
				if (dialog.open() == Window.OK) {
					addCoupon(usageType, dto);

					refreshTableViewer();
					couponChanged();
				}
			}

		};
	}

	private void couponChanged() {
		markDirty();
		getEditor().controlModified();
	}

	private void addCoupon(final CouponUsageType usageType, final CouponModelDto dto) {
		if (isPrivateCoupon(usageType)) {
			model.add((CouponUsageModelDto) dto);
		} else {
			model.add(dto);
		}
	}

	private boolean isPrivateCoupon(final CouponUsageType usageType) {
		return usageType.equals(CouponUsageType.LIMIT_PER_SPECIFIED_USER);
	}

	private SelectionListener createEditCouponButtonSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				editCoupon();
			}
		};
	}

	private void refreshTableViewer() {
		paginationControl.refreshCurrentPage();
		couponUsageTableViewer.getSwtTableViewer().refresh();
		refreshLayout();
	}

	private void updateCoupon(final CouponUsageType usageType, final CouponModelDto dto) {
		if (isPrivateCoupon(usageType)) {
			model.update((CouponUsageModelDto) dto);
		} else {
			model.update(dto);
		}
	}

	@Override
	public void refreshLayout() {
		if (!this.mainComposite.getSwtComposite().isDisposed()) {
			this.mainComposite.getSwtComposite().layout();
		}
	}

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		this.statePolicy = statePolicy;
		super.applyStatePolicy(statePolicy);
	}

	@Override
	public void dispose() {
		super.dispose();
		paginationControl.dispose();
	}

	/**
	 * @return the couponUsageTableViewer
	 */
	public IEpTableViewer getCouponUsageTableViewer() {
		return couponUsageTableViewer;
	}

	@Override
	public StatePolicy getStatePolicy() {
		return statePolicy;
	}

	private void editCoupon() {
		StructuredSelection selection = (StructuredSelection) couponUsageTableViewer.getSwtTableViewer().getSelection();
		if (selection.isEmpty()) {
			return;
		}

		List<CouponModelDto> selectedDtos = new ArrayList<>();
		Iterator<?> iterator = selection.iterator();
		while (iterator.hasNext()) {
			selectedDtos.add((CouponModelDto) iterator.next());
		}

		AddOrEditCouponDialog dialog = new AddOrEditCouponDialog(selectedDtos, couponConfigPageModel.getCouponUsageCollectionModel(), true);
		if (dialog.open() == Window.OK) {
			CouponUsageType usageType = couponConfigPageModel.getCouponConfig().getUsageType();
			for (CouponModelDto dto : selectedDtos) {
				updateCoupon(usageType, dto);
			}

			refreshTableViewer();
			couponChanged();
		}
	}

	/**
	 * Provides action after job execution.
	 */
	private class ImportJobDoneListener extends JobChangeAdapter {

		private final Display display;

		ImportJobDoneListener(final Display display) {
			this.display = display;
		}
		@Override
		public void done(final IJobChangeEvent event) {
			//clear the search criteria and search everything again when the import job is done.
			display.syncExec(CouponEditorPart.this::doClear);
		}
	}

	@Override
	protected Object getLayoutData() {
		return new GridData(GridData.FILL, GridData.FILL, true, true);
	}

	@Override
	protected Layout getLayout() {
		return new GridLayout(1, false);
	}

}
