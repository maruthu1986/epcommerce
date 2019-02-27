/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies;

import java.util.List;

import io.reactivex.Observable;

import com.elasticpath.rest.advise.LinkedMessage;
import com.elasticpath.rest.definition.datapolicies.DataPolicyConsentFormIdentifier;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Validation service for data policy operations.
 */
public interface DataPolicyValidationService {
	/**
	 * Checks if the user has granted consent for data policies relevant to the given scope
	 * and which contain data points with the given data point locations.
	 * <p>
	 * This method can be used for advisors to validate against the desired advisor-triggering data point locations.
	 * For example, if an advisor should be triggered when a customer shopping in the "MOBEE" scope has not consented
	 * to a data policy containing a data point with the data point location value of "CUSTOMER_PROFILE", then those
	 * values should be passed to the "validate(...)" method.
	 *
	 * @param scope              scope
	 * @param dataPointLocations list of data point location values
	 * @return linked messages for unaccepted data policies
	 */
	Observable<LinkedMessage<DataPolicyConsentFormIdentifier>> validate(IdentifierPart<String> scope, List<String> dataPointLocations);
}
