/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.search.index.loader.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * Fetches a batch of {@link CmUser}s.
 */
public class BatchCmUserLoader extends AbstractEntityLoader<CmUser> {

	private CmUserService cmUserService;

	/**
	 * Loads the {@link CmUser}s for the batched ids and loads each batch in bulk.
	 * 
	 * @return the loaded {@link CmUser}s
	 */
	@Override
	public Collection<CmUser> loadBatch() {

		final Collection<CmUser> loadedCmUserBatch = cmUserService.findByUids(getUidsToLoad());

		List<CmUser> completeCmUsers = new ArrayList<>();

		for (CmUser loadedCmUser : loadedCmUserBatch) {
			CmUser fullCmUser = cmUserService.findByUserNameWithAccessInfo(loadedCmUser.getUserName());
			completeCmUsers.add(fullCmUser);
		}

		return completeCmUsers;
	}

	/**
	 * Sets the {@link CmUserService}.
	 * 
	 * @param skuService the {@link CmUserService}
	 */
	public void setCmUserService(final CmUserService skuService) {
		this.cmUserService = skuService;
	}

	/**
	 * Gets the {@link CmUserService}.
	 * 
	 * @return the {@link CmUserService}
	 */
	public CmUserService getCmUserService() {
		return cmUserService;
	}

}
