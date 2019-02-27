/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.sku;

import java.math.BigDecimal;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPermissions;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpViewPart;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * Product SKU view part for wizards.
 */
public class ProductSkuShippingViewPart implements IEpViewPart {

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private Text shippingWeightText;

	private Text shippingWidthText;

	private Text shippingLengthText;

	private Text shippingHeightText;

	private final ProductSku productSku;

	/**
	 * Constructs the view part.
	 *
	 * @param productSku the product sku
	 */
	public ProductSkuShippingViewPart(final ProductSku productSku) {
		this.productSku = productSku;
	}

	@Override
	public void createControls(final IEpLayoutComposite shippingComposite, final IEpLayoutData data) {
		EpState epState;
		if (AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.MANAGE_PRODUCT_SKU)) {
			epState = EpState.EDITABLE;
		} else {
			epState = EpState.READ_ONLY;
		}

		final IEpLayoutData labelData = shippingComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = shippingComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false);

		shippingComposite.addLabelBold(CatalogMessages.get().ProductEditorSingleSkuShipping_ShippingWeight, labelData);
		shippingWeightText = shippingComposite.addTextField(epState, fieldData);
		shippingComposite.addLabel(CatalogMessages.get().ProductEditorSingleSkuShipping_kg, fieldData);

		shippingComposite.addLabelBold(CatalogMessages.get().ProductEditorSingleSkuShipping_ShippingWidth, labelData);
		shippingWidthText = shippingComposite.addTextField(epState, fieldData);
		shippingComposite.addLabel(CatalogMessages.get().ProductEditorSingleSkuShipping_cm, fieldData);

		shippingComposite.addLabelBold(CatalogMessages.get().ProductEditorSingleSkuShipping_ShippingLength, labelData);
		shippingLengthText = shippingComposite.addTextField(epState, fieldData);
		shippingComposite.addLabel(CatalogMessages.get().ProductEditorSingleSkuShipping_cm, fieldData);

		shippingComposite.addLabelBold(CatalogMessages.get().ProductEditorSingleSkuShipping_ShippingHeight, labelData);
		shippingHeightText = shippingComposite.addTextField(epState, fieldData);
		shippingComposite.addLabel(CatalogMessages.get().ProductEditorSingleSkuShipping_cm, fieldData);
	}

	@Override
	public void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider controlBinding = EpControlBindingProvider.getInstance();
		controlBinding.bind(bindingContext,
				shippingWeightText,
				EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL,
				null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object value) {
						if (EMPTY_STRING.equals(value)) {
							productSku.setWeight(null);
						} else {
							final BigDecimal weight = new BigDecimal((String) value);
							productSku.setWeight(weight);
						}
						return Status.OK_STATUS;
					}

				},
				true);
		controlBinding.bind(bindingContext,
				shippingHeightText,
				EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL,
				null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object value) {
						if (EMPTY_STRING.equals(value)) {
							productSku.setHeight(null);
						} else {
							final BigDecimal height = new BigDecimal((String) value);
							productSku.setHeight(height);
						}
						return Status.OK_STATUS;
					}

				},
				true);
		controlBinding.bind(bindingContext,
				shippingLengthText,
				EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL,
				null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object value) {
						if (EMPTY_STRING.equals(value)) {
							productSku.setLength(null);
						} else {
							final BigDecimal length = new BigDecimal((String) value);
							productSku.setLength(length);
						}
						return Status.OK_STATUS;
					}

				},
				true);
		controlBinding.bind(bindingContext,
				shippingWidthText,
				EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL,
				null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object value) {
						if (EMPTY_STRING.equals(value)) {
							productSku.setWidth(null);
						} else {
							final BigDecimal width = new BigDecimal((String) value);
							productSku.setWidth(width);
						}
						return Status.OK_STATUS;
					}

				},
				true);
	}

	@Override
	public void populateControls() {
		// do nothing - control population performed on bind
	}

	@Override
	public ProductSku getModel() {
		return productSku;
	}
}
