/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository;

import javax.inject.Named;
import javax.inject.Singleton;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.elasticpath.rest.cache.CacheRemove;
import com.elasticpath.rest.cache.CacheRemoveDecorator;

/**
 * This is an AOP Aspect, it adds caching to methods that are annotated with {@link CacheRemove}.
 */
@Singleton
@Aspect
@Named("cacheRemoveAspect")
public class CacheRemoveAspect {

	private CacheRemoveDecorator cacheRemoveDecorator;

	/**
	 * This method will generate a key from the String parameters and Class name and use it to cache the result of the annotated method.
	 *
	 * @param joinPoint   the method that we are decorating.
	 * @param cacheRemove the cache remove annotation.
	 * @return the cache hit or result of the method invocation.
	 * @throws Throwable if the method can not proceed.
	 */
	@Around("@annotation( cacheRemove ) && execution(* *(..))")
	public Object decorateMethodWithCaching(final ProceedingJoinPoint joinPoint, final CacheRemove cacheRemove) throws Throwable {

		return cacheRemoveDecorator.decorateMethodWithCaching(joinPoint, cacheRemove);
	}

	//called from Spring, during initialization
	public void setCacheRemoveDecorator(final CacheRemoveDecorator cacheRemoveDecorator) {
		this.cacheRemoveDecorator = cacheRemoveDecorator;
	}
}
