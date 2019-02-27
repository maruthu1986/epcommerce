/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.search.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.elasticpath.cmclient.core.search.SafeSearchUids;
import com.elasticpath.persistence.api.Persistable;

/**
 * Helps enforce search privacy by ensuring a non-existent uid is supplied
 * for all searches.
 * This is needed when a user does not have permission on any catalog/store/warehouse
 * to prevent the search returning everything.
 *
 */
public class SafeSearchUidsImpl implements SafeSearchUids {
	
	private static final transient Logger LOG = Logger.getLogger(SafeSearchUidsImpl.class);

	private static final Long PERMISSION_ENFORCER = -1L;

	private Set<Long> uids;
	
	@Override
	public Set<Long> asSet() {
		if (CollectionUtils.isEmpty(uids)) {
			initialCodes();
		}
		return uids;
	}

	@Override
	public void extractAndAdd(final Collection< ? extends Persistable> collection) {
		initialCodes();
		for (Persistable object : collection) {
			try {
				if (object == null) {
					continue;
				}
				uids.add(object.getUidPk());
			} catch (Exception e) {
				LOG.error("get property error", e);   //$NON-NLS-1$
				continue;
			} 
		}
	}

	@Override
	public void extractAndAdd(final Persistable object) {
		initialCodes();
		try {
			if (object != null) {
				uids.add(object.getUidPk());
			}
		} catch (Exception e) {
			LOG.error("get property error", e);  //$NON-NLS-1$
		} 
	}

	private void initialCodes() {
		uids = new HashSet<Long>();
		uids.add(PERMISSION_ENFORCER);
	}

}
