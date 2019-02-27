/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.order.repositories;

import io.reactivex.Observable;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.emails.EmailIdentifier;
import com.elasticpath.rest.definition.emails.EmailsIdentifier;
import com.elasticpath.rest.definition.orders.EmailInfoIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.common.authentication.AuthenticationConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;

/**
 * Repository for customer email given email information identifier.
 *
 * @param <I>  the identifier type
 * @param <LI> the linked identifier type
 */
@Component
public class EmailsLinkRepositoryImpl<I extends EmailInfoIdentifier, LI extends EmailIdentifier>
		implements LinksRepository<EmailInfoIdentifier, EmailIdentifier> {

	private CustomerRepository customerRepository;
	private ResourceOperationContext resourceOperationContext;

	@Override
	public Observable<EmailIdentifier> getElements(final EmailInfoIdentifier identifier) {
		return customerRepository.getCustomer(resourceOperationContext.getUserIdentifier())
				.map(Customer::getEmail)
				.flatMapObservable(customerEmail -> getEmailIdentifier(identifier.getOrder().getScope(), customerEmail));
	}

	/**
	 * Get email identifier if customer email is not public email.
	 *
	 * @param customerEmail customer email
	 * @param scope store code
	 * @return email identifier (if any)
	 */
	protected Observable<EmailIdentifier> getEmailIdentifier(final IdentifierPart<String> scope, final String customerEmail) {

		boolean validEmail = StringUtils.isNotEmpty(customerEmail)
				&& ObjectUtils.notEqual(AuthenticationConstants.ANONYMOUS_USER_ID, customerEmail);

		if (validEmail) {
			EmailsIdentifier emails = EmailsIdentifier.builder()
					.withScope(scope)
					.build();

			return Observable.just(EmailIdentifier.builder()
					.withEmails(emails)
					.withEmailId(StringIdentifier.of(customerEmail))
					.build());
		}

		return Observable.empty();
	}

	@Reference
	public void setCustomerRepository(final CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Reference
	public void setResourceOperationContext(final ResourceOperationContext resourceOperationContext) {
		this.resourceOperationContext = resourceOperationContext;
	}
}
