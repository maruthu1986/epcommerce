/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.conversion.EpStringToBigDecimalConverter;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.ui.framework.IEpViewPart;
import com.elasticpath.cmclient.core.ui.util.EpWidgetUtil;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyTargetImpl;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.cmclient.pricelistmanager.controller.PriceListEditorController;
import com.elasticpath.common.pricing.service.BaseAmountFilterExt;
import com.elasticpath.domain.pricing.BaseAmountObjectType;


/**
 * Abstract base amount search / filter section.
 *
 */
public abstract class AbstractBaseAmountFilterSection extends AbstractStatePolicyTargetImpl   implements IEpViewPart {
	
	private static final int QUANTITY_TEXT_WIDTH = 20;

	private static final int QUANTITY_TEXT_LIMIT = 3;

	private static final int DEFAULT_WIDTH = 100;
	
	private static final int DEFAULT_NUMBER_WIDTH = 70;
	
	private CCombo objectTypeCombo;
	
	private Text  objectGuidText;
	
	private Text  lowestPriceText;
	
	private Text  highestPriceText;
	
	private Text  quantityText;
	
	private Button searchButton;

	private Button clearButton;
	
	private SelectionAdapter searchListener;
	
	private final TraverseListener traverseListener = new TraverseListener() {
		public void keyTraversed(final TraverseEvent event) {
			if (event.detail == SWT.TRAVERSE_RETURN) {
				searchListener.widgetSelected(null);
			}
		}
	};

	/**
	 * Table viewer.
	 */
	private IEpTableViewer baseAmountTableViewer;
	
	/**
	 * Search / ui filter filter.
	 */
	private final BaseAmountFilterExt filterExt;
	
	/**
	 * Controller.
	 */
	private final PriceListEditorController controller;

	private EpValueBinding objectTypeBinding;

	private EpValueBinding objectGuidBinding;

	private EpValueBinding lowestPriceBinding;

	private EpValueBinding highestPriceBinding;

	private EpValueBinding quantityBinding;
	
	private DataBindingContext bindingContext;
	
	/**
	 * 
	 * @param controller extended base amount filter from controller.
	 * @param filterExt extended search or filter filter.
	 */
	public AbstractBaseAmountFilterSection(final PriceListEditorController controller, final BaseAmountFilterExt filterExt) {
		this.controller = controller;
		this.filterExt = filterExt;
	}
	
	/**
	 * Remove all applied to table viewer filters. 
	 */
	protected void removeAllViewerFilter() {
		ViewerFilter [] appliedFilters = getBaseAmountTableViewer().getSwtTableViewer().getFilters();
		if (appliedFilters != null) {
			for (ViewerFilter filter : appliedFilters) {
				getBaseAmountTableViewer().getSwtTableViewer().removeFilter(filter);						
			}
		}		
	}


	@Override
	public String getTargetIdentifier() {
		return "baseAmountFilterSection";  //$NON-NLS-1$
	}

	/**
	 * Set table viewer, that need to be refreshed.
	 * @param baseAmountTableViewer table viewer
	 */
	public void setBaseAmountTableViewer(final IEpTableViewer baseAmountTableViewer) {
		this.baseAmountTableViewer = baseAmountTableViewer;
	}	
	
	/**
	 * 
	 * @return label for search button
	 */
	protected abstract String getSearchButtonLabel();

	@Override
	public void bindControls(final DataBindingContext bindingContext) {
		
		this.bindingContext = bindingContext;
		EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
		
		final ObservableUpdateValueStrategy objectTypeUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				Integer selectedItem = (Integer) newValue;
				switch(selectedItem) {
					case 1:
						filterExt.setObjectType(BaseAmountObjectType.PRODUCT.toString());
						break;
					case 2:
						filterExt.setObjectType(BaseAmountObjectType.SKU.toString());
						break;
					default:
						filterExt.setObjectType(null);
						break;
				}
				return Status.OK_STATUS;
			}
		};
		
		objectTypeBinding = bindingProvider.bind(bindingContext, objectTypeCombo, null, null, objectTypeUpdateStrategy, false);
		
		
		objectGuidBinding = bindingProvider.bind(bindingContext, objectGuidText, filterExt, "objectGuid", //$NON-NLS-1$ 
				EpValidatorFactory.MAX_LENGTH_64, null, true);
		
		lowestPriceBinding = bindingProvider.bind(bindingContext, lowestPriceText, filterExt, "lowestPrice", //$NON-NLS-1$ 
				EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, new EpStringToBigDecimalConverter(), true);
		
		highestPriceBinding = bindingProvider.bind(bindingContext, highestPriceText, filterExt, "highestPrice", //$NON-NLS-1$ 
				EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, new EpStringToBigDecimalConverter(), true);
		
		quantityBinding = bindingProvider.bind(bindingContext, quantityText, filterExt, "quantity", //$NON-NLS-1$ 
				EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, new EpStringToBigDecimalConverter(), true);
		
		
	}

	@Override
	public void createControls(final IEpLayoutComposite client, final IEpLayoutData data) {
		
		final IEpLayoutComposite layoutComposite = client.addGridLayoutComposite(10, false, data);
		((GridLayout) layoutComposite.getSwtComposite().getLayout()).verticalSpacing = 0;
		((GridLayout) layoutComposite.getSwtComposite().getLayout()).marginHeight = 2;
		((GridLayout) layoutComposite.getSwtComposite().getLayout()).marginWidth = 0;
		
		objectTypeCombo = layoutComposite.addComboBox(EpState.EDITABLE, createLayoutData(client));
		objectTypeCombo.add(PriceListManagerMessages.get().PriceListBaseAmountFilter_AllTypes, 0);
		objectTypeCombo.add(PriceListManagerMessages.get().PriceListBaseAmountFilter_Product, 1);
		objectTypeCombo.add(PriceListManagerMessages.get().PriceListBaseAmountFilter_Sku, 2);
		setMinimumControlWidth(objectTypeCombo, EpWidgetUtil.computeWidth(objectTypeCombo));
		
		objectGuidText = layoutComposite.addTextField(EpState.EDITABLE, createLayoutData(client));
		setMinimumControlWidth(objectGuidText, DEFAULT_WIDTH);
		objectGuidText.addTraverseListener(traverseListener);
		
		layoutComposite.addLabel(PriceListManagerMessages.get().PriceListBaseAmountFilter_PriceFrom, createLayoutData(client));
		lowestPriceText = layoutComposite.addTextField(EpState.EDITABLE, createLayoutData(client));
		setMinimumControlWidth(lowestPriceText, DEFAULT_NUMBER_WIDTH);
		lowestPriceText.addTraverseListener(traverseListener);
		
		layoutComposite.addLabel(PriceListManagerMessages.get().PriceListBaseAmountFilter_PriceTo, createLayoutData(client));
		highestPriceText = layoutComposite.addTextField(EpState.EDITABLE, createLayoutData(client));
		setMinimumControlWidth(highestPriceText, DEFAULT_NUMBER_WIDTH);
		highestPriceText.addTraverseListener(traverseListener);
		
		layoutComposite.addLabel(PriceListManagerMessages.get().PriceListBaseAmountFilter_Quantity, createLayoutData(client));
		quantityText = layoutComposite.addTextField(EpState.EDITABLE, createLayoutData(client));
		setMinimumControlWidth(quantityText, DEFAULT_NUMBER_WIDTH);
		quantityText.addTraverseListener(traverseListener);
		quantityText.setTextLimit(QUANTITY_TEXT_LIMIT);
		((GridData) quantityText.getLayoutData()).widthHint = QUANTITY_TEXT_WIDTH;
		
		searchButton = layoutComposite.addPushButton(getSearchButtonLabel(), EpState.EDITABLE, createLayoutData(client));
		setMinimumControlWidth(searchButton, DEFAULT_WIDTH);
		
		clearButton = layoutComposite.addPushButton(PriceListManagerMessages.get().PriceListBaseAmountFilter_Clear,
				EpState.EDITABLE, createLayoutData(client));
		setMinimumControlWidth(clearButton, DEFAULT_WIDTH);

		bindSearchButton();
		bindClearButton();		
		
	}

	private void bindClearButton() {
		final SelectionAdapter clearListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				doClear(event);
			}
			
		};	
		clearButton.addSelectionListener(clearListener);
		clearButton.addDisposeListener((DisposeListener) arg0 -> clearButton.removeSelectionListener(clearListener));
	}
	
	/**
	 * Perform clearing.
	 * @param event the event
	 */
	protected final void doClear(final SelectionEvent event) {
		objectGuidText.setText(StringUtils.EMPTY);
		objectGuidBinding.getBinding().updateTargetToModel();
		lowestPriceText.setText(StringUtils.EMPTY);
		lowestPriceBinding.getBinding().updateTargetToModel();
		highestPriceText.setText(StringUtils.EMPTY);			
		highestPriceBinding.getBinding().updateTargetToModel();
		quantityText.setText(StringUtils.EMPTY);
		quantityBinding.getBinding().updateTargetToModel();
		
		EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
		EpControlBindingProvider.removeEpValueBinding(bindingContext, objectTypeBinding);
		objectTypeCombo.select(0);
		getFilterExt().setObjectType(null);
		final ObservableUpdateValueStrategy objectTypeUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				Integer selectedItem = (Integer) newValue;
				switch(selectedItem) {
					case 1:
						filterExt.setObjectType(BaseAmountObjectType.PRODUCT.toString());
						break;
					case 2:
						filterExt.setObjectType(BaseAmountObjectType.SKU.toString());
						break;
					default:
						filterExt.setObjectType(null);
						break;
				}
				return Status.OK_STATUS;
			}
		};
		objectTypeBinding = bindingProvider.bind(bindingContext, objectTypeCombo, null, null, objectTypeUpdateStrategy, false);
		
		onClear(event, getController().getBaseAmountsUiFilter());
	}

	private void bindSearchButton() {
		searchListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				doSearch(event);
			}
			
		};	
		searchButton.addSelectionListener(searchListener);
		searchButton.addDisposeListener((DisposeListener) arg0 -> searchButton.removeSelectionListener(searchListener));
	}
	
	/**
	 * Perform searching.
	 * @param event the event
	 */
	protected final void doSearch(final SelectionEvent event) {
		onSearch(event, getController().getBaseAmountsUiFilter());
	}
	
	/**
	 * Called when search button is pressed.
	 * @param event the event
	 * @param baseAmountFilterExt current filter
	 */
	protected abstract void onSearch(final SelectionEvent event, final BaseAmountFilterExt baseAmountFilterExt);

	/**
	 * Called when clear button is pressed.
	 * @param event the event
	 * @param baseAmountFilterExt current filter
	 */
	protected abstract void onClear(final SelectionEvent event, final BaseAmountFilterExt baseAmountFilterExt);

	private IEpLayoutData createLayoutData(final IEpLayoutComposite client) {
		final IEpLayoutData layoutData = client.createLayoutData(
				IEpLayoutData.BEGINNING, 
				IEpLayoutData.CENTER, 
				false, 
				false);
		return layoutData;
	}


	@Override
	public Object getModel() {
		return getFilterExt();
	}

	@Override
	public void populateControls() {
		if (filterExt.getObjectGuid() != null) {
			objectGuidText.setText(filterExt.getObjectGuid());			
		}		
		if (BaseAmountObjectType.PRODUCT.toString().equals(filterExt.getObjectType())) {
			objectTypeCombo.select(1);	
		} else if (BaseAmountObjectType.SKU.toString().equals(filterExt.getObjectType())) {
			objectTypeCombo.select(2);
		} else {
			objectTypeCombo.select(0);
		}
		
		if (filterExt.getHighestPrice() != null && filterExt.getHighestPrice().floatValue() > 0) {
			highestPriceText.setText(filterExt.getHighestPrice().toPlainString());
		}
		if (filterExt.getLowestPrice() != null && filterExt.getLowestPrice().floatValue() > 0) {
			lowestPriceText.setText(filterExt.getLowestPrice().toPlainString());
		}
		if (filterExt.getQuantity() != null && filterExt.getQuantity().floatValue() > 0) {
			quantityText.setText(filterExt.getQuantity().toPlainString());
		}
	}
	
	/**
	 * Set the minimum width of a text control to a given number of characters.
	 * 
	 * @param text control
	 * @param width minimum characters
	 */
	private void setMinimumControlWidth(final Control control, final int width) {
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1);
		layoutData.widthHint = width;
		control.setLayoutData(layoutData);
	}	
	
	/** 
	 * @return table viewer.
	 */
	public IEpTableViewer getBaseAmountTableViewer() {
		return baseAmountTableViewer;
	}

	/** 
	 * @return extended filter.
	 */
	public BaseAmountFilterExt getFilterExt() {
		return filterExt;
	}

	/** 
	 * @return controller.
	 */
	public PriceListEditorController getController() {
		return controller;
	}
}
