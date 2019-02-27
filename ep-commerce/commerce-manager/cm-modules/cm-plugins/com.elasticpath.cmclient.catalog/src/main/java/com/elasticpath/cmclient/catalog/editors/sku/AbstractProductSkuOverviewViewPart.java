/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.sku;


import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.binding.EpBindingConfiguration.UpdatePolicy;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.common.DefaultStatePolicyDelegateImpl;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * Base product/sku overview view part.
 */
public abstract class AbstractProductSkuOverviewViewPart extends DefaultStatePolicyDelegateImpl implements IEpSkuOverviewViewPart {

	private static final int COLUMN_NUMBER = 2;

	/**
	 * Validation delay.
	 */
	protected static final long VALIDATION_DELAY_MILLIS = 300;

	private IPolicyTargetLayoutComposite overviewSectionComposite;

	private IPolicyTargetLayoutComposite overviewSectionOutComposite;

	private IProductSkuEventListener productSkuEventListener;

	private final ProductSku productSku;

	private ControlModificationListener controlModificationListener;

	private EpValueBinding skuCodeBinding;

	private final ProductSkuCodeValidator skuValidator;

	/**
	 * Constructs the view part.
	 *
	 * @param productSku           the product sku
	 * @param eventListener        the event listener for specific product SKU overview view part events
	 * @param checkCodeOnKeyStroke should the SKU code check be enabled on each keystroke
	 */
	public AbstractProductSkuOverviewViewPart(final ProductSku productSku,
											  final IProductSkuEventListener eventListener,
											  final boolean checkCodeOnKeyStroke) {
		this.productSku = productSku;
		this.productSkuEventListener = eventListener;
		skuValidator = new ProductSkuCodeValidator(checkCodeOnKeyStroke, productSku);
	}


	/**
	 * Constructs the view part.
	 *
	 * @param productSku    the product sku
	 * @param eventListener the event listener for specific product SKU overview view part events
	 */
	public AbstractProductSkuOverviewViewPart(final ProductSku productSku, final IProductSkuEventListener eventListener) {
		this(productSku, eventListener, false);
	}

	@Override
	public void createControls(final IEpLayoutComposite mainPane, final IEpLayoutData data) {
		mainPane.getSwtComposite().setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL));
		overviewSectionOutComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(
				mainPane.addGridLayoutComposite(COLUMN_NUMBER, false, data));
		overviewSectionComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(
				overviewSectionOutComposite.getLayoutComposite().addGridLayoutComposite(COLUMN_NUMBER, false, data));

	}

	/**
	 * @return product sku event listener.
	 */
	public IProductSkuEventListener getProductSkuEventListener() {
		return productSkuEventListener;
	}

	@Override
	public void setProductSkuEventListener(final IProductSkuEventListener productSkuEventListener) {
		this.productSkuEventListener = productSkuEventListener;
	}

	/**
	 * @return overview section composite.
	 */
	public IPolicyTargetLayoutComposite getOverviewSectionComposite() {
		return overviewSectionComposite;
	}


	/**
	 * @return overview section out composite.
	 */
	public IPolicyTargetLayoutComposite getOverviewSectionOutComposite() {
		return overviewSectionOutComposite;
	}

	/**
	 * @param overviewSectionComposite overview section composite to use.
	 */
	public void setOverviewSectionComposite(final IPolicyTargetLayoutComposite overviewSectionComposite) {
		this.overviewSectionComposite = overviewSectionComposite;
	}


	/**
	 * @return product sku.
	 */
	public ProductSku getProductSku() {
		return productSku;
	}

	@Override
	public void refreshLayout() {
		if (!getOverviewSectionComposite().getSwtComposite().isDisposed()) {
			getOverviewSectionComposite().getSwtComposite().getParent().layout();
		}
	}

	/**
	 * @return control modification listener
	 */
	public ControlModificationListener getControlModificationListener() {
		return controlModificationListener;
	}


	@Override
	public void setControlModificationListener(final ControlModificationListener listener) {
		this.controlModificationListener = listener;
		//FIXME
		getOverviewSectionComposite().setControlModificationListener(listener);
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (controlModificationListener != null) {
			controlModificationListener.controlModified();
		}
	}

	/**
	 * @return sku validator
	 */
	protected IValidator getSkuValidator() {
		return skuValidator;
	}

	/**
	 * @return sku code binding
	 */
	protected EpValueBinding getSkuCodeBinding() {
		return skuCodeBinding;
	}

	/**
	 * Sets sku code binding.
	 *
	 * @param skuCodeBinding sku code binding
	 */
	protected void setSkuCodeBinding(final EpValueBinding skuCodeBinding) {
		this.skuCodeBinding = skuCodeBinding;
	}

	@Override
	public void setValidateSku(final boolean validateSku) {
		skuValidator.setValidateSku(validateSku);
	}

	/**
	 * This class requires that the validation of the sku code is an explicit step - this enables
	 * us to avoid too many remote validation calls, which is slow.
	 */
	protected final class SkuValidationUpdateValueStrategy extends
			ObservableUpdateValueStrategy {

		private final ProductSku sku;

		/**
		 * Constructor.
		 *
		 * @param sku sku
		 */
		public SkuValidationUpdateValueStrategy(final ProductSku sku) {
			this.sku = sku;
		}

		@Override
		protected IStatus doSet(final IObservableValue observableValue, final Object value) {
			sku.setSkuCode(value.toString());
			return Status.OK_STATUS;
		}

		@Override
		public int getUpdatePolicy() {
			return UpdatePolicy.ON_REQUEST.getPolicyNumber();
		}
	}
}
