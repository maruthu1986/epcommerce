/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.price.baseamounts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.catalog.editors.model.ProductModelController;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.TableSelectionProvider;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.cmclient.pricelistmanager.controller.PriceListEditorController;
import com.elasticpath.cmclient.pricelistmanager.controller.impl.BaseAmountDtoAssembler;
import com.elasticpath.cmclient.pricelistmanager.dialogs.BaseAmountDialog;
import com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection.BaseAmountSection;
import com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection.BaseAmountTableProperties;
import com.elasticpath.cmclient.pricelistmanager.model.impl.BaseAmountType;
import com.elasticpath.cmclient.core.dto.catalog.AbstractProductModel;
import com.elasticpath.cmclient.core.dto.catalog.PriceListEditorModel;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.persistence.api.Entity;

/**
 * Section for base amount editing used for product pricing page.
 */
public class ProductEditorBaseAmountSection extends BaseAmountSection {

	private final AbstractProductModel product;

	private final PriceListEditorController controller;

	private PolicyActionContainer addButtonContainer;

	/**
	 * Constructor.
	 *
	 * @param controlModificationListener - control modification listener.
	 * @param controller                  - price list controller.
	 * @param tableSelectionProvider      - base amounts table selection provider.
	 * @param product                     - abstract product model.
	 * @param baseAmountProperties        - base amount table properties
	 */
	public ProductEditorBaseAmountSection(final AbstractCmClientFormEditor controlModificationListener, final PriceListEditorController controller,
										  final TableSelectionProvider tableSelectionProvider, final AbstractProductModel product,
										  final BaseAmountTableProperties baseAmountProperties) {
		super(controlModificationListener, controller, tableSelectionProvider, baseAmountProperties);
		this.product = product;
		this.controller = controller;

	}

	@Override
	public void createControls(final IEpLayoutComposite client, final IEpLayoutData data) {
		getBaseAmountTableContentProvider().setDefaultComparator(new ProductEditorBaseAmountDTOComparator());
		getBaseAmountTableContentProvider().setProductCodeComparator(new ProductEditorBaseAmountDTOComparator());
		addMissingProductsOrSkus(); // before initial creation of the table
		super.createControls(client, data);
		addButtonContainer = addPolicyActionContainer("priceListPaymentScheduleAddButton"); //$NON-NLS-1$

		getBaseAmountTableViewer().getSwtTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			/**
			 * Listen for changes to the base amount table and update edit and delete buttons based on row selection state.
			 *
			 * @param event the selection event
			 */
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				addButtonContainer.setPolicyDependent(selection.getFirstElement());
				reApplyStatePolicy();
			}
		});

	}

	@Override
	public void populateControls() {
		this.refreshTableViewer();
	}

	@Override
	public void refreshTableViewer() {
		addMissingProductsOrSkus(); // MUST be called before refresh.
		PriceListEditorModel model = controller.getModel();
		populateLockedObjects(model.getBaseAmountWithRemoved());
		super.refreshTableViewer();
	}

	private void addMissingProductsOrSkus() {
		PriceListEditorModel model = controller.getModel();
		List<BaseAmountDTO> baseAmounts = model.getRawBaseAmounts();
		for (BaseAmountDTO baseAmount : new ArrayList<>(baseAmounts)) {
			final List<BaseAmountDTO> baseAmountDTOs = getBaseAmountsForTier(baseAmount.getQuantity());
			if (CollectionUtils.isNotEmpty(baseAmountDTOs)) {
				final Set<BaseAmountDTO> dtosToRemoveFromEnriched = new HashSet<>();

				CollectionUtils.forAllDo(baseAmounts, input -> {
					final BaseAmountDTO dto = (BaseAmountDTO) input;

					if (!dto.getObjectType().equalsIgnoreCase(ProductModelController.PRODUCT_SKU_TYPE)) {
						return;
					}

					BaseAmountDTO duplicateEnrichedDto = (BaseAmountDTO) CollectionUtils.find(baseAmountDTOs, object -> {
						BaseAmountDTO enrichedDto = (BaseAmountDTO) object;
						return areSameBaseAmountDtos(dto, enrichedDto);
					});

					if (duplicateEnrichedDto != null) {
						copyEnrichedDtoInformation(dtosToRemoveFromEnriched, dto, duplicateEnrichedDto);
					}
				});

				baseAmountDTOs.removeAll(dtosToRemoveFromEnriched);

				int position = baseAmounts.indexOf(baseAmount);
				baseAmounts.addAll(position, baseAmountDTOs);
			}
		}
	}

	private boolean areSameBaseAmountDtos(final BaseAmountDTO dto, final BaseAmountDTO enrichedDto) {
		return enrichedDto.getObjectGuid().equalsIgnoreCase(dto.getObjectGuid())
				&& enrichedDto.getObjectType().equalsIgnoreCase(dto.getObjectType())
				&& enrichedDto.getQuantity().equals(dto.getQuantity());
	}

	private void copyEnrichedDtoInformation(final Set<BaseAmountDTO> dtosToRemoveFromEnriched, final BaseAmountDTO dto,
											final BaseAmountDTO duplicateEnrichedDto) {
		dto.setSkuConfiguration(duplicateEnrichedDto.getSkuConfiguration());
		dto.setProductCode(duplicateEnrichedDto.getProductCode());
		dtosToRemoveFromEnriched.add(duplicateEnrichedDto);
	}

	/**
	 * Method adds empty base amounts for the tier. Scenario 1: User added a price into the price list against the SKU code not the product code in
	 * this case the first row will not have a price. SKU will show up as second row with a price. Scenario 2: User added a price into the price list
	 * against the product code but not against the SKU code, first row will have a price, the second row will not have a price. Scenario 3: User
	 * added a price into a price list against the SKU and against the product code. Each row will show the corresponding price. (SKU price trumps
	 * the product price in SF.)
	 */
	private List<BaseAmountDTO> getBaseAmountsForTier(final BigDecimal quantity) {
		List<BaseAmountDTO> dtos = new LinkedList<>();
		if (product.getModelType().equals(ProductModelController.PRODUCT_TYPE)) {
			Product internalProduct = ((ProductModel) product).getProduct();
			Map<String, ProductSku> skus = internalProduct.getProductSkus();
			for (ProductSku sku : skus.values()) {
				if (!hasSkuBaseAmount(sku, quantity)) {
					BaseAmountDTO baseAmountDto = BaseAmountDtoAssembler.assembleFromSku(sku, quantity, getModel(), controller.getCurrentLocale());
					dtos.add(baseAmountDto);
					addEmptyObject(baseAmountDto);
				}
			}
			if (!hasSkuBaseAmount(internalProduct, quantity)) {
				BaseAmountDTO baseAmountDto = BaseAmountDtoAssembler.assembleFromProduct(internalProduct, quantity, getModel());
				dtos.add(baseAmountDto);
				addEmptyObject(baseAmountDto);
			}
			return dtos;
		}
		return null;
	}

	private boolean hasSkuBaseAmount(final Entity entity, final BigDecimal quantity) {
		PriceListEditorModel model = controller.getModel();
		Collection<BaseAmountDTO> baseAmounts = model.getBaseAmountWithRemoved();
		for (BaseAmountDTO baseAmountDTO : baseAmounts) {
			if (baseAmountDTO.getProductCode() == null) {
				continue;
			}
			if (isMatchFound(baseAmountDTO, entity, quantity)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Matches base amount by guid, type and quantity to find existing match for a price tier.
	 */
	private boolean isMatchFound(final BaseAmountDTO baseAmountDTO, final Entity entity, final BigDecimal quantity) {

		if (baseAmountDTO.getQuantity().compareTo(quantity) == 0) {

			if (baseAmountDTO.getObjectType().equals(BaseAmountType.PRODUCT.getType())) {
				return (entity instanceof Product) && baseAmountDTO.getProductCode().equals(entity.getGuid())
						&& baseAmountDTO.getObjectType().equals(BaseAmountType.PRODUCT.getType());
			}

			return (entity instanceof ProductSku) && baseAmountDTO.getSkuCode().equals(((ProductSku) entity).getSkuCode())
					&& baseAmountDTO.getObjectType().equals(BaseAmountType.SKU.getType());

		}

		return false;
	}

	@Override
	protected BaseAmountDialog createDialog(final boolean editMode, final BaseAmountDTO dto) {
		String windowTitle;
		String dialogTitle;
		if (editMode) {
			dialogTitle = CatalogMessages.get().EditPriceTierDialog_Title;
			windowTitle = CatalogMessages.get().EditPriceTierDialog_WindowTitle;
		} else {
			dialogTitle = CatalogMessages.get().AddPriceTierDialog_Title;
			windowTitle = CatalogMessages.get().AddPriceTierDialog_WindowTitle;
		}
		boolean hideListValue = getEmptyObjects().contains(dto);
		enrichOnDialogOpen(dto);
		return new BaseAmountDialog(editMode, dto, controller, false, windowTitle, dialogTitle, hideListValue);
	}

	private void enrichOnDialogOpen(final BaseAmountDTO baseAmountDto) {
		if (baseAmountDto == null) {
			return;
		}
		PriceListDescriptorDTO currentPriceList = getModel();
		baseAmountDto.setPriceListDescriptorGuid(currentPriceList.getGuid());
		// Required for support of sku pricing page
		// need to check for sku code in case of product editor - if it has a sku code it's a sku and should have appropriate type being set
		if (StringUtils.isEmpty(baseAmountDto.getObjectGuid())) { // only for the new objects
			if (product.getModelType().equals(ProductModelController.PRODUCT_TYPE)) {
				baseAmountDto.setObjectType(BaseAmountType.PRODUCT.getType());
				Product internalProduct = ((ProductModel) product).getProduct();
				baseAmountDto.setSkuCode(internalProduct.getDefaultSku().getSkuCode());
				baseAmountDto.setProductCode(internalProduct.getCode());
				baseAmountDto.setObjectGuid(internalProduct.getCode());
			} else {
				baseAmountDto.setObjectType(BaseAmountType.SKU.getType());
				ProductSku sku = product.getProductSku();
				baseAmountDto.setSkuCode(sku.getSkuCode());
				baseAmountDto.setObjectGuid(sku.getSkuCode());
			}
		}
	}

	@Override
	protected void createTableColumns() {
		createStandardBaseAmountTableColumns();
		getBaseAmountTableViewer().addTableColumn(PriceListManagerMessages.get().BaseAmount_PaymentSchedule,
				getTableProperties().getPaymentScheduleWidth());
		createExtensionColumns();
	}
	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}
}