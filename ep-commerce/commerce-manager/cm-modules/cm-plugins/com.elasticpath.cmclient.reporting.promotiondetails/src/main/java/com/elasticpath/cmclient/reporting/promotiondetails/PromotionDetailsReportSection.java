/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.reporting.promotiondetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.reporting.AbstractReportSection;
import com.elasticpath.cmclient.reporting.promotiondetails.parameters.PromotionDetailsParameters;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.store.Store;

/**
 * A report section.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.PrematureDeclaration" })
public class PromotionDetailsReportSection extends AbstractReportSection implements ISelectionChangedListener {

	private static final int TABLE_WIDTH = 200;

	private static final int COMBOVIEWER_HEIGHT = 20;

	private IEpLayoutComposite parentEpComposite;

	private ComboViewer storeCombo;

	private ComboViewer currencyCombo;

	private IEpDateTimePicker fromDatePicker;

	private IEpDateTimePicker toDatePicker;

	private ComboViewer promotionCombo;

	private final PromotionDetailsReportParametersRetriever parametersRetriever = new PromotionDetailsReportParametersRetriever();

	private Text couponCodeText;

	/**
	 * Section id.
	 */
	public static final String SECTION_ID = 
		"com.elasticpath.cmclient.reporting.promotiondetails.PromotionDetailsReportSection"; //$NON-NLS-1$

	private final PromotionDetailsParameters parameters = new PromotionDetailsParameters();

	/** {@inheritDoc} */
	@Override
	public void bindControlsInternal(final EpControlBindingProvider bindingProvider, final DataBindingContext context) {

		bindStoreCombo(bindingProvider, context, true);
		bindCurrencyCombo(bindingProvider, context, true);

		bindDates(context);

		bindPromotionCode(bindingProvider, context, true);
		bindCouponText(bindingProvider, context, true);

		// from-to date interbinding for from before to date validation
		final ModifyListener updateModels = new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				context.updateModels(); // re-validate bound events
				updateButtonsStatus();
			}
		};
		fromDatePicker.getSwtText().addModifyListener(updateModels);
		toDatePicker.getSwtText().addModifyListener(updateModels);
	}

	private void bindStoreCombo(final EpControlBindingProvider bindingProvider, final DataBindingContext context,
			final boolean hideDecorationOnFirstValidation) {
		final ObservableUpdateValueStrategy storeComboUpdateStrategy = new ObservableUpdateValueStrategy() {
			/** {@inheritDoc} */
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				parameters.setStoreUidPk(((Store) getSelectedObject(storeCombo)).getUidPk());
				return Status.OK_STATUS;
			}

		};
		bindingProvider.bind(context, storeCombo.getCCombo(), null, null, storeComboUpdateStrategy, hideDecorationOnFirstValidation);

	}

	private void bindPromotionCode(final EpControlBindingProvider bindingProvider, final DataBindingContext context,
			final boolean hideDecorationOnFirstValidation) {
		final ObservableUpdateValueStrategy storeComboUpdateStrategy = new ObservableUpdateValueStrategy() {
			/** {@inheritDoc} */
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				if (!promotionCombo.getSelection().isEmpty()) {
					parameters.setPromotionCode(((Rule) getSelectedObject(promotionCombo)).getCode());
				}
				return Status.OK_STATUS;
			}
		};
		bindingProvider.bind(context, promotionCombo.getCCombo(), null, null, storeComboUpdateStrategy, hideDecorationOnFirstValidation);

	}

	private void bindCurrencyCombo(final EpControlBindingProvider bindingProvider, final DataBindingContext context,
			final boolean hideDecorationOnFirstValidation) {
		final ObservableUpdateValueStrategy storeComboUpdateStrategy = new ObservableUpdateValueStrategy() {
			/** {@inheritDoc} */
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				if (currencyCombo.getCCombo().getSelectionIndex() > 0) {
					parameters.setCurrencyCode((String) getSelectedObject(currencyCombo));
				} else {
					parameters.setCurrencyCode(null);
				}
				return Status.OK_STATUS;
			}
		};
		bindingProvider.bind(context, currencyCombo.getCCombo(), null, null, storeComboUpdateStrategy, hideDecorationOnFirstValidation);
	}

	private void bindCouponText(final EpControlBindingProvider bindingProvider, final DataBindingContext context,
			final boolean hideDecorationOnFirstValidation) {
		final ObservableUpdateValueStrategy storeComboUpdateStrategy = new ObservableUpdateValueStrategy() {
			/** {@inheritDoc} */
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				if (StringUtils.isEmpty(couponCodeText.getText())) {
					parameters.setCouponCode(null);
				} else {
					parameters.setCouponCode(couponCodeText.getText());
				}
				return Status.OK_STATUS;
			}
		};
		bindingProvider.bind(context, couponCodeText, null, null, storeComboUpdateStrategy, hideDecorationOnFirstValidation);
	}

	/** {@inheritDoc} */
	public void createControl(final FormToolkit toolkit, final Composite parent, final IWorkbenchPartSite site) {
		EpState state = EpState.EDITABLE;
		if (!isAuthorized()) {
			state = EpState.DISABLED;
		}

		parentEpComposite = CompositeFactory.createGridLayoutComposite(parent, 1, false);
		final IEpLayoutData data = parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		final GridData comboViewerData = new GridData(GridData.FILL, GridData.FILL, true, true);
		comboViewerData.widthHint = TABLE_WIDTH;
		comboViewerData.heightHint = COMBOVIEWER_HEIGHT;

		parentEpComposite.addLabelBoldRequired(PromotionDetailsMessages.store, EpState.EDITABLE, null);

		storeCombo = new ComboViewer(parentEpComposite.addComboBox(state, data));
		storeCombo.getCCombo().setLayoutData(comboViewerData);
		storeCombo.setContentProvider(new ArrayContentProvider());
		storeCombo.setLabelProvider(new LabelProvider() {
			/** {@inheritDoc} */
			@Override
			public String getText(final Object element) {
				Store store = (Store) element;
				return store.getName();
			}
		});
		storeCombo.addSelectionChangedListener(this);
		storeCombo.setComparator(new ViewerComparator() {
			/** {@inheritDoc} */
			@Override
			public int compare(final Viewer viewer, final Object object1, final Object object2) {
				return String.CASE_INSENSITIVE_ORDER.compare(((Store) object1).getName(), ((Store) object2).getName());
			}
		});

		parentEpComposite.addLabelBold(PromotionDetailsMessages.currency, null);
		currencyCombo = new ComboViewer(parentEpComposite.addComboBox(EpState.DISABLED, data));
		currencyCombo.setContentProvider(new ArrayContentProvider());
		currencyCombo.setLabelProvider(new LabelProvider());
		currencyCombo.getCCombo().setLayoutData(comboViewerData);
		currencyCombo.setComparator(new ViewerComparator() {
			/** {@inheritDoc} */
			@Override
			public int compare(final Viewer viewer, final Object object1, final Object object2) {
				String currency1 = (String) object1;
				String currency2 = (String) object2;

				if (StringUtils.equalsIgnoreCase(currency1, PromotionDetailsMessages.selectAll)) {
					return -1;
				}

				if (StringUtils.equalsIgnoreCase(currency2, PromotionDetailsMessages.selectAll)) {
					return 1;
				}

				return String.CASE_INSENSITIVE_ORDER.compare(currency1, currency2);
			}
		});

		parentEpComposite.addLabelBold(PromotionDetailsMessages.fromdate, null);
		fromDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);

		parentEpComposite.addLabelBoldRequired(PromotionDetailsMessages.todate, EpState.EDITABLE, data);
		toDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);

		parentEpComposite.addLabelBoldRequired(PromotionDetailsMessages.Promotion, EpState.EDITABLE, data);
		promotionCombo = new ComboViewer(parentEpComposite.addComboBox(EpState.DISABLED, data));
		promotionCombo.getCCombo().setLayoutData(comboViewerData);
		promotionCombo.setContentProvider(new ArrayContentProvider());
		promotionCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				Rule rule = (Rule) element;
				return rule.getName();
			}
		});
		promotionCombo.setComparator(new ViewerComparator() {
			/** {@inheritDoc} */
			@Override
			public int compare(final Viewer viewer, final Object object1, final Object object2) {
				return String.CASE_INSENSITIVE_ORDER.compare(((Rule) object1).getName(), ((Rule) object2).getName());
			}
		});

		parentEpComposite.addLabelBold(PromotionDetailsMessages.CouponCode, data);
		couponCodeText = parentEpComposite.addTextField(state, data);

		populateControls();

		parentEpComposite.getSwtComposite().getParent().layout();
	}

	/** {@inheritDoc} */
	public Map<String, Object> getParameters() {

		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put(PromotionDetailsParameters.DETAILS_PARAMETERS, parameters);

		return paramsMap;
	}

	/** {@inheritDoc} */
	public String getReportTitle() {
		return PromotionDetailsMessages.reportTitle;
	}

	/** {@inheritDoc} */
	public boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(PromotionDetailsReportPermissions.REPORTING_PROMOTION_DETAILS_MANAGE);
	}

	private void bindDates(final DataBindingContext context) {
		// from-to date interbinding for from before to date validation
		final ModifyListener updateModels = new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				context.updateModels(); // re-validate bound events
				updateButtonsStatus();
			}
		};
		fromDatePicker.getSwtText().addModifyListener(updateModels);
		toDatePicker.getSwtText().addModifyListener(updateModels);


		fromDatePicker.bind(context, EpValidatorFactory.DATE_TIME, parameters, "startDate"); //$NON-NLS-1$

		IValidator toValidator = new CompoundValidator(new IValidator[] { EpValidatorFactory.DATE_TIME_REQUIRED,
				EpValidatorFactory.createToDateValidator(fromDatePicker, toDatePicker) });
		toDatePicker.bind(context, toValidator, parameters, "endDate"); //$NON-NLS-1$
	}

	private List<String> convertCurrencies(final Store store) {
		List<String> allCurrencies = new ArrayList<String>();
		allCurrencies.add(PromotionDetailsMessages.selectAll);

		if (store != null) {
			for (Currency currency : store.getSupportedCurrencies()) {
				allCurrencies.add(currency.getCurrencyCode());
			}
		}

		return allCurrencies;
	}

	@Override
	public boolean isInputValid() {
		boolean storeSelected = !storeCombo.getSelection().isEmpty();
		boolean promotionSelected = promotionCombo.getInput() == null || !promotionCombo.getSelection().isEmpty();
		if (promotionCombo.getInput() instanceof Collection<?>) {
			promotionSelected |= ((Collection<?>) promotionCombo.getInput()).isEmpty();
		}
		return storeSelected && promotionSelected && super.isInputValid();
	}

	/**
	 * Populates the controls.
	 */
	protected void populateControls() {
		storeCombo.setInput(parametersRetriever.getAvailableStores().toArray());
		storeCombo.getCCombo().setText(PromotionDetailsMessages.selectAStore);
		toDatePicker.setDate(new Date());
		currencyCombo.setInput(convertCurrencies(null));
		promotionCombo.setInput(null);
	}

	/** {@inheritDoc} */
	public void selectionChanged(final SelectionChangedEvent event) {
		Store store = (Store) getSelectedObject(storeCombo);

		currencyCombo.setInput(convertCurrencies(store));
		promotionCombo.setInput(parametersRetriever.getAvailablePromotions(store.getCode()));

		selectFirstItem(currencyCombo.getCCombo());
		selectFirstItem(promotionCombo.getCCombo());

		enableCombo(currencyCombo.getCCombo());
		enableCombo(promotionCombo.getCCombo());
		updateButtonsStatus();
	}

	private Object getSelectedObject(final ComboViewer comboViewer) {
		IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
		return selection.getFirstElement();
	}

	private void enableCombo(final CCombo combo) {
		EpControlFactory.changeEpState(combo, EpState.EDITABLE);
	}

	private void selectFirstItem(final CCombo combo) {
		if (combo.getItemCount() > 0) {
			combo.select(0);
		}
	}

	@Override
	public void refreshLayout() {
		// not used
	}
}
