/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.sku;

import java.math.BigDecimal;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.cmclient.core.dto.catalog.AbstractProductModel;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * This class implements the section of the Product editor that displays product shipping information.
 */
public class ProductSkuDetailsPageShippingSection extends AbstractPolicyAwareEditorPageSectionPart {

	private static final int TEXT_FIELD_WIDTH_HINT = 60;

	private static final int COLUMNS = 3;

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private Text shippingWeightText;

	private Text shippingWidthText;

	private Text shippingLengthText;

	private Text shippingHeightText;

	private final ProductSku productSku;

	private final AbstractCmClientFormEditor controlModificationListener;

	private IPolicyTargetLayoutComposite shippingComposite;

	private ProductSkuInventoryViewPart inventorySection;


	/**
	 * Constructor.
	 *
	 * @param formPage parent form page
	 * @param editor the editor where the detail section will be placed
	 */
	public ProductSkuDetailsPageShippingSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		this.productSku = ((AbstractProductModel) editor.getModel()).getProductSku();
		this.controlModificationListener = editor;
	}

	@Override
	protected String getSectionTitle() {
		return CatalogMessages.get().ProductEditorSingleSkuShipping_Title;
	}

	@Override
	protected void createControls(final Composite parent, final FormToolkit toolkit) {
		shippingComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(CompositeFactory.createGridLayoutComposite(parent, COLUMNS, false));
		shippingComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		PolicyActionContainer skuControls = addPolicyActionContainer("skuControls"); //$NON-NLS-1$
		PolicyActionContainer partContainer = addPolicyActionContainer("part"); //$NON-NLS-1$

		final IEpLayoutData labelData2 = shippingComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL);
		final IEpLayoutData labelData = shippingComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = shippingComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);

		shippingComposite.addLabelBold(CatalogMessages.get().ProductEditorSingleSkuShipping_ShippingWeight, labelData, skuControls);
		shippingWeightText = shippingComposite.addTextField(fieldData, skuControls);
		shippingComposite.addLabel(CatalogMessages.get().ProductEditorSingleSkuShipping_kg, labelData2, skuControls);
		applyMinWidth(shippingWeightText);
		
		shippingComposite.addLabelBold(CatalogMessages.get().ProductEditorSingleSkuShipping_ShippingWidth, labelData, skuControls);
		shippingWidthText = shippingComposite.addTextField(fieldData, skuControls);
		shippingComposite.addLabel(CatalogMessages.get().ProductEditorSingleSkuShipping_cm, labelData2, skuControls);
		applyMinWidth(shippingWidthText);

		shippingComposite.addLabelBold(CatalogMessages.get().ProductEditorSingleSkuShipping_ShippingLength, labelData, skuControls);
		shippingLengthText = shippingComposite.addTextField(fieldData, skuControls);
		shippingComposite.addLabel(CatalogMessages.get().ProductEditorSingleSkuShipping_cm, labelData2, skuControls);
		applyMinWidth(shippingLengthText);

		shippingComposite.addLabelBold(CatalogMessages.get().ProductEditorSingleSkuShipping_ShippingHeight, labelData, skuControls);
		shippingHeightText = shippingComposite.addTextField(fieldData, skuControls);
		shippingComposite.addLabel(CatalogMessages.get().ProductEditorSingleSkuShipping_cm, labelData2, skuControls);
		applyMinWidth(shippingHeightText);

		inventorySection = new ProductSkuInventoryViewPart(productSku, getEditor());
		partContainer.addDelegate(inventorySection);
		inventorySection.createControls(shippingComposite,
				shippingComposite.createLayoutData(IEpLayoutData.FILL,
						IEpLayoutData.FILL, true, false, COLUMNS, 1));
		addCompositesToRefresh(shippingComposite.getSwtComposite().getParent());
	}

	private void applyMinWidth(final Text text) {
		((GridData) text.getLayoutData()).widthHint = TEXT_FIELD_WIDTH_HINT;
	}

	@Override
	protected void populateControls() {
		this.shippingWeightText.setText(getValueOf(productSku.getWeight()));
		this.shippingWidthText.setText(getValueOf(productSku.getWidth()));
		this.shippingLengthText.setText(getValueOf(productSku.getLength()));
		this.shippingHeightText.setText(getValueOf(productSku.getHeight()));

		inventorySection.populateControls();


	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider controlBinding = EpControlBindingProvider.getInstance();
		controlBinding.bind(bindingContext, shippingWeightText, EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, 
				null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				if ("".equals(value)) { //$NON-NLS-1$
					productSku.setWeight(null);
				} else {
					final BigDecimal weight = new BigDecimal((String) value);
					productSku.setWeight(weight);
				}
				return Status.OK_STATUS;
			}

		}, true);
		controlBinding.bind(bindingContext, shippingHeightText, EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, 
				null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				if ("".equals(value)) { //$NON-NLS-1$
					productSku.setHeight(null);
				} else {
					final BigDecimal height = new BigDecimal((String) value);
					productSku.setHeight(height);
				}
				return Status.OK_STATUS;
			}

		}, true);
		controlBinding.bind(bindingContext, shippingLengthText, EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, 
				null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				if ("".equals(value)) { //$NON-NLS-1$
					productSku.setLength(null);
				} else {
					final BigDecimal length = new BigDecimal((String) value);
					productSku.setLength(length);
				}
				return Status.OK_STATUS;
			}

		}, true);
		controlBinding.bind(bindingContext, shippingWidthText, EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, 
				null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				if ("".equals(value)) { //$NON-NLS-1$
					productSku.setWidth(null);
				} else {
					final BigDecimal width = new BigDecimal((String) value);
					productSku.setWidth(width);
				}
				return Status.OK_STATUS;
			}

		}, true);

		shippingComposite.setControlModificationListener(controlModificationListener);
	}

	/**
	 * Gets the value of a BigDecimal object.
	 *
	 * @param decimalValue {@link BigDecimal}
	 * @return String
	 */
	private String getValueOf(final BigDecimal decimalValue) {
		String value = EMPTY_STRING;
		if (decimalValue != null) {
			value = decimalValue.toString();
		}
		return value;
	}
}
