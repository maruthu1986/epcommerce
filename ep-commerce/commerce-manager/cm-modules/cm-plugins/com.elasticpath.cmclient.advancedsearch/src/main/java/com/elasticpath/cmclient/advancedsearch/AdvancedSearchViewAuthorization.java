/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.advancedsearch;

import com.elasticpath.cmclient.core.security.Authorizable;
import com.elasticpath.cmclient.core.service.AuthorizationService;

/**
 * @author dyao
 *
 */
public class AdvancedSearchViewAuthorization implements Authorizable {

	/**
	 * Determine if the user has the permission to register this view.
	 * 
	 * @return true if the user is allowed to register this view
	 */
	@Override
	public boolean isAuthorized() {
		AuthorizationService authorizationService = AuthorizationService.getInstance();
		return authorizationService.isAuthorizedWithPermission(AdvancedSearchPermissions.CREATE_QUERIES)
			|| authorizationService.isAuthorizedWithPermission(AdvancedSearchPermissions.MANAGE_QUERIES);
	}

}
