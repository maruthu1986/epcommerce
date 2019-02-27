/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.persistence.impl;

import java.util.Collections;

import org.apache.openjpa.event.LifecycleEvent;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.support.PersistablePostLoadStrategy;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

public class PersistablePostLoadListenerTest {
	public static final String FOO_SERVICE = "foo";
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final BeanFactory beanFactory = context.mock(BeanFactory.class);
	private final Persistable persistable = context.mock(Persistable.class);
	@SuppressWarnings("unchecked")
	private final PersistablePostLoadStrategy<Persistable> foo = context.mock(PersistablePostLoadStrategy.class);
	private PersistablePostLoadListener listener;
	private BeanFactoryExpectationsFactory bfef;

	@Before
	public void setUp() throws Exception {
		bfef = new BeanFactoryExpectationsFactory(context, beanFactory);

		listener = new PersistablePostLoadListener();
		listener.setBeanFactory(beanFactory);
		listener.setPostLoadStrategyBeanIds(Collections.singletonList(FOO_SERVICE));
	}

	protected void givenBeanFactoryGetFooIsSuccessful() {
		bfef.allowingBeanFactoryGetBean(FOO_SERVICE, foo);
	}

	@After
	public void tearDown() {
		bfef.close();
	}

	@Test
	public void verifyThatListenerInvokesProcessOnAllStrategiesThatCanProcessAnObjectAfterLoad() throws Exception {
		givenBeanFactoryGetFooIsSuccessful();
		context.checking(new Expectations() {
			{
				allowing(foo).canProcess(persistable);
				will(returnValue(true));

				oneOf(foo).process(persistable);
			}
		});

		listener.afterLoad(new LifecycleEvent(persistable, LifecycleEvent.AFTER_LOAD));
	}

	@Test
	public void verifyThatListenerDoesNotInvokeProcessOnStrategiesThatCanNotProcessAnObject() throws Exception {
		givenBeanFactoryGetFooIsSuccessful();
		context.checking(new Expectations() {
			{
				allowing(foo).canProcess(persistable);
				will(returnValue(false));

				never(foo).process(persistable);
			}
		});

		listener.afterLoad(new LifecycleEvent(persistable, LifecycleEvent.AFTER_LOAD));
	}

	@Test
	public void verifyThatListenerCanDelayLazyLoadingUntilSpringIsReady() throws Exception {
		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(FOO_SERVICE);
				will(throwException(
						new BeanCreationException("This bean cannot be instantiated because spring is still in the middle of building the context")));

				oneOf(beanFactory).getBean(FOO_SERVICE);
				will(throwException(
						new BeanCreationException("This bean cannot be instantiated because spring is still in the middle of building the context")));

				oneOf(beanFactory).getBean(FOO_SERVICE);
				will(returnValue(foo));

				allowing(foo).canProcess(persistable);
				will(returnValue(true));

				oneOf(foo).process(persistable);
			}
		});

		// This call should not trigger the foo.process()
		listener.afterLoad(new LifecycleEvent(persistable, LifecycleEvent.AFTER_LOAD));
		// This call should not trigger the foo.process() either
		listener.afterLoad(new LifecycleEvent(persistable, LifecycleEvent.AFTER_LOAD));
		// This call should now trigger foo.process() because the bean factory has finally stabilized
		listener.afterLoad(new LifecycleEvent(persistable, LifecycleEvent.AFTER_LOAD));
	}

}
