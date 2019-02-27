/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.taxes.views;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.admin.taxes.TaxesImageRegistry;
import com.elasticpath.cmclient.admin.taxes.TaxesMessages;
import com.elasticpath.cmclient.admin.taxes.TaxesPlugin;
import com.elasticpath.cmclient.admin.taxes.actions.CreateTaxCodeAction;
import com.elasticpath.cmclient.admin.taxes.actions.DeleteTaxCodeAction;
import com.elasticpath.cmclient.admin.taxes.actions.EditTaxCodeAction;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractListView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.tax.TaxCodeService;

/**
 * View to show and allow the manipulation of the available tax codes in CM.
 */
public class TaxCodeListView extends AbstractListView {
	/** The view ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.admin.taxes.views.TaxCodeListView"; //$NON-NLS-1$

	private static final int INDEX_TAX_CODE = 0;

	private static final String TAX_CODE_TABLE = "Tax Code"; //$NON-NLS-1$

	private final TaxCodeService taxCodeService;

	private Action createTaxCodeAction;

	private Action editTaxCodeAction;

	private Action deleteTaxCodeAction;

	/**
	 * The constructor.
	 */
	public TaxCodeListView() {
		super(false, TAX_CODE_TABLE);
		taxCodeService = ServiceLocator.getService(ContextIdNames.TAX_CODE_SERVICE);
	}

	@Override
	protected String getPluginId() {
		return TaxesPlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeViewToolbar() {
		final Separator taxCodesActionGroup = new Separator("taxCodeActionGroup"); //$NON-NLS-1$

		getToolbarManager().add(taxCodesActionGroup);

		createTaxCodeAction = new CreateTaxCodeAction(this, TaxesMessages.get().CreateTaxCode, TaxesImageRegistry.IMAGE_TAX_CODE_CREATE);
		createTaxCodeAction.setToolTipText(TaxesMessages.get().CreateTaxCode);

		editTaxCodeAction = new EditTaxCodeAction(this, TaxesMessages.get().EditTaxCode, TaxesImageRegistry.IMAGE_TAX_CODE_EDIT);
		editTaxCodeAction.setToolTipText(TaxesMessages.get().EditTaxCode);
		editTaxCodeAction.setEnabled(false);
		addDoubleClickAction(editTaxCodeAction);

		deleteTaxCodeAction = new DeleteTaxCodeAction(this, TaxesMessages.get().DeleteTaxCode, TaxesImageRegistry.IMAGE_TAX_CODE_DELETE);
		deleteTaxCodeAction.setToolTipText(TaxesMessages.get().DeleteTaxCode);
		deleteTaxCodeAction.setEnabled(false);

		final ActionContributionItem createTaxCodeActionContributionItem = new ActionContributionItem(createTaxCodeAction);
		final ActionContributionItem editTaxCodeActionContributionItem = new ActionContributionItem(editTaxCodeAction);
		final ActionContributionItem removeTaxCodeActionContributionItem = new ActionContributionItem(deleteTaxCodeAction);

		createTaxCodeActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		editTaxCodeActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		removeTaxCodeActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		getToolbarManager().appendToGroup(taxCodesActionGroup.getGroupName(), editTaxCodeActionContributionItem);
		getToolbarManager().appendToGroup(taxCodesActionGroup.getGroupName(), createTaxCodeActionContributionItem);
		getToolbarManager().appendToGroup(taxCodesActionGroup.getGroupName(), removeTaxCodeActionContributionItem);
	}

	@Override
	protected void initializeTable(final IEpTableViewer viewerTable) {
		final String[] columnNames = new String[]{TaxesMessages.get().TaxCode};

		final int[] columnWidths = new int[]{300};

		for (int i = 0; i < columnNames.length; i++) {
			viewerTable.addTableColumn(columnNames[i], columnWidths[i]);
		}
		viewerTable.getSwtTableViewer().addSelectionChangedListener(event -> {
			editTaxCodeAction.setEnabled(getSelectedTaxCode() != null);
			deleteTaxCodeAction.setEnabled(getSelectedTaxCode() != null);
		});
	}

	@Override
	protected Object[] getViewInput() {
		final List<TaxCode> taxCodeList = taxCodeService.list();
		final TaxCode[] taxCodesArray = taxCodeList.toArray(new TaxCode[taxCodeList.size()]);
		Arrays.sort(taxCodesArray, Comparator.comparing(TaxCode::getCode));
		return taxCodesArray;
	}

	/**
	 * Gets the currently-selected tax code.
	 * 
	 * @return the currently-selected TaxCode
	 */
	public TaxCode getSelectedTaxCode() {
		final IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
		TaxCode taxCode = null;
		if (!selection.isEmpty()) {
			taxCode = (TaxCode) selection.getFirstElement();
		}
		return taxCode;
	}

	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new TaxCodeListViewLabelProvider();
	}

	/**
	 * Label provider for the view.
	 */
	protected class TaxCodeListViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		/**
		 * Get the image to put in each column.
		 * 
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the Image to put in the column
		 */
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		/**
		 * Get the text to put in each column.
		 * 
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the String to put in the column
		 */
		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final TaxCode taxCode = (TaxCode) element;
			if (columnIndex == INDEX_TAX_CODE) {
				return taxCode.getCode();
			}

			return ""; //$NON-NLS-1$
		}
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
