/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce.transformer;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

/**
 * Transform a {@link GrantedAuthority} collection into Strings.
 */
public interface AuthoritiesTransformer {

	/**
	 * Transform the collection of authorities to a string readable form.
	 *
	 * @param authorities the authorities
	 * @return the converted authorities
	 */
	Collection<String> transform(Collection<? extends GrantedAuthority> authorities);
}
