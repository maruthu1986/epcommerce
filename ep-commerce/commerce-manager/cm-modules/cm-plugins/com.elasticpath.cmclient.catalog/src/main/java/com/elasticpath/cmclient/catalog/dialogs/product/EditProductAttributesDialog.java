/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.dialogs.product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.catalog.editors.attribute.AttributesViewPart;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.comparator.AttributeValueComparatorByNameIgnoreCase;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.StatePolicyTarget;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareDialog;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.AttributeValueIsRequiredException;
import com.elasticpath.domain.catalog.Product;

/**
 * Editing attributes of a single item in order dialog.
 */
public class EditProductAttributesDialog  extends AbstractPolicyAwareDialog implements StatePolicyTarget {

	private static final String EDIT_PRODUCT_ATTRIBUTES_DIALOG_TARGET_ID = "editProductAttributesDialog";
	private static final String EDIT_PRODUCT_ATTRIBUTES_DIALOG_POLICY_CONTAINER = "editProductAttributesDialog";
	private static final String NEW_LINE = "\n";

	private final Product product;

	private AttributesViewPart attributesViewPart;

	private List<Locale> supportedLocales;

	private Locale selectedLocale;

	private CCombo languageSelector;

	private DataBindingContext bindingContext;

	/**
	 * Policy container for the dialog controls.
	 */
	private PolicyActionContainer editProductAttributesDialogContainer;

	/**
	 * Constructor.
	 *
	 * @param parentShell the Shell
	 * @param product the product
	 */
	public EditProductAttributesDialog(final Shell parentShell, final Product product) {
		super(parentShell, 2, false);
		this.product = product;
	}

	@Override
	protected void bindControls() {

		languageSelector.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				// do nothing.
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				selectedLocale = supportedLocales.get(languageSelector.getSelectionIndex());
				attributesViewPart.setInput(getAttributes());
			}

		});
	}

	@Override
	protected void createDialogContent(final IPolicyTargetLayoutComposite dialogComposite) {

		editProductAttributesDialogContainer = addPolicyActionContainer(EDIT_PRODUCT_ATTRIBUTES_DIALOG_POLICY_CONTAINER);


		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		dialogComposite.addEmptyComponent(fieldData, editProductAttributesDialogContainer);
		languageSelector = dialogComposite.addComboBox(fieldData, editProductAttributesDialogContainer);

		attributesViewPart = new AttributesViewPart(this.product, EpState.EDITABLE, null);
		attributesViewPart.createControls(dialogComposite.getLayoutComposite());


	}

	@Override
	protected void populateControls() {

		populateLanguageSelector();

		attributesViewPart.setInput(getAttributes());

	}

	private void populateLanguageSelector() {

		supportedLocales = new ArrayList<>(product.getMasterCatalog().getSupportedLocales());
		if (selectedLocale == null) {
			selectedLocale = product.getMasterCatalog().getDefaultLocale();
		}

		int selection = 0;
		Locale selectionLocale = supportedLocales.get(selection);
		final String[] localesForCCombo = new String[supportedLocales.size()];
		for (int index = 0; index < supportedLocales.size(); index++) {
			final Locale locale = supportedLocales.get(index);
			localesForCCombo[index] = locale.getDisplayName();

			if (locale.equals(selectedLocale)) {
				selection = index;
				selectionLocale = locale;
			}
		}

		languageSelector.setItems(localesForCCombo);
		languageSelector.select(selection);
		languageSelector.setText(languageSelector.getItem(selection));
		selectedLocale = selectionLocale;

	}

	/**
	 * Retrieves the product attribute values.
	 */
	private AttributeValue[] getAttributes() {

		final List<AttributeValue> list = product.getFullAttributeValues(selectedLocale);
		Collections.sort(list, new AttributeValueComparatorByNameIgnoreCase());
		return list.toArray(new AttributeValue[list.size()]);
	}


	@Override
	public boolean isComplete() {
		return super.isComplete() && validateRequired();
	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return product;
	}

	private boolean validateRequired() {
		final Set<Locale> locales = new HashSet<>(Arrays.asList(selectedLocale));
		// Ensure that all Required Attributes are specified
		try {
			product.validateRequiredAttributes(locales);
		} catch (AttributeValueIsRequiredException e) {
			setErrorMessage(

					NLS.bind(CatalogMessages.get().ProductSaveMissingValueForRequiredAttributeMessage,
					e.getAttributesAsString(NEW_LINE)));
			return false;
		}

		setErrorMessage(null);
		return true;
	}


	@Override
	protected String getInitialMessage() {
		return null;
	}

	@Override
	protected String getTitle() {
		return CatalogMessages.get().EditProductAttributes_DialogTitle;
	}

	@Override
	protected Image getWindowImage() {
		return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT);
	}

	@Override
	protected String getWindowTitle() {
		return CatalogMessages.get().EditProductAttributes_DialogTitle;
	}

	@Override
	public String getTargetIdentifier() {
		return EDIT_PRODUCT_ATTRIBUTES_DIALOG_TARGET_ID;
	}
	
	@Override
	protected PolicyActionContainer getOkButtonPolicyActionContainer() {
		return editProductAttributesDialogContainer;
	}
	
	@Override
	protected Object getDependentObject() {
		return product;
	}

	@Override
	protected void refreshLayout() {
		// Do nothing.
	}

	@Override
	public DataBindingContext getDataBindingContext() {
		return bindingContext;
	}

}
