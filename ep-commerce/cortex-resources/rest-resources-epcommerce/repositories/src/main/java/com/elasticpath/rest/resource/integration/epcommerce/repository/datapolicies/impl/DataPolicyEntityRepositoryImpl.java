/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.impl;

import java.util.Collection;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.apache.commons.lang.BooleanUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.datapolicy.ConsentAction;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.datapolicies.DataPoliciesIdentifier;
import com.elasticpath.rest.definition.datapolicies.DataPolicyConsentFormIdentifier;
import com.elasticpath.rest.definition.datapolicies.DataPolicyEntity;
import com.elasticpath.rest.definition.datapolicies.DataPolicyIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.identity.attribute.SubjectAttribute;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.DataPolicyRepository;

/**
 * DataPolicy Entity Repository.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class DataPolicyEntityRepositoryImpl<E extends DataPolicyEntity, I extends DataPolicyIdentifier>
		implements Repository<DataPolicyEntity, DataPolicyIdentifier> {

	private static final String POLICY_GUID_NOT_PRESENT = "No policy GUID found on the request.";

	private DataPolicyRepository dataPolicyRepository;
	private ResourceOperationContext resourceOperationContext;


	@Override
	public Single<SubmitResult<DataPolicyIdentifier>> submit(final DataPolicyEntity entity, final IdentifierPart<String> scope) {

		if (!containsValidConsentValue(entity)) {
			return Single.error(ResourceOperationFailure.badRequestBody(DataPolicyRepositoryImpl.BOOLEAN_EXPECTED));
		}

		if (resourceOperationContext.getResourceIdentifier().isPresent()) {
			String policyId = extractDataPolicyGuid();
			String userId = resourceOperationContext.getUserIdentifier();

			return dataPolicyRepository.findValidDataPolicy(policyId, scope.getValue(), resourceOperationContext.getSubject().getAttributes())
					.flatMap(dataPolicy -> dataPolicyRepository.createCustomerConsentForDataPolicy(userId, dataPolicy, entity))
					.map(updatedConsent -> buildSubmitResult(scope, policyId));
		}

		return Single.error(ResourceOperationFailure.badRequestBody(POLICY_GUID_NOT_PRESENT));
	}

	private boolean containsValidConsentValue(final DataPolicyEntity entity) {
		return entity != null && BooleanUtils.toBooleanObject(entity.getDataPolicyConsent()) != null;
	}

	private String extractDataPolicyGuid() {
		return ((DataPolicyConsentFormIdentifier) resourceOperationContext.getResourceIdentifier().get())
				.getDataPolicy().getPolicyId().getValue();
	}

	@Override
	public Single<DataPolicyEntity> findOne(final DataPolicyIdentifier identifier) {

		String policyGuid = identifier.getPolicyId().getValue();
		String scope = identifier.getDataPolicies().getScope().getValue();
		Collection<SubjectAttribute> headers = resourceOperationContext.getSubject().getAttributes();

		return dataPolicyRepository.findValidDataPolicy(policyGuid, scope, headers)
				.flatMap(policy -> buildDataPolicyEntity(resourceOperationContext.getUserIdentifier(), policy));
	}

	@Override
	public Observable<DataPolicyIdentifier> findAll(final IdentifierPart<String> scope) {
		List<String> segmentHeaders = dataPolicyRepository.getSegmentHeadersLowerCase(resourceOperationContext.getSubject().getAttributes());

		return dataPolicyRepository.findActiveDataPoliciesForSegmentsAndStore(segmentHeaders, scope.getValue())
				.map(dataPolicy -> buildDataPolicyIdentifier(dataPolicy.getGuid(), scope));
	}

	private SubmitResult<DataPolicyIdentifier> buildSubmitResult(final IdentifierPart<String> scope, final String dataPolicyGuid) {
		return SubmitResult.<DataPolicyIdentifier>builder()
				.withStatus(SubmitStatus.UPDATED)
				.withIdentifier(buildDataPolicyIdentifier(dataPolicyGuid, scope))
				.build();
	}

	private Single<DataPolicyEntity> buildDataPolicyEntity(final String userIdentifier, final DataPolicy policy) {
		return dataPolicyRepository.findCustomerConsentByDataPolicyGuidForCustomer(policy.getGuid(), userIdentifier)
				.map(consent -> buildDataPolicyEntity(policy,
						Boolean.toString(consent.getAction().getOrdinal() == ConsentAction.GRANTED_ORDINAL)))
				.defaultIfEmpty(buildDataPolicyEntity(policy, Boolean.FALSE.toString())).toSingle();
	}

	private DataPolicyEntity buildDataPolicyEntity(final DataPolicy dataPolicy, final String consentFlag) {
		return DataPolicyEntity.builder()
				.withDataPolicyConsent(consentFlag)
				.withPolicyReferenceKey(dataPolicy.getReferenceKey())
				.withPolicyName(dataPolicy.getPolicyName())
				.build();
	}

	private DataPolicyIdentifier buildDataPolicyIdentifier(final String dataPolicyGuid, final IdentifierPart<String> scope) {
		return DataPolicyIdentifier.builder()
				.withPolicyId(StringIdentifier.of(dataPolicyGuid))
				.withDataPolicies(buildDataPoliciesIdentifier(scope))
				.build();
	}

	private DataPoliciesIdentifier buildDataPoliciesIdentifier(final IdentifierPart<String> scope) {
		return DataPoliciesIdentifier.builder().withScope(scope).build();
	}

	@Reference
	public void setDataPolicyRepository(final DataPolicyRepository dataPolicyRepository) {
		this.dataPolicyRepository = dataPolicyRepository;
	}

	@Reference
	public void setResourceOperationContext(final ResourceOperationContext resourceOperationContext) {
		this.resourceOperationContext = resourceOperationContext;
	}
}
