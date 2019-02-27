/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository;

import javax.inject.Named;
import javax.inject.Singleton;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.cache.CacheResultDecorator;

/**
 * This is an AOP Aspect, it adds caching to methods that are annotated with {@link CacheResult}.
 */
@Singleton
@Aspect
@Named("cacheResultAspect")
public class CacheResultAspect {

	private CacheResultDecorator cacheResultDecorator;

	/**
	 * This method will generate a key from the String parameters and Class name and use it to cache the result of the annotated method.
	 *
	 * @param joinPoint   the method that we are decorating.
	 * @param cacheResult the cache annotation.
	 * @return the cache hit or result of the method invocation.
	 * @throws Throwable if the method can not proceed.
	 */
	@Around("@annotation(cacheResult) && execution(* *(..))")
	public Object decorateMethodWithCaching(final ProceedingJoinPoint joinPoint, final CacheResult cacheResult) throws Throwable {
		return cacheResultDecorator.decorateMethodWithCaching(joinPoint, cacheResult);
	}

	//called from Spring, during initialization
	public void setCacheResultDecorator(final CacheResultDecorator cacheResultDecorator) {
		this.cacheResultDecorator = cacheResultDecorator;
	}
}
