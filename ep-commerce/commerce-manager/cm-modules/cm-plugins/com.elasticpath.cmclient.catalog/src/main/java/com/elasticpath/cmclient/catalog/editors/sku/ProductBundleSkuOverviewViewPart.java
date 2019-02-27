/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.sku;

import java.util.Locale;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.wizards.product.create.RepeatableDelayedTask;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * ProductBundle SKU overview view part for the UI controls.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity"})
public class ProductBundleSkuOverviewViewPart extends AbstractProductSkuOverviewViewPart implements IEpSkuOverviewViewPart {

	private Text parentProductText;

	private Text skuConfText;

	private Text skuCodeText;

	private final RepeatableDelayedTask skuCodeValidationDelayedScheduler = new RepeatableDelayedTask(
			new Runnable() {
				@Override
				public void run() {
					getSkuCodeBinding().getBinding().updateTargetToModel();
					getProductSkuEventListener().skuCodeChanged(skuCodeText.getText());
				}
			}, VALIDATION_DELAY_MILLIS);


	/**
	 * Constructs the view part.
	 *
	 * @param productSku           the product sku
	 * @param eventListener        the event listener for specific product SKU overview view part events
	 * @param checkCodeOnKeyStroke should the SKU code check be enabled on each keystroke
	 */
	public ProductBundleSkuOverviewViewPart(final ProductSku productSku,
											final IProductSkuEventListener eventListener, final boolean checkCodeOnKeyStroke) {
		super(productSku, eventListener, checkCodeOnKeyStroke);
	}

	/**
	 * Constructs the view part.
	 *
	 * @param productSku    the product sku
	 * @param eventListener the event listener for specific product SKU overview view part events
	 */
	public ProductBundleSkuOverviewViewPart(final ProductSku productSku,
											final IProductSkuEventListener eventListener) {
		super(productSku, eventListener);
	}

	@Override
	public void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider provider = EpControlBindingProvider.getInstance();
		final IValidator compoundSkuValidator = new CompoundValidator(new IValidator[]{EpValidatorFactory.SKU_CODE, getSkuValidator()});
		EpValueBinding binding = provider.bind(bindingContext, skuCodeText, compoundSkuValidator, null,
				new SkuValidationUpdateValueStrategy(getProductSku()), true);
		binding.getBinding().validateTargetToModel();
		setSkuCodeBinding(binding);
		skuCodeText.addModifyListener(this);
	}

	@Override
	public void createControls(final IEpLayoutComposite mainPane, final IEpLayoutData data) {

		super.createControls(mainPane, data);

		PolicyActionContainer codeControls = addPolicyActionContainer("guid"); //$NON-NLS-1$

		final IEpLayoutData labelData = getOverviewSectionComposite().createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = getOverviewSectionComposite().createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		getOverviewSectionComposite().addLabelBoldRequired(CatalogMessages.get().ProductEditorSingleSkuOverview_SkuCode, labelData, codeControls);
		skuCodeText = getOverviewSectionComposite().addTextField(fieldData, codeControls);
		final int widthHint = 200;
		((GridData) skuCodeText.getLayoutData()).widthHint = widthHint;

	}

	@Override
	public ProductSku getModel() {
		return super.getProductSku();
	}

	@Override
	public void populateControls() {
		skuCodeText.setText(getProductSku().getSkuCode());
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// not used
	}


	/**
	 * Updates the text fields.
	 *
	 * @param locale the locale to be used
	 */
	public void updateTextFields(final Locale locale) {
		if (getProductSku().getProduct().hasMultipleSkus()) {
			String productName = getProductSku().getProduct().getDisplayName(locale);
			if (productName != null) {
				parentProductText.setText(productName);
			}
			final String skuDisplayName = getProductSku().getDisplayName(locale);
			if (skuDisplayName != null) {
				skuConfText.setText(skuDisplayName);
			}
		}
	}


	@Override
	public void modifyText(final ModifyEvent event) {
		if (event.getSource() == skuCodeText) {
			getSkuCodeBinding().getBinding().getValidationStatus().setValue(Status.CANCEL_STATUS);
			getProductSkuEventListener().skuCodeChanged(skuCodeText.getText());

			skuCodeValidationDelayedScheduler.schedule();
			if (getControlModificationListener() != null) {
				getControlModificationListener().controlModified();
			}
		}
	}

}
