/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.product;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.editors.sku.ProductSkuEditor;
import com.elasticpath.cmclient.catalog.wizards.sku.AddSkuWizard;
import com.elasticpath.cmclient.catalog.wizards.sku.AddSkuWizardPage1;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.pagination.PaginationInfo;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.ui.util.EditorUtil;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwarePaginationControl;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.Page;
import com.elasticpath.commons.pagination.PaginationConfig;
import com.elasticpath.commons.pagination.Paginator;
import com.elasticpath.commons.pagination.PaginatorFactory;
import com.elasticpath.commons.pagination.SortingDirection;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;
import com.elasticpath.service.catalog.ProductSkuOrderingField;
import com.elasticpath.service.catalog.ProductSkuService;

/**
 * Product editor multi-sku page section.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveImports", "PMD.GodClass" })
public class ProductEditorMultiSkuSection extends AbstractPolicyAwareEditorPageSectionPart implements SelectionListener, IDoubleClickListener {

	private static final String[] YES_NO = new String[] { CatalogMessages.get().ProductEditorMultiSkuSection_Yes, "-" }; //$NON-NLS-1$

	private static final String SKU_TABLE = "Multi Sku"; //$NON-NLS-1$

	private IEpTableViewer skuTableViewer;

	private Button addButton;

	private Button openSkuButton;

	private Button removeButton;

	private final ControlModificationListener controlModificationListener;

	private final IWorkbenchPage workbenchPage;

	private static final Logger LOG = Logger.getLogger(ProductEditorMultiSkuSection.class);

	private final ProductSkuService productSkuService = getBean(ContextIdNames.PRODUCT_SKU_SERVICE);
	
	private IPolicyTargetLayoutComposite mainEpComposite;
	
	private StatePolicy statePolicy;
	
	private PolicyActionContainer multiSkuControls;
	
	private PolicyActionContainer openSkuControls;
	
	private PolicyActionContainer deleteSkuControls;
	
	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	private AbstractPolicyAwarePaginationControl<ProductSku> paginationControl;

	private PolicyActionContainer navigationControls;

	private final ProductMultiSkuPage formPage;
	
	/**
	 * Constructs this section.
	 * 
	 * @param formPage the parent form page
	 * @param editor the editor
	 */
	public ProductEditorMultiSkuSection(final ProductMultiSkuPage formPage, final ProductEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		this.formPage = formPage;
		controlModificationListener = editor;
		workbenchPage = editor.getSite().getPage();
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// nothing to bind here

	}

	@Override
	protected String getSectionTitle() {
		return CatalogMessages.get().ProductEditorMultiSkuSection_Title;
	}

	@Override
	protected String getSectionDescription() {
		return CatalogMessages.get().ProductEditorMultiSkuSection_Description;
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {

		mainEpComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(
				CompositeFactory.createGridLayoutComposite(client, 2, false));
		mainEpComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		multiSkuControls = addPolicyActionContainer("multiSkuControls"); //$NON-NLS-1$
		deleteSkuControls = addPolicyActionContainer("deleteSkuControls"); //$NON-NLS-1$
		openSkuControls = addPolicyActionContainer("openSkuControls"); //$NON-NLS-1$
		navigationControls = addPolicyActionContainer("navigationControls"); //$NON-NLS-1$

		IEpLayoutData paginationData = mainEpComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);

		PaginatorFactory factory = getBean(ContextIdNames.PAGINATOR_FACTORY);
		PaginationConfig config = getBean(ContextIdNames.PAGINATION_CONFIG);
		config.setObjectId(getProduct().getGuid());
		config.setPageSize(getPageSize());
		config.setSortingFields(new DirectedSortingField(ProductSkuOrderingField.SKU_CODE, SortingDirection.ASCENDING));
		
		Paginator<ProductSku> paginator = factory.createPaginator(ProductSku.class, config); 

		paginationControl = new AbstractPolicyAwarePaginationControl<ProductSku>(mainEpComposite, paginationData, navigationControls, paginator) {
			@Override
			public void update(final Page<ProductSku> newPage) {
				skuTableViewer.setInput(newPage);
				skuTableViewer.getSwtTable().setFocus();
				if (statePolicy != null) {
					applyStatePolicy(statePolicy);
				}
			}
		};
		paginationControl.createControls();

		// empty control to fill the second cell in the row
		mainEpComposite.addEmptyComponent(null, multiSkuControls);

		skuTableViewer = mainEpComposite.addTableViewer(false, mainEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL,
			true, true), multiSkuControls, SKU_TABLE);

		final int[] columnSize = new int[] { 150, 160, 100, 100, 75, 75 };
		final String[] columnName = new String[] {
				CatalogMessages.get().ProductEditorMultiSkuSection_SkuCode,
				CatalogMessages.get().ProductEditorMultiSkuSection_SkuConfiguration,
				CatalogMessages.get().ProductEditorMultiSkuSection_SkuEnableDate,
				CatalogMessages.get().ProductEditorMultiSkuSection_SkuDisableDate,
				CatalogMessages.get().ProductEditorMultiSkuSection_SkuShippable,
				CatalogMessages.get().ProductEditorMultiSkuSection_SkuDigAsset
		};
		for (int index = 0; index < columnName.length; index++) {
			skuTableViewer.addTableColumn(columnName[index], columnSize[index]);
		}

		skuTableViewer.getSwtTableViewer().addDoubleClickListener(this);
		skuTableViewer.setContentProvider(new SkuContentProvider());
		skuTableViewer.setLabelProvider(new SkuLabelProvider());

		skuTableViewer.getSwtTableViewer().addSelectionChangedListener(event -> {
			boolean validSelection = event.getSelection() != null && !event.getSelection().isEmpty();

			if (validSelection) {
				Object selectedObject = ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (selectedObject instanceof ProductSku) {
					openSkuControls.setPolicyDependent(selectedObject);
					deleteSkuControls.setPolicyDependent(selectedObject);
					applyStatePolicy(statePolicy);
				}
			}
		});

		final IPolicyTargetLayoutComposite buttonsComposite = mainEpComposite.addGridLayoutComposite(1, false, null, multiSkuControls);

		openSkuButton = buttonsComposite.addPushButton(CatalogMessages.get().ProductEditorMultiSkuSection_EditButton, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_PRODUCT_SKU), null, openSkuControls);
		openSkuButton.addSelectionListener(this);
		addButton = buttonsComposite.addPushButton(CatalogMessages.get().ProductEditorMultiSkuSection_AddButton, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_ADD), null, multiSkuControls);
		addButton.addSelectionListener(this);
		removeButton = buttonsComposite.addPushButton(CatalogMessages.get().ProductEditorMultiSkuSection_RemoveButton, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_REMOVE), null, deleteSkuControls);
		removeButton.addSelectionListener(this);

		addButton.setEnabled(false);
		openSkuButton.setEnabled(false);
		removeButton.setEnabled(false);
		mainEpComposite.setControlModificationListener(controlModificationListener);
		
		addCompositesToRefresh(mainEpComposite.getSwtComposite().getParent());
	}

	private int getPageSize() {
		return PaginationInfo.getInstance().getPagination();
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
		paginationControl.populateControls();
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// not used
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() == addButton) {
			openAddSkuWizard();
		} else if (event.getSource() == openSkuButton) {
			openProductSkuEditor();
		} else if (event.getSource() == removeButton) {
			removeButtonPressed();
		}

	}

	private void openAddSkuWizard() {
		final AddSkuWizard wizard = new AddSkuWizard(CatalogMessages.get().AddSkuWizard_Title, this.getProduct());

		final EpWizardDialog wizardDialog = new EpWizardDialog(this.getSection().getShell(), wizard);
		wizardDialog.addPageChangingListener(wizard);

		if (wizardDialog.open() == Window.OK) {
			final ProductSku newSku = ((AddSkuWizardPage1) wizard.getStartingPage()).getProductSku();
			getProduct().addOrUpdateSku(newSku);
			skuTableViewer.getSwtTableViewer().refresh();
			controlModificationListener.controlModified();
			formPage.fireSkuChangedEvent();
		}
	}

	/**
	 * Opens a SKU in a new Product SKU editor.
	 */
	private void openProductSkuEditor() {
		final IStructuredSelection selection = (IStructuredSelection) skuTableViewer.getSwtTableViewer().getSelection();
		if (selection != null && !selection.isEmpty()) {
			final ProductSku sku = (ProductSku) selection.getFirstElement();
			if (sku.isPersisted()) {
				try {
					workbenchPage.openEditor(new GuidEditorInput(sku.getGuid(), ProductSku.class), ProductSkuEditor.PART_ID);
				} catch (final PartInitException exc) {
					LOG.error("Error opening the Product SKU Editor", exc); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * Actions performed on removing a SKU.
	 */
	private void removeButtonPressed() {
		final IStructuredSelection selection = (IStructuredSelection) skuTableViewer.getSwtTableViewer().getSelection();
		if (selection.isEmpty()) {
			return;
		}
		final ProductSku selectedSku = (ProductSku) selection.getFirstElement();

		final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		for (IEditorReference editorRef : workbenchPage.getEditorReferences()) {

			try {
				if (EditorUtil.isSameEditor(editorRef, ProductSkuEditor.PART_ID)
						&& EditorUtil.isSameEntity(selectedSku, editorRef)) {
					MessageDialog.openWarning(skuTableViewer.getSwtTableViewer().getControl().getShell(),
						CatalogMessages.get().ProductEditorMultiSkuSection_CanNotRemove,
							NLS.bind(CatalogMessages.get().ProductEditorMultiSkuSection_CloseEditor,
							new Object[]{selectedSku.getSkuCode(),
							selectedSku.getDisplayName(CorePlugin.getDefault().getDefaultLocale())}));
					return;

				}
			} catch (PartInitException e) {
				LOG.error(e.getStackTrace());
				throw new EpUiException("Could not get productSku editor input", e); //$NON-NLS-1$
			}
		}
		if (productSkuService.canDelete(selectedSku)) {
			StringBuilder message = new StringBuilder(CatalogMessages.get().ProductEditorMultiSkuSection_Question);
			message.append(
				NLS.bind(CatalogMessages.get().ProductEditorMultiSkuSection_Info,
				new Object[]{selectedSku.getSkuCode(),
				selectedSku.getDisplayName(CorePlugin.getDefault().getDefaultLocale())}));

			if (changeSetHelper.isChangeSetsEnabled()) {
				ChangeSetObjectStatus changeSetObjectStatus = changeSetHelper.getChangeSetObjectStatus(selectedSku);
				if (!changeSetObjectStatus.isLocked()) {
					message.append(
						NLS.bind(CatalogMessages.get().ProductEditorMultiSkuSection_Auto_Change_Association,
						new Object[]{changeSetHelper.getActiveChangeSet().getName()}));
				}
			}

			final boolean answerYes = MessageDialog.openConfirm(getSection().getShell(),
					CatalogMessages.get().ProductEditorMultiSkuSection_RemoveConfirmation, message.toString());
			if (answerYes) {
				getProduct().removeSku(selectedSku);
				((ProductModel) getEditor().getModel()).removeSku(selectedSku);
				skuTableViewer.getSwtTableViewer().refresh();
				formPage.fireSkuChangedEvent();
				ItemChangeEvent<ProductSku> event = new ItemChangeEvent<>(this, selectedSku, EventType.REMOVE);
				CatalogEventService.getInstance().notifyProductSkuChanged(event);
				controlModificationListener.controlModified();
			}
		} else {
			if (productSkuService.isInBundle(selectedSku)) {
				final Collection<ProductBundle> bundlesContainingProduct = productSkuService.findProductBundlesContaining(selectedSku);

				MessageDialog.openWarning(null,
						CatalogMessages.get().DeleteProductSku_CanNotRemove,

						NLS.bind(CatalogMessages.get().DeleteProductSku_CanNotRemoveBundleMsg,
						new Object[]{selectedSku.getSkuCode(), getBundleCodesAsString(bundlesContainingProduct)}));
			} else {
				MessageDialog.openWarning(getSection().getShell(), CatalogMessages.get().ProductEditorMultiSkuSection_CanNotRemove,

						NLS.bind(CatalogMessages.get().ProductEditorMultiSkuSection_CanNotRemoveMsg,
						new Object[]{selectedSku.getSkuCode()}));
			}
			return;
		}

	}

	private String getBundleCodesAsString(final Collection<ProductBundle> bundles) {
		final StringBuilder messageBuilder = new StringBuilder();
		for (ProductBundle bundle : bundles) {
			messageBuilder.append(bundle.getCode());
			messageBuilder.append('\n');
		}
		return messageBuilder.toString();
	}

	/**
	 * Content provider for the SKUs table.
	 */
	private class SkuContentProvider implements IStructuredContentProvider {

		/**
		 *
		 */
		SkuContentProvider() {
			super();
		}

		@Override
		public Object[] getElements(final Object inputElement) {
			return ((Page<ProductSku>) inputElement).getItems().toArray();
		}

		/**
		 *
		 */
		@Override
		public void dispose() {
			// nothing to do

		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// nothing to do
		}
	}

	/**
	 * Label provider for the text and images.
	 */
	protected static class SkuLabelProvider extends LabelProvider implements ITableLabelProvider {

		private static final int SKU_CODE_COLUMN = 0;

		private static final int SKU_CONF_COLUMN = 1;

		private static final int DIGASSET_COLUMN = 5;

		private static final int SHIPPABLE_COLUMN = 4;

		private static final int ENABLE_DATE_COLUMN = 2;

		private static final int DISABLE_DATE_COLUMN = 3;

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final ProductSku sku = (ProductSku) element;
			String result = StringUtils.EMPTY;
			switch (columnIndex) {
			case SKU_CODE_COLUMN:
				result = sku.getSkuCode();
				break;
			case SKU_CONF_COLUMN:
				result = sku.getDisplayName(CorePlugin.getDefault().getDefaultLocale());
				break;
			case ENABLE_DATE_COLUMN:
				result = DateTimeUtilFactory.getDateUtil().formatAsDate(sku.getStartDate());
				break;
			case DISABLE_DATE_COLUMN:
				result = DateTimeUtilFactory.getDateUtil().formatAsDate(sku.getEndDate());
				break;
			case SHIPPABLE_COLUMN:
				result = getYesOrNo(sku.isShippable());
				break;
			case DIGASSET_COLUMN:
				result = getYesOrNo(sku.isDigital());
				break;
			default:
				// do nothing
			}
			return result;
		}

		/**
		 * Converts a boolean to Yes or No string.
		 * 
		 * @param value the boolean value
		 * @return String
		 */
		private String getYesOrNo(final boolean value) {
			if (value) {
				return YES_NO[0];
			}
			return YES_NO[1];
		}
	}

	private Product getProduct() {
		return ((ProductModel) getEditor().getModel()).getProduct();
	}

	@Override
	public void doubleClick(final DoubleClickEvent event) {
		openProductSkuEditor();
	}

	@Override
	public void applyStatePolicy(final StatePolicy policy) {
		this.statePolicy = policy;
		super.applyStatePolicy(policy);
	}
	
}
