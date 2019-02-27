/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.domain.store.StoreType;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.command.CommandService;
import com.elasticpath.service.command.UpdateStoreCommand;
import com.elasticpath.service.command.UpdateStoreCommandResult;
import com.elasticpath.service.datapolicy.DataPolicyService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;

/**
 * This class is responsible for store editor model creation, reloading and removing.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class StoreEditorModelHelper {

	private static final String COMMERCE_STORE_THEME = "COMMERCE/STORE/theme"; //$NON-NLS-1$

	private static final String COMMERCE_STORE_BROWSING = "COMMERCE/STORE/FILTEREDNAVIGATION/filteredNavigationConfiguration"; //$NON-NLS-1$

	private static final String COMMERCE_STORE_ADVANCED_SEARCH = "COMMERCE/STORE/ADVANCEDSEARCH/advancedSearchConfiguration"; //$NON-NLS-1$

	private CommandService commandService;

	private UpdateStoreCommand updateStoreCommand;

	private SettingsService settingsService;

	private StoreService storeService;

	private FetchGroupLoadTuner fetchGroupLoadTuner;

	private static final String AVAILABLE_TO_MARKETING = "availableToMarketing"; //$NON-NLS-1$

	private static final String[] SYSTEM_SETTING_VALUE_PATHS = { };
	
	private CmUserService cmUserService;

	/**
	 * Constructs store editor model helper.
	 */
	StoreEditorModelHelper() {
		//do nothing
	}
	
	/**
	 * Creates the store editor model helper.
	 * 
	 * @return the store editor model helper
	 */
	public static StoreEditorModelHelper createStoreEditorModelHelper() {
		final StoreEditorModelHelper editorModelHelper = new StoreEditorModelHelper();
		editorModelHelper.setSettingsService(ServiceLocator.getService(ContextIdNames.SETTINGS_SERVICE));
		editorModelHelper.setStoreService(ServiceLocator.getService(ContextIdNames.STORE_SERVICE));
		editorModelHelper.setCommandService(ServiceLocator.getService(ContextIdNames.COMMAND_SERVICE));
		editorModelHelper.setUpdateStoreCommand(ServiceLocator.getService(ContextIdNames.UPDATE_STORE_COMMAND));
		editorModelHelper.setCmUserService(ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE));
		final FetchGroupLoadTuner fetchGroupLoadTuner = ServiceLocator.getService(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		fetchGroupLoadTuner.addFetchGroup(FetchGroupConstants.STORE_FOR_EDIT);
		editorModelHelper.setFetchGroupLoadTuner(fetchGroupLoadTuner);
		return editorModelHelper;
	}

	/**
	 * Creates the store editor model based on store UID.
	 * 
	 * @param storeUid the store UID
	 * @return the new store editor model
	 */
	public StoreEditorModel createStoreEditorModel(final long storeUid) {
		return createStoreEditorModel(storeUid, false);
	}

	/**
	 * Creates the store editor model based on store UID and way of loading marketing relating settings.
	 * 
	 * @param storeUid the store UID
	 * @param availableToMarketingOnly use only settings available to marketing if true, all otherwise 
	 * @return store editor model
	 */
	public StoreEditorModel createStoreEditorModel(final long storeUid, final boolean availableToMarketingOnly) {
		final Store store = loadStoreWithSharedStores(storeUid, fetchGroupLoadTuner);
		final StoreEditorModel storeEditorModel = new StoreEditorModel(store, availableToMarketingOnly);
		loadSettings(storeEditorModel);
		return storeEditorModel;
	}

	/**
	 * Loads the store with shared stores.
	 * 
	 * @param storeUid the store uid
	 * @param loadTuner the fetch group load tuner
	 * @return the loaded store
	 */
	Store loadStoreWithSharedStores(final long storeUid, final FetchGroupLoadTuner loadTuner) {
		final Store store = loadStore(storeUid, loadTuner);
		loadSharedStores(store);
		return store;
	}
	
	/**
	 * Loads all the shared stores for the given store.
	 * 
	 * @param store the root store
	 * @return the collection of shared stored
	 */
	Collection<Store> loadSharedStores(final Store store) {
		Collection<Store> result = storeService.getTunedStores(store.getAssociatedStoreUids(), fetchGroupLoadTuner);
		if (result == null) {
			result = Collections.emptySet();
		}
		return result;
	}

	/**
	 * Loads the store with given uid.
	 * 
	 * @param storeUid the store uid
	 * @param loadTuner the fetch group load tuner
	 * @return the loaded store
	 */
	Store loadStore(final long storeUid, final FetchGroupLoadTuner loadTuner) {
		final Store store;
		if (storeUid > 0) {
			store = storeService.getTunedStore(storeUid, loadTuner);
			if (store == null) {
				throw new EpServiceException("Store with UID " + storeUid + " does not exist"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			store = createStore();

			store.setStoreType(StoreType.B2B);
			store.setCountry(getDefaultLocale().getCountry());
			store.setEnabled(true);
		}
		return store;
	}

	/**
	 * This is stubbed out for testing.
	 *
	 * @return a new store instance
	 */
	protected Store createStore() {
		return ServiceLocator.getService(ContextIdNames.STORE);
	}

	/**
	 * Gets the default locale.
	 * 
	 * @return default locale
	 */
	Locale getDefaultLocale() {
		return CorePlugin.getDefault().getDefaultLocale();
	}

	/**
	 * Loads the settings to model.
	 * 
	 * @param model the model to update
	 */
	void loadSettings(final StoreEditorModel model) {
		model.setMarketingSettings(getMarketingSettings(model));
		model.setSystemSettings(getSystemSettings(model));
		model.setStoreThemeSetting(getSettingValue(COMMERCE_STORE_THEME, model));
		model.setStoreBrowsingSetting(getSettingValue(COMMERCE_STORE_BROWSING, model));
		model.setStoreAdvancedSearchSetting(getSettingValue(COMMERCE_STORE_ADVANCED_SEARCH, model));

		SettingValue settingValue = settingsService.getSettingValue(DataPolicyService.COMMERCE_STORE_ENABLE_DATA_POLICIES, model.getCode());
		model.setStoreEnableDataPoliciesSettingEnabled(settingValue != null && settingValue.getBooleanValue());
	}

	/**
	 * Gets the marketing settings for given model.
	 * 
	 * @param model the model
	 * @return the list of settings
	 */
	List<SettingModel> getMarketingSettings(final StoreEditorModel model) {
		final List<SettingModel> marketingSettings = new ArrayList<SettingModel>();
		marketingSettings.addAll(loadMarketingSettingsContainedInStore(model));
		marketingSettings.addAll(loadCommonStoreMarketingSettings(model));
		return marketingSettings;
	}

	/*
	 * Loads marketing related settings contained in Store domain object.
	 */
	private List<SettingModel> loadMarketingSettingsContainedInStore(final StoreEditorModel model) {
		final SettingsFactory storeSettingsFactory = new StoreSettingsFactory(model);
		final String[] storeSettingPaths = { StoreSettingsFactory.FRIENDLY_NAME_SETTING, StoreSettingsFactory.SENDER_ADDRESS_SETTING,
				StoreSettingsFactory.ADMIN_EMAIL_ADDRESS_SETTING, StoreSettingsFactory.DISPLAY_OUT_OF_STOCK_SETTING };
		return createSettingModelList(storeSettingPaths, model.getCode(), storeSettingsFactory);
	}

	/*
	 * Loads marketing related settings with associated 'availableToMarketing' metadata.
	 */
	private List<SettingModel> loadCommonStoreMarketingSettings(final StoreEditorModel model) {
		final List<SettingDefinition> settingDefinitions = new ArrayList<SettingDefinition>();
		if (model.loadSettingsAvailableToMarketingOnly()) {
			settingDefinitions.addAll(settingsService.findSettingDefinitionsByMetadataValue(
					AVAILABLE_TO_MARKETING, Boolean.TRUE.toString()));
		} else {
			settingDefinitions.addAll(settingsService.findSettingDefinitionsByMetadata(AVAILABLE_TO_MARKETING)); 
		}

		final List<SettingModel> marketingSettingModels = new ArrayList<SettingModel>();
		final SettingsFactory settingValuesFactory = new SettingsValueFactory(model);

		for (SettingDefinition settingDefinition : settingDefinitions) {
			marketingSettingModels.add(settingValuesFactory.createSetting(settingDefinition.getPath(), model.getCode()));
		}
		return marketingSettingModels;
	}

	/**
	 * Gets the system settings for given model.
	 * 
	 * @param model the model
	 * @return the list of settings
	 */
	List<SettingModel> getSystemSettings(final StoreEditorModel model) {
		final SettingsFactory storeSettingsFactory = new StoreSettingsFactory(model);
		final SettingsFactory settingValuesFactory = new SettingsValueFactory(model);

		final List<SettingModel> systemSettings = new ArrayList<SettingModel>();
		final String[] storeSettingPaths = { StoreSettingsFactory.HTML_ENCODING_SETTING };
		systemSettings.addAll(createSettingModelList(storeSettingPaths, model.getCode(), storeSettingsFactory));
		systemSettings.addAll(createSettingModelList(SYSTEM_SETTING_VALUE_PATHS, model.getCode(), settingValuesFactory));
		return systemSettings;
	}

	private List<SettingModel> createSettingModelList(final String[] paths, final String storeCode, final SettingsFactory factory) {
		final List<SettingModel> resultList = new ArrayList<SettingModel>();
		for (String path : paths) {
			resultList.add(factory.createSetting(path, storeCode));
		}
		return resultList;
	}

	/**
	 * Deletes the given store from database.
	 * 
	 * @param model the store model that contains store to delete
	 */
	public void destroyModel(final StoreEditorModel model) {
		storeService.remove(model.getStore());
	}

	/**
	 * Reloads the given model.
	 * 
	 * @param model the model to reload
	 * @return the updated model
	 */
	public StoreEditorModel reload(final StoreEditorModel model) {
		final Store store = loadStoreWithSharedStores(model.getUidPk(), fetchGroupLoadTuner);
		model.setStore(store);
		loadSettings(model);
		return model;
	}

	/**
	 * Flushes the model to database.
	 * If the store was not previously persistent then it adds the store
	 * to the list of stores accessible by the current user and refreshes the local
	 * cache of store permissions.
	 * 
	 * @param model the model to update
	 * @return updated model
	 */
	public StoreEditorModel flush(final StoreEditorModel model) {
		sanityCheck(model);

		final Set<StoreEditorModel> addedModels = model.getAddedModels();
		final Set<StoreEditorModel> removedModels = model.getRemovedModels();
		//determine whether this store is being saved for the first time
		boolean newStore = false;
		if (!model.getStore().isPersisted()) {
			newStore = true;
		}
		//save the store to the server
		updateStoreModel(model);
		//make sure the store, if new, is added to the current user's list of stores
		if (newStore) {
			addStoreToCurrentUser(model.getStore());
		}

		reflectAdditionInModels(model, addedModels);
		reflectDeletionInModels(model, removedModels);
		return model;
	}

	/**
	 * Adds the given store code to the list of store codes designating the stores
	 * to which the current user has edit permissions, and updates the local cache
	 * of the user's permissions so that if the user closes the store editor and re-opens
	 * it then they'll still have permissions to edit the store they just created.
	 *  
	 * @param store the store to add to the current user
	 */
	protected void addStoreToCurrentUser(final Store store) {
		if (AuthorizationService.getInstance().isAuthorizedAllStores()) {
			return;
		}
		//retrieve the CmUser from the server with the stores, warehouses, and catalogs populated
		CmUser serverCmUser = getCmUserService().findByUserNameWithAccessInfo(LoginManager.getCmUserUsername());
		serverCmUser.addStore(store);
		getCmUserService().update(serverCmUser);	
		AuthorizationService.getInstance().refreshRolesAndPermissions();
	}
	
	/**
	 * Checks that given model can be saved.
	 * 
	 * @param model the model to save
	 * @throws EpServiceException if store with given code or url already exists
	 */
	void sanityCheck(final StoreEditorModel model) {
		final Store storeByCode = storeService.findStoreWithCode(model.getCode());
		if (checkStoresDifference(model, storeByCode)) {
			throw new EpServiceException(CoreMessages.get().StoreCodeExists);
		}

		if (isUrlNotUniqueForOpenStore(model)) {
			throw new EpServiceException(CoreMessages.get().StoreUrlExists);
		}
		
		if (!model.isPaymentMethodSelected()) {
			throw new EpServiceException(CoreMessages.get().PaymentMethodRequired);
		}
	}
	
	/**
	 * Check if the store's Url is unique for open stores. 
	 *  Used when store is transfered to open state.  
	 * 
	 * @param model the model to check
	 * @return true if we are changing an open store and other open store has the same url
	 */
	boolean isUrlNotUniqueForOpenStore(final StoreEditorModel model) {
		return StringUtils.isNotEmpty(model.getUrl()) && model.getStoreState().equals(StoreState.OPEN)
				&& !storeService.isStoreUrlUniqueForState(model.getStore(), StoreState.OPEN);
	}

	/**
	 * Check if the store's Url is unique for stores.
	 * 
	 * @param model the model to check
	 * @return true if we are saving store and other open store has the same url
	 */
	public boolean isUrlNotUniqueStore(final StoreEditorModel model) {
		return StringUtils.isNotEmpty(model.getUrl()) 	&& !storeService.isStoreUrlUniqueForState(model.getStore(), StoreState.OPEN);
	}

	
	/**
	 * Checks if the stores are different.
	 * 
	 * @param model the store model
	 * @param storeByCode the store
	 * @return true if stores are different and false otherwise
	 */
	boolean checkStoresDifference(final StoreEditorModel model, final Store storeByCode) {
		return storeByCode != null && storeByCode.getUidPk() != model.getUidPk();
	}

	/**
	 * Updates the store model.
	 * 
	 * @param model the model to update
	 */
	void updateStoreModel(final StoreEditorModel model) {
		final Store store = model.getStore();
		store.getAssociatedStoreUids().clear();
		for (StoreEditorModel sharedModel : model.getSharedLoginStoreEntries()) {
			store.getAssociatedStoreUids().add(sharedModel.getStore().getUidPk());
		}
		
		updateStoreCommand.setStore(store);
		updateStoreCommand.setSettingValues(getSettingsMap(model));
		final Store updatedStore = ((UpdateStoreCommandResult) commandService.execute(updateStoreCommand)).getStore();
		model.setStore(updatedStore);
	}

	/*
	 * Adds store encapsulated in this model to shared stores of models which have been added to this model.
	 */
	private void reflectAdditionInModels(final StoreEditorModel model, final Set<StoreEditorModel> addedModels) {
		for (StoreEditorModel addedModel : addedModels) {
			addedModel = reload(addedModel);
			addedModel.getStore().getAssociatedStoreUids().add(model.getStore().getUidPk());
			updateStoreCommand.setStore(addedModel.getStore());
			updateStoreCommand.setSettingValues(getSettingsMap(addedModel));
			commandService.execute(updateStoreCommand);
		}
	}

	/*
	 * Deletes store encapsulated in this model from shared stores of models which have been deleted from this model.
	 */
	private void reflectDeletionInModels(final StoreEditorModel model, final Set<StoreEditorModel> removedModels) {
		for (StoreEditorModel removedModel : removedModels) {
			removedModel = reload(removedModel);
			removedModel.getStore().getAssociatedStoreUids().remove(model.getStore().getUidPk());
			updateStoreCommand.setStore(removedModel.getStore());
			updateStoreCommand.setSettingValues(getSettingsMap(removedModel));
			commandService.execute(updateStoreCommand);
		}
	}

	/**
	 * Gets the settings map from given model.
	 * 
	 * @param model the model
	 * @return the model settings
	 */
	Map<String, String> getSettingsMap(final StoreEditorModel model) {
		final Map<String, String> settingsValueMap = new HashMap<String, String>();
		settingsValueMap.put(COMMERCE_STORE_THEME, model.getStoreThemeSetting());
		settingsValueMap.put(COMMERCE_STORE_BROWSING, model.getStoreBrowsingSetting());
		settingsValueMap.put(COMMERCE_STORE_ADVANCED_SEARCH, model.getStoreAdvancedSearchSetting());
		settingsValueMap.put(DataPolicyService.COMMERCE_STORE_ENABLE_DATA_POLICIES, model.isStoreEnableDataPoliciesSettingEnabled().toString());
		processSettingModels(settingsValueMap, model.getMarketingSettings());
		processSettingModels(settingsValueMap, model.getSystemSettings());
		return settingsValueMap;
	}

	private void processSettingModels(final Map<String, String> settingsValueMap, final List<SettingModel> settings) {
		if (settings == null) {
			return;
		}

		for (SettingModel settingModel : settings) {
			settingModel.updateSettings(settingsValueMap);
		}
	}

	private String getSettingValue(final String path, final StoreEditorModel model) {
		return settingsService.getSettingValue(path, model.getCode()).getValue();
	}

	/**
	 * finds all available store editor models.
	 * 
	 * @return the list of all store editor models
	 */
	public List<StoreEditorModel> findAllStoreEditorModels() {
		final List<StoreEditorModel> result = new ArrayList<StoreEditorModel>();
		final List<Store> allStores = storeService.findAllStores();
		for (Store store : allStores) {
			result.add(new StoreEditorModel(loadStore(store.getUidPk(), null)));
		}
		return result;
	}

	/**
	 * Sets the settings service.
	 * 
	 * @param settingsService the settings service to set
	 */
	void setSettingsService(final SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	/**
	 * Sets the store service.
	 * 
	 * @param storeService the store service to set
	 */
	void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	/**
	 * Sets the command service.
	 * 
	 * @param commandService the command service to set
	 */
	void setCommandService(final CommandService commandService) {
		this.commandService = commandService;
	}
	
	/**
	 * Sets the CmUserService.
	 * @param cmUserService the cmuserservice to set
	 */
	void setCmUserService(final CmUserService cmUserService) {
		this.cmUserService = cmUserService;
	}
	
	/**
	 * @return the CmUserService
	 */
	CmUserService getCmUserService() {
		return this.cmUserService;
	}

	/**
	 * Sets update store command service.
	 * 
	 * @param updateStoreCommand the update store command service to set
	 */
	void setUpdateStoreCommand(final UpdateStoreCommand updateStoreCommand) {
		this.updateStoreCommand = updateStoreCommand;
	}

	/**
	 * Sets the fetch group load tuner.
	 * 
	 * @param fetchGroupLoadTuner the load tuner to set
	 */
	void setFetchGroupLoadTuner(final FetchGroupLoadTuner fetchGroupLoadTuner) {
		this.fetchGroupLoadTuner = fetchGroupLoadTuner;
	}
}