/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.populators.impl;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Abstract implementation of a Builder's Populator instance. It automatically handles recreating the instance under build, so that each
 * successive call to {@link #build()} returns a new and populated instance.
 *
 * @param <I> the interface type of the item being populated.
 * @param <C> the concrete type of the item being populated.
 * @param <P> the interface type of this Populator.
 */
public abstract class AbstractRespawnBuilderPopulatorImpl<I, C, P> {

	private C instanceUnderBuild;
	private C previousInstanceUnderBuild;

	private Supplier<C> instanceSupplier;

	/**
	 * Default constructor. Calls {@link #createDefaultInstanceSupplier()} to set {@link #instanceSupplier} if it returns a default.
	 * Either way it can be set explicitly by a call to {@link #setInstanceSupplier(Supplier)}.
	 */
	public AbstractRespawnBuilderPopulatorImpl() {
		createDefaultInstanceSupplier().ifPresent(defaultSupplier -> this.instanceSupplier = defaultSupplier);
	}

	/**
	 * Returns a newly built instance from what was populated previously. Successive calls to this method will return in equal but newly created
	 * objects being returned.
	 *
	 * @return the newly built and populated instance.
	 */
	@SuppressWarnings("unchecked")
	public I build() {
		// This builder has been populating the instance under build as it's been going along so we can just return it
		final C result = getInstanceUnderBuild();
		
		// Before returning it clear the object so next getInstanceUnderBuild()/build() invocation returns a new instance
		invalidateInstanceUnderBuild();

		return (I) result;
	}

	/**
	 * Populate the instance fields.
	 *
	 * @param externalInstance the external instance.
	 * @return the builder itself for supporting fluent programming model.
	 */
	public P from(final I externalInstance) {
		copy(externalInstance);
		return self();
	}

	/**
	 * Returns this object using the generic builder type. Used for implementing {@code with...} methods and returning the correct builder sub-type.
	 * @return this object typed as the builder sub-type.
	 */
	@SuppressWarnings("unchecked")
	protected P self() {
		return (P) this;
	}

	/**
	 * Copy the external instance fields into the internal instance under build.
	 *
	 * @param externalInstance the external instance.
	 */
	protected abstract void copy(I externalInstance);

	/**
	 * Creates brand new instance for building.
	 *
	 * @return the instance.
	 */
	protected C createInstance() {
		return instanceSupplier.get();
	}

	/**
	 * Gets the instance under build, never {@code null}.
	 *
	 * @return the non-null internal instance under build.
	 */
	protected C getInstanceUnderBuild() {
		if (this.instanceUnderBuild == null) {
			initializeInstance();
		}
		return this.instanceUnderBuild;
	}

	protected void setInstanceUnderBuild(final C instanceUnderBuild) {
		this.instanceUnderBuild = instanceUnderBuild;
	}

	/**
	 * Creates a new instance, populates it from the previous instance if set (see {@link #getPreviousInstanceUnderBuild()}), and updates
	 * the {@link #getPreviousInstanceUnderBuild()} field to also point to this new field ready .
	 */
	@SuppressWarnings("unchecked")
	private void initializeInstance() {
		final C newInstance = createInstance();
		setInstanceUnderBuild(newInstance);

		final C previousInstance = getPreviousInstanceUnderBuild();

		if (previousInstance != null) {
			copy((I) previousInstance);
		}

		// We also set the previous instance to the new object so that multiple calls to initializeInstance() doesn't lose state
		setPreviousInstanceUnderBuild(newInstance);
	}

	/**
	 * An extension point that allows the instance supplier to be created by default by a subtype rather than forcing it to be injected,
	 * though it still can be if needed.
	 *
	 * @return {@link Optional#empty()} unless overridden.
	 */
	protected Optional<Supplier<C>> createDefaultInstanceSupplier() {
		return Optional.empty();
	}

	/**
	 * Invalidates the current instance under build so that the next time {@link #getInstanceUnderBuild()} is called, a new object is returned.
	 * This allows multiple successive invocations of {@link #build()} to be made each containing the same state, but returning different
	 * objects.
	 */
	protected void invalidateInstanceUnderBuild() {
		setInstanceUnderBuild(null);
	}

	protected C getPreviousInstanceUnderBuild() {
		return this.previousInstanceUnderBuild;
	}

	protected void setPreviousInstanceUnderBuild(final C previousInstanceUnderBuild) {
		this.previousInstanceUnderBuild = previousInstanceUnderBuild;
	}

	protected Supplier<C> getInstanceSupplier() {
		return this.instanceSupplier;
	}

	public void setInstanceSupplier(final Supplier<C> instanceSupplier) {
		this.instanceSupplier = instanceSupplier;
	}
}
