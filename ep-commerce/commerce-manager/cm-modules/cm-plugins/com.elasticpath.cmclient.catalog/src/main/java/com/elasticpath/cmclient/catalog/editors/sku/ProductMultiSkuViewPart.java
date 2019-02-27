/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.sku;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.wizards.sku.AddSkuWizard;
import com.elasticpath.cmclient.catalog.wizards.sku.AddSkuWizardPage1;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.ui.framework.IEpViewPart;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;

/**
 *
 * @author shallinan
 *
 */
public class ProductMultiSkuViewPart implements IEpViewPart, SelectionListener,
		IDoubleClickListener {

	private static final int SKU_CONF_COLUMN_WIDTH = 160;

	private static final int SKU_CODE_COLUMN_WIDTH = 90;

	private static final int SKU_DATE_COLUMN_WIDTH = 100;

	private static final int SKU_SHIPPABLE_COLUMN_WIDTH = 60;

	private static final int SKU_DIGASSET_COLUMN_WIDTH = 75;

	private static final String[] YES_NO = new String[] { CatalogMessages.get().ProductEditorMultiSkuSection_Yes, "-" }; //$NON-NLS-1$

	private static final String MULTI_SKU_TABLE = "Multi Sku"; //$NON-NLS-1$

	private IEpTableViewer skuTableViewer;

	private Button addButton;

	private Button removeButton;

	private ControlModificationListener controlModificationListener;

	private final Product product;

	private final Shell shell;

	private final boolean authorized;


	/**
	 * Constructs the view part.
	 *
	 * @param product the product
	 * @param authorized is the user authorized
	 * @param shell the parent shell
	 */
	public ProductMultiSkuViewPart(final Product product, final boolean authorized, final Shell shell) {
		this.shell = shell;
		this.product = product;
		this.authorized = authorized;
	}

	@Override
	public void bindControls(final DataBindingContext bindingContext) {
		// TODO Auto-generated method stub
	}

	@Override
	public void createControls(final IEpLayoutComposite mainEpComposite, final IEpLayoutData data) {
		EpState epState;
		if (authorized) {
			epState = EpState.EDITABLE;
		} else {
			epState = EpState.READ_ONLY;
		}

		skuTableViewer = mainEpComposite.addTableViewer(false, epState, mainEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL,
			false, false), MULTI_SKU_TABLE);

		skuTableViewer.addTableColumn(CatalogMessages.get().ProductEditorMultiSkuSection_SkuCode, SKU_CODE_COLUMN_WIDTH);
		skuTableViewer.addTableColumn(CatalogMessages.get().ProductEditorMultiSkuSection_SkuConfiguration, SKU_CONF_COLUMN_WIDTH);
		skuTableViewer.addTableColumn(CatalogMessages.get().ProductEditorMultiSkuSection_SkuEnableDate, SKU_DATE_COLUMN_WIDTH);
		skuTableViewer.addTableColumn(CatalogMessages.get().ProductEditorMultiSkuSection_SkuDisableDate, SKU_DATE_COLUMN_WIDTH);
		skuTableViewer.addTableColumn(CatalogMessages.get().ProductEditorMultiSkuSection_SkuShippable, SKU_SHIPPABLE_COLUMN_WIDTH);
		skuTableViewer.addTableColumn(CatalogMessages.get().ProductEditorMultiSkuSection_SkuDigAsset, SKU_DIGASSET_COLUMN_WIDTH);

		if (authorized) {
			skuTableViewer.getSwtTableViewer().addDoubleClickListener(this);
		}
		skuTableViewer.setContentProvider(new ArrayContentProvider());
		skuTableViewer.setLabelProvider(new SkuLabelProvider());

		// just make the table invoke ContentProvider.getElements()
		skuTableViewer.setInput(getInput());

		skuTableViewer.getSwtTableViewer().addSelectionChangedListener(event -> removeButton.setEnabled(!event.getSelection().isEmpty()));
		final IEpLayoutComposite buttonsComposite = mainEpComposite.addGridLayoutComposite(1, false, null);

		addButton = buttonsComposite.addPushButton(CatalogMessages.get().ProductEditorMultiSkuSection_AddButton, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_ADD), epState, null);
		addButton.addSelectionListener(this);

		removeButton = buttonsComposite.addPushButton(CatalogMessages.get().ProductEditorMultiSkuSection_RemoveButton, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_REMOVE), EpState.READ_ONLY, null);
		removeButton.addSelectionListener(this);

		if (controlModificationListener != null) {
			mainEpComposite.setControlModificationListener(controlModificationListener);
		}
	}

	private Object[] getInput() {
		return getModel().getProductSkus().values().toArray();
	}

	@Override
	public Product getModel() {
		return product;
	}

	@Override
	public void populateControls() {
		skuTableViewer.setInput(getInput());

	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() == addButton) {

			openAddSkuWizard();
			refreshTable();
		} else if (event.getSource() == removeButton) {
			removeButtonPressed();
			refreshTable();
		}

	}

	/**
	 * Refreshes the table with the appropriate input.
	 */
	public void refreshTable() {
		skuTableViewer.setInput(getInput());
		skuTableViewer.getSwtTableViewer().refresh();
	}

	private void openAddSkuWizard() {

		final AddSkuWizard wizard = new AddSkuWizard(CatalogMessages.get().AddSkuWizard_Title, this.getModel());

		final EpWizardDialog wizardDialog = new EpWizardDialog(shell, wizard);
		wizardDialog.addPageChangingListener(wizard);

		if (wizardDialog.open() == Window.OK) {
			final ProductSku newSku = ((AddSkuWizardPage1) wizard.getStartingPage()).getProductSku();

			getModel().addOrUpdateSku(newSku);
			skuTableViewer.getSwtTableViewer().refresh();

			if (controlModificationListener != null) {
				controlModificationListener.controlModified();
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

		final boolean answerYes = MessageDialog.openConfirm(shell,
				CatalogMessages.get().ProductEditorMultiSkuSection_RemoveConfirmation, CatalogMessages.get().ProductEditorMultiSkuSection_Question
				+
					NLS.bind(CatalogMessages.get().ProductEditorMultiSkuSection_Info,
					new Object[]{selectedSku.getSkuCode(),
				selectedSku.getDisplayName(CorePlugin.getDefault().getDefaultLocale())}));
		if (answerYes) {
			getModel().removeSku(selectedSku);
			ItemChangeEvent<ProductSku> event = new ItemChangeEvent<>(this, selectedSku, EventType.REMOVE);
			CatalogEventService.getInstance().notifyProductSkuChanged(event);
			skuTableViewer.getSwtTableViewer().refresh();
			
			if (controlModificationListener != null) {
				controlModificationListener.controlModified();
			}
		}
	}

	/**
	 * Label provider for the text and images.
	 */
	private class SkuLabelProvider extends LabelProvider implements ITableLabelProvider {

		private static final int SKU_CODE_COLUMN = 0;

		private static final int SKU_CONF_COLUMN = 1;

		private static final int DIGASSET_COLUMN = 5;

		private static final int SHIPPABLE_COLUMN = 4;

		private static final int ENABLE_DATE_COLUMN = 2;

		private static final int DISABLE_DATE_COLUMN = 3;

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			
			String result = CatalogMessages.EMPTY_STRING;
			final ProductSku sku = (ProductSku) element;
			if (sku != null) {

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
					result = getYesOrNo(sku.getDigitalAsset() != null);
					break;
				default:
					// do nothing
				}
			}
			
			if (result == null) {
				return CatalogMessages.EMPTY_STRING;
			}
			return result;
		}

	}

	@Override
	public void doubleClick(final DoubleClickEvent event) {
		//openProductSkuEditor();
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

	/**
	 * Add a selection listener to all buttons.
	 * 
	 * @param listener selection listener
	 */
	public void addButtonsSelectionListener(final SelectionListener listener) {
		addButton.addSelectionListener(listener);
		removeButton.addSelectionListener(listener);
	}

	
}


