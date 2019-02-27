/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.emails.impl;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.definition.emails.EmailIdentifier;
import com.elasticpath.rest.definition.emails.EmailsIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;

/**
 * Email Entity Repository.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class EmailEntityRepositoryImpl<E extends EmailEntity, I extends EmailIdentifier>
		implements Repository<EmailEntity, EmailIdentifier> {

	private CustomerRepository customerRepository;
	private ResourceOperationContext resourceOperationContext;

	@Override
	public Single<SubmitResult<EmailIdentifier>> submit(final EmailEntity emailEntity, final IdentifierPart<String> scope) {

		String customerGuid = resourceOperationContext.getUserIdentifier();

		String email = emailEntity.getEmail();

		return customerRepository.getCustomer(customerGuid)
				.flatMap(customer -> saveAndUpdateCustomerEmail(customer, email, scope));
	}

	@Override
	public Single<EmailEntity> findOne(final EmailIdentifier identifier) {

		String emailId = identifier.getEmailId().getValue();

		return customerRepository.getCustomer(resourceOperationContext.getUserIdentifier())
				.map(customer -> EmailEntity.builder()
						.withEmailId(emailId)
						.withEmail(customer.getEmail())
						.build());
	}

	@Override
	public Observable<EmailIdentifier> findAll(final IdentifierPart<String> scope) {

		EmailsIdentifier emailsIdentifier = EmailsIdentifier.builder().withScope(scope).build();

		return customerRepository.getCustomer(resourceOperationContext.getUserIdentifier())
				.map(customer -> EmailIdentifier.builder()
						.withEmails(emailsIdentifier)
						.withEmailId(StringIdentifier.of(customer.getEmail()))
						.build())
				.toObservable();
	}

	@Reference
	public void setCustomerRepository(final CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Reference
	public void setResourceOperationContext(final ResourceOperationContext resourceOperationContext) {
		this.resourceOperationContext = resourceOperationContext;
	}

	/**
	 * Sets and updates a customers email.
	 *
	 * @param customer	the customer
	 * @param email		customer's email
	 * @param scope		the scope
	 * @return Single<EmailIdentifier>
	 */
	protected Single<SubmitResult<EmailIdentifier>> saveAndUpdateCustomerEmail(final Customer customer, final String email, final
	IdentifierPart<String> scope) {

		String emailTrim = StringUtils.trimToNull(email);

		customer.setEmail(emailTrim);

		return customerRepository.updateCustomerAsCompletable(customer)
				.toSingle(() -> SubmitResult.<EmailIdentifier>builder()
						.withIdentifier(buildEmailIdentifier(email, scope))
						.withStatus(SubmitStatus.CREATED)
						.build());

	}

	private EmailIdentifier buildEmailIdentifier(final String email, final IdentifierPart<String> scope) {
		return EmailIdentifier.builder()
				.withEmailId(StringIdentifier.of(email))
				.withEmails(EmailsIdentifier.builder()
						.withScope(scope)
						.build())
				.build();
	}

}