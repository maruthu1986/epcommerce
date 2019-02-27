/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce.transformer.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.springframework.security.core.GrantedAuthority;

import com.elasticpath.rest.relos.rs.authentication.epcommerce.transformer.AuthoritiesTransformer;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transform an authorities collection by calling each one's {@link GrantedAuthority#getAuthority()} method.
 */
@Component(property = AbstractDomainTransformer.DS_SERVICE_RANKING)
public class AuthoritiesTransformerImpl implements AuthoritiesTransformer {

	@Override
	public Collection<String> transform(final Collection<? extends GrantedAuthority> authorities) {
		Collection<String> strings = new ArrayList<>(authorities.size());
		for (GrantedAuthority authority : authorities) {
			strings.add(authority.getAuthority());
		}
		return strings;
	}
}
