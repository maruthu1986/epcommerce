/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.sku;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpViewPart;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.DefaultStatePolicyDelegateImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * Product SKU textValidationEnabled asset view part.
 */
public class ProductSkuDigitalAssetViewPart extends DefaultStatePolicyDelegateImpl implements IEpViewPart {

	private static final int GRID_LAYOUT_COLUMNS = 3;

	private static final int MAX_VALUE = 1000;

	private Text filenameText;

	private Spinner downloadLimitSpinner;

	private Spinner downloadExpSpinner;

	private IPolicyTargetLayoutComposite digitalAssetComposite;

	private final ProductSku productSku;

	private final DigitalAsset digitalAsset;

	private boolean textValidationEnabled;

	private EpValueBinding filenameTextBinding;

	/**
	 * Constructs the view part.
	 *
	 * @param productSku product SKU object
	 * @param digitalAsset digital asset to be edited within this view.
	 */
	public ProductSkuDigitalAssetViewPart(final ProductSku productSku, final DigitalAsset digitalAsset) {
		this.productSku = productSku;
		this.digitalAsset = digitalAsset;
	}

	@Override
	public void createControls(final IEpLayoutComposite mainPane, final IEpLayoutData data) {
		digitalAssetComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(mainPane.addGridLayoutComposite(GRID_LAYOUT_COLUMNS, false, null));

		final IEpLayoutData labelData = digitalAssetComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = digitalAssetComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
		final IEpLayoutData fieldData2 = digitalAssetComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, false, false, 2, 1);

		PolicyActionContainer assetControls = addPolicyActionContainer("assetControls"); //$NON-NLS-1$

		digitalAssetComposite.addLabelBoldRequired(CatalogMessages.get().ProductEditorSingleSkuDigitalAsset_File, labelData, assetControls);
		filenameText = digitalAssetComposite.addTextField(fieldData2, assetControls);

		digitalAssetComposite.addLabelBold(CatalogMessages.get().ProductEditorSingleSkuDigitalAsset_DownloadLimit, labelData, assetControls);
		downloadLimitSpinner = digitalAssetComposite.addSpinnerField(fieldData, assetControls);
		downloadLimitSpinner.setMaximum(MAX_VALUE);
		digitalAssetComposite.addLabel(CatalogMessages.get().ProductEditorSingleSkuDigitalAsset_DownloadLimitExpl, fieldData, assetControls);

		digitalAssetComposite.addLabelBold(CatalogMessages.get().ProductEditorSingleSkuDigitalAsset_DownloadExpiry, labelData, assetControls);
		downloadExpSpinner = digitalAssetComposite.addSpinnerField(fieldData, assetControls);
		downloadExpSpinner.setMaximum(MAX_VALUE);
		digitalAssetComposite.addLabel(CatalogMessages.get().ProductEditorSingleSkuDigitalAsset_DownloadExpiryExpl, fieldData, assetControls);
	}

	@Override
	public void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider provider = EpControlBindingProvider.getInstance();
		setFilenameTextBinding(provider.bind(bindingContext,
				filenameText,
				digitalAsset,
				"fileName", //$NON-NLS-1$
				new FileTextValidator(),
				null,
				true));
		provider.bind(bindingContext, downloadLimitSpinner, digitalAsset, "maxDownloadTimes"); //$NON-NLS-1$
		provider.bind(bindingContext, downloadExpSpinner, digitalAsset, "expiryDays"); //$NON-NLS-1$
	}

	/**
	 * Sets the filename text binding.
	 *
	 * @param binding the new filename text binding
	 */
	protected void setFilenameTextBinding(final EpValueBinding binding) {
		filenameTextBinding = binding;
	}


	/**
	 * Gets the filename text binding.
	 *
	 * @return the filename text binding
	 */
	public EpValueBinding getFilenameTextBinding() {
		return filenameTextBinding;
	}

	@Override
	public void populateControls() {
		if (digitalAsset != null) {
			if (digitalAsset.getFileName() != null) {
				filenameText.setText(digitalAsset.getFileName());
			}
			downloadLimitSpinner.setSelection(digitalAsset.getMaxDownloadTimes());
			downloadExpSpinner.setSelection(digitalAsset.getExpiryDays());
		}
	}

	@Override
	public ProductSku getModel() {
		return productSku;
	}

	/**
	 * Validator for the file text.
	 */
	private class FileTextValidator implements IValidator {

		@Override
		public IStatus validate(final Object value) {
			if (textValidationEnabled) {
				return EpValidatorFactory.STRING_255_REQUIRED.validate(value);
			}
			return Status.OK_STATUS;
		}
	}

	/**
	 * Sets whether the validation for the text field is enabled.
	 *
	 * @param textValidationEnabled should the text validation be enabled
	 */
	public void setTextValidationEnabled(final boolean textValidationEnabled) {
		this.textValidationEnabled = textValidationEnabled;
	}

	/**
	 * Sets control modification listener.
	 *
	 * @param controlModificationListener the listener
	 */
	public void setControlModificationListener(final ControlModificationListener controlModificationListener) {
		digitalAssetComposite.setControlModificationListener(controlModificationListener);
	}

	@Override
	public void refreshLayout() {
		if (!digitalAssetComposite.getSwtComposite().isDisposed()) {
			digitalAssetComposite.getSwtComposite().layout();
		}
	}

	/**
	 * Get the file name text entered in this control.
	 *
	 * @return the file name
	 */
	public String getFilenameText() {
		return filenameText.getText();
	}

}
