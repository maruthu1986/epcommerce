/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.taxes.dialogs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import com.elasticpath.cmclient.admin.taxes.TaxesImageRegistry;
import com.elasticpath.cmclient.admin.taxes.TaxesMessages;
import com.elasticpath.cmclient.admin.taxes.TaxesPlugin;
import com.elasticpath.cmclient.admin.taxes.actions.CreateTaxValueAction;
import com.elasticpath.cmclient.admin.taxes.actions.DeleteTaxValueAction;
import com.elasticpath.cmclient.admin.taxes.actions.EditTaxValueAction;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCategoryTypeEnum;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.service.tax.TaxCodeService;
import com.elasticpath.service.tax.TaxJurisdictionService;

/**
 * Dialog for creating and editing Tax Values.
 */
public class ManageTaxValuesDialog extends AbstractEpDialog {

	private static final int MARGIN = 4;

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(ManageTaxValuesDialog.class);

	/** UI constants. */
	private static final int TAX_CODES_WIDTH = 70;

	private static final int REGION_NAME_WIDTH = 50;

	private static final int FILTER_GROUP_COLUMN = 2;

	private static final int TABLE_VALUES_HEIGHT = 60;

	private static final String TAX_VALUES_TABLE = "Tax Values"; //$NON-NLS-1$

	/** This dialog's title. */
	private final String title;

	/** This dialog's message. */
	private final String message;

	/** This dialog's image. */
	private final Image image;

	private boolean isRegionTypeCountry;

	private TaxCategory selectedTaxCategory;

	private TaxJurisdiction selectedTaxJurisdiction;

	private CCombo taxJurisdictionCombo;

	private CCombo taxCategoryCombo;

	private Button filterButton;

	private Button addTaxValueButton;

	private Button editTaxValueButton;

	private Button removeTaxValueButton;

	private IEpTableViewer taxValuesTableViewer;

	/**
	 * The constructor.
	 * 
	 * @param parentShell the parent Shell
	 * @param image the image for this dialog
	 * @param title the title for this dialog
	 * @param message the message for this dialog
	 */
	public ManageTaxValuesDialog(final Shell parentShell, final String title, final String message, final Image image) {
		super(parentShell, 2, false);
		this.title = title;
		this.message = message;
		this.image = image;
	}

	@Override
	protected String getInitialMessage() {
		return message;
	}

	@Override
	protected String getTitle() {
		return title;
	}

	@Override
	protected Image getWindowImage() {
		return image;
	}

	@Override
	protected String getWindowTitle() {
		return getTitle();
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {

		LOG.debug("ManageTaxValuesDialog.createEpDialogContent"); //$NON-NLS-1$
		IEpLayoutComposite group = dialogComposite.addGroup(TaxesMessages.get().ManageTaxValuesFilterGroup,
				FILTER_GROUP_COLUMN, false, dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1));

		IEpLayoutData groupLabel = group.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		IEpLayoutData groupData = group.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		IEpLayoutData buttonData = group.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, true, false, 2, 1);

		group.addLabelBoldRequired(TaxesMessages.get().ManageTaxValuesTaxJurisdictionLabel, EpState.EDITABLE, groupLabel);
		taxJurisdictionCombo = group.addComboBox(EpState.EDITABLE, groupData);
		taxJurisdictionCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				// do nothing
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				populateTaxCategoryCombo();
			}
		});

		group.addLabelBoldRequired(TaxesMessages.get().ManageTaxValuesTaxLabel, EpState.EDITABLE, groupLabel);
		taxCategoryCombo = group.addComboBox(EpState.EDITABLE, groupData);
		filterButton = group.addPushButton(TaxesMessages.get().ManageTaxValuesFilterButton, TaxesImageRegistry
				.getImage(TaxesImageRegistry.IMAGE_FILTER), EpState.EDITABLE, buttonData);

		addActionToButton(filterButton, new Action() {
			@Override
			public void run() {
				selectedTaxJurisdiction = (TaxJurisdiction) taxJurisdictionCombo.getData(taxJurisdictionCombo.getText());
				selectedTaxCategory = selectedTaxJurisdiction.getTaxCategory(taxCategoryCombo.getText());
				TableColumn tableColumn = taxValuesTableViewer.getSwtTable().getColumn(0);
				tableColumn.setText(CoreMessages.get().getMessage(selectedTaxCategory.getFieldMatchType().getName()));

				isRegionTypeCountry = (selectedTaxCategory.getFieldMatchType() == TaxCategoryTypeEnum.FIELD_MATCH_COUNTRY);
				addTaxValueButton.setEnabled(!isRegionTypeCountry);
				removeTaxValueButton.setEnabled(!isRegionTypeCountry);
				taxValuesTableViewer.setInput(selectedTaxCategory);

				taxValuesTableViewer.getSwtTable().notifyListeners(SWT.Selection, null);
			}
		});

		IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, false, false, 2, 1);

		dialogComposite.addLabelBold(TaxesMessages.get().ManageTaxValuesLabel, labelData);
		taxValuesTableViewer = dialogComposite.addTableViewer(false, EpState.EDITABLE, dialogComposite.createLayoutData(IEpLayoutData.FILL,
			IEpLayoutData.FILL, true, false, 1, MARGIN), TAX_VALUES_TABLE);
		((GridData) taxValuesTableViewer.getSwtTable().getLayoutData()).heightHint = TABLE_VALUES_HEIGHT;

		final List<Integer> taxValuesColumnWidths = new ArrayList<>();
		final List<String> taxValuesColumnNames = new ArrayList<>();
		TaxCodeService taxCodeService = ServiceLocator.getService(ContextIdNames.TAX_CODE_SERVICE);
		taxValuesColumnNames.add(""); //$NON-NLS-1$
		taxValuesColumnWidths.add(REGION_NAME_WIDTH);

		for (TaxCode taxCode : taxCodeService.list()) {
			taxValuesColumnNames.add(taxCode.toString());
			taxValuesColumnWidths.add(TAX_CODES_WIDTH);
		}

		for (int index = 0; index < taxValuesColumnNames.size(); index++) {
			taxValuesTableViewer.addTableColumn(taxValuesColumnNames.get(index), taxValuesColumnWidths.get(index));
		}

		taxValuesTableViewer.setContentProvider(new TaxValuesContentProvider());
		taxValuesTableViewer.setLabelProvider(new TaxValuesLabelProvider());

		addTaxValueButton = dialogComposite.addPushButton(TaxesMessages.get().TaxValueAddLabel, TaxesImageRegistry
				.getImage(TaxesImageRegistry.IMAGE_TAX_VALUE_CREATE), EpState.EDITABLE, fieldData);
		editTaxValueButton = dialogComposite.addPushButton(TaxesMessages.get().TaxValueEditLabel, TaxesImageRegistry
				.getImage(TaxesImageRegistry.IMAGE_TAX_VALUE_EDIT), EpState.EDITABLE, fieldData);
		removeTaxValueButton = dialogComposite.addPushButton(TaxesMessages.get().TaxValueRemoveLabel, TaxesImageRegistry
				.getImage(TaxesImageRegistry.IMAGE_TAX_VALUE_DELETE), EpState.EDITABLE, fieldData);

		dialogComposite.addEmptyComponent(fieldData);

		addActionToButton(addTaxValueButton, new CreateTaxValueAction(this, null, null));
		Action editAction = new EditTaxValueAction(this, null, null);
		addActionToButton(editTaxValueButton, editAction);
		addDoubleClickAction(editAction);
		addActionToButton(removeTaxValueButton, new DeleteTaxValueAction(this, null, null));

		taxValuesTableViewer.getSwtTableViewer().addSelectionChangedListener(event -> {
			final boolean editable = (getSelectedTaxRegion() != null);
			editTaxValueButton.setEnabled(editable);
			removeTaxValueButton.setEnabled(editable && !isRegionTypeCountry);
		});

		addTaxValueButton.setEnabled(false);
		editTaxValueButton.setEnabled(false);
		removeTaxValueButton.setEnabled(false);
	}

	@Override
	protected void bindControls() {
		// do nothing
	}

	@Override
	protected String getPluginId() {
		return TaxesPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return Arrays.asList(this.selectedTaxCategory, this.selectedTaxJurisdiction);
	}

	@Override
	protected void populateControls() {
		TaxJurisdictionService taxJurisdictionService = ServiceLocator.getService(
				ContextIdNames.TAX_JURISDICTION_SERVICE);
		Geography geography = ServiceLocator.getService(ContextIdNames.GEOGRAPHY);
		List<TaxJurisdiction> taxJurisdictionList = taxJurisdictionService.list();
		for (TaxJurisdiction taxJurisdiction : taxJurisdictionList) {
			String countryName = geography.getCountryDisplayName(taxJurisdiction.getRegionCode(), CorePlugin.getDefault().getDefaultLocale());
			if (countryName == null) {
				countryName = taxJurisdiction.getRegionCode();
			}
			taxJurisdictionCombo.setData(countryName, taxJurisdiction);
			taxJurisdictionCombo.add(countryName);
		}

		if (taxJurisdictionCombo.getItemCount() == 0) {
			filterButton.setEnabled(false);
		} else {
			taxJurisdictionCombo.select(0);
			taxJurisdictionCombo.notifyListeners(SWT.Selection, null);
		}

	}

	private void populateTaxCategoryCombo() {
		TaxJurisdiction taxJurisdiction = (TaxJurisdiction) taxJurisdictionCombo.getData(taxJurisdictionCombo.getText());
		Set<TaxCategory> taxCategorySet = taxJurisdiction.getTaxCategorySet();
		taxCategoryCombo.removeAll();

		String[] taxCategoryNames = new String[taxCategorySet.size()];
		int index = 0;
		for (TaxCategory taxCategory : taxCategorySet) {
			taxCategoryNames[index++] = taxCategory.getName();
		}

		for (String taxCategoryName : taxCategoryNames) {
			taxCategoryCombo.add(taxCategoryName);
		}

		if (taxCategoryCombo.getItemCount() == 0) {
			filterButton.setEnabled(false);
		} else {
			taxCategoryCombo.select(0);
			filterButton.setEnabled(true);
		}
	}

	private void addActionToButton(final Button button, final Action action) {
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				// do nothing
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				action.run();
			}
		});
	}

	/**
	 * Gets the selected tax region.
	 * 
	 * @return the selected TaxRegion
	 */
	public TaxRegion getSelectedTaxRegion() {
		TaxRegion taxRegion = null;
		IStructuredSelection selection = (IStructuredSelection) taxValuesTableViewer.getSwtTableViewer().getSelection();
		if (!selection.isEmpty()) {
			taxRegion = (TaxRegion) selection.getFirstElement();
		}

		return taxRegion;
	}

	/**
	 * Get selected Tax Category.
	 * 
	 * @return Selected TaxCategory
	 */
	public TaxCategory getSelectedTaxCategory() {
		return selectedTaxCategory;
	}

	/**
	 * Get selected Tax Jurisdiction.
	 * 
	 * @return Selected TaxJurisdiction
	 */
	public TaxJurisdiction getSelectedTaxJurisdiction() {
		return selectedTaxJurisdiction;
	}

	/**
	 * Refresh tax regions list view.
	 */
	public void refreshTaxRegions() {
		taxValuesTableViewer.getSwtTableViewer().refresh();
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpOkButton(parent, CoreMessages.get().AbstractEpDialog_ButtonOK, null);
	}

	private void addDoubleClickAction(final Action doubleClickAction) {
		taxValuesTableViewer.getSwtTableViewer().addDoubleClickListener(event -> {
			if (doubleClickAction.isEnabled()) {
				doubleClickAction.run();
			}
		});
	}

	/**
	 * Tax Values label provider.
	 */
	private class TaxValuesLabelProvider extends LabelProvider implements ITableLabelProvider {

		private static final int INDEX_TYPE_COLUMN = 0;

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			TaxRegion taxRegion = (TaxRegion) element;
			taxRegion.getTaxValuesMap();

			if (columnIndex == INDEX_TYPE_COLUMN) {
				return taxRegion.getRegionName();
			}

			TableColumn tableColumn = ManageTaxValuesDialog.this.taxValuesTableViewer.getSwtTable().getColumn(columnIndex);
			String taxValueCode = ""; //$NON-NLS-1$
			BigDecimal decimal = taxRegion.getValue(tableColumn.getText());
			if (decimal != null) {
				taxValueCode = String.valueOf(decimal.doubleValue() / TaxValueDialog.MULTIPLIER_TAX_VALUE);
			}

			return taxValueCode;
		}

	}

	/**
	 * Tax Values content provider.
	 */
	private static class TaxValuesContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(final Object inputElement) {
			TaxCategory taxCategory = (TaxCategory) inputElement;
			TaxRegion[] taxRegionArray = taxCategory.getTaxRegionSet().toArray(new TaxRegion[taxCategory.getTaxRegionSet().size()]);
			Arrays.sort(taxRegionArray, Comparator.comparing(TaxRegion::getRegionName));
			return taxRegionArray;
		}

		@Override
		public void dispose() {
			// do nothing
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// do nothing
		}
	}

}
