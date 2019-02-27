/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.removers;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.service.datapolicy.DataPointLocationEnum;
import com.elasticpath.service.datapolicy.impl.DataPointValue;

/**
 * The order IP address value remover.
 */
public class OrderIPAddressRemover extends AbstractDataPointValueRemover {

	@Override
	public String getSupportedLocation() {
		return DataPointLocationEnum.ORDER_IP_ADDRESS.getName();
	}

	@Override
	public Pair<String, String> getJPQLUpdate() {
		return new Pair<>("OrderImpl", "o");
	}

	@Override
	protected String getUpdateSetForEntity(final Collection<DataPointValue> dataPointValues, final String alias) {
		String jpqlSet =  "SET #alias#.lastModifiedDate=CURRENT_TIMESTAMP, #alias#.ipAddress='" + HYPHEN + "'";

		return StringUtils.replace(jpqlSet, "#alias#", alias);
	}
}
