/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.controller.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.dto.catalog.PriceListEditorModel;
import com.elasticpath.cmclient.core.dto.catalog.impl.PriceListEditorModelImpl;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.service.BaseAmountEventService;
import com.elasticpath.cmclient.pricelistmanager.controller.PriceListEditorController;
import com.elasticpath.cmclient.pricelistmanager.model.impl.BaseAmountType;
import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.common.pricing.service.BaseAmountFilterExt;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.settings.SettingsService;

/**
 * Controller class for managing the PriceListEditor parts.
 */
@SuppressWarnings({"PMD.GodClass"})
public class PriceListEditorControllerImpl implements PriceListEditorController {

	private PriceListEditorModel model;

	private PriceListService priceListService;

	private ProductLookup productLookup;

	private ProductSkuLookup productSkuLookup;
	
	/** Controllers are tied to a model. We don't want to manage multiple opened models. **/
	private String plGuid = StringUtils.EMPTY;

	private Locale currentLocale;
	
	/**
	 * Common used filter.
	 */
	private BaseAmountFilterExt filter;
	
	/**
	 * Base amount second filter, that used for perform filtering on obtained from server side result.
	 */
	private BaseAmountFilterExt uiFilter;


	// True if this is a new price list that has not yet been saved (in saveModel())
	// Controls whether this price list should be added to the change set.
	// Should only be true when this is a new change set and not one that has already been loaded.
	private boolean isUnsavedNew;

	private final ChangeSetHelper changeSetHelper;

	/**
	 * Construct a controller for a certain PriceListDescriptor.
	 *
	 * @param guid of the PriceListDescriptor
	 */
	public PriceListEditorControllerImpl(final String guid) {
		plGuid = guid;
		this.changeSetHelper = getBean(ChangeSetHelper.BEAN_ID);
	}

	/**
	 * Gets the bean instance.
	 *
	 * @param beanId bean identifier string
	 * @param <T> the type
	 * @return the instance of the bean
	 */
	protected <T> T getBean(final String beanId) {
		return ServiceLocator.getService(beanId);
	}

	/**
	 * @return the price list service
	 */
	protected PriceListService getPriceListService() {
		if (this.priceListService == null) {
			this.priceListService = this.getBean(ContextIdNames.PRICE_LIST_CLIENT_SERVICE);
		}
		return this.priceListService;
	}

	/**
	 * @return the product reader
	 */
	protected ProductLookup getProductLookup() {
		if (this.productLookup == null) {
			this.productLookup = this.getBean(ContextIdNames.PRODUCT_LOOKUP);
		}
		return productLookup;
	}
	
	/**
	 * @return the product sku reader
	 */
	protected ProductSkuLookup getProductSkuLookup() {
		if (this.productSkuLookup == null) {
			this.productSkuLookup = this.getBean(ContextIdNames.PRODUCT_SKU_LOOKUP);
		}
		return productSkuLookup;
	}

	@Override
	public boolean isPriceTierExists(final BaseAmountDTO baseAmountDTO) {
		
		if (clientSideDuplicatesExist(baseAmountDTO, getModel().getBaseAmounts())) {
			return true;
		}
		return serverSideDuplicatesExist(baseAmountDTO);
	}

	/**
	 * Checks if duplicates exist on the client side.
	 *
	 * @param baseAmountDTO created/edited base amount
	 * @param clientSideBaseAmounts client side base amounts (stored in the model)
	 * @return true if duplicates exist
	 */
	boolean clientSideDuplicatesExist(final BaseAmountDTO baseAmountDTO, final Collection<BaseAmountDTO> clientSideBaseAmounts) {
		CollectionUtils.filter(clientSideBaseAmounts, baseAmount -> isQuantityAndTypeEqual(baseAmountDTO,
				(BaseAmountDTO) baseAmount));
		return isNotSameFirstElement(baseAmountDTO, clientSideBaseAmounts);
	}

	private boolean isQuantityAndTypeEqual(final BaseAmountDTO first, final BaseAmountDTO second) {
		boolean equal = second.getQuantity().compareTo(first.getQuantity()) == 0 && second.getObjectType().equals(first.getObjectType());

		if (first.getObjectGuid() != null &&  second.getObjectGuid() != null) { // when you create a base amount in a wizard it has no object guid
			equal = equal && second.getObjectGuid().equals(first.getObjectGuid());
		}

		return equal;
	}

	/**
	 * @param baseAmountDTO the dto to check for duplicates
	 * @return true if there are duplicates, false otherwise.
	 */
	protected boolean serverSideDuplicatesExist(final BaseAmountDTO baseAmountDTO) {
		if (isValidBaseAmountDTO(baseAmountDTO)) {
			// "if" above, is an optimisation switch to prevent unnecessary server calls
			// in case of new pricelist creation
			return isNotSameFirstElement(baseAmountDTO, getPersistentBaseAmounts(baseAmountDTO));
		}
		return false;
	}

	/**
	 * Checks if baseAmountDTO has non null pricelistdescriptor guid and object guid.
	 * @param baseAmountDTO to test.
	 * @return true if base amount has price list and object guid.
	 */
	protected boolean isValidBaseAmountDTO(final BaseAmountDTO baseAmountDTO) {
		return (baseAmountDTO.getPriceListDescriptorGuid() != null  && baseAmountDTO.getObjectGuid() != null);
	}

	/**
	 * @param baseAmountDTO the dto to use as a filter.
	 * @return persistent base amounts matching the input.
	 */
	protected Collection<BaseAmountDTO> getPersistentBaseAmounts(final BaseAmountDTO baseAmountDTO) {
		BaseAmountFilter filter = this.getBean(ContextIdNames.BASE_AMOUNT_FILTER);
		filter.setObjectGuid(baseAmountDTO.getObjectGuid());
		filter.setObjectType(baseAmountDTO.getObjectType());
		filter.setQuantity(baseAmountDTO.getQuantity());
		filter.setPriceListDescriptorGuid(baseAmountDTO.getPriceListDescriptorGuid());
		return this.getPriceListService().getBaseAmounts(filter);
	}

	/**
	 * @param baseAmountDTO the base amount to find duplicates for.
	 * @param baseAmounts the baseAmounts collection to find duplicates in.
	 * @return true if the first element in the collection is not the same as <code>baseAmountDTO</code>
	 */
	protected boolean isNotSameFirstElement(final BaseAmountDTO baseAmountDTO,
			final Collection<BaseAmountDTO> baseAmounts) {
		if (CollectionUtils.isNotEmpty(baseAmounts)) {
			BaseAmountDTO atLeastOne = baseAmounts.iterator().next();
			//if this method returns false - we are editing base amount (only in this case the guids will be equal)
			return !StringUtils.equals(baseAmountDTO.getGuid(), atLeastOne.getGuid());
		}
		return false;
	}
	
	/**
	 * Creates a new model object from price list in storage.
	 * 
	 * @return instance of the price list model
	 */
	protected PriceListEditorModel populateModel() {
		PriceListDescriptorDTO descriptor;
		Collection<BaseAmountDTO> baDTOs = new ArrayList<>();
		if (StringUtils.isEmpty(this.plGuid)) {
			descriptor = new PriceListDescriptorDTO();
			isUnsavedNew = true;
		} else {
			descriptor = this.getPriceListService().getPriceListDescriptor(plGuid);
			if (descriptor == null) {
				throw new IllegalArgumentException(
					NLS.bind(CoreMessages.get().Given_Object_Not_Exist,
					new String[]{"Price List", plGuid})); //$NON-NLS-1$
			}
			if (getNewlyCreatedProduct() == null) { //if we create a new product no need to query base amounts
				baDTOs = getBaseAmounts();
			}	
			isUnsavedNew = false;
		}
		return new PriceListEditorModelImpl(descriptor, baDTOs);
	}

	/**
	 * Returns base amounts.
	 *
	 * @return collection of base amounts.
	 */
	protected Collection<BaseAmountDTO> getBaseAmounts() {
		return getBaseAmounts(getBaseAmountsFilter());
	}


	@Override
	public PriceListEditorModel getModel() {
		if (this.model == null) {
			this.model = populateModel();
		}
		return this.model;
	}
	
	/**
	 * @param filter the filter to use for retrieving base amounts.
	 * @return uses service to retrieve base amount for given filter.
	 */
	protected Collection<BaseAmountDTO> getBaseAmounts(final BaseAmountFilterExt filter) {
		return this.getPriceListService().getBaseAmountsExt(filter);
	}

	@Override
	public boolean isUniqueBaseAmountDTO(final BaseAmountDTO oldBaseAmountDTO, final BaseAmountDTO newBaseAmountDTO) {
		return (this.getModel().logicalEquals(oldBaseAmountDTO, newBaseAmountDTO)
							|| (!isAlreadyPersisted(newBaseAmountDTO) && !isAlreadyInChangeSet(newBaseAmountDTO)))
				&& !isPriceTierExists(newBaseAmountDTO);
	}

	private boolean isAlreadyInChangeSet(final BaseAmountDTO newBaseAmountDTO) {
		for (BaseAmountDTO bas : this.getModel().getBaseAmounts()) {
			if (this.getModel().logicalEquals(bas, newBaseAmountDTO)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Is base amount persisted ?
	 * 
	 * @param newBaseAmountDTO base amount dto to check
	 * @return true if object persisted.
	 */
	private boolean isAlreadyPersisted(final BaseAmountDTO newBaseAmountDTO) {
		if (newBaseAmountDTO.getGuid() == null || newBaseAmountDTO.getObjectGuid() == null || newBaseAmountDTO.getObjectType() == null) {
			return false;
		}
		BaseAmountFilter filter = this.getBean(ContextIdNames.BASE_AMOUNT_FILTER);
		filter.setObjectGuid(newBaseAmountDTO.getObjectGuid());
		filter.setObjectType(newBaseAmountDTO.getObjectType());
		filter.setQuantity(newBaseAmountDTO.getQuantity());
		filter.setPriceListDescriptorGuid(newBaseAmountDTO.getPriceListDescriptorGuid());
		return this.getPriceListService().getBaseAmounts(filter)  != null;
	}

	@Override
	public void updateBaseAmountDTO(final BaseAmountDTO oldBaseAmountDTO, final BaseAmountDTO newBaseAmountDTO) {
		this.getModel().updateBaseAmount(oldBaseAmountDTO, newBaseAmountDTO);
	}

	@Override
	public void addBaseAmountDTO(final BaseAmountDTO newBaseAmountDTO) {
		preprocessNewBaseAmountDTO(newBaseAmountDTO);
		this.getModel().addBaseAmount(newBaseAmountDTO);
	}

	private BaseAmountDTO preprocessNewBaseAmountDTO(final BaseAmountDTO baseAmountDTO) {
		BaseAmountType baseAmountType = BaseAmountType.findByType(baseAmountDTO.getObjectType());
		if (baseAmountType == null) {
			return baseAmountDTO;
		}
		switch (baseAmountType) {
		case PRODUCT:
			Product product;
			if (baseAmountDTO.getObjectGuid() == null) {
				product = getNewlyCreatedProduct();
			} else {
				product = getProductLookup().findByGuid(baseAmountDTO.getObjectGuid());
			}			
			if (product == null) {
				break;
			}
			baseAmountDTO.setProductName(product.getDisplayName(getCurrentLocale()));
			baseAmountDTO.setProductCode(product.getCode());
			if (product.getProductType().isMultiSku()) {
				baseAmountDTO.setMultiSku(true);
			} 
			break;
		case SKU:
			ProductSku productSku = getProductSkuLookup().findBySkuCode(baseAmountDTO.getObjectGuid());
			if (productSku == null) {
				break;
			}
			baseAmountDTO.setSkuCode(productSku.getSkuCode());
			String skuConfiguration = PriceListEditorControllerHelper.formatSkuConfiguration(productSku.getOptionValues(), getCurrentLocale());
			baseAmountDTO.setSkuConfiguration(skuConfiguration);
			baseAmountDTO.setProductName(productSku.getProduct().getDisplayName(getCurrentLocale()));
			baseAmountDTO.setProductCode(productSku.getProduct().getCode());
			break;
		default:
		}
		return baseAmountDTO;
	}

	@Override
	public Collection<Catalog> getCatalogsFor(final BaseAmountDTO baseAmountDTO) {
		BaseAmountType baseAmountType = BaseAmountType.findByType(baseAmountDTO.getObjectType());
		if (baseAmountType == null) {
			return null;
		}
		Collection<Catalog> catalogs = null;
		switch (baseAmountType) {
		case PRODUCT:
			Product product = getProductLookup().findByGuid(baseAmountDTO.getObjectGuid());
			if (product == null) {
				break;
			}
			catalogs = product.getCatalogs();
			break;
		case SKU:
			ProductSku productSku = getProductSkuLookup().findBySkuCode(baseAmountDTO.getObjectGuid());
			if (productSku == null) {
				break;
			}
			catalogs = productSku.getProduct().getCatalogs();
			break;
		default:
		}
		return catalogs;
	}
	
	
	
	/**
	 * @return the newly created product or null if there isn't one. 
	 */
	protected Product getNewlyCreatedProduct() {
		return null;
	}


	@Override
	public void deleteBaseAmountDTO(final BaseAmountDTO baseAmountDTO) {
		this.getModel().removeBaseAmount(baseAmountDTO);
	}

	/**
	 * Reload the model from storage. Remove existing state. Notify views that the model has changed.
	 */
	@Override
	public void reloadModel() {
		this.model = populateModel();
	}

	/**
	 * Save the changes made to the Price list under edit by processing the BaseAmountChangeSet. Force set price list descriptor guid to base amount
	 * in case of new price list descriptor.
	 */
	@Override
	public void saveModel() {
		PriceListDescriptorDTO dto = this.getModel().getPriceListDescriptor();
		
		PriceListDescriptorDTO pldDTO = getPriceListService().saveOrUpdate(dto);

		if (isUnsavedNew && !changeSetHelper.getChangeSetObjectStatus(pldDTO).isLocked()) {
			changeSetHelper.addObjectToChangeSet(pldDTO, ChangeSetMemberAction.ADD);
			isUnsavedNew = false;
		}
		setPriceListDescriptorGuid(pldDTO.getGuid());
		setPriceListDescriptorGuidToNewBaseAmounts(plGuid);
		ChangeSetObjects<BaseAmountDTO> baseAmountChangeSet = this.getModel().getChangeSet();

		if (baseAmountChangeSet.getRemovalList().isEmpty()) {
			getPriceListService().modifyBaseAmountChangeSet(baseAmountChangeSet);
			changeSetHelper.addObjectsToChangeSet(baseAmountChangeSet);
		} else {
			changeSetHelper.addObjectsToChangeSet(baseAmountChangeSet);
			getPriceListService().modifyBaseAmountChangeSet(baseAmountChangeSet);
		}



		fireBaseAmountChangedEvent(baseAmountChangeSet);
		
		// remove all edited base amounts as they are already in a change set
		baseAmountChangeSet.getUpdateList().clear();

		CmUser currentUser = LoginManager.getCmUser();
		CmUserService cmUserService = this.getBean(ContextIdNames.CMUSER_SERVICE);
		currentUser = cmUserService.findByGuid(currentUser.getGuid());
		if (!currentUser.getPriceLists().contains(pldDTO.getGuid())) {
			// Make sure that the price list is added to the accessible price list collection
			// for both the CMUser in memory and stored in the database.
			currentUser.addPriceList(pldDTO.getGuid());
			cmUserService.update(currentUser);
			LoginManager.getCmUser().addPriceList(pldDTO.getGuid());
			AuthorizationService.getInstance().refreshRolesAndPermissions();
		}
		reloadModel();
	}

	private void setPriceListDescriptorGuidToNewBaseAmounts(final String plGuid) {
		for (BaseAmountDTO dto : this.getModel().getChangeSet().getAdditionList()) {
			dto.setPriceListDescriptorGuid(plGuid);
		}
	}

	private void fireBaseAmountChangedEvent(final ChangeSetObjects<BaseAmountDTO> baseAmountChangeSet) {
		fireBaseAmountEvent(baseAmountChangeSet.getAdditionList(), EventType.ADD);
		fireBaseAmountEvent(baseAmountChangeSet.getUpdateList(), EventType.CHANGE);
		fireBaseAmountEvent(baseAmountChangeSet.getRemovalList(), EventType.REMOVE);
	}

	private void fireBaseAmountEvent(final List<BaseAmountDTO> baseAmounts, final EventType eventType) {
		for (BaseAmountDTO baseAmountDTO : baseAmounts) {
			ItemChangeEvent<BaseAmountDTO> event = new ItemChangeEvent<>(this, baseAmountDTO, eventType);
			BaseAmountEventService.getInstance().fireBaseAmountChangedEvent(event);
		}
	}

	/**
	 * Sets the price list descriptor GUID. Can be set after controller is constructed. If the managed price list is different, change is not picked
	 * up until controller is reloaded.
	 * 
	 * @param plGuid a price list GUID identifier
	 */
	@Override
	public void setPriceListDescriptorGuid(final String plGuid) {
		this.plGuid = plGuid;
	}

	@Override
	public Collection<BaseAmountDTO> getAllBaseAmounts() {
		return getModel().getBaseAmountWithRemoved();
	}

	@Override
	public PriceListDescriptorDTO getPriceListDescriptor() {
		return getModel().getPriceListDescriptor();
	}

	@Override
	public boolean isNewlyAdded(final BaseAmountDTO baseAmount) {
		return getModel().isNewlyAdded(baseAmount);
	}

	@Override

	public boolean isDeleted(final BaseAmountDTO baseAmountDto) {
		return getModel().isDeleted(baseAmountDto);
	}

	@Override
	public boolean isEdited(final BaseAmountDTO baseAmountDto) {
		return getModel().isEdited(baseAmountDto);
	}

	@Override
	public boolean isPriceListNameUnique() {
		return getPriceListService().isPriceListNameUnique(getPriceListDescriptor().getGuid(), getPriceListDescriptor().getName());
	}

	@Override
	public Collection<PriceListDescriptorDTO> getAllPriceListDesctiptorsDTO() {
		return getPriceListService().getPriceListDescriptors(false);
	}

	@Override
	public BaseAmountFilterExt getBaseAmountsFilter() {
		if (filter == null) {
			filter = ServiceLocator.getService(ContextIdNames.BASE_AMOUNT_FILTER_EXT);
			SettingsService settingsService = ServiceLocator.getService(ContextIdNames.SETTINGS_SERVICE);
			int numOfResults =
				settingsService.getSettingValue("COMMERCE/APPSPECIFIC/RCP/PRICING/maximumBaseAmounts").getIntegerValue(); //$NON-NLS-1$
			filter.setLimit(numOfResults);
			filter.setLocale(new Locale(getCurrentLocale().getLanguage()));
		}
		filter.setPriceListDescriptorGuid(this.plGuid);				
		return filter;
	}

	@Override
	public Locale getCurrentLocale() {
		if (currentLocale == null) {
			return CorePlugin.getDefault().getDefaultLocale();
		}
		return currentLocale;
	}
	
	
	@Override
	public BaseAmountFilterExt getBaseAmountsUiFilter() {
		if (uiFilter == null) {
			uiFilter = ServiceLocator.getService(ContextIdNames.BASE_AMOUNT_FILTER_EXT);
			uiFilter.setPriceListDescriptorGuid(this.plGuid);
		}
		return uiFilter;
	}	

	@Override
	public void setModel(final PriceListEditorModel model) {
		this.model = model;
	}
	
	@Override
	public boolean isModelPersistent() {
		return !isUnsavedNew;
	}

	@Override
	public void setCurrentLocale(final Locale currentLocale) {
		this.currentLocale = currentLocale;
	}
	
}
