/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder;

/**
 * Generic interface defining super builder.
 *
 * @param <I> interface type of the instance being built.
 * @param <P> interface type of the corresponding Populator (see {@link #getPopulator()} for more information).
 */
public interface Builder<I, P> {

	/**
	 * Returns the Populator associated with this builder. The Populator is used to populate this builder and is retrieved separately
	 * to allow for simpler builder inheritance as with the generic types this Builder interface cannot be reimplemented by a sub-type
	 * once it's been implemented by a super type. Instead the Populator interfaces can extend one another as they don't implement this
	 * Builder interface.
	 *
	 * @return the corresponding Populator object.
	 */
	P getPopulator();

	/**
	 * Copy from external instance as template.
	 *
	 * @param externalInstance the external instance copy from.
	 *
	 * @return the Populator to further populate/overwrite the object being built.
	 */
	P from(I externalInstance);

	/**
	 * Builds instance, multiple calls to this method should result in identical but distinct objects being returned.
	 *
	 * @return T the newly built instance.
	 */
	I build();
}
