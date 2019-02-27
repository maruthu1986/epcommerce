/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.command.impl;

import java.util.Map;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.command.CommandResult;
import com.elasticpath.service.command.UpdateStoreCommand;
import com.elasticpath.service.command.UpdateStoreCommandResult;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Updates store using core services.
 */
public class UpdateStoreCommandImpl implements UpdateStoreCommand {

	private static final long serialVersionUID = 1L;

	private Store store;

	private Map<String, String> settingValueMap;

	private UpdateStoreCommandResult updateStoreCommandResult;

	/**
	 * Updates the store and setting values.
	 * 
	 * @return command result
	 */
	@Override
	public CommandResult execute() {
		updateStoreCommandResult.setStore(getStoreService().saveOrUpdate(store));
		final String storeCode = store.getCode();
		for (final Map.Entry<String, String> settingValueEntry : settingValueMap.entrySet()) {
			final SettingValue settingValue = getSettingsService().getSettingValue(settingValueEntry.getKey(), storeCode);
			settingValue.setValue(settingValueEntry.getValue());
			getSettingsService().updateSettingValue(settingValue);
		}
		return updateStoreCommandResult;
	}

	@Override
	public void setStore(final Store store) {
		this.store = store;
	}

	@Override
	public void setSettingValues(final Map<String, String> settingValueMap) {
		this.settingValueMap = settingValueMap;
	}

	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	private StoreService getStoreService() {
		return ElasticPathImpl.getInstance().getBean(ContextIdNames.STORE_SERVICE);
	}

	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	private SettingsService getSettingsService() {
		return ElasticPathImpl.getInstance().getBean(ContextIdNames.SETTINGS_SERVICE);
	}

	public void setUpdateStoreCommandResult(final UpdateStoreCommandResult updateStoreCommandResult) {
		this.updateStoreCommandResult = updateStoreCommandResult;
	}
}
