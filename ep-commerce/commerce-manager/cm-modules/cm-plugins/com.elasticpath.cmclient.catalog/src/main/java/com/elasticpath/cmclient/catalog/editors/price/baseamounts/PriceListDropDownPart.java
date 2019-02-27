/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.price.baseamounts;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.editors.model.ProductModelController;
import com.elasticpath.cmclient.catalog.wizards.product.create.ComboModel;
import com.elasticpath.cmclient.catalog.wizards.product.create.PlaSortingPolicy;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpViewPart;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyTargetImpl;
import com.elasticpath.cmclient.pricelistmanager.controller.PriceListEditorController;
import com.elasticpath.cmclient.core.dto.catalog.AbstractProductModel;
import com.elasticpath.cmclient.core.dto.catalog.PriceListEditorModel;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.common.dto.pricing.PriceListAssignmentsDTO;
import com.elasticpath.common.pricing.service.PriceListAssignmentHelperService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.cmuser.CmUser;


/**
 * Class represents part of Product pricing page with a Price List descriptors drop down widget.
 */
public class PriceListDropDownPart extends AbstractStatePolicyTargetImpl implements IEpViewPart {

	private final PriceListEditorController controller;
	private final AbstractProductModel product;
	private final SelectionListener selectionAction;
	private final ComboModel comboModel = new ComboModel();
	private final PriceListAssignmentHelperService plaHelperService;
	private final Locale locale;

	/**
	 * Constructor.
	 *
	 * @param controller      - price list editor controller.
	 * @param product         - product model.
	 * @param selectionAction - drop down selection action.
	 * @param locale          the selected locale of the page
	 */
	public PriceListDropDownPart(final PriceListEditorController controller,
								 final AbstractProductModel product,
								 final SelectionListener selectionAction,
								 final Locale locale) {
		this.controller = controller;
		this.product = product;
		this.selectionAction = selectionAction;
		this.plaHelperService = ServiceLocator.getService(
				ContextIdNames.PRICE_LIST_ASSIGNMENT_HELPER_SERVICE);
		this.locale = locale;
	}

	@Override
	public void bindControls(final DataBindingContext bindingContext) {
		//
	}

	@Override
	public void createControls(final IEpLayoutComposite mainComposite, final IEpLayoutData data) {

		String displayName;
		if (product.getModelType().equals(ProductModelController.PRODUCT_TYPE)) {
			Product dispProduct = ((ProductModel) product).getProduct();
			displayName = dispProduct.getDisplayName(locale);
		} else {
			displayName = product.getProductSku().getProduct().getDisplayName(locale);
		}
		if (displayName == null) {
			displayName = StringUtils.EMPTY;
		}

		IEpLayoutData productNameLabelLayoutData =
				mainComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER, false, false);

		IEpLayoutComposite nameComposite = mainComposite.addGridLayoutComposite(2, false, productNameLabelLayoutData);

		nameComposite.addLabelBold(CatalogMessages.get().ProductPricePage_ProductName, productNameLabelLayoutData);
		nameComposite.addLabel(displayName, productNameLabelLayoutData);

		//section components formatting
		mainComposite.addEmptyComponent(null);

		IEpLayoutData priceListLayoutData = mainComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER, true, false);
		IEpLayoutComposite priceListComposite = mainComposite.addTableWrapLayoutComposite(2, false, priceListLayoutData);

		priceListComposite.addLabel(CatalogMessages.get().ProductPricePage_PriceListCaption, priceListLayoutData);
		createComboBox(priceListLayoutData, priceListComposite);

		//section components formatting
		mainComposite.addEmptyComponent(null);
	}


	private void createComboBox(final IEpLayoutData layoutData, final IEpLayoutComposite composite) {
		final CCombo priceListsCombo = composite.addComboBox(EpState.EDITABLE, layoutData);

		final List<PriceListAssignmentsDTO> plas = getPriceListAssignmentsByProductCatalogs();

		List<PriceListAssignmentsDTO> uniquePlas = PlaSortingPolicy.sortAlpha(PlaSortingPolicy.findLowestPriorityPlas(plas));
		this.comboModel.setPlas(uniquePlas);
		comboModel.populate(priceListsCombo);

		String currentGuid = controller.getModel().getPriceListDescriptor().getGuid();
		if (currentGuid == null) {
			int initialComboSelectionIndex = PlaSortingPolicy.findLowestPriorityPlaIndex(uniquePlas);
			if (initialComboSelectionIndex != -1) {
				priceListsCombo.select(initialComboSelectionIndex);
				String guid = comboModel.get(initialComboSelectionIndex).getPriceListGuid();
				controller.setPriceListDescriptorGuid(guid);
			}
		} else {
			priceListsCombo.select(comboModel.get(currentGuid));
		}
		controller.reloadModel();

		priceListsCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				int selection = priceListsCombo.getSelectionIndex();
				PriceListAssignmentsDTO descriptor = comboModel.get(selection);
				PriceListEditorModel model = controller.getModel();
				String currentGuid = model.getPriceListDescriptor().getGuid();
				if (currentGuid.equals(descriptor.getPriceListGuid())) {
					return;
				}
				selectionEvent.text = descriptor.getPriceListGuid();
				selectionEvent.widget = priceListsCombo;
				selectionEvent.detail = comboModel.get(currentGuid);
				selectionAction.widgetSelected(selectionEvent);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				//
			}
		});

	}

	/**
	 * Get all price list assignments by product catalogs with price list security check.
	 *
	 * @return list of price list assignments.
	 */
	private List<PriceListAssignmentsDTO> getPriceListAssignmentsByProductCatalogs() {
		CmUser currentUser = LoginManager.getCmUser();
		return plaHelperService.getPriceListAssignmentsDTO(product.getCatalogs(), currentUser);
	}

	@Override
	public Object getModel() {
		return null;
	}

	@Override
	public void populateControls() {
		//
	}

	@Override
	public String getTargetIdentifier() {
		return null;
	}


}

