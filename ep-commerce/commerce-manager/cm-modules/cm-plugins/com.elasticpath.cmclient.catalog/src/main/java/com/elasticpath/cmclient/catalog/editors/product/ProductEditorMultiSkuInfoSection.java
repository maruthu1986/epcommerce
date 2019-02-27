/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.catalog.editors.product;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.editors.product.ProductEditorMultiSkuSection.SkuLabelProvider;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * A section representing the changes done to SKUs in the product editor.
 */
public class ProductEditorMultiSkuInfoSection extends AbstractCmClientEditorPageSectionPart implements IPropertyListener {

	private static final String PRODUCT_EDITOR_MULTI_SKU_TABLE = "Product Editor Multi Sku"; //$NON-NLS-1$
	private IEpLayoutComposite mainEpComposite;
	private IEpTableViewer skuTableViewer;
	private final ProductEditor productEditor;

	/**
	 * Constructor.
	 *
	 * @param formPage the form page
	 * @param editor   the editor
	 */
	public ProductEditorMultiSkuInfoSection(final FormPage formPage, final ProductEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | Section.DESCRIPTION);
		this.productEditor = editor;
		// doesn't have to be visible if no elements are in this table. assuming there are no items which are not persisted
		getSection().setVisible(false);

		formPage.addPropertyListener(this);
		getSection().addDisposeListener((DisposeListener) event -> formPage.removePropertyListener(ProductEditorMultiSkuInfoSection.this));
	}


	@Override
	public void propertyChanged(final Object source, final int propertyId) {
		if (propertyId == ProductMultiSkuPage.SKU_CHANGED_EVENT_ID) {
			refreshTable();
			getSection().setVisible(true);
			getSection().setExpanded(true);
		}
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// nothing to bind
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		mainEpComposite = CompositeFactory.createGridLayoutComposite(client, 1, false);
		mainEpComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		skuTableViewer = mainEpComposite.addTableViewer(false, EpState.READ_ONLY,
				mainEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL,
					true, true), PRODUCT_EDITOR_MULTI_SKU_TABLE);

		final int[] columnSize = new int[]{60, 90, 160, 100, 100, 60, 75};
		final String[] columnName = new String[]{
				CatalogMessages.get().ProductEditorMultiSkuInfoSection_Change,
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

		skuTableViewer.setContentProvider(new NewSkusContentProvider());
		skuTableViewer.setLabelProvider(new ChangedSkuLabelProvider());

	}

	@Override
	protected void populateControls() {
		skuTableViewer.setInput(getModel());
	}

	/**
	 *
	 */
	protected void refreshTable() {
		skuTableViewer.getSwtTableViewer().refresh();
	}

	@Override
	protected String getSectionDescription() {
		return CatalogMessages.get().ProductEditorMultiSkuInfoSection_Description;
	}

	@Override
	protected String getSectionTitle() {
		return CatalogMessages.get().ProductEditorMultiSkuInfoSection_Title;
	}

	/**
	 * A SKU change enum.
	 */
	enum SkuChangeType {
		/**
		 * SKU added.
		 */
		ADDED,
		/**
		 * SKU removed.
		 */
		REMOVED
	}

	/**
	 * A wrapper object representing a change done on a SKU.
	 */
	class SkuChange {

		private final SkuChangeType changeType;
		private final ProductSku productSku;

		/**
		 * @return the changeType
		 */
		public SkuChangeType getChangeType() {
			return changeType;
		}

		/**
		 * @return the productSku
		 */
		public ProductSku getProductSku() {
			return productSku;
		}

		/**
		 * Constructor.
		 *
		 * @param productSku the product SKU
		 * @param changeType the change type
		 */
		SkuChange(final ProductSku productSku, final SkuChangeType changeType) {
			this.productSku = productSku;
			this.changeType = changeType;
		}
	}

	/**
	 * A label provider that uses the {@link SkuLabelProvider} as a delegate for all
	 * the columns except the one with index 0.
	 * Uses an offset of -1 in order to retrieve the right column from the delegate implementation.
	 */
	class ChangedSkuLabelProvider extends LabelProvider implements ITableLabelProvider {

		private final SkuLabelProvider delegate = new SkuLabelProvider();

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			final SkuChange skuChange = (SkuChange) element;
			if (columnIndex == 0) {
				if (skuChange.getChangeType() == SkuChangeType.ADDED) {
					return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_ADDED_SMALL);
				} else if (skuChange.getChangeType() == SkuChangeType.REMOVED) {
					return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_DELETED_SMALL);
				}
			}
			return delegate.getColumnImage(skuChange.getProductSku(), columnIndex - 1);
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final SkuChange skuChange = (SkuChange) element;
			return delegate.getColumnText(skuChange.getProductSku(), columnIndex - 1);
		}
	}

	/**
	 * Filters only the new SKUs and provides them to the viewer.
	 */
	class NewSkusContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(final Object input) {
			ProductModel productEditorModel = (ProductModel) input;
			Collection<SkuChange> changedSkus = new ArrayList<>();
			for (ProductSku productSku : productEditorModel.getProduct().getProductSkus().values()) {
				if (!productSku.isPersisted()) {
					changedSkus.add(new SkuChange(productSku, SkuChangeType.ADDED));
				}
			}
			for (ProductSku productSku : productEditor.getRemovedSkus()) {
				changedSkus.add(new SkuChange(productSku, SkuChangeType.REMOVED));

			}
			return changedSkus.toArray();
		}

		@Override
		public void dispose() {
			// nothing to dispose
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// nothing to do
		}
	}
}
