/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.users.helpers;

import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.cmclient.admin.users.event.AdminUsersEventService;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * This class represents a job responsible for retrieving CmUsers from the database.
 */
public class UserSearchRequestJob extends AbstractSearchRequestJob<CmUser> {

	private static final Logger LOG = Logger.getLogger(UserSearchRequestJob.class);
	
	private final CmUserService userService;

	/**
	 * Default constructor.
	 * 
	 */
	public UserSearchRequestJob() {
		super();
		this.userService = (CmUserService) ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);
	}

	@Override
	public void fireItemsUpdated(final List<CmUser> itemList, final int startIndex, final int totalFound) {
		SearchResultEvent<CmUser> event = new SearchResultEvent<>(this, itemList, startIndex, totalFound, EventType.SEARCH);
		AdminUsersEventService.getInstance().fireUsersSearchResultEvent(event);
	}

	@Override
	public List<CmUser> getItems(final List<Long> uidList) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("Converting %1$S UID(s) to CmUsers.", uidList.size())); //$NON-NLS-1$
		}
		return userService.findByUids(uidList);
	}
}
