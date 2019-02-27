/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.taxes.views;

import java.util.List;

import org.apache.log4j.Logger;
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
import com.elasticpath.cmclient.admin.taxes.actions.CreateTaxJurisdictionAction;
import com.elasticpath.cmclient.admin.taxes.actions.DeleteTaxJurisdictionAction;
import com.elasticpath.cmclient.admin.taxes.actions.EditTaxJurisdictionAction;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractListView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.service.tax.TaxJurisdictionService;

/**
 * View to show and allow the manipulation of the available taxJurisdictions in CM.
 */
public class TaxJurisdictionsListView extends AbstractListView {

	/** The logger. */
	protected static final Logger LOG = Logger.getLogger(TaxJurisdictionsListView.class);

	/** The View's ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.admin.taxes.views.TaxJurisdictionListView"; //$NON-NLS-1$

	/** Column indices. */

	private static final int INDEX_JURISDICTION_COUNTRY = 0;

	private static final int INDEX_CALCULATION_METHOD = 1;

	private static final String TAX_JURISDICTION_TABLE = "Tax Jurisdiction"; //$NON-NLS-1$

	/** Actions. */
	private Action createTaxJurisdictionAction;

	private Action editTaxJurisdictionAction;

	private Action deleteTaxJurisdictionAction;

	/** Instance of <code>TaxJurisdictionService</code>. */
	private final TaxJurisdictionService taxJurisdictionService;

	/**
	 * The constructor.
	 */
	public TaxJurisdictionsListView() {
		super(false, TAX_JURISDICTION_TABLE);
		taxJurisdictionService = ServiceLocator.getService(ContextIdNames.TAX_JURISDICTION_SERVICE);
	}

	@Override
	protected Object[] getViewInput() {
		List<TaxJurisdiction> taxJurisdictionList = taxJurisdictionService.list();
		return taxJurisdictionList.toArray(new TaxJurisdiction[taxJurisdictionList.size()]);
	}

	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new TaxJurisdictionListViewLabelProvider();
	}

	@Override
	protected void initializeTable(final IEpTableViewer table) {
		String[] columnNames = new String[]{TaxesMessages.get().TaxJurisdictionCountryColumnLabel,
				TaxesMessages.get().TaxJurisdictionCalcMethodColumnLabel};

		final int[] columnWidths = new int[]{200, 200};
		
		for (int i = 0; i < columnNames.length; i++) {
			table.addTableColumn(columnNames[i], columnWidths[i]);
		}
	}

	@Override
	protected String getPluginId() {
		return TaxesPlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeViewToolbar() {

		Separator taxJurisdictionActionGroup = new Separator("taxJurisdictionActionGroup"); //$NON-NLS-1$
		getToolbarManager().add(taxJurisdictionActionGroup);

		createTaxJurisdictionAction = new CreateTaxJurisdictionAction(this, TaxesMessages.get().CreateTaxJurisdiction,
				TaxesImageRegistry.IMAGE_TAX_JURISDICTION_CREATE);
		createTaxJurisdictionAction.setToolTipText(TaxesMessages.get().CreateTaxJurisdiction);
		editTaxJurisdictionAction = new EditTaxJurisdictionAction(this, TaxesMessages.get().EditTaxJurisdiction,
				TaxesImageRegistry.IMAGE_TAX_JURISDICTION_EDIT);
		editTaxJurisdictionAction.setToolTipText(TaxesMessages.get().EditTaxJurisdiction);
		addDoubleClickAction(editTaxJurisdictionAction);
		deleteTaxJurisdictionAction = new DeleteTaxJurisdictionAction(this, TaxesMessages.get().DeleteTaxJurisdiction,
				TaxesImageRegistry.IMAGE_TAX_JURISDICTION_DELETE);
		deleteTaxJurisdictionAction.setToolTipText(TaxesMessages.get().DeleteTaxJurisdiction);

		ActionContributionItem createTaxJurisdictionActionContributionItem = new ActionContributionItem(createTaxJurisdictionAction);
		ActionContributionItem editTaxJurisdictionActionContributionItem = new ActionContributionItem(editTaxJurisdictionAction);
		ActionContributionItem deleteTaxJurisdictionActionContributionItem = new ActionContributionItem(deleteTaxJurisdictionAction);

		createTaxJurisdictionActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		editTaxJurisdictionActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		deleteTaxJurisdictionActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		getToolbarManager().appendToGroup(taxJurisdictionActionGroup.getGroupName(), editTaxJurisdictionActionContributionItem);
		getToolbarManager().appendToGroup(taxJurisdictionActionGroup.getGroupName(), createTaxJurisdictionActionContributionItem);
		getToolbarManager().appendToGroup(taxJurisdictionActionGroup.getGroupName(), deleteTaxJurisdictionActionContributionItem);
		// Disable buttons until a row is selected.
		editTaxJurisdictionAction.setEnabled(false);
		deleteTaxJurisdictionAction.setEnabled(false);

		this.getViewer().addSelectionChangedListener(event -> {
			final IStructuredSelection strSelection = (IStructuredSelection) event.getSelection();
			Object firstSelection = strSelection.getFirstElement();
			deleteTaxJurisdictionAction.setEnabled(firstSelection != null);
			editTaxJurisdictionAction.setEnabled(firstSelection != null);
		});
	}

	/**
	 * TaxJurisdiction list view label provider.
	 */
	protected class TaxJurisdictionListViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		private final Geography geography;

		/** Default constructor. */
		public TaxJurisdictionListViewLabelProvider() {
			geography = ServiceLocator.getService(ContextIdNames.GEOGRAPHY);
		}

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			TaxJurisdiction taxJurisdiction = (TaxJurisdiction) element;

			switch (columnIndex) {
			case TaxJurisdictionsListView.INDEX_JURISDICTION_COUNTRY:
				return geography.getCountryDisplayName(taxJurisdiction.getRegionCode(), CorePlugin.getDefault().getDefaultLocale());
			case TaxJurisdictionsListView.INDEX_CALCULATION_METHOD:
				String method;
				if (taxJurisdiction.getPriceCalculationMethod().equals(TaxJurisdiction.PRICE_CALCULATION_EXCLUSIVE)) {
					method = TaxesMessages.get().CalculationMethod_Exclusive;
				} else {
					method = TaxesMessages.get().CalculationMethod_Inclusive;
				}
				return method;
			default:
				return ""; //$NON-NLS-1$
			}
		}
	}

	/**
	 * Gets the currently-selected taxJurisdiction.
	 * 
	 * @return the currently-selected taxJurisdiction
	 */
	public TaxJurisdiction getSelectedTaxJurisdiction() {
		IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
		TaxJurisdiction taxJurisdiction = null;
		if (!selection.isEmpty()) {
			taxJurisdiction = (TaxJurisdiction) selection.getFirstElement();
		}
		return taxJurisdiction;
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
