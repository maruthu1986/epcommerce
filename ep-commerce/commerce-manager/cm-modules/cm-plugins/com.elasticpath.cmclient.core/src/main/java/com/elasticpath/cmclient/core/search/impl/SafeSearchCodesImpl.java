/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.search.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.elasticpath.cmclient.core.search.SafeSearchCodes;

/**
 * Helps enforce search privacy by ensuring a non-existent code is supplied
 * for all searches.
 * This is needed when a user does not have permission on any catalog/store/warehouse
 * to prevent the search returning everything.
 *
 */
public class SafeSearchCodesImpl implements SafeSearchCodes {
	
	private static final transient Logger LOG = Logger.getLogger(SafeSearchCodesImpl.class);

	private static final String PERMISSION_ENFORCER = "PermissionEnforcer";    //$NON-NLS-1$

	private Set<String> codes;
	
	@Override
	public Set<String> asSet() {
		if (CollectionUtils.isEmpty(codes)) {
			initialCodes();
		}
		return codes;
	}

	@Override
	public void extractAndAdd(final Collection<?> collection, final String propertyName) {
		initialCodes();
		for (Object object : collection) {
			try {
				String code = BeanUtils.getProperty(object, propertyName);
				codes.add(code);
			} catch (Exception e) {
				LOG.error("get property error", e);   //$NON-NLS-1$
				continue;
			} 
		}
	}

	@Override
	public void extractAndAdd(final Object object, final String propertyName) {
		initialCodes();
		try {
			String code = BeanUtils.getProperty(object, propertyName);
			codes.add(code);
		} catch (Exception e) {
			LOG.error("get property error", e);  //$NON-NLS-1$
		} 
	}

	private void initialCodes() {
		codes = new HashSet<String>();
		codes.add(PERMISSION_ENFORCER);
	}

}
