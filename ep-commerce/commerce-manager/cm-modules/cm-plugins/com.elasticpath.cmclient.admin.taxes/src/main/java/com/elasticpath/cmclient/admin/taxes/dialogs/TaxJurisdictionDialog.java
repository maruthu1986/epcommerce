/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.taxes.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.admin.taxes.TaxesImageRegistry;
import com.elasticpath.cmclient.admin.taxes.TaxesMessages;
import com.elasticpath.cmclient.admin.taxes.TaxesPlugin;
import com.elasticpath.cmclient.admin.taxes.actions.CreateTaxCategoryAction;
import com.elasticpath.cmclient.admin.taxes.actions.DeleteTaxCategoryAction;
import com.elasticpath.cmclient.admin.taxes.actions.EditTaxCategoryAction;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.Country;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.service.tax.TaxJurisdictionService;

/**
 * Dialog for creating and editing tax jurisdiction.
 */
@SuppressWarnings({ "PMD.PrematureDeclaration" })
public class TaxJurisdictionDialog extends AbstractEpDialog {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(TaxJurisdictionDialog.class);

	private static final String TAX_CATEGORIES_TABLE = "Tax Categories"; //$NON-NLS-1$

	/** This dialog's title. Depends from whether this is create or edit dialog */
	private final String title;

	/** This dialog's image. Depends from whether this is create or edit dialog */
	private final Image image;

	private final DataBindingContext dataBindingContext;

	/** List of Tax Justification countries. */
	private CCombo countryCombo;

	/** List of Calculation methods. */
	private CCombo calculationMethodCombo;

	/** Tax Categories IEpTableViewer. */
	private IEpTableViewer taxCategoriesTableViewer;

	/** Create Tax Category Button. */
	private Button createTaxButton;

	/** Edit Tax Category Button. */
	private Button editTaxButton;

	/** Remove Tax Category Button. */
	private Button deleteTaxButton;

	private Action createTaxAction;

	private Action editTaxAction;

	private Action deleteTaxAction;

	/** Current Tax Justification entity. */
	private final TaxJurisdiction taxJurisdiction;

	private final TaxJurisdictionService taxJurisdictionService;

	/**
	 * The constructor.
	 * 
	 * @param parentShell the parent Shell
	 * @param taxJurisdiction Tax Jurisdiction
	 * @param title the title for this dialog
	 * @param image the image for this dialog
	 */
	public TaxJurisdictionDialog(final Shell parentShell, final TaxJurisdiction taxJurisdiction, final String title, final Image image) {
		super(parentShell, 2, true);
		dataBindingContext = new DataBindingContext();
		this.taxJurisdiction = taxJurisdiction;
		this.title = title;
		this.image = image;
		taxJurisdictionService = ServiceLocator.getService(ContextIdNames.TAX_JURISDICTION_SERVICE);
	}

	/**
	 * Convenience method to open a create dialog.
	 * 
	 * @param parentShell the parent Shell
	 * @param taxJurisdiction the Tax Jurisdiction to create
	 * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
	 */
	public static boolean openCreateDialog(final Shell parentShell, final TaxJurisdiction taxJurisdiction) {
		final TaxJurisdictionDialog dialog = new TaxJurisdictionDialog(parentShell, taxJurisdiction,
				TaxesMessages.get().CreateTaxJurisdictionDialogTitle, TaxesImageRegistry.getImage(TaxesImageRegistry.IMAGE_TAX_JURISDICTION_CREATE));
		return dialog.open() == 0;
	}

	/**
	 * Convenience method to open an edit dialog.
	 * 
	 * @param parentShell the parent Shell
	 * @param taxJurisdiction the Tax Jurisdiction to edit
	 * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
	 */
	public static boolean openEditDialog(final Shell parentShell, final TaxJurisdiction taxJurisdiction) {
		final TaxJurisdictionDialog dialog = new TaxJurisdictionDialog(parentShell, taxJurisdiction,
				TaxesMessages.get().EditTaxJurisdictionDialogTitle, TaxesImageRegistry.getImage(TaxesImageRegistry.IMAGE_TAX_JURISDICTION_EDIT));
		return dialog.open() == 0;
	}

	@Override
	protected String getInitialMessage() {
		return TaxesMessages.get().TaxJurisdictionDialogInitialMessage;
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
		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData horizontalFill = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
		final IEpLayoutData horizontalFill2Cells = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);
		final IEpLayoutComposite fieldLabelComposite = dialogComposite.addGridLayoutComposite(2, false, horizontalFill2Cells);

		fieldLabelComposite.addLabelBoldRequired(TaxesMessages.get().TaxJurisdictionCountry, EpState.EDITABLE, labelData);
		if (isEditTaxJurisdiction()) {
			countryCombo = fieldLabelComposite.addComboBox(EpState.READ_ONLY, horizontalFill);
		} else {
			countryCombo = fieldLabelComposite.addComboBox(EpState.EDITABLE, horizontalFill);
		}

		fieldLabelComposite.addLabelBoldRequired(TaxesMessages.get().TaxJurisdictionCalculationMethod, EpState.EDITABLE, labelData);
		if (isEditTaxJurisdiction()) {
			calculationMethodCombo = fieldLabelComposite.addComboBox(EpState.READ_ONLY, horizontalFill);
		} else {
			calculationMethodCombo = fieldLabelComposite.addComboBox(EpState.EDITABLE, horizontalFill);
		}

		dialogComposite.addLabelBold(TaxesMessages.get().TaxJurisdictionConfigureTaxes, horizontalFill2Cells);

		final IEpLayoutData tableLayoutData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 1, 3);
		taxCategoriesTableViewer = dialogComposite.addTableViewer(false, EpState.EDITABLE, tableLayoutData, TAX_CATEGORIES_TABLE);
		final String[] taxValuesColumnNames = new String[] { TaxesMessages.get().TaxJurisdictionConfigureTaxes_TaxName,
				TaxesMessages.get().TaxJurisdictionConfigureTaxes_AddressField };
		
		final int[] taxCategoriesColumnWidths = new int[] { 100, 100 };
		
		for (int index = 0; index < taxValuesColumnNames.length; index++) {
			taxCategoriesTableViewer.addTableColumn(taxValuesColumnNames[index], taxCategoriesColumnWidths[index]);
		}
		taxCategoriesTableViewer.setContentProvider(new TaxCategoryListContentLabelProvider());
		taxCategoriesTableViewer.setLabelProvider(new TaxCategoryListViewLabelProvider());

		final IEpLayoutComposite buttonComposite = dialogComposite.addGridLayoutComposite(1, true, horizontalFill);
		createTaxButton = addPushButton(buttonComposite, horizontalFill, TaxesMessages.get().AddTax, CoreImageRegistry.IMAGE_ADD, true);
		editTaxButton = addPushButton(buttonComposite, horizontalFill, TaxesMessages.get().EditTax, CoreImageRegistry.IMAGE_EDIT, false);
		deleteTaxButton = addPushButton(buttonComposite, horizontalFill, TaxesMessages.get().RemoveTax, CoreImageRegistry.IMAGE_REMOVE, false);

		editTaxAction = new EditTaxCategoryAction(this, taxJurisdiction, TaxesMessages.get().EditTaxCategory, CoreImageRegistry.IMAGE_EDIT);
		createTaxAction = new CreateTaxCategoryAction(taxCategoriesTableViewer, taxJurisdiction, TaxesMessages.get().CreateTaxCategory,
				CoreImageRegistry.IMAGE_ADD);
		deleteTaxAction = new DeleteTaxCategoryAction(this, taxJurisdiction, TaxesMessages.get().DeleteTaxCategory, CoreImageRegistry.IMAGE_REMOVE);
		addListeners();
		addDoubleClickAction(editTaxAction);
	}

	@Override
	protected void bindControls() {
		if (isEditTaxJurisdiction()) {
			return;
		}

		final boolean hideDecorationOnFirstValidation = true;
		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		final ObservableUpdateValueStrategy countryUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				String country = (String) countryCombo.getData(countryCombo.getText());
				if (LOG.isDebugEnabled()) {
					LOG.debug("Tax jurisdiction country: " + country); //$NON-NLS-1$
				}
				taxJurisdiction.setRegionCode(country);
				return Status.OK_STATUS;
			}
		};

		final ObservableUpdateValueStrategy calcMethodUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				taxJurisdiction.setPriceCalculationMethod((Boolean) calculationMethodCombo.getData(calculationMethodCombo.getText()));
				return Status.OK_STATUS;
			}
		};

		binder.bind(dataBindingContext, countryCombo, null, null, countryUpdateStrategy, hideDecorationOnFirstValidation);

		binder.bind(dataBindingContext, calculationMethodCombo, null, null, calcMethodUpdateStrategy, hideDecorationOnFirstValidation);

		EpDialogSupport.create(this, dataBindingContext);
		dataBindingContext.updateModels();
	}

	@Override
	protected String getPluginId() {
		return TaxesPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return taxJurisdiction;
	}

	@Override
	protected void populateControls() {
		Geography geography = ServiceLocator.getService(ContextIdNames.GEOGRAPHY);

		if (isEditTaxJurisdiction()) {
			String countryDisplayName = geography.getCountryDisplayName(taxJurisdiction.getRegionCode(), CorePlugin.getDefault().getDefaultLocale());
			countryCombo.add(countryDisplayName);
			countryCombo.select(0);

			countryCombo.setEnabled(false);

			String priceCalculationMethod;
			if (taxJurisdiction.getPriceCalculationMethod().equals(TaxJurisdiction.PRICE_CALCULATION_INCLUSIVE)) {
				priceCalculationMethod = TaxesMessages.get().CalculationMethod_Inclusive;
			} else {
				priceCalculationMethod = TaxesMessages.get().CalculationMethod_Exclusive;
			}
			calculationMethodCombo.add(priceCalculationMethod);
			calculationMethodCombo.select(0);

			taxCategoriesTableViewer.setInput(taxJurisdiction);
		} else {
			Set<String> countryCodes = geography.getCountryCodes();
			countryCodes.removeAll(taxJurisdictionService.getCountryCodesInUse());

			List<Country> countries = new ArrayList<>();
			for (String countryCode : countryCodes) {
				countries.add(new Country(countryCode, geography.getCountryDisplayName(countryCode, CorePlugin.getDefault().getDefaultLocale())));
			}
			Collections.sort(countries);

			for (Country country : countries) {
				countryCombo.setData(country.getCountryName(), country.getCountryCode());
				countryCombo.add(country.getCountryName());
			}

			calculationMethodCombo.setData(TaxesMessages.get().CalculationMethod_Exclusive, TaxJurisdiction.PRICE_CALCULATION_EXCLUSIVE);
			calculationMethodCombo.setData(TaxesMessages.get().CalculationMethod_Inclusive, TaxJurisdiction.PRICE_CALCULATION_INCLUSIVE);
			calculationMethodCombo.add(TaxesMessages.get().CalculationMethod_Exclusive);
			calculationMethodCombo.add(TaxesMessages.get().CalculationMethod_Inclusive);

			countryCombo.select(0);
			calculationMethodCombo.select(0);
			taxCategoriesTableViewer.setInput(taxJurisdiction);
		}
	}

	/**
	 * This method adds push button to the specified <code>IEpLayoutComposite</code> composite.
	 * 
	 * @param composite composite that will contain button.
	 * @param layout composite layout.
	 * @param text text to show on button.
	 * @param descriptor image to place on button.
	 * @param enabled enabled
	 * @return button
	 */
	private Button addPushButton(final IEpLayoutComposite composite, final IEpLayoutData layout, final String text,
			final ImageDescriptor descriptor, final boolean enabled) {
		Button taxButton = composite.addPushButton("", EpState.EDITABLE, layout); //$NON-NLS-1$
		taxButton.setImage(CoreImageRegistry.getImage(descriptor));
		taxButton.setText(text);
		taxButton.setEnabled(enabled);
		return taxButton;
	}

	private void addListeners() {
		taxCategoriesTableViewer.getSwtTableViewer().addSelectionChangedListener(event -> {
			final IStructuredSelection strSelection = (IStructuredSelection) event.getSelection();
			Object firstSelection = strSelection.getFirstElement();
			deleteTaxButton.setEnabled(firstSelection != null);
			editTaxButton.setEnabled(firstSelection != null);
		});
		createTaxButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				createTaxAction.run();
			}
		});

		editTaxButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				editTaxAction.run();
			}
		});
		deleteTaxButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				deleteTaxAction.run();
			}
		});

	}

	private void addDoubleClickAction(final Action doubleClickAction) {
		taxCategoriesTableViewer.getSwtTableViewer().addDoubleClickListener(event -> {
			if (doubleClickAction.isEnabled()) {
				doubleClickAction.run();
			}
		});
	}

	/**
	 * @return true if it is edit dialog
	 */
	private boolean isEditTaxJurisdiction() {
		return taxJurisdiction.isPersisted();
	}

	/**
	 * Get selected Tax Category.
	 * 
	 * @return the selected tax category
	 */
	public TaxCategory getSelectedTaxCategory() {
		IStructuredSelection selection = (IStructuredSelection) taxCategoriesTableViewer.getSwtTableViewer().getSelection();
		if (selection.isEmpty()) {
			return null;
		}
		return (TaxCategory) selection.getFirstElement();
	}

	/**
	 * Refresh tax categories list view.
	 */
	public void refreshTaxCategory() {
		taxCategoriesTableViewer.getSwtTableViewer().refresh();
	}

	/**
	 * TaxCategory list view content provider.
	 */
	protected class TaxCategoryListContentLabelProvider implements IStructuredContentProvider {

		/**
		 * The constructor.
		 */
		public TaxCategoryListContentLabelProvider() {
			// none
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			LOG.debug("The view's input has been changed"); //$NON-NLS-1$
		}

		@Override
		public void dispose() {
			// nothing
		}

		@Override
		public Object[] getElements(final Object inputElement) {
			TaxJurisdiction jurisdiction = (TaxJurisdiction) inputElement;
			return jurisdiction.getTaxCategorySet().toArray(new TaxCategory[0]);
		}
	}

	/**
	 * TaxCategory list view label provider.
	 */
	protected class TaxCategoryListViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			TaxCategory category = (TaxCategory) element;
			switch (columnIndex) {
			case 0:
				return category.getName();
			case 1:
				return CoreMessages.get().getMessage(category.getFieldMatchType().getName());
			default:
				return ""; //$NON-NLS-1$
			}
		}
	}

}
