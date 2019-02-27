/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.search.index.pipeline.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.search.IndexBuildStatus;
import com.elasticpath.domain.search.impl.IndexBuildStatusImpl;
import com.elasticpath.persistence.dao.IndexBuildStatusDao;
import com.elasticpath.service.search.IndexType;

/**
 * IndexBuildStatusUpdater does it work in a seperate thread, and in production would be processing multiple threads, thereby making it
 * difficult to write meaningful tests for.
 * the tests included here are more 'exercising' tests that can be used by a develop as a starting point to debug problems if needed.
 * @author acanterla
 *
 */
public class IndexBuildStatusUpdaterTest {

	private static final int TEN_SECONDS = 10000;

	private static final int TWO_SECONDS = 2000;

	private static final int NUM_OF_THREADS = 20;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private IndexBuildStatusUpdater indexBuildStatusUpdater;
	
	private IndexBuildStatusDao mockIndexBuildStatusDao;
	
	private IndexBuildStatus indexBuildStatus;
	
	private IndexBuildStatus indexBuildStatusthrowsException;
	
	@Before
	public void setUp() {
		
		mockIndexBuildStatusDao = context.mock(IndexBuildStatusDao.class, "mockIndexBuildStatusDao");
		indexBuildStatus = context.mock(IndexBuildStatus.class, "indexBuildStatus");
		indexBuildStatusthrowsException = context.mock(IndexBuildStatus.class, "indexBuildStatusthrowsException");
		
		indexBuildStatusUpdater = new IndexBuildStatusUpdater();
		indexBuildStatusUpdater.setIndexBuildStatusDao(mockIndexBuildStatusDao);
		indexBuildStatusUpdater.initialize();

		context.checking(new Expectations() {
			{
				allowing(indexBuildStatus).getIndexType();
				will(returnValue(IndexType.PRODUCT));
				
				allowing(mockIndexBuildStatusDao).get(IndexType.PRODUCT);
				will(returnValue(null));

				allowing(indexBuildStatusthrowsException).getIndexType();
				will(returnValue(IndexType.CATEGORY));
				
				allowing(indexBuildStatusthrowsException).getUidPk();
				will(returnValue(Long.MIN_VALUE));
				
				allowing(mockIndexBuildStatusDao).get(IndexType.CATEGORY);
				will(returnValue(null));
				
				allowing(mockIndexBuildStatusDao).saveOrUpdate(indexBuildStatusthrowsException);
				will(throwException(new EpServiceException("epServiceException")));
			}
		});
		
	}
	
	@After
	public void tearDown() {
		indexBuildStatusUpdater.shutdown();
	}
	
	@Test
	public void testIndexBuildStatusUpdater() throws InterruptedException {
		
		ExecutorService threadPool = Executors.newFixedThreadPool(NUM_OF_THREADS);
		
		for (int i = 0; i < NUM_OF_THREADS; i++) {
			threadPool.submit(simulateIndexerThread(i));
		}
		
		Thread.sleep(TWO_SECONDS);
		
		for (int i = 0; i < NUM_OF_THREADS; i++) {
			threadPool.submit(simulateIndexerThread(i));
		}
		
		threadPool.shutdown();
		//give the IndexStatusUpdater a chance to run for a while. 
		//if we dont do this the test ends and all the threads go away.
		threadPool.awaitTermination(TEN_SECONDS, TimeUnit.SECONDS);
	}
	
	@Test
	public void testIndexBuildStatusUpdaterWithExceptionThrown() throws InterruptedException {

		ExecutorService threadPool = Executors.newFixedThreadPool(NUM_OF_THREADS);
		
		for (int i = 0; i < NUM_OF_THREADS; i++) {
			threadPool.submit(simulateIndexerThread(i));
		}
		
		Thread.sleep(TWO_SECONDS);
		
		threadPool.submit(simulateIndexerThreadThrowsException());
		
		for (int i = 0; i < NUM_OF_THREADS; i++) {
			threadPool.submit(simulateIndexerThread(i));
		}
		
		threadPool.shutdown();
		//give the IndexStatusUpdater a chance to run for a while. 
		//if we dont do this the test ends and all the threads go away.
		threadPool.awaitTermination(TEN_SECONDS, TimeUnit.SECONDS);
	}
	
	private Runnable simulateIndexerThread(final int simpleGuid) {
		return new Runnable() {
			@Override
			public void run() {

				IndexBuildStatus status = new IndexBuildStatusImpl();
				status.setIndexType(IndexType.PRODUCT);
				status.setUidPk(simpleGuid);
				
				indexBuildStatusUpdater.enqueue(status);
			}
		};
	}
	
	private Runnable simulateIndexerThreadThrowsException() {
		return new Runnable() {
			@Override
			public void run() {
				indexBuildStatusUpdater.enqueue(indexBuildStatusthrowsException);
			}
		};
	}
}
